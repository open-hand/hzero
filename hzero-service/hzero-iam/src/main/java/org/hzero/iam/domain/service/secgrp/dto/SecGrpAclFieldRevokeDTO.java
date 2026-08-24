package org.hzero.iam.domain.service.secgrp.dto;

import java.util.*;
import org.hzero.iam.domain.entity.SecGrpAclField;

/**
 * 安全组字段权限回收DTO
 *
 * @author allen.liu
 * @date 2019/12/16
 */
public class SecGrpAclFieldRevokeDTO {
	private Long roleId;
	private Long parentRoleId;
	/**
	 * 当前角色行分配的安全组字段权限
	 */
	private List<SecGrpAclFieldRevokeLineDTO> assignedSecGrpAclFieldList;
	/**
	 * 当前角色行创建的安全组字段权限
	 */
	private List<SecGrpAclFieldRevokeLineDTO> createdSecGrpAclFieldList;

	public SecGrpAclFieldRevokeDTO(Long roleId, Long parentRoleId) {
		this.roleId = roleId;
		this.parentRoleId = parentRoleId;
		this.assignedSecGrpAclFieldList = new ArrayList<>();
		this.createdSecGrpAclFieldList = new ArrayList<>();

	}


	public static List<SecGrpAclFieldRevokeDTO> buildRevokeList(List<SecGrpAclField> createdSecGrpAclFieldList,
																List<SecGrpAclField> assignedSecGrpAclFieldList) {
		Map<Long, SecGrpAclFieldRevokeDTO> secGrpAclFieldRevokeMap = new HashMap<>(16);

		for (SecGrpAclField secGrpAclField : createdSecGrpAclFieldList) {
			SecGrpAclFieldRevokeDTO secGrpAclFieldRevokeDTO = secGrpAclFieldRevokeMap.get(secGrpAclField.getRoleId());
			if (null == secGrpAclFieldRevokeDTO){
				secGrpAclFieldRevokeDTO = new SecGrpAclFieldRevokeDTO(secGrpAclField.getRoleId(),secGrpAclField.getParentRoleId());
			}
			secGrpAclFieldRevokeDTO.getCreatedSecGrpAclFieldList().add(new SecGrpAclFieldRevokeLineDTO(secGrpAclField));
			secGrpAclFieldRevokeMap.put(secGrpAclField.getRoleId(),secGrpAclFieldRevokeDTO);
		}

		for (SecGrpAclField secGrpAclField : assignedSecGrpAclFieldList) {
			SecGrpAclFieldRevokeDTO secGrpAclFieldRevokeDTO = secGrpAclFieldRevokeMap.get(secGrpAclField.getRoleId());
			if (null == secGrpAclFieldRevokeDTO){
				secGrpAclFieldRevokeDTO = new SecGrpAclFieldRevokeDTO(secGrpAclField.getRoleId(),secGrpAclField.getParentRoleId());
			}
			secGrpAclFieldRevokeDTO.getAssignedSecGrpAclFieldList().add(new SecGrpAclFieldRevokeLineDTO(secGrpAclField));
			secGrpAclFieldRevokeMap.put(secGrpAclField.getRoleId(),secGrpAclFieldRevokeDTO);
		}

		return new ArrayList<>(secGrpAclFieldRevokeMap.values());
	}


	public Long getRoleId() {
		return roleId;
	}

	public SecGrpAclFieldRevokeDTO setRoleId(Long roleId) {
		this.roleId = roleId;
		return this;
	}

	public Long getParentRoleId() {
		return parentRoleId;
	}

	public SecGrpAclFieldRevokeDTO setParentRoleId(Long parentRoleId) {
		this.parentRoleId = parentRoleId;
		return this;
	}

	public List<SecGrpAclFieldRevokeLineDTO> getAssignedSecGrpAclFieldList() {
		return assignedSecGrpAclFieldList;
	}

	public SecGrpAclFieldRevokeDTO setAssignedSecGrpAclFieldList(List<SecGrpAclFieldRevokeLineDTO> assignedSecGrpAclFieldList) {
		this.assignedSecGrpAclFieldList = assignedSecGrpAclFieldList;
		return this;
	}

	public List<SecGrpAclFieldRevokeLineDTO> getCreatedSecGrpAclFieldList() {
		return createdSecGrpAclFieldList;
	}

	public SecGrpAclFieldRevokeDTO setCreatedSecGrpAclFieldList(List<SecGrpAclFieldRevokeLineDTO> createdSecGrpAclFieldList) {
		this.createdSecGrpAclFieldList = createdSecGrpAclFieldList;
		return this;
	}
}
