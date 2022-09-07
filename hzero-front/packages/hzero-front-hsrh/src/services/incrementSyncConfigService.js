/**
 * 增量同步
 * @date: 2019-11-10
 * @author: WT <tao13.wang@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */

import request from 'utils/request';
import { HZERO_HSRH } from 'utils/config';
import { getCurrentOrganizationId } from 'utils/utils';

const organizationId = getCurrentOrganizationId();

export async function fetchSync(params) {
  const { syncConfCode } = params;
  return request(`${HZERO_HSRH}/v1/${organizationId}/index-sync-configs/sync/${syncConfCode}`, {
    method: 'GET',
  });
}
