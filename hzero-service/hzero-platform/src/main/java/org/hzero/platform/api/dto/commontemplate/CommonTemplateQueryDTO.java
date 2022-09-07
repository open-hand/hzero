package org.hzero.platform.api.dto.commontemplate;

import io.swagger.annotations.ApiModelProperty;

/**
 * 通用模板查询数据传输对象
 *
 * @author bergturing 2020/08/04 11:41
 */
public class CommonTemplateQueryDTO {
    @ApiModelProperty(value = "模板编码")
    private String templateCode;

    @ApiModelProperty(value = "模板名称")
    private String templateName;

    @ApiModelProperty(value = "模板分类.HPFM.TEMPLATE_CATEGORY")
    private String templateCategoryCode;

    @ApiModelProperty(value = "语言")
    private String lang;

    @ApiModelProperty(value = "是否启用。1启用，0未启用")
    private Integer enabledFlag;

    @ApiModelProperty(value = "租户ID,hpfm_tenant.tenant_id")
    private Long tenantId;

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getTemplateCategoryCode() {
        return templateCategoryCode;
    }

    public void setTemplateCategoryCode(String templateCategoryCode) {
        this.templateCategoryCode = templateCategoryCode;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    @Override
    public String toString() {
        return "CommonTemplateQueryDTO{" +
                "templateCode='" + templateCode + '\'' +
                ", templateName='" + templateName + '\'' +
                ", templateCategoryCode='" + templateCategoryCode + '\'' +
                ", lang='" + lang + '\'' +
                ", enabledFlag=" + enabledFlag +
                ", tenantId=" + tenantId +
                '}';
    }
}
