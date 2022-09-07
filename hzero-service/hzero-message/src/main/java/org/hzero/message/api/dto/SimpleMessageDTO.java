package org.hzero.message.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.starter.keyencrypt.core.Encrypt;

import java.util.Date;

/**
 * <p>
 * 消息预览DTO
 * </p>
 *
 * @author qingsheng.chen 2018/8/13 星期一 10:00
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel("消息预览")
public class SimpleMessageDTO {
    @Encrypt
	@ApiModelProperty("用户消息ID")
	private Long userMessageId;
    @ApiModelProperty("消息ID")
    @Encrypt
    private Long messageId;
    @ApiModelProperty("公告ID")
    @Encrypt
    private Long noticeId;
    @ApiModelProperty("租户ID")
    private Long tenantId;
    @ApiModelProperty("租户名称")
    private String tenantName;
    @ApiModelProperty("消息主题")
    private String subject;
    @ApiModelProperty("消息日期")
    private Date creationDate;
    @ApiModelProperty("消息内容")
    private String content;
    @LovValue(lovCode = "HMSG.USER.MESSAGE_TYPE")
    @ApiModelProperty("用户消息类型编码")
    private String userMessageTypeCode;
    @ApiModelProperty("用户消息类型含义")
    private String userMessageTypeMeaning;

    public Long getMessageId() {
        return messageId;
    }

    public SimpleMessageDTO setMessageId(Long messageId) {
        this.messageId = messageId;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public SimpleMessageDTO setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getTenantName() {
        return tenantName;
    }

    public SimpleMessageDTO setTenantName(String tenantName) {
        this.tenantName = tenantName;
        return this;
    }

    public String getSubject() {
        return subject;
    }

    public SimpleMessageDTO setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public SimpleMessageDTO setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
        return this;
    }

    public String getContent() {
        return content;
    }

    public SimpleMessageDTO setContent(String content) {
        this.content = content;
        return this;
    }

    @Override
    public String toString() {
        return "SimpleMessageDTO{" +
                "messageId=" + messageId +
                ", messageType=" + userMessageTypeMeaning +
                ", tenantId=" + tenantId +
                ", tenantName='" + tenantName + '\'' +
                ", subject='" + subject + '\'' +
                ", creationDate=" + creationDate +
                ", content='" + content + '\'' +
                '}';
    }

	public String getUserMessageTypeCode() {
		return userMessageTypeCode;
	}

	public SimpleMessageDTO setUserMessageTypeCode(String userMessageTypeCode) {
		this.userMessageTypeCode = userMessageTypeCode;
		return this;
	}

	public String getUserMessageTypeMeaning() {
		return userMessageTypeMeaning;
	}

	public SimpleMessageDTO setUserMessageTypeMeaning(String userMessageTypeMeaning) {
		this.userMessageTypeMeaning = userMessageTypeMeaning;
		return this;
	}

	public Long getUserMessageId() {
		return userMessageId;
	}

	public SimpleMessageDTO setUserMessageId(Long userMessageId) {
		this.userMessageId = userMessageId;
		return this;
	}

	public Long getNoticeId() {
		return noticeId;
	}

	public SimpleMessageDTO setNoticeId(Long noticeId) {
		this.noticeId = noticeId;
		return this;
	}
}
