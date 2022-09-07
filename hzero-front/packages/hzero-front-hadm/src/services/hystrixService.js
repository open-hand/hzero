import { HZERO_PLATFORM, HZERO_ADM } from 'utils/config';
import request from 'utils/request';
import { parseParameters, isTenantRoleLevel, getCurrentOrganizationId } from 'utils/utils';

const tenantId = getCurrentOrganizationId();

function hystrixApi() {
  return isTenantRoleLevel() ? `${tenantId}/hystrix-confs` : 'hystrix-confs';
}

function hystrixLineApi() {
  return isTenantRoleLevel() ? `${tenantId}/hystrix-conf-lines` : 'hystrix-conf-lines';
}

/**
 * 查询单位类型定义列表
 */
export async function fetchList(params = {}) {
  return request(`${HZERO_ADM}/v1/${hystrixApi()}`, {
    method: 'GET',
    query: parseParameters(params),
  });
}

export async function add(params) {
  return request(`${HZERO_ADM}/v1/${hystrixApi()}`, {
    method: 'POST',
    body: [params],
  });
}

export async function fetchHeaderInformation(params) {
  const { page, pageSize, confId } = params;
  return request(`${HZERO_ADM}/v1/${hystrixApi()}/${confId}`, {
    method: 'GET',
    query: { page, pageSize },
  });
}

export async function refresh(params) {
  return request(`${HZERO_ADM}/v1/${hystrixApi()}/batch-refresh`, {
    method: 'POST',
    body: params,
  });
}

export async function fetchDetailList(params = {}) {
  return request(`${HZERO_ADM}/v1/${hystrixLineApi()}`, {
    method: 'GET',
    query: params,
  });
}

export async function deleteDetails(params) {
  return request(`${HZERO_ADM}/v1/${hystrixLineApi()}`, {
    method: 'DELETE',
    body: params,
  });
}

export async function fetchConfTypeCodeList(params) {
  return request(`${HZERO_PLATFORM}/v1/lovs/value`, {
    method: 'GET',
    query: params,
  });
}
export async function fetchProperNameList(params) {
  return request(`${HZERO_PLATFORM}/v1/lovs/value/parent-value`, {
    method: 'GET',
    query: params,
  });
}
