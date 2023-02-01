package org.hzero.export;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;

import org.hzero.common.HZeroService;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.helper.LanguageHelper;
import org.hzero.core.jackson.annotation.IgnoreTimeZone;
import org.hzero.core.redis.RedisHelper;
import org.hzero.core.util.ResponseUtils;
import org.hzero.export.annotation.ExcelColumn;
import org.hzero.export.annotation.ExcelExport;
import org.hzero.export.annotation.ExcelSheet;
import org.hzero.export.constant.ExportConstants;
import org.hzero.export.constant.FileType;
import org.hzero.export.entity.Template;
import org.hzero.export.entity.TemplateColumn;
import org.hzero.export.feign.ExportHimpRemoteService;
import org.hzero.export.redis.ImportTemplateRedis;
import org.hzero.export.vo.ExcelColumnProperty;
import org.hzero.export.vo.ExcelSheetProperty;
import org.hzero.export.vo.ExportColumn;
import org.hzero.export.vo.ExportParam;

/**
 * excel export column helper
 *
 * @author bojiangzhou 2018/07/26
 */
public class ExportColumnHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExportColumnHelper.class);

    private static final String MULTI_LANGUAGE_PREFIX = HZeroService.Platform.CODE + ":prompt:";

    private static final ThreadLocal<Long> ID_LOCAL = new ThreadLocal<>();

    private final ExportProperties properties;
    private final RedisHelper redisHelper;
    private final ExportHimpRemoteService himpRemoteService;

    public ExportColumnHelper(ExportProperties properties, RedisHelper redisHelper, ExportHimpRemoteService himpRemoteService) {
        this.properties = properties;
        this.redisHelper = redisHelper;
        this.himpRemoteService = himpRemoteService;
    }

    /**
     * 根据导出类获取导出列
     *
     * @param excelExport 导出类
     * @return ExportColumn
     */
    public ExportColumn getExportColumn(ExcelExport excelExport, ExportParam exportParam) {
        if (StringUtils.isNotBlank(excelExport.templateCode())) {
            // 绑定了导入模板
            redisHelper.setCurrentDatabase(HZeroService.Import.REDIS_DB);
        } else {
            redisHelper.setCurrentDatabase(HZeroService.Platform.REDIS_DB);
        }
        ExportColumn column;
        try {
            ID_LOCAL.set(ExportConstants.ROOT_PARENT_ID);
            column = doGetExportColumn(excelExport);
        } finally {
            redisHelper.clearCurrentDatabase();
        }
        // 处理排序
        order(column);
        // 清除ID
        ID_LOCAL.remove();
        return column;
    }

    private void order(ExportColumn parent) {
        if (parent == null) {
            return;
        }
        List<ExportColumn> children = parent.getChildren();
        if (CollectionUtils.isEmpty(parent.getChildren())) {
            return;
        }
        // 排序
        children = children.parallelStream().sorted(Comparator.comparing(ExportColumn::getOrder)).collect(Collectors.toList());
        parent.setChildren(children);
        // 处理下级
        for (ExportColumn item : children) {
            order(item);
        }
    }

    /**
     * 根据导出类获取导出列
     *
     * @param excelExport 导出类
     * @return ExportColumn
     */
    private ExportColumn doGetExportColumn(ExcelExport excelExport) {
        ExportColumn root;
        if (StringUtils.isNotBlank(excelExport.templateCode())) {
            root = getExportColumnWithTemplate(excelExport.templateCode());
        } else {
            Class<?> exportClass = excelExport.value();
            if (!exportClass.isAnnotationPresent(ExcelSheet.class)) {
                LOGGER.error("导出类[{}]请使用 @ExcelSheet 标注", exportClass.getName());
                return null;
            }
            root = getExportColumnFromClass(exportClass, excelExport.groups(), ExportConstants.ROOT_PARENT_ID, false);
        }
        if (root != null) {
            root.setEnableAsync(false)
                    .setDefaultRequestMode(null)
                    .setAllowFillType(Arrays.asList(excelExport.fillType()))
                    .setAllowFileType(Arrays.stream(excelExport.exportFileType()).map(FileType::getName).collect(Collectors.toList()));
            if (properties != null) {
                root.setEnableAsync(properties.getEnableAsync())
                        .setDefaultRequestMode(properties.getDefaultRequestMode());
            }
        }

        return root;
    }

    /**
     * 标记已选择的列
     *
     * @param excelExport 导出类
     * @param exportParam 导出参数
     */
    public ExportColumn getCheckedExportColumn(ExcelExport excelExport, ExportParam exportParam) {
        Set<Long> checkedIds = ObjectUtils.defaultIfNull(exportParam.getIds(), new HashSet<>());
        Set<ExportColumn> columns = exportParam.getColumns();
        if (CollectionUtils.isNotEmpty(columns)) {
            for (ExportColumn column : columns) {
                checkedIds.add(column.getId());
            }
        }
        ExportColumn root = getExportColumn(excelExport, exportParam);
        // 暂存虚拟父级字段
        Map<Long, ExportColumn> parentMap = new HashMap<>(16);
        // 将所有字段重置为未勾选，后面会根据前端勾选判断是否导出
        setUnChecked(root, parentMap);
        checkChildren(root.getChildren(), checkedIds, parentMap);
        return root;
    }

    /**
     * 将所有节点置为未勾选，并使用map记录父级虚拟字段
     */
    private void setUnChecked(ExportColumn column, Map<Long, ExportColumn> map) {
        column.setChecked(false);
        if (column.hasChildren()) {
            map.put(column.getId(), column);
        }
        if (CollectionUtils.isNotEmpty(column.getChildren())) {
            for (ExportColumn child : column.getChildren()) {
                setUnChecked(child, map);
            }
        }
    }

    private void checkChildren(List<ExportColumn> children, Set<Long> checkedIds, Map<Long, ExportColumn> parentMap) {
        if (CollectionUtils.isEmpty(children)) {
            return;
        }
        for (ExportColumn child : children) {
            if (checkedIds.contains(child.getId()) && !child.hasChildren()) {
                child.setChecked(true);
                // 字段被选中，递归选中上级
                setParentCheck(parentMap, child);
            }
            if (child.getExcelColumnProperty().isChild()) {
                checkChildren(child.getChildren(), checkedIds, parentMap);
            }
        }
    }

    private void setParentCheck(Map<Long, ExportColumn> parentMap, ExportColumn column) {
        Long parentId = column.getParentId();
        if (parentId == null) {
            return;
        }
        ExportColumn parent = parentMap.get(parentId);
        if (parent == null) {
            return;
        }
        // 将parent设置为选中
        parent.setChecked(true);
        // 已经设为选中的，从map种移除
        parentMap.remove(parentId);
        // 递归处理上级
        setParentCheck(parentMap, parent);
    }

    /**
     * 根据指定的导入模板编码获取导出列信息
     */
    public ExportColumn getExportColumnWithTemplate(String templateCode) {
        Long tenantId = BaseConstants.DEFAULT_TENANT_ID;
        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
        if (customUserDetails != null) {
            tenantId = customUserDetails.getTenantId();
        }
        Template template = getTemplateByTenantId(tenantId, templateCode);
        if (template == null && !BaseConstants.DEFAULT_TENANT_ID.equals(tenantId)) {
            template = getTemplateByTenantId(BaseConstants.DEFAULT_TENANT_ID, templateCode);
        }
        if (template == null || CollectionUtils.isEmpty(template.getTemplatePageList())) {
            throw new CommonException(String.format("Template %s is obtained.", templateCode));
        }
        ExportColumn root = new ExportColumn(autoIncrementId(), template.getTemplateName(), templateCode, ExportConstants.ROOT_PARENT_ID)
                .setType(ExportConstants.TemplateType.TEMPLATE)
                .setHasChildren(true)
                .setChecked(true)
                .setTitle(template.getTemplatePageList().get(0).getSheetName());
        List<TemplateColumn> columnList = template.getTemplatePageList().get(0).getTemplateColumnList();
        List<ExportColumn> children = new ArrayList<>();
        if (CollectionUtils.isEmpty(columnList)) {
            return root.setChildren(children);
        }
        // field
        for (TemplateColumn item : columnList) {
            Long id = autoIncrementId();
            ExportColumn column = new ExportColumn(id, item.getColumnName(), item.getColumnCode(), root.getId())
                    .setOrder(item.getColumnIndex())
                    .setType(item.getColumnType())
                    // default selected
                    .setChecked(true);
            children.add(column);
        }
        return root.setChildren(children);
    }

    /**
     * 获取导入模板
     */
    private Template getTemplateByTenantId(Long tenantId, String templateCode) {
        String lang = LanguageHelper.language();
        redisHelper.setCurrentDatabase(HZeroService.Import.REDIS_DB);
        Template template;
        String str;
        redisHelper.setCurrentDatabase(HZeroService.Import.REDIS_DB);
        try {
            str = ImportTemplateRedis.getTemplateStr(redisHelper, tenantId, templateCode, lang);
            if (ImportTemplateRedis.NO.equals(str)) {
                // redis记录了防穿透的缓存标识
                return null;
            }
            if (StringUtils.isNotBlank(str)) {
                template = redisHelper.fromJson(str, Template.class);
            } else {
                // 缓存不存在，feign调用查询指定租户的模板
                template = ResponseUtils.getResponse(himpRemoteService.getTemplate(tenantId, templateCode, lang), Template.class);
                if (template == null) {
                    // feign调用查询模板不存在，写入指定标识，防止缓存穿透
                    ImportTemplateRedis.refreshCache(redisHelper, tenantId, templateCode, lang, ImportTemplateRedis.NO);
                } else {
                    // 写入缓存数据
                    ImportTemplateRedis.refreshCache(redisHelper, tenantId, templateCode, lang, redisHelper.toJson(template));
                }
            }
        } finally {
            redisHelper.clearCurrentDatabase();
        }
        if (template != null) {
            template.setColumnNameByLang();
        }
        return template;
    }

    private ExportColumn getExportColumnFromClass(Class<?> exportClass, Class<?>[] groups, Long parentId, boolean currentClassIsCollection) {
        // class
        ExcelSheet excelSheet = AnnotationUtils.findAnnotation(exportClass, ExcelSheet.class);
        if (excelSheet == null) {
            return null;
        }
        Long rootId = autoIncrementId();
        ExportColumn root = new ExportColumn(rootId, getSheetName(exportClass, excelSheet), exportClass.getSimpleName(), parentId)
                .setType(ExportConstants.TemplateType.CLASS)
                .setHasChildren(true)
                .setExcelSheetProperty(new ExcelSheetProperty(excelSheet))
                .setExportClass(exportClass)
                .setCurrentClassIsCollection(currentClassIsCollection);

        // field
        Field[] fields = FieldUtils.getAllFields(exportClass);
        List<ExportColumn> children = new ArrayList<>(fields.length);
        for (Field field : fields) {
            if (field.isAnnotationPresent(ExcelColumn.class)) {
                ExcelColumn excelColumn = AnnotationUtils.findAnnotation(field, ExcelColumn.class);
                //preserve NPE
                if (excelColumn == null) {
                    continue;
                }
                if (!includeGroup(excelColumn.groups(), groups)) {
                    continue;
                }
                if (excelColumn.child()) {
                    Type type = field.getGenericType();
                    Class<T> entityClass;
                    currentClassIsCollection = false;
                    if (type instanceof ParameterizedType) {
                        ParameterizedType parameterizedType = (ParameterizedType) type;
                        entityClass = (Class<T>) parameterizedType.getActualTypeArguments()[0];
                        currentClassIsCollection = true;
                    } else {
                        entityClass = (Class<T>) type;
                    }
                    ExportColumn child = getExportColumnFromClass(entityClass, groups, rootId, currentClassIsCollection);
                    if (child != null) {
                        child.setOrder(excelColumn.order())
                                .setName(field.getName())
                                .setExcelColumnProperty(new ExcelColumnProperty(excelColumn))
                                // default selected
                                .setChecked(excelColumn.defaultSelected());
                        if (field.isAnnotationPresent(IgnoreTimeZone.class)) {
                            child.setIgnoreTimeZone(true);
                        }
                        children.add(child);
                    }
                } else {
                    Long id = autoIncrementId();
                    ExportColumn column = new ExportColumn(id, getColumnTitle(excelSheet, excelColumn, field), field.getName(), rootId);
                    column.setOrder(excelColumn.order())
                            .setType(field.getType().getSimpleName())
                            .setExcelColumnProperty(new ExcelColumnProperty(excelColumn))
                            // default selected
                            .setChecked(excelColumn.defaultSelected());
                    if (field.isAnnotationPresent(IgnoreTimeZone.class)) {
                        column.setIgnoreTimeZone(true);
                    }
                    children.add(column);
                }
            }
        }
        return root.setChildren(children);
    }

    /**
     * 自增ID
     */
    public static Long autoIncrementId() {
        Long id = ID_LOCAL.get() + 1;
        ID_LOCAL.set(id);
        return id;
    }

    /**
     * 获取Sheet标题
     */
    private String getSheetName(Class<?> exportClass, ExcelSheet excelSheet) {
        String title = null;
        if (StringUtils.isNoneBlank(excelSheet.promptKey(), excelSheet.promptCode())) {
            Long tenantId = BaseConstants.DEFAULT_TENANT_ID;
            CustomUserDetails userDetails = DetailsHelper.getUserDetails();
            if (userDetails != null) {
                tenantId = userDetails.getTenantId();
            }
            title = redisHelper.hshGet(MULTI_LANGUAGE_PREFIX + excelSheet.promptKey() + "." + LanguageHelper.language() + "." + tenantId, excelSheet.promptKey() + "." + excelSheet.promptCode());
            if (StringUtils.isBlank(title)) {
                // 兼容甄云的用法
                title = redisHelper.hshGet(MULTI_LANGUAGE_PREFIX + excelSheet.promptKey() + "." + LanguageHelper.language() + "." + tenantId, excelSheet.promptCode());
            }
            if (StringUtils.isBlank(title) && !BaseConstants.DEFAULT_TENANT_ID.equals(tenantId)) {
                title = redisHelper.hshGet(MULTI_LANGUAGE_PREFIX + excelSheet.promptKey() + "." + LanguageHelper.language() + "." + BaseConstants.DEFAULT_TENANT_ID, excelSheet.promptKey() + "." + excelSheet.promptCode());
                if (StringUtils.isBlank(title)) {
                    // 兼容甄云的用法
                    title = redisHelper.hshGet(MULTI_LANGUAGE_PREFIX + excelSheet.promptKey() + "." + LanguageHelper.language() + "." + BaseConstants.DEFAULT_TENANT_ID, excelSheet.promptCode());
                }
            }
        }
        if (StringUtils.isBlank(title)) {
            if (Locale.SIMPLIFIED_CHINESE.toString().equalsIgnoreCase(LanguageHelper.language())) {
                title = StringUtils.defaultIfBlank(excelSheet.zh(), null);
            } else {
                title = StringUtils.defaultIfBlank(excelSheet.en(), null);
            }
        }
        if (StringUtils.isBlank(title)) {
            title = exportClass.getSimpleName();
        }
        return title;
    }

    /**
     * 获取列标题
     */
    private String getColumnTitle(ExcelSheet excelSheet, ExcelColumn excelColumn, Field field) {
        String title = null;
        String promptKey = StringUtils.isNotBlank(excelColumn.promptKey()) ? excelColumn.promptKey() : excelSheet.promptKey();
        if (StringUtils.isNoneBlank(promptKey, excelColumn.promptCode())) {
            Long tenantId = BaseConstants.DEFAULT_TENANT_ID;
            CustomUserDetails userDetails = DetailsHelper.getUserDetails();
            if (userDetails != null) {
                tenantId = userDetails.getTenantId();
            }
            title = redisHelper.hshGet(MULTI_LANGUAGE_PREFIX + promptKey + "." + LanguageHelper.language() + "." + tenantId, promptKey + "." + excelColumn.promptCode());
            if (StringUtils.isBlank(title)) {
                // 兼容甄云的用法
                title = redisHelper.hshGet(MULTI_LANGUAGE_PREFIX + promptKey + "." + LanguageHelper.language() + "." + tenantId, excelColumn.promptCode());
            }
            if (StringUtils.isBlank(title) && !BaseConstants.DEFAULT_TENANT_ID.equals(tenantId)) {
                title = redisHelper.hshGet(MULTI_LANGUAGE_PREFIX + promptKey + "." + LanguageHelper.language() + "." + BaseConstants.DEFAULT_TENANT_ID, promptKey + "." + excelColumn.promptCode());
                if (StringUtils.isBlank(title)) {
                    // 兼容甄云的用法
                    title = redisHelper.hshGet(MULTI_LANGUAGE_PREFIX + promptKey + "." + LanguageHelper.language() + "." + BaseConstants.DEFAULT_TENANT_ID, excelColumn.promptCode());
                }
            }
        }
        if (StringUtils.isBlank(title)) {
            if (Locale.SIMPLIFIED_CHINESE.toString().equalsIgnoreCase(LanguageHelper.language())) {
                title = StringUtils.defaultIfBlank(excelColumn.zh(), null);
            } else {
                title = StringUtils.defaultIfBlank(excelColumn.en(), null);
            }
        }
        if (StringUtils.isBlank(title)) {
            title = StringUtils.capitalize(field.getName());
        }
        return title;
    }

    // 分组条件是 @ExcelExport 和 @ ExcelColumn 的 groups 必须包含相同的类型，如果没有配置，默认包含进去
    private boolean includeGroup(Class<?>[] columnGroups, Class<?>[] groups) {
        if (ArrayUtils.isNotEmpty(columnGroups) && ArrayUtils.isNotEmpty(groups)) {
            for (Class<?> group : groups) {
                for (Class<?> columnGroup : columnGroups) {
                    if (columnGroup.equals(group)) {
                        return true;
                    }
                }
            }
        } else {
            return true;
        }
        return false;
    }

    /**
     * 获取需要查询的子集
     *
     * @param root 已选择
     * @return 返回类名
     */
    public Set<String> getSelection(ExportColumn root) {
        Set<String> selection = new HashSet<>(8);
        chooseChildren(root.getChildren(), selection);
        return selection;
    }

    private void chooseChildren(List<ExportColumn> children, Set<String> selection) {
        if (CollectionUtils.isNotEmpty(children)) {
            for (ExportColumn child : children) {
                if (child.hasChildren() && child.isChecked()) {
                    selection.add(child.getName());
                    chooseChildren(child.getChildren(), selection);
                }
            }
        }
    }

}
