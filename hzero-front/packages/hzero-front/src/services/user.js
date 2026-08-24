import request from 'utils/request';
import { getEnvConfig } from 'utils/iocUtils';

const { AUTH_SELF_URL, HZERO_IAM, HZERO_PLATFORM } = getEnvConfig();

export async function query() {
  return request('/api/users');
}

// export async function queryCurrent() {
//   return request('/api/currentUser');
// }

export async function queryCurrent() {
  return request(AUTH_SELF_URL);
}

/**
 * @async
 * @function queryTenant - 查询用户租户列表数据
 */
export async function queryTenant() {
  return request(`${HZERO_IAM}/hzero/v1/users/self-tenants`, {
    method: 'GET',
  });
}

/**
 * 组织层查询当前登录用户默认角色
 * @async
 * @function queryDefaultRole
 * @param {!number} organizationId - 组织ID
 * @returns {object} fetch Promise
 */
export async function queryDefaultRole() {
  return request(`${HZERO_IAM}/hzero/v1/member-roles/current-role`);
}

/**
 * 组织层查询分配给当前登录用户的角色
 * @async
 * @function queryRoleList
 * @param {!number} organizationId - 组织ID
 * @returns {object} fetch Promise
 */
export async function queryRoleList() {
  return request(`${HZERO_IAM}/hzero/v1/member-roles/self-roles`);
}

/**
 * 缓存当前用户的角色
 * @async
 * @function updateCurrentRole
 * @param {!number} roleId
 * @returns {object} fetch Promise
 */
export async function updateCurrentRole(roleId) {
  return request(`${HZERO_IAM}/v1/users/roles`, {
    method: 'PUT',
    query: {
      roleId,
    },
  });
}

/**
 * 缓存当前用户的租户
 * @async
 * @function updateCurrentRole
 * @param {!number} roleId
 * @returns {object} fetch Promise
 */
export async function updateCurrentTenant(tenantId) {
  return request(`${HZERO_IAM}/v1/users/tenant-id`, {
    method: 'PUT',
    query: {
      tenantId,
    },
  });
}

/**
 * 更新默认租户
 * @async
 * @function updateDefaultTenant
 * @param {!number} tenantId
 * @returns {object} fetch Promise
 */
export async function updateDefaultTenant(tenantId) {
  return request(`${HZERO_IAM}/hzero/v1/users/default-tenant`, {
    method: 'PUT',
    query: {
      tenantId,
    },
  });
}

/**
 * @async
 * @function queryTenant - 查询列表数据
 */
export async function fetchDataHierarchiesList(params) {
  return request(`${HZERO_PLATFORM}/v1/${params.organizationId}/data-hierarchies/switch/value`, {
    method: 'GET',
  });
}

/**
 * @async
 * @function queryTenant - 查询列表数据
 */
export async function fetchDataHierarchiesSelectList(params) {
  const { organizationId, ...others } = params;
  return request(`${HZERO_PLATFORM}/v1/${organizationId}/data-hierarchies/switch/value/details`, {
    method: 'GET',
    query: others,
  });
}

/**
 * @async
 * @function queryTenant - 查询用户租户列表数据
 */
export async function switchDataHierarchies(params = {}) {
  const {
    dataHierarchyCode,
    dataHierarchyValue = '',
    dataHierarchyMeaning = '',
    organizationId,
  } = params;
  return request(`${HZERO_PLATFORM}/v1/${organizationId}/data-hierarchies/switch`, {
    method: 'PUT',
    query: { dataHierarchyCode, dataHierarchyValue, dataHierarchyMeaning },
  });
}

/**
 * @async
 * @function queryTenant - 查询用户租户列表数据
 */
export async function queryDataHierarchies(params = {}) {
  const { organizationId } = params;
  return request(
    `${HZERO_PLATFORM}/v1/${organizationId}/data-hierarchies/switch/display-style/value`,
    {
      method: 'GET',
    }
  );
}
