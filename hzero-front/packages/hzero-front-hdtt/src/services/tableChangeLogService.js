/**
 * service - 表结构变更
 * @date: 2019-7-18
 * @author: hulingfangzi <lingfangzi.hu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import request from 'utils/request';
import { HZERO_DTT } from 'utils/config';
import { parseParameters, getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';

const organizationId = getCurrentOrganizationId();
const organizationRoleLevel = isTenantRoleLevel();

/**
 * 查询表结构变更列表数据
 * @param {*} params.tableName 生产表名
 * @param {*} params.name 服务名称
 * @param {*} params.tenantId 租户名称
 * @param {*} params.page 分页参数
 * @param {*} params.size 分页参数
 */
export async function queryChangeLog(params) {
  const param = parseParameters(params);
  return request(
    organizationRoleLevel
      ? `${HZERO_DTT}/v1/${organizationId}/db-migration-logs`
      : `${HZERO_DTT}/v1/db-migration-logs`,
    {
      method: 'GET',
      query: { ...param },
    }
  );
}

/**
 * 处理错误
 * @param {Object} payload 错误事件
 */
export async function handleError(payload) {
  return request(
    organizationRoleLevel
      ? `${HZERO_DTT}/v1/${organizationId}/db-migration-logs/execute`
      : `${HZERO_DTT}/v1/db-migration-logs/execute`,
    {
      method: 'POST',
      body: payload,
    }
  );
}
