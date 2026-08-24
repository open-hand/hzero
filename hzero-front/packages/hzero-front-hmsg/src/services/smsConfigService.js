import request from 'utils/request';
import { HZERO_MSG } from 'utils/config';
import { parseParameters, getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import { queryIdpValue } from 'hzero-front/lib/services/api';

const organizationId = getCurrentOrganizationId();
const organizationRoleLevel = isTenantRoleLevel();

/**
 * 查询短信配置列表数据
 * @async
 * @function fetchSMSList
 * @param {object} params - 查询条件
 * @param {!number} [params.page = 0] - 数据页码
 * @param {!number} [params.size = 10] - 分页大小
 * @returns {object} fetch Promise
 */
export async function fetchSMSList(params) {
  const param = parseParameters(params);
  return request(
    organizationRoleLevel
      ? `${HZERO_MSG}/v1/${organizationId}/sms/servers`
      : `${HZERO_MSG}/v1/sms/servers`,
    {
      method: 'GET',
      query: { ...param },
    }
  );
}

/**
 * 获取服务类型的值集
 * @async
 * @function fetchTrxStatus
 */
export async function fetchServerType() {
  return queryIdpValue('HMSG.SMS_SERVER_TYPE');
}

/**
 * 添加短信配置信息
 * @async
 * @function createSMS
 * @param {object} params - 请求参数
 * @param {!object} params.tenantId - 租户Id
 * @param {!string} params.serverCode - 账户代码
 * @param {?number} params.serverName - 账户名称
 * @param {!string} params.signName - 短信签名
 * @param {!number} params.serverTypeList - 服务类型
 * @param {!string} params.endPoint - endPoint
 * @param {!string} params.accessKey - accessKey
 * @param {!string} params.accessKeySecret - endPoint
 * @param {!string} params.enabledFlag - 启用标记
 * @returns {object} fetch Promise
 */
export async function createSMS(params) {
  return request(
    organizationRoleLevel
      ? `${HZERO_MSG}/v1/${organizationId}/sms/servers`
      : `${HZERO_MSG}/v1/sms/servers`,
    {
      method: 'POST',
      body: params,
    }
  );
}

/**
 * 编辑短信配置信息
 * @async
 * @function createSMS
 * @param {object} params - 请求参数
 * @param {?number} params.serverName - 账户名称
 * @param {!string} params.signName - 短信签名
 * @param {!number} params.serverTypeList - 服务类型
 * @param {!string} params.endPoint - endPoint
 * @param {!string} params.accessKey - accessKey
 * @param {!string} params.accessKeySecret - endPoint
 * @param {!string} params.enabledFlag - 启用标记
 * @param {!string} params.serverId - serverId
 * @param {!string} params.objectVersionNumber - 版本号
 * @returns {object} fetch Promise
 */
export async function editSMS(params) {
  return request(
    organizationRoleLevel
      ? `${HZERO_MSG}/v1/${organizationId}/sms/servers`
      : `${HZERO_MSG}/v1/sms/servers`,
    {
      method: 'PUT',
      body: params,
    }
  );
}

export async function deleteSMS(params) {
  return request(
    organizationRoleLevel
      ? `${HZERO_MSG}/v1/${organizationId}/sms/servers`
      : `${HZERO_MSG}/v1/sms/servers`,
    {
      method: 'DELETE',
      body: params,
    }
  );
}

export async function fetchFilterList(params) {
  return request(
    organizationRoleLevel
      ? `${HZERO_MSG}/v1/${organizationId}/sms-filters`
      : `${HZERO_MSG}/v1/sms-filters`,
    {
      method: 'GET',
      query: params,
    }
  );
}

export async function updateFilter(params) {
  return request(
    organizationRoleLevel
      ? `${HZERO_MSG}/v1/${organizationId}/sms-filters`
      : `${HZERO_MSG}/v1/sms-filters`,
    {
      method: 'POST',
      body: params,
    }
  );
}

export async function deleteFilter(params) {
  return request(
    organizationRoleLevel
      ? `${HZERO_MSG}/v1/${organizationId}/sms-filters`
      : `${HZERO_MSG}/v1/sms-filters`,
    {
      method: 'DELETE',
      body: params,
    }
  );
}
