/**
 * serviceRoute - 配置管理-服务路由
 * @date: 2019-1-23
 * @author: wangjiacheng <jiacheng.wang@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import request from 'utils/request';
import { HZERO_ADM } from 'utils/config';

import { isTenantRoleLevel, getCurrentOrganizationId } from 'utils/utils';

const tenantId = getCurrentOrganizationId();

function routeApi() {
  return isTenantRoleLevel() ? `${tenantId}/routes` : `routes`;
}

/**
 * 查询列表数据
 * @async
 * @function fetchServiceRouteList
 * @param {Object} params - 查询参数
 * @param {String} [params.page = 0] - 页码
 * @param {String} [params.size = 0] - 页数
 */
export async function fetchServiceRouteList(params) {
  return request(`${HZERO_ADM}/v1/${routeApi()}`, {
    method: 'GET',
    query: params,
  });
}

/**
 * 查询详细数据
 * @async
 * @function fetchServiceRouteList
 * @param {Object} params - 查询参数
 */
export async function fetchServiceRouteDetail(params) {
  return request(`${HZERO_ADM}/v1/${routeApi()}/${params.serviceRouteId}`, {
    method: 'GET',
  });
}

/**
 * 创建
 * @async
 * @function createServiceRoute
 * @param {String} params.enabledFlag - 是否启用
 * @param {String} params.groupNum - 集团编码
 */
export async function createServiceRoute(params) {
  return request(`${HZERO_ADM}/v1/${routeApi()}`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 更新
 * @async
 * @function updateServiceRoute
 * @param {Object} params - 查询参数
 */
export async function updateServiceRoute(params) {
  return request(`${HZERO_ADM}/v1/${routeApi()}`, {
    method: 'PUT',
    body: params,
  });
}

/**
 * 删除
 * @async
 * @function deleteServiceRoute
 * @param {Object} params - 查询参数
 */
export async function deleteServiceRoute(params) {
  return request(`${HZERO_ADM}/v1/${routeApi()}`, {
    method: 'DELETE',
    body: params,
  });
}

/**
 * 刷新服务路由
 * @async
 * @function refreshServiceRoute
 */
export async function refreshServiceRoute() {
  return request(`${HZERO_ADM}/v1/monitor/refresh-route`, {
    method: 'POST',
  });
}
