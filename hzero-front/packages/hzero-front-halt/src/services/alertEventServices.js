/**
 * 告警事件管理- service
 * @date: 2020-5-20
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2020, Hand
 */

import request from 'utils/request';
import { isTenantRoleLevel, getCurrentOrganizationId } from 'utils/utils';
import { HZERO_ALT } from 'utils/config';

const organizationId = getCurrentOrganizationId();
const isTenant = isTenantRoleLevel();
const apiPrefix = isTenant ? `${HZERO_ALT}/v1/${organizationId}` : `${HZERO_ALT}/v1`;

/**
 * 获取预警事件日志信息
 * @param {number} alertEventId - 告警事件ID
 * @returns
 */
export async function fetchLog(alertEventId) {
  return request(`${apiPrefix}/alert-event-logs/${alertEventId}/list`, {
    method: 'GET',
  });
}
