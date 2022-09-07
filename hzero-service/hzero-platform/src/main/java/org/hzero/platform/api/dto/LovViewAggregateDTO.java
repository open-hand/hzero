package org.hzero.platform.api.dto;

import java.util.List;

import org.hzero.platform.domain.entity.LovViewLine;

import io.swagger.annotations.ApiModelProperty;

/**
 * 聚合返回值集视图头行数据DTO
 *
 * @author xiaoyu.zhao@hand-china.com 2020/12/23 14:57
 */
public class LovViewAggregateDTO {
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
    @ApiModelProperty("启用标识")
    private Integer enabledFlag;
    private List<LovViewLine> lovViewLines;

    public String getViewCode() {
        return viewCode;
    }

    public LovViewAggregateDTO setViewCode(String viewCode) {
        this.viewCode = viewCode;
        return this;
    }

    public String getViewName() {
        return viewName;
    }

    public LovViewAggregateDTO setViewName(String viewName) {
        this.viewName = viewName;
        return this;
    }

    public String getLovCode() {
        return lovCode;
    }

    public LovViewAggregateDTO setLovCode(String lovCode) {
        this.lovCode = lovCode;
        return this;
    }

    public String getLovName() {
        return lovName;
    }

    public LovViewAggregateDTO setLovName(String lovName) {
        this.lovName = lovName;
        return this;
    }

    public String getLovTypeCode() {
        return lovTypeCode;
    }

    public LovViewAggregateDTO setLovTypeCode(String lovTypeCode) {
        this.lovTypeCode = lovTypeCode;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public LovViewAggregateDTO setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getValueField() {
        return valueField;
    }

    public LovViewAggregateDTO setValueField(String valueField) {
        this.valueField = valueField;
        return this;
    }

    public String getDisplayField() {
        return displayField;
    }

    public LovViewAggregateDTO setDisplayField(String displayField) {
        this.displayField = displayField;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public LovViewAggregateDTO setTitle(String title) {
        this.title = title;
        return this;
    }

    public Integer getWidth() {
        return width;
    }

    public LovViewAggregateDTO setWidth(Integer width) {
        this.width = width;
        return this;
    }

    public Integer getHeight() {
        return height;
    }

    public LovViewAggregateDTO setHeight(Integer height) {
        this.height = height;
        return this;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public LovViewAggregateDTO setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public Integer getDelayLoadFlag() {
        return delayLoadFlag;
    }

    public LovViewAggregateDTO setDelayLoadFlag(Integer delayLoadFlag) {
        this.delayLoadFlag = delayLoadFlag;
        return this;
    }

    public String getChildrenFieldName() {
        return childrenFieldName;
    }

    public LovViewAggregateDTO setChildrenFieldName(String childrenFieldName) {
        this.childrenFieldName = childrenFieldName;
        return this;
    }

    public String getQueryUrl() {
        return queryUrl;
    }

    public LovViewAggregateDTO setQueryUrl(String queryUrl) {
        this.queryUrl = queryUrl;
        return this;
    }

    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public LovViewAggregateDTO setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
        return this;
    }

    public List<LovViewLine> getLovViewLines() {
        return lovViewLines;
    }

    public LovViewAggregateDTO setLovViewLines(List<LovViewLine> lovViewLines) {
        this.lovViewLines = lovViewLines;
        return this;
    }
}
