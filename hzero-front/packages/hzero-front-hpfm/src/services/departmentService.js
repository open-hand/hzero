/**
 * service 部门架构维护
 * @date: 2018-6-19
 * @version: 0.0.1
 * @author: WH <heng.wei@hand-china.com>
 * @copyright Copyright (c) 2018, Hands
 */

import request from 'utils/request';
import { filterNullValueObject } from 'utils/utils';
import { HZERO_PLATFORM } from 'utils/config';
// const HZERO_PLATFORM = `/hpfm-10902`;
/**
 * 请求API前缀
 * @type {string}
 */
const prefix = `${HZERO_PLATFORM}/v1`;
/**
 * 获取公司信息
 * @async
 * @function gainCodeAndName
 * @param {object} params - 请求参数
 * @param {!number} params.tenantId - 租户Id
 * @param {!number} params.unitId - 组织Id
 * @returns {object} fetch Promise
 */
export async function gainCodeAndName(params) {
  return request(`${prefix}/${params.tenantId}/units/${params.unitId}`, {
    method: 'GET',
    // query: params,
  });
}
/**
 * 获取所有部门信息
 * @async
 * @function searchAll
 * @param {object} params - 请求参数
 * @param {!string} params.tenantId - 租户Id
 * @param {!number} param.companyUnitId - 组织Id
 * @returns {object} fetch Promise
 */
export async function searchAll(params) {
  return request(`${prefix}/${params.tenantId}/units/department/tree`, {
    method: 'GET',
    query: filterNullValueObject(params),
  });
}
/**
 * 根据查询条件获取部门信息
 * @async
 * @function search
 * @param {object} params - 请求参数
 * @param {!string} params.tenantId - 租户Id
 * @param {!number} param.companyUnitId - 组织Id
 * @param {?string} param.unitCode - 部门编码
 * @param {?string} param.unitName - 部门名称
 * @returns {object} fetch Promise
 */
export async function search(params) {
  return request(`${prefix}/${params.tenantId}/units/department`, {
    method: 'GET',
    query: filterNullValueObject(params),
  });
}
/**
 * 添加部门信息
 * @async
 * @function saveAdd
 * @param {Object} params - 请求参数
 * @param {!string} params.tenantId - 租户Id
 * @param {!object[]} params.data - 新增部门对象列表
 * @param {!string} params.data[].unitCode - 部门编码
 * @param {!string} params.data[].unitName - 部门名称
 * @param {!number} params.data[].orderSeq - 排序号
 * @param {!string} [params.data[].unitTypeCode = D] - 部门类型编码
 * @param {?number} [params.data[].supervisorFlag = 0] - 主管组织标记
 * @param {?number} [params.data[].enabledFlag = 1 ] - 启用标记
 * @param {?string} params.data[].companyUnitId - 所属组织Id
 * @param {?number} params.data[].parentUnitId - 上级部门Id
 * @param {?string} params.data[].parentUnitName - 上级部门名称
 * @returns {object} fetch Promise
 */
export async function saveAdd(params) {
  return request(`${prefix}/${params.tenantId}/units`, {
    method: 'POST',
    body: [...params.data],
  });
}
/**
 * 更新部门信息
 * @async
 * @function saveEdit
 * @param {Object} params - 请求参数
 * @param {!string} params.tenantId - 租户Id
 * @param {!object} params.values - 部门信息对象
 * @param {!number} params.values.unitId - 部门Id
 * @param {!string} params.values.unitCode - 部门编码
 * @param {!string} params.values.unitName - 部门名称
 * @param {!number} params.values.orderSeq - 排序号
 * @param {!string} params.values.unitTypeCode - 部门类型编码
 * @param {?number} params.values.parentUnitId - 上级部门Id
 * @param {?number} params.values.enabledFlag - 启用标记
 * @param {?string} params.values.companyUnitId - 所属组织Id
 * @returns {object} fetch Promise
 */
export async function saveEdit(params) {
  return request(`${prefix}/${params.tenantId}/units`, {
    method: 'PUT',
    body: { ...params.values },
  });
}
/**
 * 禁用部门信息
 * @async
 * @function forbindLine
 * @param {Object} params - 请求参数
 * @param {!number} params.tenantId - 租户Id
 * @param {!number} params.unitId - 部门Id
 * @param {!string} params._token - token
 * @param {!number} params.objectVersionNumber - 版本号
 * @returns {object} fetch Promise
 */
export async function forbindLine(params) {
  return request(`${prefix}/${params.tenantId}/units/disable`, {
    method: 'POST',
    body: params,
  });
}
/**
 * 启用部门信息
 * @async
 * @function enabledLine
 * @param {!number} params.tenantId - 租户Id
 * @param {!number} params.unitId - 部门Id
 * @param {!string} params._token - token
 * @param {!number} params.objectVersionNumber - 版本号
 * @returns {object} fetch Promise
 */
export async function enabledLine(params) {
  return request(`${prefix}/${params.tenantId}/units/enable`, {
    method: 'POST',
    body: params,
  });
}
/**
 * 处理接口获取的数据，提取每个节点的层次路径
 * @param {array} collections - 页面展示数据
 * @param {array} levelPath - 特定组织的层级路径
 * @returns {object} 节点树和层次路径组成的对象
 */
export function renderTreeData(collections = [], levelPath = {}) {
  // debugger;
  const pathMap = levelPath;
  const renderTree = collections.map(item => {
    const temp = item;
    pathMap[temp.unitId] = [...(pathMap[temp.parentUnitId] || []), temp.unitId];
    if (temp.children) {
      temp.children = [...renderTreeData(temp.children || [], pathMap).renderTree];
    }
    return temp;
  });
  return {
    renderTree,
    pathMap,
  };
}
