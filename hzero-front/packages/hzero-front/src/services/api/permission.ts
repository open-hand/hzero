/**
 * @email WY <yang.wang06@hand-china.com>
 * @creationDate 2019/12/25
 * @copyright HAND ® 2019
 */

import request from 'utils/request';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import { getEnvConfig } from 'utils/iocUtils';

const { HZERO_IAM, HZERO_MNT, HZERO_PLATFORM } = getEnvConfig();
// import { getCurrentOrganizationId, isTenantRoleLevel } from '@/utils/utils';

// /**
//  * 获取菜单对应的权限集
//  * @param {string} params.service - 权限编码
//  */
// export async function getPermission(params) {
//   const organizationId = getCurrentOrganizationId();
//   return request(
//     `${HZERO_IAM}/hzero/v1/${isTenantRoleLevel() ? `${organizationId}/` : ''}menus/buttons`,
//     {
//       query: { ...params },
//       method: 'GET',
//     }
//   );
// }

/**
 * 检验权限
 * @param {array} params.code - 权限编码
 */
export async function checkPermission(params) {
  return request(`${HZERO_IAM}/hzero/v1/menus/check-permissions`, {
    method: 'POST',
    body: params,
  });
}
/**
 * 检验权限
 * @param {array} params.code - 权限编码
 */
export async function checkHistoryLog(params) {
  return request(`${HZERO_MNT}/v1/${getCurrentOrganizationId()}/audit-documents/page-route`, {
    method: 'GET',
    query: params,
  });
}

/**
 * 查询导航栏配置
 */
export async function checkEntryDirectory() {
  return request(
    `${HZERO_PLATFORM}/v1/${
      isTenantRoleLevel() ? `${getCurrentOrganizationId()}/` : ''
    }quick-entry/map-list`,
    {
      method: 'GET',
    }
  );
}
