import request from 'utils/request';
import { HZERO_PLATFORM } from 'utils/config';
import { parseParameters, isTenantRoleLevel, getCurrentOrganizationId } from 'utils/utils';

const tenantId = getCurrentOrganizationId();
const isSiteFlag = !isTenantRoleLevel();

function sourceApi() {
  return isTenantRoleLevel() ? `${tenantId}/datasources` : `datasources`;
}

// function serviceApi() {
//   return isSiteFlag ? `datasource-services` : `${tenantId}/datasource-services`;
// }

/**
 * 查询数据源列表数据
 * @async
 * @function fetchDataSourceList
 * @param {object} params - 查询条件
 * @param {!number} [params.page = 0] - 数据页码
 * @param {!number} [params.size = 10] - 分页大小
 * @returns {object} fetch Promise
 */
export async function fetchDataSourceList(params) {
  const param = parseParameters(params);
  return request(`${HZERO_PLATFORM}/v1/${sourceApi()}`, {
    method: 'GET',
    query: param,
  });
}

/**
 * 查询数据源详情
 * @async
 * @function fetchDataSourceDetail
 * @param {object} params - 查询条件
 * @returns {object} fetch Promise
 */
export async function fetchDataSourceDetail(params) {
  return request(`${HZERO_PLATFORM}/v1/${sourceApi()}/${params.datasourceId}`, {
    method: 'GET',
    query: params,
  });
}

/**
 * 添加数据源信息
 * @async
 * @function createDataSource
 * @param {object} params - 请求参数
 * @param {!object} params.datasourceCode - 数据源编码
 * @param {!string} params.description - 描述
 * @param {?number} params.datasourceUrl - URL地址
 * @param {!string} params.user - 用户
 * @param {?number} params.password - 密码
 * @param {!string} params.enabledFlag - 启用标记
 * @returns {object} fetch Promise
 */
export async function createDataSource(params) {
  return request(`${HZERO_PLATFORM}/v1/${sourceApi()}`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 编辑数据源信息
 * @async
 * @function editDataSource
 * @param {object} params - 请求参数
 * @param {!object} params.datasourceCode - 数据源编码
 * @param {!string} params.description - 描述
 * @param {?number} params.datasourceUrl - URL地址
 * @param {!string} params.user - 用户
 * @param {?number} params.password - 密码
 * @param {!string} params.enabledFlag - 启用标记
 * @param {!string} params.datasourceId - datasourceId
 * @param {!string} params.objectVersionNumber - 版本号
 * @returns {object} fetch Promise
 */
export async function editDataSource(params) {
  return request(`${HZERO_PLATFORM}/v1/${sourceApi()}`, {
    method: 'PUT',
    body: params,
  });
}
// /**
//  * 删除数据源信息
//  * @async
//  * @function deleteDataSource
//  * @param {number} datasourceId - 数据源Id
//  * @returns {object} fetch Promise
//  */
// export async function deleteDataSource(params) {
//   return request(`${HZERO_PLATFORM}/v1/${sourceApi()}`, {
//     method: 'DELETE',
//     body: params,
//   });
// }

/**
 * todo 废弃，保存的同时测试
 * 测试数据源
 * @async
 * @function createDataSource
 * @param {object} params - 请求参数
 * @param {!object} params.datasourceCode - 数据源编码
 * @param {!string} params.description - 描述
 * @param {?number} params.datasourceUrl - URL地址
 * @param {!string} params.user - 用户
 * @param {?number} params.password - 密码
 * @param {!string} params.enabledFlag - 启用标记
 * @returns {object} fetch Promise
 */
export async function handleTestDataSource(params) {
  return request(`${HZERO_PLATFORM}/v1/datasources/check`, {
    method: 'GET',
    query: { ...params },
  });
}

/**
 * 获取连接池参数
 * @async
 * @function getDbPoolParams
 * @param {object} params - 查询条件
 * @returns {object} fetch Promise
 */
export async function getDbPoolParams(params) {
  return request(`${HZERO_PLATFORM}/v1/${sourceApi()}/${params.dbPoolType}/dbpool-option`, {
    method: 'GET',
  });
}

/**
 * 获取驱动类
 * @async
 * @function getDriverClass
 * @param {object} params - 查询条件
 * @returns {object} fetch Promise
 */
export async function getDriverClass(params) {
  return request(`${HZERO_PLATFORM}/v1/${sourceApi()}/${params.dbType}/initialize`, {
    method: 'GET',
  });
}

// /**
//  * 查询服务
//  * @async
//  * @function fetchServiceList
//  * @param {object} params - 查询条件
//  * @param {!number} [params.page = 0] - 数据页码
//  * @param {!number} [params.size = 10] - 分页大小
//  * @returns {object} fetch Promise
//  */
// export async function fetchServiceList(params) {
//   return request(`${HZERO_PLATFORM}/v1/${serviceApi()}`, {
//     method: 'GET',
//     query: params,
//   });
// }

// /**
//  * 添加服务
//  * @async
//  * @function addService
//  * @param {object} params - 请求参数
//  * @param {!object} params.datasourceId - 数据库id
//  * @param {!string} params.tenantId - 租户id
//  * @returns {object} fetch Promise
//  */
// export async function addService(params) {
//   return request(`${HZERO_PLATFORM}/v1/${serviceApi()}`, {
//     method: 'POST',
//     body: params,
//   });
// }

// /**
//  * 删除服务
//  * @async
//  * @function deleteService
//  * @param {object} params,datasourceId - 请求参数
//  * @param {number} datasourceServiceId - datasourceServiceId
//  * @returns {object} fetch Promise
//  */
// export async function deleteService(params) {
//   return request(`${HZERO_PLATFORM}/v1/${serviceApi()}`, {
//     method: 'DELETE',
//     body: params,
//   });
// }

/**
 * 表单配置
 * @async
 * @function fetchFormParams
 * @param {object} params - 请求参数
 * @returns {object} fetch Promise
 */
export async function fetchFormParams(params) {
  return request(
    `${HZERO_PLATFORM}/v1${isSiteFlag ? '/' : `/${tenantId}/`}form-lines/header-code`,
    {
      method: 'GET',
      query: params,
    }
  );
}

/**
 * 删除服务驱动
 * @param params
 * @returns {Promise<Object>}
 */
export async function testConnection(params = {}) {
  return request(
    `${HZERO_PLATFORM}/v1/${isSiteFlag ? '' : `${tenantId}/`}datasources/test-connection`,
    {
      method: 'POST',
      body: params,
    }
  );
}
