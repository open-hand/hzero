import request from 'utils/request';
import {
  parseParameters,
  filterNullValueObject,
  getCurrentOrganizationId,
  isTenantRoleLevel,
} from 'utils/utils';
import { HZERO_HITF, HZERO_PLATFORM } from 'utils/config';

const organizationId = getCurrentOrganizationId();
const organizationRoleLevel = isTenantRoleLevel();

/**
 * 分页查询应用信息
 * @async
 * @function queryList
 * @param {!number} organizationId - 组织ID
 * @param {object} params - 查询条件
 * @param {!number} [params.page = 0] - 数据页码
 * @param {!number} [params.size = 10] - 分页大小
 * @returns {object} fetch Promise
 */
export async function queryList(params = {}) {
  const query = filterNullValueObject(parseParameters(params));
  return request(
    organizationRoleLevel
      ? `${HZERO_HITF}/v1/${organizationId}/applications`
      : `${HZERO_HITF}/v1/applications`,
    {
      query,
    }
  );
}

/**
 * 查询应用明细信息
 * @async
 * @function queryDetail
 * @param {!number} organizationId - 组织ID
 * @param {!number} applicationId - 主键ID
 * @returns {object} fetch Promise
 */
export async function queryDetail(applicationId) {
  return request(
    organizationRoleLevel
      ? `${HZERO_HITF}/v1/${organizationId}/applications/${applicationId}`
      : `${HZERO_HITF}/v1/applications/${applicationId}`
  );
}

/**
 * 批量修改应用及Oauth2客户端明细信息
 * @async
 * @function save
 * @param {!number} organizationId - 组织ID
 * @param {object} data - 主键ID
 * @returns {object} fetch Promise
 */
export async function save(data = {}) {
  return request(
    organizationRoleLevel
      ? `${HZERO_HITF}/v1/${organizationId}/applications`
      : `${HZERO_HITF}/v1/applications`,
    {
      method: 'PUT',
      body: data,
    }
  );
}

/**
 * 批量新增应用及Oauth2客户端明细信息
 * @async
 * @function create
 * @param {!number} organizationId - 组织ID
 * @param {object} data - 主键ID
 * @returns {object} fetch Promise
 */
export async function create(data) {
  return request(
    organizationRoleLevel
      ? `${HZERO_HITF}/v1/${organizationId}/applications`
      : `${HZERO_HITF}/v1/applications`,
    {
      method: 'POST',
      body: data,
    }
  );
}

/**
 * 批量删除应用及Oauth2客户端明细信息
 * @async
 * @function deleteApplication
 * @param {!number} organizationId - 组织ID
 * @param {Array} data - 数据
 * @returns {object} fetch Promise
 */
export async function deleteApplication(data = []) {
  return request(
    organizationRoleLevel
      ? `${HZERO_HITF}/v1/${organizationId}/applications`
      : `${HZERO_HITF}/v1/applications`,
    {
      method: 'DELETE',
      body: data,
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

export async function deleteLines(assignIds = []) {
  return request(
    organizationRoleLevel
      ? `${HZERO_HITF}/v1/${organizationId}/applications/untied`
      : `${HZERO_HITF}/v1/applications/untied`,
    {
      method: 'DELETE',
      body: assignIds,
    }
  );
}
