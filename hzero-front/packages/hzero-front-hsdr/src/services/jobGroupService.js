/**
 * model 任务执行器
 * @date: 2018-9-3
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
 * 查询执行器列表数据
 * @async
 * @function fetchGroupsList
 * @param {object} params - 查询条件
 */
export async function fetchGroupsList(params) {
  const param = parseParameters(params);
  return request(
    organizationRoleLevel ? `${prefix}/${organizationId}/executors` : `${prefix}/executors`,
    {
      method: 'GET',
      query: param,
    }
  );
}

/**
 * 创建执行器
 * @async
 * @function createGroups
 * @param {object} params - 创建参数
 */
export async function createGroups(params) {
  return request(
    organizationRoleLevel ? `${prefix}/${organizationId}/executors` : `${prefix}/executors`,
    {
      method: 'POST',
      body: params,
    }
  );
}

/**
 * 更新执行器
 * @async
 * @function updateGroups
 * @param {object} params - 参数
 */
export async function updateGroups(params) {
  return request(
    organizationRoleLevel ? `${prefix}/${organizationId}/executors` : `${prefix}/executors`,
    {
      method: 'PUT',
      body: params,
    }
  );
}

/**
 * 删除执行器
 * @async
 * @function deleteGroups
 * @param {object} params - 参数
 */
export async function deleteGroups(params) {
  return request(
    organizationRoleLevel ? `${prefix}/${organizationId}/executors` : `${prefix}/executors`,
    {
      method: 'DELETE',
      body: params,
    }
  );
}

/**
 * 查询执行器配置列表数据
 * @async
 * @function fetchGroupsList
 * @param {object} params - 查询条件
 */
export async function fetchExecutorList(params) {
  return request(
    organizationRoleLevel
      ? `${prefix}/${organizationId}/executor-configs/${params.executorId}/config`
      : `${prefix}/executor-configs/${params.executorId}/config`,
    {
      method: 'GET',
      query: params,
    }
  );
}

/**
 * 效验执行器配置列表数据
 * @async
 * @function fetchGroupsList
 * @param {object} params - 查询条件
 */
export async function checkExecutor(params) {
  return request(
    organizationRoleLevel
      ? `${prefix}/${organizationId}/executor-configs/${params.executorId}/check`
      : `${prefix}/executor-configs/${params.executorId}/check`,
    {
      method: 'GET',
      query: params,
    }
  );
}

/**
 * 删除执行器配置项
 * @async
 * @function deleteExecutor
 * @param {object} params - 查询条件
 */
export async function deleteExecutor(params) {
  return request(
    organizationRoleLevel
      ? `${prefix}/${organizationId}/executor-configs`
      : `${prefix}/executor-configs`,
    {
      method: 'DELETE',
      body: params,
    }
  );
}

/**
 * 更新执行器配置项
 * @async
 * @function updateExecutor
 * @param {object} params - 查询条件
 */
export async function updateExecutor(params) {
  return request(
    organizationRoleLevel
      ? `${prefix}/${organizationId}/executor-configs`
      : `${prefix}/executor-configs`,
    {
      method: 'POST',
      body: params,
    }
  );
}

/**
 * 执行器列表配置
 * @async
 * @function executorConfig
 * @param {object} params - 查询条件
 */
export async function executorConfig(params) {
  return request(
    organizationRoleLevel
      ? `${prefix}/${organizationId}/executor-configs/${params.executorId}/config`
      : `${prefix}/executor-configs/${params.executorId}/config`,
    {
      method: 'GET',
      query: params,
    }
  );
}

/**
 * 执行器列表配置
 * @async
 * @function executorConfig
 * @param {object} params - 查询条件
 */
export async function executorConfigByJob(params) {
  const { executorId, ...others } = params;
  return request(
    organizationRoleLevel
      ? `${prefix}/${organizationId}/executor-configs/${executorId}/by-job`
      : `${prefix}/executor-configs/${executorId}/by-job`,
    {
      method: 'GET',
      query: others,
    }
  );
}
