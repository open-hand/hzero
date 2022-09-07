/**
 * 汇率定义-平台级
 */
import request from 'utils/request';
import { HZERO_PLATFORM } from 'utils/config';
import { isTenantRoleLevel, getCurrentOrganizationId } from 'utils/utils';

const tenantId = getCurrentOrganizationId();

function rateApi() {
  return isTenantRoleLevel() ? `${tenantId}/exchange-rates` : 'exchange-rates';
}

/**
 * 查询汇率定义的数据
 * @param {Object} params - 查询参数
 * @param {String} params.page - 页码
 * @param {String} params.size - 页数
 * @param {String} params.fromCurrencyCode - 币种代码
 * @param {String} params.fromCurrencyName - 币种名称
 * @param {String} params.toCurrencyCode - 兑换币种代码
 * @param {String} params.toCurrencyName - 兑换币种名称
 */
export async function fetchRateData(params) {
  return request(`${HZERO_PLATFORM}/v1/${rateApi()}`, {
    method: 'GET',
    query: params,
  });
}

/**
 * 新增汇率定义
 * @param {Object} params - 参数
 * @param {String} params.enabledFlag - 启用标识
 * @param {String} params.startDate - 起始时间
 * @param {String} params.endDate - 结束时间
 * @param {String} params.fromCurrencyCode - 币种代码
 * @param {String} params.fromCurrencyName - 币种名称
 * @param {String} params.toCurrencyCode - 兑换币种代码
 * @param {String} params.toCurrencyName - 兑换币种名称
 * @param {String} params.rateTypeCode - 汇率类型代码
 * @param {String} params.rateTypeName - 汇率类型名称
 * @param {String} params.rate - 汇率值
 */
export async function createRate(params) {
  return request(`${HZERO_PLATFORM}/v1/${rateApi()}`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 更新汇率定义
 * @param {Object} params - 参数
 * @param {String} params.enabledFlag - 启用标识
 * @param {String} params.startDate - 起始时间
 * @param {String} params.endDate - 结束时间
 * @param {String} params.fromCurrencyCode - 币种代码
 * @param {String} params.fromCurrencyName - 币种名称
 * @param {String} params.toCurrencyCode - 兑换币种代码
 * @param {String} params.toCurrencyName - 兑换币种名称
 * @param {String} params.rateTypeCode - 汇率类型代码
 * @param {String} params.rateTypeName - 汇率类型名称
 * @param {String} params.rate - 汇率值
 */
export async function updateRate(params) {
  return request(`${HZERO_PLATFORM}/v1/${rateApi()}`, {
    method: 'PUT',
    body: params,
  });
}

/**
 * 生成交叉汇率
 * @param {object} params - 参数
 * @param {String} params.currencyCode - 币种代码
 * @param {String} params.rateTypeCode - 汇率类型代码
 * @param {String} params.rateDate - 兑换日期
 * @returns
 */
export async function createCrossRate(params) {
  return request(`${HZERO_PLATFORM}/v1/${rateApi()}/cross-exchange-rate`, {
    method: 'POST',
    body: params,
  });
}
