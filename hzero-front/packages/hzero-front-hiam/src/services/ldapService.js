/**
 * ldapService.js - LDAP service
 * @date: 2018-12-20
 * @author: LZY <zhuyan.luo@hand-china.com>
 * @version: 0.0.1
 * @copyright: Copyright (c) 2018, Hand
 */

import request from 'utils/request';
import { HZERO_IAM } from 'utils/config';

/**
 * 查询LDAP数据
 * @param {Object} params - 查询参数
 */
export async function fetchLDAP(params) {
  return request(`${HZERO_IAM}/v1/${params.tenantId}/ldaps`, {
    method: 'GET',
  });
}
/**
 * 更新LDAP
 * @param {Object} params
 */
export async function updateLDAP(params) {
  const { tenantId, ...others } = params;
  return request(`${HZERO_IAM}/v1/${tenantId}/ldaps`, {
    method: 'PUT',
    body: others,
  });
}
/**
 * 连接测试
 * @param {Object} params
 */
export async function testConnect(params) {
  const { tenantId, id, ...others } = params;
  return request(`${HZERO_IAM}/v1/${tenantId}/ldaps/${id}/test-connect`, {
    method: 'POST',
    body: others,
  });
}
/**
 * 禁用LDAP
 * @param {Object} params
 */
export async function disabledLDAP(params) {
  const { tenantId, ...others } = params;
  return request(`${HZERO_IAM}/v1/${tenantId}/ldaps/disable`, {
    method: 'PUT',
    body: others,
  });
}
/**
 * 启用LDAP
 * @param {Object} params
 */
export async function enabledLDAP(params) {
  const { tenantId, ...others } = params;
  return request(`${HZERO_IAM}/v1/${tenantId}/ldaps/enable`, {
    method: 'PUT',
    body: others,
  });
}
/**
 * 查询数据同步信息
 * @param {Object} params - 查询参数
 */
export async function fetchSyncInfo(params) {
  const { tenantId, id, ...others } = params;
  return request(`${HZERO_IAM}/v1/${tenantId}/ldaps/${id}/history`, {
    method: 'GET',
    query: others,
    // responseType: 'text',
  });
}
/**
 * 查询数据同步错误信息
 * @param {Object} params - 查询参数
 */
export async function fetchSyncErrorInfo(params) {
  const { tenantId, ldapHistoryId, ...others } = params;
  return request(`${HZERO_IAM}/v1/${tenantId}/ldaps/ldap-histories/${ldapHistoryId}/error-users`, {
    method: 'GET',
    query: others,
  });
}
/**
 * 同步用户
 * @param {Object} params
 */
export async function syncUser(params) {
  const { tenantId, id } = params;
  return request(`${HZERO_IAM}/v1/${tenantId}/ldaps/${id}/sync-users`, {
    method: 'POST',
  });
}
/**
 * 停止同步用户
 * @param {Object} params
 */
export async function stopSyncUser(params) {
  const { tenantId, id } = params;
  return request(`${HZERO_IAM}/v1/${tenantId}/ldaps/${id}/stop`, {
    method: 'PUT',
  });
}

/**
 * 查询ldap同步离职
 * @param {Object} params - 查询参数
 */
export async function querySyncLeave(params) {
  const { tenantId } = params;
  return request(`${HZERO_IAM}/v1/${tenantId}/ldaps/sync-leave-config`, {
    method: 'GET',
  });
}
/**
 * 更新ldap同步离职
 * @param {Object} params
 */
export async function updateSyncLeave(params) {
  const { tenantId } = params;
  return request(`${HZERO_IAM}/v1/${tenantId}/ldaps/sync-leave-config`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 查询ldap同步用户
 * @param {Object} params - 查询参数
 */
export async function querySyncUser(params) {
  const { tenantId } = params;
  return request(`${HZERO_IAM}/v1/${tenantId}/ldaps/sync-user-config`, {
    method: 'GET',
  });
}
/**
 * 更新ldap同步用户
 * @param {Object} params
 */
export async function updateSyncUser(params) {
  const { tenantId } = params;
  return request(`${HZERO_IAM}/v1/${tenantId}/ldaps/sync-user-config`, {
    method: 'POST',
    body: params,
  });
}
