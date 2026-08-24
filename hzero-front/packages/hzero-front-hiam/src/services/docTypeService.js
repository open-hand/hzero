/**
 * docType.js - 单据权限定义 service
 * @date: 2018-10-29
 * @author: geekrainy <chao.zheng02@hand-china.com>
 * @version: 0.0.1
 * @copyright: Copyright (c) 2018, Hand
 */

import request from 'utils/request';
import { parseParameters, isTenantRoleLevel, getCurrentOrganizationId } from 'utils/utils';
import { HZERO_IAM } from 'utils/config';

const tenantId = getCurrentOrganizationId();

function docTypeApi() {
  return isTenantRoleLevel() ? `${tenantId}/doc-types` : `doc-types`;
}

/**
 * 查询单据类型定义
 * @param {Object} params - 查询参数
 */
export async function queryDocType(params) {
  return request(`${HZERO_IAM}/v1/${isTenantRoleLevel() ? `${tenantId}/doc-types` : 'doc-types'}`, {
    method: 'GET',
    query: parseParameters(params),
  });
}

/**
 * 查询单据类型定义详情
 * @param {Object} params - 查询参数
 */
export async function queryDocTypeDetail(params) {
  return request(`${HZERO_IAM}/v1/${docTypeApi()}/${params.docTypeId}`, {
    method: 'GET',
    query: params,
  });
}

/**
 * 新增单据类型定义
 * @param {Object} params - 新增单据类型定义
 */
export async function addDocType(params) {
  const { body } = params;
  return request(`${HZERO_IAM}/v1/${docTypeApi()}`, {
    method: 'POST',
    body,
  });
}

/**
 * 更新单据类型定义
 * @param {Object} params - 更新单据类型定义
 */
export async function updateDocType(params) {
  const { body } = params;
  return request(`${HZERO_IAM}/v1/${docTypeApi()}`, {
    method: 'PUT',
    body,
  });
}

/**
 * 查询单据类型权限维度
 * @param {Object} params - 查询参数
 */
export async function queryDocTypeAuth(params) {
  return request(`${HZERO_IAM}/v1/${docTypeApi()}/${params.docTypeId}/auth-dim`, {
    method: 'GET',
    query: params,
  });
}

/**
 * 更新单据类型权限维度
 * @param {Object} params - 更新单据类型权限维度数据
 */
export async function updateDocTypeAuth(params) {
  const { docTypeId, body } = params;
  return request(`${HZERO_IAM}/v1/${docTypeApi()}/${docTypeId}/auth-dim/save`, {
    method: 'POST',
    body,
  });
}
