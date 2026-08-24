import request from 'utils/request';
import { HZERO_PLATFORM } from 'utils/config';
import { isTenantRoleLevel, getCurrentOrganizationId, parseParameters } from 'utils/utils';

// 地区定义角色层级判断
function platformApi() {
  return isTenantRoleLevel() ? `${getCurrentOrganizationId()}/` : '';
}

/**
 * 查询水平结构的地区
 * @param {number} countryId - 国家id
 * @param {object} query - 查询参数
 * @param {string} query.condition - 地区编码/名称
 * @param {page} query.page - 分页信息
 */
export function regionQueryLine(countryId, query) {
  return request(`${HZERO_PLATFORM}/v1/${platformApi()}countries/${countryId}/regions/list`, {
    method: 'GET',
    query: parseParameters(query),
  });
}

/**
 * 懒加载查询树结构地区
 * @param {number} countryId - 国家id
 * @param {string} regionId - 地区Id
 */
export function regionQueryLazyTree(countryId, regionId) {
  return request(`${HZERO_PLATFORM}/v1/${platformApi()}countries/${countryId}/regions/lazy-tree`, {
    method: 'GET',
    query: {
      regionId,
    },
  });
}

/**
 * 更新地区信息
 * @param {number} countryId - 国家id
 * @param {string} regionId - 地区Id
 * @param {object} body - 地区
 */
export function regionUpdate(countryId, regionId, body) {
  return request(
    `${HZERO_PLATFORM}/v1/${platformApi()}countries/${countryId}/regions/${regionId}`,
    {
      method: 'PUT',
      body,
    }
  );
}

/**
 * 新增地区信息
 * @param {number} countryId - 国家id
 * @param {object} body - 地区
 */
export function regionCreate(countryId, body) {
  return request(`${HZERO_PLATFORM}/v1/${platformApi()}countries/${countryId}/regions`, {
    method: 'POST',
    body,
  });
}

/**
 * 启用 地区
 * @param {string} regionId - 地区Id
 * @param {object} body - 地区
 */
export function regionEnable(regionId, body) {
  return request(`${HZERO_PLATFORM}/v1/${platformApi()}countries/regions/${regionId}`, {
    method: 'PUT',
    body,
  });
}

/**
 * 禁用 地区
 * @param {string} regionId - 地区Id
 * @param {object} body - 地区
 */
export function regionDisable(regionId, body) {
  return request(`${HZERO_PLATFORM}/v1/${platformApi()}countries/regions/${regionId}`, {
    method: 'PUT',
    body,
  });
}

/**
 * 查询指定地区的详情
 * @param {string} regionId - 地区Id
 */
export function regionQueryDetail(regionId) {
  return request(`${HZERO_PLATFORM}/v1/${platformApi()}countries/regions/${regionId}`, {
    method: 'GET',
  });
}
