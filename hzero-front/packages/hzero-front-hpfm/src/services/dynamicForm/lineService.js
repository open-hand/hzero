/**
 * 动态表单配置行
 * @author liang.xiong@hand-china.com
 * @date 2019-07-15
 * @version: 0.0.1
 * @copyright: Copyright (c) 2018, Hand
 */

import request from 'utils/request';
import { parseParameters, getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import { HZERO_PLATFORM } from 'utils/config';

const tenantId = getCurrentOrganizationId();

function formLines() {
  return isTenantRoleLevel() ? `${tenantId}/form-lines` : 'form-lines';
}

/**
 * 查询动态配置行列表
 * @param {Object} params - 参数
 * @returns {Promise<void>}
 */
export async function fetchLineList(params) {
  return request(`${HZERO_PLATFORM}/v1/${formLines()}/${params.formHeaderId}`, {
    method: 'GET',
    query: { ...parseParameters(params) },
  });
}

/**
 * 通过 lineId 查询配置行明细
 * @param {Object} params - 参数
 * @returns {Promise<void>}
 */
export async function fetchLineById(params) {
  return request(`${HZERO_PLATFORM}/v1/${formLines()}/detail/${params.formLineId}`, {
    method: 'GET',
  });
}

/**
 * 新增动态配置行
 * @param {Object} params - 参数
 * @returns {Promise<void>}
 */
export async function createLine(params) {
  return request(`${HZERO_PLATFORM}/v1/${formLines()}`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 更新动态配置行
 * @param {Object} params - 参数
 * @returns {Promise<void>}
 */
export async function updateLine(params) {
  return request(`${HZERO_PLATFORM}/v1/${formLines()}`, {
    method: 'PUT',
    body: params,
  });
}

/**
 * 删除配置行
 * @param {Object} params - 参数
 * @returns {Promise<void>}
 */
export async function removeLine(params) {
  return request(`${HZERO_PLATFORM}/v1/${formLines()}`, {
    method: 'DELETE',
    body: params,
  });
}

/**
 * 通过表单头code查询启用状态的表单配置行列表
 * @param {Object} params - 参数
 * @returns {Promise<void>}
 */
export async function fetchListByHeaderCode(params) {
  return request(`${HZERO_PLATFORM}/v1/${formLines()}/header-code`, {
    method: 'GET',
    query: { ...parseParameters(params) },
  });
}

/**
 * 通过表单头id查询启用状态的表单配置行列表
 * @param {Object} params - 参数
 * @returns {Promise<void>}
 */
export async function fetchListByHeaderId(params) {
  return request(`${HZERO_PLATFORM}/v1/${formLines()}/header-code`, {
    method: 'GET',
    query: params,
  });
}
