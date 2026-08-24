/*
 * zuulRateLimitService - 限流设置
 * @date: 2018/10/13 11:14:04
 * @author: HB <bin.huang02@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import { HZERO_ADM } from 'utils/config';
import request from 'utils/request';
import { getCurrentOrganizationId, isTenantRoleLevel, parseParameters } from 'utils/utils';

const tenantId = getCurrentOrganizationId();

function zuulApi() {
  return isTenantRoleLevel() ? `${tenantId}/gateway-rate-limits` : 'gateway-rate-limits';
}

function zuulLineApi() {
  return isTenantRoleLevel() ? `${tenantId}/gateway-rate-limit-lines` : 'gateway-rate-limit-lines';
}

/**
 * 查询限流规则列表
 */
export async function fetchRateLimitList(params) {
  return request(`${HZERO_ADM}/v1/${zuulApi()}`, {
    method: 'GET',
    query: parseParameters(params),
  });
}

/**
 * 添加限流规则
 */
export async function addRateLimit(params) {
  return request(`${HZERO_ADM}/v1/${zuulApi()}/create`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 查询限流规则头详情
 */
export async function fetchHeaderInformation(params) {
  const { page, size, rateLimitId } = params;
  return request(`${HZERO_ADM}/v1/${zuulApi()}/${rateLimitId}/lines`, {
    method: 'GET',
    query: { page, size },
  });
}

/**
 * 头行保存
 */
export async function detailSave(params) {
  return request(`${HZERO_ADM}/v1/${zuulApi()}/detail/save`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 查询行详情
 */
export async function fetchLineDetail(params) {
  const { rateLimitLineId } = params;
  return request(`${HZERO_ADM}/v1/${zuulLineApi()}/${rateLimitLineId}`, {
    method: 'GET',
  });
}

/**
 * 头删除
 */
export async function deleteHeaders(params) {
  return request(`${HZERO_ADM}/v1/${zuulApi()}/batch-delete`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 刷新头
 */
export async function refresh(params) {
  return request(`${HZERO_ADM}/v1/${zuulApi()}/config/refresh`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 行新增
 */
export async function insertLine(params) {
  return request(`${HZERO_ADM}/v1/${zuulLineApi()}`, {
    method: 'POST',
    body: params,
  });
}
/**
 * 行修改
 */
export async function updateLine(params) {
  return request(`${HZERO_ADM}/v1/${zuulLineApi()}`, {
    method: 'PUT',
    body: params,
  });
}
/**
 * 行删除
 */
export async function deleteLines(params) {
  return request(`${HZERO_ADM}/v1/${zuulLineApi()}/batch-delete`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 查询限流规则行
 */
export async function fetchLines(params) {
  return request(`${HZERO_ADM}/v1/${zuulLineApi()}`, {
    method: 'GET',
    query: parseParameters(params),
  });
}

/* 限流维度 */

/**
 * 查询限流维度信息 详情
 * @param {number} rateLimitDimId
 * @returns {Promise<void>}
 */
export async function queryDimensionConfigsDetail(rateLimitDimId) {
  const reqUrl = isTenantRoleLevel()
    ? `${HZERO_ADM}/v1/${tenantId}/dimension-configs/${rateLimitDimId}`
    : `${HZERO_ADM}/v1/dimension-configs/${rateLimitDimId}`;
  return request(reqUrl, {
    method: 'GET',
  });
}

/**
 * 查询 限流方式 对应的 维度信息列表(分页)
 * @param {number} rateLimitLineId - 限流配置id
 * @param {object} pagination - 分页信息
 * @returns {Promise<void>}
 */
export async function queryDimensionConfigs(rateLimitLineId, pagination) {
  const reqUrl = isTenantRoleLevel()
    ? `${HZERO_ADM}/v1/${tenantId}/dimension-configs`
    : `${HZERO_ADM}/v1/dimension-configs`;
  return request(reqUrl, {
    method: 'GET',
    query: {
      rateLimitLineId,
      ...parseParameters(pagination),
    },
  });
}

/**
 * 查询 限流方式 的维度是否允许修改
 * @param {number} rateLimitLineId - 限流配置id
 * @returns {Promise<void>}
 */
export async function queryGateWayRateLimitDimensionAllowChange(rateLimitLineId) {
  const reqUrl = isTenantRoleLevel()
    ? `${HZERO_ADM}/v1/${tenantId}/gateway-rate-limit-lines/if-allow-change`
    : `${HZERO_ADM}/v1/gateway-rate-limit-lines/if-allow-change`;
  return request(reqUrl, {
    method: 'GET',
    query: {
      rateLimitLineId,
    },
  });
}

/**
 * 删除维度信息(维度信息有唯一id, 所以不需要限流方式id) 不支持批量
 * @param {object} dimensionConfig - 维度信息
 * @returns {Promise<void>}
 */
export async function deleteDimensionConfigs(dimensionConfig) {
  const reqUrl = isTenantRoleLevel()
    ? `${HZERO_ADM}/v1/${tenantId}/dimension-configs`
    : `${HZERO_ADM}/v1/dimension-configs`;
  return request(reqUrl, {
    method: 'DELETE',
    body: dimensionConfig,
  });
}

/**
 * 更新维度信息(维度信息有唯一id, 所以不需要限流方式id) 不支持批量
 * @param {object} dimensionConfig - 维度信息
 * @returns {Promise<void>}
 */
export async function updateDimensionConfigs(dimensionConfig) {
  const reqUrl = isTenantRoleLevel()
    ? `${HZERO_ADM}/v1/${tenantId}/dimension-configs`
    : `${HZERO_ADM}/v1/dimension-configs`;
  return request(reqUrl, {
    method: 'PUT',
    body: dimensionConfig,
  });
}

/**
 * 新增维度信息(维度信息有唯一id, 所以不需要限流方式id) 不支持批量
 * @param {object} dimensionConfig - 维度信息
 * @returns {Promise<void>}
 */
export async function insertDimensionConfigs(dimensionConfig) {
  const reqUrl = isTenantRoleLevel()
    ? `${HZERO_ADM}/v1/${tenantId}/dimension-configs`
    : `${HZERO_ADM}/v1/dimension-configs`;
  return request(reqUrl, {
    method: 'POST',
    body: dimensionConfig,
  });
}
