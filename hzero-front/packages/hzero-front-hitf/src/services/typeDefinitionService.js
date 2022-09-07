import request from 'utils/request';
import { HZERO_HITF, HZERO_PLATFORM } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel, parseParameters } from 'utils/utils';

const organizationId = getCurrentOrganizationId();
const organizationRoleLevel = isTenantRoleLevel();

/**
 * 请求应用定义列表
 * @param {obejct} params - 参数类型
 */
export async function queryList(params = {}) {
  return request(
    organizationRoleLevel
      ? `${HZERO_HITF}/v1/${organizationId}/applications`
      : `${HZERO_HITF}/v1/applications`,
    {
      query: parseParameters(params),
    }
  );
}

/**
 * 删除应用定义
 * @param {obejct} payload - 应用类型定义列表选中行
 */
export async function deleteDefinition(payload) {
  return request(
    organizationRoleLevel
      ? `${HZERO_HITF}/v1/${organizationId}/applications`
      : `${HZERO_HITF}/v1/applications`,
    {
      method: 'DELETE',
      body: payload,
    }
  );
}

/**
 * 查询应用类型详情
 * @param {number} applicationId - 应用ID
 */
export async function queryDefinition(applicationId) {
  return request(
    organizationRoleLevel
      ? `${HZERO_HITF}/v1/${organizationId}/applications/${applicationId}`
      : `${HZERO_HITF}/v1/applications/${applicationId}`,
    {
      query: null,
    }
  );
}

/**
 * 创建/修改应用类型
 * @param {object} payload - 表单内容
 */
export async function saveDefinition(payload) {
  return request(
    organizationRoleLevel
      ? `${HZERO_HITF}/v1/${organizationId}/applications`
      : `${HZERO_HITF}/v1/applications`,
    {
      method: 'POST',
      body: payload,
    }
  );
}

/**
 * 查询应用实例列表
 * @param {obejct} params - 参数类型
 */
export async function queryInstanceLineList(params = {}) {
  const { applicationId } = params;
  return request(
    organizationRoleLevel
      ? `${HZERO_HITF}/v1/${organizationId}/application-insts/${applicationId}/page`
      : `${HZERO_HITF}/v1/application-insts/${applicationId}/page`,
    {
      query: parseParameters(params),
    }
  );
}

/**
 * 删除应用实例
 * @param {obejct} payload - 应用实例列表选中行
 */
export async function deleteInstanceLineList(payload) {
  return request(
    organizationRoleLevel
      ? `${HZERO_HITF}/v1/${organizationId}/application-insts`
      : `${HZERO_HITF}/v1/application-insts`,
    {
      method: 'DELETE',
      body: payload,
    }
  );
}

/**
 * 查询应用实例详情
 * @param {number} applicationInstId - 实例id
 */
export async function queryInstanceDetail(applicationInstId) {
  return request(
    organizationRoleLevel
      ? `${HZERO_HITF}/v1/${organizationId}/application-insts/${applicationInstId}`
      : `${HZERO_HITF}/v1/application-insts/${applicationInstId}`
  );
}

/**
 * 创建/更新应用实例
 * @param {obejct} payload - 应用实例列表选中行
 */
export async function saveInstance(payload) {
  const { applicationId } = payload;
  return request(
    organizationRoleLevel
      ? `${HZERO_HITF}/v1/${organizationId}/application-insts/${applicationId}`
      : `${HZERO_HITF}/v1/application-insts/${applicationId}`,
    {
      method: 'POST',
      body: payload,
    }
  );
}

/**
 * 刷新应用实例
 * @param {number} applicationInstId - 实例id
 */
export async function refreshInstance(applicationInstId) {
  return request(
    organizationRoleLevel
      ? `${HZERO_HITF}/v1/${organizationId}/application-insts/${applicationInstId}/sync-param`
      : `${HZERO_HITF}/v1/application-insts/${applicationInstId}/sync-param`
  );
}

/**
 * 查询映射类
 * @param {object} payload - 参数
 */
export async function queryMappingClass(params = {}) {
  return request(
    organizationRoleLevel
      ? `${HZERO_HITF}/v1/${organizationId}/application-insts/param-template`
      : `${HZERO_HITF}/v1/application-insts/param-template`,
    {
      query: params,
    }
  );
}

/**
 * 测试映射类
 * @param {number} applicationInstId - 实例id
 * @param {string} template - 代码
 */
export async function testMappingClass(payload) {
  return request(
    organizationRoleLevel
      ? `${HZERO_HITF}/v1/${organizationId}/application-insts/param-template`
      : `${HZERO_HITF}/v1/application-insts/param-template`,
    {
      method: 'POST',
      body: payload,
    }
  );
}

/**
 * 查询应用小类
 * @param {string} params - 请求参数
 */
export async function fetchMinorCategory(params) {
  return request(`${HZERO_PLATFORM}/v1/lovs/value/parent-value`, {
    method: 'GET',
    query: params,
  });
}
