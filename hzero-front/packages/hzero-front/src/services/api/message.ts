/**
 * @email WY <yang.wang06@hand-china.com>
 * @creationDate 2019/12/25
 * @copyright HAND ® 2019
 */

import request from 'utils/request';
import { getEnvConfig } from 'utils/iocUtils';
import { getCurrentOrganizationId } from 'utils/utils';

const { HZERO_MSG } = getEnvConfig();
/**
 * 查询未读的站内消息
 * {HZERO_MSG}/v1/{organizationId}/messages/user/preview
 * @export
 * @param {object} params
 */
export async function queryNotices(params) {
  const organizationId = getCurrentOrganizationId();
  return request(`${HZERO_MSG}/v1/${organizationId}/messages/user/preview`, {
    method: 'GET',
    query: params,
  });
}

/**
 * 查询未读的站内公告
 * {HZERO_MSG}/v1/{organizationId}/notices/user/preview
 * @export
 * @param {object} params
 */
export async function queryAnnounces(params) {
  const organizationId = getCurrentOrganizationId();
  return request(`${HZERO_MSG}/v1/${organizationId}/notices/user/preview`, {
    method: 'GET',
    query: params,
  });
}

/**
 * 查询站内消息条数
 * {HZERO_MSG}/v1/{organizationId}/messages/user/count
 * @export
 */
export async function queryCount() {
  const organizationId = getCurrentOrganizationId();
  return request(`${HZERO_MSG}/v1/${organizationId}/messages/user/count`, {
    method: 'GET',
  });
}

/**
 * 已读全部消息
 * {HZERO_MSG}/v1/${organizationId}/messages/user/read-flag
 * @export
 */
export async function allRead(params) {
  const organizationId = getCurrentOrganizationId();
  return request(`${HZERO_MSG}/v1/${organizationId}/messages/user/read-flag`, {
    method: 'POST',
    query: params,
  });
}

/**
 * 删除全部消息
 * {HZERO_MSG}/v1/${organizationId}/messages/user/clear
 * @export
 */
export async function deleteAll() {
  const organizationId = getCurrentOrganizationId();
  return request(`${HZERO_MSG}/v1/${organizationId}/messages/user/clear`, {
    method: 'GET',
  });
}
