/**
 * tenantManagerService.js - 租户管理员 service
 * @date: 2019-01-10
 * @author: zhengmin.liang <zhengmin.liang@hand-china.com>
 * @version: 0.0.1
 * @copyright: Copyright (c) 2018, Hand
 */

import request from 'utils/request';
import { HZERO_PLATFORM } from 'utils/config';
import { parseParameters } from 'utils/utils';

const prefix = `${HZERO_PLATFORM}/v1`;

/**
 * 登录记录查询
 * @export
 * @param {*} params
 * @returns
 */
export async function fetchRecords({ tenantId, ...params }) {
  const param = parseParameters(params);
  return request(`${prefix}/audit-logins/self`, {
    method: 'GET',
    query: param,
  });
}
