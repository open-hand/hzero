/**
 * roleManagementService - 角色管理service
 * 使用了 平台 HZERO_PLATFORM 的接口 (查询角色分配的卡片)
 * @date: 2018-7-24
 * @author: lijun <jun.li06@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import qs from 'querystring';
import request from 'utils/request';
import {
  getCurrentOrganizationId,
  parseParameters,
  filterNullValueObject,
  isTenantRoleLevel,
} from 'utils/utils';
import { getEnvConfig } from 'utils/iocUtils';

const { HZERO_IAM, HZERO_PLATFORM } = getEnvConfig();

const tenantId = getCurrentOrganizationId();
const organizationRoleLevel = isTenantRoleLevel();

function memberRoleClient() {
  return isTenantRoleLevel()
    ? `${tenantId}/member-roles/role-clients`
    : 'member-roles/role-clients';
}

function clientDel() {
  return isTenantRoleLevel()
    ? `${tenantId}/member-roles/batch-delete`
    : 'member-roles/batch-delete';
}

function clientAdd() {
  return isTenantRoleLevel()
    ? `${tenantId}/member-roles/batch-assign`
    : 'member-roles/batch-assign';
}

function enableStatus(status) {
  return status ? 'enable' : 'disable';
}

const secGrp = organizationRoleLevel
  ? `v1/${tenantId}/sec-grp-role-assign`
  : `v1/sec-grp-role-assign`;

/**
 * 获取表单数据
 * @param {Number} organizationId 租户id
 */
