package org.hzero.message.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Range;
import org.hzero.boot.message.entity.Attachment;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.core.util.Regexs;
import org.hzero.starter.keyencrypt.core.Encrypt;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * 消息信息
 *
 * @author xianzhi.chen@hand-china.com 2018-07-27 11:30:58
 */
@VersionAudit
@ModifyAudit
@Table(name = "hmsg_message")
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel("消息信息")
public class Message extends AuditDomain {

    public static final String FIELD_MESSAGE_ID = "messageId";
    public static final String FIELD_MESSAGE_TYPE_CODE = "messageTypeCode";
    public static final String FIELD_SERVER_CODE = "serverCode";
    public static final String FIELD_SUBJECT = "subject";
    public static final String FIELD_CONTENT = "content";
    public static final String FIELD_SEND_FLAG = "sendFlag";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_SEND_ARGS = "sendArgs";



    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @Id
    @GeneratedValue
    @ApiModelProperty("消息ID")
    @Encrypt
    private Long messageId;
    @NotNull
    @ApiModelProperty("租户ID")
    private Long tenantId;
    @NotBlank
    @Size(max = 30)
    @LovValue(lovCode = "HMSG.MESSAGE_TYPE")
    @ApiModelProperty("消息类型编码，值集：HMSG.MESSAGE_TYPE")
    private String messageTypeCode;
    @NotBlank
    @Size(max = 60)
    @ApiModelProperty("消息模板编码")
    @Pattern(regexp = Regexs.CODE_UPPER)
    private String templateCode;
    @Size(max = 30)
    @ApiModelProperty("语言")
    private String lang;
    @Size(max = 30)
    @ApiModelProperty("服务账户编码")
    @Pattern(regexp = Regexs.CODE_UPPER)
    private String serverCode;
    @NotBlank
    @Size(max = 240)
    @ApiModelProperty("消息主题")
    private String subject;
    @NotBlank
    @ApiModelProperty("消息内容")
    private String content;
    @Range(min = 0, max = 1)
    @ApiModelProperty("发送标记")
    private Integer sendFlag;
    @ApiModelProperty(value = "发送参数", hidden = true)
    @JsonIgnore
    private String sendArgs;
    @ApiModelProperty("外部代码")
    private String externalCode;
    //
    // 非数据库字段
    // ------------------------------------------------------------------------------
    /**
     * 消息类型 meaning
     */
    @Transient
    @ApiModelProperty("消息类型 meaning")
    private String messageTypeMeaning;
    /**
     * 消息发送状态
     */
    @Transient
    @LovValue(lovCode = "HMSG.TRANSACTION_STATUS")
    @ApiModelProperty("消息发送状态，值集：HMSG.TRANSACTION_STATUS")
    private String trxStatusCode;
    @Transient
    @ApiModelProperty("息发送状态 meaning")
    private String trxStatusMeaning;
    @Transient
    @ApiModelProperty("消息事务ID")
    @Encrypt
    private Long transactionId;
    /**
     * 消息发送时间
     */
    @Transient
    @ApiModelProperty("消息发送时间")
    private Date sendDate;
    @Transient
    @ApiModelProperty("租户名称")
    private String tenantName;

    @Transient
    private List<MessageReceiver> messageReceiverList;

    @Transient
    private List<Attachment> attachmentList;

    @Transient
    private List<String> ccList;

    @Transient
    private List<String> bccList;
    @Transient
    private String plainContent;

    @Transient
    private String templateEditType;
    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 表ID，主键，供其他表做外键
     */
    public Long getMessageId() {
        return messageId;
    }

    public Message setMessageId(Long messageId) {
        this.messageId = messageId;
        return this;
    }

    /**
     * @return 消息类型，值集:HMSG_MESSAGE_TYPE M:邮箱 S:短信 W:站内
     */
    public String getMessageTypeCode() {
        return messageTypeCode;
    }

