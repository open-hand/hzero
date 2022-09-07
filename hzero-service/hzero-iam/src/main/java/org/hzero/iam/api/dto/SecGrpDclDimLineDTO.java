package org.hzero.iam.api.dto;

import org.hzero.boot.platform.lov.annotation.LovValue;

/**
 * 安全组数据权限行DTO
 *
 * @author xingxingwu.hand-china.com 2019/10/20 12:27
 */
public class SecGrpDclDimLineDTO {
	private Long secGrpId;
	private Long secGrpDclDimId;
	private Long tenantId;

	private Long docTypeId;
	private Long secGrpDclDimLineId;
	private String authTypeCode;
	private String dimensionType;
	private Integer secGrpDclDimLineCheckedFlag;
	private String authTypeMeaning;
	private Integer deleteEnableFlag;

	public Long getSecGrpDclDimLineId() {
		return secGrpDclDimLineId;
	}

	public void setSecGrpDclDimLineId(Long secGrpDclDimLineId) {
		this.secGrpDclDimLineId = secGrpDclDimLineId;
	}

	public String getAuthTypeCode() {
		return authTypeCode;
	}

	public void setAuthTypeCode(String authTypeCode) {
		this.authTypeCode = authTypeCode;
	}

	public String getDimensionType() {
		return dimensionType;
	}

	public void setDimensionType(String dimensionType) {
		this.dimensionType = dimensionType;
	}

	public Integer getSecGrpDclDimLineCheckedFlag() {
		return secGrpDclDimLineCheckedFlag;
	}

	public void setSecGrpDclDimLineCheckedFlag(Integer secGrpDclDimLineCheckedFlag) {
		this.secGrpDclDimLineCheckedFlag = secGrpDclDimLineCheckedFlag;
	}

	public String getAuthTypeMeaning() {
		return authTypeMeaning;
	}

	public void setAuthTypeMeaning(String authTypeMeaning) {
		this.authTypeMeaning = authTypeMeaning;
	}

	public Long getSecGrpId() {
		return secGrpId;
	}

	public void setSecGrpId(Long secGrpId) {
		this.secGrpId = secGrpId;
	}

	public Long getSecGrpDclDimId() {
		return secGrpDclDimId;
	}

	public void setSecGrpDclDimId(Long secGrpDclDimId) {
		this.secGrpDclDimId = secGrpDclDimId;
	}

	public Long getTenantId() {
		return tenantId;
	}

	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
	}

	public Long getDocTypeId() {
		return docTypeId;
	}

	public void setDocTypeId(Long docTypeId) {
		this.docTypeId = docTypeId;
	}

	public Integer getDeleteEnableFlag() {
		return deleteEnableFlag;
	}

	public void setDeleteEnableFlag(Integer deleteEnableFlag) {
		this.deleteEnableFlag = deleteEnableFlag;
	}
}
