import request from 'utils/request';
import { HZERO_PLATFORM } from 'utils/config';
import { parseParameters, isTenantRoleLevel, getCurrentOrganizationId } from 'utils/utils';

const tenantId = getCurrentOrganizationId();

function templateAssignApi() {
  return isTenantRoleLevel() ? `${tenantId}/template-assigns` : 'template-assigns';
}

// 查询分配模板列表
export async function fetchTemplateAssign(params) {
  const param = parseParameters(params);
  return request(`${HZERO_PLATFORM}/v1/${templateAssignApi()}`, {
    method: 'GET',
    query: param,
  });
}

// 查询可分配模板列表
export async function fetchPortalAssignAssignable(params) {
  const param = parseParameters(params);
  return request(`${HZERO_PLATFORM}/v1/${templateAssignApi()}/assignable`, {
    method: 'GET',
    query: param,
  });
}

// 批量分配模板
export async function templateAssignCreate(params) {
  return request(`${HZERO_PLATFORM}/v1/${templateAssignApi()}/batch-create`, {
    method: 'POST',
    body: params,
  });
}

// 删除分配模板
export async function templateAssignDelete(params) {
  const param = parseParameters(params);
  return request(`${HZERO_PLATFORM}/v1/${templateAssignApi()}`, {
    method: 'DELETE',
    body: param,
  });
}

// 设置默认分配模板
export async function templateAssignDefault(params) {
  const param = parseParameters(params);
  return request(`${HZERO_PLATFORM}/v1/${templateAssignApi()}/default`, {
    method: 'PUT',
    query: param,
  });
}
