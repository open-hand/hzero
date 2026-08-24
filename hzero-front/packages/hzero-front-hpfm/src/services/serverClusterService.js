/**
 * 服务器集群管理
 * @date: 2019-7-9
 * @author:  xl <liang.xiong@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import request from 'utils/request';
import { HZERO_PLATFORM } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';

const tenantId = getCurrentOrganizationId();
function serverCluster() {
  return isTenantRoleLevel() ? `${tenantId}/server-clusters` : 'server-clusters';
}
/**
 * 删除服务器集群设置
 * @param {Object} params - 参数
 */
export async function deleteServerCluster(params) {
  return request(`${HZERO_PLATFORM}/v1/${serverCluster()}`, {
    method: 'DELETE',
    body: params,
  });
}

/**
 * 查询服务器集群列表
 * @param {Object} params - 查询参数
 */
export async function fetchServerClusterList(params) {
  return request(`${HZERO_PLATFORM}/v1/${serverCluster()}`, {
    method: 'GET',
    query: params,
  });
}

/**
 * 创建服务器集群
 * @param {Object} params - 参数
 */
export async function createServerCluster(params) {
  return request(`${HZERO_PLATFORM}/v1/${serverCluster()}`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 修改服务器集群设置
 * @param {Object} params - 参数
 */
export async function updateServerCluster(params) {
  return request(`${HZERO_PLATFORM}/v1/${serverCluster()}`, {
    method: 'PUT',
    body: params,
  });
}

/**
 * 查询服务器集群明细
 * @param {Object} params - 查询参数
 */
export async function getServerClusterDetail(params) {
  const { clusterId } = params;
  return request(`${HZERO_PLATFORM}/v1/${serverCluster()}/${clusterId}`, {
    method: 'GET',
  });
}
