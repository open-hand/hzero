/**
 * roleService - 角色管理service
 * @date: 2018-7-24
 * @author: lijun <jun.li06@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import qs from 'querystring';
import request from 'utils/request';
import { HZERO_IAM, HZERO_PLATFORM } from 'utils/config';

function enableStatus(status) {
  return status ? 'enable' : 'disable';
}

/**
 * 查询分配给当前登录用户的角色
 * @async
 * @function queryAdminRoles
 * @param {object} params - 查询条件
 * @returns {object} fetch Promise
 */
export async function queryAdminRoles(params = {}) {
  return request(`${HZERO_IAM}/hzero/v1/member-roles/self`, {
    query: params,
  });
}

/**
 * 查询管理员用户所创建的角色子树
 * @async
 * @function queryCreateRolesSubtree
 * @param {!number} parentRoleId - 角色(父)ID
 * @returns {object} fetch Promise
 */
export async function queryCreateRolesSubtree(parentRoleId) {
  return request(`${HZERO_IAM}/hzero/v1/member-roles/create-roles-subtree/${parentRoleId}`);
}

/**
 * 查询用户所拥有的全部角色，包括被直接授予的，以及间接继承的
 * @async
 * @function queryInheritRolesTree
 * @param {object} params - 查询条件
 * @param {?number} params.userId - 用户ID
 * @returns {object} fetch Promise
 */
export async function queryInheritRolesTree(params = {}) {
  return request(`${HZERO_IAM}/hzero/v1/member-roles/inherit-roles-tree`, {
    query: params,
  });
}

/**
 * 查询值集
 * @async
 * @function queryCode
 * @param {object} params - 查询条件
 * @param {!string} param.lovCode - 查询条件
 * @returns {object} fetch Promise
 */
export async function queryCode(params = {}) {
  return request(`${HZERO_PLATFORM}/v1/lovs/value`, {
    query: params,
  });
}

/**
 * 查询值集
 * @async
 * @function queryCode
 * @param {!number} id - 角色ID
 * @param {!boolean} [status=false] - 启用或停用
 * @returns {object} fetch Promise
 */
export async function enableRole({ id, status }) {
  return request(`${HZERO_IAM}/v1/roles/${id}/${enableStatus(status)}`, {
    method: 'PUT',
  });
}

/**
 * 通过类型查询label
 * @async
 * @function queryLabels
 * @param {object} params - 角色ID
 * @param {!boolean} [status=false] - 启用或停用
 * @returns {object} fetch Promise
 */
export async function queryLabels(params) {
  return request(`${HZERO_IAM}/v1/labels`, {
    query: params,
  });
}

/**
 * 通过id查询角色
 * @async
 * @function queryRole
 * @param {!number} roleId - 角色ID
 * @returns {object} fetch Promise
 */
export async function queryRole(roleId) {
  return request(`${HZERO_IAM}/hzero/v1/roles/${roleId}`);
}

/**
 * 分页查询角色对应层级的权限
 * @deprecated 弃用,API已调整
 * @async
 * @function queryPermissions
 * @param {!number} roleId - 角色ID
 * @param {object} params - 查询条件
 * @param {!number} [params.page = 0] - 数据页码
 * @param {!number} [params.size = 10] - 分页大小
 * @returns {object} fetch Promise
 */
export async function queryPermissions(roleId) {
  return request(`${HZERO_IAM}/hzero/v1/permissions/${roleId}`);
}

/**
 * 分页查询角色对应层级的权限
 * @deprecated 弃用,API已调整
 * @async
 * @function queryOrganizationPermissions
 * @param {!number} roleId - 角色ID
 * @param {!number} organizationId - 组织ID
 * @param {object} params - 查询条件
 * @param {!number} [params.page = 0] - 数据页码
 * @param {!number} [params.size = 10] - 分页大小
 * @returns {object} fetch Promise
 */
export async function queryOrganizationPermissions(roleId, organizationId) {
  return request(`${HZERO_IAM}/hzero/v1/${organizationId}/permissions/${roleId}`);
}

/**
 * 分页查询角色对应层级的权限
 * @async
 * @function queryLevelPermissions
 * @param {!number} roleId - 角色ID
 * @param {object} params - 查询条件
 * @param {!number} [params.page = 0] - 数据页码
 * @param {!number} [params.size = 10] - 分页大小
 * @returns {object} fetch Promise
 */
export async function queryLevelPermissions(roleId, level, params = {}) {
  return request(`${HZERO_IAM}/hzero/v1/permissions/${roleId}`, {
    query: params,
  });
}

/**
 * 分页查询角色对应层级的权限 - 租户级
 * @async
 * @function queryOrganizationLevelPermissions
 * @param {!number} roleId - 角色ID
 * @param {!number} organizationId - 组织ID
 * @param {object} params - 查询条件
 * @param {!number} [params.page = 0] - 数据页码
 * @param {!number} [params.size = 10] - 分页大小
 * @returns {object} fetch Promise
 */
export async function queryOrganizationLevelPermissions(
  roleId,
  level,
  params = {},
  organizationId
) {
  return request(`${HZERO_IAM}/hzero/v1/${organizationId}/permissions/${roleId}`, {
    query: params,
  });
}

/**
 * 查询所有已分配给角色的权限
 * @async
 * @function queryPermissionsAll
 * @param {!number} roleId - 角色ID
 * @returns {object} fetch Promise
 */
export async function queryPermissionsAll(roleId) {
  return request(`${HZERO_IAM}/hzero/v1/permissions/all/${roleId}`);
}

