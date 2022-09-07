/*
 * AlertRuleService 告警规则配置Services
 * @date: 2020-05-18
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @copyright Copyright (c) 2020, Hand
 */
import request from 'utils/request';
import { isTenantRoleLevel, getCurrentOrganizationId } from 'utils/utils';
import { HZERO_ALT } from 'utils/config';

const organizationId = getCurrentOrganizationId();
const isTenant = isTenantRoleLevel();
const apiPrefix = isTenant ? `${HZERO_ALT}/v1/${organizationId}` : `${HZERO_ALT}/v1`;

/**
 * 启动调度
 * @param {object} params
 * @returns
 */
export async function startSchedule(param) {
  return request(`${apiPrefix}/alert-schedulers/start`, {
    method: 'GET',
    query: param,
  });
}

/**
 * 终止调度
 * @param {object} params
 * @returns
 */
export async function stopSchedule(param) {
  return request(`${apiPrefix}/alert-schedulers/stop`, {
    method: 'GET',
    query: param,
  });
}

/**
 * 暂停调度
 * @param {object} params
 * @returns
 */
export async function pauseSchedule(param) {
  return request(`${apiPrefix}/alert-schedulers/pause`, {
    method: 'GET',
    query: param,
  });
}

/**
 * 恢复调度
 * @param {object} params
 * @returns
 */
export async function resumeSchedule(param) {
  return request(`${apiPrefix}/alert-schedulers/resume`, {
    method: 'GET',
    query: param,
  });
}

/**
 * 重新加载数据处理列表
 * @param {number} alertSourceId - 数据来源id
 */
export async function reloadHandleData(alertSourceId) {
  return request(`${apiPrefix}/alert-rule-param-maps/${alertSourceId}/reload`, {
    method: 'POST',
  });
}
