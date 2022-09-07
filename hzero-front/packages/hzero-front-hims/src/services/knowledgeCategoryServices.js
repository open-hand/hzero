/**
 * service - 知识类别
 * @date: 2019-12-5
 * @author: LiLin <lin.li03@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */

import request from 'utils/request';
import { HZERO_IM } from 'utils/config';
import { isTenantRoleLevel, getCurrentOrganizationId } from 'utils/utils';

const organizationId = getCurrentOrganizationId();
const apiPrefix = isTenantRoleLevel() ? `${HZERO_IM}/v1/${organizationId}` : `${HZERO_IM}/v1`;

/**
 * 启用知识类别
 * @param {object} params
 */
export async function enableKnowledgeCategories(params) {
  return request(`${apiPrefix}/knowledge-categories/enable`, {
    method: 'PUT',
    body: params,
  });
}

/**
 * 禁用知识类别
 * @param {object} params
 */
export async function disableKnowledgeCategories(params) {
  return request(`${apiPrefix}/knowledge-categories/disable`, {
    method: 'PUT',
    body: params,
  });
}
