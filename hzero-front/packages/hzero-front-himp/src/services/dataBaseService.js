/**
 * service 数据源信息
 * @since 2018-8-23
 * @version 0.0.1
 * @author  fushi.wang <fushi.wang@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import request from 'utils/request';
import { HZERO_IMP } from 'utils/config';

/**
 * 数据源查询接口
 * @async
 * @function fetchDataBaseList
 * @param {Object} params - 查询参数
 * @param {String} params.page - 页码
 * @param {String} params.size - 页数
 */
export async function fetchDataBaseList(params) {
  return request(`${HZERO_IMP}/v1/imported/database/list`, {
    method: 'GET',
    query: params,
  });
}

/**
 * 新增数据源信息
 * @async
 * @function createDatabase
 * @param {Object} params - 参数
 */
export async function createDatabase(params) {
  return request(`${HZERO_IMP}/v1/imported/database/info`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 更新数据源
 * @async
 * @function updateDatabase
 * @param {Object}} params.body -修改的数据
 */
export async function updateDatabase(params) {
  return request(`${HZERO_IMP}/v1/imported/database/info`, {
    method: 'PUT',
    body: params,
  });
}

/**
 * 删除数据源信息
 * @async
 * @function deleteDataBase
 * @param {Object} params - 参数
 * @param {String} params.databaseId - 数据源id
 */
export async function deleteDataBase(params) {
  return request(`${HZERO_IMP}/v1/imported/database/info/${params.databaseId}`, {
    method: 'DELETE',
  });
}

/**
 * 删除数据源信息
 * @async
 * @function connectTest
 * @param {Object} params - 参数
 * @param {String} params.databaseId - 数据源id
 */
export async function connectTest(params) {
  return request(`${HZERO_IMP}/v1/imported/database/testConnecting/${params.databaseId}`, {
    method: 'POST',
  });
}
