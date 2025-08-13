import request from 'utils/request';
import { HZERO_IAM } from 'utils/config';
import { getCurrentOrganizationId } from 'utils/utils';

const organizationId = getCurrentOrganizationId();

/**
 * 获取表单数据
 * @param {Number} organizationId 租户id
 */
export async function fetchPasswordPolicyList() {
  return request(`${HZERO_IAM}/v1/${organizationId}/password-policies`, {
    method: 'GET',
  });
}

/**
 * 更新表单数据
 * @param {Number} organizationId 租户id
 * @param {Number} id 数据id
 * @param {String} params 其他参数
 */
export async function updatePasswordPolicy(params) {
  return request(`${HZERO_IAM}/v1/${organizationId}/password-policies`, {
    method: 'PUT',
    body: params,
  });
}

/**
 * 更新二次校验用户列表
 * @param {Number} organizationId 租户id
 * @param {String} params 其他参数
 */
export async function fetchUserCheckList(params) {
  return request(`${HZERO_IAM}/hzero/v1/${organizationId}/users/sec-check`, {
    method: 'GET',
    query: params,
  });
}

/**
 * 新增二次校验用户列表
 * @param {Number} organizationId 租户id
 * @param {String} params 参数
 */
export async function addUserPhoneCheckList(params) {
  return request(`${HZERO_IAM}/hzero/v1/${organizationId}/users/sec-check/enable/phone`, {
    method: 'PUT',
    body: params,
  });
}

/**
 * 删除二次校验用户列表
 * @param {Number} organizationId 租户id
 * @param {String} params 参数
 */
export async function deleteUserPhoneCheckList(params) {
  return request(`${HZERO_IAM}/hzero/v1/${organizationId}/users/sec-check/disable/phone`, {
    method: 'PUT',
    body: params,
  });
}

/**
 * 新增二次校验用户列表
 * @param {Number} organizationId 租户id
 * @param {String} params 参数
 */
export async function addUserEmailCheckList(params) {
  return request(`${HZERO_IAM}/hzero/v1/${organizationId}/users/sec-check/enable/email`, {
    method: 'PUT',
    body: params,
  });
}

/**
 * 删除二次校验用户列表
 * @param {Number} organizationId 租户id
 * @param {String} params 参数
 */
export async function deleteUserEmailCheckList(params) {
  return request(`${HZERO_IAM}/hzero/v1/${organizationId}/users/sec-check/disable/email`, {
    method: 'PUT',
    body: params,
  });
}
