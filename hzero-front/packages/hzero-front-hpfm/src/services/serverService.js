/**
 * 服务器管理
 * @date: 2019-7-9
 * @author:  xl <liang.xiong@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import request from 'utils/request';
import { HZERO_PLATFORM } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';

const tenantId = getCurrentOrganizationId();
function server() {
  return isTenantRoleLevel()
    ? `${tenantId}/servers/clusters/cluster-id`
    : 'servers/clusters/cluster-id';
}
function canAssign() {
  return isTenantRoleLevel() ? `${tenantId}/servers/can-assign` : 'servers/can-assign';
}
function serverAssign() {
  return isTenantRoleLevel() ? `${tenantId}/server-assigns` : 'server-assigns';
}
/**
 * 根据clusterid查询带有cluster信息服务器信息
 * @param {Object} params - 参数
 */
export async function fetchServerList(params) {
  const { clusterId } = params;
  return request(`${HZERO_PLATFORM}/v1/${server()}/${clusterId}`, {
    method: 'GET',
    query: params,
  });
}
/**
 * 查询可分配的服务器信息
 * @param {Object} params - 参数
 */
export async function fetchCanAssignList(params) {
  const { clusterId } = params;
  return request(`${HZERO_PLATFORM}/v1/${canAssign()}/${clusterId}`, {
    method: 'GET',
    query: params,
  });
}
/**
 * 删除服务器集群分配信息
 * @param {Object} params - 参数
 */
export async function deleteServerAssign(params) {
  return request(`${HZERO_PLATFORM}/v1/${serverAssign()}`, {
    method: 'DELETE',
    body: params,
  });
}

/**
 * 创建服务器集群分配信息
 * @param {Object} params - 参数
 */
export async function createServerAssign(params) {
  return request(`${HZERO_PLATFORM}/v1/${serverAssign()}`, {
    method: 'POST',
    body: params,
  });
}
