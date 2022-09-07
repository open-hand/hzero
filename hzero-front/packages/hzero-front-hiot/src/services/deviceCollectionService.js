import request from 'utils/request';
import { HZERO_HIOT } from 'utils/config';
import { getCurrentOrganizationId, getCurrentUser } from 'utils/utils';

const organizationId = getCurrentOrganizationId();
const userId = getCurrentUser().id;

export async function fixStatus(params = {}) {
  const { tagIds, ...other } = params;
  return request(
    `${HZERO_HIOT}/v1/${organizationId}/egk-dc-device-tag/batch-update/attr/is-enable`,
    {
      method: 'POST',
      body: tagIds,
      query: other,
    }
  );
}

export async function exportData(params = {}) {
  return request(`${HZERO_HIOT}/v1/${organizationId}/egk-dc-device/export`, {
    method: 'POST',
    body: { ...params, userId },
  });
}
