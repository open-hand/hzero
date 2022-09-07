/**
 * nlpTemplateService
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-05-27
 * @copyright 2019-05-27 © HAND
 */
import { HZERO_NLP } from 'utils/config';
import request from 'utils/request';
import { getCurrentOrganizationId, parseParameters, isTenantRoleLevel } from 'utils/utils';

const tenantId = getCurrentOrganizationId();
function template() {
  return isTenantRoleLevel() ? `${tenantId}/nlp-templates` : 'nlp-templates';
}
/**
 * @typedef {Object} NLPTemplate
 * @property {number} templateId - 主键
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
 * create new template
 * @param {NLPTemplate} body - new template
 * @returns {Promise<void>}
 */
export async function templateCreate(body = {}) {
  return request(`${HZERO_NLP}/v1/${template()}`, {
    method: 'POST',
    body,
  });
}

/**
 * 单条删除
 * @param {number} nlpTemplateId
 * @param {NLPTemplate} body
 * @returns {Promise<void>}
 */
export async function templateRemove(nlpTemplateId, body = {}) {
  return request(`${HZERO_NLP}/v1/${template()}/${nlpTemplateId}`, {
    method: 'DELETE',
    body,
  });
}

/**
 * 单条更新
 * @param {number} nlpTemplateId - 详情id
 * @param {NLPTemplate} body - 单条更新
 * @returns {Promise<void>}
 */
export async function templateUpdate(nlpTemplateId, body = {}) {
  return request(`${HZERO_NLP}/v1/${template()}`, {
    method: 'PUT',
    body,
  });
}

/**
 * query NLPTemplate List
 * @param {NLPTemplate | PageInfo} query
 * @returns {Promise<Page<NLPTemplate>>}
 */
export async function templateQuery(query) {
  const newQuery = parseParameters(query);
  return request(`${HZERO_NLP}/v1/${template()}`, {
    method: 'GET',
    query: newQuery,
  });
}

/**
 * query NLPTemplate Detail
 * @param {NLPTemplate | PageInfo} query
 * @param {number} nlpTemplateId - NLPTemplateId
 * @returns {Promise<Page<NLPTemplate>>}
 */
export async function templateQueryDetail(nlpTemplateId, query) {
  return request(`${HZERO_NLP}/v1/${template()}/${nlpTemplateId}`, {
    method: 'GET',
    query,
  });
}
