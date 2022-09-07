/**
 * service - 流程转交
 */

import request from 'utils/request';
import { HZERO_HWFP } from 'utils/config';
import { parseParameters, filterNullValueObject, getCurrentOrganizationId } from 'utils/utils';

const tenantId = getCurrentOrganizationId();

// 查询流程
export async function queryProcess(params) {
  const query = filterNullValueObject(parseParameters(params));
  return request(`${HZERO_HWFP}/v1/${tenantId}/task/delegate`, {
    query,
  });
}

// 流程转交
export async function delegateProcess(params) {
  return request(`${HZERO_HWFP}/v1/${tenantId}/task/delegate`, {
    method: 'POST',
    body: params,
  });
}
