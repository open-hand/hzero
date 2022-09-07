/**
 * service - 购买详单
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
 * 查询购买详单列表
 * @param {*} params - 参数
 */
export async function queryList(params = {}) {
  return request(
    organizationRoleLevel
      ? `${HZERO_CHG}/v1/${organizationId}/charge-orders`
      : `${HZERO_CHG}/v1/charge-orders`,
    {
      query: parseParameters(params),
    }
  );
}

/**
 * 查看详情
 * @param {number} chargeOrderId - 订单id
 */
export async function queryDetail(chargeOrderId) {
  return request(
    organizationRoleLevel
      ? `${HZERO_CHG}/v1/${organizationId}/charge-orders/${chargeOrderId}`
      : `${HZERO_CHG}/v1/charge-orders/${chargeOrderId}`
  );
}
