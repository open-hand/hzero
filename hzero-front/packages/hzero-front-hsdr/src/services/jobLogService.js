import request from 'utils/request';
import { HZERO_SDR } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';

const organizationId = getCurrentOrganizationId();
const organizationRoleLevel = isTenantRoleLevel();

/**
 * 查询调度日志列表数据
 * @async
 * @function fetchLogsList
 * @param {Object} params - 查询参数
 */
export async function fetchLogsList(params) {
  return request(
    organizationRoleLevel
      ? `${HZERO_SDR}/v1/${organizationId}/job-logs`
      : `${HZERO_SDR}/v1/job-logs`,
    {
      method: 'GET',
      query: params,
    }
  );
}

/**
 * 删除
 * @async
 * @function deleteLogs
 * @param {object} params - 参数
 */
export async function deleteLogs(params) {
  return request(
    organizationRoleLevel
      ? `${HZERO_SDR}/v1/${organizationId}/job-logs`
      : `${HZERO_SDR}/v1/job-logs`,
    {
      method: 'DELETE',
      body: params,
    }
  );
}

/**
 * 清除日志
 * @async
 * @function clearLogs
 * @param {object} params - 参数
 */
export async function clearLogs(params) {
  const api = organizationRoleLevel
    ? `${HZERO_SDR}/v1/${organizationId}/job-logs/clear`
    : `${HZERO_SDR}/v1/job-logs/clear`;
  return request(api, {
    method: 'DELETE',
    query: params,
  });
}

/**
 * 查询错误日志详情
 * @async
 * @function fetchErrorDetail
 * @param {Object} params - 查询参数
 */
export async function fetchErrorDetail(params) {
  return request(
    organizationRoleLevel
      ? `${HZERO_SDR}/v1/${organizationId}/job-logs/${params.logId}/error-detail`
      : `${HZERO_SDR}/v1/job-logs/${params.logId}/error-detail`,
    {
      method: 'GET',
      responseType: 'text',
    }
  );
}

/**
 * 查询日志详情
 * @async
 * @function fetchLogDetail
 * @param {Object} params - 查询参数
 */
export async function fetchLogDetail(params) {
  return request(
    organizationRoleLevel
      ? `${HZERO_SDR}/v1/${organizationId}/job-logs/${params.logId}/log-detail`
      : `${HZERO_SDR}/v1/job-logs/${params.logId}/log-detail`,
    {
      method: 'GET',
      responseType: 'text',
    }
  );
}

/**
 * 获取任务进度
 * @async
 * @function fetchProgress
 * @param {Object} params - 查询参数
 */
export async function fetchProgress(params) {
  return request(
    organizationRoleLevel
      ? `${HZERO_SDR}/v1/${organizationId}/job-logs/${params.logId}/progress`
      : `${HZERO_SDR}/v1/job-logs/${params.logId}/progress`,
    {
      method: 'GET',
      responseType: 'text',
    }
  );
}
