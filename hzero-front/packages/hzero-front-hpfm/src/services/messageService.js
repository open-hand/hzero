/**
 * Message API返回消息管理
 * @date: 2019-1-9
 * @version: 0.0.1
 * @author:  guochaochao <chaochao.guo@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import request from 'utils/request';
import { HZERO_PLATFORM } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';

const tenantId = getCurrentOrganizationId();
function message() {
  return isTenantRoleLevel() ? `${tenantId}/response-messages` : 'response-messages';
}
/**
 * 删除消息
 * @param {Object} params - 参数
 */
export async function deleteMessage(params) {
  return request(`${HZERO_PLATFORM}/v1/${message()}`, {
    method: 'DELETE',
    body: params,
  });
}

/**
 * 查询消息列表
 * @param {Object} params - 查询参数
 */
export async function fetchMessageList(params) {
  return request(`${HZERO_PLATFORM}/v1/${message()}`, {
    method: 'GET',
    query: params,
  });
}

/**
 * 创建后端消息
 * @param {Object} params - 参数
 */
export async function createMessage(params) {
  return request(`${HZERO_PLATFORM}/v1/${message()}`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 修改消息
 * @param {Object} params - 参数
 */
export async function updateMessage(params) {
  return request(`${HZERO_PLATFORM}/v1/${message()}`, {
    method: 'PUT',
    body: params,
  });
}

/**
 * 查询后端消息明细
 * @param {Object} params - 查询参数
 */
export async function getMessageDetail(params) {
  const { messageId } = params;
  return request(`${HZERO_PLATFORM}/v1/${message()}/details/${messageId}`, {
    method: 'GET',
  });
}
