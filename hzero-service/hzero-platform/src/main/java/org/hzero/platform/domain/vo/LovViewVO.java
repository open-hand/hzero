package org.hzero.platform.domain.vo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.hzero.core.base.BaseConstants;
import org.hzero.platform.domain.entity.LovViewLine;
import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 值集视图VO
 *
 * @author gaokuo.dai@hand-china.com 2018年6月13日下午1:45:35
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel("值集视图VO")
public class LovViewVO {

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
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private LovViewAccessStatus accessStatus;

    /**
     * 值集视图可访问状态
     *
     * @author gaokuo.dai@hand-china.com 2019年3月1日上午12:09:24
     */
    public enum LovViewAccessStatus {
        /**
         * 可访问
         */
        ACCESS,
        /**
         * 禁止访问
         */
        FORBIDDEN,
        /**
         * 未找到
         */
        NOT_FOUND
    }

    public LovViewVO() {
    }

    /**
     * 构造函数
     *
     * @param accessStatus 值集视图可访问状态
     */
    public LovViewVO(LovViewAccessStatus accessStatus) {
        this.accessStatus = accessStatus;
    }

    /**
     * @return 视图代码
     */
    public String getViewCode() {
        return viewCode;
    }

    public void setViewCode(String viewCode) {
        this.viewCode = viewCode;
    }

    public String getViewName() {
        return viewName;
    }

    public LovViewVO setViewName(String viewName) {
        this.viewName = viewName;
        return this;
    }

    public String getLovName() {
        return lovName;
    }

    public LovViewVO setLovName(String lovName) {
        this.lovName = lovName;
        return this;
    }

    /**
     * @return 值集代码
     */
    public String getLovCode() {
        return lovCode;
    }

    public void setLovCode(String lovCode) {
        this.lovCode = lovCode;
    }

    /**
     * @return 值集类型
     */
    public String getLovTypeCode() {
        return lovTypeCode;
    }

    public void setLovTypeCode(String lovTypeCode) {
        this.lovTypeCode = lovTypeCode;
    }

    /**
     * @return 租户ID
     */
    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    /**
     * @return 值字段
     */
    public String getValueField() {
        return valueField;
    }

    public void setValueField(String valueField) {
        this.valueField = valueField;
    }

    /**
     * @return 显示字段
     */
    public String getDisplayField() {
        return displayField;
    }

    public void setDisplayField(String displayField) {
        this.displayField = displayField;
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
     * @return 宽度
     */
    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    /**
     * @return 高度
     */
    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    /**
     * @return 分页大小
     */
    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * @return 是否延迟加载
     */
    public Integer getDelayLoadFlag() {
        return delayLoadFlag;
    }

    public void setDelayLoadFlag(Integer delayLoadFlag) {
        this.delayLoadFlag = delayLoadFlag;
    }

    /**
     * @return 数据查询URL
     */
    public String getQueryUrl() {
        return queryUrl;
    }

    public void setQueryUrl(String queryUrl) {
        this.queryUrl = queryUrl;
    }

    /**
     * @return 树形查询子字段名
     */
    public String getChildrenFieldName() {
        return childrenFieldName;
    }

    public void setChildrenFieldName(String childrenFieldName) {
        this.childrenFieldName = childrenFieldName;
    }

    /**
     * @return 值集视图可访问状态
     */
    public LovViewAccessStatus getAccessStatus() {
        return accessStatus;
    }

    public LovViewVO setAccessStatus(LovViewAccessStatus accessStatus) {
        this.accessStatus = accessStatus;
        return this;
    }

    /**
     * @return 查询字段
     */
    public List<QueryField> getQueryFields() {
        return queryFields;
    }

    public void setQueryFields(List<QueryField> queryFields) {
        this.queryFields = queryFields;
    }

    /**
     * @return 表格列
     */
    public List<TableField> getTableFields() {
        return tableFields;
    }

    public void setTableFields(List<TableField> tableFields) {
        this.tableFields = tableFields;
    }

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
        @ApiModelProperty("数据类型")
        private String dataType;

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

        public String getDataType() {
            return dataType;
        }

        public TableField setDataType(String dataType) {
            this.dataType = dataType;
            return this;
        }

        public TableField() {
        }

        public TableField(String title, String dataIndex, Integer width) {
            this.title = title;
            this.dataIndex = dataIndex;
            this.width = width;
        }

        public TableField(String title, String dataIndex, Integer width, String dataType) {
            this.title = title;
            this.dataIndex = dataIndex;
            this.width = width;
            this.dataType = dataType;
        }

        @Override
        public String toString() {
            return "TableField{" +
                    "title='" + title + '\'' +
                    ", dataIndex='" + dataIndex + '\'' +
                    ", width=" + width +
                    ", dataType='" + dataType + '\'' +
                    '}';
        }
    }

