import request from 'utils/request';
import { HZERO_SDR } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel, parseParameters } from 'utils/utils';

const organizationId = getCurrentOrganizationId();
const organizationRoleLevel = isTenantRoleLevel();

/**
 * 查询列表数据
 * @async
 * @function fetchExecutable
 * @param {Object} params - 查询参数
 * @param {String} [params.page = 0] - 页码
 * @param {String} [params.size = 0] - 页数
 */
export async function fetchExecutable(params) {
  const param = parseParameters(params);
  return request(
    organizationRoleLevel
      ? `${HZERO_SDR}/v1/${organizationId}/executables`
      : `${HZERO_SDR}/v1/executables`,
    {
      method: 'GET',
      query: param,
    }
  );
}

/**
 * 查询详情
 * @async
 * @function fetchExecutableDetail
 * @param {Object} params - 查询参数
 */
export async function fetchExecutableDetail(params) {
  return request(
    organizationRoleLevel
      ? `${HZERO_SDR}/v1/${organizationId}/executables/${params.executableId}`
      : `${HZERO_SDR}/v1/executables/${params.executableId}`,
    {
      method: 'GET',
    }
  );
}

/**
 * 创建
 * @async
 * @function createExecutable
 * @param {String} params - 保存参数
 */
export async function createExecutable(params) {
  return request(
    organizationRoleLevel
      ? `${HZERO_SDR}/v1/${organizationId}/executables`
      : `${HZERO_SDR}/v1/executables`,
    {
      method: 'POST',
      body: params,
    }
  );
}

/**
 * 修改
 * @async
 * @function updateExecutable
 * @param {String} params.enabledFlag - 是否启用
 */
export async function updateExecutable(params) {
  return request(
    organizationRoleLevel
      ? `${HZERO_SDR}/v1/${organizationId}/executables`
      : `${HZERO_SDR}/v1/executables`,
    {
      method: 'PUT',
      body: params,
    }
  );
}

/**
 * 删除头
 * @async
 * @function deleteHeader
 * @param {object} params - 请求参数
 */
export async function deleteHeader(params) {
  return request(
    organizationRoleLevel
      ? `${HZERO_SDR}/v1/${organizationId}/executables`
      : `${HZERO_SDR}/v1/executables`,
    {
      method: 'DELETE',
      body: params,
    }
  );
}

/**
 * 执行器列表配置
 * @async
 * @function executorConfig
 * @param {object} params - 查询条件
 */
export async function executorConfig(params) {
  return request(
    organizationRoleLevel
      ? `${HZERO_SDR}/v1/${organizationId}/executor-configs/${params.executorId}/config`
      : `${HZERO_SDR}/v1/executor-configs/${params.executorId}/config`,
    {
      method: 'GET',
      query: params,
    }
  );
}

/**
 * 执行器列表配置
 * @async
 * @function executorConfig
 * @param {object} params - 查询条件
 */
export async function executorConfigByExecutable(params) {
  const { executorId, ...others } = params;
  return request(
    organizationRoleLevel
      ? `${HZERO_SDR}/v1/${organizationId}/executor-configs/${executorId}/by-executable`
      : `${HZERO_SDR}/v1/executor-configs/${executorId}/by-executable`,
    {
      method: 'GET',
      query: others,
    }
  );
}
