/**
 * nlpBasicDataService
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-05-28
 * @copyright 2019-05-28 © HAND
 */

import { HZERO_NLP } from 'utils/config';
import request from 'utils/request';
import { getCurrentOrganizationId, parseParameters } from 'utils/utils';

/**
 * @typedef {Object} NLPBasicData
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
 * 批量删除
 * @param {NLPBasicData[]} body
 * @returns {Promise<void>}
 */
export async function basicDataRemoveBatch(body = {}) {
  const organizationId = getCurrentOrganizationId();
  return request(`${HZERO_NLP}/v1/${organizationId}/basic-datas`, {
    method: 'DELETE',
    body,
  });
}

/**
 * 单条更新
 * @param {number} nlpBasicDataId - 详情id
 * @param {NLPBasicData} body - 单条更新
 * @returns {Promise<void>}
 */
export async function basicDataUpdate(nlpBasicDataId, body = {}) {
  const organizationId = getCurrentOrganizationId();
  return request(`${HZERO_NLP}/v1/${organizationId}/basic-datas`, {
    method: 'PUT',
    body,
  });
}

/**
 * query NLPBasicData List
 * @param {NLPBasicData | PageInfo} query
 * @returns {Promise<Page<NLPBasicData>>}
 */
export async function basicDataQuery(query) {
  const organizationId = getCurrentOrganizationId();
  const newQuery = parseParameters(query);
  return request(`${HZERO_NLP}/v1/${organizationId}/basic-datas`, {
    method: 'GET',
    query: newQuery,
  });
}

/**
 * query NLPBasicData Detail
 * @param {NLPBasicData | PageInfo} query
 * @param {number} nlpBasicDataId - id
 * @returns {Promise<Page<NLPBasicData>>}
 */
export async function basicDataQueryDetail(nlpBasicDataId, query) {
  const organizationId = getCurrentOrganizationId();
  return request(`${HZERO_NLP}/v1/${organizationId}/basic-datas/${nlpBasicDataId}`, {
    method: 'GET',
    query,
  });
}
