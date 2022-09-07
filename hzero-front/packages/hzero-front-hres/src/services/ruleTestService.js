/**
 * 流程测试
 * @Author: wangke <ke.wang05@hand-china.com>
 * @Date: 2019-10-15
 * @LastEditTime: 2019-10-15 16:45
 * @Copyright: Copyright (c) 2018, Hand
 */
import request from 'utils/request';
import { HZERO_HRES } from 'utils/config';
import { getCurrentOrganizationId } from 'utils/utils';

/**
 * 获取动态列
 * @param param
 * @returns {Promise<void>}
 */
export async function getColumnList(param) {
  return request(`${HZERO_HRES}/v1/${getCurrentOrganizationId()}/in-parameter`, {
    method: 'GET',
    query: { ...param },
    // body: param,
  });
}

/**
 * executeTest
 * @param param
 * @returns {Promise<void>}
 */
export async function executeTest(param) {
  return request(`${HZERO_HRES}/v1/${getCurrentOrganizationId()}/rule/test-rule`, {
    method: 'POST',
    query: { tenantId: getCurrentOrganizationId() },
    body: param,
  });
}

/**
 * 流程记录状态
 * @param param
 * @returns {Promise<void>}
 */
export async function detailExecuteStatus(param) {
  return request(`${HZERO_HRES}/v1/${getCurrentOrganizationId()}/history-detail/execute-status`, {
    method: 'GET',
    query: { tenantId: getCurrentOrganizationId() },
    body: { ...param, tenantId: getCurrentOrganizationId() },
  });
}

/**
 * 测试流程图
 * @param param
 * @returns {Promise<void>}
 */
export async function testFlow(param) {
  return request(`${HZERO_HRES}/v1/${getCurrentOrganizationId()}/process/validate`, {
    method: 'GET',
    query: { ...param, tenantId: getCurrentOrganizationId() },
  });
}
