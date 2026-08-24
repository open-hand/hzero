/**
 * template - 通用模板
 * @since 2018-08-28
 * @author yuan.tian <yuan.tian@hand-china.com>
 * @version 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import request from 'utils/request';
import { HZERO_IMP, HZERO_PLATFORM } from 'utils/config';
import { parseParameters, getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';

const tenantId = getCurrentOrganizationId();

function templateHeaderApi() {
  return isTenantRoleLevel() ? `${tenantId}/template-headers` : `template-headers`;
}

function templateLineApi() {
  return isTenantRoleLevel() ? `${tenantId}/template-lines` : `template-lines`;
}

/**
 * 查询模板头数据列表
 * @async
 * @function query
 * @param {Object} params - 查询参数
 */
export async function query(params) {
  const { organizationId, ...otherParams } = params;
  const param = parseParameters(otherParams);
  return request(`${HZERO_IMP}/v1/${templateHeaderApi()}`, {
    method: 'GET',
    query: param,
  });
}
/**
 * 查询模板单条头数据信息
 * @async
 * @function queryOneHeader
 * @param {Object} params - 查询参数
 */
export async function queryOneHeader(params) {
  const { templateId } = params;
  return request(`${HZERO_IMP}/v1/${templateHeaderApi()}/${templateId}`, {
    method: 'GET',
  });
}
/**
 * 更新头数据信息
 * @async
 * @function update
 * @param {Object} params - 查询参数
 */
export async function update(params) {
  return request(`${HZERO_IMP}/v1/${templateHeaderApi()}`, {
    method: 'PUT',
    body: { tenantId, ...params },
  });
}

/**
 * 创建头数据
 * @async
 * @function create
 * @param {Object} params - 查询参数
 */
export async function create(params) {
  return request(`${HZERO_IMP}/v1/${templateHeaderApi()}`, {
    method: 'POST',
    body: { tenantId, ...params },
  });
}

/**
 * 移除单条头数据
 * @async
 * @function removeHeader
 * @param {Object} params - 查询参数
 */
export async function removeHeader(params) {
  const { organizationId, ...otherParams } = params;
  return request(`${HZERO_IMP}/v1/${templateHeaderApi()}`, {
    method: 'DELETE',
    body: otherParams,
  });
}

/**
 * 获取模板列列表数据
 * @async
 * @function fetchColumnList
 * @param {Object} params - 查询参数
 */
export async function fetchColumnList(params) {
  return request(`${HZERO_IMP}/v1/${templateLineApi()}`, {
    method: 'GET',
    query: { tenantId, ...params },
  });
}

/**
 * 获取模板列详情数据
 * @async
 * @function fetchColumnDetail
 * @param {Object} params - 查询参数
 */
export async function fetchColumnDetail(params) {
  return request(`${HZERO_IMP}/v1/${templateLineApi()}/${params.id}`, {
    method: 'GET',
    query: { tenantId },
  });
}

/**
 * 创建模板列
 * @async
 * @function createColumn
 * @param {Object} params - 查询参数
 */
export async function createColumn(params) {
  return request(`${HZERO_IMP}/v1/${templateLineApi()}`, {
    method: 'POST',
    body: { tenantId, ...params },
  });
}

/**
 * 更新模板列数据
 * @async
 * @function updateColumn
 * @param {Object} params - 查询参数
 */
export async function updateColumn(params) {
  return request(`${HZERO_IMP}/v1/${templateLineApi()}`, {
    method: 'PUT',
    body: { tenantId, ...params },
  });
}

/**
 * 删除模板列数据
 * @async
 * @function updateColumn
 * @param {Object} params - 查询参数
 */
export async function deleteColumn(params) {
  return request(`${HZERO_IMP}/v1/${templateLineApi()}`, {
    method: 'DELETE',
    body: { tenantId, ...params },
  });
}

/**
 * 查询值集
 * @async
 * @function queryCode
 * @param {object} params - 查询条件
 * @param {!string} param.lovCode - 查询条件
 * @returns {object} fetch Promise
 */
export async function queryCode(params = {}) {
  return request(`${HZERO_PLATFORM}/v1/lovs/value`, {
    query: params,
  });
}
