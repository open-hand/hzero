package org.hzero.platform.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * 分配模板 DTO
 *
 * @author xiaoyu.zhao@hand-china.com 2019/07/03 15:45
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TemplateAssignDTO {
    @Encrypt
    private Long templateAssignId;
    private String sourceType;
    @Encrypt
    private String sourceKey;
    @Encrypt
    private Long templateId;
    private Long tenantId;
    private Integer defaultFlag;

    private String templateCode;
    private String templateName;
    private String templateAvatar;
    private String templatePath;
    private String templateLevelCode;

    public String getTemplateLevelCode() {
        return templateLevelCode;
    }

    public void setTemplateLevelCode(String templateLevelCode) {
        this.templateLevelCode = templateLevelCode;
    }

    public Long getTemplateAssignId() {
        return templateAssignId;
    }

    public void setTemplateAssignId(Long templateAssignId) {
        this.templateAssignId = templateAssignId;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getSourceKey() {
        return sourceKey;
    }

    public void setSourceKey(String sourceKey) {
        this.sourceKey = sourceKey;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Integer getDefaultFlag() {
        return defaultFlag;
    }

    public void setDefaultFlag(Integer defaultFlag) {
        this.defaultFlag = defaultFlag;
    }

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

    public String getTemplateAvatar() {
        return templateAvatar;
    }

    public void setTemplateAvatar(String templateAvatar) {
        this.templateAvatar = templateAvatar;
    }

    public String getTemplatePath() {
        return templatePath;
    }

    public void setTemplatePath(String templatePath) {
        this.templatePath = templatePath;
    }

    @Override
    public String toString() {
        return "TemplateAssignDTO{" + "templateAssignId=" + templateAssignId + ", sourceType='" + sourceType + '\''
                        + ", sourceKey='" + sourceKey + '\'' + ", templateId=" + templateId + ", tenantId=" + tenantId
                        + ", defaultFlag=" + defaultFlag + ", templateCode='" + templateCode + '\''
                        + ", templateName='" + templateName + '\'' + ", templateAvatar='" + templateAvatar + '\''
                        + ", templatePath='" + templatePath + '\'' + '}';
    }
}
