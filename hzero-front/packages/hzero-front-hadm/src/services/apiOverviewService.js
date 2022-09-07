import { HZERO_ADM } from 'utils/config';
import request from 'utils/request';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';

const organizationId = getCurrentOrganizationId();
const apiPrefix = isTenantRoleLevel() ? `${HZERO_ADM}/v1/${organizationId}` : `${HZERO_ADM}/v1`;

/**
 *
 * 获取服务api总数
 * @export
 * @param {*} params
 * @returns
 */
export async function fetchApiCount() {
  return request(`${apiPrefix}/statistics/access/api/count`, {
    method: 'GET',
  });
}

/**
 *
 * 获取服务api调用总数
 * @export
 * @param {*} params
 * @returns
 */
export async function fetchApiInvokeCount(params) {
  return request(`${apiPrefix}/statistics/access/service-invoke/count`, {
    method: 'GET',
    query: params,
  });
}
/**
 *
 * 获取单个服务api调用总数
 * @export
 * @param {*} params
 * @returns
 */
export async function fetchSingleApiInvokeCount(params) {
  return request(`${apiPrefix}/statistics/access/api-invoke/count`, {
    method: 'GET',
    query: params,
  });
}

/**
 *
 * 获取单个服务api调用总数
 * @export
 * @param {*} params
 * @returns
 */
export async function fetchMicroservice() {
  return request(`${apiPrefix}/swagger/resource`, {
    method: 'GET',
    // query: params,
  });
}
