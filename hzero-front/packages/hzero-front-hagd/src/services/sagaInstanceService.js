/**
 * sagaInstance - 事务实例
 * @date: 2018-12-24
 * @author: wangjiacheng <jiacheng.wang@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import request from 'utils/request';
import { HZERO_ASGARD } from 'utils/config';
import { isTenantRoleLevel, getCurrentOrganizationId } from 'utils/utils';

const tenantId = getCurrentOrganizationId();

function sagaInstanceApi() {
  return isTenantRoleLevel() ? `${tenantId}/sagas/instances` : `sagas/instances`;
}

function taskApi() {
  return isTenantRoleLevel() ? `${tenantId}/sagas/tasks/instances` : `sagas/tasks/instances`;
}

/**
 * 查询列表数据
 * @async
 * @function fetchSagaInstanceList
 * @param {Object} params - 查询参数
 * @param {String} [params.page = 0] - 页码
 * @param {String} [params.size = 0] - 页数
 */
export async function fetchSagaInstanceList(params) {
  return request(`${HZERO_ASGARD}/v1/${sagaInstanceApi()}`, {
    method: 'GET',
    query: params,
  });
}

/**
 * 查询详情
 * @async
 * @function querySagaInstanceDetail
 * @param {Object} params - 查询参数
 */
export async function querySagaInstanceDetail(params) {
  return request(`${HZERO_ASGARD}/v1/${sagaInstanceApi()}/${params.id}/details`, {
    method: 'GET',
    query: params,
  });
}

/**
 * 查询实例运行详情
 * @async
 * @function queryInstanceStatistic
 * @param {Object} params - 查询参数
 */
export async function queryInstanceRun(params) {
  return request(`${HZERO_ASGARD}/v1/${sagaInstanceApi()}/${params.id}`, {
    method: 'GET',
  });
}

/**
 * 查询实例统计数据
 * @async
 * @function queryInstanceStatistic
 * @param {Object} params - 查询参数
 */
export async function queryInstanceStatistic() {
  return request(`${HZERO_ASGARD}/v1/${sagaInstanceApi()}/statistics`, {
    method: 'GET',
  });
}

/**
 * 查询任务列表数据
 * @async
 * @function fetchSagaInstanceTaskList
 * @param {Object} params - 查询参数
 * @param {String} [params.page = 0] - 页码
 * @param {String} [params.size = 0] - 页数
 */
export async function fetchSagaInstanceTaskList(params) {
  return request(`${HZERO_ASGARD}/v1/${taskApi()}`, {
    method: 'GET',
    query: params,
  });
}

/**
 * 重试
 * @async
 * @function instanceRetry
 * @param {Object} params - 查询参数
 */
export async function instanceRetry(params) {
  return request(`${HZERO_ASGARD}/v1/${taskApi()}/${params.id}/retry`, {
    method: 'PUT',
    query: params,
  });
}

/**
 * 解锁
 * @async
 * @function instanceUnlock
 * @param {Object} params - 查询参数
 */
export async function instanceUnlock(params) {
  return request(`${HZERO_ASGARD}/v1/${taskApi()}/${params.id}/unlock-service`, {
    method: 'PUT',
    query: params,
  });
}
