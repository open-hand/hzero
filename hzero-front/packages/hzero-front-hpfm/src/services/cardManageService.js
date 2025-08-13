/**
 * 卡片管理
 * @date 2019-01-24
 * @author WY yang.wang06@hand-china.com
 * @copyright © HAND 2019
 */

import { HZERO_PLATFORM } from 'utils/config';
import request from 'utils/request';
import { parseParameters, getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';

/**
 * @typedef {object} Card - 卡片
 * @property {!string} code - 卡片编码
 * @property {!string} name -卡片名称
 * @property {string} description - 卡片描述
 * @property {!string} catalogType - 卡片类型
 * @property {!string} level - 层级
 * @property {1|2|3|4|5|6|7|8|9|10|11|12} w - 卡片宽度
 * @property {number} h - 卡片高度(1到200)
 * @property {boolean} [enabled=true] - 启用
 */

/**
 * @typedef {object} Tenant - 租户/集团
 * @property {!number} cardId - 卡片id
 * @property {number} id - 主键
 * @property {!number} tenantId - 租户id
 // * @property {string} tenantName - 租户名称
 // * @property {string} tenantNum - 租户编码
 */

function requestPreUrl() {
  return `${HZERO_PLATFORM}/v1${isTenantRoleLevel() ? `/${getCurrentOrganizationId()}` : ''}`;
}

/**
 * 查询卡片信息
 * @param {object} params - 带分页信息的查询信息
 * @param {object} params.page - 分页信息
 * @param {object} params.sort - 排序信息
 * @param {string} params.code - 卡片编码
 * @param {string} params.name - 卡片名称
 * @param {string} params.fdLevel - 层级
 * @requestUrl {HZERO_PLATFORM}/v1/dashboard/card
 * @requestUrl {HZERO_PLATFORM}/v1/{organizationId}/dashboard/card
 * @requestMethod GET
 */
export async function cardQueryPage(params) {
  const parseParams = parseParameters(params);
  return request(`${requestPreUrl()}/dashboard/card`, {
    method: 'GET',
    query: parseParams,
  });
}

/**
 * 新建卡片
 * @param {Card} params
 * @requestUrl {HZERO_PLATFORM}/v1/dashboard/card
 * @requestUrl {HZERO_PLATFORM}/v1/{organizationId}/dashboard/card
 * @requestMethod POST
 */
export async function cardInsert(params) {
  return request(`${requestPreUrl()}/dashboard/card`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 更新卡片
 * @param {Card} params
 * @requestUrl {HZERO_PLATFORM}/v1/dashboard/card
 * @requestUrl {HZERO_PLATFORM}/v1/{organizationId}/dashboard/card
 * @requestMethod PUT
 */
export async function cardUpdate(params) {
  return request(`${requestPreUrl()}/dashboard/card`, {
    method: 'PUT',
    body: params,
  });
}

/**
 * 获取卡片详情
 * @param {Card} params
 * @requestUrl {HZERO_PLATFORM}/v1/dashboard/card/details/{dashboardCardId}
 * @requestUrl {HZERO_PLATFORM}/v1/{organizationId}/dashboard/card/details/{dashboardCardId}
 * @requestMethod PUT
 */
export async function cardDetails(params) {
  const { dashboardCardId } = params;
  return request(`${requestPreUrl()}/dashboard/card/details/${dashboardCardId}`, {
    method: 'GET',
  });
}

/**
 * 查询卡片 已经分配的租户信息
 * @param {object} params - 带分页信息的查询信息
 * @param {object} params.page - 分页信息
 * @param {object} params.sort - 排序信息
 * @param {string} params.tenantName - 租户名称
 * @param {string} params.beginDate - 注册时间从
 * @param {string} params.endDate - 注册时间至
 * @requestUrl {HZERO_PLATFORM}/v1/dashboard-tenant-cards/${cardId}
 * @requestUrl {HZERO_PLATFORM}/v1/{organizationId}/dashboard-tenant-cards/${cardId}
 * @requestMethod GET
 */
export async function cardTenantQueryPage(params) {
  const { cardId, ...restParams } = params;
  const parseParams = parseParameters(restParams);
  return request(`${requestPreUrl()}/dashboard-tenant-cards/${cardId}`, {
    method: 'GET',
    query: parseParams,
  });
}

/**
 * 给卡片 分配新的租户
 * @param {Tenant[]} params - 租户信息
 * @requestUrl {HZERO_PLATFORM}/v1/dashboard-tenant-cards
 * @requestUrl {HZERO_PLATFORM}/v1/{organizationId}/dashboard-tenant-cards
 * @requestMethod POST
 */
export async function cardTenantInsert(params) {
  return request(`${requestPreUrl()}/dashboard-tenant-cards`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 删除 卡片分配新的租户
 * @param {Tenant[]} params - 租户信息
 * @requestUrl {HZERO_PLATFORM}/v1/dashboard-tenant-cards
 * @requestUrl {HZERO_PLATFORM}/v1/{organizationId}/dashboard-tenant-cards
 * @requestMethod DELETE
 */
export async function cardTenantDelete(params) {
  return request(`${requestPreUrl()}/dashboard-tenant-cards`, {
    method: 'DELETE',
    body: params,
  });
}
