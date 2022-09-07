/**
 * service: 服务器上传配置
 * @date: 2019-7-5
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */

import request from 'utils/request';
import { HZERO_FILE } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';

const organizationId = getCurrentOrganizationId();

/**
 * 查询列表
 * @async
 * @function queryConfig
 * @param {Object} params - 查询参数
 */
export async function queryConfig(params) {
  return request(
    `${HZERO_FILE}/v1/${isTenantRoleLevel() ? `${organizationId}/` : ''}server-configs`,
    {
      method: 'GET',
      query: params,
    }
  );
}

/**
 * 获取配置明细
 * @async
 * @function fetchConfigDetail
 * @param {Object} params - 查询参数
 */
export async function fetchConfigDetail(params) {
  const { configId } = params;
  return request(
    `${HZERO_FILE}/v1/${isTenantRoleLevel() ? `${organizationId}/` : ''}server-configs/${configId}`,
    {
      method: 'GET',
    }
  );
}
/**
 * 创建配置
 * @async
 * @function createConfig
 * @param {Object} params - 查询参数
 */
export async function createConfig(params) {
  return request(
    `${HZERO_FILE}/v1/${isTenantRoleLevel() ? `${organizationId}/` : ''}server-configs`,
    {
      method: 'POST',
      body: params,
    }
  );
}

/**
 * 保存配置
 * @async
 * @function saveConfig
 * @param {Object} params - 查询参数
 */
export async function saveConfig(params) {
  return request(
    `${HZERO_FILE}/v1/${isTenantRoleLevel() ? `${organizationId}/` : ''}server-configs`,
    {
      method: 'PUT',
      body: params,
    }
  );
}
