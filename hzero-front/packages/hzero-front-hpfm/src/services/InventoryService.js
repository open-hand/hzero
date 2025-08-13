/**
 * service 库存组织
 * @date: 2018-7-5
 * @version: 0.0.1
 * @author:  邓婷敏 <tingmin.deng@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import request from 'utils/request';
import { HZERO_PLATFORM } from 'utils/config';
import { parseParameters } from 'utils/utils';

/**
 * 查询 查询库存组织内容
 * @async
 * @function queryInventoryData -函数名称
 * @param {Object} params - 接口参数
 * @param {String} params.getOrganizationId -租户ID(tenantId)
 * @param {!number} [params.page = 0] -数据页码
 * @param {!number} [params.size = 10] -分页条数
 * @returns {object} fetch Promise
 */
export async function queryInventoryData(params) {
  const param = parseParameters(params.body);
  return request(`${HZERO_PLATFORM}/v1/${params.organizationId}/inv-organizations`, {
    method: 'GET',
    query: param,
  });
}
/**
 * 新增 库存组织(单条新增)
 * @async
 * @function insertInventoryData -函数名称
 * @param {Object} params -新增参数
 * @param {String} params.getOrganizationId - 租户ID(tenantId)
 * @param {String} params.enabledFlag - 启用标识
 * @param {String} params.organizationCode - 库房组织编码
 * @param {String} params.organizationName - 库房组织名称
 * @param {String} params.ouName - 业务实体名称
 * @param {String} params.ouId -业务实体ID
 * @param {String} params.sourceCode -数据来源
 * @returns {object} fetch Promise
 */
export async function insertInventoryData(params) {
  return request(`${HZERO_PLATFORM}/v1/${params.getOrganizationId}/inv-organizations`, {
    method: 'POST',
    body: params.body,
  });
}
/**
 * 更新 库存组织(单条更新)
 * @async
 * @function updateInventoryData -函数名称
 * @param {Object} params - 更新参数
 * @param {String} params.enabledFlag - 启用标识
 * @param {String} params.getOrganizationId - 租户ID(tenantId)
 * @param {String} params.organizationCode - 库房组织编码
 * @param {String} params.organizationName - 库房组织名称
 * @param {String} params.ouName - 业务实体名称
 * @param {String} params.ouId -业务实体ID
 * @param {String} params.sourceCode -数据来源
 * @returns {object} fetch Promise
 */
export async function updateInventoryData(params) {
  return request(`${HZERO_PLATFORM}/v1/${params.getOrganizationId}/inv-organizations`, {
    method: 'PUT',
    body: params.body,
  });
}

/**
 * 批量修改/新增 库存组织
 * @function updateAllInventoryData
 * @param {Object} params - 新增参数
 * @param {String} params.enabledFlag - 启用标识
 * @param {String} params.tenantId - 租户ID(tenantId)
 * @param {String} params.organizationCode - 库房组织编码
 * @param {String} params.organizationName - 库房组织名称
 * @param {String} params.ouName - 业务实体名称
 * @param {String} params.ouId -业务实体ID
 * @param {String} params.sourceCode -数据来源
 * @returns {object} fetch Promise
 */
export async function updateAllInventoryData(params) {
  const { body, organizationId } = params;
  return request(`${HZERO_PLATFORM}/v1/${organizationId}/inv-organizations/batch-save`, {
    method: 'POST',
    body,
  });
}
/**
 * 禁用 库存组织
 * @function disableInventory
 * @param {Object} params - 新增参数
 * @param {Object} params.getOrganizationId --租户ID(tenantId)
 * @param {Object} params.invOrganizationId --库存组织ID
 * @returns {object} fetch Promise
 */
export async function disableInventory(params) {
  return request(
    `${HZERO_PLATFORM}/v1/${params.getOrganizationId}/inv-organizations/${params.invOrganizationId}/disable`,
    {
      method: 'PATCH',
      body: params.body,
    }
  );
}
/**
 * 启用 库存组织
 * @param {Object} params - 新增参数
 * @param {Object} params.getOrganizationId --租户ID
 * @param {Object} params.invOrganizationId --库存组织ID
 * @returns {object} fetch Promise
 */
export async function enableInventory(params) {
  return request(
    `${HZERO_PLATFORM}/v1/${params.getOrganizationId}/inv-organizations/${params.invOrganizationId}/enable`,
    {
      method: 'PATCH',
      body: params.body,
    }
  );
}
