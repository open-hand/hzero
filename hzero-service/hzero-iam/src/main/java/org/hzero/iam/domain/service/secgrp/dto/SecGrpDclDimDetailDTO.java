package org.hzero.iam.domain.service.secgrp.dto;

/**
 * 安全组权限维度明细DTO
 *
 * @author xingxingwu.hand-china.com 2019/11/07 10:17
 */
public class SecGrpDclDimDetailDTO {
	/**
	 * 安全组数据维度行ID
	 */
	private Long secGrpDclDimLineId;
	/**
	 * 安全组数据维度ID
	 */
	private Long secGrpDclDimId;
	/**
	 * 安全组ID
	 */
	private Long secGrpId;
	/**
	 * 租户ID
	 */
	private Long tenantId;
	/**
	 * 权限单据类型ID
	 */
	private Long authDocTypeId;
	/**
	 * 权限单据范围代码
	 */
    private String authScopeCode;
    /**
     * 权限类型代码
     */
    private String authTypeCode;
    /**
     * 分配类型
     */
    private String assignTypeCode;
    /**
     * 自动分配ID
     */
    private Long autoAssignRoleId;
    /**
     * 创建角色ID
     */
    private Long roleId;
    /**
     * 创建角色父角色ID
     */
	private Long parentRoleId;


	public String buildUniqueKey(){
        return String.format("%s-%s-%s", this.authDocTypeId, this.authScopeCode, this.authTypeCode);
    }

	public String buildSecGrpDclDimKey(){
		return this.authDocTypeId+authTypeCode;
	}

	public Long getAuthDocTypeId() {
		return authDocTypeId;
	}

	public SecGrpDclDimDetailDTO setAuthDocTypeId(Long authDocTypeId) {
		this.authDocTypeId = authDocTypeId;
		return this;
	}

	public String getAuthScopeCode() {
		return authScopeCode;
	}

	public SecGrpDclDimDetailDTO setAuthScopeCode(String authScopeCode) {
		this.authScopeCode = authScopeCode;
		return this;
	}

	public String getAuthTypeCode() {
		return authTypeCode;
	}

	public SecGrpDclDimDetailDTO setAuthTypeCode(String authTypeCode) {
		this.authTypeCode = authTypeCode;
        return this;
    }

    public String getAssignTypeCode() {
        return assignTypeCode;
    }

    public void setAssignTypeCode(String assignTypeCode) {
        this.assignTypeCode = assignTypeCode;
    }

    public Long getAutoAssignRoleId() {
        return autoAssignRoleId;
    }

    public void setAutoAssignRoleId(Long autoAssignRoleId) {
        this.autoAssignRoleId = autoAssignRoleId;
	}

	public Long getSecGrpId() {
		return secGrpId;
	}

	public void setSecGrpId(Long secGrpId) {
		this.secGrpId = secGrpId;
	}

	public Long getSecGrpDclDimLineId() {
		return secGrpDclDimLineId;
	}

	public void setSecGrpDclDimLineId(Long secGrpDclDimLineId) {
		this.secGrpDclDimLineId = secGrpDclDimLineId;
	}

	public Long getSecGrpDclDimId() {
		return secGrpDclDimId;
	}

	public void setSecGrpDclDimId(Long secGrpDclDimId) {
		this.secGrpDclDimId = secGrpDclDimId;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public Long getParentRoleId() {
		return parentRoleId;
	}

	public void setParentRoleId(Long parentRoleId) {
		this.parentRoleId = parentRoleId;
	}

	public Long getTenantId() {
		return tenantId;
	}

	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
	}
}
