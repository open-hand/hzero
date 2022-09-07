/**
 * 数据审计及操作审计
 * @date: 2019/5/5
 * @author: hulingfangzi <lingfangzi.hu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import request from 'utils/request';
import { HZERO_MNT } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel, parseParameters } from 'utils/utils';

const organizationId = getCurrentOrganizationId();
const organizationRoleLevel = isTenantRoleLevel();

/**
 * 通用查询数据审计记录
 * @param {string} params.entityCode 审计代码
 * @param {number} params.entityId 审计实体ID
 */
export async function queryAuditVersion(params) {
  const param = parseParameters(params);
  return request(
    organizationRoleLevel
      ? `${HZERO_MNT}/v1/${organizationId}/audit-datas`
      : `${HZERO_MNT}/v1/audit-datas`,
    {
      method: 'GET',
      query: { ...param },
    }
  );
}
