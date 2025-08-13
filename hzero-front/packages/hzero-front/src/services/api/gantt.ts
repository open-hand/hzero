/**
 * 文件相关
 * @email WY <yang.wang06@hand-china.com>
 * @creationDate 2019/12/25
 * @copyright HAND ® 2019
 */

import request from 'utils/request';
import { getEnvConfig } from 'utils/iocUtils';
import { getCurrentOrganizationId, isTenantRoleLevel, getResponse } from 'utils/utils';

const { HZERO_PLATFORM } = getEnvConfig();
/**
 * 保存
 * @export
 * @param {object} params 传递参数
 */
export async function saveGanttTaskData(gantt) {
  const organizationId = getCurrentOrganizationId();
  const apiPrefix = isTenantRoleLevel()
    ? `${HZERO_PLATFORM}/v1/${organizationId}`
    : `${HZERO_PLATFORM}/v1`;
  const arr = gantt.$data.tasksStore.getItems().map((item) => {
    return {
      ganttId: gantt.serverList('ganttId')[0],
      taskId: item.objectVersionNumber ? item.id : undefined,
      taskName: item.text,
      orderSeq: item.order,
      startDate: `${item.start_date.getFullYear()}-${
        item.start_date.getMonth() + 1
      }-${item.start_date.getDate()} 00:00:00`,
      endDate: `${item.end_date.getFullYear()}-${
        item.end_date.getMonth() + 1
      }-${item.end_date.getDate()} 00:00:00`,
      parentId: item.parent || null,
      taskType: item.type || 'task',
      tenantId: item.tenantId || organizationId,
      objectVersionNumber: item.objectVersionNumber,
      _token: item._token,
      duration: item.duration,
      priority: item.priority,
      progress: item.progress.toFixed(2),
      taskRender: item.render && item.render[0],
    };
  });
  return request(`${apiPrefix}/gantt-tasks/batch`, {
    method: 'POST',
    body: arr,
  });
}

/**
 * 保存
 * @export
 * @param {object} params 传递参数
 */
export async function getGanttTaskData(gantt) {
  const organizationId = getCurrentOrganizationId();
  const apiPrefix = isTenantRoleLevel()
    ? `${HZERO_PLATFORM}/v1/${organizationId}`
    : `${HZERO_PLATFORM}/v1`;
  const res = await request(`${apiPrefix}/gantt-tasks`, {
    method: 'GET',
    query: {
      tenantId: organizationId,
      ganttId: gantt.serverList('ganttId')[0],
    },
  });
  const data = getResponse(res, (e) => e);
  return formatGanttTaskData(data) || [];
}

/**
 * 保存
 * @export
 * @param {object} params 传递参数
 */
export async function deleteGanttTaskData(task) {
  const organizationId = getCurrentOrganizationId();
  const apiPrefix = isTenantRoleLevel()
    ? `${HZERO_PLATFORM}/v1/${organizationId}`
    : `${HZERO_PLATFORM}/v1`;
  const obj = task;
  const res = await request(`${apiPrefix}/gantt-tasks`, {
    method: 'DELETE',
    body: obj,
  });
  return getResponse(res, (e) => e);
}

/**
 * 保存
 * @export
 * @param {object} params 传递参数
 */
export async function saveGanttLinkData(gantt) {
  const organizationId = getCurrentOrganizationId();
  const apiPrefix = isTenantRoleLevel()
    ? `${HZERO_PLATFORM}/v1/${organizationId}`
    : `${HZERO_PLATFORM}/v1`;
  const arr = gantt.$data.linksStore.getItems().map((item) => {
    return {
      ganttId: gantt.serverList('ganttId')[0],
      linkType: item.type,
      taskFromId: item.source,
      taskTargetId: item.target,
      taskLinkId: item.objectVersionNumber ? item.id : undefined,
      tenantId: item.tenantId || organizationId,
      objectVersionNumber: item.objectVersionNumber,
      _token: item._token,
    };
  });
  return request(`${apiPrefix}/gantt-task-links`, {
    method: 'POST',
    body: arr,
  });
}

/**
 * 保存
 * @export
 * @param {object} params 传递参数
 */
export async function getGanttLinkData(gantt) {
  const organizationId = getCurrentOrganizationId();
  const apiPrefix = isTenantRoleLevel()
    ? `${HZERO_PLATFORM}/v1/${organizationId}`
    : `${HZERO_PLATFORM}/v1`;
  const res = await request(`${apiPrefix}/gantt-task-links/${gantt.serverList('ganttId')[0]}`, {
    method: 'GET',
  });
  const data = getResponse(res, (e) => e);
  return formatGanttLinkData(data) || [];
}

/**
 * 保存
 * @export
 * @param {object} params 传递参数
 */
export async function deleteGanttLinkData(link) {
  const organizationId = getCurrentOrganizationId();
  const apiPrefix = isTenantRoleLevel()
    ? `${HZERO_PLATFORM}/v1/${organizationId}`
    : `${HZERO_PLATFORM}/v1`;
  const res = await request(`${apiPrefix}/gantt-task-links`, {
    method: 'DELETE',
    body: link,
  });
  return getResponse(res, (e) => e);
}

/**
 * 获取数据
 * @export
 * @param {object} params 传递参数
 */
export function formatGanttTaskData(data = []) {
  const arr = data.map((item) => {
    const temp: {
      taskName: string;
      taskId: number;
      orderSeq: number;
      parent: number;
      startDate: string;
      endDate: string;
      taskRender: string;
      [propName: string]: any;
    } = item;
    return {
      ...temp,
      id: temp.taskId,
      text: temp.taskName,
      order: temp.orderSeq,
      start_date: temp.startDate.substr(0, 10),
      end_date: temp.endDate.substr(0, 10),
      parent: temp.parentId || null,
      render: [temp.taskRender],
    };
  });
  return arr;
}

/**
 * 获取数据
 * @export
 * @param {object} params 传递参数
 */
export function formatGanttLinkData(data = []) {
  const arr = data.map((item) => {
    const temp: {
      linkType: string;
      taskFromId: number;
      taskTargetId: number;
      taskLinkId: number;
      [propName: string]: any;
    } = item;
    return {
      ...temp,
      type: temp.linkType,
      source: temp.taskFromId,
      target: temp.taskTargetId,
      id: temp.taskLinkId,
    };
  });
  return arr;
}
