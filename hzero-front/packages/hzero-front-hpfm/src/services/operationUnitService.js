/*
 * operationUnitService.js - 业务实体定义 service
 * @date: 2018-10-24
 * @author: geekrainy <chao.zheng02@hand-china.com>
 * @version: 0.0.1
 * @copyright: Copyright (c) 2018, Hand
 */

import request from 'utils/request';
import { parseParameters } from 'utils/utils';
import { HZERO_PLATFORM } from 'utils/config';

/**
 * 查询业务实体列表
 * @param {Object} params - 查询参数
 */
export async function queryOperationUnit(params) {
  const query = parseParameters(params);
  return request(`${HZERO_PLATFORM}/v1/${params.tenantId}/operation-units`, {
    method: 'GET',
    query,
  });
}

/**
 * 新增或更新业务实体
 * @param {Object} params 新增或更新业务实体参数
 */
export async function saveOperationUnit(params) {
  const { list, tenantId } = params;
  return request(`${HZERO_PLATFORM}/v1/${tenantId}/operation-units`, {
    method: 'POST',
    body: list,
  });
}
