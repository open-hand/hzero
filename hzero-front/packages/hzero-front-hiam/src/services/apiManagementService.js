/**
 * @since 2019-12-03
 * @author WT <tao13.wang@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import { HZERO_IAM } from 'utils/config';
import request from 'utils/request';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';

const organizationId = getCurrentOrganizationId();
/**
 * 批量分配API
 * @async
 * @function distributeApi
 * @param {object} params - 分配参数
 */
export async function distributeApi(params) {
  const url = isTenantRoleLevel()
    ? `${HZERO_IAM}/hzero/v1/${organizationId}/permissions/assign`
    : `${HZERO_IAM}/hzero/v1/permissions/assign`;

  return request(url, {
    method: 'POST',
    body: params,
  });
}

/**
 * 给单一租户分配API
 * @async
 * @function distributeApi
 * @param {object} params - 分配参数
 */
export async function distributeApiToTenant(params) {
  const { tenantId, permissions } = params;
  const url = isTenantRoleLevel()
    ? `${HZERO_IAM}/hzero/v1/${organizationId}/permissions/assign/${tenantId}`
    : `${HZERO_IAM}/hzero/v1/permissions/assign/${tenantId}`;
  return request(url, {
    method: 'POST',
    body: permissions,
  });
}

/**
 * 新建
 * @async
 * @function deleteBatchApi
 * @param {object} params - 删除项
 */
export async function deleteBatchApi(params) {
  const url = isTenantRoleLevel()
    ? `${HZERO_IAM}/hzero/v1/${organizationId}/permissions`
    : `${HZERO_IAM}/hzero/v1/permissions`;
  return request(url, {
    method: 'DELETE',
    body: params,
  });
}

/**
 * 删除单一用户的API
 * @async
 * @function deleteSingleTenantAPI
 * @param {object} params - 删除项
 */
export async function deleteSingleTenantAPI(params) {
  const { permissions } = params;
  const url = isTenantRoleLevel()
    ? `${HZERO_IAM}/hzero/v1/${organizationId}/permissions/revoke`
    : `${HZERO_IAM}/hzero/v1/permissions/revoke`;
  return request(url, {
    method: 'DELETE',
    body: permissions,
  });
}

export async function queryApiLabel(params) {
  return request(
    isTenantRoleLevel()
      ? `${HZERO_IAM}/v1/${organizationId}/label-rels/API/${params}`
      : `${HZERO_IAM}/v1/label-rels/API/${params}`,
    {
      method: 'GET',
    }
  );
}
