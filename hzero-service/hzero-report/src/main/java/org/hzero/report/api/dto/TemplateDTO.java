package org.hzero.report.api.dto;

import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2019/12/20 17:08
 */
public class TemplateDTO {


    @Encrypt
    private Long templateId;
    @Encrypt
    private Long templateDtlId;
    private Long tenantId;
    private String codeTenant;
    private String tenantName;
    private String templateCode;
    private String templateName;
    private String lang;
    private String langName;
    private String type;

    public Long getTemplateId() {
        return templateId;
    }

    public TemplateDTO setTemplateId(Long templateId) {
        this.templateId = templateId;
        return this;
    }

    public Long getTemplateDtlId() {
        return templateDtlId;
    }

    public TemplateDTO setTemplateDtlId(Long templateDtlId) {
        this.templateDtlId = templateDtlId;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public TemplateDTO setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getCodeTenant() {
        return codeTenant;
    }

    public TemplateDTO setCodeTenant(String codeTenant) {
        this.codeTenant = codeTenant;
        return this;
    }

    public String getTenantName() {
        return tenantName;
    }

    public TemplateDTO setTenantName(String tenantName) {
        this.tenantName = tenantName;
        return this;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public TemplateDTO setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
        return this;
    }

    public String getTemplateName() {
        return templateName;
    }

    public TemplateDTO setTemplateName(String templateName) {
        this.templateName = templateName;
        return this;
    }

    public String getLang() {
        return lang;
    }

    public TemplateDTO setLang(String lang) {
        this.lang = lang;
        return this;
    }

    public String getLangName() {
        return langName;
    }

    public TemplateDTO setLangName(String langName) {
        this.langName = langName;
        return this;
    }

    public String getType() {
        return type;
    }

    public TemplateDTO setType(String type) {
        this.type = type;
        return this;
    }

    @Override
    public String toString() {
        return "TemplateDTO{" +
                "templateId=" + templateId +
                ", tenantId=" + tenantId +
                ", codeTenant='" + codeTenant + '\'' +
                ", tenantName='" + tenantName + '\'' +
                ", templateCode='" + templateCode + '\'' +
                ", templateName='" + templateName + '\'' +
                ", lang='" + lang + '\'' +
                ", langName='" + langName + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
