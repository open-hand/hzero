import request from 'utils/request';
import { HZERO_PLATFORM } from 'utils/config';
import { isTenantRoleLevel } from 'utils/utils';

// 国家定义角色层级判断
function countryApi(params) {
  return isTenantRoleLevel() ? `${params.tenantId}/countries` : 'countries';
}

/**
 * 查询国家定义列表的数据
 * @param {Object} params - 查询参数
 * @param {String} params.page - 页码
 * @param {String} params.size - 页数
 */
export async function fetchCountryList(params) {
  return request(`${HZERO_PLATFORM}/v1/${countryApi(params)}`, {
    method: 'GET',
    query: params,
  });
}

/**
 * 新增国家
 * @param {Object} params - 查询参数
 * @param {String} params.todoContent - todoItem 的内容
 * @param {String} params.status - todoItem 的状态
 */
export async function createCountry(params) {
  return request(
    `${HZERO_PLATFORM}/v1/${isTenantRoleLevel() ? `${params.tenantId}/country` : 'country'}`,
    {
      method: 'POST',
      body: params,
    }
  );
}

/**
 * 更新国家数据
 * @param {Object} params - 查询参数
 * @param {String} params.page - 页码
 * @param {String} params.size - 页数
 */
export async function updateCountry(params) {
  return request(`${HZERO_PLATFORM}/v1/${countryApi(params)}/${params.countryId}`, {
    method: 'PUT',
    body: params,
  });
}

/**
 * 删除国家定义行数据
 * @param {Object} params - 参数
 * @param {String} params.todoContent - todoItem 的内容
 * @param {String} params.status - todoItem 的状态
 */
export async function deleteCountry(params) {
  return request(`${HZERO_PLATFORM}/v1/${countryApi(params)}`, {
    method: 'DELETE',
    body: params.seleCountryRows,
  });
}
