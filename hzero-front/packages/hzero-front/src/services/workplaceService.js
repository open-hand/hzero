/* eslint-disable */
/**
 * service - 工作台配置
 * @date: 2018-9-25
 * @version: 1.0.0
 * @author: LZY <zhuyan.luo@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */

import request from 'utils/request';
import { getEnvConfig } from 'utils/iocUtils';
import { isTenantRoleLevel, getCurrentOrganizationId } from 'utils/utils';

const { HZERO_PLATFORM } = getEnvConfig();

/**
 * 请求API前缀
 * @type {string}
 */
const prefix = `${HZERO_PLATFORM}/v1`;

/**
 * 查询当前布局
 * @param {Object} params - 查询参数
 */
export async function workplaceLayoutQuery(params) {
  return request(`${prefix}/dashboard/layout`, {
    method: 'GET',
    query: params,
  });
}
/**
 * 保存当前布局
 * @param {Object} params - 需保存的数据
 */
export async function workplaceLayoutUpdate(params) {
  return request(`${prefix}/dashboard/layout`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 查询当前角色 所有的卡片
 */
export async function workplaceCardsQuery(params) {
  return request(`${prefix}/dashboard/layout/role-cards`, {
    method: 'GET',
    query: params,
  });
}
