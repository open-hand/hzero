/**
 * payConfigService
 * @author wangjiacheng <jiacheng.wang@hand-china.com>
 * @date 2019-06-13
 * @copyright 2019-05-28 © HAND
 */
import request from 'utils/request';
import { HZERO_HPAY } from 'utils/config';
import { getCurrentOrganizationId } from 'utils/utils';

const organizationId = getCurrentOrganizationId();

/**
 * 查询列表
 * @async
 * @function fetchConfigList
 * @param {Object} params - 查询参数
 */
export async function fetchConfigList(params) {
  return request(`${HZERO_HPAY}/v1/${organizationId}/configs`, {
    method: 'GET',
    query: params,
  });
}

/**
 * 查询明细
 * @async
 * @function fetchConfigDetail
 * @param {Object} params - 查询参数
 */
export async function fetchConfigDetail(params) {
  return request(`${HZERO_HPAY}/v1/${organizationId}/configs/${params.configId}`, {
    method: 'GET',
    query: params,
  });
}

/**
 * 创建
 * @async
 * @function createConfig
 * @param {Object} params - 查询参数
 */
export async function createConfig(params) {
  return request(`${HZERO_HPAY}/v1/${organizationId}/configs`, {
    method: 'POST',
    body: { tenantId: organizationId, ...params },
  });
}

/**
 * 更新
 * @async
 * @function updateConfig
 * @param {Object} params - 查询参数
 */
export async function updateConfig(params) {
  return request(`${HZERO_HPAY}/v1/${organizationId}/configs`, {
    method: 'PUT',
    body: { tenantId: organizationId, ...params },
  });
}

/**
 * 删除
 * @async
 * @function deleteConfig
 * @param {Object} params - 查询参数
 */
export async function deleteConfig(params) {
  return request(`${HZERO_HPAY}/v1/${organizationId}/configs/${params.configId}`, {
    method: 'DELETE',
    body: params,
  });
}

/**
 * 查询证书
 * @async
 * @function fetchCert
 * @param {Object} params - 查询参数
 */
export async function fetchCert(params) {
  return request(`${HZERO_HPAY}/v1/${organizationId}/configs/query-cert`, {
    method: 'POST',
    query: params,
  });
}

/**
 * 删除证书
 * @async
 * @function deleteCert
 * @param {Object} params - 查询参数
 */
export async function deleteCert(params) {
  return request(`${HZERO_HPAY}/v1/${organizationId}/configs/clear-cert`, {
    method: 'POST',
    body: params,
  });
}