    /**
     * 根据传入参数构建对象
     *
     * @param viewCode
     * @param lovCode
     * @param tenantId
     * @param valueField
     * @param displayField
     * @param title
     * @param width
     * @param height
     * @param pageSize
     * @param delayLoadFlag
     * @param queryUrl
     * @return 构建的对象
     */
    public static LovViewVO build(String viewCode, String viewName, String lovCode, String lovName, String lovTypeCode, Long tenantId, String valueField, String displayField,
                                  String title, Integer width, Integer height, Integer pageSize, Integer delayLoadFlag,
                                  String queryUrl, String childrenFieldName) {
        LovViewVO lovViewDTO = new LovViewVO();
        lovViewDTO.viewCode = viewCode;
        lovViewDTO.viewName = viewName;
        lovViewDTO.lovCode = lovCode;
        lovViewDTO.lovName = lovName;
        lovViewDTO.lovTypeCode = lovTypeCode;
        lovViewDTO.tenantId = tenantId;
        lovViewDTO.valueField = valueField;
        lovViewDTO.displayField = displayField;
        lovViewDTO.title = title;
        lovViewDTO.width = width;
        lovViewDTO.height = height;
        lovViewDTO.pageSize = pageSize;
        lovViewDTO.delayLoadFlag = delayLoadFlag;
        lovViewDTO.queryUrl = queryUrl;
        lovViewDTO.childrenFieldName = childrenFieldName;
        return lovViewDTO;
    }

    /**
     * 将Lov视图行转化为VO中的数据结构
     *
     * @param lines Lov视图行列表
     */
    public void convertLovViewLines(List<LovViewLine> lines) {
        Assert.notNull(lines, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        Collections.sort(lines, (a, b) -> Long.compare(a.getOrderSeq(), b.getOrderSeq()));
        this.queryFields = new ArrayList<>(8);
        this.tableFields = new ArrayList<>(8);
        for (LovViewLine line : lines) {
            if (Objects.equals(line.getQueryFieldFlag(), BaseConstants.Flag.YES)) {
                this.queryFields.add(this.convertLineToQueryField(line));
            }
            if (Objects.equals(line.getTableFieldFlag(), BaseConstants.Flag.YES)) {
                this.tableFields.add(this.convertLineToTableField(line));
            }
        }
    }

    /**
     * 将Lov视图行转化为查询条件
     *
     * @param line Lov视图行
     * @return 查询条件
     */
    private QueryField convertLineToQueryField(LovViewLine line) {
        return new QueryField(line.getFieldName(), line.getDisplay(), line.getDataType(), line.getSourceCode());
    }

    /**
     * 将Lov视图行转化为表格列
     *
     * @param line Lov视图行
     * @return 表格列
     */
    private TableField convertLineToTableField(LovViewLine line) {
        return new TableField(line.getDisplay(), line.getFieldName(), line.getTableFieldWidth(), line.getDataType());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("LovViewVO [viewCode=");
        builder.append(viewCode);
        builder.append(", viewName=");
        builder.append(viewName);
        builder.append(", lovCode=");
        builder.append(lovCode);
        builder.append(", lovName=");
        builder.append(lovName);
        builder.append(", lovTypeCode=");
        builder.append(lovTypeCode);
        builder.append(", tenantId=");
        builder.append(tenantId);
        builder.append(", valueField=");
        builder.append(valueField);
        builder.append(", displayField=");
        builder.append(displayField);
        builder.append(", title=");
        builder.append(title);
        builder.append(", width=");
        builder.append(width);
        builder.append(", height=");
        builder.append(height);
        builder.append(", pageSize=");
        builder.append(pageSize);
        builder.append(", delayLoadFlag=");
        builder.append(delayLoadFlag);
        builder.append(", childrenFieldName=");
        builder.append(childrenFieldName);
        builder.append(", queryUrl=");
        builder.append(queryUrl);
        builder.append(", queryFields=");
        builder.append(queryFields);
        builder.append(", tableFields=");
        builder.append(tableFields);
        builder.append("]");
        return builder.toString();
    }

}
