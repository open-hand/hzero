package org.hzero.starter.sms.entity;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2019/12/23 17:10
 */
public class SmsMessage {

    private Long tenantId;
    private String messageTypeCode;
    private String templateCode;
    private String lang;
    private String serverCode;
    private String subject;
    private String content;
    private Integer sendFlag;
    private String sendArgs;
    private String externalCode;

    public Long getTenantId() {
        return tenantId;
    }

    public SmsMessage setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getMessageTypeCode() {
        return messageTypeCode;
    }

    public SmsMessage setMessageTypeCode(String messageTypeCode) {
        this.messageTypeCode = messageTypeCode;
        return this;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public SmsMessage setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
        return this;
    }

    public String getLang() {
        return lang;
    }

    public SmsMessage setLang(String lang) {
        this.lang = lang;
        return this;
    }

    public String getServerCode() {
        return serverCode;
    }

    public SmsMessage setServerCode(String serverCode) {
        this.serverCode = serverCode;
        return this;
    }

    public String getSubject() {
        return subject;
    }

    public SmsMessage setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public String getContent() {
        return content;
    }

    public SmsMessage setContent(String content) {
        this.content = content;
        return this;
    }

    public Integer getSendFlag() {
        return sendFlag;
    }

    public SmsMessage setSendFlag(Integer sendFlag) {
        this.sendFlag = sendFlag;
        return this;
    }

    public String getSendArgs() {
        return sendArgs;
    }

    public SmsMessage setSendArgs(String sendArgs) {
        this.sendArgs = sendArgs;
        return this;
    }

    public String getExternalCode() {
        return externalCode;
    }

    public SmsMessage setExternalCode(String externalCode) {
        this.externalCode = externalCode;
        return this;
    }
}
