/**
 * apiIndividuationService - API个性化
 * @date: 2020/7/17
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import request from 'utils/request';
import { HZERO_PLATFORM } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';

const organizationId = getCurrentOrganizationId();
const organizationRoleLevel = isTenantRoleLevel();

/**
 *
 * @param {string} customizeCode - 分页参数
 */
export async function validateDuplicateCustomizeCode(customizeCode) {
  return request(
    organizationRoleLevel
      ? `${HZERO_PLATFORM}/v1/${organizationId}/api-cuszs/validate/${customizeCode}`
      : `${HZERO_PLATFORM}/v1/api-cuszs/validate/${customizeCode}`,
    {}
  );
}

/**
 *
 * @param {string} customizeCode - 分页参数
 */
export async function validateDuplicateVersion({ customizeId, versionNumber }) {
  return request(
    organizationRoleLevel
      ? `${HZERO_PLATFORM}/v1/${organizationId}/api-cusz-contents/validate/${customizeId}/${versionNumber}`
      : `${HZERO_PLATFORM}/v1/api-cusz-contents/validate/${customizeId}/${versionNumber}`,
    {}
  );
}
