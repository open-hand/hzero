/**
 * service - 期间定义(租户级)
 * @date: 2018-7-12
 * @version: 0.0.1
 * @author: WH <heng.wei@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */

import request from 'utils/request';
import { HZERO_PLATFORM } from 'utils/config';
import { parseParameters, getCurrentOrganizationId } from 'utils/utils';
/**
 * 请求API前缀
 * @type {string}
 */
const prefix = `${HZERO_PLATFORM}/v1`;

const organizationId = getCurrentOrganizationId();

/**
 * 租户级会计期查询
 * @async
 * @function searchHeader
 * @param {object} params - 查询条件
 * @param {!number} params.tenantId - 租户Id
 * @param {?string} params.periodSetName - 会计期名称
 * @param {?string} params.periodSetCode - 会计期编码
 * @param {!object} params.page - 分页参数
 * @returns {object} fetch Promise
 */
export async function searchHeader(params) {
  const param = parseParameters(params);
  return request(`${prefix}/${organizationId}/cost-centers`, {
    method: 'GET',
    query: { ...param },
  });
}

/**
 * 会计期保存
 * @async
 * @function savePeriodHeader
 * @param {Object} params - 请求参数
 * @param {!number} params.tenantId - 租户Id
 * @param {Object[]} param.saveData - 保存数据列表
 * @param {?number} param.saveData[].periodSetId - 会计期Id
 * @param {!string} param.saveData[].periodSetCode - 会计期编码
 * @param {!string} param.saveData[].periodSetName - 会计期名称
 * @param {?number} [param.saveData[].enabledFlag = 1] - 启用标记
 * @param {?number} param.saveData[].periodTotalCount - 期间总数
 * @param {?string} param.saveData[].refPeriodSetId - 引用期间Id
 * @param {?number} [param.saveData[].tenantId = 0] - 租户Id
 * @param {?number} param.saveData[].objectVersionNumber - 版本号
 * @returns {Object} fetch Promise
 */
export async function edit(params) {
  return request(`${prefix}/${organizationId}/cost-centers`, {
    method: 'PUT',
    body: params,
  });
}

export async function create(params) {
  return request(`${prefix}/${organizationId}/cost-centers`, {
    method: 'POST',
    body: params,
  });
}

export async function getDetail(params) {
  return request(`${prefix}/${organizationId}/cost-centers/detail/${params.costId}`, {
    method: 'GET',
  });
}
