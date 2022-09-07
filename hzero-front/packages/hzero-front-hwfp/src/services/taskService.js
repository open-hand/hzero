/**
 * service - 待办事情列表
 * @date: 2018-8-14
 * @version: 1.0.0
 * @author: LZY <zhuyan.luo@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import request from 'utils/request';
import { HZERO_HWFP, HZERO_PLATFORM, HZERO_FILE } from 'utils/config';
import { parseParameters, getCurrentOrganizationId } from 'utils/utils';

/**
 * 请求API前缀
 * @type {string}
 */
const prefix = `${HZERO_HWFP}/v1`;
const hpfmFix = `${HZERO_PLATFORM}/v1`;

/**
 * 数据查询
 * @async
 * @function searchTaskList
 * @param {object} params - 查询条件
 * @param {!string} params.tenantId - 租户ID
 * @param {?number} params.processDefinitionId - 流程ID
 * @param {?string} params.name - 流程名称
 * @param {?string} params.createdBefore - 创建时间从
 * @param {?string} params.createdAfter - 创建时间至
 * @param {!number} [params.page = 0] - 数据页码
 * @param {!number} [params.size = 10] - 分页大小
 * @returns {object} fetch Promise
 */
export async function fetchTaskList(params) {
  const param = parseParameters(params);
  const { tenantId, ...others } = param;
  return request(`${prefix}/${tenantId}/activiti/task/query/page`, {
    method: 'POST',
    body: others,
  });
}

export async function fetchEmployeeList(params) {
  return request(`${hpfmFix}/lovs/sql/data`, {
    method: 'GET',
    query: params,
  });
}

/**
 * 明细
 * @async
 * @function searchTaskList
 * @param {object} params - 查询条件
 * @param {?string} params.tenantId - 租户ID
 * @param {?string} params.taskId - 待办事项ID
 * @returns {object} fetch Promise
 */
export async function searchDetail(params) {
  return request(`${prefix}/${params.tenantId}/activiti/task/${params.taskId}`, {
    method: 'GET',
  });
}
/**
 * 流程图上表格数据
 * @async
 * @function searchTaskList
 * @param {object} params - 查询条件
 * @param {?string} params.tenantId - 租户ID
 * @param {?string} params.taskId - 待办事项ID
 * @returns {object} fetch Promise
 */
export async function fetchForecast(params) {
  return request(`${prefix}/${params.tenantId}/process/instance/forecast/${params.Id}`, {
    method: 'GET',
  });
}

/**
 * 审批
 * @async
 * @function searchTaskList
 * @param {object} params - 保存条件
 * @param {?string} params.tenantId - 租户ID
 * @param {?string} params.id - 待办事项ID
 * @returns {object} fetch Promise
 */
export async function saveTask(params) {
  const { tenantId, currentTaskId, ...others } = params;
  return request(`${prefix}/${tenantId}/runtime/tasks/${currentTaskId}`, {
    method: 'POST',
    body: { ...others, currentTaskId },
  });
}

/**
 * 获取可跳转的节点
 * @async
 * @function getJumpList
 * @param {object} params - 请求参数
 * @returns {object} fetch Promise
 */
export async function getJumpList(params) {
  const { tenantId, taskId } = params;
  return request(`${prefix}/${tenantId}/activiti/task/getJumpList/${taskId}`, {
    method: 'GET',
  });
}

/**
 * 回退上一审批人
 *
 */
export async function rollBack(params) {
  const { currentTaskId } = params;
  return request(`${prefix}/${getCurrentOrganizationId()}/runtime/tasks/${currentTaskId}`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 跳转指定节点
 * @async
 * @function getJumpList
 * @param {object} params - 请求参数
 * @returns {object} fetch Promise
 */
export async function jumpActivity(params) {
  const { tenantId, taskId, activityId, appointer } = params;
  return request(
    `${prefix}/${tenantId}/activiti/task/jumpActivity/${taskId}/${activityId}/${appointer}`,
    {
      method: 'GET',
    }
  );
}

/**
 * 获取顺序流节点数据
 * @async
 * @function fetchOrderFlowJump
 * @param {object} params - 请求参数
 * @returns
 */
export async function fetchOrderFlowJump(params) {
  const { tenantId, ...data } = params;
  return request(`${prefix}/${tenantId}/process/instance/specified-sequence`, {
    method: 'GET',
    query: data,
  });
}

/**
 * 发送选中顺序流节点
 * @async
 * @function fetchOrderFlowNode
 * @param {object} params - 请求参数
 * @returns
 */
export async function fetchOrderFlowNode(params) {
  const { tenantId, ...data } = params;
  return request(`${prefix}/${tenantId}/process/instance/specified-sequence`, {
    method: 'POST',
    query: data,
  });
}

export async function batchApproveTasks(params) {
  return request(`${prefix}/${getCurrentOrganizationId()}/runtime/batch-tasks`, {
    method: 'POST',
    body: params,
  });
}

export async function getAllUser(params) {
  const formData = new FormData();
  const param = parseParameters(params);
  Object.keys(param).forEach((itemKey) => {
    if (param[itemKey] !== undefined) {
      formData.append(itemKey, param[itemKey]);
    }
  });
  return request(`${prefix}/${getCurrentOrganizationId()}/hr/employee/query`, {
    method: 'POST',
    body: formData,
  });
}

export async function fetchFileCount(params) {
  const { attachmentUUID } = params;
  return request(`${HZERO_FILE}/v1/${getCurrentOrganizationId()}/files/${attachmentUUID}/count`, {
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

//
export async function saveTaskComment(params) {
  const { taskId, comment } = params;
  return request(`${prefix}/${getCurrentOrganizationId()}/activiti/task/comment/${taskId}`, {
    query: {
      comment,
    },
  });
}
