import request from 'utils/request';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import { HZERO_HORC } from 'utils/config';

const organizationId = getCurrentOrganizationId();
const organizationRoleLevel = isTenantRoleLevel();

/**
 *  头保存
 * @async
 * @function saveHeader
 * @param {object} data - 保存参数
 */
export async function saveHeader(data) {
  return request(
    organizationRoleLevel
      ? `${HZERO_HORC}/v1/${organizationId}/cast-headers`
      : `${HZERO_HORC}/v1/cast-headers`,
    {
      method: 'POST',
      body: data,
    }
  );
}
