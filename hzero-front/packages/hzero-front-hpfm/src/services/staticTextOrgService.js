/**
 * staticTextServiceOrg.js
 * @date 2018-12-25
 * @author WY yang.wang06@hand-china.com
 * @copyright Copyright (c) 2018, Hand
 */

import request from 'utils/request';
import { HZERO_PLATFORM } from 'utils/config';
import { parseParameters } from 'utils/utils';

/**
 * 新增静态文本
 * @param {number} organizationId
 * @param {object} record
 * @return {Promise<Response>}
 */
export async function staticTextCreateOne(organizationId, record) {
  return request(`${HZERO_PLATFORM}/v1/${organizationId}/static-texts`, {
    method: 'POST',
    body: record,
  });
}

/**
 * 批量删除静态文本
 * @param {number} organizationId
 * @param {object[]} records
 * @return {Promise<Response>}
 */
export async function staticTextRemoveList(organizationId, records) {
  return request(`${HZERO_PLATFORM}/v1/${organizationId}/static-texts`, {
    method: 'DELETE',
    body: records,
  });
}

/**
 * 修改静态文本
 * @param {number} organizationId
 * @param {object} record
 * @return {Promise<Response>}
 */
export async function staticTextUpdateOne(organizationId, record) {
  return request(`${HZERO_PLATFORM}/v1/${organizationId}/static-texts`, {
    method: 'PUT',
    body: record,
  });
}

/**
 * 分页查询静态文本分页
 * @param {number} organizationId
 * @param {number} textId - 静态文本id
 * @param {object} params
 * @return {Promise<Response>}
 */
export async function staticTextFetchOne(organizationId, textId, params) {
  return request(`${HZERO_PLATFORM}/v1/${organizationId}/static-texts/details/${textId}`, {
    method: 'GET',
    query: params,
  });
}

/**
 * 分页查询静态文本分页
 * @param {number} organizationId
 * @param {object} params
 * @return {Promise<Response>}
 */
export async function staticTextFetchList(organizationId, params) {
  const parsedParams = parseParameters(params);
  return request(`${HZERO_PLATFORM}/v1/${organizationId}/static-texts`, {
    method: 'GET',
    query: parsedParams,
  });
}
