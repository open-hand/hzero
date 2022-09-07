/**
 * authorityManagementService - 租户级权限维护 - service
 * @date: 2018-7-31
 * @author: lokya <kan.li01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import request from 'utils/request';
import { HZERO_IAM } from 'utils/config';
import { getCurrentOrganizationId, parseParameters } from 'utils/utils';

export async function fetchTabList(params) {
  const organizationId = getCurrentOrganizationId();
  return request(`${HZERO_IAM}/v1/${organizationId}/doc-type/dimension/role/${params.roleId}`, {
    method: 'GET',
  });
}
export async function queryCompany(params) {
  const organizationId = getCurrentOrganizationId();
  return request(`${HZERO_IAM}/v1/${organizationId}/role/${params.roleId}/authority-org`, {
    method: 'GET',
    query: params,
  });
}

export async function updateCompany(params) {
  const organizationId = getCurrentOrganizationId();
  return request(`${HZERO_IAM}/v1/${organizationId}/role/${params.roleId}/authority-org`, {
    method: 'POST',
    body: params.checkList,
  });
}

export async function queryData(params) {
  const organizationId = getCurrentOrganizationId();
  const param = parseParameters(params);
  return request(`${HZERO_IAM}/v1/${organizationId}/role/${param.roleId}/authority`, {
    method: 'GET',
    query: param,
  });
}

export async function saveData(params) {
  const organizationId = getCurrentOrganizationId();
  return request(
    `${HZERO_IAM}/v1/${organizationId}/role/${params.roleId}/authority?authorityTypeCode=${params.authorityTypeCode}`,
    {
      method: 'POST',
      body: params,
    }
  );
}
export async function deleteData(params) {
  const organizationId = getCurrentOrganizationId();
  return request(`${HZERO_IAM}/v1/${organizationId}/role/${params.roleId}/authority`, {
    method: 'DELETE',
    body: params.deleteRows,
  });
}

export async function changeAuthority(params) {
  const organizationId = getCurrentOrganizationId();
  return request(
    `${HZERO_IAM}/v1/${organizationId}/role/${params.roleId}/authority/exchange?exchanageroleId=${params.exchanageroleId}`,
    {
      method: 'POST',
      body: params,
    }
  );
}

export async function copyAuthority(params) {
  const organizationId = getCurrentOrganizationId();
  return request(`${HZERO_IAM}/v1/${organizationId}/role/${params.roleId}/copy`, {
    method: 'POST',
    body: params.copyRoleIdList,
  });
}

export async function queryUserInfo(params) {
  const organizationId = getCurrentOrganizationId();
  return request(`${HZERO_IAM}/hzero/v1/${organizationId}/role/${params.roleId}/info`, {
    method: 'GET',
  });
}

export async function queryCompanyModalData(params) {
  const organizationId = getCurrentOrganizationId();
  const param = parseParameters(params);
  return request(`${HZERO_IAM}/v1/${organizationId}/role/${param.roleId}/data/customers`, {
    method: 'GET',
    query: param,
  });
}

export async function querySupplierModalData(params) {
  const organizationId = getCurrentOrganizationId();
  const param = parseParameters(params);
  return request(`${HZERO_IAM}/v1/${organizationId}/role/${param.roleId}/data/suppliers`, {
    method: 'GET',
    query: param,
  });
}

export async function queryPurorgModalData(params) {
  const organizationId = getCurrentOrganizationId();
  const param = parseParameters(params);
  return request(`${HZERO_IAM}/v1/${organizationId}/role/${param.roleId}/data/purorgs`, {
    method: 'GET',
    query: param,
  });
}

export async function queryPuragentModalData(params) {
  const organizationId = getCurrentOrganizationId();
  const param = parseParameters(params);
  return request(`${HZERO_IAM}/v1/${organizationId}/role/${param.roleId}/data/puragents`, {
    method: 'GET',
    query: param,
  });
}

export async function queryPurcatModalData(params) {
  const organizationId = getCurrentOrganizationId();
  const param = parseParameters(params);
  return request(`${HZERO_IAM}/v1/${organizationId}/role/${param.roleId}/data/purcats`, {
    method: 'GET',
    query: param,
  });
}

/**
 * queryValueListModalData
 * @param {object} params - 值集新建数据
 */
export async function queryValueListModalData(params) {
  const organizationId = getCurrentOrganizationId();
  const param = parseParameters(params);
  return request(`${HZERO_IAM}/v1/${organizationId}/role/${param.roleId}/data/lovs`, {
    method: 'GET',
    query: param,
  });
}

/**
 * 查询值集视图
 * queryLovViewModalData
 * @param {object} params - 值集视图新建数据
 */
export async function queryLovViewModalData(params) {
  const organizationId = getCurrentOrganizationId();
  const param = parseParameters(params);
  return request(`${HZERO_IAM}/v1/${organizationId}/role/${param.roleId}/data/lov-views`, {
    method: 'GET',
    query: param,
  });
}

/**
 * 查询数据源
 * queryLovViewModalData
 * @param {object} params - 数据源新建数据
 */
export async function queryDataSourceModalData(params) {
  const organizationId = getCurrentOrganizationId();
  const param = parseParameters(params);
  return request(`${HZERO_IAM}/v1/${organizationId}/role/${param.roleId}/data/datasources`, {
    method: 'GET',
    query: param,
  });
}

/**
 * 查询数据组新建弹窗数据
 * queryDataGroupModalData
 * @param {object} params - 数据组新建数据
 */
export async function queryDataGroupModalData(params) {
  const organizationId = getCurrentOrganizationId();
  const param = parseParameters(params);
  return request(`${HZERO_IAM}/v1/${organizationId}/role/${param.roleId}/data/data-group`, {
    method: 'GET',
    query: param,
  });
}
