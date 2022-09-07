/**
 * service 采购组织
 * @date: 2018-7-9
 * @version: 0.0.1
 * @author:  王家程 <jiacheng.wang@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import request from 'utils/request';
import { HZERO_PLATFORM } from 'utils/config';

/**
 * 查询采购组织
 * @param {Object} params - 查询参数
 * @param {String} params.organizationId - 租户ID
 * @param {String} params.page - 页码
 * @param {String} params.size - 页数
 */
export async function fetchPurchaseOrgList(params) {
  const { organizationId, ...other } = params;
  return request(`${HZERO_PLATFORM}/v1/${organizationId}/purchases/organizations`, {
    method: 'GET',
    query: other,
  });
}

/**
 * 批量新增编辑采购组织
 * @param {Object} params - 参数
 * @param {String} params.organizationId - 租户ID
 * @param {Array} params.purchaseOrgList - 数据列表
 * @param {String} params.enabledFlag - 启用标识
 * @param {String} params.organizationCode - 采购组织代码
 * @param {String} params.organizationName - 采购组织名称
 * @param {String} params.purchaseOrgId - 采购组织ID
 * @param {String} params.sourceCode - 数据来源代码
 */
export async function savePurchaseOrg(params) {
  return request(
    `${HZERO_PLATFORM}/v1/${params.organizationId}/purchases/organizations/batch-save`,
    {
      method: 'POST',
      body: params.purchaseOrgList,
    }
  );
}
