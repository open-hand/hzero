package org.hzero.boot.message.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.Objects;

/**
 * <p>
 * 消息内容实体
 * </p>
 *
 * @author qingsheng.chen 2018/8/6 星期一 20:13
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@SuppressWarnings("all")
public class Message {

    public Message() {
    }

    public Message(Message message) {
        this.messageId = message.getMessageId();
        this.tenantId = message.getTenantId();
        this.messageTypeCode = message.getMessageTypeCode();
        this.lang = message.getLang();
        this.templateCode = message.getTemplateCode();
        this.serverCode = message.getServerCode();
        this.subject = message.getSubject();
        this.content = message.getContent();
        this.plainContent = message.getPlainContent();
        this.sendFlag = message.getSendFlag();
        this.transactionId = message.getTransactionId();
        this.trxStatusCode = message.getTrxStatusCode();
        this.sendDate = message.getSendDate();
        this.externalCode = message.getExternalCode();
        this.serverTypeCode = message.getServerTypeCode();
        if (message.getReceiverList() != null) {
            List<Receiver> receiverList = new ArrayList<>();
            for (Receiver receiver : message.getReceiverList()) {
                receiverList.add(new Receiver(receiver));
            }
            this.receiverList = receiverList;
        } else {
            this.receiverList = null;
        }
        this.sendArgs = message.getSendArgs();
        this.targetUserTenantId = message.getTargetUserTenantId();
    }

    /**
     * 消息ID，hzeor_message.message_id
     */
    private Long messageId;
    /**
     * 租户ID
     */
    private Long tenantId;
    /**
     * 消息类型编码，值集：HMSG.MESSAGE_TYPE
     */
    private String messageTypeCode;
    /**
     * 语言
     */
    private String lang;
    /**
     * 消息模板编码
     */
    private String templateCode;
    /**
     * 服务账户编码
     */
    private String serverCode;
    /**
     * 消息主题
     */
    private String subject;
    /**
     * 消息内容
     */
    private String content;
    /**
     * 明文消息内容
     */
    private String plainContent;
    /**
     * 发送标记
     */
    private Integer sendFlag;
    /**
     * 消息事务ID
     */
    private Long transactionId;
    /**
     * 消息发送状态，值集：HMSG.TRANSACTION_STATUS
     */
    private String trxStatusCode;
    /**
     * 消息发送时间
     */
    private Date sendDate;

    /**
     * 短信非空，外部代码
     */
    private String externalCode;
    /**
     * 短信服务类型，值集：HMSG.SMS_SERVER_TYPE
     */
    private String serverTypeCode;

    private List<Receiver> receiverList;

    /**
     * 消息发送的参数json，用于消息重发
     */
    private String sendArgs;

    /**
     * 目标用户租户ID
     */
    private Long targetUserTenantId;

    public Long getMessageId() {
        return messageId;
    }

    public Message setMessageId(Long messageId) {
        this.messageId = messageId;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public Message setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getMessageTypeCode() {
        return messageTypeCode;
    }

    public Message setMessageTypeCode(String messageTypeCode) {
        this.messageTypeCode = messageTypeCode;
        return this;
    }

    public String getLang() {
        return lang;
    }

    public Message setLang(String lang) {
        this.lang = lang;
        return this;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public Message setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
        return this;
    }

    public String getServerCode() {
        return serverCode;
    }

    public Message setServerCode(String serverCode) {
        this.serverCode = serverCode;
        return this;
    }

    public String getSubject() {
        return subject;
    }

    public Message setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public String getContent() {
        return content;
    }

    public Message setContent(String content) {
        this.content = content;
        return this;
    }

    public String getPlainContent() {
        return plainContent;
    }

    public Message setPlainContent(String plainContent) {
        this.plainContent = plainContent;
        return this;
    }

    public Integer getSendFlag() {
        return sendFlag;
    }

    public Message setSendFlag(Integer sendFlag) {
        this.sendFlag = sendFlag;
        return this;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public Message setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public String getTrxStatusCode() {
        return trxStatusCode;
    }

    public Message setTrxStatusCode(String trxStatusCode) {
        this.trxStatusCode = trxStatusCode;
        return this;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public Message setSendDate(Date sendDate) {
        this.sendDate = sendDate;
        return this;
    }

    public List<Receiver> getReceiverList() {
        return receiverList;
    }

    public void setReceiverList(List<Receiver> receiverList) {
        this.receiverList = receiverList;
    }

    public String getExternalCode() {
        return externalCode;
    }

    public Message setExternalCode(String externalCode) {
        this.externalCode = externalCode;
        return this;
    }

    public String getServerTypeCode() {
        return serverTypeCode;
    }

    public Message setServerTypeCode(String serverTypeCode) {
        this.serverTypeCode = serverTypeCode;
        return this;
    }

    public String getSendArgs() {
        return sendArgs;
    }

    public Message setSendArgs(String sendArgs) {
        this.sendArgs = sendArgs;
        return this;
    }

    public Long getTargetUserTenantId() {
        return targetUserTenantId;
    }

    public Message setTargetUserTenantId(Long targetUserTenantId) {
        this.targetUserTenantId = targetUserTenantId;
        return this;
    }

    @Override
    public String toString() {
        return "Message{" +
                "messageId=" + messageId +
                ", tenantId=" + tenantId +
                ", messageTypeCode='" + messageTypeCode + '\'' +
                ", lang='" + lang + '\'' +
                ", templateCode='" + templateCode + '\'' +
                ", serverCode='" + serverCode + '\'' +
                ", subject='" + subject + '\'' +
                ", content='" + content + '\'' +
                ", plainContent='" + plainContent + '\'' +
                ", sendFlag=" + sendFlag +
                ", transactionId=" + transactionId +
                ", trxStatusCode='" + trxStatusCode + '\'' +
                ", sendDate=" + sendDate +
                ", externalCode='" + externalCode + '\'' +
                ", serverTypeCode='" + serverTypeCode + '\'' +
                ", receiverList=" + receiverList +
                ", sendArgs='" + sendArgs + '\'' +
                ", targetUserTenantId=" + targetUserTenantId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Message)) {
            return false;
        }
        Message message = (Message) o;
        return Objects.equal(messageId, message.messageId) &&
                Objects.equal(tenantId, message.tenantId) &&
                Objects.equal(messageTypeCode, message.messageTypeCode) &&
                Objects.equal(lang, message.lang) &&
                Objects.equal(templateCode, message.templateCode) &&
                Objects.equal(serverCode, message.serverCode) &&
                Objects.equal(subject, message.subject) &&
                Objects.equal(content, message.content) &&
                Objects.equal(plainContent, message.plainContent) &&
                Objects.equal(sendFlag, message.sendFlag) &&
                Objects.equal(transactionId, message.transactionId) &&
                Objects.equal(trxStatusCode, message.trxStatusCode) &&
                Objects.equal(sendDate, message.sendDate) &&
                Objects.equal(externalCode, message.externalCode) &&
                Objects.equal(serverTypeCode, message.serverTypeCode) &&
                Objects.equal(receiverList, message.receiverList) &&
                Objects.equal(sendArgs, message.sendArgs) &&
                Objects.equal(targetUserTenantId, message.targetUserTenantId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(messageId, tenantId, messageTypeCode, lang, templateCode, serverCode, subject, content,
                plainContent, sendFlag, transactionId, trxStatusCode, sendDate, externalCode, serverTypeCode,
                receiverList, sendArgs, targetUserTenantId);
    }

}
