package org.hzero.iam.api.dto;

import java.util.List;

import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.starter.keyencrypt.core.Encrypt;

import io.swagger.annotations.ApiModelProperty;

/**
 * 安全组数据权限DTO
 *
 * @author xingxingwu.hand-china.com 2019/10/20 12:07
 */
public class SecGrpDclDimDTO {

	//单据类型字段
	@Encrypt
	private Long docTypeId;
	private String docTypeCode;
	private String docTypeName;
	@LovValue(lovCode = "HIAM.AUTHORITY_SCOPE_CODE",meaningField = "authScopeMeaning")
	private String authScopeCode;
	private String authScopeMeaning;


	//安全组数据权限维度字段
	@ApiModelProperty("表ID，主键，供其他表做外键")
	@Encrypt
	private Long secGrpDclDimId;
    @ApiModelProperty(value = "安全组ID")
    @Encrypt
    private Long secGrpId;
	@ApiModelProperty(value = "租户")
	private Long tenantId;
	private Long objectVersionNumber;
	private Integer secGrpDclDimCheckedFlag;
	private Integer editEnableFlag;

	//权限维度行
	private List<SecGrpDclDimLineDTO> secGrpDclDimLineList;

	public Long getDocTypeId() {
		return docTypeId;
	}

	public void setDocTypeId(Long docTypeId) {
		this.docTypeId = docTypeId;
	}

	public String getDocTypeCode() {
		return docTypeCode;
	}

	public void setDocTypeCode(String docTypeCode) {
		this.docTypeCode = docTypeCode;
	}

	public String getDocTypeName() {
		return docTypeName;
	}

	public void setDocTypeName(String docTypeName) {
		this.docTypeName = docTypeName;
	}

	public String getAuthScopeCode() {
		return authScopeCode;
	}

	public void setAuthScopeCode(String authScopeCode) {
		this.authScopeCode = authScopeCode;
	}

	public String getAuthScopeMeaning() {
		return authScopeMeaning;
	}

	public void setAuthScopeMeaning(String authScopeMeaning) {
		this.authScopeMeaning = authScopeMeaning;
	}


	public Integer getSecGrpDclDimCheckedFlag() {
		return secGrpDclDimCheckedFlag;
	}

	public void setSecGrpDclDimCheckedFlag(Integer secGrpDclDimCheckedFlag) {
		this.secGrpDclDimCheckedFlag = secGrpDclDimCheckedFlag;
	}

	public Long getSecGrpDclDimId() {
		return secGrpDclDimId;
	}

	public void setSecGrpDclDimId(Long secGrpDclDimId) {
		this.secGrpDclDimId = secGrpDclDimId;
	}

	public Long getSecGrpId() {
		return secGrpId;
	}

	public void setSecGrpId(Long secGrpId) {
		this.secGrpId = secGrpId;
	}

	public Long getTenantId() {
		return tenantId;
	}

	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
	}

	public Long getObjectVersionNumber() {
		return objectVersionNumber;
	}

	public void setObjectVersionNumber(Long objectVersionNumber) {
		this.objectVersionNumber = objectVersionNumber;
	}

	public List<SecGrpDclDimLineDTO> getSecGrpDclDimLineList() {
		return secGrpDclDimLineList;
	}

	public void setSecGrpDclDimLineList(List<SecGrpDclDimLineDTO> secGrpDclDimLineList) {
		this.secGrpDclDimLineList = secGrpDclDimLineList;
	}

	public Integer getEditEnableFlag() {
		return editEnableFlag;
	}

	public void setEditEnableFlag(Integer editEnableFlag) {
		this.editEnableFlag = editEnableFlag;
	}
}
