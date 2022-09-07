/**
 * 动态表单配置头
 * @author liang.xiong@hand-china.com
 * @date 2019-07-15
 * @version: 0.0.1
 * @copyright: Copyright (c) 2018, Hand
 */

import request from 'utils/request';
import { parseParameters, isTenantRoleLevel, getCurrentOrganizationId } from 'utils/utils';
import { HZERO_PLATFORM } from 'utils/config';

const tenantId = getCurrentOrganizationId();

function formHeaders() {
  return isTenantRoleLevel() ? `${tenantId}/form-headers` : 'form-headers';
}
/**
 * 分页查询动态表单配置头
 * @param params
 * @returns {Promise<void>}
 */
export async function fetchHeaderList( params= {} ) {
  return request(`${HZERO_PLATFORM}/v1/${formHeaders()}`, {
    method: 'GET',
    query: parseParameters(params),
  });
}

/**
 * 动态表单配置头明细
 * @param {Object} params - 参数
 * @returns {Promise<void>}
 */
export async function fetchHeaderById(params) {
  return request(`${HZERO_PLATFORM}/v1/${formHeaders()}/${params.formHeaderId}`, {
    method: 'GET',
  });
}

/**
 * 新增动态配表单置头
 * @param {Object} params - 参数
 * @returns {Promise<void>}
 */
export async function createHeader(params) {
  return request(`${HZERO_PLATFORM}/v1/${formHeaders()}`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 更新动态表单配置头
 * @param {Object} params - 参数
 * @returns {Promise<void>}
 */
export async function updateHeader(params) {
  return request(`${HZERO_PLATFORM}/v1/${formHeaders()}`, {
    method: 'PUT',
    body: params,
  });
}

/**
 * 删除动态表单配置头
 * @param {Object} params - 参数
 * @returns {Promise<void>}
 */
export async function removeHeader(params) {
  return request(`${HZERO_PLATFORM}/v1/${formHeaders()}`, {
    method: 'DELETE',
    body: params,
  });
}

// 查询启用的表单配置头列表
export async function fetchAbleHeaderList(params={}){
  return request(`${HZERO_PLATFORM}/v1/${formHeaders()}/list`, {
    method: 'GET',
    query: parseParameters(params),
  });
}
