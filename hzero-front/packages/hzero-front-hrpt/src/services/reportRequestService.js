/**
 * service - 报表平台/报表请求
 * @date: 2019-1-28
 * @version: 1.0.0
 * @author: LZY <zhuyan.luo@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */

import request from 'utils/request';
import { HZERO_RPT } from 'utils/config';
import { parseParameters, getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';

const organizationId = getCurrentOrganizationId();
const organizationRoleLevel = isTenantRoleLevel();
/**
 * 请求API前缀
 * @type {string}
 */
const prefix = `${HZERO_RPT}/v1`;

/**
 * 报表列表查询
 * @async
 * @function fetchRequestList
 * @param {object} params - 查询条件
 * @param {!string} params.tenantId - 租户ID
 * @param {!number} [params.page = 0] - 数据页码
 * @param {!number} [params.size = 10] - 分页大小
 * @returns {object} fetch Promise
 */
export async function fetchRequestList(params) {
  const param = parseParameters(params);
  return request(
    organizationRoleLevel
      ? `${prefix}/${organizationId}/report-requests`
      : `${prefix}/report-requests`,
    {
      method: 'GET',
      query: param,
    }
  );
}
/**
 * 查询详情
 * @async
 * @function fetchRequestDetail
 * @param {object} params - 查询条件
 * @returns {object} fetch Promise
 */
export async function fetchRequestDetail(params) {
  return request(
    organizationRoleLevel
      ? `${prefix}/${organizationId}/report-requests/${params.requestId}`
      : `${prefix}/report-requests/${params.requestId}`,
    {
      method: 'GET',
    }
  );
}
