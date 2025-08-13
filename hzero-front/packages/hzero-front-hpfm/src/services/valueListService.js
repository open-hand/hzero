/**
 * valueListService.js - 值集配置 service
 * @date: 2018-10-29
 * @author: geekrainy <chao.zheng02@hand-china.com>
 * @version: 0.0.1
 * @copyright: Copyright (c) 2018, Hand
 */

import request from 'utils/request';
import { HZERO_PLATFORM } from 'utils/config';
import { parseParameters, isTenantRoleLevel, getCurrentOrganizationId } from 'utils/utils';

const tenantId = getCurrentOrganizationId();
function urlPrefix(params) {
  return `${HZERO_PLATFORM}/v1${isTenantRoleLevel() ? `/${params.tenantId}/` : '/'}`;
}

/**
 * 条件查询值集头
 * @param {Object} params - 查询参数
 */
export async function queryLovHeadersList(params) {
  const query = parseParameters(params);
  return request(`${urlPrefix(params)}lov-headers`, {
    method: 'GET',
    query,
  });
}

/**
 * 批量更新值集头
 * @param {Array} params - 更新值集头
 */
export async function updateLovHeaders(params) {
  return request(`${urlPrefix(params)}lov-headers`, {
    method: 'PUT',
    body: params,
  });
}

/**
 * 批量插入值集头
 * @param {Array} params - 插入值集头
 */
export async function insertLovHeaders(params) {
  return request(`${urlPrefix(params)}lov-headers`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 批量删除值集头
 * @param {Array} params - 删除值集头
 */
export async function deleteLovHeaders(params) {
  return request(`${urlPrefix({ tenantId })}lov-headers/delete`, {
    method: 'DELETE',
    body: params,
  });
}

/**
 * 批量更新值集值
 * @param {Array} params - 更新值集值
 */
export async function updateLovValues(params) {
  return request(`${urlPrefix(params)}lov-values`, {
    method: 'PUT',
    body: params,
  });
}

/**
 * 批量插入值集值
 * @param {Array} params - 插入值集值
 */
export async function insertLovValues(params) {
  return request(`${urlPrefix(params)}lov-values`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 批量删除值集值
 * @param {Array} params - 删除值集值
 */
export async function deleteLovValues(params) {
  const { deleteRows } = params;
  return request(`${urlPrefix(params)}lov-values`, {
    method: 'DELETE',
    body: deleteRows,
  });
}

/**
 * 根据值集头 ID 查询值集值
 * @param {Object} params - 查询参数
 */
export async function queryLovValues(params) {
  const { lovId, ...queryProp } = params;
  return request(`${urlPrefix(params)}lov-headers/${lovId}/values`, {
    method: 'GET',
    query: queryProp,
  });
}

/**
 * 查询单个值集头
 * @param {Number} params - Lov Id
 */
export async function queryLovHeader(params) {
  return request(`${urlPrefix(params)}lov-headers/${params.lovId}`);
}

/**
 * 根据值集头 ID 查询值集值
 * @param {Object} params - 查询参数
 */
export async function copyLov(params) {
  return request(`${urlPrefix(params)}lov/copy`, {
    method: 'POST',
    body: params,
    query: params,
  });
}
