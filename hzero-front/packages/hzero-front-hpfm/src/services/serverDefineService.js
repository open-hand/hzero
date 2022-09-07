/**
 * service: 服务器定义
 * @date: 2019-7-1
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
 * @function fetchServerList
 * @param {Object} params - 查询参数
 */
export async function fetchServerList(params) {
  return request(`${HZERO_PLATFORM}/v1/${isTenantRoleLevel() ? `${organizationId}/` : ''}servers`, {
    method: 'GET',
    query: params,
  });
}

/**
 * 获取服务器明细
 * @async
 * @function getServerDetail
 * @param {Object} params - 查询参数
 */
export async function getServerDetail(params) {
  const { serverId } = params;
  return request(
    `${HZERO_PLATFORM}/v1/${isTenantRoleLevel() ? `${organizationId}/` : ''}servers/${serverId}`,
    {
      method: 'GET',
    }
  );
}

/**
 * 新建服务器
 * @async
 * @function createServer
 * @param {Object} params - 查询参数
 */
export async function createServer(params) {
  return request(`${HZERO_PLATFORM}/v1/${isTenantRoleLevel() ? `${organizationId}/` : ''}servers`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 编辑服务器
 * @async
 * @function editServer
 * @param {Object} params - 查询参数
 */
export async function editServer(params) {
  return request(`${HZERO_PLATFORM}/v1/${isTenantRoleLevel() ? `${organizationId}/` : ''}servers`, {
    method: 'PUT',
    body: params,
  });
}

/**
 * 删除服务器
 * @async
 * @function deleteServer
 * @param {Object} params - 查询参数
 */
export async function deleteServer(params) {
  return request(`${HZERO_PLATFORM}/v1/${isTenantRoleLevel() ? `${organizationId}/` : ''}servers`, {
    method: 'DELETE',
    body: params,
  });
}

/**
 * 重置密码
 * @async
 * @function resetPssword
 * @param {Object} params - 查询参数
 */
export async function resetPssword(params) {
  return request(`${HZERO_PLATFORM}/v1/${isTenantRoleLevel() ? `${organizationId}/` : ''}servers`, {
    method: 'PUT',
    body: params,
  });
}

/**
 * 获取服务器集群
 * @async
 * @function getServerCluster
 * @param {Object} params - 查询参数
 */
export async function getServerCluster(params) {
  const { serverId } = params;
  return request(
    `${HZERO_PLATFORM}/v1/${
      isTenantRoleLevel() ? `${organizationId}/` : ''
    }servers/clusters/server-id/${serverId}`,
    {
      method: 'GET',
    }
  );
}
