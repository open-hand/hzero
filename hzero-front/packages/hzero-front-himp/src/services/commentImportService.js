import request from 'utils/request';
import { HZERO_IMP } from 'utils/config';
import { parseParameters, isTenantRoleLevel, getCurrentOrganizationId } from 'utils/utils';

/**
 * 模板头数据查询接口
 * @async
 * @function loadTemplate
 * @param {Object} params - 查询参数
 * @param {String} params.code - 模板编码
 */
export async function loadTemplate(params) {
  const { prefixPatch, code, tenantId } = params;
  const site = isTenantRoleLevel();
  const reqUrl = prefixPatch
    ? `${prefixPatch}/v1/${tenantId}/import/template/${code}/info`
    : `${HZERO_IMP}/v1/${site ? `${tenantId}/` : ''}template/${code}/info${
        !site ? `?tenantId=${getCurrentOrganizationId()}` : ''
      }`;
  return request(reqUrl, {
    method: 'GET',
  });
}

/**
 * 导入数据查询
 * @async
 * @function loadDataSource
 * @param {Object} params - 模板编码等必输信息
 * @param {String} params.prefixPatch - 客户端路径前缀
 * @param {String} params.templateCode - 模板编码
 * @param {String} params.batch - 批次编码
 * @param {Object} query - 查询参数
 */
export async function loadDataSource(params, query = {}) {
  const { templateCode, batch, prefixPatch, tenantId } = params;
  const reqUrl = `${prefixPatch}/v1/${tenantId}/import/data`;
  return request(reqUrl, {
    method: 'GET',
    query: {
      templateCode,
      batch,
      ...parseParameters(query),
    },
  });
}

/**
 * 导入数据验证
 * @async
 * @function validateData
 * @param {Object} params - 查询参数
 * @param {String} params.prefixPatch - 客户端路径前缀
 * @param {String} params.templateCode - 模板编码
 * @param {String} params.batch - 批次编码
 * @param {String} params.sync - 是否同步
 */
export async function validateData(params) {
  const { sync, templateCode, prefixPatch, batch, tenantId } = params;
  const reqUrl = sync
    ? `${prefixPatch}/v1/${tenantId}/import/data/sync/data-validate`
    : `${prefixPatch}/v1/${tenantId}/import/data/data-validate`;
  return request(reqUrl, {
    method: 'POST',
    query: {
      templateCode,
      batch,
    },
  });
}

/**
 * 导入数据到正式库
 * @async
 * @function importData
 * @param {Object} params - 查询参数
 * @param {String} params.templateCode - 模板编码
 * @param {String} params.prefixPatch - 客户端路径前缀
 * @param {String} params.batch - 批次编码
 * @param {String} params.sync - 是否同步
 */
export async function importData(params) {
  const { sync, prefixPatch, templateCode, batch, tenantId } = params;
  const reqUrl = sync
    ? `${prefixPatch}/v1/${tenantId}/import/data/sync/data-import`
    : `${prefixPatch}/v1/${tenantId}/import/data/data-import`;
  return request(reqUrl, {
    method: 'POST',
    query: {
      templateCode,
      batch,
    },
  });
}

/**
 * 导入单条数据到正式库
 * @async
 * @function updateOne
 * @param {Object} params - 查询参数
 * @param {String} params.prefixPatch - 客户端路径前缀
 * @param {String} params.data - 单条保存数据
 * @param {String} params.id - 单条保存数据 id
 */
export async function updateOne(params) {
  const { prefixPatch, _id: id, data, tenantId } = params;
  const reqUrl = `${prefixPatch}/v1/${tenantId}/import/data/${id}`;
  return request(reqUrl, {
    method: 'PUT',
    body: data,
  });
}

/**
 * 删除单挑数据
 * @async
 * @function removeOne
 * @param {Object} params - 查询参数
 * @param {String} params.prefixPatch - 客户端路径前缀
 * @param {String} params.data - 单条保存数据
 * @param {String} params.id - 单条保存数据 id
 */
export async function removeOne(params) {
  const { prefixPatch, _id: id, tenantId } = params;
  const reqUrl = `${prefixPatch}/v1/${tenantId}/import/data/${id}`;
  return request(reqUrl, {
    method: 'DELETE',
  });
}

/**
 * 状态查询
 * @async
 * @function queryStatus
 * @param {Object} params - 查询参数
 * @param {String} params.prefixPatch - 客户端路径前缀
 * @param {Object} query - 查询参数
 */
export async function queryStatus(params, query = {}) {
  const { prefixPatch, tenantId } = params;
  const reqUrl = `${prefixPatch}/v1/${tenantId}/import/data/status`;
  return request(reqUrl, {
    query,
  });
}

/**
 * 查询当前模板 导入历史
 * @param {object} params
 * @param {string} params.prefixPatch
 * @param {string} params.templateCode
 * @param query
 * @return {Promise<void>}
 */
export async function queryImportHistory(params, query = {}) {
  const { prefixPatch, templateCode, tenantId } = params;
  const reqUrl = `${prefixPatch}/v1/${tenantId}/import/manager`;
  return request(reqUrl, {
    query: {
      ...parseParameters(query),
      templateCode,
    },
    method: 'GET',
  });
}

/**
 * 删除 导入记录
 * @param {object} params
 * @param {object[]} records - 要删除的记录
 * @return {Promise<void>}
 */
export async function deleteImportHistory(params, records) {
  const { prefixPatch, tenantId } = params;
  const reqUrl = `${prefixPatch}/v1/${tenantId}/import/manager`;
  return request(reqUrl, {
    method: 'DELETE',
    body: records,
  });
}
