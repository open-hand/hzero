package org.hzero.export;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.poi.ss.formula.functions.T;
import org.hzero.common.HZeroService;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.helper.LanguageHelper;
import org.hzero.core.jackson.annotation.IgnoreTimeZone;
import org.hzero.core.redis.RedisHelper;
import org.hzero.core.util.FileUtils;
import org.hzero.export.annotation.ExcelColumn;
import org.hzero.export.annotation.ExcelExport;
import org.hzero.export.annotation.ExcelSheet;
import org.hzero.export.vo.ExportColumn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;

import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;

/**
 * excel export column helper
 *
 * @author bojiangzhou 2018/07/26
 */
public class ExportColumnHelper {

    private static final Logger logger = LoggerFactory.getLogger(ExportColumnHelper.class);

    private static final String MULTI_LANGUAGE_PREFIX = HZeroService.Platform.CODE + ":prompt:";

    private ThreadLocal<Long> idLocal = new ThreadLocal<>();

    private ExportProperties properties;

    private RedisHelper redisHelper;

    public ExportColumnHelper() {
    }

    public ExportColumnHelper(ExportProperties properties, RedisHelper redisHelper) {
        this.properties = properties;
        this.redisHelper = redisHelper;
    }

    /**
     * 根据导出类获取导出列
     *
     * @param excelExport 导出类
     * @return ExportColumn
     */
    public ExportColumn getExportColumn(ExcelExport excelExport) {
        redisHelper.setCurrentDatabase(HZeroService.Platform.REDIS_DB);
        try {
            return doGetExportColumn(excelExport);
        } finally {
            redisHelper.clearCurrentDatabase();
        }
    }

    /**
     * 根据导出类获取导出列
     *
     * @param excelExport 导出类
     * @return ExportColumn
     */
    private ExportColumn doGetExportColumn(ExcelExport excelExport) {

        Class<?> exportClass = excelExport.value();
        if (!exportClass.isAnnotationPresent(ExcelSheet.class)) {
            logger.error("导出类[{}]请使用 @ExcelSheet 标注", exportClass.getName());
            return null;
        }

        idLocal.set(0L);

        ExportColumn root = getExportColumnFromClass(exportClass, excelExport.groups(), null);

        idLocal.remove();

        if (root != null) {
            root.setEnableAsync(false);
            root.setDefaultRequestMode(null);
            if (properties != null) {
                root.setEnableAsync(properties.getEnableAsync());
                root.setDefaultRequestMode(properties.getDefaultRequestMode());
            }
        }

        return root;
    }

    /**
     * 标记已选择的列
     *
     * @param excelExport 导出类
     * @param checkedIds  已选择的列ID
     */
    public ExportColumn getCheckedExportColumn(ExcelExport excelExport, Set<Long> checkedIds) {
        ExportColumn root = getExportColumn(excelExport);
        if (!root.isChecked()) {
            root.setChecked(checkedIds.contains(root.getId()));
        }
        checkChildren(root, root.getChildren(), checkedIds);
        return root;
    }

    private void checkChildren(ExportColumn parent, List<ExportColumn> children, Set<Long> checkedIds) {
        if (CollectionUtils.isNotEmpty(children)) {
            // 只有勾选了字段，父级才会被选中
            boolean parentCheck = false;
            for (ExportColumn child : children) {
                if (checkedIds.contains(child.getId())) {
                    child.setChecked(true);
                    parentCheck = true;
                }
                if (child.getExcelColumn().child()) {
                    checkChildren(child, child.getChildren(), checkedIds);
                }
            }
            parent.setChecked(parentCheck);
        }
    }

    /**
     * 获取需要查询的子集
     *
     * @param excelExport 导出类
     * @param checkedIds  已选择的列ID
     * @return 返回类名
     */
    public Set<String> getSelection(ExcelExport excelExport, Set<Long> checkedIds) {
        ExportColumn root = getCheckedExportColumn(excelExport, checkedIds);
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

    @SuppressWarnings("unchecked")
    private ExportColumn getExportColumnFromClass(Class<?> exportClass, Class<?>[] groups, Long parentId) {
        // class
        ExcelSheet excelSheet = AnnotationUtils.findAnnotation(exportClass, ExcelSheet.class);
        if (excelSheet == null) {
            return null;
        }
        Long rootId = autoIncrementId();
        ExportColumn root = new ExportColumn(rootId, getSheetName(exportClass, excelSheet), exportClass.getSimpleName(), parentId);
        root.setType("Class");
        String code = Optional.ofNullable(parentId).map(pid -> parentId + "" + rootId).orElse(rootId + "");
        root.setCode(code);
        root.setHasChildren(true);
        root.setExcelSheet(excelSheet);

        // field
        //
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
                    if (type instanceof ParameterizedType) {
                        ParameterizedType parameterizedType = (ParameterizedType) type;
                        entityClass = (Class<T>) parameterizedType.getActualTypeArguments()[0];
                    } else {
                        entityClass = (Class<T>) type;
                    }
                    ExportColumn child = getExportColumnFromClass(entityClass, groups, rootId);
                    if (child != null) {
                        child.setOrder(excelColumn.order());
                        child.setName(field.getName());
                        child.setExcelColumn(excelColumn);
                        // default selected
                        child.setChecked(excelColumn.defaultSelected());
                        if (field.isAnnotationPresent(IgnoreTimeZone.class)) {
                            child.setIgnoreTimeZone(true);
                        }
                        children.add(child);
                    }
                } else {
                    Long id = autoIncrementId();
                    ExportColumn column = new ExportColumn(id, getColumnTitle(excelSheet, excelColumn, field), field.getName(), rootId);
                    column.setOrder(excelColumn.order());
                    column.setType(field.getType().getSimpleName());
                    column.setCode(code + id);
                    column.setExcelColumn(excelColumn);
                    // default selected
                    column.setChecked(excelColumn.defaultSelected());
                    if (field.isAnnotationPresent(IgnoreTimeZone.class)) {
                        column.setIgnoreTimeZone(true);
                    }
                    children.add(column);
                }
            }
        }
        // 排序
        children = children.parallelStream().sorted(Comparator.comparing(ExportColumn::getOrder)).collect(Collectors.toList());
        root.setChildren(children);
        return root;
    }

    /**
     * 自增ID
     */
    private Long autoIncrementId() {
        Long id = idLocal.get() + 1;
        idLocal.set(id);
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
            if (StringUtils.isBlank(title) && !BaseConstants.DEFAULT_TENANT_ID.equals(tenantId)) {
                title = redisHelper.hshGet(MULTI_LANGUAGE_PREFIX + excelSheet.promptKey() + "." + LanguageHelper.language() + "." + BaseConstants.DEFAULT_TENANT_ID, excelSheet.promptKey() + "." + excelSheet.promptCode());
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
            if (StringUtils.isBlank(title) && !BaseConstants.DEFAULT_TENANT_ID.equals(tenantId)) {
                title = redisHelper.hshGet(MULTI_LANGUAGE_PREFIX + promptKey + "." + LanguageHelper.language() + "." + BaseConstants.DEFAULT_TENANT_ID, promptKey + "." + excelColumn.promptCode());
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

}
