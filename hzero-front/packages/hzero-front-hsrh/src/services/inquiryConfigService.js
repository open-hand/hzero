/**
 * 配置查询
 * @date: 2020-1-6
 * @author MLF <linfeng.miao@hand-china.com>
 * @copyright Copyright (c) 2020, Hand
 */

import request from 'utils/request';
import { HZERO_HSRH } from 'utils/config';
import { getCurrentOrganizationId } from 'utils/utils';

const organizationId = getCurrentOrganizationId();

/**
 * 字段查询
 * @async
 * @function fetchList
 * @param {object} params - 查询条件
 * @returns {object} fetch Promise
 */
export async function fetchList(params) {
  return request(`${HZERO_HSRH}/v1/${organizationId}/index-fields/without-pages`, {
    method: 'GET',
    query: params,
  });
}

/**
 * SQL查询ES
 * @async
 * @function fetchList
 * @param {object} params - 查询条件
 * @returns {object} fetch Promise
 */
export async function sqlRequest(params) {
  return request(`${HZERO_HSRH}/v1/${organizationId}/request/sql`, {
    method: 'POST',
    body: params,
  });
}

/**
 * SQL查询ES
 * @async
 * @function fetchList
 * @param {object} params - 查询条件
 * @returns {object} fetch Promise
 */
export async function httpRequest(params) {
  return request(`${HZERO_HSRH}/v1/${organizationId}/request`, {
    method: 'POST',
    body: params,
  });
}

export async function configRequest(params) {
  return request(`${HZERO_HSRH}/v1/${organizationId}/query-configs/${params.configCode}/query`, {
    method: 'POST',
    body: params,
  });
}
