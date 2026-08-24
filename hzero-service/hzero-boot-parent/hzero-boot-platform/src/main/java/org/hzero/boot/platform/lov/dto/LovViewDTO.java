package org.hzero.boot.platform.lov.dto;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2019/09/18 14:26
 */
public class LovViewDTO {

    @ApiModelProperty("视图代码")
    private String viewCode;
    @ApiModelProperty("视图名称")
    private String viewName;
    @ApiModelProperty("值集代码")
    private String lovCode;
    @ApiModelProperty("值集名称")
    private String lovName;
    @ApiModelProperty("值集类型")
    private String lovTypeCode;
    @ApiModelProperty("租户ID")
    private Long tenantId;
    @ApiModelProperty("值字段")
    private String valueField;
    @ApiModelProperty("显示字段")
    private String displayField;
    @ApiModelProperty("标题")
    private String title;
    @ApiModelProperty("宽度")
    private Integer width;
    @ApiModelProperty("高度")
    private Integer height;
    @ApiModelProperty("分页大小")
    private Integer pageSize;
    @ApiModelProperty("是否延迟加载")
    private Integer delayLoadFlag;
    @ApiModelProperty("树形查询子字段名")
    private String childrenFieldName;
    @ApiModelProperty("数据查询URL")
    private String queryUrl;
    @ApiModelProperty("查询字段")
    private List<QueryField> queryFields;
    @ApiModelProperty("表格列")
    private List<TableField> tableFields;

    /**
     * 值集视图--查询条件
     *
     * @author gaokuo.dai@hand-china.com 2018年6月19日下午2:53:45
     */
    @ApiModel("值集视图查询条件")
    public static class QueryField {
        @ApiModelProperty("字段名")
        private String field;
        @ApiModelProperty("标签")
        private String label;
        @ApiModelProperty("数据类型")
        private String dataType;
        @ApiModelProperty("来源编码")
        private String sourceCode;

        /**
         * @return 字段名
         */
        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        /**
         * @return 标签
         */
        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        /**
         * @return 控件类型
         */
        public String getDataType() {
            return dataType;
        }

        public void setDataType(String dataType) {
            this.dataType = dataType;
        }

        public String getSourceCode() {
            return sourceCode;
        }

        public void setSourceCode(String sourceCode) {
            this.sourceCode = sourceCode;
        }

        public QueryField() {
        }

        public QueryField(String field, String label, String dataType, String sourceCode) {
            this.field = field;
            this.label = label;
            this.dataType = dataType;
            this.sourceCode = sourceCode;
        }

        @Override
        public String toString() {
            return "QueryField{" +
                    "field='" + field + '\'' +
                    ", label='" + label + '\'' +
                    ", dataType='" + dataType + '\'' +
                    ", sourceCode='" + sourceCode + '\'' +
                    '}';
        }

    }

    /**
     * 值集视图--表格列
     *
     * @author gaokuo.dai@hand-china.com 2018年6月19日下午2:54:03
     */
    @ApiModel("值集视图表格列")
    public static class TableField {
        @ApiModelProperty("标题")
        private String title;
        @ApiModelProperty("字段名")
        private String dataIndex;
        @ApiModelProperty("列宽")
        private Integer width;

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
         * @return 字段名
         */
        public String getDataIndex() {
            return dataIndex;
        }
        public void setDataIndex(String dataIndex) {
            this.dataIndex = dataIndex;
        }
        /**
         * @return 列宽
         */
        public Integer getWidth() {
            return width;
        }
        public void setWidth(Integer width) {
            this.width = width;
        }
        public TableField() {}

        public TableField(String title, String dataIndex, Integer width) {
            this.title = title;
            this.dataIndex = dataIndex;
            this.width = width;
        }

        @Override
        public String toString() {
            return "TableField [title=" + title + ", dataIndex=" + dataIndex + ", width=" + width + "]";
        }
    }

    public String getViewCode() {
        return viewCode;
    }

    public LovViewDTO setViewCode(String viewCode) {
        this.viewCode = viewCode;
        return this;
    }

    public String getLovCode() {
        return lovCode;
    }

    public LovViewDTO setLovCode(String lovCode) {
        this.lovCode = lovCode;
        return this;
    }

    public String getLovTypeCode() {
        return lovTypeCode;
    }

    public LovViewDTO setLovTypeCode(String lovTypeCode) {
        this.lovTypeCode = lovTypeCode;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public LovViewDTO setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getValueField() {
        return valueField;
    }

    public LovViewDTO setValueField(String valueField) {
        this.valueField = valueField;
        return this;
    }

    public String getDisplayField() {
        return displayField;
    }

    public LovViewDTO setDisplayField(String displayField) {
        this.displayField = displayField;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public LovViewDTO setTitle(String title) {
        this.title = title;
        return this;
    }

    public Integer getWidth() {
        return width;
    }

    public LovViewDTO setWidth(Integer width) {
        this.width = width;
        return this;
    }

    public Integer getHeight() {
        return height;
    }

    public LovViewDTO setHeight(Integer height) {
        this.height = height;
        return this;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public LovViewDTO setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public Integer getDelayLoadFlag() {
        return delayLoadFlag;
    }

    public LovViewDTO setDelayLoadFlag(Integer delayLoadFlag) {
        this.delayLoadFlag = delayLoadFlag;
        return this;
    }

    public String getChildrenFieldName() {
        return childrenFieldName;
    }

    public LovViewDTO setChildrenFieldName(String childrenFieldName) {
        this.childrenFieldName = childrenFieldName;
        return this;
    }

    public String getQueryUrl() {
        return queryUrl;
    }

    public LovViewDTO setQueryUrl(String queryUrl) {
        this.queryUrl = queryUrl;
        return this;
    }

    public List<QueryField> getQueryFields() {
        return queryFields;
    }

    public LovViewDTO setQueryFields(List<QueryField> queryFields) {
        this.queryFields = queryFields;
        return this;
    }

    public List<TableField> getTableFields() {
        return tableFields;
    }

    public LovViewDTO setTableFields(List<TableField> tableFields) {
        this.tableFields = tableFields;
        return this;
    }

    public String getViewName() {
        return viewName;
    }

    public LovViewDTO setViewName(String viewName) {
        this.viewName = viewName;
        return this;
    }

    public String getLovName() {
        return lovName;
    }

    public LovViewDTO setLovName(String lovName) {
        this.lovName = lovName;
        return this;
    }
}
