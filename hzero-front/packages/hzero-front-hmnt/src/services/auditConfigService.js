/**
 * audit-config  操作审计配置
 * @date: 2019-7-18
 * @author:  xl <liang.xiong@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import request from 'utils/request';
import { HZERO_MNT } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';

const tenantId = getCurrentOrganizationId();
function auditConfig() {
  return isTenantRoleLevel() ? `${tenantId}/audit-op-configs` : 'audit-op-configs';
}
/**
 * 删除操作审计配置
 * @param {Object} params - 参数
 */
export async function deleteAuditConfig(params) {
  return request(`${HZERO_MNT}/v1/${auditConfig()}`, {
    method: 'DELETE',
    body: params,
  });
}

/**
 * 查询操作审计配置列表
 * @param {Object} params - 查询参数
 */
export async function fetchAuditConfigList(params) {
  return request(`${HZERO_MNT}/v1/${auditConfig()}`, {
    method: 'GET',
    query: params,
  });
}

/**
 * 创建操作审计配置
 * @param {Object} params - 参数
 */
export async function createAuditConfig(params) {
  return request(`${HZERO_MNT}/v1/${auditConfig()}`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 修改操作审计配置
 * @param {Object} params - 参数
 */
export async function updateAuditConfig(params) {
  return request(`${HZERO_MNT}/v1/${auditConfig()}`, {
    method: 'PUT',
    body: params,
  });
}

/**
 * 查询操作审计配置明细
 * @param {Object} params - 查询参数
 */
export async function getAuditConfigDetail(params) {
  const { auditOpConfigId } = params;
  return request(`${HZERO_MNT}/v1/${auditConfig()}/${auditOpConfigId}`, {
    method: 'GET',
  });
}

/**
 * 查询关联数据审计列表
 * @param {Object} params - 查询参数
 */
export async function fetchAboutDataList(params) {
  return request(
    `${HZERO_MNT}/v1${isTenantRoleLevel() ? `/${tenantId}` : ''}/audit-rel/operational/data`,
    {
      method: 'GET',
      query: params,
    }
  );
}

/**
 * 创建关联数据审计
 * @param {Object} params - 参数
 */
export async function createAboutDataAudit(params) {
  return request(
    `${HZERO_MNT}/v1${isTenantRoleLevel() ? `/${tenantId}` : ''}/audit-rel/relationship`,
    {
      method: 'POST',
      query: params,
    }
  );
}

/**
 * 删除关联数据审计
 * @param {Object} params - 参数
 */
export async function deleteAboutDataAudit(params) {
  return request(
    `${HZERO_MNT}/v1${isTenantRoleLevel() ? `/${tenantId}` : ''}/audit-rel/relationship`,
    {
      method: 'DELETE',
      body: params,
    }
  );
}
