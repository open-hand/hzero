/**
 * service - 数据变更审计
 * @date: 2019/7/10
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
 * 查询数据审计列表
 * @param {*} params - 参数
 */
export async function queryAuditList(params = {}) {
  return request(
    organizationRoleLevel
      ? `${HZERO_MNT}/v1/${organizationId}/audit-datas/list`
      : `${HZERO_MNT}/v1/audit-datas/list`,
    {
      query: parseParameters(params),
    }
  );
}
