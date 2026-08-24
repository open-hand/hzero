/**
 *
 * @date: 2019-6-28
 * @author: XL <liang.xiong@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import request from 'utils/request';
import { HZERO_PLATFORM } from 'utils/config';
import { parseParameters, getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';

const tenantId = getCurrentOrganizationId();
function templateConfigs() {
  return isTenantRoleLevel() ? `${tenantId}/template-configs` : 'template-configs';
}
/**
 * 删除模板配置
 * @param {Object} params - 参数
 */
export async function deleteTemplateConfigs(params) {
  return request(`${HZERO_PLATFORM}/v1/${templateConfigs()}/batch-delete`, {
    method: 'DELETE',
    body: params,
  });
}

/**
 * 查询模板配置列表
 * @param {Object} params - 查询参数
 */
export async function fetchTemplateConfigsList(params) {
  const parseParams = parseParameters(params);
  return request(`${HZERO_PLATFORM}/v1/${templateConfigs()}/${params.templateAssignId}`, {
    method: 'GET',
    query: parseParams,
  });
}

/**
 * 创建模板配置
 * @param {Object} params - 参数
 */
export async function createTemplateConfigs(params) {
  const param = parseParameters(params);
  return request(`${HZERO_PLATFORM}/v1/${templateConfigs()}/${params.templateAssignId}`, {
    method: 'POST',
    body: param,
  });
}

/**
 * 修改模板配置
 * @param {Object} params - 参数
 */
export async function updateTemplateConfigs(params) {
  return request(`${HZERO_PLATFORM}/v1/${templateConfigs()}/${params.templateAssignId}`, {
    method: 'PUT',
    body: params,
  });
}

/**
 * 查询模板配置明细
 * @param {Object} params - 查询参数
 */
export async function getTemplateConfigsDetail(params) {
  return request(`${HZERO_PLATFORM}/v1/${templateConfigs()}/details/${params.configId}`, {
    method: 'GET',
  });
}
