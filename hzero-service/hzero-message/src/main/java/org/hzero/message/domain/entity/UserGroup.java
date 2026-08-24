package org.hzero.message.domain.entity;

import org.hzero.starter.keyencrypt.core.Encrypt;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

public class UserGroup {

	public static final String FIELD_USER_GROUP_ID = "userGroupId";

	@Id
	@GeneratedValue
	@Encrypt
	private Long userGroupId;

	private Long tenantId;

	private String groupCode;
	private String groupName;

	//
	// getter/setter
	// ------------------------------------------------------------------------------

	/**
	 * @return 租户ID，hpfm_tenant.tenant_id
	 */
	public Long getTenantId() {
		return tenantId;
	}

	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
	}

	public Long getUserGroupId() {
		return userGroupId;
	}

	public void setUserGroupId(Long userGroupId) {
		this.userGroupId = userGroupId;
	}

	public String getGroupCode() {
		return groupCode;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

}
