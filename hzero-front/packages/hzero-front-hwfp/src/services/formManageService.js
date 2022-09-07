/**
 * service - 流程设置/表单管理
 * @date: 2018-8-15
 * @version: 1.0.0
 * @author: CJ <juan.chen01@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */

import request from 'utils/request';
import { HZERO_HWFP } from 'utils/config';
import { parseParameters } from 'utils/utils';

/**
 * 获取流程分类
 * @async
 * @function searchCategory
 * @param {object} params - 查询条件
 * @returns {object} fetch Promise
 */
export async function searchCategory(params) {
  return request(`${HZERO_HWFP}/v1/${params.tenantId}/process/categories`, {
    method: 'GET',
    query: { ...params },
  });
}
/**
 * 数据查询
 * @async
 * @function searchFormList
 * @param {object} params,tenantId - 查询条件
 * @param {!number} tenantId - 租户id
 * @param {?string} params.category - 流程分类
 * @param {?string} params.code - 编码
 * @param {?string} params.invokeFlag - 是否回调
 * @param {!number} [params.page = 0] - 数据页码
 * @param {!number} [params.size = 10] - 分页大小
 * @returns {object} fetch Promise
 */
export async function searchFormList(params) {
  const { tenantId, ...otherParams } = params;
  const param = parseParameters(otherParams);
  return request(`${HZERO_HWFP}/v1/${tenantId}/forms`, {
    method: 'GET',
    query: param,
  });
}

/**
 * 添加表单管理信息
 * @async
 * @function creatOne
 * @param {object} params,tenantId - 请求参数
 * @param {object} tenantId - 租户id
 * @param {!object} params.code - 编码
 * @param {!string} params.url - 表单url
 * @param {?number} params.description - 描述
 * @param {!string} params.category - 流程分类
 * @param {!number} params.invokeFlag - 是否回调
 * @param {!number} params.scope - 数据范围
 * @returns {object} fetch Promise
 */
export async function creatOne(tenantId, params) {
  return request(`${HZERO_HWFP}/v1/${tenantId}/forms`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 编辑表单管理信息
 * @async
 * @function editOne
 * @param {object} params,tenantId - 请求参数
 * @param {object} tenantId - 租户id
 * @param {!object} params.code - 编码
 * @param {!string} params.url - 表单url
 * @param {?number} params.description - 描述
 * @param {!string} params.category - 流程分类
 * @param {!number} params.invokeFlag - 是否回调
 * @param {!string} params.formDefinitionId - formDefinitionId
 * @param {!string} params.objectVersionNumber - 版本号
 * @returns {object} fetch Promise
 */
export async function editOne(tenantId, formDefinitionId, params) {
  return request(`${HZERO_HWFP}/v1/${tenantId}/forms/${formDefinitionId}`, {
    method: 'PUT',
    body: params,
  });
}

/**
 * 删除表单管理信息
 * @async
 * @function deleteOne
 * @param {object} params,tenantId,formDefinitionId - 请求参数
 * @param {number} formDefinitionId - formDefinitionId
 * @param {number} tenantId - 租户id
 * @param {!object} params.code - 编码
 * @param {!string} params.url - 表单url
 * @param {?number} params.description - 描述
 * @param {!string} params.category - 流程分类
 * @param {!number} params.invokeFlag - 是否回调
 * @param {!string} params.objectVersionNumber - 版本号
 * @returns {object} fetch Promise
 */
export async function deleteOne(tenantId, formDefinitionId, params) {
  return request(`${HZERO_HWFP}/v1/${tenantId}/forms/${formDefinitionId}`, {
    method: 'DELETE',
    body: params,
  });
}
/**
 * 条件编码唯一性校验
 * @async
 * @function checkUniqueCode
 * @param {*} params - 请求参数
 * @param {!string} params.tenantId - 租户ID
 * @param {!object} params.code - 编码
 */
export async function checkUniqueCode(params) {
  return request(`${HZERO_HWFP}/v1/${params.tenantId}/forms/check`, {
    method: 'POST',
    body: { code: params.code },
    // responseType: 'text',
  });
}
