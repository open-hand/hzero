package org.hzero.platform.domain.vo;

/**
 * <p>
 * 多语言描述维护
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/06/21 19:57
 */
public class PromptVO {

    private Long promptId;
    private String promptCode;
    private String lang;
    private String description;
    private Long tenantId;
    private String tenantPromptCode;
    private String tenantLang;
    private String tenantDescription;
    private Long promptTenantId;
    private Long objectVersionNumber;

    @Override
    public String toString() {
        return "PromptVO{" + "promptId=" + promptId + ", promptCode='" + promptCode + '\'' + ", lang='" + lang + '\''
                        + ", description='" + description + '\'' + ", tenantId=" + tenantId + ", tenantPromptCode='"
                        + tenantPromptCode + '\'' + ", tenantLang='" + tenantLang + '\'' + ", tenantDescription='"
                        + tenantDescription + '\'' + ", promptTenantId=" + promptTenantId + ", objectVersionNumber="
                        + objectVersionNumber + '}';
    }

    public Long getPromptId() {
        return promptId;
    }

    public void setPromptId(Long promptId) {
        this.promptId = promptId;
    }

    public String getPromptCode() {
        return promptCode;
    }

    public void setPromptCode(String promptCode) {
        this.promptCode = promptCode;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getTenantPromptCode() {
        return tenantPromptCode;
    }

    public void setTenantPromptCode(String tenantPromptCode) {
        this.tenantPromptCode = tenantPromptCode;
    }

    public String getTenantLang() {
        return tenantLang;
    }

    public void setTenantLang(String tenantLang) {
        this.tenantLang = tenantLang;
    }

    public String getTenantDescription() {
        return tenantDescription;
    }

    public void setTenantDescription(String tenantDescription) {
        this.tenantDescription = tenantDescription;
    }

    public Long getPromptTenantId() {
        return promptTenantId;
    }

    public void setPromptTenantId(Long promptTenantId) {
        this.promptTenantId = promptTenantId;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }
}
