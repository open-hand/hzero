/**
 * service - 并发管理/并发请求
 * @date: 2018-9-10
 * @version: 1.0.0
 * @author: LZY <zhuyan.luo@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */

import request from 'utils/request';
import { HZERO_SDR } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel, parseParameters } from 'utils/utils';

const organizationId = getCurrentOrganizationId();
const organizationRoleLevel = isTenantRoleLevel();
/**
 * 请求API前缀
 * @type {string}
 */
const prefix = `${HZERO_SDR}/v1`;

/**
 * 数据查询
 * @async
 * @function fetchRequestList
 * @param {object} params - 查询条件
 * @param {?string} params.tenantId - 租户ID
 * @returns {object} fetch Promise
 */
export async function fetchRequestList(params) {
  const param = parseParameters(params);
  const { tenantId, ...others } = param;
  return request(
    organizationRoleLevel
      ? `${prefix}/${organizationId}/concurrent-requests`
      : `${prefix}/concurrent-requests`,
    {
      method: 'GET',
      query: others,
    }
  );
}
/**
 * 创建
 * @async
 * @function createRequest
 * @param {String} params - 保存参数
 */
export async function createRequest(params) {
  return request(
    organizationRoleLevel
      ? `${prefix}/${organizationId}/concurrent-requests`
      : `${prefix}/concurrent-requests`,
    {
      method: 'POST',
      body: params,
    }
  );
}

/**
 * 查询详情
 * @async
 * @function fetchRequestDetail
 * @param {Object} params - 查询参数
 */
export async function fetchRequestDetail(params) {
  return request(
    organizationRoleLevel
      ? `${prefix}/${organizationId}/concurrent-requests/${params.requestId}`
      : `${prefix}/concurrent-requests/${params.requestId}`,
    {
      method: 'GET',
    }
  );
}

/**
 * 查询请求参数
 * @async
 * @function fetchParams
 * @param {Object} params - 查询参数
 */
export async function fetchParams(params) {
  return request(
    organizationRoleLevel
      ? `${prefix}/${organizationId}/concurrent-params`
      : `${prefix}/concurrent-params`,
    {
      method: 'GET',
      query: params,
    }
  );
}

/**
 * 删除JOB
 * @async
 * @function deleteJobInfo
 * @param {String} params.tenantId - 租户ID
 */
export async function deleteJobInfo(params) {
  return request(
    organizationRoleLevel
      ? `${prefix}/${organizationId}/concurrent-requests`
      : `${prefix}/concurrent-requests`,
    {
      method: 'DELETE',
      body: params,
    }
  );
}

/**
 * 暂停
 * @async
 * @function pauseJobInfo
 * @param {String} params.jobId - 任务ID
 */
export async function pauseJobInfo(params) {
  return request(
    organizationRoleLevel
      ? `${prefix}/${organizationId}/job-info/pause`
      : `${prefix}/job-info/pause`,
    {
      method: 'POST',
      body: params,
    }
  );
}

/**
 * 恢复
 * @async
 * @function resumeJobInfo
 * @param {String} params.jobId - 任务ID
 */
export async function resumeJobInfo(params) {
  return request(
    organizationRoleLevel
      ? `${prefix}/${organizationId}/job-info/resume`
      : `${prefix}/job-info/resume`,
    {
      method: 'POST',
      body: params,
    }
  );
}

/**
 * 执行
 * @async
 * @function triggerJobInfo
 * @param {String} params.jobId - 任务ID
 */
export async function triggerJobInfo(params) {
  return request(
    organizationRoleLevel
      ? `${prefix}/${organizationId}/job-info/trigger`
      : `${prefix}/job-info/trigger`,
    {
      method: 'POST',
      body: params,
    }
  );
}

/**
 * 终止
 * @async
 * @function stopJob
 * @param {String} params.enabledFlag - 是否启用
 */
export async function stopJob(params) {
  return request(
    organizationRoleLevel
      ? `${HZERO_SDR}/v1/${organizationId}/job-info/stop`
      : `${HZERO_SDR}/v1/job-info/stop`,
    {
      method: 'POST',
      body: params,
    }
  );
}

export async function fetchJobLog(params) {
  const { jobId } = params;
  const param = parseParameters(params);
  return request(
    organizationRoleLevel
      ? `${prefix}/${organizationId}/job-logs/${jobId}/logs`
      : `${prefix}/job-logs/${jobId}/logs`,
    {
      method: 'GET',
      query: param,
    }
  );
}

/**
 * 查询错误日志详情
 * @async
 * @function fetchJobLogError
 * @param {Object} params - 查询参数
 */
export async function fetchJobLogError(params) {
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
 * 删除
 * @async
 * @function deleteJobLog
 * @param {String} params.enabledFlag - 是否启用
 */
export async function deleteJobLog(params) {
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
