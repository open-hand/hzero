/**
 * UserGroupManagement 用户组管理
 * @date: 2019-1-14
 * @version: 0.0.1
 * @author:  guochaochao <chaochao.guo@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import request from 'utils/request';
import { HZERO_IAM } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel, parseParameters } from 'utils/utils';

const tenantId = getCurrentOrganizationId();

function userGroup() {
  return isTenantRoleLevel() ? `${tenantId}/user-groups` : `user-groups`;
}

/**
 * 删除用户组
 * @param {Object} params - 参数
 */
export async function deleteUserGroup(params) {
  return request(`${HZERO_IAM}/v1/${userGroup()}`, {
    method: 'DELETE',
    body: params,
  });
}

/**
 * 查询用户组
 * @param {Object} params - 查询参数
 */
export async function fetchUserGroupList(params) {
  return request(`${HZERO_IAM}/v1/${userGroup()}`, {
    method: 'GET',
    query: params,
  });
}

/**
 * 创建用户组
 * @param {Object} params - 参数
 */
export async function createUserGroup(params) {
  return request(`${HZERO_IAM}/v1/${userGroup()}`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 修改用户组
 * @param {Object} params - 参数
 */
export async function updateUserGroup(params) {
  return request(`${HZERO_IAM}/v1/${userGroup()}`, {
    method: 'PUT',
    body: params,
  });
}

/**
 * 查询用户组明细
 * @param {Object} params - 查询参数
 */
export async function getUserGroupDetail(params) {
  const { userGroupId } = params;
  return request(`${HZERO_IAM}/v1/${userGroup()}/details/${userGroupId}`, {
    method: 'GET',
  });
}

/**
 * 查询 剩余未分配用户组
 * @return {Promise<void>}
 */
export async function getCurrentRestGroupUsers(userGroupId, params = {}) {
  const parsedParams = parseParameters(params);
  return request(`${HZERO_IAM}/v1/${userGroup()}/${userGroupId}/users/exclude-users`, {
    method: 'GET',
    query: parsedParams,
  });
}

/**
 * 分配 用户给用户组
 * @return {Promise<void>}
 */
export async function assignUsersToGroup(userGroupId, groupTenantId, body) {
  return request(`${HZERO_IAM}/v1/${userGroup()}/${userGroupId}/users`, {
    method: 'POST',
    body,
    query: {
      tenantId: groupTenantId,
    },
  });
}

/**
 * 删除 已分配的用户
 * @return {Promise<void>}
 */
export async function delCurrentGroupUsers(userGroupId, body) {
  return request(`${HZERO_IAM}/v1/${userGroup()}/${userGroupId}/users`, {
    method: 'DELETE',
    body,
  });
}

/**
 * 查询 已分配的用户组
 * @return {Promise<void>}
 */
export async function getCurrentGroupUsers(userGroupId, params) {
  const parsedParams = parseParameters(params);
  return request(`${HZERO_IAM}/v1/${userGroup()}/${userGroupId}/users`, {
    method: 'GET',
    query: parsedParams,
  });
}
