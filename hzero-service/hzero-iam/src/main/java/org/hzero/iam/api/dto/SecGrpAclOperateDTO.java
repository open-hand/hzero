package org.hzero.iam.api.dto;

import java.util.List;

/**
 * 安全组访问权限操作参数DTO
 *
 * @author xingxingwu.hand-china.com 2019/11/19 9:46
 */
public class SecGrpAclOperateDTO {
	/**
	 * 新增的权限ID
	 */
	private List<Long> addPermissionIds;
	/**
	 * 删除的权限ID
	 */
	private List<Long> deletePermissionIds;

	public List<Long> getAddPermissionIds() {
		return addPermissionIds;
	}

	public void setAddPermissionIds(List<Long> addPermissionIds) {
		this.addPermissionIds = addPermissionIds;
	}

	public List<Long> getDeletePermissionIds() {
		return deletePermissionIds;
	}

	public void setDeletePermissionIds(List<Long> deletePermissionIds) {
		this.deletePermissionIds = deletePermissionIds;
	}
}
