/**
 * lovService - 值集视图配置Service
 * @date: 2018-6-26
 * @author: lokya <kan.li01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import request from 'utils/request';
import { HZERO_PLATFORM } from 'utils/config';
import { getCurrentOrganizationId, parseParameters, isTenantRoleLevel } from 'utils/utils';

const organizationId = getCurrentOrganizationId();
const urlPrefix = `${HZERO_PLATFORM}/v1${isTenantRoleLevel() ? `/${organizationId}/` : `/`}`;
/**
 * 查询lov数据
 * @async
 * @function queryLovList
 * @param {object} params - 查询条件
 * @param {?string} params.viewCode - 视图编码
 * @param {?string} params.viewName - 视图名称
 * @param {!number} [params.page = 0] - 数据页码
 * @param {!number} [params.size = 10] - 分页大小
 * @returns {object} fetch Promise
 */
export async function queryLovList(params) {
  const query = parseParameters(params);
  return request(`${urlPrefix}lov-view-headers`, {
    method: 'GET',
    query,
  });
}

/**
 * 保存数据
 * @async
 * @function addLovValue
 * @param {object} params.data - 待保存数据
 * @param {!string} params.data.viewCode - 视图编码
 * @param {!string} params.data.viewName - 视图名称
 * @param {!string} params.data.valueField - valueField
 * @param {!string} params.data.displayField - displayField
 * @param {!string} params.data.lovId - 值集id
 * @param {!number} params.data.delayLoadFlag - 延时标记
 * @param {!number} params.data.enabledFlag - 启用标记
 * @returns {object} fetch Promise
 */
export async function addLovValue(params) {
  return request(`${urlPrefix}lov-view-headers`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 删除数据
 * @async
 * @function deleteLovValue
 * @param {object[]} params.deleteArr - 删除数据集合
 * @param {!string} params.deleteArr[].viewHeaderId - 视图头id
 * @param {!string} params.deleteArr[].objectVersionNumber - 版本号
 * @returns {object} fetch Promise
 */
export async function deleteLovValue(params) {
  return request(`${urlPrefix}lov-view-headers`, {
    method: 'DELETE',
    body: params,
  });
}

/**
 * 查询lov头数据
 * @async
 * @function queryHeadValue
 * @param {object[]} params.data - 待查询数据
 * @param {!string} params.data.viewHeaderId - 视图头id
 * @returns {object} fetch Promise
 */
export async function queryHeadValue(params) {
  return request(`${urlPrefix}lov-view-headers/${params.viewHeaderId}`, {
    method: 'GET',
  });
}

/**
 * 查询lov行数据
 * @async
 * @function queryLineValue
 * @param {object[]} params.data - 待查询数据
 * @param {!string} params.data.viewHeaderId - 视图头id
 * @param {!number} params.page - 数据页码
 * @param {!number} params.size - 分页大小
 * @returns {object} fetch Promise
 */
export async function queryLineValue(params = {}) {
  const pagination = parseParameters(params);
  return request(`${urlPrefix}lov-view-headers/${params.viewHeaderId}/lines`, {
    method: 'GET',
    query: pagination,
  });
}

/**
 * 增加lov行数据
 * @async
 * @function addLineData
 * @param {object[]} params.data - 待保存数据
 * @param {!string} params.data.viewHeaderId - 视图头id
 * @param {!string} params.tenantId - 租户id
 * @param {!number} params.tableFieldWidth - 表格列宽度
 * @param {!number} params.enabledFlag - 启用标志
 * @param {!number} params.queryFieldFlag - 是否查询字段标志
 * @param {!number} params.tableFieldFlag - 是否表格列标志
 * @param {!number} params.orderSeq - 列序号
 * @param {!string} params.fieldName - 表格字段名
 * @param {!string} params.display - 表格字段标题
 * @returns {object} fetch Promise
 */
export async function addLineData(params) {
  return request(`${urlPrefix}/lov-view-lines`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 更新lov行数据
 * @async
 * @function editLine
 * @param {object[]} params.data - 待更新数据
 * @param {!string} params.data.viewHeaderId - 视图头id
 * @param {!string} params.tenantId - 租户id
 * @param {!number} params.tableFieldWidth - 表格列宽度
 * @param {!number} params.enabledFlag - 启用标志
 * @param {!number} params.queryFieldFlag - 是否查询字段标志
 * @param {!number} params.tableFieldFlag - 是否表格列标志
 * @param {!number} params.orderSeq - 列序号
 * @param {!string} params.fieldName - 表格字段名
 * @param {!string} params.display - 表格字段标题
 * @returns {object} fetch Promise
 */
export async function editLine(params) {
  return request(`${urlPrefix}/lov-view-lines`, {
    method: 'PUT',
    body: params,
  });
}

/**
 * 删除数据
 * @async
 * @function removeLine
 * @param {object[]} params.selectedRow - 待删除的数据集合
 * @param {!string} params.selectedRow[].viewLineId - 视图头id
 * @param {!string} params.selectedRow[].objectVersionNumber - 版本号
 * @returns {object} fetch Promise
 */
export async function removeLine(params) {
  return request(`${urlPrefix}lov-view-lines`, {
    method: 'DELETE',
    body: params,
  });
}

/**
 * 更新lov头数据
 * @async
 * @function updateHeadValue
 * @param {object} params.data - 待更新数据
 * @param {!string} params.data.viewHeaderId - 视图编码
 * @param {!string} params.data.viewCode - 视图编码
 * @param {!string} params.data.viewName - 视图名称
 * @param {!string} params.data.valueField - valueField
 * @param {!string} params.data.displayField - displayField
 * @param {!string} params.data.lovId - 值集id
 * @param {!number} params.data.delayLoadFlag - 延时标记
 * @param {!number} params.data.enabledFlag - 启用标记
 * @returns {object} fetch Promise
 */
export async function updateHeadValue(params) {
  return request(`${urlPrefix}lov-view-headers`, {
    method: 'PUT',
    body: params,
  });
}

/**
 * 根据值集头 ID 查询值集值
 * @param {Object} params - 查询参数
 */
export async function copyLovView(params) {
  return request(`${urlPrefix}lov-view/copy`, {
    method: 'POST',
    body: params,
    query: params,
  });
}
