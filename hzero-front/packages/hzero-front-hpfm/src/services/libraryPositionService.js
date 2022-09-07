/*
 * service - 库位
 * @date: 2018-8-10
 * @author: YB <bo.yang02@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import request from 'utils/request';
import { HZERO_PLATFORM } from 'utils/config';
import { parseParameters, filterNullValueObject } from 'utils/utils';

/**
 *查询库房
 * @async
 * @function queryLibPosition
 * @param {?string} params.locationCode 库房编码
 * @param {?string} params.locationName 库房名称
 * @param {?string} params.ouName 业务实体
 * @param {Number} [params.page = 0] - 数据页码
 * @param {Number} [params.size = 10] - 分页大小
 * @returns {object} fetch Promise
 */
export async function queryLibPosition(params) {
  const { organizationId, ...otherParams } = params;
  const param = filterNullValueObject(parseParameters(otherParams));
  return request(`${HZERO_PLATFORM}/v1/${organizationId}/locations`, {
    method: 'GET',
    query: param,
  });
}

/**
 *保存库位数据
 * @async
 * @function saveLibraryPosition
 * @param {object} params
 * @param {!string} params.organizationId - 租户Id
 * @param {!Array<Object>} params.payloadData - 待保存的数据信息
 * @returns {object} fetch Promise
 */
export async function saveLibraryPosition(params) {
  const { payloadData, organizationId } = params;
  return request(`${HZERO_PLATFORM}/v1/${organizationId}/locations/batch-save`, {
    method: 'POST',
    body: payloadData,
  });
}
