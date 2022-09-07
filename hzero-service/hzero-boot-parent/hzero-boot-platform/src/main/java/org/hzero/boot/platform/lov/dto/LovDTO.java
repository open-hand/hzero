package org.hzero.boot.platform.lov.dto;

import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModelProperty;

import java.util.StringJoiner;

/**
 * 值集头DTO
 *
 * @author gaokuo.dai@hand-china.com 2018年9月8日下午5:10:32
 */
public class LovDTO {

    @ApiModelProperty("值集ID")
    private Long lovId;
    @ApiModelProperty("租户ID")
    private Long tenantId;
    @Size(max = 30)
    @ApiModelProperty("值集代码")
    private String lovCode;
    @ApiModelProperty("值集名称")
    private String lovName;
    @Size(max = 30)
    @ApiModelProperty("值集类型")
    private String lovTypeCode;
    @ApiModelProperty("查询URL")
    private String customUrl;
    @Size(max = 120)
    @ApiModelProperty("目标路由")
    private String routeName;
    @ApiModelProperty("是否必须分页")
    private Integer mustPageFlag;
    @ApiModelProperty("值字段")
    private String valueField;
    @ApiModelProperty("显示字段")
    private String displayField;
    @ApiModelProperty("加密字段")
    private String encryptField;
    @ApiModelProperty("是否公开")
    private Integer publicFlag;
    @ApiModelProperty("请求方式，值集：HPFM.REQUEST_METHOD")
    private String requestMethod;

    public Integer getPublicFlag() {
        return publicFlag;
    }

    public void setPublicFlag(Integer publicFlag) {
        this.publicFlag = publicFlag;
    }

    /**
     * @return 表ID，主键，供其他表做外键
     */
    public Long getLovId() {
        return lovId;
    }

    public void setLovId(Long lovId) {
        this.lovId = lovId;
    }

    /**
     * @return LOV代码
     */
    public String getLovCode() {
        return lovCode;
    }

    public void setLovCode(String lovCode) {
        this.lovCode = lovCode;
    }

    public String getLovName() {
        return lovName;
    }

    public void setLovName(String lovName) {
        this.lovName = lovName;
    }

    /**
     * @return LOV数据类型: URL/SQL/IDP
     */
    public String getLovTypeCode() {
        return lovTypeCode;
    }

    public void setLovTypeCode(String lovTypeCode) {
        this.lovTypeCode = lovTypeCode;
    }

    /**
     * @return 目标路由
     */
    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    /**
     * @return 租户ID, null时代表全局LOV
     */
    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    /**
     * @return 查询URL
     */
    public String getCustomUrl() {
        return customUrl;
    }

    public void setCustomUrl(String customUrl) {
        this.customUrl = customUrl;
    }

    /**
     * @return 是否必须分页
     */
    public Integer getMustPageFlag() {
        return mustPageFlag;
    }

    public void setMustPageFlag(Integer mustPageFlag) {
        this.mustPageFlag = mustPageFlag;
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

    public String getEncryptField() {
        return encryptField;
    }

    public LovDTO setEncryptField(String encryptField) {
        this.encryptField = encryptField;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("LovDTO [lovId=");
        builder.append(lovId);
        builder.append(", tenantId=");
        builder.append(tenantId);
        builder.append(", lovCode=");
        builder.append(lovCode);
        builder.append(", lovName=");
        builder.append(lovName);
        builder.append(", lovTypeCode=");
        builder.append(lovTypeCode);
        builder.append(", customUrl=");
        builder.append(customUrl);
        builder.append(", routeName=");
        builder.append(routeName);
        builder.append(", mustPageFlag=");
        builder.append(mustPageFlag);
        builder.append(", valueField=");
        builder.append(valueField);
        builder.append(", valueTableAlias=");
        builder.append(", displayField=");
        builder.append(displayField);
        builder.append(", meaningTableAlias=");
        builder.append("]");
        return builder.toString();
    }

}
