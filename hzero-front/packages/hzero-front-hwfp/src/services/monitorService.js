/**
 * service - 流程监控
 * @date: 2018-8-14
 * @version: 1.0.0
 * @author: LZY <zhuyan.luo@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */

import request from 'utils/request';
import { HZERO_HWFP, HZERO_PLATFORM } from 'utils/config';
import { parseParameters, getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';

const isSiteFlag = !isTenantRoleLevel();
const tenantId = getCurrentOrganizationId();
const prefix = isSiteFlag ? `${HZERO_HWFP}/v1` : `${HZERO_HWFP}/v1/${tenantId}`;

export async function fetchEmployeeList(params) {
  return request(`${HZERO_PLATFORM}/v1/lovs/sql/data`, {
    method: 'GET',
    query: params,
  });
}

/**
 * 数据查询
 * @async
 * @function searchTaskList
 * @param {object} params - 查询条件
 * @returns {object} fetch Promise
 */
export async function fetchMonitorList(params) {
  const param = parseParameters(params);
  return request(`${prefix}/process/instance/monitor/query`, {
    method: 'POST',
    body: param,
  });
}

/**
 * 详情
 * @async
 * @function fetchDetail
 * @param {object} params - 查询条件
 * @param {?string} params.tenantId - 租户ID
 * @param {?string} params.id - 流程ID
 * @returns {object} fetch Promise
 */
export async function fetchDetail(params) {
  return request(`${prefix}/process/instance/monitor/detail/${params.id}`, {
    method: 'GET',
  });
}

/**
 * 流程图上表格数据
 * @async
 * @function fetchForecast
 * @param {object} params - 查询条件
 * @param {?string} params.tenantId - 租户ID
 * @param {?string} params.id - 流程ID
 * @returns {object} fetch Promise
 */
export async function fetchForecast(params) {
  return request(`${prefix}/process/instance/monitor/forecast/${params.id}`, {
    method: 'GET',
  });
}

/**
 * 挂起详情
 * @export
 * @param {?string} params.tenantId - 租户ID
 * @param {?string} params.processInstanceId - 流程ID
 * @returns
 */
export async function fetchExceptionDetail(params) {
  return request(`${prefix}/process/instance/monitor/suspendDetail/${params.processInstanceId}`, {
    method: 'GET',
  });
}

/**
 * 终止流程
 * @async
 * @function stopProcess
 * @param {String} params.tenantId - 当前的租户ID
 * @param {String} params.processInstanceId - 流程ID
 */
export async function stopProcess(params) {
  return request(`${prefix}/process/instance/monitor/end/${params.processInstanceId}`, {
    method: 'GET',
  });
}

/**
 * 恢复流程
 * @async
 * @function resumeProcess
 * @param {String} params.tenantId - 当前的租户ID
 * @param {String} params.processInstanceId - 流程ID
 */
export async function resumeProcess(params) {
  return request(`${prefix}/process/instance/monitor/active/${params.processInstanceId}`, {
    method: 'GET',
  });
}

/**
 * 挂起流程
 * @async
 * @function suspendProcess
 * @param {String} params.tenantId - 当前的租户ID
 * @param {String} params.processInstanceId - 流程ID
 */
export async function suspendProcess(params) {
  return request(`${prefix}/process/instance/monitor/suspend/${params.processInstanceId}`, {
    method: 'GET',
  });
}

/**
 * 查询有效的节点
 * @async
 * @function fetchValidNode
 * @param {String} params.tenantId - 当前的租户ID
 * @param {String} params.processInstanceId - 流程ID
 */
export async function fetchValidNode(params) {
  return request(`${prefix}/definition/user-tasks/${params.processInstanceId}`, {
    method: 'GET',
  });
}

/**
 * 流程恢复并制指定审批人
 * @async
 * @function retryProcess
 * @param {String} params.tenantId - 当前的租户ID
 * @param {String} params.processInstanceId - 流程ID
 */
export async function retryProcess(params) {
  return request(`${prefix}/process/instance/monitor/execute/retry`, {
    method: 'POST',
    query: params,
  });
}

/**
 * 异常日志
 * @async
 * @function fetchProcessException
 * @param {String} params.tenantId - 当前的租户ID
 * @param {String} params.processInstanceId - 流程ID
 */
export async function fetchProcessException(params) {
  return request(`${prefix}/process/instance/monitor/exception/${params.encryptId}`, {
    method: 'GET',
  });
}
