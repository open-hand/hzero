/**
 * @email WY <yang.wang06@hand-china.com>
 * @creationDate 2019/12/25
 * @copyright HAND ® 2019
 */

import request from 'utils/request';
import { getCurrentOrganizationId, getResponse, isTenantRoleLevel, getUrlParam } from 'utils/utils';
import { HZERO_RPT } from 'utils/config';

/**
 * 查询报表数据
 * @param {string} code - 报表编码
 */
export async function queryReport(params) {
  const { code, chartParams = {} } = params;
  const { type, strParam, ...others } = chartParams;
  const otherParam = getUrlParam(others);
  const organizationId = getCurrentOrganizationId();
  let res = '';
  if (strParam) {
    const newParam = otherParam ? `${otherParam}&${strParam}` : `?${strParam}`;
    res = request(
      isTenantRoleLevel()
        ? `${HZERO_RPT}/v1/${organizationId}/reports/${code}/data${newParam}`
        : `${HZERO_RPT}/v1/reports/${code}/data${newParam}`,
      {
        method: 'POST',
      }
    );
  } else {
    res = request(
      isTenantRoleLevel()
        ? `${HZERO_RPT}/v1/${organizationId}/reports/${code}/data${otherParam}`
        : `${HZERO_RPT}/v1/reports/${code}/data${otherParam}`,
      {
        method: 'POST',
      }
    );
  }
  // FIXME: @WJC utils need fix
  // @ts-ignore
  return getResponse(res);
}

/**
 * 生成报表数据
 * @param {object} body
 */
export async function buildReport(body) {
  const organizationId = getCurrentOrganizationId();
  const { code, value, params, paramName, type, strParam, ...others } = body;
  const otherParam = getUrlParam(others);
  let newParams = '';
  if (otherParam) {
    newParams = strParam
      ? `${otherParam}&${paramName}=${value}&${strParam}`
      : `${otherParam}&${paramName}=${value}`;
  } else {
    newParams = strParam ? `?${paramName}=${value}&${strParam}` : `?${paramName}=${value}`;
  }
  const res = request(
    isTenantRoleLevel()
      ? `${HZERO_RPT}/v1/${organizationId}/reports/${code}/data?${newParams}`
      : `${HZERO_RPT}/v1/reports/${code}/data?${newParams}`,
    {
      method: 'POST',
      query: params,
    }
  );

  // FIXME: @WJC utils need fix
  // @ts-ignore
  return getResponse(res);
}
