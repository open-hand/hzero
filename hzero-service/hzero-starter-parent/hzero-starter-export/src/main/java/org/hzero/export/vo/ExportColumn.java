package org.hzero.export.vo;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hzero.export.annotation.ExcelColumn;
import org.hzero.export.annotation.ExcelSheet;

/**
 * Excel 导出列
 *
 * @author bojiangzhou 2018/07/25
 */
public class ExportColumn implements Serializable {

    private static final long serialVersionUID = 1674840216339609700L;

    private Long id;

    private String title;

    private String name;

    private Long parentId;

    private int order;

    private String type;

    private String code;

    private List<ExportColumn> children;

    private Boolean enableAsync;

    private String defaultRequestMode;

    private boolean checked;

    /**
     * 是否忽略时区转换
     */
    private boolean ignoreTimeZone = false;

    @JsonIgnore
    private boolean hasChildren;

    @JsonIgnore
    private transient ExcelSheet excelSheet;

    @JsonIgnore
    private transient ExcelColumn excelColumn;

    public ExportColumn() {
    }

    public ExportColumn(Long id, String title, String name, Long parentId) {
        this.id = id;
        this.title = title;
        this.name = name;
        this.parentId = parentId;
    }

    /**
     * @return ID
     */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return 标题
     */
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return 名称，类名或字段名
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return 父级ID
     */
    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    /**
     * @return 顺序
     */
    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    /**
     * @return 类型
     */
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return 与父级关联的编码
     */
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return 子孙节点
     */
    public List<ExportColumn> getChildren() {
        return children;
    }

    public void setChildren(List<ExportColumn> children) {
        this.children = children;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    /**
     * @return 是否有子集
     */
    public boolean hasChildren() {
        return hasChildren;
    }

    public void setHasChildren(boolean hasChildren) {
        this.hasChildren = hasChildren;
    }

    public Boolean getEnableAsync() {
        return enableAsync;
    }

    public String getDefaultRequestMode() {
        return defaultRequestMode;
    }

    public void setDefaultRequestMode(String defaultRequestMode) {
        this.defaultRequestMode = defaultRequestMode;
    }

    public void setEnableAsync(Boolean enableAsync) {
        this.enableAsync = enableAsync;
    }

    public ExcelSheet getExcelSheet() {
        return excelSheet;
    }

    public void setExcelSheet(ExcelSheet excelSheet) {
        this.excelSheet = excelSheet;
    }

    public ExcelColumn getExcelColumn() {
        return excelColumn;
    }

    public void setExcelColumn(ExcelColumn excelColumn) {
        this.excelColumn = excelColumn;
    }

    public boolean isIgnoreTimeZone() {
        return ignoreTimeZone;
    }

    public ExportColumn setIgnoreTimeZone(boolean ignoreTimeZone) {
        this.ignoreTimeZone = ignoreTimeZone;
        return this;
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
                ", code='" + code + '\'' +
                ", children=" + children +
                ", enableAsync=" + enableAsync +
                ", defaultRequestMode='" + defaultRequestMode + '\'' +
                ", checked=" + checked +
                ", ignoreTimeZone=" + ignoreTimeZone +
                ", hasChildren=" + hasChildren +
                ", excelSheet=" + excelSheet +
                ", excelColumn=" + excelColumn +
                '}';
    }
}
