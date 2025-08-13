package org.hzero.message.api.dto;


import java.util.Date;

import io.swagger.annotations.ApiModelProperty;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2019/10/29 10:12
 */
public class UserMsgParamDTO {

    @ApiModelProperty("租户")
    private Long tenantId;
    @ApiModelProperty("用户")
    private Long userId;
    @ApiModelProperty("消息类型编码")
    private String messageTypeCode;
    @ApiModelProperty("已读/未读标记")
    private Integer readFlag;
    @ApiModelProperty("消息类别")
    private String messageCategoryCode;
    @ApiModelProperty("消息子类别")
    private String messageSubcategoryCode;
    @ApiModelProperty("用户消息类型")
    private String userMessageTypeCode;
    @ApiModelProperty("是否包含消息内容")
    private Boolean withContent;
    @ApiModelProperty("创建时间从")
    private Date fromDate;
    @ApiModelProperty("创建时间至")
    private Date toDate;
    @ApiModelProperty("消息标题")
    private String subject;

    public Long getTenantId() {
        return tenantId;
    }

    public UserMsgParamDTO setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public UserMsgParamDTO setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public String getMessageTypeCode() {
        return messageTypeCode;
    }

    public UserMsgParamDTO setMessageTypeCode(String messageTypeCode) {
        this.messageTypeCode = messageTypeCode;
        return this;
    }

    public Integer getReadFlag() {
        return readFlag;
    }

    public UserMsgParamDTO setReadFlag(Integer readFlag) {
        this.readFlag = readFlag;
        return this;
    }

    public String getMessageCategoryCode() {
        return messageCategoryCode;
    }

    public UserMsgParamDTO setMessageCategoryCode(String messageCategoryCode) {
        this.messageCategoryCode = messageCategoryCode;
        return this;
    }

    public String getMessageSubcategoryCode() {
        return messageSubcategoryCode;
    }

    public UserMsgParamDTO setMessageSubcategoryCode(String messageSubcategoryCode) {
        this.messageSubcategoryCode = messageSubcategoryCode;
        return this;
    }

    public String getUserMessageTypeCode() {
        return userMessageTypeCode;
    }

    public UserMsgParamDTO setUserMessageTypeCode(String userMessageTypeCode) {
        this.userMessageTypeCode = userMessageTypeCode;
        return this;
    }

    public Boolean getWithContent() {
        return withContent;
    }

    public UserMsgParamDTO setWithContent(Boolean withContent) {
        this.withContent = withContent;
        return this;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public UserMsgParamDTO setFromDate(Date fromDate) {
        this.fromDate = fromDate;
        return this;
    }

    public Date getToDate() {
        return toDate;
    }

    public UserMsgParamDTO setToDate(Date toDate) {
        this.toDate = toDate;
        return this;
    }

    public String getSubject() {
        return subject;
    }

    public UserMsgParamDTO setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    @Override
    public String toString() {
        return "UserMsgParamDTO{" +
                "tenantId=" + tenantId +
                ", userId=" + userId +
                ", messageTypeCode='" + messageTypeCode + '\'' +
                ", readFlag=" + readFlag +
                ", messageCategoryCode='" + messageCategoryCode + '\'' +
                ", messageSubcategoryCode='" + messageSubcategoryCode + '\'' +
                ", userMessageTypeCode='" + userMessageTypeCode + '\'' +
                ", withContent=" + withContent +
                ", fromDate=" + fromDate +
                ", toDate=" + toDate +
                ", subject='" + subject + '\'' +
                '}';
    }
}
