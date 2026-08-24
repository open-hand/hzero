/**
 * service - SQL执行界面
 * @date: 2018-9-27
 * @author: LZY <zhuyan.luo@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import request from 'utils/request';
import { HZERO_PLATFORM } from 'utils/config';
import { getCurrentOrganizationId } from 'utils/utils';

const organizationId = getCurrentOrganizationId();

/**
 *获取可访问的数据表
 * @returns
 */
export async function fetchDataTable(params) {
  return request(`${HZERO_PLATFORM}/v1/${organizationId}/db-ide`, {
    method: 'GET',
    query: params,
  });
}
/**
 *获取数据表的字段值
 * @returns
 */
export async function fetchTableField(params) {
  return request(`${HZERO_PLATFORM}/v1/${organizationId}/db-ide/field`, {
    method: 'GET',
    query: params,
  });
}
/**
 *获取SQL执行结果
 * @returns
 */
export async function fetchExecuteResult(params) {
  return request(`${HZERO_PLATFORM}/v1/${organizationId}/db-ide/sql`, {
    method: 'POST',
    body: { sql: params.sql },
    query: { tenantId: params.tenantId, page: params.page, size: params.size },
  });
}
/**
 *获取单条SQL的翻页结果
 * @returns
 */
export async function fetchSingleResult(params) {
  return request(`${HZERO_PLATFORM}/v1/${organizationId}/db-ide/sql/page`, {
    method: 'POST',
    body: { sql: params.sql },
    query: { page: params.page, size: params.size },
  });
}
