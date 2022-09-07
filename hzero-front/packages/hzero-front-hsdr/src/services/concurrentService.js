/**
 * service - 并发管理/请求定义（并发程序）
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
 * @function fetchConcurrentList
 * @param {object} params - 查询条件
 * @returns {object} fetch Promise
 */
export async function fetchConcurrentList(params) {
  const param = parseParameters(params);
  return request(
    organizationRoleLevel ? `${prefix}/${organizationId}/concurrents` : `${prefix}/concurrents`,
    {
      method: 'GET',
      query: param,
    }
  );
}
/**
 * 创建
 * @async
 * @function createConcurrent
 * @param {String} params - 保存参数
 */
export async function createConcurrent(params) {
  return request(
    organizationRoleLevel ? `${prefix}/${organizationId}/concurrents` : `${prefix}/concurrents`,
    {
      method: 'POST',
      body: params,
    }
  );
}

/**
 * 修改
 * @async
 * @function updateConcurrent
 * @param {String} params.enabledFlag - 是否启用
 */
export async function updateConcurrent(params) {
  return request(
    organizationRoleLevel ? `${prefix}/${organizationId}/concurrents` : `${prefix}/concurrents`,
    {
      method: 'PUT',
      body: params,
    }
  );
}

/**
 * 查询详情
 * @async
 * @function fetchConcurrentDetail
 * @param {Object} params - 查询参数
 */
export async function fetchConcurrentDetail(params) {
  return request(
    organizationRoleLevel
      ? `${HZERO_SDR}/v1/${organizationId}/concurrents/${params.concurrentId}`
      : `${HZERO_SDR}/v1/concurrents/${params.concurrentId}`,
    {
      method: 'GET',
    }
  );
}

/**
 * 删除并发程序参数（行）
 * @async
 * @function deleteLine
 * @param {object} params - 请求参数
 * @param {!string} params.concParamId - 参数ID
 */
export async function deleteLine(params) {
  return request(
    organizationRoleLevel
      ? `${prefix}/${organizationId}/concurrent-params`
      : `${prefix}/concurrent-params`,
    {
      method: 'DELETE',
      body: params,
    }
  );
}

/**
 * 获取已分配的权限
 * @async
 * @function fetchTemplateDetail
 * @param {object} params - 查询条件
 * @returns {object} fetch Promise
 */
export async function fetchAssignedPermission(params) {
  return request(
    organizationRoleLevel
      ? `${prefix}/${organizationId}/conc-permissions/${params.concurrentId}`
      : `${prefix}/conc-permissions/${params.concurrentId}`,
    {
      method: 'GET',
      query: params,
    }
  );
}

/**
 * 分配权限
 * @async
 * @function createPermission
 * @param {String} params - 保存参数
 */
export async function createPermission(params) {
  return request(
    organizationRoleLevel
      ? `${prefix}/${organizationId}/conc-permissions`
      : `${prefix}/conc-permissions`,
    {
      method: 'POST',
      body: params,
    }
  );
}

/**
 * 修改权限
 * @async
 * @function updatePermission
 * @param {String} params
 */
export async function updatePermission(params) {
  return request(
    organizationRoleLevel
      ? `${prefix}/${organizationId}/conc-permissions`
      : `${prefix}/conc-permissions`,
    {
      method: 'PUT',
      body: params,
    }
  );
}
