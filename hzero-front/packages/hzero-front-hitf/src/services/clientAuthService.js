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
 * 分页汇总查询客户端授权列表
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
      ? `${HZERO_HITF}/v1/${organizationId}/client-auths`
      : `${HZERO_HITF}/v1/client-auths`,
    {
      query,
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

/**
 * 客户端授权详情查询
 * @async
 * @function queryDetail
 * @param {!number} organizationId - 组织ID
 * @param {object} clientOauthId - 查询条件
 * @returns {object} fetch Promise
 */
export async function queryDetail(payload) {
  const { tenantId, clientOauthId } = payload;
  return request(
    organizationRoleLevel
      ? `${HZERO_HITF}/v1/${organizationId}/client-auths/${clientOauthId}`
      : `${HZERO_HITF}/v1/client-auths/${clientOauthId}?tenantId=${tenantId}`
  );
}

/**
 * 客户端授权
 * @async
 * @function save
 * @param {object} params - 请求参数
 */
export async function save(data) {
  return request(
    organizationRoleLevel
      ? `${HZERO_HITF}/v1/${organizationId}/client-auths`
      : `${HZERO_HITF}/v1/client-auths?tenantId=${data.organizationId}`,
    {
      method: 'POST',
      body: data,
    }
  );
}

/**
 * 删除客户端角色
 * @async
 * @function deleteAuthRole
 * @param {!number} organizationId - 组织ID
 * @param {Array} data - 数据
 * @returns {object} fetch Promise
 */
export async function deleteAuthRole(clientOauthId, data = []) {
  return request(
    organizationRoleLevel
      ? `${HZERO_HITF}/v1/${organizationId}/client-auths/${clientOauthId}/role/batch-delete`
      : `${HZERO_HITF}/v1/client-auths/${clientOauthId}/role/batch-delete`,
    {
      method: 'DELETE',
      body: data,
    }
  );
}
