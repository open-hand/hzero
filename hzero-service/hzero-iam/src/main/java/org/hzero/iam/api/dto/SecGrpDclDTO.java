package org.hzero.iam.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.core.domain.Page;
import org.hzero.iam.domain.entity.SecGrpDcl;
import org.hzero.iam.domain.entity.SecGrpDclLine;

import java.util.List;

/**
 * 安全组访问权限明细DTO
 *
 * @author xingxingwu.hand-china.com 2019/10/22 18:37
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SecGrpDclDTO {
	private SecGrpDcl secGrpDcl;
	private Page<SecGrpDclLine> secGrpDclLineList;

	private List<CompanyOuInvorgDTO> originList;
	private List<CompanyOuInvorgDTO> treeList;

	public SecGrpDcl getSecGrpDcl() {
		return secGrpDcl;
	}

	public SecGrpDclDTO setSecGrpDcl(SecGrpDcl secGrpDcl) {
		this.secGrpDcl = secGrpDcl;
		return this;
	}

	public Page<SecGrpDclLine> getSecGrpDclLineList() {
		return secGrpDclLineList;
	}

	public SecGrpDclDTO setSecGrpDclLineList(Page<SecGrpDclLine> secGrpDclLineList) {
		this.secGrpDclLineList = secGrpDclLineList;
		return this;
	}

	public List<CompanyOuInvorgDTO> getOriginList() {
		return originList;
	}

	public SecGrpDclDTO setOriginList(List<CompanyOuInvorgDTO> originList) {
		this.originList = originList;
		return this;
	}

	public List<CompanyOuInvorgDTO> getTreeList() {
		return treeList;
	}

	public SecGrpDclDTO setTreeList(List<CompanyOuInvorgDTO> treeList) {
		this.treeList = treeList;
		return this;
	}
}
