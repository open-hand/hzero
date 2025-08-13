/**
 * 系统管理--模板管理
 * @date: 2019-6-28
 * @author: XL <liang.xiong@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import request from 'utils/request';
import { HZERO_PLATFORM } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';

const tenantId = getCurrentOrganizationId();
function templateConfigs() {
  return isTenantRoleLevel() ? `${tenantId}/template-configs` : 'template-configs';
}
/**
 * 删除模板配置
 * @param {Object} params - 参数
 */
export async function deleteTemplateConfigs(params) {
  return request(`${HZERO_PLATFORM}/v1/${templateConfigs()}`, {
    method: 'DELETE',
    body: params,
  });
}

/**
 * 查询模板配置列表
 * @param {Object} params - 查询参数
 */
export async function fetchTemplateConfigsList(params) {
  return request(`${HZERO_PLATFORM}/v1/${templateConfigs()}`, {
    method: 'GET',
    query: params,
  });
}

/**
 * 创建模板配置
 * @param {Object} params - 参数
 */
export async function createTemplateConfigs(params) {
  return request(`${HZERO_PLATFORM}/v1/${templateConfigs()}`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 修改模板配置
 * @param {Object} params - 参数
 */
export async function updateTemplateConfigs(params) {
  return request(`${HZERO_PLATFORM}/v1/${templateConfigs()}`, {
    method: 'PUT',
    body: params,
  });
}

/**
 * 查询模板配置明细
 * @param {Object} params - 查询参数
 */
export async function getTemplateConfigsDetail(params) {
  const { configId } = params;
  return request(`${HZERO_PLATFORM}/v1/${templateConfigs()}/${configId}`, {
    method: 'GET',
  });
}
