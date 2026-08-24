/**
 * expertService.js - 工作台系统消息卡片 service
 * @date: 2019-08-28
 * @author: LiLin <lin.li03@hand-china.com>
 * @version: 0.0.1
 * @copyright: Copyright (c) 2019, Hand
 */

import request from 'utils/request';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import { HZERO_MSG } from 'utils/config';

const organizationId = getCurrentOrganizationId();
function message() {
  return isTenantRoleLevel() ? `${organizationId}/` : '';
}
/**
 *
 * 系统消息查询
 * @param {Object} params 查询参数
 * @export
 * @returns
 */
export async function queryUserMessage(params) {
  return request(`${HZERO_MSG}/v1/${organizationId}/messages/user`, {
    method: 'GET',
    query: params,
  });
}

/**
 * 查询公告列表数据
 * @param {Object} params - 查询参数
 */
export async function queryAnnouncement(params) {
  return request(`${HZERO_MSG}/v1/${message()}notices`, {
    method: 'GET',
    query: params,
  });
}

/**
 *改变消息为已读
 *
 * @export
 * @param {*} params
 * @returns
 */
export async function changeRead(params) {
  return request(`${HZERO_MSG}/v1/${organizationId}/messages/user/read-flag`, {
    method: 'POST',
    query: params,
  });
}
