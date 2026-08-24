/**
 * service 员工维护与管理
 * @date: 2018-6-19
 * @version: 0.0.1
 * @author: WH <heng.wei@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */

import request from 'utils/request';
import { parseParameters } from 'utils/utils';
import { HZERO_PLATFORM } from 'utils/config';
// const HZERO_PLATFORM = `/hpfm-16353`;
/**
 * 请求API前缀
 * @type {string}
 */
const prefix = `${HZERO_PLATFORM}/v1`;
/**
 * 获取岗位信息
 * @async
 * @function searchPositionInfo
 * @param {object} params - 请求参数
 * @param {!number} params.tenantId - 租户Id
 * @param {!number} params.positionId - 岗位Id
 * @returns {object} fetch Promise
 */
export async function searchPositionInfo(params) {
  return request(`${prefix}/${params.tenantId}/companies/units/positions/${params.positionId}`, {
    method: 'GET',
    // body: params,
  });
}
/**
 * 获取岗位可添加员工信息列表
 * @async
 * @function searchAddibleStaff
 * @param {object} params - 请求参数
 * @param {!number} params.tenantId - 租户Id
 * @param {!number} params.positionId - 岗位Id
 * @param {!number} params.unitId - 部门Id
 * @param {!number} [params.page = {}] - 页码
 * @returns {object} fetch Promise
 */
export async function searchAddibleStaff(params) {
  const { tenantId, ...otherParams } = params;
  const param = parseParameters(otherParams);
  return request(`${prefix}/${tenantId}/employee-assigns/not-in-position`, {
    method: 'GET',
    query: { ...param, tenantId },
  });
}
/**
 * 获取岗位已维护员工信息列表
 * @async
 * @function searchAddedStaff
 * @param {object} params - 请求参数
 * @param {!number} params.tenantId - 租户Id
 * @param {!number} params.positionId - 岗位Id
 * @param {!number} params.unitId - 部门Id
 * @param {!object} [params.page = {}] - 分页参数
 * @returns {object} fetch Promise
 */
export async function searchAddedStaff(params) {
  const param = parseParameters(params);
  return request(`${prefix}/${param.tenantId}/employee-assigns/in-position`, {
    method: 'GET',
    query: param,
  });
}
/**
 * 岗位移除员工
 * @async
 * @function deleteStaff
 * @param {object} params - 请求参数
 * @param {!number} params.tenantId - 租户Id
 * @param {!number} params.positionId - 岗位Id
 * @param {!number} params.unit - 部门Id
 * @param {!array} params.employeeIdList - 员工id列表
 * @returns {object} fetch Promise
 */
export async function deleteStaff(params) {
  const { employeeList, ...query } = params;
  return request(`${prefix}/${params.tenantId}/employee-assigns/position`, {
    method: 'DELETE',
    query: { ...query },
    body: employeeList,
  });
}
/**
 * 岗位添加员工
 * @async
 * @function addStaff
 * @param {object} params - 请求参数
 * @param {!number} params.tenantId - 租户Id
 * @param {!number} params.positionId - 岗位Id
 * @param {!number} params.unit - 部门Id
 * @param {!array} params.employeeIdList - 员工id列表
 * @returns {object} fetch Promise
 */
export async function addStaff(params) {
  const { employeeList, ...query } = params;
  return request(`${prefix}/${params.tenantId}/employee-assigns/position`, {
    method: 'POST',
    query: { ...query },
    body: employeeList,
  });
}
