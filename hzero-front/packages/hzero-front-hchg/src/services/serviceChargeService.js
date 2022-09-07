/**
 * service - 服务计费配置
 * @date: 2019/8/28
 * @author: hulingfangzi <lingfangzi.hu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import request from 'utils/request';
import { HZERO_CHG } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel, parseParameters } from 'utils/utils';

const organizationId = getCurrentOrganizationId();
const organizationRoleLevel = isTenantRoleLevel();

/**
 * 查询数据审计列表
 * @param {object} params - 请求参数
 */
export async function queryList(params = {}) {
  return request(
    organizationRoleLevel
      ? `${HZERO_CHG}/v1/${organizationId}/charge-groups`
      : `${HZERO_CHG}/v1/charge-groups`,
    {
      query: parseParameters(params),
    }
  );
}

/**
 * 发布服务计费组
 * @param {object} payload - 表格行数据
 */
export async function publishGroup(payload) {
  return request(
    organizationRoleLevel
      ? `${HZERO_CHG}/v1/${organizationId}/charge-groups/publish`
      : `${HZERO_CHG}/v1/charge-groups/publish`,
    {
      method: 'PUT',
      body: payload,
    }
  );
}

/**
 * 取消发布服务计费组
 * @param {object} payload - 表格行数据
 */
export async function cancelGroup(payload) {
  return request(
    organizationRoleLevel
      ? `${HZERO_CHG}/v1/${organizationId}/charge-groups/cancel`
      : `${HZERO_CHG}/v1/charge-groups/cancel`,
    {
      method: 'PUT',
      body: payload,
    }
  );
}

/**
 * 查询计费组详情
 * @param {number} chargeGroupId - 计费组ID
 */
export async function queryGroupDetail({ chargeGroupId }) {
  return request(
    organizationRoleLevel
      ? `${HZERO_CHG}/v1/${organizationId}/charge-groups/${chargeGroupId}`
      : `${HZERO_CHG}/v1/charge-groups/${chargeGroupId}`
  );
}

/**
 * 查询计费规则列表
 * @param {object} params - 请求参数
 */
export async function queryChargeRule(params = {}) {
  return request(
    organizationRoleLevel
      ? `${HZERO_CHG}/v1/${organizationId}/charge-rules`
      : `${HZERO_CHG}/v1/charge-rules`,
    {
      query: parseParameters(params),
    }
  );
}

/**
 * 创建服务计费组
 * @param {object} payload - 表单数据
 */
export async function createGroup(payload) {
  return request(
    organizationRoleLevel
      ? `${HZERO_CHG}/v1/${organizationId}/charge-groups`
      : `${HZERO_CHG}/v1/charge-groups/`,
    {
      method: 'POST',
      body: payload,
    }
  );
}

/**
 * 修改服务计费组
 * @param {object} payload - 表单数据
 */
export async function updateGroup(payload) {
  return request(
    organizationRoleLevel
      ? `${HZERO_CHG}/v1/${organizationId}/charge-groups`
      : `${HZERO_CHG}/v1/charge-groups`,
    {
      method: 'PUT',
      body: payload,
    }
  );
}

/**
 * 删除计费规则
 * @param {object} payload - 选中行
 */
export async function deleteChargeRule(payload) {
  return request(
    organizationRoleLevel
      ? `${HZERO_CHG}/v1/${organizationId}/charge-rules`
      : `${HZERO_CHG}/v1/charge-rules`,
    {
      method: 'DELETE',
      body: payload,
    }
  );
}

/**
 * 查询计费规则详情
 * @param {number} chargeRuleId - 计费规则ID
 */
export async function queryRuleDetail(chargeRuleId) {
  return request(
    organizationRoleLevel
      ? `${HZERO_CHG}/v1/${organizationId}/charge-rules/${chargeRuleId}`
      : `${HZERO_CHG}/v1/charge-rules/${chargeRuleId}`
  );
}

/**
 * 创建规则行
 * @param {object} payload - 表单数据
 */
export async function createChargeRule(payload) {
  return request(
    organizationRoleLevel
      ? `${HZERO_CHG}/v1/${organizationId}/charge-rules`
      : `${HZERO_CHG}/v1/charge-rules`,
    {
      method: 'POST',
      body: payload,
    }
  );
}

/**
 * 修改规则行
 * @param {object} payload - 表单数据
 */
export async function updateChargeRule(payload) {
  return request(
    organizationRoleLevel
      ? `${HZERO_CHG}/v1/${organizationId}/charge-rules`
      : `${HZERO_CHG}/v1/charge-rules`,
    {
      method: 'PUT',
      body: payload,
    }
  );
}

/**
 * 查询服务计费范围列表
 * @param {*} params - 请求参数
 */
export async function queryChargeScope(params = {}) {
  return request(
    organizationRoleLevel
      ? `${HZERO_CHG}/v1/${organizationId}/charge-scopes`
      : `${HZERO_CHG}/v1/charge-scopes`,
    {
      query: parseParameters(params),
    }
  );
}

/**
 * 创建服务计费范围
 * @param {object} payload - 范围行
 */
export async function createChargeScope(payload) {
  return request(
    organizationRoleLevel
      ? `${HZERO_CHG}/v1/${organizationId}/charge-scopes`
      : `${HZERO_CHG}/v1/charge-scopes`,
    {
      method: 'POST',
      body: payload,
    }
  );
}

/**
 * 删除服务计费范围
 * @param {object} payload - 范围行
 */
export async function deleteChargeScope(payload) {
  return request(
    organizationRoleLevel
      ? `${HZERO_CHG}/v1/${organizationId}/charge-scopes`
      : `${HZERO_CHG}/v1/charge-scopes`,
    {
      method: 'DELETE',
      body: payload,
    }
  );
}
