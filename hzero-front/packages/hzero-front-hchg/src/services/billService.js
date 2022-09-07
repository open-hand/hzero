/**
 * service - 用户账单
 * @date: 2020/2/14
 * @author: fengwanjun <wanjun.feng@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import request from 'hzero-front/lib/utils/request';
import { HZERO_CHG } from 'hzero-front/lib/utils/config';
import {
  getCurrentOrganizationId,
  isTenantRoleLevel,
  parseParameters,
} from 'hzero-front/lib/utils/utils';

const organizationId = getCurrentOrganizationId();
const organizationRoleLevel = isTenantRoleLevel();

/**
 * 查询用户账单明细
 * @param {*} params - 参数
 */
export function queryList(params) {
  return request(
    organizationRoleLevel
      ? `${HZERO_CHG}/v1/${organizationId}/bill-headers`
      : `${HZERO_CHG}/v1/bill-headers`,
    {
      query: parseParameters(params),
    }
  );
}

export function queryDetail(params) {
  return request(
    organizationRoleLevel
      ? `${HZERO_CHG}/v1/${organizationId}/bill/bill-lines`
      : `${HZERO_CHG}/v1/bill/bill-lines`,
    {
      query: parseParameters(params),
    }
  );
}
