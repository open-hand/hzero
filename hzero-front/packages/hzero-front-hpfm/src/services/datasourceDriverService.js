/**
 * datasourceDriver-数据源驱动
 * @date: 2019-08-22
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */

import request from 'utils/request';
import { parseParameters, getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import { HZERO_PLATFORM } from 'utils/config';

const prefix = `${HZERO_PLATFORM}/v1/${
  isTenantRoleLevel() ? `${getCurrentOrganizationId()}/` : ``
}datasource-drivers`;

/**
 * 分页查询服务驱动列表
 * @param params
 * @returns {Promise<void>}
 */
export async function fetchDriversList(params) {
  return request(prefix, {
    method: 'GET',
    query: { ...parseParameters(params) },
  });
}

/**
 * 通过 driverId 查询服务驱动明细
 * @param params
 * @returns {Promise<void>}
 */
export async function fetchDriverById(params) {
  return request(`${prefix}/${params.driverId}`, {
    method: 'GET',
  });
}

/**
 * 创建服务驱动
 * @param params
 * @returns {Promise<void>}
 */
export async function createDriver(params) {
  return request(prefix, {
    method: 'POST',
    body: params,
  });
}

/**
 * 更新服务驱动
 * @param params
 * @returns {Promise<void>}
 */
export async function updateDriver(params) {
  return request(prefix, {
    method: 'PUT',
    body: params,
  });
}

/**
 * 删除服务驱动
 * @param params
 * @returns {Promise<Object>}
 */
export async function removeDriver(params) {
  return request(prefix, {
    method: 'DELETE',
    body: params,
  });
}
