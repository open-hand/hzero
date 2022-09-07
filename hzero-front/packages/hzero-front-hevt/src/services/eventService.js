/*
 * @Descripttion: 平台事件定义 - service
 * @version: 0.0.1
 * @Author: heqiheng <qiheng.he@hand-china.com>
 * @Date: 2020-03-27 13:38:04
 * @Copyright: Copyright (c) 2020, Hand
 */

import request from 'utils/request';
// import { SRM_PLATFORM } from '_utils/config';
import { isTenantRoleLevel, getCurrentOrganizationId } from 'utils/utils';
import { API_PREFIX } from '@/utils/constants';

const organizationId = getCurrentOrganizationId();

function eventApi() {
  return isTenantRoleLevel() ? `${organizationId}/event` : `event`;
}

/**
 * 查询平台事件
 * @export
 * @param {object} params 查询参数
 * @returns
 */
export async function fetchEvent(params) {
  return request(`${API_PREFIX}/v1/${eventApi()}`, {
    method: 'GET',
    query: params,
  });
}

/**
 * 新增平台事件
 * @export
 * @param {object} params 请求参数
 * @returns
 */
export async function createEvent(params) {
  return request(`${API_PREFIX}/v1/${eventApi()}`, {
    method: 'POST',
    body: params,
  });
}
