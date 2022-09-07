// /**
//  * roleService - 角色管理service
//  * @date: 2018-7-24
//  * @author: lijun <jun.li06@hand-china.com>
//  * @version: 0.0.1
//  * @copyright Copyright (c) 2018, Hand
//  */
// import request from 'utils/request';
// import { HZERO_PLATFORM, HZERO_FILE } from 'utils/config';
//
// /**
//  * 分页查询静态文本
//  * @async
//  * @function queryList
//  * @param {object} params - 查询条件
//  * @returns {object} fetch Promise
//  */
// export async function queryList(params = {}) {
//   return request(`${HZERO_PLATFORM}/v1/static-texts`, {
//     query: params,
//   });
// }
//
// /**
//  * 分页查询静态文本
//  * @async
//  * @function queryList
//  * @param {object} params - 查询条件
//  * @param {!string} [params.bucketName = 0] - 数据页码
//  * @param {!string} [params.fileName = 10] - 分页大小
//  * @returns {object} fetch Promise
//  */
// export async function uploadImage(params = {}, file) {
//   return request(`${HZERO_FILE}/v1/files/multipart`, {
//     method: 'POST',
//     query: params,
//     body: file,
//     responseType: 'text',
//   });
// }
//
// /**
//  * 查询值集
//  * @async
//  * @function queryCode
//  * @param {object} params - 查询条件
//  * @param {!string} param.lovCode - 查询条件
//  * @returns {object} fetch Promise
//  */
// export async function queryCode(params = {}) {
//   return request(`${HZERO_PLATFORM}/v1/lovs/value`, {
//     query: params,
//   });
// }
//
// /**
//  * 查询静态文本明细
//  * @async
//  * @function queryDetail
//  * @param {object} params - 查询条件
//  * @returns {object} fetch Promise
//  */
// export async function queryDetail(textId) {
//   return request(`${HZERO_PLATFORM}/v1/static-texts/details/${textId}`);
// }
//
// /**
//  * 新增静态文本
//  * @async
//  * @function create
//  * @param {object} data - 数据
//  * @returns {object} fetch Promise
//  */
// export async function create(data = {}) {
//   return request(`${HZERO_PLATFORM}/v1/static-texts`, {
//     method: 'POST',
//     body: data,
//   });
// }
//
// /**
//  * 修改平台静态信息
//  * @async
//  * @function create
//  * @param {object} data - 数据
//  * @returns {object} fetch Promise
//  */
// export async function update(data = {}) {
//   return request(`${HZERO_PLATFORM}/v1/static-texts`, {
//     method: 'PUT',
//     body: data,
//   });
// }
//
// /**
//  * 修改平台静态信息
//  * @async
//  * @function delete
//  * @param {object} data - 数据
//  * @returns {object} fetch Promise
//  */
// export async function batchDelete(data = []) {
//   return request(`${HZERO_PLATFORM}/v1/static-texts`, {
//     method: 'DELETE',
//     body: data,
//   });
// }

/**
 * staticTextServiceOrg.js
 * @date 2018-12-25
 * @author WY yang.wang06@hand-china.com
 * @copyright Copyright (c) 2018, Hand
 */

import request from 'utils/request';
import { HZERO_PLATFORM } from 'utils/config';
import { parseParameters } from 'utils/utils';

/**
 * 新增静态文本
 * @param {object} record
 * @return {Promise<Response>}
 */
export async function staticTextCreateOne(record) {
  return request(`${HZERO_PLATFORM}/v1/static-texts`, {
    method: 'POST',
    body: record,
  });
}

/**
 * 批量删除静态文本
 * @param {object[]} records
 * @return {Promise<Response>}
 */
export async function staticTextRemoveList(records) {
  return request(`${HZERO_PLATFORM}/v1/static-texts`, {
    method: 'DELETE',
    body: records,
  });
}

/**
 * 修改静态文本
 * @param {object} record
 * @return {Promise<Response>}
 */
export async function staticTextUpdateOne(record) {
  return request(`${HZERO_PLATFORM}/v1/static-texts`, {
    method: 'PUT',
    body: record,
  });
}

/**
 * 分页查询静态文本分页
 * @param {number} textId - 静态文本id
 * @param {object} params
 * @return {Promise<Response>}
 */
export async function staticTextFetchOne(textId, params) {
  return request(`${HZERO_PLATFORM}/v1/static-texts/details/${textId}`, {
    method: 'GET',
    query: params,
  });
}

/**
 * 分页查询静态文本分页
 * @param {object} params
 * @return {Promise<Response>}
 */
export async function staticTextFetchList(params) {
  const parsedParams = parseParameters(params);
  return request(`${HZERO_PLATFORM}/v1/static-texts`, {
    method: 'GET',
    query: parsedParams,
  });
}
