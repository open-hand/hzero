/**
 * openApp - 三方应用管理
 * @date: 2018-10-10
 * @author: wangjiacheng <jiacheng.wang@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import { isTenantRoleLevel, getCurrentOrganizationId } from 'utils/utils';
import request from 'utils/request';
import { HZERO_IAM } from 'utils/config';

const tenantId = getCurrentOrganizationId();

function openAppApi() {
  return isTenantRoleLevel() ? `/hzero/v1/${tenantId}/open-app` : `/hzero/v1/open-app`;
}

/**
 * 查询列表数据
 * @async
 * @function fetchOpenApp
 * @param {Object} params - 查询参数
 * @param {String} [params.page = 0] - 页码
 * @param {String} [params.size = 0] - 页数
 */
export async function fetchOpenAppList(params) {
  return request(`${HZERO_IAM}${openAppApi()}`, {
    method: 'GET',
    query: params,
  });
}

/**
 * 查询详细数据
 * @async
 * @function fetchOpenAppDetail
 * @param {Object} params - 查询参数
 * @param {string} params.openAppId - 三方应用ID
 */
export async function fetchOpenAppDetail(params) {
  return request(`${HZERO_IAM}${openAppApi()}/${params.openAppId}`, {
    method: 'GET',
    query: params.openAppId,
  });
}

/**
 * 创建
 * @async
 * @function createOpenApp
 * @param {object} params - 创建参数对象
 * @param {string} params.appCode - 应用编码
 * @param {string} params.appId - appId
 * @param {string} params.orderSeq - 序号
 * @param {string} params.appImage - 应用图片
 * @param {string} params.appName - 应用名称
 * @param {string} params.appKey - 授权码
 * @param {string} params.redirectUri - 回调地址
 * @param {string} params.authorizePath - 认证码地址
 * @param {string} params.tokenPath - AccessToken地址
 * @param {string} params.refreshTokenPath - RefreshToken地址
 * @param {string} params.selfPath - 个人信息地址
 * @param {string} params.scope - 授权列表
 */
export async function createOpenApp(params) {
  return request(`${HZERO_IAM}${openAppApi()}`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 更新
 * @async
 * @function updateOpenApp
 * @param {Object} params - 查询参数
 * @param {string} params.appCode - 应用编码
 * @param {string} params.appId - appId
 * @param {string} params.orderSeq - 序号
 * @param {string} params.appImage - 应用图片
 * @param {string} params.appName - 应用名称
 * @param {string} params.appKey - 授权码
 * @param {string} params.redirectUri - 回调地址
 * @param {string} params.authorizePath - 认证码地址
 * @param {string} params.tokenPath - AccessToken地址
 * @param {string} params.refreshTokenPath - RefreshToken地址
 * @param {string} params.selfPath - 个人信息地址
 * @param {string} params.scope - 授权列表
 */
export async function updateOpenApp(params) {
  return request(`${HZERO_IAM}${openAppApi()}`, {
    method: 'PUT',
    body: params,
  });
}

/**
 * 启用
 * @async
 * @function enabledOpenApp
 * @param {Object} params - 查询参数
 * @param {string} params.openAppId - 三方应用ID
 */
export async function enabledOpenApp(params) {
  return request(`${HZERO_IAM}${openAppApi()}/enabled`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 禁用
 * @async
 * @function disabledOpenApp
 * @param {Object} params - 查询参数
 * @param {string} params.openAppId - 三方应用ID
 */
export async function disabledOpenApp(params) {
  return request(`${HZERO_IAM}${openAppApi()}/disabled`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 删除
 * @async
 * @function deleteOpenApp
 * @param {String} params.enabledFlag - 是否启用
 * @param {string} params.openAppId - 三方应用ID
 */
export async function deleteOpenApp(params) {
  return request(`${HZERO_IAM}${openAppApi()}`, {
    method: 'DELETE',
    body: params,
  });
}
