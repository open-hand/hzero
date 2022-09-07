import request from 'utils/request';
import {
  parseParameters,
  filterNullValueObject,
  getCurrentOrganizationId,
  isTenantRoleLevel,
} from 'utils/utils';
import { HZERO_HITF } from 'utils/config';

const organizationId = getCurrentOrganizationId();
const organizationRoleLevel = isTenantRoleLevel();

/**
 * 健康异常数据查询
 * @async
 * @function queryList
 * @param {!number} organizationId - 组织ID
 * @param {object} params - 查询条件
 * @param {!number} [params.page = 0] - 数据页码
 * @param {!number} [params.size = 10] - 分页大小
 * @returns {object} fetch Promise
 */
export async function queryList(params = {}) {
  const query = filterNullValueObject(parseParameters(params));
  return request(
    organizationRoleLevel
      ? `${HZERO_HITF}/v1/${organizationId}/statistics/health-check`
      : `${HZERO_HITF}/v1/statistics/health-check`,
    {
      query,
    }
  );
}
