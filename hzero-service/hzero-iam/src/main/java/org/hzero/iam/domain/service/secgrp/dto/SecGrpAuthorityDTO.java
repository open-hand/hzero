package org.hzero.iam.domain.service.secgrp.dto;

import java.util.List;
import org.hzero.iam.domain.entity.SecGrpAcl;
import org.hzero.iam.domain.entity.SecGrpAclField;
import org.hzero.iam.domain.entity.SecGrpDclLine;

/**
 * description
 *
 * @author xingxingwu.hand-china.com 2019/11/05 18:22
 */
public class SecGrpAuthorityDTO {
	/**
	 * 安全组访问权限
	 */
	private List<SecGrpAcl> secGrpAclList;
	/**
	 * 安全组访问权限字段
	 */
	private List<SecGrpAclField> SecGrpAclFieldList;
	/**
	 * 安全组数据权限维度
	 */
	private List<SecGrpDclDimDetailDTO> secGrpDclDimDetailList;
	/**
	 * 安全组数据权限
	 */
	private List<SecGrpDclLine> secGrpDclLineList;

	public List<SecGrpAcl> getSecGrpAclList() {
		return secGrpAclList;
	}

	public SecGrpAuthorityDTO setSecGrpAclList(List<SecGrpAcl> secGrpAclList) {
		this.secGrpAclList = secGrpAclList;
		return this;
	}

	public List<SecGrpAclField> getSecGrpAclFieldList() {
		return SecGrpAclFieldList;
	}

	public SecGrpAuthorityDTO setSecGrpAclFieldList(List<SecGrpAclField> secGrpAclFieldList) {
		SecGrpAclFieldList = secGrpAclFieldList;
		return this;
	}

	public List<SecGrpDclDimDetailDTO> getSecGrpDclDimDetailList() {
		return secGrpDclDimDetailList;
	}

	public SecGrpAuthorityDTO setSecGrpDclDimDetailList(List<SecGrpDclDimDetailDTO> secGrpDclDimDetailList) {
		this.secGrpDclDimDetailList = secGrpDclDimDetailList;
		return this;
	}

	public List<SecGrpDclLine> getSecGrpDclLineList() {
		return secGrpDclLineList;
	}

	public SecGrpAuthorityDTO setSecGrpDclLineList(List<SecGrpDclLine> secGrpDclLineList) {
		this.secGrpDclLineList = secGrpDclLineList;
		return this;
	}
}
