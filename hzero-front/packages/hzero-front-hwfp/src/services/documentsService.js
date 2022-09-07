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

function apiPrefix() {
  return isSiteFlag ? `${HZERO_HWFP}/v1` : `${HZERO_HWFP}/v1/${organizationId}`;
}

/**
 * 数据查询
 * @async
 * @function fetchList
 * @param {object} params,organizationId - 查询条件
 * @param {!number} organizationId - 租户id
 * @param {?string} params.code - 流程分类编码
 * @param {?string} params.description - 流程分类描述
 * @param {!number} [params.page = 0] - 数据页码
 * @param {!number} [params.size = 10] - 分页大小
 * @returns {object} fetch Promise
 */
export async function fetchList(params) {
  const query = parseParameters(params);
  return request(`${apiPrefix()}/process/documents`, {
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
  return request(`${apiPrefix()}/process/documents/detail`, {
    method: 'GET',
    query,
  });
}
/**
 * 查询详情变量列表
 * @async
 * @function fetchVariableList
 * @param {object} params,organizationId - 查询条件
 * @param {!number} organizationId - 租户id
 * @param {?string} params.code - 流程分类编码
 * @param {?string} params.description - 流程分类描述
 * @param {!number} [params.page = 0] - 数据页码
 * @param {!number} [params.size = 10] - 分页大小
 * @returns {object} fetch Promise
 */
export async function fetchVariableList(params) {
  const { sourceType, sourceId } = params;
  return request(`${apiPrefix()}/process/variables/${sourceId}/${sourceType}/list`, {
    method: 'GET',
  });
}

/**
 * 查询详情表单列表
 * @async
 * @function fetchFormList
 * @param {object} params,organizationId - 查询条件
 * @param {!number} organizationId - 租户id
 * @param {?string} params.code - 流程分类编码
 * @param {?string} params.description - 流程分类描述
 * @param {!number} [params.page = 0] - 数据页码
 * @param {!number} [params.size = 10] - 分页大小
 * @returns {object} fetch Promise
 */
export async function fetchFormList(params) {
  const { sourceId } = params;
  return request(`${apiPrefix()}/forms/${sourceId}`, {
    method: 'GET',
  });
}

/**
 * 查询详情邮件列表
 * @async
 * @function fetchFormList
 * @param {object} params,organizationId - 查询条件
 * @param {!number} organizationId - 租户id
 * @param {?string} params.code - 流程分类编码
 * @param {?string} params.description - 流程分类描述
 * @param {!number} [params.page = 0] - 数据页码
 * @param {!number} [params.size = 10] - 分页大小
 * @returns {object} fetch Promise
 */
export async function fetchEmailList(params) {
  const { documentId } = params;
  return request(`${apiPrefix()}/mail-templates?documentId=${documentId}`, {
    method: 'GET',
  });
}

/**
 * 添加流程分类信息
 * @async
 * @function createDocuments
 * @param {object} params,organizationId - 请求参数
 * @param {object} organizationId - 租户id
 * @param {?string} params.code - 流程分类编码
 * @param {?string} params.description - 流程分类描述
 * @returns {object} fetch Promise
 */
export async function createDocuments(params) {
  return request(`${apiPrefix()}/process/documents`, {
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
  const { documentId, processDocument } = params;
  return request(`${apiPrefix()}/process/documents/${documentId}`, {
    method: 'PUT',
    body: processDocument,
  });
}

/**
 * 删除流程分类信息
 * @async
 * @function deleteDocuments
 * @param {number} processCategoryId - processVariableId
 * @param {number} organizationId - 租户id
 * @param {?string} params.code - 流程分类编码
 * @param {?string} params.description - 流程分类描述
 * @param {!string} params.objectVersionNumber - 版本号
 * @returns {object} fetch Promise
 */
export async function deleteDocuments(params) {
  const { documentId } = params;
  return request(`${apiPrefix()}/process/documents/${documentId}`, {
    method: 'DELETE',
    body: params,
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
  return request(`${apiPrefix()}/process/variables`, {
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
  return request(`${apiPrefix()}/process/variables/${variableId}`, {
    method: 'PUT',
    body: processVariable,
  });
}
/**
 * 查询流程分类
 * @async
 * @function handleSearchCategories
 * @param {object} params,organizationId - 查询条件
 * @param {!number} organizationId - 租户id
 * @param {?string} params.code - 流程分类编码
 * @param {?string} params.description - 流程分类描述
 * @param {!number} [params.page = 0] - 数据页码
 * @param {!number} [params.size = 10] - 分页大小
 * @returns {object} fetch Promise
 */
export async function handleSearchCategories(params) {
  const query = { ...parseParameters(params), enabledFlag: 1 };
  return request(`${apiPrefix()}/process/categories`, {
    method: 'GET',
    query,
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
  return request(`${apiPrefix()}/process/variables/${variableId}`, {
    method: 'DELETE',
    body: processVariable,
  });
}
/**
 * 删除流程表单
 * @async
 * @function deleteForm
 * @param {number} formId - 表单id
 * @param {number} organizationId - 租户id
 * @returns {object} fetch Promise
 */
export async function deleteForm(params) {
  const { formId } = params;
  return request(`${apiPrefix()}/forms/${formId}`, {
    method: 'DELETE',
  });
}

/**
 * 删除邮件表单
 * @async
 * @function deleteForm
 * @param {number} formId - 表单id
 * @param {number} organizationId - 租户id
 * @returns {object} fetch Promise
 */
export async function deleteEmail(params) {
  // const { formId } = params;
  const { processVariable } = params;
  return request(`${apiPrefix()}/mail-templates`, {
    method: 'DELETE',
    body: processVariable,
  });
}

/**
 * 保存详情流程表单
 * @async
 * @function handleSaveForm
 * @param {number} processCategoryId - processVariableId
 * @param {number} organizationId - 租户id
 * @param {?string} params.code - 流程分类编码
 * @param {?string} params.description - 流程分类描述
 * @param {!string} params.objectVersionNumber - 版本号
 * @returns {object} fetch Promise
 */
export async function handleSaveForm(params) {
  return request(`${apiPrefix()}/forms`, {
    method: 'POST',
    body: params,
  });
}
/**
 * 更新详情流程表单
 * @async
 * @function handleUpdateForm
 * @param {number} processCategoryId - processVariableId
 * @param {number} organizationId - 租户id
 * @param {?string} params.code - 流程分类编码
 * @param {?string} params.description - 流程分类描述
 * @param {!string} params.objectVersionNumber - 版本号
 * @returns {object} fetch Promise
 */
export async function handleUpdateForm(params) {
  const { formId, processVariable } = params;
  return request(`${apiPrefix()}/forms/${formId}`, {
    method: 'PUT',
    body: processVariable,
  });
}

/**
 * 保存详情邮件表单
 * @async
 * @function handleSaveEmail
 * @param {number} processCategoryId - processVariableId
 * @param {number} organizationId - 租户id
 * @param {?string} params.code - 流程分类编码
 * @param {?string} params.description - 流程分类描述
 * @param {!string} params.objectVersionNumber - 版本号
 * @returns {object} fetch Promise
 */
export async function handleSaveEmail(params) {
  return request(`${apiPrefix()}/mail-templates`, {
    method: 'POST',
    body: params,
  });
}
/**
 * 更新详情邮件表单
 * @async
 * @function handleUpdateEmail
 * @param {number} processCategoryId - processVariableId
 * @param {number} organizationId - 租户id
 * @param {?string} params.code - 流程分类编码
 * @param {?string} params.description - 流程分类描述
 * @param {!string} params.objectVersionNumber - 版本号
 * @returns {object} fetch Promise
 */
export async function handleUpdateEmail(params) {
  const { processVariable } = params;
  return request(`${apiPrefix()}/mail-templates`, {
    method: 'POST',
    body: processVariable,
  });
}
