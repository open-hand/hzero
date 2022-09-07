import request from 'utils/request';
import { HZERO_PLATFORM } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';

/**
 * 查询公司信息
 * @param {Object} params - 查询参数
 * @param {String} params.organizationId - 组织ID
 */
export async function fetchCompany(params) {
  const { organizationId, ...other } = params;
  return request(`${HZERO_PLATFORM}/v1/${organizationId}/companies`, {
    method: 'GET',
    query: other,
  });
}

/**
 * 设置公司启用
 * @param {Object} params - 查询参数
 * @param {String} params.organizationId - 组织ID
 */
export async function enableCompany(params) {
  const organizationId = getCurrentOrganizationId();
  return request(`${HZERO_PLATFORM}/v1/${organizationId}/companies/enable`, {
    method: 'POST',
    body: params.body,
  });
}

/**
 * 设置公司禁用
 * @param {Object} params - 查询参数
 * @param {String} params.organizationId - 组织ID
 */
export async function disableCompany(params) {
  const organizationId = getCurrentOrganizationId();
  return request(`${HZERO_PLATFORM}/v1/${organizationId}/companies/disable`, {
    method: 'POST',
    body: params.body,
  });
}

/**
 * 查询单条公司信息
 * @param {Object} params - 查询参数
 * @param {String} params.organizationId - 租户ID
 * @param {String} params.companyId - 公司ID
 */
export async function queryCompany(params) {
  return request(`${HZERO_PLATFORM}/v1/${params.organizationId}/companies/${params.companyId}`, {
    method: 'GET',
  });
}

/**
 * 新建公司
 * @param {Object} params - 查询参数
 */
export async function createCompany(params) {
  return request(`${HZERO_PLATFORM}/v1/${params.tenantId}/companies`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 查询国家地区.
 * @param {Number} countryId 国家ID
 * @export
 */
export async function queryProvinceCity(countryId) {
  const organizationId = getCurrentOrganizationId();
  return request(`${HZERO_PLATFORM}/v1/${organizationId}/countries/${countryId}/regions`, {
    method: 'GET',
    query: {
      enabledFlag: 1,
    },
  });
}

/**
 * 查询国家定义列表的数据
 * @param {Object} params - 查询参数
 * @param {String} params.page - 页码
 * @param {String} params.size - 页数
 */
export async function fetchCountryList() {
  const organizationId = getCurrentOrganizationId();
  return request(
    `${HZERO_PLATFORM}/v1${isTenantRoleLevel() ? `/${organizationId}` : ``}/countries/all`,
    {
      method: 'GET',
      query: {
        enabledFlag: 1,
      },
    }
  );
}
