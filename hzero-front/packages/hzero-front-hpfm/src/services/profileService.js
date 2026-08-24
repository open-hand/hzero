import request from 'utils/request';
import { HZERO_PLATFORM } from 'utils/config';
import { parseParameter } from 'utils/utils';

// 配置维护

/**
 * 查询列表
 * @returns {Promise<Object>}
 */
export async function profileSiteQueryPage(params) {
  const query = parseParameter(params);
  return request(`${HZERO_PLATFORM}/v1/profiles`, {
    method: 'GET',
    query,
  });
}

/**
 * 删除配置头
 * @returns {Promise<Object>}
 */
export async function profileSiteRemoveOne(params) {
  return request(`${HZERO_PLATFORM}/v1/profiles`, {
    method: 'DELETE',
    body: params,
  });
}

/**
 * 查询配置头行
 * @returns {Promise<Object>}
 */
export async function profileSiteQueryProfileValue(profileId) {
  return request(`${HZERO_PLATFORM}/v1/profiles/${profileId}`, {
    method: 'GET',
  });
}

/**
 * 删除配置行
 * @returns {Promise<Object>}
 */
export async function profileValueSiteRemoveOne(params) {
  return request(`${HZERO_PLATFORM}/v1/profiles-value`, {
    method: 'DELETE',
    body: params,
  });
}

/**
 * 保存配置 头行
 * @returns {Promise<Object>}
 */
export async function profileSiteSaveOne(profile) {
  return request(`${HZERO_PLATFORM}/v1/profiles`, {
    method: 'POST',
    body: profile,
  });
}

// 租户级

/**
 * 查询列表
 * @returns {Promise<Object>}
 */
export async function profileOrgQueryPage(organizationId, params) {
  const query = parseParameter(params);
  return request(`${HZERO_PLATFORM}/v1/${organizationId}/profiles`, {
    method: 'GET',
    query,
  });
}

/**
 * 删除配置头
 * @returns {Promise<Object>}
 */
export async function profileOrgRemoveOne(organizationId, profile) {
  return request(`${HZERO_PLATFORM}/v1/${organizationId}/profiles`, {
    method: 'DELETE',
    body: profile,
  });
}

/**
 * 查询配置头行
 * @returns {Promise<Object>}
 */
export async function profileOrgQueryProfileValue(organizationId, profileId) {
  return request(`${HZERO_PLATFORM}/v1/${organizationId}/profiles/${profileId}`, {
    method: 'GET',
  });
}

/**
 * 删除配置行
 * @returns {Promise<Object>}
 */
export async function profileValueOrgRemoveOne(organizationId, params) {
  return request(`${HZERO_PLATFORM}/v1/${organizationId}/profiles-value`, {
    method: 'DELETE',
    body: params,
  });
}

/**
 * 保存配置 头行
 * @returns {Promise<Object>}
 */
export async function profileOrgSaveOne(organizationId, profile) {
  return request(`${HZERO_PLATFORM}/v1/${organizationId}/profiles`, {
    method: 'POST',
    body: profile,
  });
}
