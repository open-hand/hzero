package org.hzero.boot.message.entity;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 消息模板
 *
 * @author qingsheng.chen@hand-china 2018-07-25 16:46:08
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@SuppressWarnings("all")
public class MessageTemplate {

    /**
     * 消息模板ID
     */
    private Long templateId;
    private Long tenantId;
    /**
     * 模板编码
     */
    private String templateCode;
    /**
     * 模板名称
     */
    private String templateName;
    /**
     * 模板标题
     */
    private String templateTitle;
    /**
     * 模板内容
     */
    private String templateContent;
    /**
     * 模版类型，值集:HMSG.TEMPLATE_TYPE
     */
    private String templateTypeCode;
    /**
     * 消息类型，值集:HMSG.MESSAGE_CATEGORY
     */
    private String messageCategoryCode;
    /**
     * 消息子类型，值集:HMSG.MESSAGE_SUBCATEGORY
     */
    private String messageSubcategoryCode;
    /**
     * 短信非空，外部代码
     */
    private String externalCode;
    /**
     * 短信服务类型，值集：HMSG.SMS_SERVER_TYPE
     */
    private String serverTypeCode;
    /**
     * 取值SQL
     */
    private String sqlValue;
    /**
     * 启用标识
     */
    private Integer enabledFlag;

    /**
     * @return 表ID，主键，供其他表做外键
     */
    public Long getTemplateId() {
        return templateId;
    }

    public MessageTemplate setTemplateId(Long templateId) {
        this.templateId = templateId;
        return this;
    }

    /**
     * @return 租户ID
     */
    public Long getTenantId() {
        return tenantId;
    }

    public MessageTemplate setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    /**
     * @return 模板代码
     */
    public String getTemplateCode() {
        return templateCode;
    }

    public MessageTemplate setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
        return this;
    }

    /**
     * @return 模板名称
     */
    public String getTemplateName() {
        return templateName;
    }

    public MessageTemplate setTemplateName(String templateName) {
        this.templateName = templateName;
        return this;
    }

    /**
     * @return 模板标题
     */
    public String getTemplateTitle() {
        return templateTitle;
    }

    public MessageTemplate setTemplateTitle(String templateTitle) {
        this.templateTitle = templateTitle;
        return this;
    }

    /**
     * @return 模板内容
     */
    public String getTemplateContent() {
        return templateContent;
    }

    public MessageTemplate setTemplateContent(String templateContent) {
        this.templateContent = templateContent;
        return this;
    }

    /**
     * @return 模版类型，值集:HMSG.TEMPLATE_TYPE
     */
    public String getTemplateTypeCode() {
        return templateTypeCode;
    }

    public MessageTemplate setTemplateTypeCode(String templateTypeCode) {
        this.templateTypeCode = templateTypeCode;
        return this;
    }

    /**
     * @return 消息类型，值集:HMSG.MESSAGE_CATEGORY
     */
    public String getMessageCategoryCode() {
        return messageCategoryCode;
    }

    public MessageTemplate setMessageCategoryCode(String messageCategoryCode) {
        this.messageCategoryCode = messageCategoryCode;
        return this;
    }

    /**
     * @return 消息子类型，值集:HMSG.MESSAGE_SUBCATEGORY
     */
    public String getMessageSubcategoryCode() {
        return messageSubcategoryCode;
    }

    public MessageTemplate setMessageSubcategoryCode(String messageSubcategoryCode) {
        this.messageSubcategoryCode = messageSubcategoryCode;
        return this;
    }

    public String getExternalCode() {
        return externalCode;
    }

    public MessageTemplate setExternalCode(String externalCode) {
        this.externalCode = externalCode;
        return this;
    }

    public String getServerTypeCode() {
        return serverTypeCode;
    }

    public MessageTemplate setServerTypeCode(String serverTypeCode) {
        this.serverTypeCode = serverTypeCode;
        return this;
    }

    /**
     * @return 取值SQL
     */
    public String getSqlValue() {
        return sqlValue;
    }

    public MessageTemplate setSqlValue(String sqlValue) {
        this.sqlValue = sqlValue;
        return this;
    }

    /**
     * @return 启用标识
     */
    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public MessageTemplate setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
        return this;
    }

    @Override
    public String toString() {
        return "MessageTemplate{" +
                "templateId=" + templateId +
                ", tenantId=" + tenantId +
                ", templateCode='" + templateCode + '\'' +
                ", templateName='" + templateName + '\'' +
                ", templateTitle='" + templateTitle + '\'' +
                ", templateContent='" + templateContent + '\'' +
                ", templateTypeCode='" + templateTypeCode + '\'' +
                ", messageCategoryCode='" + messageCategoryCode + '\'' +
                ", messageSubcategoryCode='" + messageSubcategoryCode + '\'' +
                ", externalCode='" + externalCode + '\'' +
                ", serverTypeCode='" + serverTypeCode + '\'' +
                ", sqlValue='" + sqlValue + '\'' +
                ", enabledFlag=" + enabledFlag +
                '}';
    }
}
