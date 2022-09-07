package org.hzero.iam.api.dto;

import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * 安全组APIDTO
 *
 * @author xingxingwu.hand-china.com 2019/10/28 10:24
 */
public class SecGrpAclApiDTO {
    /**
     * 租户ID
     */
	private Long tenantId;
	/**
	 * 服务名称
	 */
	private String serviceName;
	/**
	 * 请求方式
	 */
	private String method;
	/**
	 * 路径
	 */
	private String path;
	/**
	 * 请求描述
	 */
	private String description;
    /**
     * 查询所有接口OR查询分配了字段权限的接口
     */
    private Boolean includeAll;
    /**
     * 安全组ID
     */
    @Encrypt
    private Long secGrpId;

	private String secGrpSource;


	public Long getTenantId() {
		return tenantId;
	}

	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getIncludeAll() {
		return includeAll;
	}

	public void setIncludeAll(Boolean includeAll) {
		this.includeAll = includeAll;
	}

	public Long getSecGrpId() {
		return secGrpId;
	}

	public void setSecGrpId(Long secGrpId) {
		this.secGrpId = secGrpId;
	}

	public String getSecGrpSource() {
		return secGrpSource;
	}

	public void setSecGrpSource(String secGrpSource) {
		this.secGrpSource = secGrpSource;
	}
}
