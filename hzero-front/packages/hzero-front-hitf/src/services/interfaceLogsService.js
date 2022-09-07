import request from 'utils/request';
import { getCurrentOrganizationId, parseParameters, isTenantRoleLevel } from 'utils/utils';
import { HZERO_HITF } from 'utils/config';

const organizationId = getCurrentOrganizationId();
const organizationRoleLevel = isTenantRoleLevel();

export async function fetchLogsList(params = {}) {
  // const { ...query } = params;
  const param = parseParameters(params);
  return request(
    organizationRoleLevel
      ? `${HZERO_HITF}/v1/${organizationId}/interface-logs`
      : `${HZERO_HITF}/v1/interface-logs`,
    {
      method: 'GET',
      query: param,
    }
  );
}

export async function fetchLogsDetail(params) {
  const { interfaceLogId, invokeKey } = params;
  const level = organizationRoleLevel ? `/${organizationId}` : '';
  return request(
    invokeKey
      ? `${HZERO_HITF}/v1${level}/interface-logs/invokeKey/detail`
      : `${HZERO_HITF}/v1${level}/interface-logs/${interfaceLogId}`,
    {
      method: 'GET',
      query: invokeKey
        ? {
            invokeKey,
          }
        : {},
    }
  );
}

/**
 * 清除日志
 * @function clearLogs
 * @param {object} params - 参数
 */
export async function clearLogs(params) {
  return request(
    organizationRoleLevel
      ? `${HZERO_HITF}/v1/${organizationId}/interface-logs/clear-logs`
      : `${HZERO_HITF}/v1/interface-logs/clear-logs`,
    {
      method: 'DELETE',
      body: params,
    }
  );
}

/**
 * 重试
 * @param {object} params - 参数
 */
export async function retry(params = {}) {
  // const { ...query } = params;
  const param = parseParameters(params);
  return request(
    organizationRoleLevel
      ? `${HZERO_HITF}/v1/${organizationId}/interface-logs/retry/${param.interfaceLogId}`
      : `${HZERO_HITF}/v1/interface-logs/retry`,
    {
      method: 'GET',
      query: organizationRoleLevel ? null : param,
    }
  );
}
