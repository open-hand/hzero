/**
 * service - 操作审计查询
 * @date: 2019/7/18
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */

import request from 'utils/request';
import { HZERO_MNT } from 'utils/config';
import { parseParameters, isTenantRoleLevel, getCurrentOrganizationId } from 'utils/utils';

const organizationId = getCurrentOrganizationId();

/**
 * 查询操作审计列表
 * @param {*} params - 参数
 */
export async function queryAuditList(params) {
  const param = parseParameters(params);
  return request(
    `${HZERO_MNT}/v1${isTenantRoleLevel() ? `/${organizationId}` : ``}/audit/operational/logs`,
    {
      method: 'GET',
      query: { ...param },
    }
  );
}

/**
 * 查询操作审计记录行
 * @param {*} params - 参数
 */
export async function queryAuditDetail(params) {
  const { logLineId } = params[0];
  return request(
    `${HZERO_MNT}/v1${
      isTenantRoleLevel() ? `/${organizationId}` : ``
    }/audit/operational/logs/line/${logLineId}`,
    {
      method: 'GET',
    }
  );
}
