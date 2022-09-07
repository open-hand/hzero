/**
 * category - 流程设置/流程分类
 * @date: 2018-8-21
 * @version: 1.0.0
 * @author: CJ <juan.chen01@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import request from 'utils/request';
import { HZERO_HWFP } from 'utils/config';
import { parseParameters, getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';

const organizationId = getCurrentOrganizationId();
const isSiteFlag = !isTenantRoleLevel();

const apiPrefix = isSiteFlag ? `${HZERO_HWFP}/v1` : `${HZERO_HWFP}/v1/${organizationId}`;

/**
 * 数据查询
 * @async
 * @function fetchCategories
 * @param {object} params,organizationId - 查询条件
 * @param {!number} organizationId - 租户id
 * @param {?string} params.code - 流程分类编码
 * @param {?string} params.description - 流程分类描述
 * @param {!number} [params.page = 0] - 数据页码
 * @param {!number} [params.size = 10] - 分页大小
 * @returns {object} fetch Promise
 */
export async function fetchCategories(params) {
  const query = parseParameters(params);
  return request(`${apiPrefix}/process/categories`, {
    method: 'GET',
    query,
  });
}

/**
 * 添加流程分类信息
 * @async
 * @function createCategories
 * @param {object} params,organizationId - 请求参数
 * @param {object} organizationId - 租户id
 * @param {?string} params.code - 流程分类编码
 * @param {?string} params.description - 流程分类描述
 * @returns {object} fetch Promise
 */
export async function createCategories(params) {
  return request(`${apiPrefix}/process/categories`, {
    method: 'POST',
    body: params,
  });
}
/**
 * 修改流程单据头
 * @async
 * @function updateHeader
 * @param {object} params,organizationId,processCategoryId - 请求参数
 * @param {object} organizationId - 租户id
 * @param {?string} params.code - 流程分类编码
 * @param {?string} params.description - 流程分类描述
 * @param {!string} params.processCategoryId - processCategoryId
 * @param {!string} params.objectVersionNumber - 版本号
 * @returns {object} fetch Promise
 */
export async function updateHeader(params) {
  const { categoryId, processDocument } = params;
  return request(`${apiPrefix}/process/categories/${categoryId}`, {
    method: 'PUT',
    body: processDocument,
  });
}
/**
 * 编辑流程分类信息
 * @async
 * @function editCategories
 * @param {object} params,organizationId,processCategoryId - 请求参数
 * @param {object} organizationId - 租户id
 * @param {?string} params.code - 流程分类编码
 * @param {?string} params.description - 流程分类描述
 * @param {!string} params.processCategoryId - processCategoryId
 * @param {!string} params.objectVersionNumber - 版本号
 * @returns {object} fetch Promise
 */
export async function editCategories(processCategoryId, params) {
  return request(`${apiPrefix}/process/categories/${processCategoryId}`, {
    method: 'PUT',
    body: params,
  });
}

/**
 * 删除流程分类信息
 * @async
 * @function deleteCategories
 * @param {object} params,organizationId,categoryId - 请求参数
 * @param {number} categoryId - processVariableId
 * @param {number} organizationId - 租户id
 * @param {?string} params.code - 流程分类编码
 * @param {?string} params.description - 流程分类描述
 * @param {!string} params.objectVersionNumber - 版本号
 * @returns {object} fetch Promise
 */
export async function deleteCategories(categoryId, params) {
  return request(`${apiPrefix}/process/categories/${categoryId}`, {
    method: 'DELETE',
    body: params,
  });
}
/**
 * 查询流程单据
 * @async
 * @function handleSearchDocuments
 * @param {object} params,organizationId - 查询条件
 * @param {!number} organizationId - 租户id
 * @param {?string} params.code - 流程单据编码
 * @param {?string} params.description - 流程单据描述
 * @param {!number} [params.page = 0] - 数据页码
 * @param {!number} [params.size = 10] - 分页大小
 * @returns {object} fetch Promise
 */
export async function handleSearchDocuments(params) {
  const query = { ...parseParameters(params), enabledFlag: 1 };
  return request(`${apiPrefix}/process/documents`, {
    method: 'GET',
    query,
  });
}
/**
 * 查询详情头
 * @async
 * @function fetchDetailHeader
 * @param {object} params,documentId - 查询条件
 * @returns {object} fetch Promise
 */
export async function fetchDetailHeader(query) {
  const { categoryId } = query;
  return request(`${apiPrefix}/process/categories/${categoryId}`, {
    method: 'GET',
  });
}
/**
 * 查询详情列表
 * @async
 * @function fetchDetailList
 * @param {object} params,organizationId - 查询条件
 * @param {!number} organizationId - 租户id
 * @param {?string} params.code - 流程分类编码
 * @param {?string} params.description - 流程分类描述
 * @param {!number} [params.page = 0] - 数据页码
 * @param {!number} [params.size = 10] - 分页大小
 * @returns {object} fetch Promise
 */
export async function fetchDetailList(params) {
  const { sourceType, sourceId } = params;
  return request(`${apiPrefix}/process/variables/${sourceId}/${sourceType}/list`, {
    method: 'GET',
  });
}
/**
 * 保存详情流程变量
 * @async
 * @function handleSaveVariables
 * @param {number} processCategoryId - processVariableId
 * @param {number} organizationId - 租户id
 * @param {?string} params.code - 流程分类编码
 * @param {?string} params.description - 流程分类描述
 * @param {!string} params.objectVersionNumber - 版本号
 * @returns {object} fetch Promise
 */
export async function handleSaveVariables(params) {
  return request(`${apiPrefix}/process/variables`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 更新详情流程变量
 * @async
 * @function handleUpdateVariables
 * @param {number} processCategoryId - processVariableId
 * @param {number} organizationId - 租户id
 * @param {?string} params.code - 流程分类编码
 * @param {?string} params.description - 流程分类描述
 * @param {!string} params.objectVersionNumber - 版本号
 * @returns {object} fetch Promise
 */
export async function handleUpdateVariables(params) {
  const { variableId, processVariable } = params;
  return request(`${apiPrefix}/process/variables/${variableId}`, {
    method: 'PUT',
    body: processVariable,
  });
}
/**
 * 删除流程变量
 * @async
 * @function deleteVariable
 * @param {number} processCategoryId - processVariableId
 * @param {number} organizationId - 租户id
 * @param {?string} params.code - 流程分类编码
 * @param {?string} params.description - 流程分类描述
 * @param {!string} params.objectVersionNumber - 版本号
 * @returns {object} fetch Promise
 */
export async function deleteVariable(params) {
  const { variableId, processVariable } = params;
  return request(`${apiPrefix}/process/variables/${variableId}`, {
    method: 'DELETE',
    body: processVariable,
  });
}
