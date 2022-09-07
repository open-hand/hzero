import request from 'utils/request';
import { HZERO_IAM, HZERO_PLATFORM } from 'utils/config';
import { parseParameters, filterNullValueObject, isTenantRoleLevel } from 'utils/utils';

const organizationRoleLevel = isTenantRoleLevel();

/**
 *获取数据
 * @async
 * @function queryTenant
 * @param {String} params.tenantNum 组织编码
 * @param {String} params.tenantName 组织名称
 * @param {Number} [params.page = 0] - 数据页码
 * @param {Number} [params.size = 10] - 分页大小
 * @returns {Obejct}  fetch Promise
 */
export async function queryTenant(params) {
  const param = filterNullValueObject(parseParameters(params));
  return request(`${HZERO_IAM}/v1/tenants/having-custom-menu`, {
    method: 'GET',
    query: param,
  });
}

/**
 * 获取树形菜单
 * @param {*} [params={}]
 */
export async function queryMenuTree(params = {}) {
  const { tenantId } = params;
  return request(`${HZERO_IAM}/v1/tenant-menus/${tenantId}`, {
    method: 'GET',
    query: params,
  });
}

export async function queryLabelList(params = {}) {
  const { tenantId, ...others } = params;
  return request(
    organizationRoleLevel
      ? `${HZERO_IAM}/v1/${tenantId}/labels/by-type`
      : `${HZERO_IAM}/v1/labels/by-type`,
    {
      query: others,
      method: 'GET',
    }
  );
}

export async function queryMenuLabel(params) {
  const { tenantId, payload } = params;
  return request(
    organizationRoleLevel
      ? `${HZERO_IAM}/v1/${tenantId}/label-rels/MENU/${payload}`
      : `${HZERO_IAM}/v1/label-rels/MENU/${payload}`,
    {
      method: 'GET',
    }
  );
}

/**
 * 查询值集
 * @async
 * @function queryCode
 * @param {object} params - 查询条件
 * @param {!string} param.lovCode - 查询条件
 * @returns {object} fetch Promise
 */
export async function queryCode(params = {}) {
  return request(`${HZERO_PLATFORM}/v1/lovs/value`, {
    query: params,
  });
}

export async function queryDir(params = {}) {
  return request(`${HZERO_IAM}/hzero/v1/menus/dir`, {
    query: params,
  });
}

export async function queryPermissions(params = {}) {
  return request(`${HZERO_IAM}/hzero/v1/permissions/self`, {
    query: params,
  });
}

// 查询权限集
export async function queryPermissionSetTree(menuId, params = {}) {
  return request(`${HZERO_IAM}/hzero/v1/menus/${menuId}/permission-set`, {
    query: params,
  });
}

// 查询权限集下已分配的权限
export async function queryPermissionsByMenuIdAll(menuId, params = {}) {
  return request(`${HZERO_IAM}/hzero/v1/menus/permission-set/${menuId}/permissions`, {
    query: params,
  });
}

// 查询权限集下已分配的Lov
export async function queryLovsByMenuIdAll(menuId, params = {}) {
  return request(`${HZERO_IAM}/hzero/v1/menus/permission-set/${menuId}/lovs`, {
    query: params,
  });
}

export async function checkMenuDirExists(params = {}) {
  return request(`${HZERO_IAM}/hzero/v1/menus/check-duplicate`, {
    method: 'POST',
    body: params,
  });
}

// 获取当前复制的节点及其子节点
export async function queryCopyMenuList(params = {}) {
  return request(`${HZERO_IAM}/hzero/v1/menus/copy`, {
    query: params,
  });
}

// 复制并创建菜单
export async function copyMenu(payload) {
  return request(`${HZERO_IAM}/v1/tenant-menus/copy`, {
    method: 'POST',
    body: payload,
  });
}

// 复制并创建菜单
export async function createMenu(payload) {
  const { tenantId, menu } = payload;
  return request(`${HZERO_IAM}/v1/tenant-menus/add?tenantId=${tenantId}`, {
    method: 'POST',
    body: menu,
  });
}

// 复制并创建菜单
export async function saveMenu(payload) {
  const { tenantId, menu } = payload;
  return request(`${HZERO_IAM}/v1/tenant-menus?tenantId=${tenantId}`, {
    method: 'PUT',
    body: menu,
  });
}

export async function enable(params) {
  const { tenantId, ...rest } = params;
  return request(`${HZERO_IAM}/v1/tenant-menus/enable?tenantId=${tenantId}`, {
    method: 'PUT',
    body: rest,
  });
}

export async function disable(params) {
  const { tenantId, ...rest } = params;
  return request(`${HZERO_IAM}/v1/tenant-menus/disable?tenantId=${tenantId}`, {
    method: 'PUT',
    body: rest,
  });
}

// 查询权限集下可分配的所有权限
export async function queryPermissionsByMenuId(menuId, params = {}) {
  const { tenantId, ...rest } = params;
  return request(
    `${HZERO_IAM}/v1/tenant-menus/${menuId}/assignable-permissions?tenantId=${tenantId}`,
    {
      query: rest,
    }
  );
}

// 查询权限集下可分配的所有lov
export async function queryLovByMenuId(menuId, params = {}) {
  return request(`${HZERO_IAM}/v1/tenant-menus/${menuId}/assignable-lovs`, {
    query: params,
  });
}
