package org.hzero.platform.api.dto;

import java.util.List;

import org.hzero.platform.domain.entity.LovValue;
import org.hzero.starter.keyencrypt.core.Encrypt;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModelProperty;

/**
 * 聚合返回值集头行数据DTO
 *
 * @author xiaoyu.zhao@hand-china.com 2020/12/23 14:57
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LovAggregateDTO {
    @ApiModelProperty("值集ID")
    @Encrypt
    private Long lovId;
    @ApiModelProperty("值集代码")
    private String lovCode;
    @ApiModelProperty("值集类型")
    private String lovTypeCode;
    @ApiModelProperty("目标路由")
    private String routeName;
    @ApiModelProperty("值集名称")
    private String lovName;
    @ApiModelProperty("描述")
    private String description;
    @ApiModelProperty("租户ID")
    private Long tenantId;
    @ApiModelProperty("父值集代码")
    private String parentLovCode;
    @ApiModelProperty("父级值集租户ID")
    private Long parentTenantId;
    @ApiModelProperty("查询URL")
    private String customUrl;
    @ApiModelProperty("值字段")
    private String valueField;
    @ApiModelProperty("显示字段")
    private String displayField;
    @ApiModelProperty("加密字段")
    private String encryptField;
    @ApiModelProperty("是否必须分页")
    private Integer mustPageFlag;
    @ApiModelProperty("是否启用")
    private Integer enabledFlag;
    @ApiModelProperty("翻译sql")
    private String translationSql;
    @ApiModelProperty("公共标记")
    private Integer publicFlag;

    private List<LovValue> lovValues;

    public Long getLovId() {
        return lovId;
    }

    public LovAggregateDTO setLovId(Long lovId) {
        this.lovId = lovId;
        return this;
    }

    public String getLovCode() {
        return lovCode;
    }

    public LovAggregateDTO setLovCode(String lovCode) {
        this.lovCode = lovCode;
        return this;
    }

    public String getLovTypeCode() {
        return lovTypeCode;
    }

    public LovAggregateDTO setLovTypeCode(String lovTypeCode) {
        this.lovTypeCode = lovTypeCode;
        return this;
    }

    public String getRouteName() {
        return routeName;
    }

    public LovAggregateDTO setRouteName(String routeName) {
        this.routeName = routeName;
        return this;
    }

    public String getLovName() {
        return lovName;
    }

    public LovAggregateDTO setLovName(String lovName) {
        this.lovName = lovName;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public LovAggregateDTO setDescription(String description) {
        this.description = description;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public LovAggregateDTO setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getParentLovCode() {
        return parentLovCode;
    }

    public LovAggregateDTO setParentLovCode(String parentLovCode) {
        this.parentLovCode = parentLovCode;
        return this;
    }

    public Long getParentTenantId() {
        return parentTenantId;
    }

    public LovAggregateDTO setParentTenantId(Long parentTenantId) {
        this.parentTenantId = parentTenantId;
        return this;
    }

    public String getCustomUrl() {
        return customUrl;
    }

    public LovAggregateDTO setCustomUrl(String customUrl) {
        this.customUrl = customUrl;
        return this;
    }

    public String getValueField() {
        return valueField;
    }

    public LovAggregateDTO setValueField(String valueField) {
        this.valueField = valueField;
        return this;
    }

    public String getDisplayField() {
        return displayField;
    }

    public LovAggregateDTO setDisplayField(String displayField) {
        this.displayField = displayField;
        return this;
    }

    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public LovAggregateDTO setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
        return this;
    }

    public String getTranslationSql() {
        return translationSql;
    }

    public LovAggregateDTO setTranslationSql(String translationSql) {
        this.translationSql = translationSql;
        return this;
    }

    public Integer getPublicFlag() {
        return publicFlag;
    }

    public LovAggregateDTO setPublicFlag(Integer publicFlag) {
        this.publicFlag = publicFlag;
        return this;
    }

    public List<org.hzero.platform.domain.entity.LovValue> getLovValues() {
        return lovValues;
    }

    public LovAggregateDTO setLovValues(List<org.hzero.platform.domain.entity.LovValue> lovValues) {
        this.lovValues = lovValues;
        return this;
    }

    public String getEncryptField() {
        return encryptField;
    }

    public LovAggregateDTO setEncryptField(String encryptField) {
        this.encryptField = encryptField;
        return this;
    }

    public Integer getMustPageFlag() {
        return mustPageFlag;
    }

    public LovAggregateDTO setMustPageFlag(Integer mustPageFlag) {
        this.mustPageFlag = mustPageFlag;
        return this;
    }
}
