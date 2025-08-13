/**
 * service 多语言
 * @date: 2018-6-20
 * @version: 0.0.1
 * @author:  王家程 <jiacheng.wang@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import request from 'utils/request';
import { HZERO_PLATFORM, VERSION_IS_OP } from 'utils/config';
import { isTenantRoleLevel, getCurrentOrganizationId } from 'utils/utils';

const tenantId = getCurrentOrganizationId();

// 多语言层级判断
function promptApi() {
  return isTenantRoleLevel() ? `${tenantId}/prompts` : `prompts`;
}

function api() {
  return VERSION_IS_OP ? `${tenantId}/prompts` : `prompts`;
}

/**
 * 查询语言列表
 * @param {Object} params - 查询参数
 * @param {String} params.page - 页码
 * @param {String} params.size - 页数
 */
export async function fetchPromptList(params) {
  return request(`${HZERO_PLATFORM}/v1/${promptApi()}/page-list`, {
    method: 'GET',
    query: params,
  });
}

export async function fetchPromptDetail(params) {
  return request(`${HZERO_PLATFORM}/v1/${promptApi()}/detail`, {
    method: 'GET',
    query: params,
  });
}

/**
 * 新增或更新语言
 * @param {Object} params - 参数
 * @param {String} params.promptKey - 模块代码
 * @param {String} params.promptCode - 代码
 * @param {String} params.lang - 语言名称
 * @param {String} params.description - 描述
 */
export async function createPrompt(params) {
  return request(`${HZERO_PLATFORM}/v1/${promptApi()}/insert`, {
    method: 'POST',
    body: { ...params },
  });
}

export async function updatePrompt(params) {
  return request(`${HZERO_PLATFORM}/v1/${promptApi()}/update`, {
    method: 'PUT',
    body: { ...params },
  });
}
/**
 * 删除语言
 * @param {Object} params - 参数
 */
export async function deletePrompt(params) {
  return request(`${HZERO_PLATFORM}/v1/${promptApi()}/remove`, {
    method: 'DELETE',
    body: params,
  });
}

export async function refresh(params) {
  return request(`${HZERO_PLATFORM}/v1/${api()}/refresh/cache`, {
    method: 'POST',
    body: params,
  });
}

function languageApi() {
  return isTenantRoleLevel() ? `${tenantId}/languages` : 'languages';
}

export async function queryLanguage() {
  return request(`${HZERO_PLATFORM}/v1/${languageApi()}`, {
    method: 'GET',
  });
}
