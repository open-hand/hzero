/**
 * 搜索数据
 * @date: 2020-1-6
 * @author MLF <linfeng.miao@hand-china.com>
 * @copyright Copyright (c) 2020, Hand
 */

import request from 'utils/request';
import { HZERO_HSRH } from 'utils/config';
import { getCurrentOrganizationId } from 'utils/utils';

const organizationId = getCurrentOrganizationId();

/**
 * 发送
 * @async
 * @function fetchSearchDataSend
 * @param {object} params - 搜索数据发送
 * @returns {object} fetch Promise
 */
export async function fetchSearchDataSend(params) {
  return request(`${HZERO_HSRH}/v1/${organizationId}/request`, {
    method: 'POST',
    body: params,
  });
}
