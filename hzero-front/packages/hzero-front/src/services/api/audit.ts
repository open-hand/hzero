/**
 * @email WY <yang.wang06@hand-china.com>
 * @creationDate 2019/12/25
 * @copyright HAND ® 2019
 */

import request from 'utils/request';
import { getEnvConfig } from 'utils/iocUtils';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';

/**
 * 通用查询数据审计记录
 * @param {string} params.entityCode 审计代码
 * @param {number} params.entityId 审计实体ID
 */
export async function queryAuditVersion(params) {
  const { HZERO_MNT } = getEnvConfig();
  const organizationId = getCurrentOrganizationId();
  return request(
    isTenantRoleLevel()
      ? `${HZERO_MNT}/v1/${organizationId}/audit-datas`
      : `${HZERO_MNT}/v1/audit-datas`,
    {
      method: 'GET',
      query: { ...params },
    }
  );
}
