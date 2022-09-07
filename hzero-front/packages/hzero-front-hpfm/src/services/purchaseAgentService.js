/**
 * service 采购员
 * @date: 2018-7-10
 * @version: 0.0.1
 * @author:  geekrainy <chao.zheng02@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import request from 'utils/request';
import { HZERO_PLATFORM } from 'utils/config';
import { parseParameters, getCurrentOrganizationId } from 'utils/utils';

const organizationId = getCurrentOrganizationId();

export async function queryPurchaseAgent(params) {
  const query = parseParameters(params);
  return request(`${HZERO_PLATFORM}/v1/${params.tenantId}/purchases/agents`, {
    method: 'GET',
    query,
  });
}

/**
 * 新增或更新采购员
 * @param {Object} params 新增或更新采购员参数
 */
export async function savePurchaseAgent(params) {
  return request(`${HZERO_PLATFORM}/v1/${params.tenantId}/purchases/agents/batch-save`, {
    method: 'POST',
    body: params.list,
  });
}

/**
 * 获取用户列表
 * @param {Object} params 查询参数
 */
export async function fetchUserList(params = {}) {
  const query = parseParameters(params);
  return request(
    `${HZERO_PLATFORM}/v1/${organizationId}/purchases/agents/${params.purchaseAgentId}/assign-user`,
    {
      method: 'GET',
      query,
    }
  );
}

/**
 * 新建用户列表数据
 * @param {Object} params 查询参数
 */
export async function updateUser(params = {}) {
  return request(`${HZERO_PLATFORM}/v1/${organizationId}/purchases/agents/assign-user`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 删除用户列表数据
 * @param {Object} params 查询参数
 */
export async function deleteUser(params = {}) {
  return request(`${HZERO_PLATFORM}/v1/${organizationId}/purchases/agents/assign-user`, {
    method: 'DELETE',
    body: params,
  });
}
