/**
 * industryCategory - 国标品类定义 - service
 * @date: 2018-7-24
 * @author: lokya <kan.li01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import request from 'utils/request';
import { HZERO_PLATFORM } from 'utils/config';
import { isTenantRoleLevel } from 'utils/utils';

function industriesApi(params) {
  return isTenantRoleLevel() ? `${params.tenantId}/industries` : 'industries';
}

/**
 * 查询一级行业
 * @async
 * @function queryTopCategory
 * @param {object} params - 查询条件
 * @param {?string} params.industryName - 一级行业名称
 * @returns {object} fetch Promise
 */
export async function queryTopCategory(params) {
  return request(`${HZERO_PLATFORM}/v1/${industriesApi(params)}`, {
    method: 'GET',
    query: params,
  });
}

/**
 * 查询二级行业
 * @async
 * @function querySecondCategory
 * @param {object} params - 查询条件
 * @param {!string} params.industryId - 一级行业id
 * @param {?string} params.industryName - 二级行业名称
 * @returns {object} fetch Promise
 */
export async function querySecondCategory(params) {
  return request(`${HZERO_PLATFORM}/v1/${industriesApi(params)}`, {
    method: 'GET',
    query: params,
  });
}

/**
 * 查询品类
 * @async
 * @function queryCategory
 * @param {object} params - 查询条件
 * @param {!string} params.industryId - 二级行业id
 * @param {!string} params.categoryName - 品类名称
 * @returns {object} fetch Promise
 */
export async function queryCategory(params) {
  return request(`${HZERO_PLATFORM}/v1/${industriesApi(params)}/${params.industryId}/categories`, {
    method: 'GET',
    query: params,
  });
}

/**
 * 新增行业
 * @async
 * @function saveIndustry
 * @param {object} params.data - 保存数据
 * @param {?string} params.industryId - 一级行业id
 * @param {!string} params.industryCode - 行业编码
 * @param {!string} params.industryName - 行业名称
 * @param {!string} params.enabledFlag - 启用标记
 * @returns {object} fetch Promise
 */
export async function saveIndustry(params) {
  return request(`${HZERO_PLATFORM}/v1/${industriesApi(params)}`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 新增品类
 * @async
 * @function saveCategory
 * @param {object} params.data - 保存数据
 * @param {?string} params.industryId - 二级行业id
 * @param {!string} params.categoryCode - 品类编码
 * @param {!string} params.categoryName - 品类名称
 * @param {!string} params.enabledFlag - 启用标记
 * @returns {object} fetch Promise
 */
export async function saveCategory(params) {
  return request(`${HZERO_PLATFORM}/v1/${industriesApi(params)}/${params.industryId}/categories`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 更新行业
 * @async
 * @function putIndustry
 * @param {object} params.data - 保存数据
 * @param {!string} params.industryId - 一级/二级行业id
 * @param {!string} params.industryCode - 行业编码
 * @param {!string} params.industryName - 行业名称
 * @param {!string} params.enabledFlag - 启用标记
 * @param {!string} params.objectVersionNumber - 版本
 * @returns {object} fetch Promise
 */
export async function putIndustry(params) {
  return request(`${HZERO_PLATFORM}/v1/${industriesApi(params)}`, {
    method: 'PUT',
    body: params,
  });
}

/**
 * 更新品类
 * @async
 * @function saveCategory
 * @param {object} params.data - 保存数据
 * @param {!string} params.industryId - 二级行业id
 * @param {!string} params.categoryId - 品类id
 * @param {!string} params.categoryCode - 品类编码
 * @param {!string} params.categoryName - 品类名称
 * @param {!string} params.enabledFlag - 启用标记
 * @param {!string} params.objectVersionNumber - 版本
 * @returns {object} fetch Promise
 */
export async function putCategory(params) {
  return request(`${HZERO_PLATFORM}/v1/${industriesApi(params)}/${params.industryId}/categories`, {
    method: 'PUT',
    body: params,
  });
}
