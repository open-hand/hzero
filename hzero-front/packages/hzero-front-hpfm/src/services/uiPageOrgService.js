/**
 * uiPageService.js
 * @date 2018/9/29
 * @author WY yang.wang06@hand-china.com
 * @copyright Copyright (c) 2018, Hand
 */

import request from 'utils/request';
import { HZERO_PLATFORM } from 'utils/config';
import { parseParameter } from 'utils/utils';

export async function uiPageOrgQueryPaging(organizationId, params) {
  const query = parseParameter(params);
  return request(`${HZERO_PLATFORM}/v1/${organizationId}/ui-pages`, {
    query,
    method: 'GET',
  });
}

export async function uiPageOrgCreate(organizationId, uiPage) {
  return request(`${HZERO_PLATFORM}/v1/${organizationId}/ui-pages`, {
    body: uiPage,
    method: 'POST',
  });
}

export async function uiPageOrgUpdate(organizationId, uiPage) {
  return request(`${HZERO_PLATFORM}/v1/${organizationId}/ui-pages`, {
    body: uiPage,
    method: 'PUT',
  });
}

export async function uiPageOrgQueryDetail(organizationId, pageCode) {
  return request(`${HZERO_PLATFORM}/v1/${organizationId}/ui-pages/${pageCode}`, {
    method: 'GET',
  });
}

/**
 * save page config
 * @param config
 * @returns {Promise<void>}
 */
export async function uiPageOrgDetailUpdate(organizationId, config) {
  return request(`${HZERO_PLATFORM}/v1/${organizationId}/ui-pages/detail`, {
    method: 'PUT',
    body: config,
  });
}
