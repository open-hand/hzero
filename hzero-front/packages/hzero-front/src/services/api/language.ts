/**
 * @email WY <yang.wang06@hand-china.com>
 * @creationDate 2019/12/25
 * @copyright HAND ® 2019
 */

import request from 'utils/request';
import { getEnvConfig, BaseEnvConfig } from 'utils/iocUtils';

/**
 * 查询TL多语言
 * {HZERO_PLATFORM}/v1/multi-language
 * @export
 * @param {object} params - 查询参数
 * @param {string} params.fieldName - 查询的表单域名称
 * @param {string} params._token - token
 */
export async function queryTL(params) {
  const { HZERO_PLATFORM } = getEnvConfig<BaseEnvConfig>();
  return request(`${HZERO_PLATFORM}/v1/multi-language`, {
    method: 'GET',
    query: params,
  });
}

/**
 * 变更用户的默认语言选项
 * @export
 * {HZERO_IAM}/hzero/v1/users/default-language
 * @function updateDefaultLanguage
 * @param {!string} params.languageCode - 语言编码
 * @returns fetch Promise
 */
export async function updateDefaultLanguage(params) {
  const { HZERO_IAM } = getEnvConfig<BaseEnvConfig>();
  return request(`${HZERO_IAM}/hzero/v1/users/default-language`, {
    method: 'PUT',
    query: params,
  });
}
