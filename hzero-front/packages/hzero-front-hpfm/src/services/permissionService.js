/**
 * permissionService - 数据权限service
 * @date: 2018-7-24
 * @author: lijun <jun.li06@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import request from 'utils/request';
import { HZERO_PLATFORM } from 'utils/config';
import { isTenantRoleLevel, getCurrentOrganizationId } from 'utils/utils';

const tenantId = getCurrentOrganizationId();

function relApi() {
  return isTenantRoleLevel() ? `${tenantId}/permission-rel` : `permission-rel`;
}

/**
 * 屏蔽范围列表
 * @async
 * @function queryRangeList
 * @param {object} params - 查询条件
 * @returns {object} fetch Promise
 */
export async function queryRangeList(params = {}) {
  return request(`${HZERO_PLATFORM}/v1/permission-ranges`, {
    query: params,
  });
}

/**
 * 屏蔽范围列表 - 租户级
 * @async
 * @function queryOrganizationRangeList
 * @param {object} params - 查询条件
 * @param {!number} organizationId - 组织ID
 * @returns {object} fetch Promise
 */
export async function queryOrganizationRangeList(params = {}, organizationId) {
  return request(`${HZERO_PLATFORM}/v1/${organizationId}/permission-ranges`, {
    query: params,
  });
}

/**
 * 屏蔽规则列表
 * @async
 * @function queryRuleList
 * @param {object} params - 查询条件
 * @returns {object} fetch Promise
 */
export async function queryRuleList(params = {}) {
  return request(`${HZERO_PLATFORM}/v1/permission-rules`, {
    query: params,
  });
}

/**
 * 屏蔽规则列表 - 租户级
 * @async
 * @function queryOrganizationRuleList
 * @param {object} params - 查询条件
 * @param {!number} organizationId - 组织ID
 * @returns {object} fetch Promise
 */
export async function queryOrganizationRuleList(params = {}, organizationId) {
  return request(`${HZERO_PLATFORM}/v1/${organizationId}/permission-rules`, {
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
 * 创建屏蔽规则
 * @async
 * @function createRule
 * @param {object} data - 数据
 * @returns {object} fetch Promise
 */
export async function createRule(data = {}) {
  return request(`${HZERO_PLATFORM}/v1/permission-rules`, {
    method: 'POST',
    body: data,
  });
}

/**
 * 创建屏蔽规则 - 租户级
 * @async
 * @function createOrganizationRule
 * @param {object} data - 数据
 * @param {!number} organizationId - 组织ID
 * @returns {object} fetch Promise
 */
export async function createOrganizationRule(data = {}, organizationId) {
  return request(`${HZERO_PLATFORM}/v1/${organizationId}/permission-rules`, {
    method: 'POST',
    body: data,
  });
}

/**
 * 修改屏蔽规则
 * @async
 * @function updateRule
 * @param {object} data - 数据
 * @returns {object} fetch Promise
 */
export async function updateRule(data = {}) {
  return request(`${HZERO_PLATFORM}/v1/permission-rules`, {
    method: 'PUT',
    body: data,
  });
}

/**
 * 修改屏蔽规则 - 租户级
 * @async
 * @function updateOrganizationRule
 * @param {object} data - 数据
 * @param {!number} organizationId - 组织ID
 * @returns {object} fetch Promise
 */
export async function updateOrganizationRule(data = {}, organizationId) {
  return request(`${HZERO_PLATFORM}/v1/${organizationId}/permission-rules`, {
    method: 'PUT',
    body: data,
  });
}

/**
 * 删除屏蔽规则
 * @async
 * @function delete
 * @param {object} data - 数据
 * @returns {object} fetch Promise
 */
export async function deleteRule(params) {
  return request(`${HZERO_PLATFORM}/v1/permission-rules`, {
    method: 'DELETE',
    body: params,
  });
}

/**
 * 删除屏蔽规则 - 租户级
 * @async
 * @function deleteOrganizationRule
 * @param {object} data - 数据
 * @param {!number} organizationId - 组织ID
 * @returns {object} fetch Promise
 */
export async function deleteOrganizationRule(params, organizationId) {
  return request(`${HZERO_PLATFORM}/v1/${organizationId}/permission-rules`, {
    method: 'DELETE',
    body: params,
  });
}

/**
 * 创建屏蔽范围
 * @async
 * @function createRange
 * @param {object} data - 数据
 * @returns {object} fetch Promise
 */
export async function createRange(data = {}) {
  return request(`${HZERO_PLATFORM}/v1/permission-ranges`, {
    method: 'POST',
    body: data,
  });
}

/**
 * 创建屏蔽范围 - 租户级
 * @async
 * @function createRange
 * @param {object} data - 数据
 * @returns {object} fetch Promise
 */
export async function createOrganizationRange(data = {}, organizationId) {
  return request(`${HZERO_PLATFORM}/v1/${organizationId}/permission-ranges`, {
    method: 'POST',
    body: data,
  });
}

/**
 * 修改屏蔽范围
 * @async
 * @function updateRange
 * @param {object} data - 数据
 * @returns {object} fetch Promise
 */
export async function updateRange(data = {}) {
  return request(`${HZERO_PLATFORM}/v1/permission-ranges`, {
    method: 'PUT',
    body: data,
  });
}

/**
 * 修改屏蔽范围 - 租户级
 * @async
 * @function updateOrganizationRange
 * @param {object} data - 数据
 * @returns {object} fetch Promise
 */
export async function updateOrganizationRange(data = {}, organizationId) {
  return request(`${HZERO_PLATFORM}/v1/${organizationId}/permission-ranges`, {
    method: 'PUT',
    body: data,
  });
}

/**
 * 删除屏蔽范围
 * @async
 * @function deleteRange
 * @param {number} rangeId - 屏蔽范围ID
 * @returns {object} fetch Promise
 */
export async function deleteRange(params) {
  return request(`${HZERO_PLATFORM}/v1/permission-ranges`, {
    method: 'DELETE',
    body: params,
  });
}

/**
 * 删除屏蔽范围
 * @async
 * @function deleteOrganizationRange
 * @param {number} rangeId - 屏蔽范围ID
 * @returns {object} fetch Promise
 */
export async function deleteOrganizationRange(params, organizationId) {
  return request(`${HZERO_PLATFORM}/v1/${organizationId}/permission-ranges`, {
    method: 'DELETE',
    body: params,
  });
}

/**
 * 创建屏蔽范围规则关系
 * @async
 * @function createRange
 * @param {object} data - 数据
 * @returns {object} fetch Promise
 */
export async function addPermissionRel(data = {}) {
  return request(`${HZERO_PLATFORM}/v1/${relApi()}`, {
    method: 'POST',
    body: data,
  });
}

/**
 * 屏蔽范围规则关系列表
 * @async
 * @function queryPermissionRel
 * @param {!number} rangeId - 数据
 * @returns {object} fetch Promise
 */
export async function queryPermissionRel(rangeId) {
  return request(`${HZERO_PLATFORM}/v1/${relApi()}/${rangeId}`);
}

/**
 * 删除屏蔽范围规则关系
 * @async
 * @function deletePermissionRel
 * @param {!number} permissionRelId - 屏蔽范围规则关系ID
 * @returns {object} fetch Promise
 */
export async function deletePermissionRel(params) {
  return request(`${HZERO_PLATFORM}/v1/${relApi()}`, {
    method: 'DELETE',
    body: params,
  });
}
