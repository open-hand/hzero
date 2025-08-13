/**
 * service: 二级域名单点登录配置
 * @date: 2019-6-27
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */

import request from 'utils/request';
import { HZERO_PLATFORM } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';

const organizationId = getCurrentOrganizationId();

/**
 * 查询列表
 * @async
 * @function query
 * @param {Object} params - 查询参数
 */
export async function query(params) {
  return request(
    `${HZERO_PLATFORM}/v1/${isTenantRoleLevel() ? `${organizationId}/` : ''}export-task`,
    {
      method: 'GET',
      query: params,
    }
  );
}

/**
 * 查询列表
 * @async
 * @function query
 * @param {Object} params - 查询参数
 */
export async function cancel(params) {
  return request(
    `${HZERO_PLATFORM}/v1/${isTenantRoleLevel() ? `${organizationId}/` : ''}export-task/cancel`,
    {
      method: 'PUT',
      query: params,
    }
  );
}

/**
 * 查询列表
 * @async
 * @function query
 * @param {Object} params - 查询参数
 */
export async function download(params) {
  const { downloadUrl, taskName } = params;
  return request(
    `${HZERO_PLATFORM}/v1/${isTenantRoleLevel() ? `${organizationId}/` : ''}export-task/download`,
    {
      method: 'POST',
      query: { downloadUrl, fileName: taskName },
    }
  );
}
