/**
 * service - 接收者类型维护
 * @date: 2018-7-26
 * @version: 0.0.1
 * @author: WH <heng.wei@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */

import request from 'utils/request';
import { HZERO_MSG } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel, parseParameters } from 'utils/utils';

const tenantId = getCurrentOrganizationId();

function receiverApi() {
  return isTenantRoleLevel() ? `${tenantId}/receiver-types` : 'receiver-types';
}

/**
 * 请求API前缀
 * @type {string}
 */
const prefix = `${HZERO_MSG}/v1`;

/**
 * 查询接收者类型列表数据
 * @async
 * @function fetchReceiverType
 * @param {object} params - 查询条件
 * @param {?string} params.typeCode - 类型编码
 * @param {?string} params.typeName - 描述
 * @param {!number} [params.page = 0] - 数据页码
 * @param {!number} [params.size = 10] - 分页大小
 * @returns {object} fetch Promise
 */
export async function fetchReceiverType(params) {
  const param = parseParameters(params);
  return request(`${prefix}/${receiverApi()}`, {
    method: 'GET',
    query: param,
  });
}

/**
 * 更新接收者类型信息
 * @async
 * @function updateReceiverType
 * @param {object} params - 请求参数
 * @param {!object} params.data - 更新对象
 * @param {!string} params.data.apiUrl - URL
 * @param {?number} params.data.enabledFlag - 启用标记
 * @param {!string} params.data.routeName - 服务
 * @param {!number} params.data.tenantId - 租户ID
 * @param {!string} params.data.typeCode - 类型编码
 * @param {!string} params.data.typeName - 描述
 * @returns {object} fetch Promise
 */

export async function updateReceiverType(params) {
  return request(`${prefix}/${receiverApi()}`, {
    method: 'PUT',
    body: { ...params },
  });
}

/**
 * 添加接收者类型信息
 * @async
 * @function addReceiverType
 * @param {object} params - 请求参数
 * @param {!object} params.data - 保存对象
 * @param {!string} params.data.apiUrl - URL
 * @param {?number} params.data.enabledFlag - 启用标记
 * @param {!string} params.data.routeName - 服务
 * @param {!number} params.data.tenantId - 租户ID
 * @param {!string} params.data.typeCode - 类型编码
 * @param {!string} params.data.typeName - 描述
 * @returns {object} fetch Promise
 */
export async function addReceiverType(params) {
  return request(`${prefix}/${receiverApi()}`, {
    method: 'POST',
    body: { ...params },
  });
}

/**
 * 查询已经分配的行数据
 * @returns {Promise<object>}
 */
export async function queryAssignedList(receiverTypeId, params) {
  const newParams = parseParameters(params);
  const isTenant = isTenantRoleLevel();
  const reqUrl = isTenant
    ? `${HZERO_MSG}/v1/${getCurrentOrganizationId()}/receiver-types/lines/${receiverTypeId}`
    : `${HZERO_MSG}/v1/receiver-types/lines/${receiverTypeId}`;
  return request(reqUrl, {
    method: 'GET',
    query: newParams,
  });
}

/**
 * 将 用户组 或者 组织 分配给接收者类型
 * @returns {Promise<object>}
 */
export async function assignListToReceiverType(receiverTypeId, records) {
  const isTenant = isTenantRoleLevel();
  const reqUrl = isTenant
    ? `${HZERO_MSG}/v1/${getCurrentOrganizationId()}/receiver-types/lines/${receiverTypeId}`
    : `${HZERO_MSG}/v1/receiver-types/lines/${receiverTypeId}`;
  return request(reqUrl, {
    method: 'POST',
    body: records,
  });
}

/**
 * 移除已经分配给 接收者类型的行数据
 * @returns {Promise<void>}
 */
export async function removeReceiverTypeList(_ /* receiverTypeId */, records) {
  const isTenant = isTenantRoleLevel();
  const reqUrl = isTenant
    ? `${HZERO_MSG}/v1/${getCurrentOrganizationId()}/receiver-types/lines`
    : `${HZERO_MSG}/v1/receiver-types/lines`;
  return request(reqUrl, {
    method: 'DELETE',
    body: records,
  });
}

/**
 * 查询没有分配的 用户组
 * @returns {Promise<void>}
 */
export async function queryNoAssignUserGroupList(receiverTypeId, query) {
  const isTenant = isTenantRoleLevel();
  const reqUrl = isTenant
    ? `${HZERO_MSG}/v1/${getCurrentOrganizationId()}/receiver-types/lines/user-groups/${receiverTypeId}`
    : `${HZERO_MSG}/v1/receiver-types/lines/user-groups/${receiverTypeId}`;
  return request(reqUrl, {
    method: 'GET',
    query: parseParameters(query),
  });
}

/**
 * 查询没有分配的 组织
 * @returns {Promise<void>}
 */
export async function queryNoAssignUnitList(receiverTypeId, query) {
  const isTenant = isTenantRoleLevel();
  const reqUrl = isTenant
    ? `${HZERO_MSG}/v1/${getCurrentOrganizationId()}/receiver-types/lines/units/${receiverTypeId}`
    : `${HZERO_MSG}/v1/receiver-types/lines/units/${receiverTypeId}`;
  return request(reqUrl, {
    method: 'GET',
    query: parseParameters(query),
  });
}

// 接收者类型用户组列表
// GET /v1/{organizationId}/receiver-types/lines/{receiverTypeId}
// 创建接收者类型用户组
// POST /v1/{organizationId}/receiver-types/lines
// 删除接收者类型用户组（批量）
// DELETE /v1/{organizationId}/receiver-types/lines
