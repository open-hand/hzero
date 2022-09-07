/*
 * @Descripttion:事件类型定义 - service
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

function categoryApi() {
  return isTenantRoleLevel() ? `${organizationId}/event-category` : `event-category`;
}

/**
 * 查询事件类型
 * @export
 * @param {object} params 查询参数
 * @returns
 */

export async function fetchEventList(params) {
  return request(`${API_PREFIX}/v1/${categoryApi()}`, {
    method: 'GET',
    query: params,
  });
}

/**
 * 新增事件类型
 * @export
 * @param {object} params 请求参数
 * @returns
 */
export async function createEvent(params) {
  return request(`${API_PREFIX}/v1/${categoryApi()}`, {
    method: 'POST',
    body: params,
  });
}
