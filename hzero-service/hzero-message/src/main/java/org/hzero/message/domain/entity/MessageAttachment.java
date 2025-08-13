package org.hzero.message.domain.entity;

import org.hzero.starter.keyencrypt.core.Encrypt;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * 消息附件
 *
 * @author xianzhi.chen@hand-china.com 2018-07-27 11:30:58
 */
@VersionAudit
@ModifyAudit
@Table(name = "hmsg_message_attachment")
public class MessageAttachment extends AuditDomain {

    public static final String FIELD_ATTACHMENT_ID = "attachmentId";
    public static final String FIELD_MESSAGE_ID = "messageId";
    public static final String FIELD_BUCKET_NAME = "bucketName";
    public static final String FIELD_FILE_URL = "fileUrl";
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
    private Long attachmentId;
    @NotNull
	@Encrypt
	private Long messageId;
    @NotBlank
    private String bucketName;
    @NotBlank
    private String fileUrl;
    @NotNull
    private Long tenantId;

	//
    // 非数据库字段
    // ------------------------------------------------------------------------------

    //
    // getter/setter
    // ------------------------------------------------------------------------------


	public Long getAttachmentId() {
		return attachmentId;
	}

	public MessageAttachment setAttachmentId(Long attachmentId) {
		this.attachmentId = attachmentId;
		return this;
	}

	public Long getMessageId() {
		return messageId;
	}

	public MessageAttachment setMessageId(Long messageId) {
		this.messageId = messageId;
		return this;
	}

	public String getBucketName() {
		return bucketName;
	}

	public MessageAttachment setBucketName(String bucketName) {
		this.bucketName = bucketName;
		return this;
	}

	public String getFileUrl() {
		return fileUrl;
	}

	public MessageAttachment setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
		return this;
	}

	public Long getTenantId() {
		return tenantId;
	}

	public MessageAttachment setTenantId(Long tenantId) {
		this.tenantId = tenantId;
		return this;
	}
}
