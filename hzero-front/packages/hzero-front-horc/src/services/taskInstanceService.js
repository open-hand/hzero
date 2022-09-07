import request from 'utils/request';
import { HZERO_HORC } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';

const organizationId = getCurrentOrganizationId();
const level = isTenantRoleLevel() ? `/${organizationId}` : '';

export async function queryLog(taskInstanceId) {
  return request(`${HZERO_HORC}/v1${level}/logs/${taskInstanceId}`, {
    method: 'GET',
    responseType: 'text',
  });
}
