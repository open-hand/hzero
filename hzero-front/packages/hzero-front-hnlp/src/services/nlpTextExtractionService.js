/**
 * nlpTextExtractionService
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-06-04
 * @copyright 2019-06-04 © HAND
 */

import { HZERO_NLP } from 'utils/config';
import request from 'utils/request';

import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';

const tenantId = getCurrentOrganizationId();
function nlpTextExtraction() {
  return isTenantRoleLevel() ? `${tenantId}/text-extract/do` : 'text-extract/do';
}
/**
 * @typedef {Object} NLPTextExtraction
 * @property {number} resultId - 主键
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

/**
 * create new HZERO_NLP
 * @param {NLPTextExtraction} body - new HZERO_NLP
 * @returns {Promise<void>}
 */
export async function nlpTextExtractionCreate(body = {}) {
  return request(`${HZERO_NLP}/v1/${nlpTextExtraction()}`, {
    method: 'POST',
    body,
  });
}
