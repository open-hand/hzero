/**
 * currencyService - 币种定义Service
 * @date: 2018-7-3
 * @author: lokya <kan.li01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import request from 'utils/request';
import { HZERO_PLATFORM } from 'utils/config';
import { parseParameters, isTenantRoleLevel, getCurrentOrganizationId } from 'utils/utils';

const tenantId = getCurrentOrganizationId();

function currencyApi() {
  return isTenantRoleLevel() ? `${tenantId}/currencys` : `currencys`;
}

/**
 * 货币类型查询
 * @async
 * @function queryCurrency
 * @param {object} params - 查询条件
 * @param {?string} params.currencyCode - 币种编码
 * @param {?string} params.currencyName - 币种名称
 * @param {?string} params.countryName - 国家/地区名称
 * @param {!number} [params.page = 0] - 数据页码
 * @param {!number} [params.size = 10] - 分页大小
 * @returns {object} fetch Promise
 */
export async function queryCurrency(params) {
  const param = parseParameters(params);
  return request(`${HZERO_PLATFORM}/v1/${currencyApi(params)}`, {
    method: 'GET',
    query: param,
  });
}

/**
 * 币种新增
 * @async
 * @function addCurrency
 * @param {object} params.data - 待保存数据
 * @param {!string} params.data.currencyCode - 货币编码
 * @param {!string} params.data.currencyName - 货币名称
 * @param {!string} params.data.countryId - 国家/地区编码
 * @param {!number} params.data.financialPrecision - 财务精度
 * @param {!number} params.data.defaultPrecision - 精度
 * @param {!string} params.data.currencySymbol - 货币符号
 * @param {!number} params.data.enabledFlag - 启用标记
 * @returns {object} fetch Promise
 */
export async function addCurrency(params) {
  return request(`${HZERO_PLATFORM}/v1/${currencyApi(params)}`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 币种编辑
 * @async
 * @function updateCurrency
 * @param {object} params.data - 待保存数据
 * @param {!string} params.data.currencyId - 货币id
 * @param {!string} params.data.currencyCode - 货币编码
 * @param {!string} params.data.currencyName - 货币名称
 * @param {!string} params.data.countryId - 国家/地区编码
 * @param {!number} params.data.financialPrecision - 财务精度
 * @param {!number} params.data.defaultPrecision - 精度
 * @param {!string} params.data.currencySymbol - 货币符号
 * @param {!number} params.data.enabledFlag - 启用标记
 * @returns {object} fetch Promise
 */
export async function updateCurrency(params) {
  return request(`${HZERO_PLATFORM}/v1/${currencyApi(params)}`, {
    method: 'PUT',
    body: params,
  });
}

/**
 * 币种引用明细查询-将废弃
 * @async
 * @function currencyDetail
 * @param {object} params - 查询数据
 * @param {!string} params.currencyId - 货币id
 * @param {!number} [params.page = 0] - 数据页码
 * @param {!number} [params.size = 10] - 分页大小
 * @returns {object} fetch Promise
 */
export async function currencyDetail(params) {
  return request(`${HZERO_PLATFORM}/v1/${currencyApi(params)}?tenantId=${params.tenantId}`, {
    method: 'GET',
    query: params,
  });
}

/**
 * 币种引用明细查询
 * @async
 * @function fetchDetail
 * @param {object} params - 查询数据
 * @param {!string} params.currencyId - 货币id
 * @returns {object} fetch Promise
 */
export async function fetchDetail(params) {
  return request(`${HZERO_PLATFORM}/v1/${currencyApi(params)}/${params.currencyId}`, {
    method: 'GET',
    query: params,
  });
}
