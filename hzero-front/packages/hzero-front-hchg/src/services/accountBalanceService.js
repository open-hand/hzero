/**
 * service - 账户余额
 * @author HBT <baitao.huang@hand-china.com>
 * @creationDate 2020/2/20
 * @copyright HAND ® 2020
 */

import request from 'utils/request';
import { HZERO_CHG } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';

const organizationId = getCurrentOrganizationId();
const organizationRoleLevel = isTenantRoleLevel();

/**
 * 充值
 */
export async function accountRecharge(params = {}) {
  return request(
    organizationRoleLevel
      ? `${HZERO_CHG}/v1/${organizationId}/account-recharges`
      : `${HZERO_CHG}/v1/account-recharges`,
    {
      query: params,
      method: 'POST',
      responseType: 'text',
    }
  );
}
