/**
 *  客户端
 * @date: 2018-12-24
 * @author: LZY <zhuyan.luo@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import request from 'utils/request';
import { HZERO_IAM } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel, parseParameters } from 'utils/utils';

/**
 * 请求API前缀
 * @type {string}
 */
const prefix = `${HZERO_IAM}/v1`;

const currentOrganizationId = getCurrentOrganizationId();

function clientApi() {
  return isTenantRoleLevel() ? `${currentOrganizationId}/clients` : `clients`;
}

function client() {
  return isTenantRoleLevel() ? `${currentOrganizationId}/` : ``;
}

function visitRole() {
  return isTenantRoleLevel()
    ? `${currentOrganizationId}/clients/access-roles`
    : `clients/access-roles`;
}

/**
 * 查询client列表数据
 * @async
 * @function fetchClientList
 * @param {object} params - 查询条件
 */
export async function fetchClientList(params) {
  const param = parseParameters(params);
  const { tenantId, ...others } = param;
  return request(`${prefix}/${clientApi()}`, {
    method: 'GET',
    query: others,
  });
}
/**
 * 查询clien详情数据
 * @async
 * @function fetchDetail
 * @param {object} params - 查询条件
 */
export async function fetchDetail(params) {
  const { clientId } = params;
  return request(`${prefix}/${clientApi()}/${clientId}`, {
    method: 'GET',
  });
}

/**
 * 校验
 * @async
 * @function checkClient
 * @param {object} params - 创建参数
 */
export async function checkClient(params) {
  const { tenantId, ...others } = params;
  return request(`${prefix}/${clientApi()}/check`, {
    method: 'POST',
    responseType: 'text',
    body: others,
  });
}

/**
 * 新建
 * @async
 * @function createClient
 * @param {object} params - 创建参数
 */
export async function createClient(params) {
  const { tenantId, ...others } = params;
  return request(`${prefix}/${clientApi()}`, {
    method: 'POST',
    body: others,
  });
}

/**
 * 更新
 * @async
 * @function updateClient
 * @param {object} params - 参数
 */
export async function updateClient(params) {
  const { tenantId, ...others } = params;
  return request(`${prefix}/${clientApi()}`, {
    method: 'PUT',
    body: others,
  });
}

/**
 * 删除
 * @async
 * @function deleteClient
 * @param {object} params - 参数
 */
// export async function deleteClient(params) {
//   const { tenantId } = params;
//   return request(`${prefix}/${tenantId}/clients`, {
//     method: 'DELETE',
//     // body: params,
//   });
// }

/**
 * 查询当前登录用户所拥有 分配 全选的 角色
 * @param {!Number} currentOrganizationId 租户id
 */
export async function subAccountOrgRoleQueryAll(payload) {
  return request(`${HZERO_IAM}/hzero/v2/roles/self/assignable-roles`, {
    method: 'GET',
    query: {
      ...payload,
      memberType: 'client',
    },
  });
}

/**
 * 查询当前租户下的用户所拥有的角色
 * @param {!Number} currentOrganizationId 租户id
 * @param {!Number} userId 用户id
 */
export async function subAccountOrgRoleCurrent(params) {
  const { clientId, ...others } = params;
  const parsedParams = parseParameters(others);
  return request(`${HZERO_IAM}/hzero/v1/${client()}member-roles/client-roles/${clientId}`, {
    method: 'GET',
    query: parsedParams,
  });
}

/**
 * 保存角色
 * @async
 * @function createClient
 * @param {object} params - 创建参数
 */
export async function saveRoleSet(params) {
  return request(`${HZERO_IAM}/hzero/v1/${client()}/member-roles/batch-assign`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 删除角色
 * @param {Number} memberRoleList 用户角色列表
 */
export async function deleteRoles(memberRoleList) {
  return request(`${HZERO_IAM}/hzero/v1/${client()}/member-roles`, {
    method: 'DELETE',
    body: memberRoleList,
  });
}

/**
 * 查询当前租户下的用户所拥有的角色
 * @param {!Number} currentOrganizationId 租户id
 * @param {!Number} userId 用户id
 */
export async function roleVisitCurrent(params) {
  const { clientId, ...others } = params;
  const parsedParams = parseParameters(others);
  return request(`${HZERO_IAM}/v1/${visitRole()}/${clientId}`, {
    method: 'GET',
    query: parsedParams,
  });
}

/**
 * 保存角色
 * @async
 * @function createClient
 * @param {object} params - 创建参数
 */
export async function saveVisitRoleSet(params) {
  const { clientId, memberRoleList } = params;
  return request(`${HZERO_IAM}/v1/${visitRole()}/${clientId}`, {
    method: 'POST',
    body: memberRoleList,
  });
}

/**
 * 删除角色
 * @param {Number} memberRoleList 用户角色列表
 */
export async function deleteVisitRoles(params) {
  const { clientId, memberRoleList } = params;
  return request(`${HZERO_IAM}/v1/${visitRole()}/${clientId}`, {
    method: 'DELETE',
    body: memberRoleList,
  });
}
