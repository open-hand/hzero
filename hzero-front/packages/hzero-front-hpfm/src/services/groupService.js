import request from 'utils/request';
import { HZERO_PLATFORM } from 'utils/config';

/**
 * 查询集团信息
 * @param {Object} params - 查询参数
 * @param {String} params.organizationId - 组织ID
 */
export async function fetchGroup(params) {
  return request(`${HZERO_PLATFORM}/v1/${params.organizationId}/groups/self`, {
    method: 'GET',
  });
}

/**
 * 更新集团信息
 * @param {Object} params - 查询参数
 * @param {String} params.organizationId - 组织ID
 */
export async function updateGroup(params) {
  return request(`${HZERO_PLATFORM}/v1/${params.organizationId}/groups`, {
    method: 'POST',
    body: params,
  });
}
