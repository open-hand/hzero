/**
 * service - 数据变更审计配置
 * @date: 2019/7/10
 * @author: hulingfangzi <lingfangzi.hu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import request from 'utils/request';
import { HZERO_MNT } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel, parseParameters } from 'utils/utils';

const organizationId = getCurrentOrganizationId();
const organizationRoleLevel = isTenantRoleLevel();
/**
 * 查询数据审计配置列表
 * @param {*} params - 参数
 */
export async function queryConfigList(params = {}) {
  return request(
    organizationRoleLevel
      ? `${HZERO_MNT}/v1/${organizationId}/audit-data-configs`
      : `${HZERO_MNT}/v1/audit-data-configs`,
    {
      query: parseParameters(params),
    }
  );
}

/**
 * 更新数数据审计配置列表
 * @param {object} data - 列表数据
 */
export async function updateConfigList(data) {
  return request(
    organizationRoleLevel
      ? `${HZERO_MNT}/v1/${organizationId}/audit-data-configs`
      : `${HZERO_MNT}/v1/audit-data-configs`,
    {
      method: 'PUT',
      body: [...data],
    }
  );
}

/**
 * 数据审计配置列表启用/禁用
 * @param {object} data - 列表行数据
 */
export async function enableConfigList(data) {
  return request(
    organizationRoleLevel
      ? `${HZERO_MNT}/v1/${organizationId}/audit-data-configs`
      : `${HZERO_MNT}/v1/audit-data-configs`,
    {
      method: 'POST',
      body: { ...data },
    }
  );
}

/**
 * 查询详情列表
 * @param {*} params - 参数
 */
export async function queryDetailList(params = {}) {
  const { auditDataConfigId } = params;
  return request(
    organizationRoleLevel
      ? `${HZERO_MNT}/v1/${organizationId}/audit-data-config-lines/${auditDataConfigId}`
      : `${HZERO_MNT}/v1/audit-data-config-lines/${auditDataConfigId}`,
    {
      query: parseParameters(params),
    }
  );
}

/**
 * 详情列表启用/禁用
 * @param {object} data - 列表行数据
 */
export async function enableDetailList(data) {
  return request(
    organizationRoleLevel
      ? `${HZERO_MNT}/v1/${organizationId}/audit-data-config-lines`
      : `${HZERO_MNT}/v1/audit-data-config-lines`,
    {
      method: 'POST',
      body: { ...data },
    }
  );
}

/**
 * 更新详情列表
 * @param {object} data - 详情列表数据
 */
export async function updateDetailList(data) {
  return request(
    organizationRoleLevel
      ? `${HZERO_MNT}/v1/${organizationId}/audit-data-config-lines`
      : `${HZERO_MNT}/v1/audit-data-config-lines`,
    {
      method: 'PUT',
      body: [...data],
    }
  );
}

/**
 * 查询列表行
 * @param {*} params - 参数
 */
export async function queryLineDetail(params = {}) {
  const { auditDataConfigId } = params;
  return request(
    organizationRoleLevel
      ? `${HZERO_MNT}/v1/${organizationId}/audit-data-configs/${auditDataConfigId}`
      : `${HZERO_MNT}/v1/audit-data-configs/${auditDataConfigId}`
  );
}
/**
 * 查询关联操作审计列表
 * @param {Object} params - 查询参数
 */
export async function fetchAboutConfigList(params) {
  return request(
    `${HZERO_MNT}/v1${
      organizationRoleLevel ? `/${organizationId}` : ''
    }/audit-rel/data/operational`,
    {
      method: 'GET',
      query: params,
    }
  );
}

/**
 * 创建关联操作审计
 * @param {Object} params - 参数
 */
export async function createAboutConfigAudit(params) {
  return request(
    `${HZERO_MNT}/v1${organizationRoleLevel ? `/${organizationId}` : ''}/audit-rel/relationship`,
    {
      method: 'POST',
      query: params,
    }
  );
}

/**
 * 删除关联操作审计
 * @param {Object} params - 参数
 */
export async function deleteAboutConfigAudit(params) {
  return request(
    `${HZERO_MNT}/v1${organizationRoleLevel ? `/${organizationId}` : ''}/audit-rel/relationship`,
    {
      method: 'DELETE',
      body: params,
    }
  );
}

/**
 * 保存编辑数据
 * @param {object} data - 数据
 */
export async function updateDataAuditConfig(data) {
  return request(
    organizationRoleLevel
      ? `${HZERO_MNT}/v1/${organizationId}/audit-data-configs/single`
      : `${HZERO_MNT}/v1/audit-data-configs/single`,
    {
      method: 'PUT',
      body: { ...data },
    }
  );
}
