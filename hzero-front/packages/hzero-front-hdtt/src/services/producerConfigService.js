/**
 * service - 数据消息生产消费配置
 * @date: 2019/4/16
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
 * 查询数据消息生产消费配置列表数据
 * @export
 * @param {string} params.tableName - 生产表名
 * @param {string} params.name - 服务名称
 * @param {string} params.tenantId - 租户名称
 * @param {number} params.page - 分页参数
 * @param {number} params.size - 分页参数
 * @returns
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
 * 删除生产消费配置
 * @param {object} params - 生产消费配置
 */
export async function deleteProducer(params) {
  const param = parseParameters(params);
  return request(
    organizationRoleLevel
      ? `${HZERO_DTT}/v1/${organizationId}/producer-config`
      : `${HZERO_DTT}/v1/producer-config`,
    {
      method: 'DELETE',
      body: { ...param },
    }
  );
}

/**
 * 查询配置头信息
 * @param {string} params - 数据生产消费配置ID
 */
export async function queryProducerDetail(params) {
  return request(
    organizationRoleLevel
      ? `${HZERO_DTT}/v1/${organizationId}/producer-config/${params.producerConfigId}`
      : `${HZERO_DTT}/v1/producer-config/${params.producerConfigId}`
  );
}

/**
 * 查询生产配置列表信息
 * @param {object} params - 参数数据
 */
export async function queryConsumerList(params) {
  const param = parseParameters(params);
  return request(
    organizationRoleLevel
      ? `${HZERO_DTT}/v1/${organizationId}/db-cons/${params.producerConfigId}/paging`
      : `${HZERO_DTT}/v1/db-cons/${params.producerConfigId}/paging`,
    {
      method: 'GET',
      query: { ...param },
    }
  );
}

/**
 * 新建生产消费配置头信息
 * @export
 * @param {*} data - 生产消费配置表单
 * @returns
 */
export async function saveProducer(data) {
  return request(
    organizationRoleLevel
      ? `${HZERO_DTT}/v1/${organizationId}/producer-config`
      : `${HZERO_DTT}/v1/producer-config`,
    {
      method: 'POST',
      body: data,
    }
  );
}

/**
 * 修改生产消费配置头信息
 * @export
 * @param {*} data - 生产消费配置表单
 * @returns
 */
export async function updateProducer(data) {
  return request(
    organizationRoleLevel
      ? `${HZERO_DTT}/v1/${organizationId}/producer-config`
      : `${HZERO_DTT}/v1/producer-config`,
    {
      method: 'PUT',
      body: data,
    }
  );
}

/**
 * 删除消费配置列
 * @param {object} data - 消费数据
 */
export async function deleteConsumer(data) {
  return request(
    organizationRoleLevel ? `${HZERO_DTT}/v1/${organizationId}/db-cons` : `${HZERO_DTT}/v1/db-cons`,
    {
      method: 'DELETE',
      body: data,
    }
  );
}

/**
 * 新建消费配置租户信息
 * @param {object} data - 消费数据
 */
export async function saveConsumer(data) {
  return request(
    organizationRoleLevel
      ? `${HZERO_DTT}/v1/${organizationId}/db-cons/${data.producerConfigId}`
      : `${HZERO_DTT}/v1/db-cons/${data.producerConfigId}`,
    {
      method: 'POST',
      body: data.payload,
    }
  );
}

/**
 * 修改消费配置信息
 * @param {object} data - 消费数据
 */
export async function updateConsumer(data) {
  return request(
    organizationRoleLevel
      ? `${HZERO_DTT}/v1/${organizationId}/db-cons/${data.consDbConfigId}`
      : `${HZERO_DTT}/v1/db-cons/${data.consDbConfigId}`,
    {
      method: 'PUT',
      body: data.payload,
    }
  );
}

/**
 * 查询消费租户列表
 * @param {object} params - 参数数据
 */
export async function queryConsumerTenantList(params) {
  const param = parseParameters(params);
  return request(
    organizationRoleLevel
      ? `${HZERO_DTT}/v1/${organizationId}/tenant-cons/${params.consDbConfigId}`
      : `${HZERO_DTT}/v1/tenant-cons/${params.consDbConfigId}`,
    {
      method: 'GET',
      query: { ...param },
    }
  );
}

/**
 * 删除租户列
 * @param {object} data - 租户数据
 */
export async function deleteTenantConsumer(data) {
  return request(
    organizationRoleLevel
      ? `${HZERO_DTT}/v1/${organizationId}/tenant-cons`
      : `${HZERO_DTT}/v1/tenant-cons`,
    {
      method: 'DELETE',
      body: data,
    }
  );
}

/**
 * 初始化消费者 包含db维度与租户维度
 * @param  {object} data - 初始化数据
 */
export async function initConsumer(data) {
  return request(
    organizationRoleLevel
      ? `${HZERO_DTT}/v1/${organizationId}/producer-cons-init`
      : `${HZERO_DTT}/v1/producer-cons-init`,
    {
      method: 'POST',
      body: data,
    }
  );
}

/**
 * 更新DDL语句
 * @param {number} producerConfigId - 生产消费配置ID
 */
export async function updateDdl(params) {
  const { producerConfig, producerConfigId } = params;
  return request(
    organizationRoleLevel
      ? `${HZERO_DTT}/v1/${organizationId}/producer-config/${producerConfigId}/refresh`
      : `${HZERO_DTT}/v1/producer-config/${producerConfigId}/refresh`,
    {
      method: 'PUT',
      body: producerConfig,
    }
  );
}

/**
 * 详情查询数据消息消费DB配置
 * @param {object} params - 参数数据
 */
export async function queryConsumerDbConfig(consDbConfigId) {
  return request(
    organizationRoleLevel
      ? `${HZERO_DTT}/v1/${organizationId}/db-cons/${consDbConfigId}`
      : `${HZERO_DTT}/v1/db-cons/${consDbConfigId}`
  );
}
