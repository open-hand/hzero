/**
 * 指令下发日志监控- service
 * @date: 2020-6-15
 * @author: LiLin <lin.li03@hand-china.com>
 * @copyright Copyright (c) 2020, Hand
 */

import request from 'utils/request';
import { isTenantRoleLevel, getCurrentOrganizationId } from 'utils/utils';
import { HZERO_HIOT } from 'utils/config';

const organizationId = getCurrentOrganizationId();
const isTenant = isTenantRoleLevel();
const apiPrefix = isTenant ? `${HZERO_HIOT}/v1/${organizationId}` : `${HZERO_HIOT}/v1`;

/**
 * 获取指令下发日志信息
 * @param {number} alertEventId - 指令下发ID
 * @returns
 */
export async function fetchLog(downLogId) {
  return request(`${apiPrefix}/log-msg/down/${downLogId}`, {
    method: 'GET',
  });
}
