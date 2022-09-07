/**
 * rateTypeService.js - 汇率类型 service
 * @date: 2018-10-24
 * @author: geekrainy <chao.zheng02@hand-china.com>
 * @version: 0.0.1
 * @copyright: Copyright (c) 2018, Hand
 */

import request from 'utils/request';
import { parseParameters, isTenantRoleLevel, getCurrentOrganizationId } from 'utils/utils';
import { HZERO_PLATFORM } from 'utils/config';

const tenantId = getCurrentOrganizationId();

function rateTypeApi() {
  return isTenantRoleLevel() ? `${tenantId}/exchange-rate-types` : 'exchange-rate-types';
}

/**
 * 查询平台级汇率类型
 * @param {Object} params - 查询参数
 */
export async function queryRateType(params) {
  return request(`${HZERO_PLATFORM}/v1/${rateTypeApi()}`, {
    method: 'GET',
    query: parseParameters(params),
  });
}

/**
 * 新增汇率类型
 * @param {Object} params 新增汇率类型
 */
export async function insertRateType(params) {
  return request(`${HZERO_PLATFORM}/v1/${rateTypeApi()}`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 批量删除汇率类型
 * @param {Array} params 批量删除汇率类型
 */
export async function deleteRateType(params) {
  return request(`${HZERO_PLATFORM}/v1/${rateTypeApi()}`, {
    method: 'DELETE',
    body: params,
  });
}

/**
 * 修改平台级汇率类型
 * @param {Object} params 修改参数
 */
export async function updateRateType(params) {
  return request(`${HZERO_PLATFORM}/v1/${rateTypeApi()}`, {
    method: 'PUT',
    body: params,
  });
}

/**
 * 查询汇率类型明细
 * @param {Number} rateTypeId 汇率类型 Id
 * @param {Object} params 汇率类型明细查询参数
 */
export async function queryRateTypeDetail(params) {
  return request(`${HZERO_PLATFORM}/v1/${rateTypeApi()}/tenants`, {
    method: 'GET',
    query: params,
  });
}

/**
 * 根据 rateTypeId 查询汇率类型信息
 * @param {Number} rateTypeId 汇率类型 ID
 */
export async function queryRateTypeInfo(rateTypeId) {
  return request(`${HZERO_PLATFORM}/v1/${rateTypeApi()}/${rateTypeId}`, {
    method: 'GET',
  });
}
