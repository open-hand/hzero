package org.hzero.message.domain.entity;

import io.swagger.annotations.ApiModel;
import org.hzero.starter.keyencrypt.core.Encrypt;

import java.util.Objects;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * 接收者类型用户组
 *
 * @author minghui.qiu@hand-china.com 2019-06-12 09:03:01
 */
@ApiModel("接收者类型用户组")
@VersionAudit
@ModifyAudit
@Table(name = "hmsg_receiver_type_line")
public class ReceiverTypeLine extends AuditDomain {

	public static final String FIELD_RECEIVER_USER_GROUP_ID = "receiverTypeLineId";
	public static final String FIELD_RECEIVER_TYPE_ID = "receiverTypeId";
	public static final String FIELD_USER_GROUP_ID = "receiveTargetId";
	public static final String FIELD_RECEIVER_DETAIL = "receiverDetail";

	//
	// 业务方法(按public protected private顺序排列)
	// ------------------------------------------------------------------------------

	//
	// 数据库字段
	// ------------------------------------------------------------------------------

	@Id
	@GeneratedValue
	@Encrypt
	private Long receiverTypeLineId;
	@Encrypt
	@NotNull
	private Long receiverTypeId;
	@NotNull
	@Encrypt
	private Long receiveTargetId;
	private Long receiveTargetTenantId;
	//
	// 非数据库字段
	// ------------------------------------------------------------------------------
	@Transient
	private String receiveTargetCode;
	@Transient
	private String receiveTargetName;
	@Transient
	private ReceiverDetail receiverDetail;
	//
	// getter/setter
	// ------------------------------------------------------------------------------

	/**
	 * @return 接收组类型ID
	 */
	public Long getReceiverTypeId() {
		return receiverTypeId;
	}

	public ReceiverTypeLine setReceiverTypeId(Long receiverTypeId) {
		this.receiverTypeId = receiverTypeId;
		return this;
	}

	
	public Long getReceiverTypeLineId() {
		return receiverTypeLineId;
	}

	public void setReceiverTypeLineId(Long receiverTypeLineId) {
		this.receiverTypeLineId = receiverTypeLineId;
	}

	public Long getReceiveTargetId() {
		return receiveTargetId;
	}

	public ReceiverTypeLine setReceiveTargetId(Long receiveTargetId) {
		this.receiveTargetId = receiveTargetId;
		return this;
	}

	public String getReceiveTargetCode() {
		return receiveTargetCode;
	}

	public void setReceiveTargetCode(String receiveTargetCode) {
		this.receiveTargetCode = receiveTargetCode;
	}

	public String getReceiveTargetName() {
		return receiveTargetName;
	}

	public void setReceiveTargetName(String receiveTargetName) {
		this.receiveTargetName = receiveTargetName;
	}

	public Long getReceiveTargetTenantId() {
		return receiveTargetTenantId;
	}

	public void setReceiveTargetTenantId(Long receiveTargetTenantId) {
		this.receiveTargetTenantId = receiveTargetTenantId;
	}

	public ReceiverDetail getReceiverDetail() {
		return receiverDetail;
	}

	public ReceiverTypeLine setReceiverDetail(ReceiverDetail receiverDetail) {
		this.receiverDetail = receiverDetail;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof ReceiverTypeLine)) {
			return false;
		}
		ReceiverTypeLine that = (ReceiverTypeLine) o;
		return Objects.equals(receiverTypeLineId, that.receiverTypeLineId) &&
				Objects.equals(receiverTypeId, that.receiverTypeId) &&
				Objects.equals(receiveTargetId, that.receiveTargetId) &&
				Objects.equals(receiveTargetTenantId, that.receiveTargetTenantId) &&
				Objects.equals(receiveTargetCode, that.receiveTargetCode) &&
				Objects.equals(receiveTargetName, that.receiveTargetName) &&
				Objects.equals(receiverDetail, that.receiverDetail);
	}

	@Override
	public int hashCode() {
		return Objects.hash(receiverTypeLineId, receiverTypeId, receiveTargetId, receiveTargetTenantId, receiveTargetCode, receiveTargetName);
	}
}
