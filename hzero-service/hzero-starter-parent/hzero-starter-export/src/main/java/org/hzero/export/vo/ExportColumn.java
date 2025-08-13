package org.hzero.export.vo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Excel 导出列
 *
 * @author bojiangzhou 2018/07/25
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExportColumn implements Serializable {

    private static final long serialVersionUID = 1674840216339609700L;
    /**
     * 列ID
     */
    private Long id;
    /**
     * 标题
     */
    private String title;
    /**
     * 名称，类名或字段名
     */
    private String name;
    /**
     * 父级ID
     */
    private Long parentId;
    /**
     * 顺序
     */
    private int order;
    /**
     * 类型
     */
    private String type;
    /**
     * 子级列
     */
    private List<ExportColumn> children;
    /**
     * 是否开启异步导出
     */
    private boolean enableAsync;
    /**
     * 默认请求模式
     */
    private String defaultRequestMode;
    /**
     * 是否被选择
     */
    private boolean checked;
    /**
     * 业务对象编码
     */
    private String businessObjectCode;
    /**
     * 业务对象字段编码
     */
    private String businessObjectFieldCode;
    /**
     * 业务对象模板下级字段是否需要打平
     */
    private boolean needPlat = false;
    /**
     * 允许的数据填充方式
     */
    private List<String> allowFillType;
    /**
     * 允许的文件导出格式
     */
    private List<String> allowFileType;
    /**
     * 是否忽略时区转换
     */
    @JsonIgnore
    private boolean ignoreTimeZone = false;
    /**
     * 是否包含子节点
     */
    @JsonIgnore
    private boolean hasChildren;

    @JsonIgnore
    private ExcelSheetProperty excelSheetProperty;

    @JsonIgnore
    private ExcelColumnProperty excelColumnProperty;
    /**
     * 当前节点创建的第一列的下标
     */
    @JsonIgnore
    private int leftIndex;
    /**
     * 当前节点在各批次下的sheet页名称记录
     */
    @JsonIgnore
    private final Map<String, String> sheetNames = new HashMap<>(16);

    /**
     * 导出列对应的导出DTO
     */
    @JsonIgnore
    private Class<?> exportClass;

    /**
     * 导出列对应的导出DTO是否是集合
     */
    @JsonIgnore
    private boolean currentClassIsCollection;

    /**
     * 当前节点绑定的值集
     */
    @JsonIgnore
    private Map<String, String> lov;

    public ExportColumn() {
    }

    public ExportColumn(Long id, String title, String name, Long parentId) {
        this.id = id;
        this.title = title;
        this.name = name;
        this.parentId = parentId;
    }

    public Long getId() {
        return id;
    }

    public ExportColumn setId(Long id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public ExportColumn setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getName() {
        return name;
    }

    public ExportColumn setName(String name) {
        this.name = name;
        return this;
    }

    public Long getParentId() {
        return parentId;
    }

    public ExportColumn setParentId(Long parentId) {
        this.parentId = parentId;
        return this;
    }

    public int getOrder() {
        return order;
    }

    public ExportColumn setOrder(int order) {
        this.order = order;
        return this;
    }

    public String getType() {
        return type;
    }

    public ExportColumn setType(String type) {
        this.type = type;
        return this;
    }

    public List<ExportColumn> getChildren() {
        return children;
    }

    public ExportColumn setChildren(List<ExportColumn> children) {
        this.children = children;
        return this;
    }

    public boolean isEnableAsync() {
        return enableAsync;
    }

    public ExportColumn setEnableAsync(boolean enableAsync) {
        this.enableAsync = enableAsync;
        return this;
    }

    public String getDefaultRequestMode() {
        return defaultRequestMode;
    }

    public ExportColumn setDefaultRequestMode(String defaultRequestMode) {
        this.defaultRequestMode = defaultRequestMode;
        return this;
    }

    public boolean isChecked() {
        return checked;
    }

    public ExportColumn setChecked(boolean checked) {
        this.checked = checked;
        return this;
    }

    public String getBusinessObjectCode() {
        return businessObjectCode;
    }

    public ExportColumn setBusinessObjectCode(String businessObjectCode) {
        this.businessObjectCode = businessObjectCode;
        return this;
    }

    public String getBusinessObjectFieldCode() {
        return businessObjectFieldCode;
    }

    public ExportColumn setBusinessObjectFieldCode(String businessObjectFieldCode) {
        this.businessObjectFieldCode = businessObjectFieldCode;
        return this;
    }

    public boolean isNeedPlat() {
        return needPlat;
    }

    public ExportColumn setNeedPlat(boolean needPlat) {
        this.needPlat = needPlat;
        return this;
    }

    public List<String> getAllowFillType() {
        return allowFillType;
    }

    public ExportColumn setAllowFillType(List<String> allowFillType) {
        this.allowFillType = allowFillType;
        return this;
    }

    public List<String> getAllowFileType() {
        return allowFileType;
    }

    public ExportColumn setAllowFileType(List<String> allowFileType) {
        this.allowFileType = allowFileType;
        return this;
    }

    public boolean isIgnoreTimeZone() {
        return ignoreTimeZone;
    }

    public ExportColumn setIgnoreTimeZone(boolean ignoreTimeZone) {
        this.ignoreTimeZone = ignoreTimeZone;
        return this;
    }

    public boolean hasChildren() {
        return hasChildren;
    }

    public ExportColumn setHasChildren(boolean hasChildren) {
        this.hasChildren = hasChildren;
        return this;
    }

    public ExcelSheetProperty getExcelSheetProperty() {
        return excelSheetProperty;
    }

    public ExportColumn setExcelSheetProperty(ExcelSheetProperty excelSheetProperty) {
        this.excelSheetProperty = excelSheetProperty;
        return this;
    }

    public ExcelColumnProperty getExcelColumnProperty() {
        if (excelColumnProperty == null) {
            this.excelColumnProperty = new ExcelColumnProperty();
        }
        return excelColumnProperty;
    }

    public ExportColumn setExcelColumnProperty(ExcelColumnProperty excelColumnProperty) {
        this.excelColumnProperty = excelColumnProperty;
        return this;
    }

    public int getLeftIndex() {
        return leftIndex;
    }

    public ExportColumn setLeftIndex(int leftIndex) {
        this.leftIndex = leftIndex;
        return this;
    }

    public Map<String, String> getSheetNames() {
        return sheetNames;
    }

    public ExportColumn setLov(Map<String, String> lov) {
        this.lov = lov;
        return this;
    }

    public Map<String, String> getLov() {
        return lov;
    }

    public Class<?> getExportClass() {
        return exportClass;
    }

    public ExportColumn setExportClass(Class<?> exportClass) {
        this.exportClass = exportClass;
        return this;
    }

    public boolean getCurrentClassIsCollection() {
        return currentClassIsCollection;
    }

    public ExportColumn setCurrentClassIsCollection(boolean currentClassIsCollection) {
        this.currentClassIsCollection = currentClassIsCollection;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ExportColumn that = (ExportColumn) o;
        return order == that.order
                && enableAsync == that.enableAsync
                && checked == that.checked
                && ignoreTimeZone == that.ignoreTimeZone
                && hasChildren == that.hasChildren
                && leftIndex == that.leftIndex
                && needPlat == that.needPlat
                && Objects.equals(id, that.id)
                && Objects.equals(title, that.title)
                && Objects.equals(name, that.name)
                && Objects.equals(parentId, that.parentId)
                && Objects.equals(type, that.type)
                && Objects.equals(children, that.children)
                && Objects.equals(defaultRequestMode, that.defaultRequestMode)
                && Objects.equals(businessObjectCode, that.businessObjectCode)
                && Objects.equals(businessObjectFieldCode, that.businessObjectFieldCode)
                && Objects.equals(allowFillType, that.allowFillType)
                && Objects.equals(allowFileType, that.allowFileType)
                && Objects.equals(excelSheetProperty, that.excelSheetProperty)
                && Objects.equals(excelColumnProperty, that.excelColumnProperty)
                && Objects.equals(sheetNames, that.sheetNames)
                && Objects.equals(lov, that.lov);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, name, parentId, order, type, children, enableAsync, defaultRequestMode, checked, businessObjectCode, businessObjectFieldCode, needPlat, allowFillType, allowFileType, ignoreTimeZone, hasChildren, excelSheetProperty, excelColumnProperty, leftIndex, sheetNames, lov);
    }

    @Override
    public String toString() {
        return "ExportColumn{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", name='" + name + '\'' +
                ", parentId=" + parentId +
                ", order=" + order +
                ", type='" + type + '\'' +
                ", children=" + children +
                ", enableAsync=" + enableAsync +
                ", defaultRequestMode='" + defaultRequestMode + '\'' +
                ", checked=" + checked +
                ", businessObjectCode=" + businessObjectCode +
                ", businessObjectFieldCode=" + businessObjectFieldCode +
                ", needPlat=" + needPlat +
                ", allowFillType=" + allowFillType +
                ", allowFileType=" + allowFileType +
                ", ignoreTimeZone=" + ignoreTimeZone +
                ", hasChildren=" + hasChildren +
                ", excelSheet=" + excelSheetProperty +
                ", excelColumn=" + excelColumnProperty +
                ", leftIndex=" + leftIndex +
                ", sheetNames=" + sheetNames +
                ", lov=" + lov +
                '}';
    }
}
