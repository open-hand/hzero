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

export async function queryCompany(params) {
  const organizationId = getCurrentOrganizationId();
  return request(`${HZERO_IAM}/v1/${organizationId}/users/${params.userId}/authority-org`, {
    method: 'GET',
    query: params,
  });
}

export async function updateCompany(params) {
  const organizationId = getCurrentOrganizationId();
  return request(`${HZERO_IAM}/v1/${organizationId}/users/${params.userId}/authority-org`, {
    method: 'POST',
    body: params.checkList,
  });
}

export async function queryData(params) {
  const organizationId = getCurrentOrganizationId();
  const param = parseParameters(params);
  return request(`${HZERO_IAM}/v1/${organizationId}/users/${param.userId}/authority`, {
    method: 'GET',
    query: param,
  });
}

export async function saveData(params) {
  const organizationId = getCurrentOrganizationId();
  return request(
    `${HZERO_IAM}/v1/${organizationId}/users/${params.userId}/authority?authorityTypeCode=${params.authorityTypeCode}`,
    {
      method: 'POST',
      body: params,
    }
  );
}
export async function deleteData(params) {
  const organizationId = getCurrentOrganizationId();
  return request(`${HZERO_IAM}/v1/${organizationId}/users/${params.userId}/authority`, {
    method: 'DELETE',
    body: params.deleteRows,
  });
}

export async function changeAuthority(params) {
  const organizationId = getCurrentOrganizationId();
  return request(
    `${HZERO_IAM}/v1/${organizationId}/users/${params.userId}/authority/exchange?exchanageUserId=${params.exchanageUserId}`,
    {
      method: 'POST',
      body: params,
    }
  );
}

export async function copyAuthority(params) {
  const organizationId = getCurrentOrganizationId();
  return request(`${HZERO_IAM}/v1/${organizationId}/users/${params.userId}/authority/copy`, {
    method: 'POST',
    body: params.copyUserIdList,
  });
}

export async function queryUserInfo(params) {
  const organizationId = getCurrentOrganizationId();
  return request(`${HZERO_IAM}/hzero/v1/${organizationId}/users/${params.userId}/info`, {
    method: 'GET',
  });
}

export async function queryCompanyModalData(params) {
  const organizationId = getCurrentOrganizationId();
  const param = parseParameters(params);
  return request(`${HZERO_IAM}/v1/${organizationId}/users/${param.userId}/data/customers`, {
    method: 'GET',
    query: param,
  });
}

export async function querySupplierModalData(params) {
  const organizationId = getCurrentOrganizationId();
  const param = parseParameters(params);
  return request(`${HZERO_IAM}/v1/${organizationId}/users/${param.userId}/data/suppliers`, {
    method: 'GET',
    query: param,
  });
}

export async function queryPurorgModalData(params) {
  const organizationId = getCurrentOrganizationId();
  const param = parseParameters(params);
  return request(`${HZERO_IAM}/v1/${organizationId}/users/${param.userId}/data/purorgs`, {
    method: 'GET',
    query: param,
  });
}

export async function queryPuragentModalData(params) {
  const organizationId = getCurrentOrganizationId();
  const param = parseParameters(params);
  return request(`${HZERO_IAM}/v1/${organizationId}/users/${param.userId}/data/puragents`, {
    method: 'GET',
    query: param,
  });
}

export async function queryPurcatModalData(params) {
  const organizationId = getCurrentOrganizationId();
  const param = parseParameters(params);
  return request(`${HZERO_IAM}/v1/${organizationId}/users/${param.userId}/data/purcats`, {
    method: 'GET',
    query: param,
  });
}

export async function queryLovModalData(params) {
  const organizationId = getCurrentOrganizationId();
  const param = parseParameters(params);
  return request(`${HZERO_IAM}/v1/${organizationId}/users/${param.userId}/data/lovs`, {
    method: 'GET',
    query: param,
  });
}

export async function queryLovViewModalData(params) {
  const organizationId = getCurrentOrganizationId();
  const param = parseParameters(params);
  return request(`${HZERO_IAM}/v1/${organizationId}/users/${param.userId}/data/lov-views`, {
    method: 'GET',
    query: param,
  });
}
// 获取数据源数据
export async function queryDataSourceModalData(params) {
  const organizationId = getCurrentOrganizationId();
  const param = parseParameters(params);
  return request(`${HZERO_IAM}/v1/${organizationId}/users/${param.userId}/data/datasources`, {
    method: 'GET',
    query: param,
  });
}

// 获取数据组弹窗数据
export async function queryDataGroupModalData(params) {
  const organizationId = getCurrentOrganizationId();
  const param = parseParameters(params);
  return request(`${HZERO_IAM}/v1/${organizationId}/users/${param.userId}/data/data-group`, {
    method: 'GET',
    query: param,
  });
}

// 获取权限维度显示tab页面
export async function queryUserDimension(params) {
  const organizationId = getCurrentOrganizationId();
  return request(`${HZERO_IAM}/v1/${organizationId}/doc-type/dimension/user/${params.userId}`, {
    method: 'GET',
  });
}
