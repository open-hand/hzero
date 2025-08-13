/**
 * service 程序信息
 * @since 2018-6-20
 * @version 0.0.1
 * @author  yuan.tian <yuan.tian@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import request from 'utils/request';
import { HZERO_IMP } from 'utils/config';

/**
 * 查询程序列表
 * @param {Object} params - 查询参数
 * @param {String} params.page - 页码
 * @param {String} params.size - 页数
 */
export async function query(params) {
  return request(`${HZERO_IMP}/v1/imported/program/list`, {
    method: 'GET',
    query: params,
  });
}
/**
 * 新增程序信息
 * @async
 * @function create
 * @param {Object} params - 查询参数
 */
export async function create(params) {
  return request(`${HZERO_IMP}/v1/imported/program/info`, {
    method: 'POST',
    body: params,
  });
}
/**
 * 更新程序信息
 * @param {Object} params - 参数
 * @param {String} params.promptKey - 模块代码
 * @param {String} params.promptCode - 代码
 * @param {String} params.lang - 语言名称
 * @param {String} params.description - 描述
 */
export async function update(params) {
  return request(`${HZERO_IMP}/v1/imported/program/info`, {
    method: 'PUT',
    body: params,
  });
}
/**
 * 删除程序
 * @param {Object} params - 参数
 */
export async function remove(params) {
  return request(`${HZERO_IMP}/v1/imported/program/info/${params.id}`, {
    method: 'DELETE',
  });
}
