package org.hzero.export.filler;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;

import org.hzero.core.jackson.JacksonConstant;
import org.hzero.export.annotation.ExpandProperty;
import org.hzero.export.render.ValueRenderer;
import org.hzero.export.vo.ExcelColumnProperty;
import org.hzero.export.vo.ExportColumn;
import org.hzero.export.vo.ExportProperty;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2021/08/10 11:45
 */
public abstract class FileFiller {

    protected static final Logger LOGGER = LoggerFactory.getLogger(FileFiller.class);

    private final Map<Class<? extends ValueRenderer>, ValueRenderer> renderers = new ConcurrentHashMap<>();

    /**
     * 导出属性参数
     */
    protected ExportProperty exportProperty;

    /**
     * 数据分组
     */
    protected List<List<?>> shardData(int firstSize, int maxRow, List<?> data) {
        int size = data.size();
        if (firstSize >= size) {
            return Collections.singletonList(data);
        } else {
            List<List<?>> result = new ArrayList<>();
            int remain = size - firstSize;
            if (firstSize > 0) {
                result.add(data.subList(0, firstSize));
            }
            int shardNum = (int) Math.ceil((double) remain / maxRow);
            for (int i = 0; i < shardNum; i++) {
                result.add(data.subList(firstSize + maxRow * i, Math.min(firstSize + maxRow * (i + 1), data.size())));
            }
            return result;
        }
    }

    protected Object doRender(Object value, Object rowData, List<Class<? extends ValueRenderer>> rendererTypes) {
        if (CollectionUtils.isNotEmpty(rendererTypes)) {
            for (Class<? extends ValueRenderer> rendererType : rendererTypes) {
                ValueRenderer renderer = this.renderers.computeIfAbsent(rendererType, key ->
                        Optional.ofNullable(key).map(type -> {
                            try {
                                return type.getConstructor().newInstance();
                            } catch (Exception e) {
                                LOGGER.error("can not create renderer!", e);
                                return null;
                            }
                        }).orElse(null)
                );

                if (renderer != null) {
                    value = renderer.render(value, rowData);
                }
            }
        }
        return value;
    }

    /**
     * 日期类型时区转换
     *
     * @param cellValue 日期
     * @param column    列定义
     * @return 转换后的值
     */
    protected Object convertTimeZone(Object cellValue, ExportColumn column) {
        if (cellValue instanceof Date) {
            // 日期类型需要进行时区转换
            String pattern = getPattern(column.getExcelColumnProperty());
            SimpleDateFormat dateFormatGmt = new SimpleDateFormat(StringUtils.isBlank(pattern) ? JacksonConstant.DEFAULT_DATE_FORMAT : pattern);
            if (!column.isIgnoreTimeZone()) {
                CustomUserDetails details = DetailsHelper.getUserDetails();
                if (details != null && details.getTimeZone() != null) {
                    dateFormatGmt.setTimeZone(TimeZone.getTimeZone(details.getTimeZone()));
                }
            }
            return dateFormatGmt.format(cellValue);
        }
        return cellValue;
    }


    /**
     * 获取格式掩码
     */
    protected String getPattern(ExcelColumnProperty excelColumnProperty) {
        Class<? extends ExpandProperty> propertyClass = excelColumnProperty.getExpandProperty();
        ExpandProperty property;
        try {
            // 先尝试从容器中获取
            property = ApplicationContextHelper.getContext().getBean(propertyClass);
        } catch (Exception e) {
            try {
                property = propertyClass.newInstance();
            } catch (Exception exception) {
                LOGGER.error("Interface Pattern Instantiation failed!", e);
                return excelColumnProperty.getPattern();
            }
        }
        String patternStr = property.getPattern();
        if (StringUtils.isBlank(patternStr)) {
            return excelColumnProperty.getPattern();
        }
        return patternStr;
    }

