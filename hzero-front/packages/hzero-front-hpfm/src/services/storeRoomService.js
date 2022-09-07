/*
 * storeRoomService - 库房
 * @date: 2018/10/13 10:42:18
 * @author: HB <bin.huang02@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import request from 'utils/request';
import { getCurrentOrganizationId, parseParameters } from 'utils/utils';
import { HZERO_PLATFORM } from 'utils/config';

const organizationId = getCurrentOrganizationId();

/**
 * 查询库房列表的数据
 * @param {Object} params - 查询参数
 * @param {String} params.page - 页码
 * @param {String} params.size - 页数
 * @param {String} params.companyId - 公司编码
 */
export async function queryStoreRoom(params) {
  const param = parseParameters(params);
  return request(`${HZERO_PLATFORM}/v1/${organizationId}/inventories`, {
    method: 'GET',
    query: param,
  });
}
export async function saveStoreRoom(params) {
  return request(`${HZERO_PLATFORM}/v1/${organizationId}/inventories/batch-save`, {
    method: 'POST',
    body: params,
  });
}
