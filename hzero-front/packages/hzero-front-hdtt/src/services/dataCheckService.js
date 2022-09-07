/**
 * service - 数据核对
 * @date: 2019/7/28
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
 * 查询数据核对列表数据
 * @param {*} params.tableName 生产表名
 */
export async function queryList(params) {
  const param = parseParameters(params);
  return request(
    organizationRoleLevel
      ? `${HZERO_DTT}/v1/${organizationId}/data-chk-batchs`
      : `${HZERO_DTT}/v1/data-chk-batchs`,
    {
      method: 'GET',
      query: { ...param },
    }
  );
}

/**
 * 查询数据核对详情表单数据
 * @param {*} params.tableName 生产表名
 */
export async function queryDetailInfo(dataChkBatchLineId) {
  return request(
    organizationRoleLevel
      ? `${HZERO_DTT}/v1/${organizationId}/data-chk-batchs/${dataChkBatchLineId}`
      : `${HZERO_DTT}/v1/data-chk-batchs/${dataChkBatchLineId}`
  );
}

/**
 * 查询数据核对详情列表数据
 * @param {*} params.tableName 生产表名
 */
export async function queryDetailList(params) {
  const { dataChkBatchId, dataChkBatchLineId } = params;
  const param = parseParameters(params);
  return request(
    organizationRoleLevel
      ? `${HZERO_DTT}/v1/${organizationId}/data-chk-batchs/${dataChkBatchId}/${dataChkBatchLineId}`
      : `${HZERO_DTT}/v1/data-chk-batchs/${dataChkBatchId}/${dataChkBatchLineId}`,
    {
      method: 'GET',
      query: { ...param },
    }
  );
}

/**
 * 查询来源表数据
 * @param {number} params.page - 分页参数
 * @param {number} params.size - 分页参数
 */
export async function queryProducer(params) {
  const param = parseParameters(params);
  return request(
    organizationRoleLevel
      ? `${HZERO_DTT}/v1/${organizationId}/producer-config`
      : `${HZERO_DTT}/v1/producer-config`,
    {
      method: 'GET',
      query: { ...param },
    }
  );
}

/**
 * 查询目标表数据
 * @param {number} params.page - 分页参数
 * @param {number} params.size - 分页参数
 */
export async function queryConsumer(params) {
  const param = parseParameters(params);
  return request(
    organizationRoleLevel
      ? `${HZERO_DTT}/v1/${organizationId}/db-cons/paging`
      : `${HZERO_DTT}/v1/db-cons/paging`,
    {
      method: 'GET',
      query: { ...param },
    }
  );
}

/**
 * 发起核对
 * @param {object} data - 请求数据
 */
export async function confirmLaunch(data) {
  return request(
    organizationRoleLevel
      ? `${HZERO_DTT}/v1/${organizationId}/data-chk-batchs`
      : `${HZERO_DTT}/v1/data-chk-batchs`,
    {
      method: 'POST',
      body: data,
    }
  );
}
