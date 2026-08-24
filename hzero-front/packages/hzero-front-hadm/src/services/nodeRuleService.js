import request from 'utils/request';
import { HZERO_ADM, HZERO_IAM } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';

const organizationId = getCurrentOrganizationId();
const apiPrefix = !isTenantRoleLevel()
  ? `${HZERO_ADM}/v1/node-rules`
  : `${HZERO_ADM}/v1/${organizationId}/node-rules`;

/**
 * 查询列表数据
 * @async
 * @function fetchPortalAssign
 * @param {Object} params - 查询参数
 * @param {String} [params.page = 0] - 页码
 * @param {String} [params.size = 0] - 页数
 */
export async function fetchNodeRule(params) {
  return request(`${apiPrefix}`, {
    method: 'GET',
    query: params,
  });
}

/**
 * 查询启用状态下的列表数据
 * @async
 * @function fetchPortalAssign
 * @param {Object} params - 查询参数
 * @param {String} [params.page = 0] - 页码
 * @param {String} [params.size = 0] - 页数
 */
export async function fetchNodeRuleEnabled(params) {
  return request(`${apiPrefix}/enabled-rules`, {
    method: 'GET',
    query: params,
  });
}

/**
 * 查询详细数据
 * @async
 * @function fetchNodeRuleDetails
 * @param {Object} params - 查询参数
 */
export async function fetchNodeRuleDetails(params) {
  return request(`${apiPrefix}/${params.nodeRuleId}`, {
    method: 'GET',
    query: params,
  });
}

/**
 * 查询租户
 * @async
 * @function fetchTenantList
 * @param {Object} params - 查询参数
 */
export async function fetchTenantList(params) {
  return request(`${apiPrefix}/${params.nodeRuleId}/tenants`, {
    method: 'GET',
  });
}

/**
 * 创建
 * @async
 * @function createJobInfo
 * @param {String} params.enabledFlag - 是否启用
 * @param {String} params.groupNum - 集团编码
 */
export async function createNodeRule(params) {
  return request(`${apiPrefix}`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 更新
 * @async
 * @function updateJobGlue
 * @param {Object} params - 查询参数
 */
export async function updateNodeRule(params) {
  return request(`${apiPrefix}`, {
    method: 'PUT',
    body: params,
  });
}

/**
 * 启用
 * @async
 * @function enabledNodeRule
 * @param {Object} params - 查询参数
 */
export async function enabledNodeRule(params) {
  return request(`${apiPrefix}/enabled`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 禁用
 * @async
 * @function disabledNodeRule
 * @param {Object} params - 查询参数
 * @param {string} params.nodeRuleId - 节点组规则ID
 */
export async function disabledNodeRule(params) {
  return request(`${apiPrefix}/disabled`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 删除租户
 * @async
 * @function deleteTenant
 */
export async function deleteTenant(params) {
  return request(`${apiPrefix}/tenants`, {
    method: 'DELETE',
    body: params,
  });
}

/**
 * 删除节点组规则
 * @async
 * @function deleteTenant
 * @param {String} params.nodeRuleId - 节点组规则ID
 */
export async function deleteNodeRule(params) {
  return request(`${apiPrefix}`, {
    method: 'DELETE',
    body: params,
  });
}

/**
 * 查询租户下 Lov - lov用
 * @async
 * @function fetchTenantLovList
 * @param {Object} params - 查询参数
 */
export async function fetchTenantLovList(params) {
  return request(
    isTenantRoleLevel() ? `${HZERO_IAM}/v1/${organizationId}/tenants` : `${HZERO_IAM}/v1/tenants`,
    {
      method: 'GET',
      query: params,
    }
  );
}

/**
 * 查询租户下的用户
 * @async
 * @function fetchUserList
 * @param {Object} params - 查询参数
 */
export async function fetchUserList(params) {
  return request(
    isTenantRoleLevel()
      ? `${HZERO_IAM}/hzero/v1/${organizationId}/users/paging`
      : `${HZERO_IAM}/hzero/v1/users/paging`,
    {
      method: 'GET',
      query: params,
    }
  );
}

/**
 * 查询租户下的url
 * @async
 * @function fetchUrlList
 * @param {Object} params - 查询参数
 */
export async function fetchUrlList(params) {
  return request(
    isTenantRoleLevel()
      ? `${HZERO_IAM}/hzero/v1/${organizationId}/permissions`
      : `${HZERO_IAM}/hzero/v1/permissions`,
    {
      method: 'GET',
      query: params,
    }
  );
}
