/**
 * service - 消息管理/发送配置
 * @date: 2018-8-21
 * @version: 1.0.0
 * @author: CJ <juan.chen01@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import request from 'utils/request';
import { HZERO_MSG } from 'utils/config';
import { parseParameters, getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';

const organizationId = getCurrentOrganizationId();
const organizationRoleLevel = isTenantRoleLevel();

/**
 * 查询模板服务映射列表数据
 * @async
 * @function querySMSList
 * @param {object} params - 查询条件
 * @param {!number} [params.page = 0] - 数据页码
 * @param {!number} [params.size = 10] - 分页大小
 * @returns {object} fetch Promise
 */
export async function querySMSList(params) {
  const param = parseParameters(params);
  return request(
    organizationRoleLevel
      ? `${HZERO_MSG}/v1/${organizationId}/template-servers`
      : `${HZERO_MSG}/v1/template-servers`,
    {
      method: 'GET',
      query: { ...param },
    }
  );
}
/**
 * 删除模板服务
 * @async
 * @function deleteHeader
 * @param {object} params - 请求参数
 * @param {!object} params.tempServerId - 消息模板ID
 */
export async function deleteHeader(params) {
  return request(
    organizationRoleLevel
      ? `${HZERO_MSG}/v1/${organizationId}/template-servers/${params.tempServerId}`
      : `${HZERO_MSG}/v1/template-servers/${params.tempServerId}`,
    {
      method: 'DELETE',
      body: { ...params },
    }
  );
}
/**
 * 编辑条件头查询
 * @async
 * @function searchApproveHeader
 * @param {object} params - 查询条件
 * @param {?string} params.tempServerId - 消息模板ID
 * @returns {object} fetch Promise
 */

export async function searchHeader(params) {
  return request(
    organizationRoleLevel
      ? `${HZERO_MSG}/v1/${organizationId}/template-servers/line/${params.tempServerId}`
      : `${HZERO_MSG}/v1/template-servers/line/${params.tempServerId}`,
    {
      method: 'GET',
    }
  );
}

/**
 * 复制
 * @param {*} params
 */
export async function searchCopyHeader(params) {
  return request(
    organizationRoleLevel
      ? `${HZERO_MSG}/v1/${organizationId}/template-servers/${params.tempServerId}/copy`
      : `${HZERO_MSG}/v1/template-servers/${params.tempServerId}/copy`,
    {
      method: 'GET',
    }
  );
}
/**
 * 新增
 * @async
 * @function saveHeader
 * @param {object} params - 请求参数
 * @param {!object} params.dto - 待保存对象
 */
export async function saveHeader(params) {
  return request(
    organizationRoleLevel
      ? `${HZERO_MSG}/v1/${organizationId}/template-servers`
      : `${HZERO_MSG}/v1/template-servers`,
    {
      method: 'POST',
      body: { ...params.dto },
    }
  );
}

/**
 * 更新
 * @async
 * @function updateHeader
 * @param {object} params - 请求参数
 * @param {!string} params.tempServerId - 消息模板ID
 * @param {!object} params.dto - 待保存对象
 */
export async function updateHeader(params) {
  return request(
    organizationRoleLevel
      ? `${HZERO_MSG}/v1/${organizationId}/template-servers/${params.tempServerId}`
      : `${HZERO_MSG}/v1/template-servers/${params.tempServerId}`,
    {
      method: 'PUT',
      body: { ...params.dto },
    }
  );
}
/**
 * 删除行服务
 * @async
 * @function deleteHeader
 * @param {object} params - 请求参数
 * @param {!object} params.recordTempServerLineId - 参数ID
 */
export async function deleteLine(params) {
  return request(
    organizationRoleLevel
      ? `${HZERO_MSG}/v1/${organizationId}/template-servers/line/${params.tempServerLineId}`
      : `${HZERO_MSG}/v1/template-servers/line/${params.tempServerLineId}`,
    {
      method: 'DELETE',
      body: { ...params },
    }
  );
}

/**
 * 获取语言类型
 * @async
 * @function fetchLangType
 * @param {object} params - 请求参数
 */
export async function fetchLangType(params) {
  return request(
    organizationRoleLevel
      ? `${HZERO_MSG}/v1/${organizationId}/message/templates/template-lang`
      : `${HZERO_MSG}/v1/message/templates/template-lang`,
    {
      method: 'GET',
      query: { ...params },
    }
  );
}
/**
 * 改变语言，获取参数名称
 * @async
 * @function getParams
 * @param {object} params - 请求参数
 */
export async function getParams(params) {
  return request(
    organizationRoleLevel
      ? `${HZERO_MSG}/v1/${organizationId}/message/templates/template-args`
      : `${HZERO_MSG}/v1/message/templates/template-args`,
    {
      method: 'GET',
      query: { ...params },
    }
  );
}
/**
 * 发送消息
 * @async
 * @function sendMessage
 * @param {object} params - 请求参数
 */
export async function sendMessage(params) {
  return request(
    organizationRoleLevel
      ? `${HZERO_MSG}/v1/${organizationId}/message/relevance/all`
      : `${HZERO_MSG}/v1/message/relevance/all`,
    {
      method: 'POST',
      body: { ...params },
    }
  );
}

/**
 * 获取模板数据
 * @param {object} params
 */
export async function fetchTemplateData(params) {
  return request(
    organizationRoleLevel
      ? `${HZERO_MSG}/v1/${organizationId}/message/templates/template-code`
      : `${HZERO_MSG}/v1/message/templates/template-code`,
    {
      method: 'GET',
      query: params,
    }
  );
}

/**
 * 获取模板数据
 * @param {object} params
 */
export async function GetWebhook(params) {
  const { tempServerLineId } = params;
  return request(
    organizationRoleLevel
      ? `${HZERO_MSG}/v1/${organizationId}/template-server-whs/${tempServerLineId}`
      : `${HZERO_MSG}/v1/template-server-whs/${tempServerLineId}`,
    {
      method: 'GET',
    }
  );
}

/**
 * 获取模板数据
 * @param {object} params
 */
export async function deleteWebhook(params) {
  const { tempServerLineId, payload } = params;
  return request(
    organizationRoleLevel
      ? `${HZERO_MSG}/v1/${organizationId}/template-server-whs/${tempServerLineId}`
      : `${HZERO_MSG}/v1/template-server-whs/${tempServerLineId}`,
    {
      method: 'DELETE',
      body: payload,
    }
  );
}

/**
 * 获取模板数据
 * @param {object} params
 */
export async function createWebhook(params) {
  const { tempServerLineId, payload } = params;
  return request(
    organizationRoleLevel
      ? `${HZERO_MSG}/v1/${organizationId}/template-server-whs/${tempServerLineId}`
      : `${HZERO_MSG}/v1/template-server-whs/${tempServerLineId}`,
    {
      method: 'POST',
      body: payload,
    }
  );
}
