package org.hzero.iam.api.dto;

import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.core.algorithm.tree.Child;
import org.hzero.core.algorithm.tree.Node;
import org.hzero.core.base.BaseConstants;
import org.hzero.iam.infra.constant.Constants;
import org.hzero.starter.keyencrypt.core.Encrypt;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.choerodon.core.exception.CommonException;

/**
 * 公司业务实体库存组织DTO
 *
 * @author liang.jin@hand-china.com 2018/07/31 19:23
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CompanyOuInvorgDTO extends Child<CompanyOuInvorgDTO> {
	public static final String ROOT_ID = "-1";
	private String id;
	@LovValue(lovCode = "HIAM.AUTHORITY_TYPE_CODE")
	private String typeCode;
	private String typeMeaning;
	@Encrypt
	private Long dataId;
	private String dataCode;
	private String dataName;
	@Encrypt
	private Long companyId;
	@Encrypt
	private Long ouId;
	@Encrypt
	private Long parentId;
	private Integer checkedFlag;

	@Encrypt
	private Long secGrpDclLineId;
	private Integer shieldFlag;

	/**
	 * ADD 20191021
	 * 支持不同角色的批量新加
	 */
	@Encrypt
	private Long roleId;

	public CompanyOuInvorgDTO() {
	}

	public CompanyOuInvorgDTO(String id, String typeCode, Long dataId, String dataCode, String dataName, Long parentId, Integer checkedFlag) {
		this.id = id;
		this.typeCode = typeCode;
		this.dataId = dataId;
		this.dataCode = dataCode;
		this.dataName = dataName;
		this.parentId = parentId;
		this.checkedFlag = checkedFlag;
	}

	public CompanyOuInvorgDTO(String id, String typeCode, Long dataId, String dataCode, String dataName, Long parentId, Integer shieldFlag, Long secGrpDclLineId) {
		this.id = id;
		this.typeCode = typeCode;
		this.dataId = dataId;
		this.dataCode = dataCode;
		this.dataName = dataName;
		this.parentId = parentId;
		this.shieldFlag = shieldFlag;
		this.secGrpDclLineId = secGrpDclLineId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTypeCode() {
		return typeCode;
	}

	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}

	public String getTypeMeaning() {
		return typeMeaning;
	}

	public void setTypeMeaning(String typeMeaning) {
		this.typeMeaning = typeMeaning;
	}

	public Long getDataId() {
		return dataId;
	}

	public void setDataId(Long dataId) {
		this.dataId = dataId;
	}

	public String getDataCode() {
		return dataCode;
	}

	public void setDataCode(String dataCode) {
		this.dataCode = dataCode;
	}

	public String getDataName() {
		return dataName;
	}

	public void setDataName(String dataName) {
		this.dataName = dataName;
	}

	public Integer getCheckedFlag() {
		return checkedFlag;
	}

	public void setCheckedFlag(Integer checkedFlag) {
		this.checkedFlag = checkedFlag;
	}


	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public Long getOuId() {
		return ouId;
	}

	public void setOuId(Long ouId) {
		this.ouId = ouId;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public static final Node<String, CompanyOuInvorgDTO> NODE = new Node<String, CompanyOuInvorgDTO>() {
		@Override
		public String getKey(CompanyOuInvorgDTO item) {
			return item.getId();
		}

		@Override
		public String getParentKey(CompanyOuInvorgDTO item) {
			if (item.getParentId() == null) {
				return ROOT_ID;
			}
			switch (item.getTypeCode()) {
				case Constants.AUTHORITY_TYPE_CODE.OU:
					return Constants.AUTHORITY_TYPE_CODE.COMPANY + item.getParentId();
				case Constants.AUTHORITY_TYPE_CODE.INVORG:
					return Constants.AUTHORITY_TYPE_CODE.OU + item.getParentId();
				default:
					throw new CommonException(BaseConstants.ErrorCode.DATA_INVALID);
			}
		}
	};

	@Override
	public String toString() {
		return "CompanyOuInvorgDTO{" +
				"id='" + id + '\'' +
				", typeCode='" + typeCode + '\'' +
				", typeMeaning='" + typeMeaning + '\'' +
				", dataId=" + dataId +
				", dataCode='" + dataCode + '\'' +
				", dataName='" + dataName + '\'' +
				", companyId=" + companyId +
				", ouId=" + ouId +
				", parentId=" + parentId +
				", checkedFlag=" + checkedFlag +
				'}';
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public Long getSecGrpDclLineId() {
		return secGrpDclLineId;
	}

	public void setSecGrpDclLineId(Long secGrpDclLineId) {
		this.secGrpDclLineId = secGrpDclLineId;
	}

	public Integer getShieldFlag() {
		return shieldFlag;
	}

	public void setShieldFlag(Integer shieldFlag) {
		this.shieldFlag = shieldFlag;
	}
}
