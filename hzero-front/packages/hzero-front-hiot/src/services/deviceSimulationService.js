/**
 * 设备模拟上报- service
 * @date: 2020-7-7
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
 * 获取表单
 */
export async function fetchForm(params) {
  return request(`${apiPrefix}/thing-monitor/thing-property`, {
    method: 'GET',
    query: params,
  });
}

// 消息上报
export async function fetchMessageReport(params = {}) {
  const { thingId, thingType, qos, ...other } = params;
  return request(`${HZERO_HIOT}/v1/${organizationId}/cmd/reported`, {
    method: 'POST',
    body: other,
    query: { qos, thingId, thingType },
  });
}
