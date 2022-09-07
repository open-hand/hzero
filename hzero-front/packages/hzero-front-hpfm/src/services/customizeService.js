/**
 * customize - API 个性化 Service
 * @date: 2019-7-8
 * @author: jiacheng.wang <jiacheng.wang@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2019, Hand
 */
import request from 'utils/request';
import { HZERO_PLATFORM } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';

const isSiteFlag = !isTenantRoleLevel();
const tenantId = getCurrentOrganizationId();
const pointPrefix = isSiteFlag ? `/v1/customize-points` : `/v1/${tenantId}/customize-points`;
const rulePrefix = isSiteFlag ? `/v1/customize-rules` : `/v1/${tenantId}/customize-rules`;
const rangePrefix = isSiteFlag ? `/v1/customize-ranges` : `/v1/${tenantId}/customize-ranges`;

/**
 * 查询个性化切入点列表
 * @param {Object} params - 查询参数
 * @param {String} params.page - 页码
 * @param {String} params.size - 页数
 */
export async function fetchPointList(params) {
  return request(`${HZERO_PLATFORM}${pointPrefix}`, {
    method: 'GET',
    query: params,
  });
}

/**
 * 刷新服务
 * refreshPoint
 * @param {Object} params - 查询参数
 */
export async function refreshPoint(params) {
  return request(`${HZERO_PLATFORM}${pointPrefix}/refresh`, {
    method: 'POST',
    query: params,
  });
}

/**
 * 删除
 * deletePoint
 * @param {Object} params - 查询参数
 */
export async function deletePoint(params) {
  return request(`${HZERO_PLATFORM}${pointPrefix}`, {
    method: 'DELETE',
    body: params,
  });
}

/**
 * 查询个性化规则列表
 * @param {Object} params - 查询参数
 * @param {String} params.page - 页码
 * @param {String} params.size - 页数
 */
export async function fetchRuleList(params) {
  return request(`${HZERO_PLATFORM}${rulePrefix}`, {
    method: 'GET',
    query: params,
  });
}

/**
 * 查询个性化规则明细
 * @param {Object} params - 查询参数
 */
export async function fetchRuleDetail(params) {
  return request(`${HZERO_PLATFORM}${rulePrefix}/${params.ruleId}`, {
    method: 'GET',
    query: params,
  });
}

/**
 *  创建
 * createRule
 * @param {Object} params - 查询参数
 */
export async function createRule(params) {
  return request(`${HZERO_PLATFORM}${rulePrefix}`, {
    method: 'POST',
    body: params,
  });
}

/**
 *  更新
 * updateRule
 * @param {Object} params - 查询参数
 */
export async function updateRule(params) {
  return request(`${HZERO_PLATFORM}${rulePrefix}`, {
    method: 'PUT',
    body: params,
  });
}

/**
 *  删除
 * deleteRule
 * @param {Object} params - 查询参数
 */
export async function deleteRule(params) {
  return request(`${HZERO_PLATFORM}${rulePrefix}`, {
    method: 'DELETE',
    body: params,
  });
}

/**
 * 查询个性化范围列表
 * fetchRangeList
 * @param {Object} params - 查询参数
 */
export async function fetchRangeList(params) {
  return request(`${HZERO_PLATFORM}${rangePrefix}`, {
    method: 'GET',
    query: params,
  });
}

/**
 * fetchRangeDetail
 * 查询个性化范围明细
 * @param {Object} params - 查询参数
 */
export async function fetchRangeDetail(params) {
  return request(`${HZERO_PLATFORM}${rangePrefix}/${params.rangeId}`, {
    method: 'GET',
    query: params,
  });
}

/**
 *  创建
 * createRange
 * @param {Object} params - 查询参数
 */
export async function createRange(params) {
  return request(`${HZERO_PLATFORM}${rangePrefix}`, {
    method: 'POST',
    body: params,
  });
}

/**
 *  更新
 * updateRange
 * @param {Object} params - 查询参数
 */
export async function updateRange(params) {
  return request(`${HZERO_PLATFORM}${rangePrefix}`, {
    method: 'PUT',
    body: params,
  });
}

/**
 *  删除
 * deleteRange
 * @param {Object} params - 查询参数
 */
export async function deleteRange(params) {
  return request(`${HZERO_PLATFORM}${rangePrefix}`, {
    method: 'DELETE',
    body: params,
  });
}

/**
 * 查询个性化范围-切入点
 * fetchPointListToRange
 * @param {Object} params - 查询参数
 */
export async function fetchPointListToRange(params) {
  return request(`${HZERO_PLATFORM}${rangePrefix}/${params.rangeId}/points`, {
    method: 'GET',
    query: params,
  });
}

/**
 * 查询个性化范围-规则
 * fetchPointListToRule
 * @param {Object} params - 查询参数
 */
export async function fetchRuleListToRange(params) {
  return request(`${HZERO_PLATFORM}${rangePrefix}/${params.rangeId}/rules`, {
    method: 'GET',
    query: params,
  });
}

/**
 * 删除切入点
 *  deletePointToRange
 * @param {Object} params - 查询参数
 */
export async function deletePointToRange(params) {
  return request(`${HZERO_PLATFORM}${rangePrefix}/${params.rangeId}/points`, {
    method: 'DELETE',
    body: params.list,
  });
}

/**
 * 删除规则
 *  deletePointToRange
 * @param {Object} params - 查询参数
 */
export async function deleteRuleToRange(params) {
  return request(`${HZERO_PLATFORM}${rangePrefix}/${params.rangeId}/rules`, {
    method: 'DELETE',
    body: params.list,
  });
}

/**
 * 应用
 *  applyRules
 * @param {Object} params - 查询参数
 */
export async function applyRules(params) {
  return request(`${HZERO_PLATFORM}${rangePrefix}/${params.rangeId}/apply-rules`, {
    method: 'GET',
  });
}
