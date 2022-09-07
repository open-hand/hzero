/**
 * ManualInspection 手工发票查验
 * @date: 2019-8-25
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */

import request from 'utils/request';
import { HZERO_INVOICE, HZERO_HFLE } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';

const organizationId = getCurrentOrganizationId();
/**
 * 创建配置
 * @async
 * @function createConfig
 * @param {Object} params - 查询参数
 */
export async function create(params) {
  return request(`${HZERO_INVOICE}/v1/${organizationId}/invoice/check`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 获取配置明细列表
 * @async
 * @function getConfigDetail
 * @param {Object} params - 查询参数
 */
export async function getDetail(params) {
  return request(
    `${HZERO_INVOICE}/v1${
      isTenantRoleLevel() ? `/${organizationId}` : ''
    }/check-hist-results/batch`,
    {
      method: 'GET',
      query: params,
    }
  );
}

/**
 * 获取配置明细列表
 * @async
 * @function getConfigDetail
 * @param {Object} params - 查询参数
 */
export async function redirect(params) {
  return request(
    `${HZERO_HFLE}/v1${isTenantRoleLevel() ? `/${organizationId}` : ''}/files/redirect-url`,
    {
      method: 'GET',
      query: params,
      responseType: 'blob',
    }
  );
}
