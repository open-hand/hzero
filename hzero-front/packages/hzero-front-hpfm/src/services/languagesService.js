/**
 * service - 语言维护
 * @date: 2018-8-10
 * @author: YB <bo.yang02@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import request from 'utils/request';
import { HZERO_PLATFORM } from 'utils/config';
import {
  parseParameters,
  filterNullValueObject,
  getCurrentOrganizationId,
  isTenantRoleLevel,
} from 'utils/utils';

const tenantId = getCurrentOrganizationId();

// 处理Api
function languageApi() {
  return isTenantRoleLevel() ? `${tenantId}/languages` : 'languages';
}

/**
 *查询页面数据
 * @async
 * @function queryLanguage
 * @param {?string} params.searchCode - 语言编码
 * @param {Number} [params.page = 0] - 数据页码
 * @param {Number} [params.size = 10] - 分页大小
 * @returns {object} fetch Promise
 */
export async function queryLanguage(params) {
  const param = filterNullValueObject(parseParameters(params));
  return request(`${HZERO_PLATFORM}/v1/${languageApi()}`, {
    method: 'GET',
    query: param,
  });
}
/**
 *编辑行数据
 * @async
 * @function editLanguage
 * @param {!number} params.id - 语言Id
 * @param {!number} params.objectVersionNumber 语言版本好号
 * @param {!string} params.code - 语言编码
 * @param {!string} params.name - 语言名称
 * @param {!string} param.description - 语言描述
 * @returns {object} fetch Promise
 */
export async function editLanguage(params) {
  return request(`${HZERO_PLATFORM}/v1/${languageApi()}/${params.id}`, {
    method: 'PUT',
    body: params,
  });
}
