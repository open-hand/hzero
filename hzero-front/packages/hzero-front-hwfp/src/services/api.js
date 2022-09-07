import { HZERO_HWFP } from 'utils/config';
import request from 'utils/request';
import { getCurrentOrganizationId } from 'utils/utils';

const tenantId = getCurrentOrganizationId();

/**
 * 获取审批历史列表
 */
export async function fetchHistoryList(params) {
  return request(`${HZERO_HWFP}/v1/${tenantId}/process/instance/history`, {
    method: 'GET',
    query: params,
  });
}
