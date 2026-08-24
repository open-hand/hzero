/**
 * service 员工定义
 * @date: 2018-6-27
 * @version: 0.0.1
 * @author: WH <heng.wei@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import { filterNullValueObject, parseParameters } from 'utils/utils';
import request from 'utils/request';
import { HZERO_IAM, HZERO_PLATFORM } from 'utils/config';

// const HZERO_PLATFORM = `/hpfm-16353`;
/**
 * 请求API前缀
 * @type {string}
 */
const prefix = `${HZERO_PLATFORM}/v1`;

/**
 * 查询员工信息
 * @async
 * @function searchEmployee
 * @param {object} params - 请求参数
 * @param {!number} params.tenantId - 租户Id
 * @param {!object} params.page - 分页参数
 * @param {?string} params.employeeNum - 员工编码
 * @param {?string} params.name - 员工姓名
 * @returns {object} fetch Promise
 */
export async function searchEmployee(params) {
  const param = parseParameters(params);
  return request(`${prefix}/${param.tenantId}/employees`, {
    method: `GET`,
    query: param,
  });
}

/**
 * 添加员工信息
 * @async
 * @function addEmployee
 * @param {object} params - 请求参数
 * @param {!number} params.tenantId - 租户Id
 * @param {!array<object>} params.saveData - 新增员工信息列表
 * @returns {object} fetch Promise
 */
export async function addEmployee(params) {
  return request(`${prefix}/${params.tenantId}/employees`, {
    method: 'POST',
    query: { tenantId: params.tenantId, flexModelCode: 'HPFM.EMPLOYEE' },
    body: [...params.saveData],
  });
}

/**
 * 新增/更新员工信息
 * @async
 * @function saveEmployee
 * @param {object} params - 请求参数
 * @param {!number} params.tenantId - 租户Id
 * @param {!number} params.positionId - 主岗Id
 * @param {!object[]} params.saveData - 待保存数据对象列表
 * @param {?string} params.saveData[].employeeNum - 员工编码
 * @param {!string} params.saveData[].name - 员工姓名
 * @returns {object} fetch Promise
 */
export async function saveEmployee(params) {
  const { tenantId, saveData, positionId, customizeUnitCode } = params;
  return request(`${prefix}/${tenantId}/employees/employee-position`, {
    method: `POST`,
    query: { positionId, tenantId, customizeUnitCode },
    body: { ...saveData },
  });
}

/**
 * 获取员工已分配岗位信息(非分页)
 * @async
 * @function fetchPosition
 * @param {object} params - 请求参数
 * @param {!number} params.tenantId - 租户Id
 * @param {!object[]} params.employeeId - 员工Id
 * @returns {object} fetch Promise
 */
export async function fetchPosition(params) {
  return request(`${prefix}/${params.tenantId}/employee-assigns`, {
    method: 'GET',
    query: { ...params },
  });
}

/**
 * 根据租户Id和员工Id，获取岗位信息(树形结构)
 * @async
 * @function searchPositionTree
 * @param {object} params - 请求参数
 * @param {!number} params.tenantId - 租户Id
 * @param {!string} params.employeeId - 员工Id
 * @returns {object} fetch Promise
 */
export async function searchPositionTree(params) {
  return request(`${prefix}/${params.tenantId}/unit-aggregate/${params.employeeId}`, {
    method: 'GET',
    query: filterNullValueObject(params),
  });
}

/**
 * 批量更新员工岗位分配信息
 * @async
 * @function updatePosition
 * @param {!object} params
 * @param {!number} params.tenantId - 租户Id
 * @param {!string} params.employeeId - 员工Id
 * @param {!array<object>} params.positionList - 岗位信息列表
 * @returns {object} fetch Promise
 */
export async function updatePosition(params) {
  return request(`${prefix}/${params.tenantId}/employee-assigns`, {
    method: 'POST',
    query: { employeeId: params.employeeId },
    body: [...params.positionList],
  });
}

/**
 * 获取员工已分配的用户信息(非分页)
 * @async
 * @function fetchUser
 * @param {object} params - 请求参数
 * @param {!number} params.tenantId - 租户Id
 * @param {!string} params.employeeId - 员工Id
 * @returns {Object} fetch Promise
 */
export async function fetchUser(params) {
  return request(`${prefix}/${params.tenantId}/employee-users`, {
    method: 'GET',
    query: { ...params },
  });
}

/**
 * 批量移除员工已分配的用户
 * @async
 * @function deleteUser
 * @param {object} params - 请求参数
 * @param {!number} params.tenantId - 租户Id
 * @param {!object[]} params.employeeUsers - 员工Id
 * @returns {Object} fetch Promise
 */
export async function deleteUser(params) {
  return request(`${prefix}/${params.tenantId}/employee-users`, {
    method: 'DELETE',
    body: [...params.data],
  });
}

/**
 * 批量添加员工-分配的用户
 * @async
 * @function updateUser
 * @param {object} params - 请求参数
 * @param {!number} params.tenantId - 租户Id
 * @param {!object[]} params.employeeUsers - 员工Id
 * @returns {Object} fetch Promise
 */
export async function updateUser(params) {
  return request(`${prefix}/${params.tenantId}/employee-users`, {
    method: 'POST',
    body: [...params.data],
  });
}

/**
 * 查询用户信息
 * @async
 * @function searchUserList
 * @param {Object} params - 接口参数
 * @param {Number} params.tenantId - 租户ID
 * @returns {Object} fetch Promise
 */
export async function searchUserList(params) {
  const param = parseParameters(params);
  return request(`${HZERO_IAM}/hzero/v1/${param.tenantId}/users/multi-tenant-list`, {
    method: 'GET',
    query: {
      excludeHasAssignedEmployeeFlag: true,
      ...param,
    },
  });
}

/**
 * 查询员工信息
 * @async
 * @function searchEmployeeById
 * @param {object} params - 请求参数
 * @param {!number} params.tenantId - 租户Id
 * @param {!object} params.page - 分页参数
 * @param {?string} params.employeeId - 员工编码
 * @returns {object} fetch Promise
 */
export async function searchEmployeeById(params) {
  const param = parseParameters(params);
  return request(`${prefix}/${param.tenantId}/employees/id`, {
    method: `GET`,
    query: param,
  });
}

/**
 * 处理接口获取的数据，提取每个节点的层次路径
 * @param {array} [collections = [] ] - 页面展示数据
 * @param {array} levelPath - 特定组织的层级路径
 * @returns {object} 节点树和层次路径组成的对象
 * @returns {Object} 节点路径对象
 */
export function renderTreeData(collections = [], levelPath = {}, parent = {}) {
  const pathMap = levelPath;
  collections.map(item => {
    const temp = item;
    const parentPath = parent.typeWithId ? pathMap[parent.typeWithId] || [] : [];
    pathMap[temp.typeWithId] = [...parentPath, temp.typeWithId];
    if (item.children) {
      renderTreeData(temp.children, pathMap, item);
    }
    return pathMap;
  });
  return pathMap;
}