/**
 * 查询所有已分配给角色的权限 - 租户级
 * @async
 * @function queryOrganizationPermissionsAll
 * @param {!number} roleId - 角色ID
 * @param {!number} organizationId - 组织ID
 * @returns {object} fetch Promise
 */
export async function queryOrganizationPermissionsAll(roleId, organizationId) {
  return request(`${HZERO_IAM}/hzero/v1/${organizationId}/permissions/all/${roleId}`);
}

/**
 * 查询角色已收回的权限
 * @async
 * @function queryOrganizationPermissionsAll
 * @param {!number} roleId - 角色ID
 * @param {object} params - 查询条件
 * @param {!number} [params.page = 0] - 数据页码
 * @param {!number} [params.size = 10] - 分页大小
 * @returns {object} fetch Promise
 */
export async function queryRevokedPermissionsAll(roleId, params) {
  return request(`${HZERO_IAM}/hzero/v1/permissions/revoked/all/${roleId}`, {
    query: params,
  });
}

/**
 * 查询角色已收回的权限 - 租户级
 * @async
 * @function queryOrganizationRevokedPermissionsAll
 * @param {!number} roleId - 角色ID
 * @param {!number} organizationId - 组织ID
 * @param {object} params - 查询条件
 * @param {!number} [params.page = 0] - 数据页码
 * @param {!number} [params.size = 10] - 分页大小
 * @returns {object} fetch Promise
 */
export async function queryOrganizationRevokedPermissionsAll(roleId, params, organizationId) {
  return request(`${HZERO_IAM}/hzero/v1/${organizationId}/permissions/revoked/all/${roleId}`, {
    query: params,
  });
}

/**
 * 创建角色
 * @async
 * @function createRole
 * @param {!object} params - 保存参数
 * @returns {object} fetch Promise
 */
export async function createRole(params) {
  return request(`${HZERO_IAM}/hzero/v1/roles`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 创建角色 - 租户级
 * @async
 * @function createOrganizationRole
 * @param {!number} organizationId - 组织ID
 * @param {!object} params - 保存参数
 * @returns {object} fetch Promise
 */
export async function createOrganizationRole(params, organizationId) {
  return request(`${HZERO_IAM}/hzero/v1/${organizationId}/roles`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 修改角色
 * @async
 * @function editRole
 * @param {!object} params - 保存参数
 * @returns {object} fetch Promise
 */
export async function editRole(params) {
  return request(`${HZERO_IAM}/hzero/v1/roles`, {
    method: 'PUT',
    body: params,
  });
}

/**
 * 修改角色 - 租户级
 * @async
 * @function editOrganizationRole
 * @param {!number} organizationId - 组织ID
 * @param {!object} params - 保存参数
 * @returns {object} fetch Promise
 */
export async function editOrganizationRole(params, organizationId) {
  return request(`${HZERO_IAM}/hzero/v1/${organizationId}/roles`, {
    method: 'PUT',
    body: params,
  });
}

/**
 * 复制并创建角色
 * @async
 * @function copyRole
 * @param {!object} params - 保存参数
 * @returns {object} fetch Promise
 */
export async function copyRole(params) {
  return request(`${HZERO_IAM}/hzero/v1/roles/copy`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 复制并创建角色 - 租户级
 * @async
 * @function copyOrganizationRole
 * @param {!number} organizationId - 组织ID
 * @param {!object} params - 保存参数
 * @returns {object} fetch Promise
 */
export async function copyOrganizationRole(params, organizationId) {
  return request(`${HZERO_IAM}/hzero/v1/${organizationId}/roles/copy`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 继承并创建角色
 * @async
 * @function inheritRole
 * @param {!object} params - 保存参数
 * @returns {object} fetch Promise
 */
export async function inheritRole(params) {
  return request(`${HZERO_IAM}/hzero/v1/roles/inherit`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 继承并创建角色 - 租户级
 * @async
 * @function inheritOrganizationRole
 * @param {!number} organizationId - 组织ID
 * @param {!object} params - 保存参数
 * @returns {object} fetch Promise
 */
export async function inheritOrganizationRole(params, organizationId) {
  return request(`${HZERO_IAM}/hzero/v1/${organizationId}/roles/inherit`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 取消回收角色权限
 * @async
 * @function unrevoke
 * @param {!number} roleId - 角色ID
 * @param {!object} params - 保存参数
 * @returns {object} fetch Promise
 */
export async function unrevoke(roleId, params) {
  return request(`${HZERO_IAM}/hzero/v1/role-permissions/${roleId}/unrevoke`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 取消回收角色权限 - 租户级
 * @async
 * @function unrevokeOrganization
 * @param {!number} organizationId - 组织ID
 * @param {!number} roleId - 角色ID
 * @param {!object} params - 保存参数
 * @returns {object} fetch Promise
 */
export async function unrevokeOrganization(roleId, params, organizationId) {
  return request(`${HZERO_IAM}/hzero/v1/${organizationId}/role-permissions/${roleId}/unrevoke`, {
    method: 'POST',
    body: params,
  });
}

export async function queryRoleAuth(params) {
  const { roleId, body } = params;
  return request(`${HZERO_IAM}/v1/roles/${roleId}/role-auths`, {
    method: 'GET',
    query: body,
  });
}

export async function saveRoleAuth(params) {
  const { roleId, body } = params;

  return request(`${HZERO_IAM}/v1/roles/${roleId}/role-auths`, {
    method: 'POST',
    body,
  });
}

export async function deleteRoleAuth(params) {
  const { roleId, body } = params;
  return request(
    `${HZERO_IAM}/v1/roles/${roleId}/role-auths?${qs.stringify({
      roleAuthId: body.roleAuthId,
    })}`,
    {
      method: 'DELETE',
      body,
    }
  );
}
