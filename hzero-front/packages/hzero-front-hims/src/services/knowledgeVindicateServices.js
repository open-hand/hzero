/**
 * service - 知识维护
 * @date: 2019-12-31
 * @author: LiLin <lin.li03@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */

import request from 'utils/request';
import { HZERO_IM } from 'utils/config';
import { isTenantRoleLevel, getCurrentOrganizationId } from 'utils/utils';

const organizationId = getCurrentOrganizationId();
const apiPrefix = isTenantRoleLevel() ? `${HZERO_IM}/v1/${organizationId}` : `${HZERO_IM}/v1`;

/**
 * 审核知识问题
 * @param {object} params
 * @returns
 */
export async function checkKnowledgeQuestion(body) {
  return request(`${apiPrefix}/knowledge/check`, {
    method: 'PUT',
    body,
  });
}