export async function fetchPasswordPolicyList() {
  return request(`${HZERO_IAM}/v1/${tenantId}/password-policies/query`, {
    method: 'GET',
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
export async function enableRole(params) {
  const { status, ...others } = params;
  return request(
    organizationRoleLevel
      ? `${HZERO_IAM}/hzero/v1/${tenantId}/roles/${enableStatus(status)}`
      : `${HZERO_IAM}/hzero/v1/roles/${enableStatus(status)}`,
    {
      method: 'PUT',
      body: others,
    }
  );
}

/**
 * 查询值集
 * @async
 * @function queryCode
 * @param {!number} id - 角色ID
 * @param {!boolean} [status=false] - 启用或停用
 * @returns {object} fetch Promise
 */
export async function enableOrganizationRole({ id, status }, organizationId) {
  return request(`${HZERO_IAM}/hzero/v1/${organizationId}/roles/${id}/${enableStatus(status)}`, {
    method: 'PUT',
    responseType: 'text',
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
  return request(
    organizationRoleLevel ? `${HZERO_IAM}/v1/${tenantId}/labels` : `${HZERO_IAM}/v1/labels`,
    {
      query: params,
    }
  );
}

/**
 * 通过类型查询label
 * @async
 * @function queryLabels
 * @param {object} params - 角色ID
 * @param {!boolean} [status=false] - 启用或停用
 * @returns {object} fetch Promise
 */
export async function queryRoleLabel(params) {
  return request(
    organizationRoleLevel
      ? `${HZERO_IAM}/v1/${tenantId}/label-rels/ROLE/${params}`
      : `${HZERO_IAM}/v1/label-rels/ROLE/${params}`,
    {}
  );
}

/**
 * 通过层级查询label
 * @async
 * @function queryLabelList
 * @param {object} params - 角色ID
 * @param {!boolean} [status=false] - 启用或停用
 * @returns {object} fetch Promise
 */
export async function queryLabelList(params) {
  return request(
    organizationRoleLevel
      ? `${HZERO_IAM}/v1/${tenantId}/labels/by-type`
      : `${HZERO_IAM}/v1/labels/by-type`,
    {
      query: params,
      method: 'GET',
    }
  );
}

/**
 * 通过id查询角色
 * @async
 * @function queryRole
 * @param {!number} roleId - 角色ID
 * @returns {object} fetch Promise
 */
export async function queryRole(roleId) {
  return request(
    organizationRoleLevel
      ? `${HZERO_IAM}/hzero/v1/${tenantId}/roles/${roleId}`
      : `${HZERO_IAM}/hzero/v1/roles/${roleId}`
  );
}

/**
 * 通过id查询角色 - 租户级
 * @async
 * @function queryOrganizationRole
 * @param {!number} roleId - 角色ID
 * @returns {object} fetch Promise
 */
export async function queryOrganizationRole(roleId, organizationId) {
  return request(`${HZERO_IAM}/hzero/v1/${organizationId}/roles/${roleId}`);
}

/**
 * 查询角色可分配权限集子树
 * @async
 * @function queryLevelPermissions
 * @param {!number} roleId - 角色ID
 * @param {object} params - 查询条件
 * @param {!number} [params.page = 0] - 数据页码
 * @param {!number} [params.size = 10] - 分页大小
 * @returns {object} fetch Promise
 */
export async function queryPermissionSets(roleId, params = {}) {
  return request(
    organizationRoleLevel
      ? `${HZERO_IAM}/hzero/v1/${tenantId}/roles/${roleId}/permission-sets`
      : `${HZERO_IAM}/hzero/v1/roles/${roleId}/permission-sets`,
    {
      query: params,
    }
  );
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
export async function queryLevelPermissions(roleId, params = {}) {
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
export async function queryOrganizationLevelPermissions(roleId, params = {}, organizationId) {
  return request(`${HZERO_IAM}/hzero/v1/${organizationId}/permissions/${roleId}`, {
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

export async function queryHrunitsTree(params = {}) {
  return request(
    organizationRoleLevel
      ? `${HZERO_IAM}/hzero/v1/${tenantId}/hrunits/tree`
      : `${HZERO_IAM}/hzero/v1/hrunits/tree`,
    {
      query: params,
    }
  );
}

export async function queryMemberRolesUsers(params = {}) {
  const { roleId } = params;
  return request(`${HZERO_IAM}/hzero/v1/member-roles/role-users/${roleId}`, {
    query: params,
  });
}

export async function queryOrganizationMemberRolesUsers(params = {}, organizationId) {
  const { roleId } = params;
  return request(`${HZERO_IAM}/hzero/v1/${organizationId}/member-roles/role-users/${roleId}`, {
    query: params,
  });
}

export async function saveMembers(data, isEdit) {
  return request(`${HZERO_IAM}/hzero/v1/member-roles/batch-assign`, {
    method: 'POST',
    body: data,
    query: {
      isEdit,
    },
  });
}

export async function saveOrganizationMembers(data, isEdit, organizationId) {
  return request(`${HZERO_IAM}/hzero/v1/${organizationId}/member-roles/batch-assign`, {
    method: 'POST',
    body: data,
    query: {
      isEdit,
    },
  });
}

export async function deleteMember(data = {}) {
  return request(`${HZERO_IAM}/hzero/v1/member-roles/batch-delete`, {
    method: 'DELETE',
    body: data,
  });
}

export async function deleteOrganizationMember(data = {}, organizationId) {
  return request(`${HZERO_IAM}/hzero/v1/${organizationId}/member-roles/batch-delete`, {
    method: 'DELETE',
    body: data,
  });
}

/**
 * 查询角色已分配的客户端
 * @param {*} params
 */
export async function queryMemberRolesClients(params = {}) {
  const { roleId } = params;
  return request(`${HZERO_IAM}/hzero/v1/${memberRoleClient()}/${roleId}`, {
    query: parseParameters(params),
  });
}

export async function deleteClient(params = {}) {
  return request(`${HZERO_IAM}/hzero/v1/${clientDel()}`, {
    method: 'DELETE',
    body: params,
  });
}

export async function saveClients(data) {
  return request(`${HZERO_IAM}/hzero/v1/${clientAdd}`, {
    method: 'POST',
    body: data,
  });
}

export async function queryRoleAuth(params) {
  const { roleId, body } = params;
  return request(
    organizationRoleLevel
      ? `${HZERO_IAM}/v1/${tenantId}/roles/${roleId}/role-auths/${getCurrentOrganizationId()}`
      : `${HZERO_IAM}/v1/roles/${roleId}/role-auths/${getCurrentOrganizationId()}`,
    {
      method: 'GET',
      query: body,
    }
  );
}

export async function saveRoleAuth(params) {
  const { roleId, body } = params;
  return request(
    organizationRoleLevel
      ? `${HZERO_IAM}/v1/${tenantId}/roles/${roleId}/role-auths`
      : `${HZERO_IAM}/v1/roles/${roleId}/role-auths`,
    {
      method: 'POST',
      body,
    }
  );
}

export async function deleteRoleAuth(params) {
  const { roleId, body } = params;
  return request(
    organizationRoleLevel
      ? `${HZERO_IAM}/v1/${tenantId}/roles/${roleId}/role-auths?${qs.stringify({
          roleAuthId: body.roleAuthId,
        })}`
      : `${HZERO_IAM}/v1/roles/${roleId}/role-auths?${qs.stringify({
          roleAuthId: body.roleAuthId,
        })}`,
    {
      method: 'DELETE',
      body,
    }
  );
}

/**
 * 查询角色可分配权限的菜单子树
 * @async
 * @function queryPermissionMenus
 * @param {!object} params - 保存参数
 * @returns {object} fetch Promise
 */
export async function queryPermissionMenus(roleId, params) {
  return request(
    organizationRoleLevel
      ? `${HZERO_IAM}/hzero/v1/${tenantId}/roles/${roleId}/permission-set-tree`
      : `${HZERO_IAM}/hzero/v1/roles/${roleId}/permission-set-tree`,
    {
      query: params,
    }
  );
}

/**
 * 批量分配权限集至角色
 * @async
 * @function batchAssignPermissionSets
 * @param {!object} params - 保存参数
 * @param {!number} roleId - 角色ID
 * @returns {object} fetch Promise
 */
export async function batchAssignPermissionSets(roleId, data) {
  return request(`${HZERO_IAM}/hzero/v1/roles/${roleId}/permission-sets/assign`, {
    method: 'PUT',
    body: data,
  });
}

/**
 * 批量分配权限集至角色 - 租户级
 * @async
 * @function batchAssignPermissionSets
 * @param {!object} params - 保存参数
 * @param {!number} roleId - 角色ID
 * @returns {object} fetch Promise
 */
export async function batchAssignOrganizationPermissionSets(roleId, data, organizationId) {
  return request(`${HZERO_IAM}/hzero/v1/${organizationId}/roles/${roleId}/permission-sets/assign`, {
    method: 'PUT',
    body: data,
  });
}

/**
 * 批量取消分配权限集至角色
 * @async
 * @function batchAssignPermissionSets
 * @param {!object} params - 保存参数
 * @param {!number} roleId - 角色ID
 * @returns {object} fetch Promise
 */
export async function batchUnassignPermissionSets(roleId, data) {
  return request(`${HZERO_IAM}/hzero/v1/roles/${roleId}/permission-sets/recycle`, {
    method: 'PUT',
    body: data,
  });
}

/**
 * 批量取消分配权限集至角色 - 租户级
 * @async
 * @function batchAssignPermissionSets
 * @param {!object} params - 保存参数
 * @param {!number} roleId - 角色ID
 * @returns {object} fetch Promise
 */
export async function batchUnassignOrganizationPermissionSets(roleId, data, organizationId) {
  return request(
    `${HZERO_IAM}/hzero/v1/${organizationId}/roles/${roleId}/permission-sets/recycle`,
    {
      method: 'PUT',
      body: data,
    }
  );
}

/**
 * 批量取消分配明细权限
 * @async
 * @function unassignOrganizationPermissions
 * @param {!object} params - 保存参数
 * @param {!number} roleId - 角色ID
 * @returns {object} fetch Promise
 */
export async function unassignOrganizationPermissions(roleId, data, organizationId) {
  return request(`${HZERO_IAM}/hzero/v1/${organizationId}/roles/${roleId}/permissions/unassign`, {
    method: 'PUT',
    body: data,
    responseType: 'text',
  });
}

/**
 * 组织层查询当前登录用户默认角色
 * @async
 * @function queryCurrentRole
 * @returns {object} fetch Promise
 */
export async function queryCurrentRole() {
  return request(`${HZERO_IAM}/hzero/v1/member-roles/current-role`);
}

/**
 * 分页查查询角色信息
 * @async
 * @function queryCreatedSubroles
 * @param {object} params - 查询条件
 * @param {!number} [params.page = 0] - 数据页码
 * @param {!number} [params.size = 10] - 分页大小
 * @returns {object} fetch Promise
 */
export async function queryCreatedSubroles(params = {}) {
  const query = filterNullValueObject(parseParameters(params));
  return request(`${HZERO_IAM}/hzero/v2/roles/self/manageable-roles`, {
    query,
  });
}

/* 角色分配卡片 */

/**
 * 查询角色分配的卡片
 * @requestUrl {HZERO_PLATFORM}/v1/dashboard-role-cards/{roleId}
 * @requestMethod GET
 * @param {object} - page - 分页信息
 * @param {object} - sort - 排序信息
 * @param {number} - roleId - 角色id
 */
export async function roleCardsQuery(params) {
  const { roleId, ...restParams } = params;
  const parsedParams = parseParameters(restParams);
  return request(`${HZERO_PLATFORM}/v1/dashboard-role-cards/${roleId}`, {
    method: 'GET',
    query: parsedParams,
  });
}

/**
 * 删除角色分配的卡片
 * @requestUrl {HZERO_PLATFORM}/v1/dashboard-role-cards
 * @requestMethod DELETE
 * @param {object[]} - params - 删除的卡片信息
 */
export async function roleCardsDelete(params) {
  return request(`${HZERO_PLATFORM}/v1/dashboard-role-cards`, {
    method: 'DELETE',
    body: params,
  });
}

/**
 * 新增或修改角色分配的卡片
 * @requestUrl {HZERO_PLATFORM}/v1/dashboard-role-cards
 * @requestMethod POST
 * @param {object[]} - params - 删除的卡片信息
 */
export async function roleCardsInsertOrUpdate(params) {
  return request(`${HZERO_PLATFORM}/v1/dashboard-role-cards`, {
    method: 'POST',
    body: params,
  });
}

/* 角色分配卡片 租户级 */

/**
 * 查询角色分配的卡片
 * @requestUrl {HZERO_PLATFORM}/v1/{organizationId}/dashboard-role-cards/{roleId}
 * @requestMethod GET
 * @param {object} - page - 分页信息
 * @param {object} - sort - 排序信息
 * @param {number} - roleId - 角色id
 */
export async function orgRoleCardsQuery(organizationId, params) {
  const { roleId, ...restParams } = params;
  const parsedParams = parseParameters(restParams);
  return request(`${HZERO_PLATFORM}/v1/${organizationId}/dashboard-role-cards/${roleId}`, {
    method: 'GET',
    query: parsedParams,
  });
}

/**
 * 删除角色分配的卡片
 * @requestUrl {HZERO_PLATFORM}/v1/{organizationId}/dashboard-role-cards
 * @requestMethod DELETE
 * @param {object[]} - params - 删除的卡片信息
 */
export async function orgRoleCardsDelete(organizationId, params) {
  return request(`${HZERO_PLATFORM}/v1/${organizationId}/dashboard-role-cards`, {
    method: 'DELETE',
    body: params,
  });
}

/**
 * 新增或修改角色分配的卡片
 * @requestUrl {HZERO_PLATFORM}/v1/{organizationId}/dashboard-role-cards
 * @requestMethod POST
 * @param {object[]} - params - 删除的卡片信息
 */
export async function orgRoleCardsInsertOrUpdate(organizationId, params) {
  return request(`${HZERO_PLATFORM}/v1/${organizationId}/dashboard-role-cards`, {
    method: 'POST',
    body: params,
  });
}

/**
 * @async
 * @function queryTenant - 查询用户租户列表数据
 */
export async function queryCurrentTenants() {
  return request(`${HZERO_IAM}/hzero/v1/users/self-tenants`, {
    method: 'GET',
  });
}

export async function queryAdminRole(roleId) {
  return request(`${HZERO_IAM}/hzero/v1/roles/admin-role/${roleId}`, {
    method: 'GET',
  });
}

/**
 * 接口字段维护 服务
 * apiFieldService
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-07-09
 * @copyright 2019-07-09 © HAND
 */

/**
 * 接口 model 不能修改
 * @typedef {object} API
 * @property {number} fieldPermissionId - 主键
 * @property {number} fieldId - 字段id
 * @property {string} permissionType - 权限类型 值集
 * @property {string} permissionRule - 权限规则 值集
 * @property {string} permissionDimension - 权限维度 值集
 * @property {number} dimensionValue - 权限维度值
 */

/**
 * @description 字段权限 model 可以 查增改删
 * @typedef {Object} FIELD_PERMISSION
 * @property {number} fieldId - 主键
 * @property {string} fieldDescription - 字段描述 480
 * @property {string} fieldName - 字段名称 120
 * @property {string} fieldType - 字段类型 值集
 * @property {number} orderSeq - 排序
 * @property {number} permissionId - 接口id
 */

/**
 * @typedef {Object} Page<T>
 * @property {number} number - 分页
 * @property {number} size - 分页大小
 * @property {number} totalElements - 总数据量
 * @property {T[]} content - 数据
 */

/**
 * @typedef {Object} PageInfo
 * @property {number} page - 分页
 * @property {number} size - 分页大小
 */

/**
 * 查询接口
 * @param params
 * @return {Promise<API[]>}
 */
export async function apiFieldApiQuery(params) {
  const query = parseParameters(params);
  return request(`${HZERO_IAM}/v1/${getCurrentOrganizationId()}/apis`, {
    method: 'GET',
    query,
  });
}

/**
 * 查询搜索字段-标签 数据
 * @export
 * @param {*} param
 * @returns
 */
export async function querySearchLabels(param) {
  return request(
    organizationRoleLevel
      ? `${HZERO_IAM}/v1/${tenantId}/labels/by-type`
      : `${HZERO_IAM}/v1/labels/by-type`,
    {
      method: 'GET',
      query: param,
    }
  );
}

/**
 * 查询对应接口的字段权限
 * @param {number} roleId - 角色id
 * @param {number} permissionId - 接口id
 * @param {PageInfo} params - 查询分页参数
 * @param {FIELD_PERMISSION} params - 查询实体
 * @return {Promise<API[]>}
 */
export async function apiFieldFieldPermissionQuery(roleId, permissionId, params) {
  const query = parseParameters(params);
  return request(
    `${HZERO_IAM}/v1/${getCurrentOrganizationId()}/apis/${permissionId}/role/${roleId}`,
    {
      method: 'GET',
      query,
    }
  );
}

/**
 * 修改对应接口的字段权限
 * @param {number} roleId - 角色id
 * @param {number} permissionId - 接口id
 * @param {FIELD_PERMISSION} record - 修改实体
 */
export async function apiFieldFieldPermissionUpdate(roleId, permissionId, record) {
  return request(
    `${HZERO_IAM}/v1/${getCurrentOrganizationId()}/apis/${permissionId}/role/${roleId}`,
    {
      method: 'PUT',
      body: record,
    }
  );
}

/**
 * 新增对应接口的字段权限
 * @param {number} roleId - 角色id
 * @param {number} permissionId - 接口id
 * @param {FIELD_PERMISSION} record - 修改实体
 */
export async function apiFieldFieldPermissionCreate(roleId, permissionId, record) {
  return request(
    `${HZERO_IAM}/v1/${getCurrentOrganizationId()}/apis/${permissionId}/role/${roleId}`,
    {
      method: 'POST',
      body: record,
    }
  );
}

/**
 * 删除对应接口的字段权限
 * @param {number} permissionId - 接口id
 * @param {FIELD_PERMISSION} record - 修改实体
 */
export async function apiFieldFieldPermissionRemove(permissionId, record) {
  return request(`${HZERO_IAM}/v1/${getCurrentOrganizationId()}/apis/${permissionId}/permission`, {
    method: 'DELETE',
    body: record,
  });
}

/**
 * 查询树形列表
 * @param {number} parentRoleId - 父级节点角色ID
 */
export async function queryRoleTree(params) {
  const param = filterNullValueObject(parseParameters(params));
  return request(`${HZERO_IAM}/hzero/v1/roles/self/manageable-roles/tree/sub`, {
    method: 'GET',
    query: { ...param },
  });
}

/**
 * 条件查询树形列表
 * @param {object} params - 查询参数
 */
export async function queryRoleTreeAll(params) {
  const param = parseParameters(params);
  return request(`${HZERO_IAM}/hzero/v1/roles/self/manageable-roles/tree`, {
    method: 'GET',
    query: { ...param },
  });
}

export async function queryRoleCard(params) {
  const param = parseParameters(params);
  return request(
    isTenantRoleLevel()
      ? `${HZERO_PLATFORM}/v1/${tenantId}/dashboard-role-cards/role-assign-card`
      : `${HZERO_PLATFORM}/v1/dashboard-role-cards/role-assign-card`,
    {
      method: 'GET',
      query: { ...param },
    }
  );
}

/**
 * 查询已分配的安全组
 * @param {object} params - 查询参数
 */
export async function querySecurityGroup(params) {
  const { roleId } = params;
  const param = parseParameters(params);
  return request(`${HZERO_IAM}/${secGrp}/${roleId}/assigned-sec-grp`, {
    method: 'GET',
    query: { ...param },
  });
}

/**
 * 查询可分配的安全组
 * @param {object} params - 查询参数
 */
export async function queryAssignableSecGrp(params) {
  const { roleId } = params;
  const param = parseParameters(params);
  return request(`${HZERO_IAM}/${secGrp}/${roleId}/assignable-sec-grp`, {
    method: 'GET',
    query: { ...param },
  });
}

/**
 * 角色分配安全组
 * @param {number} roleId - 角色id
 * @param {number} permissionId - 接口id
 * @param {FIELD_PERMISSION} record - 修改实体
 */
export async function assignSecGrp(secGrpList, roleId) {
  return request(`${HZERO_IAM}/${secGrp}/${roleId}/assign-sec-grp`, {
    method: 'POST',
    body: secGrpList,
  });
}

/**
 * 角色取消分配安全组
 * @param {object} payload - 删除的安全组集合
 */
export async function deleteSecGrp(payload) {
  const { roleId, secGrpList } = payload;
  return request(`${HZERO_IAM}/${secGrp}/${roleId}/recycle-sec-grp`, {
    method: 'DELETE',
    body: secGrpList,
  });
}

/**
 * 查询角色已分配的指定安全组的访问权限
 * @param {number} roleId - 角色ID
 * @param {number} secGrpId - 安全组ID
 */
export async function querySecGrpPermissionMenus(roleId, secGrpId) {
  return request(`${HZERO_IAM}/${secGrp}/${roleId}/${secGrpId}/acl`);
}

/**
 * 查询角色已分配的指定安全组的字段权限
 * @param {object} params - 请求参数
 */
export async function queryFieldPermission(params) {
  const { roleId, secGrpId } = params;
  const param = parseParameters(params);
  return request(`${HZERO_IAM}/${secGrp}/${roleId}/${secGrpId}/field-api`, {
    method: 'GET',
    query: { ...param },
  });
}

/**
 * 查询角色已分配的指定安全组的字段权限配置
 * @param {number} params - 请求参数
 */
export async function queryFieldConfig(params) {
  const { roleId, secGrpId, permissionId } = params;
  const param = parseParameters(params);
  return request(`${HZERO_IAM}/${secGrp}/${roleId}/${secGrpId}/field-api/${permissionId}/fields`, {
    method: 'GET',
    query: { ...param },
  });
}

/**
 * 查询角色已分配的指定安全组的工作台配置
 * @param {object} params - 请求参数
 */
export async function queryCardPermission(params) {
  const { roleId, secGrpId } = params;
  const param = parseParameters(params);
  return request(`${HZERO_IAM}/${secGrp}/${roleId}/${secGrpId}/dashboard`, {
    method: 'GET',
    query: { ...param },
  });
}

/**
 * 查询角色已分配的指定安全组的数据权限维度
 * @param {object} params - 请求参数
 */
export async function queryDataDimension(params) {
  const { roleId, secGrpId } = params;
  const param = parseParameters(params);
  return request(`${HZERO_IAM}/${secGrp}/${roleId}/${secGrpId}/dim`, {
    method: 'GET',
    query: { ...param },
  });
}

/**
 * 查询安全组数据权限tab页
 * @param {number} secGrpId - 安全组ID
 */
export async function queryTabList(secGrpId) {
  return request(`${HZERO_IAM}/v1/sec-grp-dcls/${secGrpId}/dims/assigned`);
}

/**
 * 查询角色已分配的指定安全组的数据权限-除公司外
 * @param {object} params - 请求参数
 */
export async function queryData(params) {
  const { roleId, secGrpId } = params;
  const param = parseParameters(params);
  return request(`${HZERO_IAM}/${secGrp}/${roleId}/${secGrpId}/dcl`, {
    method: 'GET',
    query: { ...param },
  });
}

/**
 * 查询角色已分配的指定安全组的数据权限-公司
 * @param {object} params - 请求参数
 */
export async function queryCompanyPermission(params) {
  const { roleId, secGrpId } = params;
  const param = parseParameters(params);
  return request(`${HZERO_IAM}/${secGrp}/${roleId}/${secGrpId}/dcl`, {
    method: 'GET',
    query: param,
  });
}

/**
 * 屏蔽安全组权限
 * @param {object} payload - 请求数据
 */
export async function shieldSecGrpPermission(payload) {
  const { roleId, secGrpId, ...rest } = payload;
  const queryString = qs.stringify(rest);
  return request(`${HZERO_IAM}/${secGrp}/${roleId}/${secGrpId}/shield?${queryString}`, {
    method: 'POST',
  });
}

/**
 * 取消屏蔽安全组权限
 * @param {object} payload - 请求数据
 */
export async function cancelShieldSecGrpPermission(payload) {
  const { roleId, secGrpId, ...rest } = payload;
  const queryString = qs.stringify(rest);
  return request(`${HZERO_IAM}/${secGrp}/${roleId}/${secGrpId}/cancel-shield?${queryString}`, {
    method: 'POST',
  });
}

export async function fetchUserList(params) {
  return request(
    organizationRoleLevel
      ? `${HZERO_IAM}/hzero/v1/${tenantId}/users/paging/all`
      : `${HZERO_IAM}/hzero/v1/users/paging/all`,
    {
      method: 'GET',
      query: params,
    }
  );
}

export async function fetchClientList(params) {
  return request(`${HZERO_IAM}/v1/clients/paging/all`, {
    method: 'GET',
    query: params,
  });
}

export async function fetchTreeRole(params) {
  const param = filterNullValueObject(parseParameters(params));
  return request(`${HZERO_IAM}/hzero/v2/roles/self/manageable-roles/tree`, {
    method: 'GET',
    query: { ...param },
  });
}