    /**
     * 获取单元格数据
     *
     * @param data   数据
     * @param column 列定义
     * @return 子级数据
     */
    protected Object getCellValue(Object data, ExportColumn column) {
        if (data == null) {
            return null;
        }
        Object cellValue = null;
        try {
            Map<String, String> lov = column.getLov();
            Field field = ReflectionUtils.findField(data.getClass(), column.getName());
            if (field != null) {
                cellValue = FieldUtils.readField(field, data, true);
            }
            if (field == null || cellValue == null) {
                // 先从对象属性取，取不到尝试从 flex map 中取
                Map<String, Object> flex = getFlexMap(data);
                cellValue = flex.get(column.getName());
            }
            // 处理值集
            if (cellValue != null && MapUtils.isNotEmpty(lov)) {
                cellValue = lov.getOrDefault(cellValue.toString(), cellValue.toString());
            }
            // 日期类型需要进行时区转换
            cellValue = convertTimeZone(cellValue, column);
        } catch (Exception e) {
            LOGGER.error("get value error.", e);
        }
        return cellValue;
    }

    private Map<String, Object> getFlexMap(Object data) throws Exception {
        Field flexFiled = ReflectionUtils.findField(data.getClass(), "flex");
        if (flexFiled != null) {
            return (Map<String, Object>) FieldUtils.readField(flexFiled, data, true);
        }
        return new HashMap<>(1);
    }

    private static final Map<Class<?>, Method[]> LOCAL_METHODS_CACHE = new ConcurrentHashMap<>();

    /**
     * 获取子级数据
     *
     * @param data  数据
     * @param child 自己列定义
     * @return 子级数据
     */
    protected List<Object> getChildData(Object data, ExportColumn child) {
        if (Objects.nonNull(data)) {
            String getter = "get" + child.getName();
            Method[] methods = LOCAL_METHODS_CACHE.computeIfAbsent(data.getClass(), Class::getDeclaredMethods);
            for (Method method : methods) {
                if (method.getName().equalsIgnoreCase(getter)) {
                    try {
                        if (child.getCurrentClassIsCollection()) {
                            method.setAccessible(true);
                            Object childData = method.invoke(data);
                            return childData == null ? Collections.emptyList() : (List<Object>) childData;
                        } else {
                            method.setAccessible(true);
                            Object childData = method.invoke(data);
                            if (childData == null) {
                                return Collections.emptyList();
                            } else {
                                return Collections.singletonList(childData);
                            }
                        }
                    } catch (Exception e) {
                        LOGGER.error("get child data error.", e);
                    }
                }
            }
        }
        return Collections.emptyList();
    }

    /**
     * 从根节点逐层读取树形结构
     *
     * @param root 根节点
     */
    protected List<ExportColumn> collectAllTitleColumnFromRoot(ExportColumn root) {
        List<ExportColumn> titleColumns = new ArrayList<>();
        if (root.isChecked() && root.hasChildren()) {
            titleColumns.add(root);
            // 处理根节点的子孙级
            if (CollectionUtils.isNotEmpty(root.getChildren())) {
                collectAllTitleColumn(titleColumns, Collections.singletonList(root));
            }
        }
        return titleColumns;
    }

    /**
     * 逐层读取树形结构
     *
     * @param titleColumns 标题列定义
     * @param pre          上级节点
     */
    private void collectAllTitleColumn(List<ExportColumn> titleColumns, List<ExportColumn> pre) {
        if (CollectionUtils.isEmpty(pre)) {
            return;
        }
        List<ExportColumn> currentColumns = new ArrayList<>();
        for (ExportColumn parent : pre) {
            // 处理根节点的子孙级
            if (CollectionUtils.isNotEmpty(parent.getChildren())) {
                for (ExportColumn current : parent.getChildren()) {
                    if (current.isChecked() && current.hasChildren()) {
                        titleColumns.add(current);
                        currentColumns.add(current);
                    }
                }
            }
        }
        collectAllTitleColumn(titleColumns, currentColumns);
    }
}
