/**
 * service - 自动转交配置
 * @date: 2018-8-22
 * @version: 1.0.0
 * @author: LZY <zhuyan.luo@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */

import request from 'utils/request';
import { HZERO_HWFP } from 'utils/config';
/**
 * 请求API前缀
 * @type {string}
 */
const prefix = `${HZERO_HWFP}/v1`;

/**
 * 查询转交配置
 * @param {Object} params - 查询参数
 */
export async function queryDelegateSet(params) {
  return request(`${prefix}/${params.organizationId}/delegate`, {
    method: 'GET',
  });
}
/**
 * 保存转交配置
 * @param {Object} params - 需保存的数据
 */
export async function addDelegateSet(params) {
  return request(`${prefix}/${params.organizationId}/delegate`, {
    method: 'POST',
    body: params,
  });
}
