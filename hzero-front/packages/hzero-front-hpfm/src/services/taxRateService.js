/**
 * taxRateService.js - 税率定义 service
 * @date: 2018-10-29
 * @author: geekrainy <chao.zheng02@hand-china.com>
 * @version: 0.0.1
 * @copyright: Copyright (c) 2018, Hand
 */

import request from 'utils/request';
import { parseParameters, isTenantRoleLevel, getCurrentOrganizationId } from 'utils/utils';
import { HZERO_PLATFORM } from 'utils/config';

const tenantId = getCurrentOrganizationId();

function texRateApi() {
  return isTenantRoleLevel() ? `${tenantId}/taxs` : 'taxs';
}

/**
 * 查询平台级税率
 * @param {Object} params - 查询参数
 */
export async function queryTaxRate(params) {
  return request(`${HZERO_PLATFORM}/v1/${texRateApi()}`, {
    method: 'GET',
    query: parseParameters(params),
  });
}

/**
 * 新增税率
 * @param {Object} params 新增税率
 */
export async function insertTaxRate(params) {
  return request(`${HZERO_PLATFORM}/v1/${texRateApi()}`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 修改平台级税率定义
 * @param {Object} params 修改参数
 */
export async function updateTaxRate(params) {
  return request(`${HZERO_PLATFORM}/v1/${texRateApi()}`, {
    method: 'PUT',
    body: params,
  });
}

/**
 * 查询税率定义明细
 * @param {Number} taxId 税率定义明细查询参数
 */
export async function queryTaxRateInfo(taxId) {
  return request(`${HZERO_PLATFORM}/v1/${texRateApi()}/${taxId}`);
}

/**
 * 查询税率引用明细
 * @param {Object} params 税率引用明细查询参数
 */
export async function queryTaxRateDetail(params) {
  return request(`${HZERO_PLATFORM}/v1/${texRateApi()}/${params.taxId}/refs`, {
    method: 'GET',
    query: params,
  });
}
