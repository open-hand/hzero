import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import request from 'utils/request';
import { HZERO_MSG } from 'utils/config';

const organizationId = getCurrentOrganizationId();
const isSiteFlag = !isTenantRoleLevel();

const apiPrefix = isSiteFlag
  ? `${HZERO_MSG}/v1/email/servers`
  : `${HZERO_MSG}/v1/${organizationId}/email/servers`;
const filterPrefix = isSiteFlag
  ? `${HZERO_MSG}/v1/email-filters`
  : `${HZERO_MSG}/v1/${organizationId}/email-filters`;

/**
 * 查询邮箱账户数据
 * @async
 * @function fetchEmailData
 * @param {Object} params - 查询参数
 */
export async function fetchEmail(params) {
  return request(`${apiPrefix}`, {
    method: 'GET',
    query: params,
  });
}

/**
 * 查询邮箱账户服务器配置项
 * @async
 * @function fetchEmailData
 * @param {Object} params - 查询参数
 * @param {String} params.serverId - 邮箱账号Id
 */
export async function queryEmailServers(params) {
  return request(`${apiPrefix}/${params.serverId}`, {
    method: 'GET',
  });
}

/**
 * 新建邮箱账户
 * @async
 * @function fetchEmailData
 * @param {Object} params - 查询参数
 */
export async function createEmail(params) {
  return request(`${apiPrefix}`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 更新邮箱账户
 * @async
 * @function fetchEmailData
 * @param {Object} params - 查询参数
 */
export async function updateEmail(params) {
  return request(`${apiPrefix}`, {
    method: 'PUT',
    body: params,
  });
}

export async function deleteEmail(params) {
  return request(`${apiPrefix}`, {
    method: 'DELETE',
    body: params,
  });
}

/**
 * 黑白名单列表
 * @async
 * @function fetchFilterList
 * @param {Object} params - 查询参数
 */
export async function fetchFilterList(params) {
  return request(`${filterPrefix}`, {
    method: 'GET',
    query: params,
  });
}

/**
 * 更新黑白名单
 * @async
 * @function fetchFilterList
 * @param {Object} params - 查询参数
 */
export async function updateFilter(params) {
  return request(`${filterPrefix}`, {
    method: 'POST',
    body: params,
  });
}
/**
 * 删除黑白名单
 * @async
 * @function fetchFilterList
 * @param {Object} params - 查询参数
 */
export async function deleteFilter(params) {
  return request(`${filterPrefix}`, {
    method: 'DELETE',
    body: params,
  });
}
