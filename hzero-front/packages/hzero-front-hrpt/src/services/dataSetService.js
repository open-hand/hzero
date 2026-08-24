/**
 * service - 报表平台/数据集
 * @date: 2018-11-19
 * @version: 1.0.0
 * @author: CJ <juan.chen01@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */

import request from 'utils/request';
import { HZERO_RPT } from 'utils/config';
import { parseParameters, getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';

const organizationId = getCurrentOrganizationId();
const organizationRoleLevel = isTenantRoleLevel();

/**
 * 请求API前缀
 * @type {string}
 */
const prefix = `${HZERO_RPT}/v1`;

/**
 * 数据查询
 * @async
 * @function fetchDataSetList
 * @param {object} params - 查询条件
 * @param {!string} params.tenantId - 租户ID
 * @param {!number} [params.page = 0] - 数据页码
 * @param {!number} [params.size = 10] - 分页大小
 * @returns {object} fetch Promise
 */
export async function fetchDataSetList(params) {
  const param = parseParameters(params);
  return request(
    organizationRoleLevel ? `${prefix}/${organizationId}/datasets` : `${prefix}/datasets`,
    {
      method: 'GET',
      query: { ...param },
    }
  );
}
/**
 * 初始化元数据
 * @async
 * @function getMetadata
 * @param {object} params - 查询条件
 * @returns {object} fetch Promise
 */
export async function getMetadata(params) {
  return request(
    organizationRoleLevel
      ? `${prefix}/${organizationId}/datasets/execute-sql`
      : `${prefix}/datasets/execute-sql`,
    {
      method: 'POST',
      body: { ...params },
    }
  );
}
/**
 * 初始化参数
 * @async
 * @function getParameters
 * @param {object} params - 查询条件
 * @returns {object} fetch Promise
 */
export async function getParameters(params) {
  return request(
    organizationRoleLevel
      ? `${prefix}/${organizationId}/datasets/extract-param`
      : `${prefix}/datasets/extract-param`,
    {
      method: 'POST',
      body: { ...params },
    }
  );
}
/**
 * 预览sql
 * @async
 * @function previewSql
 * @param {object} params - 查询条件
 * @returns {object} fetch Promise
 */
export async function previewSql(params) {
  return request(
    organizationRoleLevel
      ? `${prefix}/${organizationId}/datasets/preview-sql`
      : `${prefix}/datasets/preview-sql`,
    {
      method: 'POST',
      body: { ...params },
    }
  );
}
/**
 * 获取xml示例数据
 * @async
 * @function handleGetXmlSample
 * @param {object} params - 查询条件
 * @returns {object} fetch Promise
 */
export async function handleGetXmlSample(params) {
  return request(
    organizationRoleLevel
      ? `${prefix}/${organizationId}/datasets/xml-sample`
      : `${prefix}/datasets/xml-sample`,
    {
      method: 'POST',
      body: { ...params },
    }
  );
}
/**
 * 导出xml文件
 * @async
 * @function handleExportXmlFile
 * @param {object} params - 查询条件
 * @returns {object} fetch Promise
 */
export async function handleExportXmlFile(params) {
  return request(
    organizationRoleLevel
      ? `${prefix}/${organizationId}/datasets/xml-sample-file`
      : `${prefix}/datasets/xml-sample-file`,
    {
      method: 'POST',
      query: { ...params },
    }
  );
}
/**
 * 获取数据集明细
 * @async
 * @function fetchDataSetDetail
 * @param {object} params - 查询条件
 * @returns {object} fetch Promise
 */

export async function fetchDataSetDetail(params) {
  return request(
    organizationRoleLevel
      ? `${prefix}/${organizationId}/datasets/${params.datasetId}`
      : `${prefix}/datasets/${params.datasetId}`,
    {
      method: 'GET',
    }
  );
}
/**
 * 新增数据集
 * @async
 * @function createDataSet
 * @param {object} params - 请求参数
 * @param {!object} params.dto - 待保存对象
 */
export async function createDataSet(params) {
  return request(
    organizationRoleLevel ? `${prefix}/${organizationId}/datasets` : `${prefix}/datasets`,
    {
      method: 'POST',
      body: { ...params },
    }
  );
}

/**
 * 更新数据集
 * @async
 * @function updateDataSet
 * @param {object} params - 请求参数
 */
export async function updateDataSet(params) {
  return request(
    organizationRoleLevel ? `${prefix}/${organizationId}/datasets` : `${prefix}/datasets`,
    {
      method: 'PUT',
      body: { ...params },
    }
  );
}
/**
 * 删除数据集
 * @async
 * @function deleteHeader
 * @param {object} params - 请求参数
 */
export async function deleteDataSet(params) {
  return request(
    organizationRoleLevel ? `${prefix}/${organizationId}/datasets` : `${prefix}/datasets`,
    {
      method: 'DELETE',
      body: { ...params },
    }
  );
}

/**
 * 查询数据集关联报表
 * @async
 * @function fetchDataSetList
 * @param {object} params - 查询条件
 * @returns {object} fetch Promise
 */
export async function fetchAssignList(params) {
  const param = parseParameters(params);
  return request(
    organizationRoleLevel
      ? `${prefix}/${organizationId}/reports/${params.datasetId}/assign`
      : `${prefix}/reports/${params.datasetId}/assign`,
    {
      method: 'GET',
      query: { ...param },
    }
  );
}
