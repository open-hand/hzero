/**
 * saga - 事务定义
 * @date: 2018-12-24
 * @author: wangjiacheng <jiacheng.wang@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import request from 'utils/request';
import { HZERO_ASGARD } from 'utils/config';
import { isTenantRoleLevel, getCurrentOrganizationId } from 'utils/utils';

const tenantId = getCurrentOrganizationId();

function sagaApi() {
  return isTenantRoleLevel() ? `${tenantId}/sagas` : `sagas`;
}

/**
 * 查询列表数据
 * @async
 * @function fetchSagaList
 * @param {Object} params - 查询参数
 * @param {String} [params.page = 0] - 页码
 * @param {String} [params.size = 0] - 页数
 */
export async function fetchSagaList(params) {
  return request(`${HZERO_ASGARD}/v1/${sagaApi()}`, {
    method: 'GET',
    query: params,
  });
}

/**
 * 查询详情
 * @async
 * @function querySagaDetail
 * @param {Object} params - 查询参数
 */
export async function querySagaDetail(params) {
  return request(`${HZERO_ASGARD}/v1/${sagaApi()}/${params.id}`, {
    method: 'GET',
    query: params,
  });
}
