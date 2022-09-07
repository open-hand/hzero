/**
 * 服务定义
 * @date: 2019-5-7
 * @version: 1.0.0
 * @author: wangjiacheng <jiacheng.wang@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */

import request from 'utils/request';
import { HZERO_HWFP } from 'utils/config';
import { isTenantRoleLevel, getCurrentOrganizationId } from 'utils/utils';

const isSiteFlag = !isTenantRoleLevel();
const tenantId = getCurrentOrganizationId();
const apiPrefix = isSiteFlag ? `${HZERO_HWFP}/v1/service` : `${HZERO_HWFP}/v1/${tenantId}/service`;

/**
 * 列表查询
 * @async
 * @function fetchList
 * @param {object} params - 查询条件
 * @returns {object} fetch Promise
 */
export async function fetchList(params) {
  return request(`${apiPrefix}`, {
    method: 'GET',
    query: params,
  });
}

/**
 * 删除列表数据
 * @async
 * @function deleteService
 * @param {object} params - 查询条件
 * @returns {object} fetch Promise
 */
export async function deleteService(params) {
  return request(`${apiPrefix}/${params.serviceId}`, {
    method: 'DELETE',
    body: params,
  });
}

/**
 * 新建
 * @async
 * @function createService
 * @param {object} params - 查询条件
 * @returns {object} fetch Promise
 */
export async function createService(params) {
  return request(`${apiPrefix}`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 更新
 * @async
 * @function updateService
 * @param {object} params - 查询条件
 * @returns {object} fetch Promise
 */
export async function updateService(params) {
  return request(`${apiPrefix}/${params.serviceId}`, {
    method: 'PUT',
    body: params,
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
  return request(`${apiPrefix}/${params.serviceId}`, {
    method: 'GET',
    query: params,
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
  return request(`${apiPrefix}/${params.serviceId}/parameter/${params.interfaceParameterId}`, {
    method: 'PUT',
    body: params,
  });
}

/**
 * 根据接口定义查询参数
 * @async
 * @function queryParams
 * @param {object} params - 查询条件
 * @returns {object} fetch Promise
 */
export async function queryParams(params) {
  return request(`${apiPrefix}/interface/parameter`, {
    method: 'GET',
    query: params,
  });
}

/**
 * 获取流程变量
 * @async
 * @function fetchDocuments
 * @param {object} params - 查询条件
 * @returns {object} fetch Promise
 */
export async function fetchVariable(params) {
  return request(`${apiPrefix}/interface/variable`, {
    method: 'GET',
    query: params,
  });
}
