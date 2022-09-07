import request from 'utils/request';
import { HZERO_ADM } from 'utils/config';
import { clientRequest } from 'utils/market-client';

export function getImageUrl(data) {
  return request(`${HZERO_ADM}/v1/market/work-order/multipart`, {
    method: 'POST',
    body: data,
    type: 'FORM',
    processData: true, // 不会将 data 参数序列化字符串
  });
}

/**
 * 是否显示反馈按钮
 * @param query
 */
export function queryVisibleFeedback(query) {
  // 这个请求很特殊，只有有结果的时候才会显示按钮，否则静默失败
  return clientRequest(`${HZERO_ADM}/v1/market/config/feedback`, {
    method: 'GET',
    query,
  });
}

/**
 * 查询反馈一级分类
 * @param query
 */
export function queryCategory(query) {
  return request(`${HZERO_ADM}/v1/market/work-order/category`, {
    method: 'GET',
    query,
  });
}

/**
 * 查询反馈二级分类
 * @param query
 */
export function querySubCategory(query) {
  return request(`${HZERO_ADM}/v1/market/work-order/handler/page`, {
    method: 'GET',
    query,
  });
}

/**
 * 查询服务对应关系
 * @param query
 */
export function queryMapCategory(query) {
  return request(`${HZERO_ADM}/v1/market/work-order/mapping`, {
    method: 'GET',
    query,
  });
}

/**
 * 提交工单
 * @param data
 */
export function submitOrder(data) {
  return request(`${HZERO_ADM}/v1/market/work-order/submit`, {
    method: 'POST',
    body: data,
  });
}

/**
 * 上传附件
 * @param data
 */
export function uploadOrder(data) {
  return request(`${HZERO_ADM}/v1/market/work-order/secret-multipart`, {
    method: 'POST',
    body: data,
  });
}

/**
 * 将字符串保存为文件
 * @param {*} param
 * @param {string} param.fileName - 文件名(非必须)
 * @param {string} param.content - 字符串格式的文件内容
 */
export async function saveAsFile({ fileName, content }) {
  const formData = new FormData();
  formData.append('bucketName', 'doc');
  formData.append('directory', 'doc_classify');

  const blob = new Blob([content]);
  const file = new window.File([blob], fileName);
  formData.append('file', file);
  const res = await request(`${HZERO_ADM}/v1/market/work-order/secret-multipart`, {
    method: 'POST',
    body: formData,
    type: 'FORM',
    processData: false, // 不会将 data 参数序列化字符串
  });

  if (res && res.failed) return false;
  return { data: { fileKey: res.fileKey, fileName } };
}

/**
 * 获取跳转链接
 * @returns {*}
 */
export function queryDisplayUrl() {
  return request(`${HZERO_ADM}/v1/market/work-order/display-url`, {
    method: 'GET',
  });
}

/**
 * 查询所属项目
 * @returns {*}
 */
export function queryProject() {
  return request(`${HZERO_ADM}/v1/market/work-order/project`, {
    method: 'GET',
  });
}

/**
 * 查询紧急程度
 * @returns {*}
 */
export function queryPriority(params) {
  return request(`${HZERO_ADM}/v1/market/work-order/enabled-priority`, {
    method: 'GET',
    query: params,
  });
}

/**
 * 查询所属租户
 * @returns {*}
 */
export function queryOrganization() {
  return request(`${HZERO_ADM}/v1/market/work-order/self-tenants`, {
    method: 'GET',
  });
}

/**
 * 查询Hzero版本
 * @returns {*}
 */
export function queryHzeroVersion() {
  return request(`${HZERO_ADM}/v1/market/work-order/current-version`, {
    method: 'GET',
  });
}

/**
 * 开始收集日志
 * @returns {*}
 */
export function startTraceLogs() {
  return request(`${HZERO_ADM}/v1/market/work-order/trace/start`, {
    method: 'GET',
  });
}

/**
 * 结束日志
 * @returns {*}
 */
export function endTraceLogs() {
  return request(`${HZERO_ADM}/v1/market/work-order/trace/end`, {
    method: 'GET',
  });
}

/**
 * 查询日志收集的状态
 * @returns {*}
 */
export function queryTraceLogsStatus() {
  return request(`${HZERO_ADM}/v1/market/work-order/trace/status`, {
    method: 'GET',
  });
}
