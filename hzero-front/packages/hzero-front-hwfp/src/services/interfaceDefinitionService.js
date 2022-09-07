/**
 * 接口定义
 * @date: 2019-4-26
 * @version: 1.0.0
 * @author: wangjiacheng <jiacheng.wang@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */

import request from 'utils/request';
import { HZERO_HWFP } from 'utils/config';
import { isTenantRoleLevel, getCurrentOrganizationId } from 'utils/utils';

const apiConfig = () =>
  isTenantRoleLevel() ? `v1/${getCurrentOrganizationId()}/interfaces` : 'v1/interfaces';

/**
 * 列表查询
 * @async
 * @function fetchList
 * @param {object} params - 查询条件
 * @returns {object} fetch Promise
 */
export async function fetchList(params) {
  return request(`${HZERO_HWFP}/${apiConfig()}`, {
    method: 'GET',
    query: params,
  });
}

/**
 * 删除列表数据
 * @async
 * @function deleteInterface
 * @param {object} params - 查询条件
 * @returns {object} fetch Promise
 */
export async function deleteInterface(params) {
  const { tenantId, ...other } = params;
  return request(`${HZERO_HWFP}/${apiConfig()}/${other.interfaceId}`, {
    method: 'DELETE',
    body: other,
  });
}

/**
 * 新建接口定义
 * @async
 * @function createInterface
 * @param {object} params - 查询条件
 * @returns {object} fetch Promise
 */
export async function createInterface(params) {
  return request(`${HZERO_HWFP}/${apiConfig()}`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 更新接口定义
 * @async
 * @function updateInterface
 * @param {object} params - 查询条件
 * @returns {object} fetch Promise
 */
export async function updateInterface(params) {
  const { tenantId, ...other } = params;
  return request(`${HZERO_HWFP}/${apiConfig()}/${other.interfaceId}`, {
    method: 'PUT',
    body: other,
  });
}

/**
 * 详情查询
 * @async
 * @function fetchDetail
 * @param {object} params - 查询条件
 * @returns {object} fetch Promise
 */
export async function fetchDetail(params) {
  const { tenantId, ...other } = params;
  return request(`${HZERO_HWFP}/${apiConfig()}/${other.interfaceId}`, {
    method: 'GET',
    query: other,
  });
}

/**
 * 新建参数
 * @async
 * @function createParam
 * @param {object} params - 查询条件
 * @returns {object} fetch Promise
 */
export async function createParam(params) {
  const { tenantId, ...other } = params;
  return request(`${HZERO_HWFP}/${apiConfig()}/${other.interfaceId}/parameter`, {
    method: 'POST',
    body: other,
  });
}

/**
 * 更新参数
 * @async
 * @function updateParam
 * @param {object} params - 查询条件
 * @returns {object} fetch Promise
 */
export async function updateParam(params) {
  const { tenantId, ...other } = params;
  return request(
    `${HZERO_HWFP}/${apiConfig()}/${other.interfaceId}/parameter/${other.parameterId}`,
    {
      method: 'PUT',
      body: other,
    }
  );
}

/**
 * 删除参数
 * @async
 * @function deleteParam
 * @param {object} params - 查询条件
 * @returns {object} fetch Promise
 */
export async function deleteParam(params) {
  const { tenantId, ...other } = params;
  return request(
    `${HZERO_HWFP}/${apiConfig()}/${other.interfaceId}/parameter/${other.parameterId}`,
    {
      method: 'DELETE',
      body: other,
    }
  );
}

/**
 * 测试
 * @async
 * @function resultInterface
 * @param {object} params - 查询条件
 * @returns {object} fetch Promise
 */
export async function resultInterface(params) {
  const { args, ...other } = params;
  return request(`${HZERO_HWFP}/${apiConfig()}/result`, {
    method: 'POST',
    query: { tenantId: getCurrentOrganizationId(), ...other },
    body: args,
  });
}
