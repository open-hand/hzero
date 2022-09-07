/**
 * service 弹性域模型
 * @date: 2019-4-25
 * @version: 0.0.1
 * @author: lijun <heng.wei@hand-china.com>
 * @copyright Copyright (c) 2018, Hands
 */

import request from 'utils/request';
import { HZERO_PLATFORM } from 'utils/config';
import { getCurrentOrganizationId } from 'utils/utils';

const organizationId = getCurrentOrganizationId();

/**
 * 个性化明细列表
 * @async
 * @function queryPersonalityDetails
 * @param {object} 个性化明细列表 - 查询条件
 * @returns {object} fetch Promise
 */
export async function queryPersonalityDetails(personalityCode) {
  return request(`${HZERO_PLATFORM}/v1/${organizationId}/personality-details/${personalityCode}`);
}

/**
 * 创建个性化明细
 * @async
 * @function savePersonalityDetails
 * @param {object} params - 保存数据
 * @returns {object} fetch Promise
 */
export async function savePersonalityDetails(personalityCode, scope, data = {}) {
  return request(
    `${HZERO_PLATFORM}/v1/${organizationId}/personality-details/${scope}/${personalityCode}`,
    {
      method: 'POST',
      body: data,
    }
  );
}
