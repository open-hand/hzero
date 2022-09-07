/**
 * service - 我的抄送流程
 * @date: 2018-8-14
 * @version: 1.0.0
 * @author: WH <heng.wei@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import request from 'utils/request';
import { HZERO_HWFP } from 'utils/config';
import { parseParameters, getCurrentOrganizationId } from 'utils/utils';
/**
 * 请求API前缀
 * @type {string}
 */
const prefix = `${HZERO_HWFP}/v1`;

/**
 * 数据查询
 * @async
 * @function searchTaskList
 * @param {object} params - 查询条件
 * @param {?string} params.tenantId - 租户ID
 * @param {!number} [params.page = 0] - 数据页码
 * @param {!number} [params.size = 10] - 分页大小
 * @returns {object} fetch Promise
 */
export async function fetchTaskList(params) {
  const param = parseParameters(params);
  const { tenantId, carbonCopy, ...others } = param;
  return request(`${prefix}/${tenantId}/process/instance/query/page`, {
    method: 'POST',
    query: { carbonCopy },
    body: others,
  });
}

/**
 * 明细
 * @async
 * @function searchTaskList
 * @param {object} params - 查询条件
 * @param {?string} params.tenantId - 租户ID
 * @param {?string} params.id - 待办事项ID
 * @returns {object} fetch Promise
 */
export async function searchDetail(params) {
  return request(`${prefix}/${params.tenantId}/instance/carbonCopy/${params.id}`, {
    method: 'GET',
    query: {
      type: params.type,
    },
  });
}
/**
 * 流程图上表格数据
 * @async
 * @function searchTaskList
 * @param {object} params - 查询条件
 * @param {?string} params.tenantId - 租户ID
 * @param {?string} params.id - 待办事项ID
 * @returns {object} fetch Promise
 */
export async function fetchForecast(params) {
  return request(`${prefix}/${params.tenantId}/process/instance/forecast/${params.id}`, {
    method: 'GET',
  });
}

/**
 * 撤回
 * @async
 * @function taskRecall
 * @param {?string} params.tenantId - 租户ID
 * @param {?string} params.processInstanceId - 流程ID
 * @returns {object} fetch Promise
 */
export async function taskRecall(params) {
  const { tenantId, processInstanceId } = params;
  return request(`${prefix}/${tenantId}/runtime/prc/back/${processInstanceId}`, {
    method: 'GET',
  });
}

// 查询审批历史记录
export async function fetchHistoryApproval(params) {
  return request(`${prefix}/${getCurrentOrganizationId()}/activiti/task/historyApproval`, {
    method: 'POST',
    query: params,
  });
}
