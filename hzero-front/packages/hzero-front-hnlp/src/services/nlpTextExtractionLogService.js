/**
 * nlpTextExtractionLogService
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-05-29
 * @copyright 2019-05-29 © HAND
 */

import { HZERO_NLP } from 'utils/config';
import request from 'utils/request';
import { getCurrentOrganizationId, parseParameters, isTenantRoleLevel } from 'utils/utils';

/**
 * @typedef {Object} NLPTextExtractionLog
 * @property {number} id - 主键
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
 * query NLPTextExtractionLog List
 * @param {NLPTextExtractionLog | PageInfo} query
 * @returns {Promise<Page<NLPTextExtractionLog>>}
 */
export async function textExtractionLogQuery(query) {
  const organizationId = getCurrentOrganizationId();
  const newQuery = parseParameters(query);
  return request(
    `${HZERO_NLP}/v1${isTenantRoleLevel() ? `/${organizationId}` : ``}/text-extract-logs`,
    {
      method: 'GET',
      query: newQuery,
    }
  );
}
