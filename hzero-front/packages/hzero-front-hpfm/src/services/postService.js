/**
 * service 岗位维护与管理
 * @date: 2018-6-19
 * @version: 0.0.1
 * @author: WH <heng.wei@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */

import request from 'utils/request';
import { filterNullValueObject } from 'utils/utils';
import { HZERO_PLATFORM } from 'utils/config';
/**
 * 请求API前缀
 * @type {string}
 */
const prefix = `${HZERO_PLATFORM}/v1`;

/**
 * 获取部门信息
 * @async
 * @function gainInfo
 * @param {object} params - 请求参数
 * @param {!number} params.tenantId - 租户Id
 * @param {!number} params.unitId - 部门id
 * @returns {object} fetch Promise
 */
export async function gainInfo(params) {
  return request(`${prefix}/${params.tenantId}/units/${params.unitId}`, {
    method: 'GET',
  });
}
/**
 * 查询部门的岗位信息
 * @async
 * @function search
 * @param {object} params - 请求参数
 * @param {!number} params.tenantId - 租户Id
 * @param {!number} params.unitId - 部门id
 * @param {?string} param.postionCode - 岗位编码
 * @param {?string} param.postionName - 岗位名称
 * @returns {object} fetch Promise
 */
export async function search(params) {
  const { tenantId, unitId } = params;
  return request(`${prefix}/${tenantId}/companies/units/${unitId}/positions`, {
    method: 'GET',
    query: filterNullValueObject(params),
  });
}
/**
 * 保存新增岗位信息
 * @async
 * @function saveAdd
 * @param {object} params - 请求参数
 * @param {!number} params.tenantId - 租户Id
 * @param {!number} params.unitCompanyId - 部门所属公司Id
 * @param {!number} params.unitId - 部门id
 * @param {!object[]} params.saveData - 新增岗位列表
 * @param {!string} param.saveData[].postionCode - 岗位编码
 * @param {!string} param.saveData[].postionName - 岗位名称
 * @param {!number} param.saveData[].orderSeq - 排序号
 * @param {?number} param.saveData[].parentPositionId - 父级岗位id
 * @param {?string} param.saveData[].parentPositionName - 父级岗位名称
 * @param {!number} [param.saveData[].supervisorFlag = 0] - 主岗标记
 * @param {!number} [param.saveData[].enabledFlag= 1] - 启用标记
 * @returns {object} fetch Promise
 */
export async function saveAdd(params) {
  const { tenantId, unitCompanyId, unitId } = params;
  return request(`${prefix}/${tenantId}/companies/${unitCompanyId}/units/${unitId}/positions`, {
    method: 'POST',
    body: [...params.saveData],
  });
}
/**
 * 更新岗位信息
 * @async
 * @function saveEdit
 * @param {object} params - 请求参数
 * @param {!number} params.tenantId - 租户Id
 * @param {!number} params.unitId - 部门id
 * @param {!number} param.postionId - 岗位Id
 * @param {!object} params.data - 新增岗位列表
 * @param {!number} param.data.postionId - 岗位Id
 * @param {!string} param.data.postionCode - 岗位编码
 * @param {!string} param.data.postionName - 岗位名称
 * @param {!number} param.data.orderSeq - 排序号
 * @param {?number} param.data.parentPositionId - 父级岗位id
 * @param {?string} param.data.parentPositionName - 父级岗位名称
 * @param {!number} param.data.supervisorFlag - 主岗标记
 * @param {!number} param.data.enabledFlag - 启用标记
 * @returns {object} fetch Promise
 */
export async function saveEdit(params) {
  const { tenantId, unitCompanyId, unitId, positionId } = params;
  return request(
    `${prefix}/${tenantId}/companies/${unitCompanyId}/units/${unitId}/positions/${positionId}`,
    {
      method: 'PUT',
      body: { ...params.data },
    }
  );
}
/**
 * 禁用岗位信息
 * @async
 * @function forbindLine
 * @param {object} params - 请求参数
 * @param {!number} params.tenantId - 租户Id
 * @param {!number} params.unitCompanyId - 部门所属公司Id
 * @param {!number} params.unitId - 部门id
 * @param {!number} param.positionId - 岗位Id
 * @param {!number} param.objectVersionNumber - 版本号
 * @returns {object} fetch Promise
 */
export async function forbindLine(params) {
  const { tenantId, unitCompanyId, unitId, positionId } = params;
  return request(
    `${prefix}/${tenantId}/companies/${unitCompanyId}/units/${unitId}/positions/disable/${positionId}`,
    {
      method: 'POST',
      body: {
        positionId: params.positionId,
        objectVersionNumber: params.objectVersionNumber,
        _token: params._token,
      },
    }
  );
}
/**
 * 启用岗位信息
 * @async
 * @function enabledLine
 * @param {object} params - 请求参数
 * @param {!number} params.tenantId - 租户Id
 * @param {!number} params.unitCompanyId - 部门所属公司Id
 * @param {!number} params.unitId - 部门id
 * @param {!number} param.positionId - 岗位Id
 * @param {!number} param.objectVersionNumber - 版本号
 * @returns {object} fetch Promise
 */
export async function enabledLine(params) {
  const { tenantId, unitCompanyId, unitId, positionId } = params;
  return request(
    `${prefix}/${tenantId}/companies/${unitCompanyId}/units/${unitId}/positions/enable/${positionId}`,
    {
      method: 'POST',
      body: {
        positionId: params.positionId,
        objectVersionNumber: params.objectVersionNumber,
        _token: params._token,
      },
    }
  );
}
/**
 * 处理接口获取的数据，提取每个节点的层次路径
 * @param {array} collections - 页面展示数据
 * @param {array} levelPath - 特定岗位的层级路径
 * @returns {object} 节点树和层次路径组成的对象
 */
export function renderTreeData(collections, levelPath) {
  const pathMap = levelPath || {};
  const renderTree = collections.map(item => {
    const temp = item;
    pathMap[temp.positionId] = [...(pathMap[temp.parentPositionId] || []), temp.positionId];
    if (temp.children) {
      temp.children = [...renderTreeData(temp.children, pathMap).renderTree];
    }
    return temp;
  });
  return {
    renderTree,
    pathMap,
  };
}
