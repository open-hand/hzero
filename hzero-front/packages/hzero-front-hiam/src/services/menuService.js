import { stringify } from 'qs';
import { omit } from 'lodash';
import request from 'utils/request';
import { HZERO_IAM } from 'utils/config';

export async function queryMenuTree(params = {}) {
  return request(`${HZERO_IAM}/hzero/v1/menus/tree`, {
    query: params,
  });
}

export async function createMenuDir(params = {}) {
  return request(`${HZERO_IAM}/v1/menus`, {
    method: 'POST',
    body: params,
  });
}

export async function saveMenuDir(params = {}) {
  return request(`${HZERO_IAM}/v1/menus/${params.id}`, {
    method: 'POST',
    body: params,
  });
}

export async function deleteMenuTree(id) {
  return request(`${HZERO_IAM}/v1/menus/${id}`, {
    method: 'DELETE',
  });
}

export async function customMenuImport(organizationId, params) {
  return request(`${HZERO_IAM}/hzero/v1/${organizationId}/menus/custom-menu-import`, {
    method: 'POST',
    body: params,
  });
}

export async function queryMenuTreeByOrganizationId(organizationId, params) {
  return request(`${HZERO_IAM}/hzero/v1/${organizationId}/menus/tree`, {
    query: params,
  });
}

export async function createMenuDirByOrganizationId(params = {}) {
  const { organizationId } = params;
  return request(`${HZERO_IAM}/hzero/v1/${organizationId}/menus/create`, {
    method: 'POST',
    body: omit(params, 'organizationId'),
  });
}

export async function checkMenuDirExists(params = {}) {
  return request(`${HZERO_IAM}/v1/menus/check`, {
    method: 'POST',
    body: params,
  });
}

export async function saveMenuTree(params, level) {
  return request(`${HZERO_IAM}/v1/menus/tree?${stringify({ level })}`, {
    method: 'POST',
    body: params,
  });
}

export async function checkMenuDirExistsByOrganizationId(
  organizationId,
  customFlag = true,
  params = {}
) {
  return request(`${HZERO_IAM}/hzero/v1/${organizationId}/menus/check-duplicate`, {
    method: 'POST',
    body: params,
    query: {
      custom_flag: customFlag,
    },
  });
}

export async function saveMenuTreeByOrganizationId(
  organizationId,
  params,
  level,
  customFlag = true
) {
  return request(`${HZERO_IAM}/hzero/v1/${organizationId}/menus/tree`, {
    method: 'POST',
    body: params,
    query: {
      level,
      custom_flag: customFlag,
    },
  });
}
