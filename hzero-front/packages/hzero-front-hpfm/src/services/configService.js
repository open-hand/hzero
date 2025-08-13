/**
 * configService.js - 系统配置 service
 * @date: 2018-10-29
 * @author: geekrainy <chao.zheng02@hand-china.com>
 * @version: 0.0.1
 * @copyright: Copyright (c) 2018, Hand
 */

import request from 'utils/request';
import { HZERO_PLATFORM } from 'utils/config';

/**
 * 根据平台级系统配置
 * @param {Number} tenantId 租户 ID
 */
export async function queryTenantConfig(organizationId) {
  return request(`${HZERO_PLATFORM}/v1/config/${organizationId}`);
}

/**
 * 根据平台级系统配置
 * @param {Object} params 租户 ID
 */
export async function updateTenantConfig(params) {
  const { values, organizationId } = params;
  return request(`${HZERO_PLATFORM}/v1/config/${organizationId}`, {
    method: 'PUT',
    body: values,
  });
}

/**
 * 当前为租户下查询系统配置
 * @param {Number} tenantId 租户 ID
 */
export async function queryOrganizationConfig(organizationId) {
  return request(`${HZERO_PLATFORM}/v1/${organizationId}/config`);
}

/**
 * 当前为租户下更新系统配置
 * @param {Object} params 更新参数，包含租户 id
 */
export async function updateOrganizationConfig(params) {
  const { values, organizationId } = params;
  return request(`${HZERO_PLATFORM}/v1/${organizationId}/config`, {
    method: 'PUT',
    body: values,
  });
}
