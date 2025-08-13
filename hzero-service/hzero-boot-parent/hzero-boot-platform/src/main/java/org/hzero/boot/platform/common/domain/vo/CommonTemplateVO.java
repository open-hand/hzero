package org.hzero.boot.platform.common.domain.vo;

/**
 * 通用模板缓存对象
 *
 * @author bergturing 2020/08/04 16:40
 */
public class CommonTemplateVO {
    /**
     * 模板编码
     */
    private String templateCode;

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 模板分类.HPFM.TEMPLATE_CATEGORY
     */
    private String templateCategoryCode;

    /**
     * 模板内容
     */
    private String templateContent;

    /**
     * 语言
     */
    private String lang;

    /**
     * 租户ID,hpfm_tenant.tenant_id
     */
    private Long tenantId;

    /**
     * 是否启用。1启用，0未启用
     */
    private Integer enabledFlag;

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

    public String getTemplateContent() {
        return templateContent;
    }

    public void setTemplateContent(String templateContent) {
        this.templateContent = templateContent;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    @Override
    public String toString() {
        return "CommonTemplateCacheVO{" +
                "templateCode='" + templateCode + '\'' +
                ", templateName='" + templateName + '\'' +
                ", templateCategoryCode='" + templateCategoryCode + '\'' +
                ", templateContent='" + templateContent + '\'' +
                ", lang='" + lang + '\'' +
                ", tenantId=" + tenantId +
                ", enabledFlag=" + enabledFlag +
                '}';
    }
}