    public Message setMessageTypeCode(String messageTypeCode) {
        this.messageTypeCode = messageTypeCode;
        return this;
    }

    /**
     * @return 模板编码
     */
    public String getTemplateCode() {
        return templateCode;
    }

    public Message setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
        return this;
    }

    /**
     * @return 消息标题
     */
    public String getSubject() {
        return subject;
    }

    public Message setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    /**
     * @return 消息内容
     */
    public String getContent() {
        return content;
    }

    public Message setContent(String content) {
        this.content = content;
        return this;
    }

    /**
     * @return 发送标记
     */
    public Integer getSendFlag() {
        return sendFlag;
    }

    public Message setSendFlag(Integer sendFlag) {
        this.sendFlag = sendFlag;
        return this;
    }

    public String getSendArgs() {
        return sendArgs;
    }

    public Message setSendArgs(String sendArgs) {
        this.sendArgs = sendArgs;
        return this;
    }

    /**
     * @return 租户ID
     */
    public Long getTenantId() {
        return tenantId;
    }

    public Message setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    /**
     * @return 账户代码
     */
    public String getServerCode() {
        return serverCode;
    }

    public Message setServerCode(String serverCode) {
        this.serverCode = serverCode;
        return this;
    }

    public String getMessageTypeMeaning() {
        return messageTypeMeaning;
    }

    public Message setMessageTypeMeaning(String messageTypeMeaning) {
        this.messageTypeMeaning = messageTypeMeaning;
        return this;
    }

    public String getTrxStatusCode() {
        return trxStatusCode;
    }

    public Message setTrxStatusCode(String trxStatusCode) {
        this.trxStatusCode = trxStatusCode;
        return this;
    }

    public String getTrxStatusMeaning() {
        return trxStatusMeaning;
    }

    public Message setTrxStatusMeaning(String trxStatusMeaning) {
        this.trxStatusMeaning = trxStatusMeaning;
        return this;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public Message setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public Message setSendDate(Date sendDate) {
        this.sendDate = sendDate;
        return this;
    }

    public String getLang() {
        return lang;
    }

    public Message setLang(String lang) {
        this.lang = lang;
        return this;
    }

    public String getTenantName() {
        return tenantName;
    }

    public Message setTenantName(String tenantName) {
        this.tenantName = tenantName;
        return this;
    }

    public List<Attachment> getAttachmentList() {
        return attachmentList;
    }

    public Message setAttachmentList(List<Attachment> attachmentList) {
        this.attachmentList = attachmentList;
        return this;
    }

    public List<String> getCcList() {
        return ccList;
    }

    public Message setCcList(List<String> ccList) {
        this.ccList = ccList;
        return this;
    }

    public List<String> getBccList() {
        return bccList;
    }

    public Message setBccList(List<String> bccList) {
        this.bccList = bccList;
        return this;
    }

    @JsonIgnore
    @Override
    public Long getCreatedBy() {
        return super.getCreatedBy();
    }

    @JsonIgnore
    @Override
    public Date getLastUpdateDate() {
        return super.getLastUpdateDate();
    }

    @JsonIgnore
    @Override
    public Long getLastUpdatedBy() {
        return super.getLastUpdatedBy();
    }

    public String getExternalCode() {
        return externalCode;
    }

    public Message setExternalCode(String externalCode) {
        this.externalCode = externalCode;
        return this;
    }

    public List<MessageReceiver> getMessageReceiverList() {
        return messageReceiverList;
    }

    public Message setMessageReceiverList(List<MessageReceiver> messageReceiverList) {
        this.messageReceiverList = messageReceiverList;
        return this;
    }

    public String getPlainContent() {
        return plainContent;
    }

    public Message setPlainContent(String plainContent) {
        this.plainContent = plainContent;
        return this;
    }

    public String getTemplateEditType() {
        return templateEditType;
    }

    public Message setTemplateEditType(String templateEditType) {
        this.templateEditType = templateEditType;
        return this;
    }
}
