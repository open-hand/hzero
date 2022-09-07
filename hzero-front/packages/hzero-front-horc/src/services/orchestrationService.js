import request from 'utils/request';
import { HZERO_HORC } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel, parseParameters } from 'utils/utils';

const organizationId = getCurrentOrganizationId();
const organizationRoleLevel = isTenantRoleLevel();

export async function queryList(params = {}) {
  return request(
    organizationRoleLevel
      ? `${HZERO_HORC}/v1/${organizationId}/orch-definitions`
      : `${HZERO_HORC}/v1/orch-definitions`,
    {
      query: parseParameters(params),
    }
  );
}

export async function duplicate(definitionId) {
  return request(
    organizationRoleLevel
      ? `${HZERO_HORC}/v1/${organizationId}/orch-definitions/${definitionId}/duplication`
      : `${HZERO_HORC}/v1/orch-definitions/${definitionId}/duplication`,
    {
      method: 'POST',
    }
  );
}
