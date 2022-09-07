/**
 * nlpTenantWordService
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-05-29
 * @copyright 2019-05-29 © HAND
 */

import { HZERO_NLP } from 'utils/config';
import request from 'utils/request';
import { getCurrentOrganizationId, parseParameters, isTenantRoleLevel } from 'utils/utils';

const tenantId = getCurrentOrganizationId();
function wordTemplate() {
  return isTenantRoleLevel() ? `${tenantId}/template-words` : 'template-words';
}
/**
 * @typedef {Object} NLPTenantWord
 * @property {number} wordTemplateId - 主键
 */

/**
 * @typedef {Object} Page<T>
 * @property {number} number - 分页
 * @property {number} size - 分页大小
 * @property {number} totalElements - 总数据量
 * @property {T[]} content - 数据
 */

/**
 * @typedef {Object} PageInfo
 * @property {number} page - 分页
 * @property {number} size - 分页大小
 */

// organization

/**
 * create new HZERO_NLP
 * @param {NLPTenantWord} body - new HZERO_NLP
 * @returns {Promise<void>}
 */
export async function wordTemplateCreate(body = {}) {
  return request(`${HZERO_NLP}/v1/${wordTemplate()}`, {
    method: 'POST',
    body,
  });
}

/**
 * 批量删除
 * @param {NLPTenantWord[]} body
 * @returns {Promise<void>}
 */
export async function wordTemplateRemoveBatch(body = {}) {
  return request(`${HZERO_NLP}/v1/${wordTemplate()}`, {
    method: 'DELETE',
    body,
  });
}

/**
 * 单条更新
 * @param {NLPTenantWord} body - 单条更新
 * @returns {Promise<void>}
 */
export async function wordTemplateUpdate(body = {}) {
  return request(`${HZERO_NLP}/v1/${wordTemplate()}`, {
    method: 'PUT',
    body,
  });
}

/**
 * query NLPTenantWord List
 * @param {NLPTenantWord | PageInfo} query
 * @returns {Promise<Page<NLPTenantWord>>}
 */
export async function wordTemplateQuery(query) {
  const newQuery = parseParameters(query);
  return request(`${HZERO_NLP}/v1/${wordTemplate()}`, {
    method: 'GET',
    query: newQuery,
  });
}
