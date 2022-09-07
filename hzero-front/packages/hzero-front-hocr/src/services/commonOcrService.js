import { getCurrentOrganizationId } from 'utils/utils';
import request from 'utils/request';
import { HZERO_OCR } from 'utils/config';

const organizationId = getCurrentOrganizationId();

/**
 * OCR识别
 * @async
 * @function fetchOcrIdentifyDetail
 * @param {Object} params - 查询参数
 */
export async function fetchOcrIdentifyDetail(params) {
  return request(`${HZERO_OCR}/v1/${organizationId}/ocr`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 更新识别明细
 * @async
 * @function updateRecognizeDetail
 * @param {Object} params - 查询参数
 */
export async function updateRecognizeDetail(params) {
  return request(`${HZERO_OCR}/v1/${organizationId}/record-details/${params.recordDtlId}`, {
    method: 'PUT',
    body: params.resultInfo,
  });
}

/**
 * 更新增值税识别明细
 * @async
 * @function updateRecognizeDetail
 * @param {Object} params - 查询参数
 */
export async function updateVatRecognizeDetail(params) {
  return request(`${HZERO_OCR}/v1/${organizationId}/vat-invoice`, {
    method: 'PUT',
    body: params,
  });
}

/**
 * 更新文本识别结果
 * @param {*} params
 */
export async function updateTextRecognizeDetail(params) {
  return request(`${HZERO_OCR}/v1/${organizationId}/general-basic`, {
    method: 'PUT',
    body: params,
  });
}

export async function updateLicenseRecognizeDetail(params) {
  return request(`${HZERO_OCR}/v1/${organizationId}/business-license`, {
    method: 'PUT',
    body: params,
  });
}

export async function updateIdRecognizeDetail(params) {
  return request(`${HZERO_OCR}/v1/${organizationId}/id-card`, {
    method: 'PUT',
    body: params,
  });
}

export async function updateTrainRecognizeDetail(params) {
  return request(`${HZERO_OCR}/v1/${organizationId}/train-ticket`, {
    method: 'PUT',
    body: params,
  });
}

export async function updateTaxiRecognizeDetail(params) {
  return request(`${HZERO_OCR}/v1/${organizationId}/taxi-receipts`, {
    method: 'PUT',
    body: params,
  });
}

export async function updateQuotaRecognizeDetail(params) {
  return request(`${HZERO_OCR}/v1/${organizationId}/quota-invoice`, {
    method: 'PUT',
    body: params,
  });
}
