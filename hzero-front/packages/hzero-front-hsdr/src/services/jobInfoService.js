import request from 'utils/request';
import { HZERO_SDR } from 'utils/config';

import { getCurrentOrganizationId, isTenantRoleLevel, parseParameters } from 'utils/utils';

const organizationId = getCurrentOrganizationId();
const organizationRoleLevel = isTenantRoleLevel();

/**
 * 查询执行器列表数据
 * @async
 * @function fetchGroupsList
 * @param {object} params - 查询条件
 */
export async function fetchGroupsList() {
  return request(
    organizationRoleLevel
      ? `${HZERO_SDR}/v1/${organizationId}/executors/available`
      : `${HZERO_SDR}/v1/executors/available`,
    {
      method: 'GET',
    }
  );
}

/**
 * 查询列表数据
 * @async
 * @function fetchPortalAssign
 * @param {Object} params - 查询参数
 * @param {String} [params.page = 0] - 页码
 * @param {String} [params.size = 0] - 页数
 */
export async function fetchJobInfo(params) {
  return request(
    organizationRoleLevel
      ? `${HZERO_SDR}/v1/${organizationId}/job-info`
      : `${HZERO_SDR}/v1/job-info`,
    {
      method: 'GET',
      query: params,
    }
  );
}

/**
 * 查询某一个
 * @async
 * @function fetchPortalAssign
 * @param {Object} params - 查询参数
 */
export async function queryJobInfo(params) {
  return request(
    organizationRoleLevel
      ? `${HZERO_SDR}/v1/${organizationId}/job-info/${params.id}`
      : `${HZERO_SDR}/v1/job-info/${params.id}`,
    {
      method: 'GET',
    }
  );
}

/**
 * 保存glue
 * @async
 * @function updateJobGlue
 * @param {Object} params - 查询参数
 */
export async function updateJobGlue(params) {
  return request(
    organizationRoleLevel
      ? `${HZERO_SDR}/v1/${organizationId}/glue-logs`
      : `${HZERO_SDR}/v1/glue-logs`,
    {
      method: 'POST',
      body: params,
    }
  );
}

/**
 * 查询glue列表
 * @async
 * @function queryJobGlueList
 * @param {Object} params - 查询参数
 */
export async function queryJobGlueList(params) {
  return request(
    organizationRoleLevel
      ? `${HZERO_SDR}/v1/${organizationId}/glue-logs`
      : `${HZERO_SDR}/v1/glue-logs`,
    {
      method: 'GET',
      query: params,
    }
  );
}

/**
 * 查询某个glue
 * @async
 * @function queryJobGlueDetail
 * @param {Object} params - 查询参数
 */
export async function queryJobGlueDetail(params) {
  return request(
    organizationRoleLevel
      ? `${HZERO_SDR}/v1/${organizationId}/glue-logs/${params.id}`
      : `${HZERO_SDR}/v1/glue-logs/${params.id}`,
    {
      method: 'GET',
    }
  );
}

/**
 * 创建
 * @async
 * @function createJobInfo
 * @param {String} params.enabledFlag - 是否启用
 * @param {String} params.groupNum - 集团编码
 */
export async function createJobInfo(params) {
  return request(
    organizationRoleLevel
      ? `${HZERO_SDR}/v1/${organizationId}/job-info`
      : `${HZERO_SDR}/v1/job-info`,
    {
      method: 'POST',
      body: params,
    }
  );
}

/**
 * 修改
 * @async
 * @function updateJobInfo
 * @param {String} params.enabledFlag - 是否启用
 */
export async function updateJobInfo(params) {
  return request(
    organizationRoleLevel
      ? `${HZERO_SDR}/v1/${organizationId}/job-info`
      : `${HZERO_SDR}/v1/job-info`,
    {
      method: 'PUT',
      body: params,
    }
  );
}

/**
 * 删除
 * @async
 * @function updateJobInfo
 * @param {String} params.enabledFlag - 是否启用
 */
export async function deleteJobInfo(params) {
  return request(
    organizationRoleLevel
      ? `${HZERO_SDR}/v1/${organizationId}/job-info`
      : `${HZERO_SDR}/v1/job-info`,
    {
      method: 'DELETE',
      body: params,
    }
  );
}

/**
 * 暂停
 * @async
 * @function updateJobInfo
 * @param {String} params.enabledFlag - 是否启用
 */
export async function pauseJobInfo(params) {
  const { jobId, tenantId } = params;
  return request(
    organizationRoleLevel
      ? `${HZERO_SDR}/v1/${organizationId}/job-info/pause`
      : `${HZERO_SDR}/v1/job-info/pause`,
    {
      method: 'POST',
      body: { jobId, tenantId },
    }
  );
}

/**
 * 恢复
 * @async
 * @function updateJobInfo
 * @param {String} params.enabledFlag - 是否启用
 */
export async function resumeJobInfo(params) {
  const { jobId, tenantId } = params;
  return request(
    organizationRoleLevel
      ? `${HZERO_SDR}/v1/${organizationId}/job-info/resume`
      : `${HZERO_SDR}/v1/job-info/resume`,
    {
      method: 'POST',
      body: { jobId, tenantId },
    }
  );
}

/**
 * 执行
 * @async
 * @function updateJobInfo
 * @param {String} params.enabledFlag - 是否启用
 */
export async function triggerJobInfo(params) {
  const { jobId, tenantId } = params;
  return request(
    organizationRoleLevel
      ? `${HZERO_SDR}/v1/${organizationId}/job-info/trigger`
      : `${HZERO_SDR}/v1/job-info/trigger`,
    {
      method: 'POST',
      body: { jobId, tenantId },
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
  const { jobId, tenantId } = params;
  return request(
    organizationRoleLevel
      ? `${HZERO_SDR}/v1/${organizationId}/job-info/stop`
      : `${HZERO_SDR}/v1/job-info/stop`,
    {
      method: 'POST',
      body: { jobId, tenantId },
    }
  );
}

export async function fetchJobLog(params) {
  const { jobId } = params;
  const param = parseParameters(params);
  return request(
    organizationRoleLevel
      ? `${HZERO_SDR}/v1/${organizationId}/job-logs/${jobId}/logs`
      : `${HZERO_SDR}/v1/job-logs/${jobId}/logs`,
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

/**
 * 复制任务
 * @async
 * @function copyJobInfo
 * @param {Object} params - 查询参数
 */
export async function copyJobInfo(params) {
  return request(
    organizationRoleLevel
      ? `${HZERO_SDR}/v1/${organizationId}/job-info/${params.jobId}/copy`
      : `${HZERO_SDR}/v1/job-info/${params.jobId}/copy`,
    {
      method: 'GET',
    }
  );
}
