/**
 * @since 2019-12-27
 * @author wjc <jiacheng.wang@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import { getEnvConfig } from 'utils/iocUtils';
import request from 'utils/request';
// import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
const { HZERO_ADM } = getEnvConfig();
/**
 * 查询trace状态
 * @async
 * @param {object} params - 分配参数
 */
export async function getTraceStatus() {
  return request(`${HZERO_ADM}/v1/trace/status`, {
    method: 'GET',
  });
}

/**
 * 开始trace
 * @async
 * @param {object} params - 分配参数
 */
export async function startTrace() {
  return request(`${HZERO_ADM}/v1/trace/start`, {
    method: 'POST',
  });
}

/**
 * 结束trace
 * @async
 * @param {object} params - 分配参数
 */
export async function endTrace() {
  return request(`${HZERO_ADM}/v1/trace/end`, {
    method: 'POST',
  });
}
