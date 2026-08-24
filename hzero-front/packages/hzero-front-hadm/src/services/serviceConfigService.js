/**
 * serviceConfig - 配置管理-服务配置
 * @date: 2019-1-23
 * @author: wangjiacheng <jiacheng.wang@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import request from 'utils/request';
import { HZERO_ADM } from 'utils/config';
import { isTenantRoleLevel, getCurrentOrganizationId } from 'utils/utils';

const tenantId = getCurrentOrganizationId();

function configApi() {
  return isTenantRoleLevel() ? `${tenantId}/configs` : `configs`;
}

/**
 * 查询列表数据
 * @async
 * @function fetchServiceConfigList
 * @param {Object} params - 查询参数
 * @param {String} [params.page = 0] - 页码
 * @param {String} [params.size = 0] - 页数
 */
export async function fetchServiceConfigList(params) {
  return request(`${HZERO_ADM}/v1/${configApi()}`, {
    method: 'GET',
    query: params,
  });
}

/**
 * 查询详细数据
 * @async
 * @function fetchServiceConfigList
 * @param {Object} params - 查询参数
 */
export async function fetchServiceConfigDetail(params) {
  return request(`${HZERO_ADM}/v1/${configApi()}/${params.serviceConfigId}`, {
    method: 'GET',
  });
}

/**
 * 创建
 * @async
 * @function createServiceConfig
 * @param {String} params.enabledFlag - 是否启用
 * @param {String} params.groupNum - 集团编码
 */
export async function createServiceConfig(params) {
  return request(`${HZERO_ADM}/v1/${configApi()}`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 更新
 * @async
 * @function updateServiceConfig
 * @param {Object} params - 查询参数
 */
export async function updateServiceConfig(params) {
  return request(`${HZERO_ADM}/v1/${configApi()}`, {
    method: 'PUT',
    body: params,
  });
}

/**
 * 删除
 * @async
 * @function deleteServiceConfig
 * @param {Object} params - 查询参数
 */
export async function deleteServiceConfig(params) {
  return request(`${HZERO_ADM}/v1/${configApi()}`, {
    method: 'DELETE',
    body: params,
  });
}

/**
 * 刷新服务路由
 * @async
 * @function refreshServiceConfig
 * @param {Object} params - 查询参数
 */
export async function refreshServiceConfig(params) {
  return request(`${HZERO_ADM}/v1/refresh/config`, {
    method: 'POST',
    query: params,
  });
}
