/**
 * nlpConfigTemplateService
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-05-27
 * @copyright 2019-05-27 © HAND
 */

import { HZERO_NLP } from 'utils/config';
import request from 'utils/request';
import { getCurrentOrganizationId, parseParameters, isTenantRoleLevel } from 'utils/utils';

/**
 * @typedef {Object} configTemplate
 * @property {number} dataId - 主键
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
 * @param {configTemplate} body - new HZERO_NLP
 * @returns {Promise<void>}
 */
export async function configTemplateCreate(body = {}) {
  const organizationId = getCurrentOrganizationId();
  return request(
    `${HZERO_NLP}/v1${isTenantRoleLevel() ? `/${organizationId}` : ``}/template-datas`,
    {
      method: 'POST',
      body,
    }
  );
}

/**
 * 单条删除
 * @param {NLPTemplate} body
 * @returns {Promise<void>}
 */
export async function configTemplateRemove(body = {}) {
  const organizationId = getCurrentOrganizationId();
  return request(
    `${HZERO_NLP}/v1${isTenantRoleLevel() ? `/${organizationId}` : ``}/template-datas`,
    {
      method: 'DELETE',
      body,
    }
  );
}

/**
 * 单条更新
 * @param {NLPTemplate} body - 单条更新
 * @returns {Promise<void>}
 */
export async function configTemplateUpdate(body = {}) {
  const organizationId = getCurrentOrganizationId();
  return request(
    `${HZERO_NLP}/v1${isTenantRoleLevel() ? `/${organizationId}` : ``}/template-datas`,
    {
      method: 'PUT',
      body,
    }
  );
}

/**
 * query NLPTemplate List
 * @param {NLPTemplate | PageInfo} query
 * @returns {Promise<Page<NLPTemplate>>}
 */
export async function configTemplateQuery(query) {
  const organizationId = getCurrentOrganizationId();
  const newQuery = parseParameters(query);
  return request(
    `${HZERO_NLP}/v1${isTenantRoleLevel() ? `/${organizationId}` : ``}/template-datas`,
    {
      method: 'GET',
      query: newQuery,
    }
  );
}

/**
 * query NLPTemplate Detail
 * @param {NLPTemplate | PageInfo} query
 * @param {number} nlpConfigTemplateId - NLPTemplateId
 * @returns {Promise<Page<NLPTemplate>>}
 */
export async function configTemplateQueryDetail(nlpConfigTemplateId, query) {
  const organizationId = getCurrentOrganizationId();
  return request(
    `${HZERO_NLP}/v1${
      isTenantRoleLevel() ? `/${organizationId}` : ``
    }/template-datas/${nlpConfigTemplateId}`,
    {
      method: 'GET',
      query,
    }
  );
}
