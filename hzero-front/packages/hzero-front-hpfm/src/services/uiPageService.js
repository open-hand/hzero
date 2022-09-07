/**
 * uiPageService.js
 * @date 2018/9/29
 * @author WY yang.wang06@hand-china.com
 * @copyright Copyright (c) 2018, Hand
 */

import request from 'utils/request';
import { HZERO_PLATFORM } from 'utils/config';
import { parseParameter } from 'utils/utils';

export async function uiPageSiteQueryPaging(params) {
  const query = parseParameter(params);
  return request(`${HZERO_PLATFORM}/v1/ui-pages`, {
    query,
    method: 'GET',
  });
}

export async function uiPageSiteCreate(uiPage) {
  return request(`${HZERO_PLATFORM}/v1/ui-pages`, {
    body: uiPage,
    method: 'POST',
  });
}

export async function uiPageSiteUpdate(uiPage) {
  return request(`${HZERO_PLATFORM}/v1/ui-pages`, {
    body: uiPage,
    method: 'PUT',
  });
}

export async function uiPageSiteQueryDetail(pageCode) {
  return request(`${HZERO_PLATFORM}/v1/ui-pages/${pageCode}`, {
    method: 'GET',
  });
}

/**
 * save page config
 * @param config
 * @returns {Promise<void>}
 */
export async function uiPageSiteDetailUpdate(config) {
  return request(`${HZERO_PLATFORM}/v1/ui-pages/detail`, {
    method: 'PUT',
    body: config,
  });
}

/**
 * 查询 个性化应用
 * @param {number} organizationId
 * @param {string} pageCode
 * @returns {Promise<void>}
 */
export async function queryConfigsByPageCode(organizationId, pageCode) {
  return request(`${HZERO_PLATFORM}/v1/${organizationId}/ui-pages/common/${pageCode}`, {
    method: 'GET',
  });
}

export async function queryTplAndScriptsByPageCode(organizationId, pageCode) {
  return request(`${HZERO_PLATFORM}/v1/${organizationId}/ui-pages/${pageCode}/tpl-and-scripts`, {
    method: 'GET',
  });
}
