/**
 * 用户信息相关
 * @date: 2019-12-25
 * @author: wjc <jiacheng.wang@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import { getDvaApp } from 'utils/iocUtils';

/**
 * 获取当前登录用户的角色信息
 */

export function getCurrentRole() {
  // eslint-disable-next-line no-underscore-dangle
  const state = getDvaApp()._store.getState();
  const { user = {} } = state;
  const { currentUser = {} } = user;
  const { currentRoleId, currentRoleName, currentRoleLevel, currentRoleCode } = currentUser;
  return {
    id: currentRoleId,
    name: currentRoleName,
    level: currentRoleLevel,
    code: currentRoleCode,
  };
}

/**
 * 判断角色层级是否是租户层级
 */
export function isTenantRoleLevel() {
  const { level } = getCurrentRole();
  return level !== 'site';
}

export function isEqualOrganization() {
  const { level } = getCurrentRole();
  return level === 'organization';
}

/**
 * 获取当前租户信息
 */
export function getCurrentTenant() {
  const state = getDvaApp()._store.getState();
  const { user = {} } = state;
  const { currentUser = {} } = user;
  const { tenantId, tenantName, tenantNum } = currentUser;
  return { tenantId, tenantName, tenantNum };
}

/**
 * 获取当前用户角色租户id
 */
export function getCurrentOrganizationId() {
  return getCurrentTenant().tenantId;
}

/**
 * 获取当前登录用户信息
 * @returns {object}
 */
export function getCurrentUser() {
  // eslint-disable-next-line no-underscore-dangle
  const state = getDvaApp()._store.getState();
  const { user = {} } = state;
  const { currentUser = {} } = user;
  return currentUser;
}

/**
 * 获取当前用户所属租户 ID
 */
export function getUserOrganizationId() {
  return getCurrentUser().organizationId;
}

/**
 * 获取当前登录用户id
 */
export function getCurrentUserId() {
  return getCurrentUser().id;
}

/**
 * 获取系统当前语言
 * @export
 * @returns
 */
export function getCurrentLanguage() {
  // eslint-disable-next-line no-underscore-dangle
  const state = getDvaApp()._store.getState();
  const { user = {} } = state;
  const { currentUser = {} } = user;
  const { language } = currentUser;
  return language;
}
