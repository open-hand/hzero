import request from 'utils/request';
import { HZERO_MSG } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';

const organizationId = getCurrentOrganizationId();
const organizationRoleLevel = isTenantRoleLevel();

/**
 * 查询消息查询列表数据
 * @async
 * @function queryMessageList
 * @param {object} params - 查询条件
 * @param {!number} [params.page = 0] - 数据页码
 * @param {!number} [params.size = 10] - 分页大小
 * @returns {object} fetch Promise
 */
export async function queryMessageList(parmas) {
  return request(
    organizationRoleLevel
      ? `${HZERO_MSG}/v1/${organizationId}/messages`
      : `${HZERO_MSG}/v1/messages`,
    {
      method: 'GET',
      query: { ...parmas },
    }
  );
}

/**
 * 查看内容
 * @async
 * @function queryContent
 * @param {object} params - 查询条件
 * @param {!number} params.messageId - 消息id
 * @returns {object} fetch Promise
 */
export async function queryContent(parmas) {
  return request(
    organizationRoleLevel
      ? `${HZERO_MSG}/v1/${organizationId}/messages/${parmas}/contents`
      : `${HZERO_MSG}/v1/messages/${parmas}/contents`
  );
}

/**
 * 查询收件人
 * @async
 * @function queryRecipient
 * @param {object} params,messageId - 查询条件
 * @param {!number} messageId - 消息id
 * @param {!number} [params.page = 0] - 数据页码
 * @param {!number} [params.size = 10] - 分页大小
 * @returns {object} fetch Promise
 */
export async function queryRecipient(messageId, parmas) {
  return request(
    organizationRoleLevel
      ? `${HZERO_MSG}/v1/${organizationId}/messages/${messageId}/receivers`
      : `${HZERO_MSG}/v1/messages/${messageId}/receivers`,
    {
      method: 'GET',
      query: { ...parmas },
    }
  );
}

/**
 * 查看错误
 * @async
 * @function queryError
 * @param {object} transactionId - 查询条件
 * @param {!number} transactionId - transactionId
 * @returns {object} fetch Promise
 */
export async function queryError(transactionId) {
  return request(
    organizationRoleLevel
      ? `${HZERO_MSG}/v1/${organizationId}/messages/transactions`
      : `${HZERO_MSG}/v1/messages/transactions`,
    {
      method: 'GET',
      query: { transactionId },
    }
  );
}
/**
 * 重试
 * @async
 * @function resendMessage
 * @param {object} transactionId - 查询条件
 * @param {!number} transactionId - transactionId
 * @returns {object} fetch Promise
 */
export async function resendMessage(transactionId) {
  return request(
    organizationRoleLevel
      ? `${HZERO_MSG}/v1/${organizationId}/messages/resend`
      : `${HZERO_MSG}/v1/messages/resend`,
    {
      method: 'POST',
      query: { transactionId },
    }
  );
}

/**
 * 删除
 * @async
 * @function deleteMessage
 * @param {array} params - 查询条件
 * @returns {object} fetch Promise
 */
export async function deleteMessage(params) {
  return request(
    organizationRoleLevel
      ? `${HZERO_MSG}/v1/${organizationId}/messages`
      : `${HZERO_MSG}/v1/messages`,
    {
      method: 'DELETE',
      body: params,
    }
  );
}
