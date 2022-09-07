/**
 * 导览工作台- service
 * @date: 2020-7-10
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
 * 获取设备地图数据
 */
export async function fetchDeviceMap(params) {
  return request(`${apiPrefix}/guide/thing-map`, {
    method: 'GET',
    query: params,
  });
}
