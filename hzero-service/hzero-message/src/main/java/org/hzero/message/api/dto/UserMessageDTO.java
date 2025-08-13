package org.hzero.message.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.boot.message.entity.Receiver;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.message.domain.entity.Message;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户消息
 * </p>
 *
 * @author qingsheng.chen 2018/8/1 星期三 17:43
 */
@ApiModel("用户消息信息")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UserMessageDTO extends Message {
    @Encrypt
    @ApiModelProperty("用户消息ID")
    private Long userMessageId;
    @ApiModelProperty("已读标记[0(未读),1(已读)]")
    private Integer readFlag;
    @LovValue(lovCode = "HMSG.USER.MESSAGE_TYPE")
    @ApiModelProperty("用户消息类型编码")
    private String userMessageTypeCode;
    @ApiModelProperty("用户消息类型含义")
    private String userMessageTypeMeaning;
    @LovValue(lovCode = "HMSG.MESSAGE_CATEGORY")
    @ApiModelProperty("消息模板类型编码")
    private String messageCategoryCode;
    @ApiModelProperty("消息模板类型含义")
    private String messageCategoryMeaning;
    @LovValue(lovCode = "HMSG.MESSAGE_SUBCATEGORY")
    @ApiModelProperty("消息模板子类型编码")
    private String messageSubcategoryCode;
    @ApiModelProperty("消息模板子类型含义")
    private String messageSubcategoryMeaning;
    @ApiModelProperty("消息模板替换参数")
    private Map<String, Object> args;
    @ApiModelProperty("消息接收者地址列表[邮箱/手机号/用户ID]")
    private List<Receiver> receiverAddressList;
    private String messageCode;
    private Long transactionObjectVersionNumber;
    private Long fromTenantId;
    private String attachmentUuid;

    @JsonIgnore
    public Message getMessage() {
        Message message = new Message();
        BeanUtils.copyProperties(this, message);
        return message;
    }

    public Long getUserMessageId() {
        return userMessageId;
    }

    public UserMessageDTO setUserMessageId(Long userMessageId) {
        this.userMessageId = userMessageId;
        return this;
    }

    public Integer getReadFlag() {
        return readFlag;
    }

    public UserMessageDTO setReadFlag(Integer readFlag) {
        this.readFlag = readFlag;
        return this;
    }

    public String getMessageCategoryCode() {
        return messageCategoryCode;
    }

    public UserMessageDTO setMessageCategoryCode(String messageCategoryCode) {
        this.messageCategoryCode = messageCategoryCode;
        return this;
    }

    public String getMessageCategoryMeaning() {
        return messageCategoryMeaning;
    }

    public UserMessageDTO setMessageCategoryMeaning(String messageCategoryMeaning) {
        this.messageCategoryMeaning = messageCategoryMeaning;
        return this;
    }

    public String getMessageSubcategoryCode() {
        return messageSubcategoryCode;
    }

    public UserMessageDTO setMessageSubcategoryCode(String messageSubcategoryCode) {
        this.messageSubcategoryCode = messageSubcategoryCode;
        return this;
    }

    public String getMessageSubcategoryMeaning() {
        return messageSubcategoryMeaning;
    }

    public UserMessageDTO setMessageSubcategoryMeaning(String messageSubcategoryMeaning) {
        this.messageSubcategoryMeaning = messageSubcategoryMeaning;
        return this;
    }

    public Map<String, Object> getArgs() {
        return args;
    }

    public UserMessageDTO setArgs(Map<String, Object> args) {
        this.args = args;
        return this;
    }

    public List<Receiver> getReceiverAddressList() {
        return receiverAddressList;
    }

    public UserMessageDTO setReceiverAddressList(List<Receiver> receiverAddressList) {
        this.receiverAddressList = receiverAddressList;
        return this;
    }

    public String getMessageCode() {
        return messageCode;
    }

    public UserMessageDTO setMessageCode(String messageCode) {
        this.messageCode = messageCode;
        return this;
    }

    public Long getTransactionObjectVersionNumber() {
        return transactionObjectVersionNumber;
    }

    public UserMessageDTO setTransactionObjectVersionNumber(Long transactionObjectVersionNumber) {
        this.transactionObjectVersionNumber = transactionObjectVersionNumber;
        return this;
    }

    @Override
    public String toString() {
        return "UserMessageDTO{" +
                "userMessageId=" + userMessageId +
                ", readFlag=" + readFlag +
                ", userMessageTypeCode='" + userMessageTypeCode + '\'' +
                ", userMessageTypeMeaning='" + userMessageTypeMeaning + '\'' +
                ", messageCategoryCode='" + messageCategoryCode + '\'' +
                ", messageCategoryMeaning='" + messageCategoryMeaning + '\'' +
                ", messageSubcategoryCode='" + messageSubcategoryCode + '\'' +
                ", messageSubcategoryMeaning='" + messageSubcategoryMeaning + '\'' +
                ", args=" + args +
                ", receiverAddressList=" + receiverAddressList +
                ", messageCode='" + messageCode + '\'' +
                ", transactionObjectVersionNumber=" + transactionObjectVersionNumber +
                ", fromTenantId=" + fromTenantId +
                ", attachmentUuid='" + attachmentUuid + '\'' +
                '}';
    }

    public String getUserMessageTypeMeaning() {
        return userMessageTypeMeaning;
    }

    public UserMessageDTO setUserMessageTypeMeaning(String userMessageTypeMeaning) {
        this.userMessageTypeMeaning = userMessageTypeMeaning;
        return this;
    }

    public String getUserMessageTypeCode() {
        return userMessageTypeCode;
    }

    public UserMessageDTO setUserMessageTypeCode(String userMessageTypeCode) {
        this.userMessageTypeCode = userMessageTypeCode;
        return this;
    }

    public Long getFromTenantId() {
        return fromTenantId;
    }

    public UserMessageDTO setFromTenantId(Long fromTenantId) {
        this.fromTenantId = fromTenantId;
        return this;
    }

	public String getAttachmentUuid() {
		return attachmentUuid;
	}

	public void setAttachmentUuid(String attachmentUuid) {
		this.attachmentUuid = attachmentUuid;
	}
}
