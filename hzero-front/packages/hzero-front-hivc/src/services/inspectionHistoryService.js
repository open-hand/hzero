/**
 * InspectionHistory 发票查验历史
 * @date: 2019-8-26
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */

import request from 'utils/request';
import { HZERO_INVOICE } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';

const organizationId = getCurrentOrganizationId();

/**
 * 查询列表数据
 * @async
 * @function queryLogList
 * @param {object} params - 查询条件
 */
export async function queryList(params) {
  return request(
    `${HZERO_INVOICE}/v1${isTenantRoleLevel() ? `/${organizationId}` : ''}/check-hists`,
    {
      method: 'GET',
      query: params,
    }
  );
}
