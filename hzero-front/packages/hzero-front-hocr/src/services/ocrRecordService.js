import { getCurrentOrganizationId, parseParameters } from 'utils/utils';
import request from 'utils/request';
import { HZERO_OCR, HZERO_HFLE } from 'utils/config';

const organizationId = getCurrentOrganizationId();

/**
 * 查询识别记录列表
 * @async
 * @function fetchOcrRecordList
 * @param {Object} params - 查询参数
 */
export async function fetchOcrRecordList(params) {
  const param = parseParameters(params);
  return request(`${HZERO_OCR}/v1/${organizationId}/records`, {
    method: 'GET',
    query: param,
  });
}

/**
 * 查询识别记录明细
 * @async
 * @function queryOcrRecordDetail
 * @param {Object} params - 查询参数
 * @param {String} params.recordId - 记录Id
 */
export async function fetchOcrRecordDetail(params) {
  return request(`${HZERO_OCR}/v1/${organizationId}/records/${params.recordId}`, {
    method: 'GET',
  });
}

/**
 * 识别列表
 * @async
 * @function fetchRecognizeList
 * @param {Object} params - 查询参数
 */
export async function fetchRecognizeList(params) {
  // const param = parseParameters(params);
  return request(`${HZERO_OCR}/v1/${organizationId}/record-details`, {
    method: 'GET',
    query: params,
  });
}

/**
 * 识别明细
 * @async
 * @function fetchRecognizeDetail
 * @param {Object} params - 查询参数
 */
export async function fetchRecognizeDetail(params) {
  return request(`${HZERO_OCR}/v1/${organizationId}/record-details/${params.recordDetailId}`, {
    method: 'GET',
  });
}

export async function redirect(params) {
  return request(`${HZERO_HFLE}/v1/${organizationId}/files/redirect-url`, {
    method: 'GET',
    query: params,
    responseType: 'blob',
  });
}

export async function fetchLicenseDetail(params) {
  return request(`${HZERO_OCR}/v1/${organizationId}/business-license/${params.recordDetailId}`, {
    method: 'GET',
  });
}

export async function fetchTextDetail(params) {
  return request(`${HZERO_OCR}/v1/${organizationId}/general-basic/${params.recordDetailId}`, {
    method: 'GET',
  });
}

export async function fetchIdDetail(params) {
  return request(`${HZERO_OCR}/v1/${organizationId}/id-card/${params.recordDetailId}`, {
    method: 'GET',
  });
}

export async function fetchTaxiDetail(params) {
  return request(`${HZERO_OCR}/v1/${organizationId}/taxi-receipts/${params.recordDetailId}`, {
    method: 'GET',
  });
}

export async function fetchTrainDetail(params) {
  return request(`${HZERO_OCR}/v1/${organizationId}/train-ticket/${params.recordDetailId}`, {
    method: 'GET',
  });
}

export async function fetchVatDetail(params) {
  const { vatInvoiceHeaderId } = params;
  return request(`${HZERO_OCR}/v1/${organizationId}/vat-invoice/${vatInvoiceHeaderId}`, {
    method: 'GET',
    // query: params,
  });
}

export async function fetchQuotaDetail(params) {
  return request(`${HZERO_OCR}/v1/${organizationId}/quota-invoice/${params.recordDetailId}`, {
    method: 'GET',
    query: params,
  });
}

export async function fetchMultiDetail(params) {
  return request(`${HZERO_OCR}/v1/${organizationId}/multi-image/${params.recordDetailId}`, {
    method: 'GET',
  });
}
