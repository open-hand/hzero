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
 * 接口汇总查询
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
      ? `${HZERO_HITF}/v1/${organizationId}/interfaces/self`
      : `${HZERO_HITF}/v1/interfaces/self`,
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
 * 接口详情查询
 * @async
 * @function queryDetail
 * @param {!number} organizationId - 组织ID
 * @param {object} interfaceId - 查询条件
 * @returns {object} fetch Promise
 */
export async function queryDetail(interfaceId) {
  return request(
    organizationRoleLevel
      ? `${HZERO_HITF}/v1/${organizationId}/interfaces/${interfaceId}`
      : `${HZERO_HITF}/v1/interfaces/${interfaceId}`
  );
}

/**
 * 查询接口认证信息列表
 * @async
 * @function queryList
 * @param {!number} organizationId - 组织ID
 * i@param {!number} interfaceId - 接口ID
 * @param {object} params - 查询条件
 * @param {!number} [params.page = 0] - 数据页码
 * @param {!number} [params.size = 10] - 分页大小
 * @returns {object} fetch Promise
 */
export async function queryAuthSelfList(interfaceId, params = {}) {
  const query = filterNullValueObject(parseParameters(params));
  return request(
    organizationRoleLevel
      ? `${HZERO_HITF}/v1/${organizationId}/${interfaceId}/auth/self`
      : `${HZERO_HITF}/v1/${interfaceId}/auth/self`,
    {
      query,
    }
  );
}

/**
 * 查询接口认证信息明细
 * @async
 * @function queryAuthSelfListDetail
 * @param {!number} organizationId - 组织ID
 * @param {object} interfaceId - 查询条件
 * @param {object} interfaceAuthId
 * @returns {object} fetch Promise
 */
export async function queryAuthSelfListDetail(interfaceId, interfaceAuthId) {
  return request(
    organizationRoleLevel
      ? `${HZERO_HITF}/v1/${organizationId}/${interfaceId}/auth/${interfaceAuthId}`
      : `${HZERO_HITF}/v1/${interfaceId}/auth/${interfaceAuthId}`
  );
}

/**
 * 新增运维配置
 * @async
 * @function createMonitor
 * @param {object} params - 请求参数
 */
export async function createAuthSelf(interfaceId, data) {
  return request(
    organizationRoleLevel
      ? `${HZERO_HITF}/v1/${organizationId}/${interfaceId}/auth`
      : `${HZERO_HITF}/v1/${interfaceId}/auth`,
    {
      method: 'POST',
      body: data,
    }
  );
}

/**
 * 新增运维配置
 * @async
 * @function createMonitor
 * @param {object} params - 请求参数
 */
export async function updateAuthSelf(interfaceId, data) {
  return request(
    organizationRoleLevel
      ? `${HZERO_HITF}/v1/${organizationId}/${interfaceId}/auth`
      : `${HZERO_HITF}/v1/${interfaceId}/auth`,
    {
      method: 'PUT',
      body: data,
    }
  );
}

/**
 * 删除接口认证信息
 * @async
 * @function deleteAuthSelf
 * @param {!number} organizationId - 组织ID
 * @param {Array} data - 数据
 * @returns {object} fetch Promise
 */
export async function deleteAuthSelf(interfaceId, data = []) {
  return request(
    organizationRoleLevel
      ? `${HZERO_HITF}/v1/${organizationId}/${interfaceId}/auth/batch-delete`
      : `${HZERO_HITF}/v1/${interfaceId}/auth/batch-delete`,
    {
      method: 'DELETE',
      body: data,
    }
  );
}

/**
 * 批量添加授权
 * @async
 * @function batchAddAuth
 * @param {object} payload - 数据
 */
export async function batchAddAuth(payload) {
  return request(
    organizationRoleLevel
      ? `${HZERO_HITF}/v1/${organizationId}/auth/batch-save`
      : `${HZERO_HITF}/v1/auth/batch-save?organizationId=${organizationId}`,
    {
      method: 'PUT',
      body: payload,
    }
  );
}

/**
 * 测试OAuth2服务认证配置
 * @async
 * @function testOAuth2
 * @param {object} params - 请求参数
 */
export async function testAuth(data) {
  return request(
    organizationRoleLevel
      ? `${HZERO_HITF}/v1/${organizationId}/http-authorizations/test`
      : `${HZERO_HITF}/v1/http-authorizations/test`,
    {
      method: 'POST',
      body: { ...data },
    }
  );
}
