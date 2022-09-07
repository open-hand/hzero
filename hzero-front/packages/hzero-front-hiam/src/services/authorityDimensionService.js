/**
 *  authorityDimensionService
 * @date: 2019-9-23
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */

import request from 'utils/request';
import { HZERO_IAM } from 'utils/config';
import { getCurrentOrganizationId, parseParameters } from 'utils/utils';

/**
 * 查询
 * @export
 * @param {object} params - 查询参数

 */
export async function queryUserAuthorityData(params) {
  const organizationId = getCurrentOrganizationId();
  const param = parseParameters(params);
  return request(`${HZERO_IAM}/v1/${organizationId}/users/${param.userId}/authority`, {
    method: 'GET',
    query: param,
  });
}

/**
 * 查询
 * @export
 * @param {object} params - 查询参数

 */
export async function addUserAuthorityData(params) {
  const {
    userId,
    authorityTypeCode,
    authData: userAuthority,
    authDataLineList: userAuthorityLineList,
    ...others
  } = params;
  const organizationId = getCurrentOrganizationId();
  return request(`${HZERO_IAM}/v1/${organizationId}/users/${userId}/authority`, {
    method: 'POST',
    query: { authorityTypeCode },
    body: { userAuthorityLineList, userAuthority, ...others },
  });
}

/**
 * 查询
 * @export
 * @param {object} params - 查询参数

 */
export async function deleteUserAuthorityData(params) {
  const { userId, deleteRows } = params;
  const organizationId = getCurrentOrganizationId();
  return request(`${HZERO_IAM}/v1/${organizationId}/users/${userId}/authority`, {
    method: 'DELETE',
    body: deleteRows,
  });
}

/**
 * 查询
 * @export
 * @param {object} params - 查询参数

 */
export async function queryRoleAuthorityData(params) {
  const organizationId = getCurrentOrganizationId();
  const param = parseParameters(params);
  return request(`${HZERO_IAM}/v1/${organizationId}/role/${param.roleId}/authority`, {
    method: 'GET',
    query: param,
  });
}

/**
 * 查询
 * @export
 * @param {object} params - 查询参数

 */
export async function addRoleAuthorityData(params) {
  const {
    roleId,
    authorityTypeCode,
    authData: roleAuthData,
    authDataLineList: roleAuthDataLineList,
    ...others
  } = params;
  const organizationId = getCurrentOrganizationId();
  return request(`${HZERO_IAM}/v1/${organizationId}/role/${roleId}/authority`, {
    method: 'POST',
    query: { authorityTypeCode },
    body: { roleAuthData, roleAuthDataLineList, roleId, authorityTypeCode, ...others },
  });
}

/**
 * 查询
 * @export
 * @param {object} params - 查询参数

 */
export async function deleteRoleAuthorityData(params) {
  const { roleId, deleteRows } = params;
  const organizationId = getCurrentOrganizationId();
  return request(`${HZERO_IAM}/v1/${organizationId}/role/${roleId}/authority`, {
    method: 'DELETE',
    body: deleteRows,
  });
}
