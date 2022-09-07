/**
 * service - 服务账单
 * @date: 2019/8/30
 * @author: hulingfangzi <lingfangzi.hu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import request from 'utils/request';
import { HZERO_CHG } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel, parseParameters } from 'utils/utils';

const organizationId = getCurrentOrganizationId();
const organizationRoleLevel = isTenantRoleLevel();

/**
 * 查询数据审计列表
 * @param {*} params - 参数
 */
export async function queryList(params = {}) {
  return request(
    organizationRoleLevel
      ? `${HZERO_CHG}/v1/${organizationId}/charge-bills`
      : `${HZERO_CHG}/v1/charge-bills`,
    {
      query: parseParameters(params),
    }
  );
}
