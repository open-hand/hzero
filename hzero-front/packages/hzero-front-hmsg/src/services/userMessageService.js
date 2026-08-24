/**
 * service - 站内消息
 * @date: 2018-8-10
 * @author: YB <bo.yang02@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import request from 'utils/request';
import { HZERO_MSG } from 'utils/config';
import { filterNullValueObject, parseParameters } from 'utils/utils';

/**
 *
 *查询站内消息
 * @export
 * @param {*} params
 * @returns
 */
export async function queryMessage(params) {
  const { organizationId, type, ...other } = params;
  const reqUrl =
    type === 'announce'
      ? `${HZERO_MSG}/v1/${organizationId}/notices`
      : `${HZERO_MSG}/v1/${organizationId}/messages/user`;
  const param = filterNullValueObject(parseParameters(other));
  if (type === 'notice') {
    param.userMessageTypeCode = 'NOTICE';
  }
  if (type === 'message') {
    param.userMessageTypeCode = 'MSG';
  }
  if (type === 'announce') {
    // param.receiverTypeCode = 'ANNOUNCE';
    // param.statusCode = 'PUBLISHED';
    param.userNotice = true;
    param.sort = 'publishedDate,DESC';
  }
  return request(reqUrl, {
    method: 'GET',
    query: param,
  });
}

/**
 *改变消息为未读
 *
 * @export
 * @param {*} params
 * @returns
 */
export async function changeRead(params) {
  const { organizationId, ...other } = params;
  return request(`${HZERO_MSG}/v1/${organizationId}/messages/user/read-flag`, {
    method: 'POST',
    query: other,
  });
}

/**
 *查询站内消息明细
 *
 * @export
 * @param {*} params
 * @returns
 */
export async function queryMessageDetail(params) {
  const { organizationId, userMessageId, type } = params;
  // 公告 传递过来的 userMessageId 实际上是 noticeId
  const noticeId = userMessageId;
  const reqUrl =
    type === 'announce'
      ? `${HZERO_MSG}/v1/${organizationId}/notices/user/${noticeId}`
      : `${HZERO_MSG}/v1/${organizationId}/messages/user/${userMessageId}`;
  return request(reqUrl, {
    method: 'GET',
  });
}

/**
 *删除站内消息
 *
 * @export
 * @param {*} params
 * @returns
 */
export async function deleteMessage(params) {
  const { organizationId, ...other } = params;
  return request(`${HZERO_MSG}/v1/${organizationId}/messages/user`, {
    method: 'DELETE',
    query: other,
  });
}
