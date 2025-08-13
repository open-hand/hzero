/**
 * 客户化管理 - service
 * @since 2019-12-18
 * @author LiLin <lin.li03@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import { HZERO_IAM } from 'utils/config';
import request from 'utils/request';

/**
 * 批量分配API
 * @async
 * @function distributeApi
 * @param {object} params - 分配参数
 */
export async function distributeApi(params) {
  return request(`${HZERO_IAM}/v1/tenant-custom/points/assign`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 删除客户化
 * @async
 * @function deleteBatchCustomer
 * @param {object} params - 删除项
 */
export async function deleteBatchCustomer(params) {
  const url = `${HZERO_IAM}/v1/tenant-custom/points`;
  return request(url, {
    method: 'DELETE',
    body: params,
  });
}

/**
 * 删除租户客户化
 * @async
 * @function deleteTenantCustomer
 * @param {object} params - 删除项
 */
export async function deleteTenantCustomer(params) {
  const { customPoints } = params;
  const url = `${HZERO_IAM}/v1/tenant-custom/points/revoke`;
  return request(url, {
    method: 'DELETE',
    body: customPoints,
  });
}
