/**
 * service HR组织架构维护
 * WY <yang.wang06@hand-china.com> 2019-05-09 tenants api move from hpfm to hiam
 * @date: 2018-6-19
 * @version: 0.0.1
 * @author: WH <heng.wei@hand-china.com>
 * @copyright Copyright (c) 2018, Hands
 */

import request from 'utils/request';
import { getCurrentOrganizationId, parseParameters } from 'utils/utils';
import { HZERO_PLATFORM, HZERO_IAM } from 'utils/config';

/**
 * 请求API前缀
 * @type {string}
 */
const prefix = `${HZERO_PLATFORM}/v1`;

/**
 * 获取租户信息
 * @async
 * @function fetchOrgInfo
 * @returns {object} fetch Promise
 */
export async function fetchOrgInfo() {
  return request(`${HZERO_IAM}/v1/${getCurrentOrganizationId()}/tenants`, {
    method: 'GET',
  });
}

/**
 * 更新组织信息
 * @async
 * @function saveEdit
 * @param {Object} params - 请求参数
 * @param {!string} params.tenantId - 租户Id
 * @param {!object} params.values - 待保存的组织信息对象
 * @param {!string} params.values.unitId - 组织Id
 * @param {!string} params.values.unitCode - 组织编码
 * @param {!string} params.values.unitName - 组织名称
 * @param {!string} params.values.unitTypeCode - 组织类型编码
 * @param {?string} params.values.supervisorFlag - 主管组织标记
 * @param {?string} params.values.enabledFlag - 启用标记
 * @param {?string} params.values.parentUnitId - 上级组织Id
 * @returns {object} fetch Promise
 */
export async function saveEdit(params) {
  return request(`${prefix}/${params.tenantId}/units`, {
    method: 'PUT',
    body: { ...params.values },
  });
}

/**
 * 添加组织信息
 * @async
 * @function saveAdd
 * @param {Object} params - 请求参数
 * @param {!string} params.tenantId - 租户Id
 * @param {!object[]} params.data - 新增组织对象列表
 * @param {!string} params.data[].unitId - 组织Id
 * @param {!string} params.data[].unitCode - 组织编码
 * @param {!string} params.data[].unitName - 组织名称
 * @param {!string} params.data[].unitTypeCode - 组织类型编码
 * @param {?number} [params.data[].supervisorFlag = 0 ] - 主管组织标记
 * @param {?number} [params.data[].enabledFlag = 1 ] - 启用标记
 * @param {?string} params.data[].parentUnitId - 上级组织Id
 * @returns {object} fetch Promise
 */
export async function saveAdd(params) {
  return request(`${prefix}/${params.tenantId}/units`, {
    method: 'POST',
    body: [...params.data],
  });
}

/**
 * 禁用组织信息
 * @async
 * @function forbindLine
 * @param {Object} params - 请求参数
 * @param {!string} params.tenantId - 租户Id
 * @param {!string} params.unitId - 组织Id
 * @param {!string} params._token - token
 * @param {!number} params.objectVersionNumber - 版本号
 * @returns {object} fetch Promise
 */
export async function forbindLine(params) {
  return request(`${prefix}/${params.tenantId}/units/disable`, {
    method: 'POST',
    body: { ...params },
  });
}

/**
 * 启用组织信息
 * @async
 * @function enabledLine
 * @param {!string} params.tenantId - 租户Id
 * @param {!string} params.unitId - 组织Id
 * @param {!string} params._token - token
 * @param {!number} params.objectVersionNumber - 版本号
 * @returns {object} fetch Promise
 */
export async function enabledLine(params) {
  return request(`${prefix}/${params.tenantId}/units/enable`, {
    method: 'POST',
    body: { ...params },
  });
}

/**
 * 无分页懒加载查询组织信息
 * 第一次 查询所有组织
 * 第二次 依据组织id 查询该组织下的直接下级组织
 * @param {{uniId?: number}} params - 查询参数
 * @return {Promise<object>}
 */
export async function organizationQueryLazyTree(params = {}) {
  return request(`${HZERO_PLATFORM}/v1/${getCurrentOrganizationId()}/units/company/lazy-tree`, {
    method: 'GET',
    query: params,
  });
}

/**
 * 分页查询所有组织信息
 * @param {{uniId?: number}} params - 查询参数
 * @return {Promise<object>}
 */
export async function organizationQueryLine(params = {}) {
  const parsedParams = parseParameters(params);
  return request(`${HZERO_PLATFORM}/v1/${getCurrentOrganizationId()}/units/list`, {
    method: 'GET',
    query: parsedParams,
  });
}
