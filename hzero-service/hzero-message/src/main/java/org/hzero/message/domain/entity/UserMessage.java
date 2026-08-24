package org.hzero.message.domain.entity;

import io.swagger.annotations.ApiModelProperty;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.starter.keyencrypt.core.Encrypt;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * 用户消息
 *
 * @author xianzhi.chen@hand-china.com 2018-07-27 11:30:58
 */
@VersionAudit
@ModifyAudit
@Table(name = "hmsg_user_message")
public class UserMessage extends AuditDomain {

    public static final String FIELD_USER_MESSAGE_ID = "userMessageId";
    public static final String FIELD_USER_ID = "userId";
    public static final String FIELD_MESSAGE_ID = "messageId";
    public static final String FIELD_READ_FLAG = "readFlag";
    public static final String FIELD_TENANT_ID = "tenantId";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @Id
    @GeneratedValue
	@Encrypt
    private Long userMessageId;
    @NotNull
    private Long userId;
    @NotNull
    private Long messageId;
    @NotNull
    private Integer readFlag;
    @NotNull
    private Long tenantId;
    private Long fromTenantId;
    @LovValue(lovCode = "HMSG.USER.MESSAGE_TYPE")
    @ApiModelProperty("用户消息类型编码")
    private String userMessageTypeCode;
    
	//
    // 非数据库字段
    // ------------------------------------------------------------------------------
    @Transient
    @ApiModelProperty("用户消息类型含义")
    private String userMessageTypeMeaning;
    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 表ID，主键，供其他表做外键
     */
	public Long getUserMessageId() {
		return userMessageId;
	}

	public UserMessage setUserMessageId(Long userMessageId) {
		this.userMessageId = userMessageId;
		return this;
	}

	/**
     * @return 用户ID
     */
	public Long getUserId() {
		return userId;
	}

	public UserMessage setUserId(Long userId) {
		this.userId = userId;
		return this;
	}

	/**
     * @return 消息ID，hmsg_message.message_id
     */
	public Long getMessageId() {
		return messageId;
	}

	public UserMessage setMessageId(Long messageId) {
		this.messageId = messageId;
		return this;
	}

	/**
     * @return 已读标识
     */
	public Integer getReadFlag() {
		return readFlag;
	}

	public UserMessage setReadFlag(Integer readFlag) {
		this.readFlag = readFlag;
		return this;
	}

	/**
     * @return 租户ID
     */
	public Long getTenantId() {
		return tenantId;
	}

	public UserMessage setTenantId(Long tenantId) {
		this.tenantId = tenantId;
		return this;
	}

	public Long getFromTenantId() {
		return fromTenantId;
	}

	/**
	 * @return 来源租户ID
	 */
	public UserMessage setFromTenantId(Long fromTenantId) {
		this.fromTenantId = fromTenantId;
		return this;
	}

	public String getUserMessageTypeCode() {
		return userMessageTypeCode;
	}

	public UserMessage setUserMessageTypeCode(String userMessageTypeCode) {
		this.userMessageTypeCode = userMessageTypeCode;
		return this;
	}

	public String getUserMessageTypeMeaning() {
		return userMessageTypeMeaning;
	}

	public UserMessage setUserMessageTypeMeaning(String userMessageTypeMeaning) {
		this.userMessageTypeMeaning = userMessageTypeMeaning;
		return this;
	}
}
