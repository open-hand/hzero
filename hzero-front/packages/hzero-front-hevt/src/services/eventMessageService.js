/*
 * @Descripttion: 事件查询 - service
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

function messageApi() {
  return isTenantRoleLevel() ? `${organizationId}/` : ``;
}

/**
 * 查询事件消息
 * @export
 * @param {object} params 查询参数
 * @returns
 */
export async function queryMessageList(params) {
  return request(`${API_PREFIX}/v1/${messageApi()}event-messages`, {
    method: 'GET',
    query: params,
  });
}

/**
 * 重试
 * @export
 * @param {object} params 请求参数
 * @returns
 */
export async function resendMessage(params) {
  return request(`${API_PREFIX}/v1/${messageApi()}event-messages`, {
    method: 'PUT',
    body: params,
  });
}
