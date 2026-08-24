import request from 'utils/request';
import { HZERO_FILE } from 'utils/config';
import { parseParameters, isTenantRoleLevel } from 'utils/utils';

function uploadApiSource(param) {
  return isTenantRoleLevel() ? `${param.tenantId}/upload-configs` : `upload-configs`;
}

function capacityApiSource(param) {
  return isTenantRoleLevel() ? `${param.tenantId}/capacity-configs` : `capacity-configs`;
}

/**
 * 查询文件大小配置列表数据
 * @async
 * @function queryFileList
 * @param {object} params - 查询条件
 * @param {!number} [params.page = 0] - 数据页码
 * @param {!number} [params.size = 10] - 分页大小
 * @returns {object} fetch Promise
 */
export async function queryFileList(params) {
  const { tenantId, ...others } = params;
  const param = parseParameters(others);
  return request(`${HZERO_FILE}/v1/${capacityApiSource(params)}`, {
    method: 'GET',
    query: { ...param, tenantId },
  });
}

/**
 * 保存文件上传头
 * @async
 * @function saveHeader
 * @param {object} params - 请求参数
 * @param {!object} params.contentType - 文件分类
 * @param {!string} params.storageSize - 文件大小限制
 * @param {?number} params.fileFormat - 文件格式
 * @param {!string} params.storageUnit - 文件大小单位
 * @param {!string} params.uploadConfigId - uploadConfigId
 * @param {!string} params.objectVersionNumber - 版本号
 * @returns {object} fetch Promise
 */
export async function saveHeader(params) {
  return request(`${HZERO_FILE}/v1/${capacityApiSource(params)}`, {
    method: 'PUT',
    body: params,
  });
}

/**
 * 添加文件上传详细配置
 * @async
 * @function addConfigDetail
 * @param {object} params - 请求参数
 * @param {!object} params.contentType - 文件分类
 * @param {!string} params.storageSize - 文件大小限制
 * @param {?number} params.fileFormat - 文件格式
 * @param {!string} params.storageUnit - 文件大小单位
 * @returns {object} fetch Promise
 */
export async function addConfigDetail(params) {
  return request(`${HZERO_FILE}/v1/${uploadApiSource(params)}`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 编辑文件上传详细配置
 * @async
 * @function editConfigDetail
 * @param {object} params - 请求参数
 * @param {!object} params.contentType - 文件分类
 * @param {!string} params.storageSize - 文件大小限制
 * @param {?number} params.fileFormat - 文件格式
 * @param {!string} params.storageUnit - 文件大小单位
 * @param {!string} params.uploadConfigId - uploadConfigId
 * @param {!string} params.objectVersionNumber - 版本号
 * @returns {object} fetch Promise
 */
export async function editConfigDetail(params) {
  return request(`${HZERO_FILE}/v1/${uploadApiSource(params)}`, {
    method: 'PUT',
    body: params,
  });
}

/**
 * 删除文件上传详细配置
 * @async
 * @function deleteConfigDetail
 * @param {object} params,uploadConfigId - 请求参数
 * @param {number} uploadConfigId - uploadConfigId
 * @returns {object} fetch Promise
 */
export async function deleteConfigDetail(params) {
  return request(`${HZERO_FILE}/v1/${uploadApiSource(params)}`, {
    method: 'DELETE',
    body: params,
  });
}

/**
 * 获取上传数据详情
 * @async
 * @function getUploadDetail
 * @param {object} params,uploadConfigId - 请求参数
 */
export async function getUploadDetail(params) {
  return request(`${HZERO_FILE}/v1/${uploadApiSource(params)}/${params.uploadConfigId}`, {
    method: 'GET',
    query: params,
  });
}
