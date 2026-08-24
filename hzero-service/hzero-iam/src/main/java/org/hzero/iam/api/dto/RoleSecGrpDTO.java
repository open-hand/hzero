package org.hzero.iam.api.dto;

import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * 角色查询DTO
 *
 * @author xingxingwu.hand-china.com 2019/12/10 9:54
 */
public class RoleSecGrpDTO {
    /**
     * 角色Id
     */
    @Encrypt
    private Long id;
    /**
     * 限制参数：角色代码
     */
    private String code;
	/**
	 * 角色名称
	 */
	private String name;
	/**
	 * 角色所属租户名称
	 */
	private String tenantName;

    /**
     * 父级角色ID
     */
    @Encrypt
    private Long parentRoleId;
    /**
     * 父级角色名称
     */
    private String parentRoleName;

	/**
	 * 角色所属的租户Id
	 */
	private Long tenantId;

	public Long getTenantId() {
		return tenantId;
	}

	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Long getParentRoleId() {
		return parentRoleId;
	}

	public void setParentRoleId(Long parentRoleId) {
		this.parentRoleId = parentRoleId;
	}

	public String getParentRoleName() {
		return parentRoleName;
	}

	public void setParentRoleName(String parentRoleName) {
		this.parentRoleName = parentRoleName;
	}

	public String getTenantName() {
		return tenantName;
	}

	public void setTenantName(String tenantName) {
		this.tenantName = tenantName;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
