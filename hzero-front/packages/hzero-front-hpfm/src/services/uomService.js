/**
 * service - 单位定义
 * @date: 2018-7-6
 * @version: 0.0.1
 * @author: WH <heng.wei@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */

import request from 'utils/request';
import { HZERO_PLATFORM } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel, parseParameters } from 'utils/utils';

/**
 * 请求API前缀
 * @type {string}
 */
const prefix = `${HZERO_PLATFORM}/v1${isTenantRoleLevel() ? `/${getCurrentOrganizationId()}` : ''}`;

/**
 * 查询平台级计量单位
 * @async
 * @function searchUomData
 * @param {object} params - 查询条件
 * @param {?string} params.uomName - 计量单位名称
 * @param {?string} params.uomCode - 计量单位编码
 * @param {!Object} params.page- 分页参数
 * @returns {object} fetch Promise
 */
export async function searchUomData(params) {
  const param = parseParameters(params);
  return request(`${prefix}/uom`, {
    method: 'GET',
    query: param,
  });
}

/**
 * 保存平台级计量单位
 * @async
 * @function saveUomData
 * @param {Object} params - 请求参数
 * @param {Object[]} params.saveData - 待保存的数据列表
 * @param {?string} params.saveData[].uomId - 计量单位ID
 * @param {!string} params.saveData[].uomCode - 计量单位编码
 * @param {!string} params.saveData[].uomName - 计量单位名称
 * @param {?string} params.saveData[].uomTypeCode - 计量单位类型编码
 * @param {number} [params.saveData[].tenantId = 0] - 租户Id
 * @param {number} [params.saveData[].enabledFlag = 1] - 启用标记
 * @returns {object} fetch Promise
 */
export async function saveUomData(params) {
  return request(`${prefix}/uom`, {
    method: 'POST',
    body: params,
  });
}
