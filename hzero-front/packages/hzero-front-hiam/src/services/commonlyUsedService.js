/**
 * expertService.js - 工作台卡片 service
 * @date: 2019-02-23
 * @author: YKK <kaikai.yang@hand-china.com>
 * @version: 0.0.1
 * @copyright: Copyright (c) 2019, Hand
 */

import request from 'utils/request';
import { HZERO_IAM } from 'utils/config';

/**
 *
 * 查询固定的常用功能
 * @export
 * @returns
 */
export async function queryFunctions() {
  return request(`${HZERO_IAM}/v1/user-dbd-menu`, {
    method: 'GET',
  });
}

/**
 * 保存 - 常用功能
 * @export
 * @param {Object} params
 * @returns
 */
export async function addFunctions(params) {
  return request(`${HZERO_IAM}/v1/user-dbd-menu`, {
    method: 'POST',
    body: params,
  });
}
