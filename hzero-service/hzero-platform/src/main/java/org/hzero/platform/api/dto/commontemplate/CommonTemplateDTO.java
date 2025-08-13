package org.hzero.platform.api.dto.commontemplate;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.starter.keyencrypt.core.Encrypt;

import java.util.Date;

/**
 * 通用模板数据传输对象
 *
 * @author bergturing 2020/08/04 11:18
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonTemplateDTO {
    /**
     * 值集编码：HPFM.TEMPLATE_CATEGORY
     */
    protected static final String HPFM_TEMPLATE_CATEGORY = "HPFM.TEMPLATE_CATEGORY";
    /**
     * 值集编码：HPFM.LANGUAGE
     */
    protected static final String HPFM_LANGUAGE = "HPFM.LANGUAGE";

    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Encrypt
    private Long templateId;

    @ApiModelProperty(value = "模板编码")
    private String templateCode;

    @ApiModelProperty(value = "模板名称")
    private String templateName;

    @ApiModelProperty(value = "模板分类.HPFM.TEMPLATE_CATEGORY")
    @LovValue(lovCode = HPFM_TEMPLATE_CATEGORY, meaningField = "templateCategoryCodeMeaning")
    private String templateCategoryCode;

    @ApiModelProperty(value = "模板内容")
    private String templateContent;

    @ApiModelProperty(value = "语言")
    @LovValue(lovCode = HPFM_LANGUAGE, meaningField = "langMeaning")
    private String lang;

    @ApiModelProperty(value = "租户ID,hpfm_tenant.tenant_id")
    private Long tenantId;

    @ApiModelProperty(value = "是否启用。1启用，0未启用")
    private Integer enabledFlag;

    @ApiModelProperty(value = "创建日期")
    private Date creationDate;

    @ApiModelProperty(value = "租户名称")
    private String tenantName;

    @ApiModelProperty(value = "模板分类含义")
    private String templateCategoryCodeMeaning;

    @ApiModelProperty(value = "语言含义")
    private String langMeaning;

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
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

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getTemplateCategoryCodeMeaning() {
        return templateCategoryCodeMeaning;
    }

    public void setTemplateCategoryCodeMeaning(String templateCategoryCodeMeaning) {
        this.templateCategoryCodeMeaning = templateCategoryCodeMeaning;
    }

    public String getLangMeaning() {
        return langMeaning;
    }

    public void setLangMeaning(String langMeaning) {
        this.langMeaning = langMeaning;
    }

    @Override
    public String toString() {
        return "CommonTemplateDTO{" +
                "templateId=" + templateId +
                ", templateCode='" + templateCode + '\'' +
                ", templateName='" + templateName + '\'' +
                ", templateCategoryCode='" + templateCategoryCode + '\'' +
                ", templateContent='" + templateContent + '\'' +
                ", lang='" + lang + '\'' +
                ", tenantId=" + tenantId +
                ", enabledFlag=" + enabledFlag +
                ", creationDate=" + creationDate +
                ", tenantName='" + tenantName + '\'' +
                ", templateCategoryCodeMeaning='" + templateCategoryCodeMeaning + '\'' +
                ", langMeaning='" + langMeaning + '\'' +
                '}';
    }
}
