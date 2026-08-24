package org.hzero.iam.domain.service.secgrp.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 安全组数据权限维度回收DTO
 *
 * @author xingxingwu.hand-china.com 2019/12/18 15:02
 */
public class SecGrpDclDimRevokeDTO {
	private Long roleId;
	private Long parentRoleId;
	/**
	 * 当前角色行分配的安全组数据权限维度
	 */
	private List<SecGrpDclDimDetailDTO> assignedSecGrpDclDimDetailList;
	/**
	 * 当前角色行创建的安全组数据权限维度
	 */
	private List<SecGrpDclDimDetailDTO> createdSecGrpDclDimDetailList;

	public SecGrpDclDimRevokeDTO(Long roleId, Long parentRoleId) {
		this.roleId = roleId;
		this.parentRoleId = parentRoleId;
		this.assignedSecGrpDclDimDetailList = new ArrayList<>();
		this.createdSecGrpDclDimDetailList = new ArrayList<>();

	}

	public static List<SecGrpDclDimRevokeDTO> buildRevokeList(List<SecGrpDclDimDetailDTO> createdSecGrpDclDimDetailList,
																List<SecGrpDclDimDetailDTO> assignedSecGrpDclDimDetailList) {
		Map<Long, SecGrpDclDimRevokeDTO> secGrpDclDimRevokeRevokeMap = new HashMap<>(16);

		for (SecGrpDclDimDetailDTO secGrpDclDimDetail : createdSecGrpDclDimDetailList) {
			SecGrpDclDimRevokeDTO secGrpDclDimRevokeDTO = secGrpDclDimRevokeRevokeMap.get(secGrpDclDimDetail.getRoleId());
			if (null == secGrpDclDimRevokeDTO){
				secGrpDclDimRevokeDTO = new SecGrpDclDimRevokeDTO(secGrpDclDimDetail.getRoleId(),secGrpDclDimDetail.getParentRoleId());
			}
			secGrpDclDimRevokeDTO.getCreatedSecGrpDclDimDetailList().add(secGrpDclDimDetail);
			secGrpDclDimRevokeRevokeMap.put(secGrpDclDimDetail.getRoleId(),secGrpDclDimRevokeDTO);
		}

		for (SecGrpDclDimDetailDTO secGrpDclDimDetail : assignedSecGrpDclDimDetailList){
			SecGrpDclDimRevokeDTO secGrpDclDimRevokeDTO = secGrpDclDimRevokeRevokeMap.get(secGrpDclDimDetail.getRoleId());
			if (null == secGrpDclDimRevokeDTO){
				secGrpDclDimRevokeDTO = new SecGrpDclDimRevokeDTO(secGrpDclDimDetail.getRoleId(),secGrpDclDimDetail.getParentRoleId());
			}
			secGrpDclDimRevokeDTO.getAssignedSecGrpDclDimDetailList().add(secGrpDclDimDetail);
			secGrpDclDimRevokeRevokeMap.put(secGrpDclDimDetail.getRoleId(),secGrpDclDimRevokeDTO);
		}

		return new ArrayList<>(secGrpDclDimRevokeRevokeMap.values());
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

	public List<SecGrpDclDimDetailDTO> getAssignedSecGrpDclDimDetailList() {
		return assignedSecGrpDclDimDetailList;
	}

	public void setAssignedSecGrpDclDimDetailList(List<SecGrpDclDimDetailDTO> assignedSecGrpDclDimDetailList) {
		this.assignedSecGrpDclDimDetailList = assignedSecGrpDclDimDetailList;
	}

	public List<SecGrpDclDimDetailDTO> getCreatedSecGrpDclDimDetailList() {
		return createdSecGrpDclDimDetailList;
	}

	public void setCreatedSecGrpDclDimDetailList(List<SecGrpDclDimDetailDTO> createdSecGrpDclDimDetailList) {
		this.createdSecGrpDclDimDetailList = createdSecGrpDclDimDetailList;
	}
}
