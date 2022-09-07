/**
 * service: 二级域名单点登录配置
 * @date: 2019-6-27
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */

import request from 'utils/request';
import { HZERO_IAM, HZERO_PLATFORM } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';

const organizationId = getCurrentOrganizationId();

/**
 * 查询列表
 * @async
 * @function queryConfig
 * @param {Object} params - 查询参数
 */
export async function queryConfig(params) {
  return request(`${HZERO_IAM}/v1/${isTenantRoleLevel() ? `${organizationId}/` : ''}domains`, {
    method: 'GET',
    query: params,
  });
}
/**
 * 创建配置
 * @async
 * @function createConfig
 * @param {Object} params - 查询参数
 */
export async function createConfig(params) {
  return request(`${HZERO_IAM}/v1/${isTenantRoleLevel() ? `${organizationId}/` : ''}domains`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 删除配置
 * @async
 * @function deleteConfig
 * @param {Object} params - 查询参数
 */
export async function deleteConfig(params) {
  return request(`${HZERO_IAM}/v1/${isTenantRoleLevel() ? `${organizationId}/` : ''}domains`, {
    method: 'DELETE',
    body: params,
  });
}

/**
 * 修改配置
 * @async
 * @function editConfig
 * @param {Object} params - 查询参数
 */
export async function editConfig(params) {
  return request(`${HZERO_IAM}/v1/${isTenantRoleLevel() ? `${organizationId}/` : ''}domains`, {
    method: 'PUT',
    body: params,
  });
}

/**
 * 获取配置明细
 * @async
 * @function getConfigDetail
 * @param {Object} params - 查询参数
 */
export async function getConfigDetail(params) {
  const { domainId } = params;
  return request(
    `${HZERO_IAM}/v1/${isTenantRoleLevel() ? `${organizationId}/` : ''}domains/${domainId}`,
    {
      method: 'GET',
    }
  );
}

/**
 * 获取模板配置提示信息详情
 */
export async function fetchMessageDetail(params) {
  return request(
    isTenantRoleLevel()
      ? `${HZERO_PLATFORM}/v1/${organizationId}/static-texts/text-code`
      : `${HZERO_PLATFORM}/v1/static-texts/text-code`,
    {
      method: 'GET',
      query: params,
    }
  );
}

/**
 * 查询分配公司/租户列表
 * @async
 * @function queryConfig
 * @param {Object} params - 查询参数
 */
export async function fetchDistribute(paramsValue) {
  const { domainId, params } = paramsValue;
  return request(
    `${HZERO_IAM}/v1/${isTenantRoleLevel() ? `${organizationId}/` : ''}domain-assigns/${domainId}`,
    {
      method: 'GET',
      query: params,
    }
  );
}

/**
 * 保存分配租户/公司
 * @async
 * @function createConfig
 * @param {Object} params - 查询参数
 */
export async function saveDistribute(params) {
  const { domainId, body } = params;
  return request(
    `${HZERO_IAM}/v1/${isTenantRoleLevel() ? `${organizationId}/` : ''}domain-assigns/${domainId}`,
    {
      method: 'POST',
      body,
    }
  );
}
/**
 * 删除分配租户/公司
 * @async
 * @function createConfig
 * @param {Object} params - 查询参数
 */
export async function deleteDistribute(params) {
  const { domainId, body } = params;
  return request(
    `${HZERO_IAM}/v1/${isTenantRoleLevel() ? `${organizationId}/` : ''}domain-assigns/${domainId}`,
    {
      method: 'DELETE',
      body,
    }
  );
}
