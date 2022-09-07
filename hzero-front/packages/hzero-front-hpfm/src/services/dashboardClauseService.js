/**
 * expertService.js - 条目配置 service
 * @date: 2019-01-28
 * @author: YKK <kaikai.yang@hand-china.com>
 * @version: 0.0.1
 * @copyright: Copyright (c) 2019, Hand
 */
import request from 'utils/request';
import { getPlatformVersionApi, parseParameters } from 'utils/utils';
import { HZERO_PLATFORM } from 'utils/config';

/**
 *
 * 查询条目配置
 * @export
 * @param {Object} params 查询参数
 * @returns
 */
export async function queryClause(params) {
  const param = parseParameters(params);
  return request(`${HZERO_PLATFORM}/v1/${getPlatformVersionApi('dashboard-clause')}`, {
    method: 'GET',
    query: param,
  });
}

/**
 *
 * 查询条目配置详情
 * @export
 * @param {Object} params 查询参数
 * @returns
 */
export async function queryClauseDetail(params) {
  const param = parseParameters(params);
  return request(`${HZERO_PLATFORM}/v1/${getPlatformVersionApi('dashboard-clause')}/details`, {
    method: 'GET',
    query: param,
  });
}

/**
 * 新建 - 条目配置
 * @export
 * @param {Object} params
 * @returns
 */
export async function addClause(params) {
  return request(`${HZERO_PLATFORM}/v1/${getPlatformVersionApi('dashboard-clause')}`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 更新 - 条目配置
 * @export
 * @param {Object} params
 * @returns
 */
export async function updateClause(params) {
  return request(`${HZERO_PLATFORM}/v1/${getPlatformVersionApi('dashboard-clause')}`, {
    method: 'PUT',
    body: params,
  });
}

/**
 * 查询卡片 已经分配的租户信息
 * @param {object} params - 带分页信息的查询信息
 * @param {object} params.page - 分页信息
 * @param {object} params.sort - 排序信息
 * @param {string} params.tenantName - 租户名称
 * @param {string} params.registerDateFrom - 注册时间从
 * @param {string} params.registerDateTo - 注册时间至
 * @requestUrl {HZERO_PLATFORM}/v1/dashboard-clause-assign
 * @requestMethod GET
 */
export async function cardTenantQueryPage(params) {
  const param = parseParameters(params);
  return request(`${HZERO_PLATFORM}/v1/${getPlatformVersionApi('dashboard-clause-assign')}`, {
    method: 'GET',
    query: param,
  });
}

/**
 * 给卡片 分配新的租户
 * @param {Tenant[]} params - 租户信息
 * @requestUrl {HZERO_PLATFORM}/v1/dashboard-clause-assign
 * @requestMethod POST
 */
export async function cardTenantInsert(params) {
  return request(`${HZERO_PLATFORM}/v1/${getPlatformVersionApi('dashboard-clause-assign')}`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 删除 卡片分配新的租户
 * @param {Tenant[]} params - 租户信息
 * @requestUrl {HZERO_PLATFORM}/v1/dashboard-clause-assign
 * @requestMethod DELETE
 */
export async function cardTenantDelete(params) {
  return request(`${HZERO_PLATFORM}/v1/${getPlatformVersionApi('dashboard-clause-assign')}`, {
    method: 'DELETE',
    body: params,
  });
}

/**
 * 查询条目配置table
 * @export
 * @param {Object} params 查询参数
 * @returns
 */
export async function fetchTable(params) {
  const { clauseId, ...otherParmams } = params;
  const param = parseParameters(otherParmams);
  return request(
    `${HZERO_PLATFORM}/v1/${getPlatformVersionApi('dashboard-card-clauses')}/${clauseId}`,
    {
      method: 'GET',
      query: param,
    }
  );
}

/**
 * 查询条目配置详情form
 * @export
 * @param {Number} clauseId 条目Id
 * @returns
 */
export async function fetchHead(clauseId) {
  return request(
    `${HZERO_PLATFORM}/v1/${getPlatformVersionApi('dashboard-clause')}/details/${clauseId}`,
    {
      method: 'GET',
    }
  );
}

/**
 * 查询条目配置详情form
 * @export
 * @param {Object} params 查询参数
 * @returns
 */
export async function saveClause(params) {
  const { isEdit, ...otherParmams } = params;
  return request(`${HZERO_PLATFORM}/v1/${getPlatformVersionApi('dashboard-clause')}`, {
    method: isEdit ? 'PUT' : 'POST',
    body: otherParmams,
  });
}

/**
 * 删除卡片
 * @export
 * @param {Array} params 查询参数
 * @returns
 */
export async function deleteCard(params) {
  return request(`${HZERO_PLATFORM}/v1/${getPlatformVersionApi('dashboard-card-clauses')}`, {
    method: 'DELETE',
    body: params,
  });
}

/**
 * 查询条目配置table
 * 如果是租户级 需要传 clauseLevel="TENANT" 和 clauseId
 * @export
 * @param {Object} params 查询参数
 * @returns
 */
export async function fetchCard(params) {
  const { clauseId, dataTenantLevel, ...newParams } = params;
  const param = parseParameters(newParams);
  const newParam =
    dataTenantLevel === 'TENANT'
      ? {
          ...param,
          clauseId,
          clauseLevel: 'TENANT',
        }
      : param;
  return request(`${HZERO_PLATFORM}/v1/${getPlatformVersionApi('dashboard/card/assign-list')}`, {
    method: 'GET',
    query: newParam,
  });
}
