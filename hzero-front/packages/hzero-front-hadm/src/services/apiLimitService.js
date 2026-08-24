/**
 * @since 2019-12-18
 * @author WT <tao13.wang@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import { HZERO_ADM } from 'utils/config';
import request from 'utils/request';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';

const organizationId = getCurrentOrganizationId();

/**
 * 批量分配API
 * @async
 * @function applyAPI
 * @param {object} params - 分配参数
 */
export async function applyAPI(params) {
  const url = isTenantRoleLevel()
    ? `${HZERO_ADM}/v1/${organizationId}/api-monitor-rules/apply?monitorRuleIds=${params}`
    : `${HZERO_ADM}/v1/api-monitor-rules/apply?monitorRuleIds=${params}`;
  return request(url, {
    method: 'PUT',
  });
}

/**
 * 将API加入黑名单
 * @async
 * @function distributeApi
 * @param {object} params - 分配参数
 */
export async function addTOBlackList(params) {
  const { monitorKey, monitorRuleId } = params;
  const url = isTenantRoleLevel()
    ? `${HZERO_ADM}/v1/${organizationId}/api-monitors?ip=${monitorKey}&monitorRuleId=${monitorRuleId}`
    : `${HZERO_ADM}/v1/api-monitors?ip=${monitorKey}&monitorRuleId=${monitorRuleId}`;
  return request(url, {
    method: 'POST',
  });
}

/**
 * 流量控制(创建或者更新接口限制)
 * @async
 * @function distributeApi
 * @param {object} params - 分配参数
 */
export async function flowLimit(params) {
  const url = isTenantRoleLevel()
    ? `${HZERO_ADM}/v1/${organizationId}/api-limits`
    : `${HZERO_ADM}/v1/api-limits`;
  return request(url, {
    method: 'POST',
    body: params,
  });
}
