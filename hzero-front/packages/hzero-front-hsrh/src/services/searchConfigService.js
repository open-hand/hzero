/**
 * 索引配置
 * @date: 2019-11-10
 * @author: WT <tao13.wang@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */

import request from 'utils/request';
import { HZERO_HSRH } from 'utils/config';
import { getCurrentOrganizationId } from 'utils/utils';

const organizationId = getCurrentOrganizationId();
/**
 * 列表查询
 * @async
 * @function fetchSync
 * @param {object} params - 同步字段
 * @returns {object} fetch Promise
 */
export async function fetchSync(params) {
  const { indexCode } = params;
  return request(`${HZERO_HSRH}/v1/${organizationId}/index-sync-configs/sync/${indexCode}`, {
    method: 'GET',
  });
}

/**
 * 编辑
 * @async
 * @function fetchEdit
 * @param {object} params - 编辑基本信息
 * @returns {object} fetch Promise
 */
export async function fetchEditBasicInformation(params) {
  return request(`${HZERO_HSRH}/v1/${organizationId}/indices`, {
    method: 'PUT',
    body: params,
  });
}

/**
 * 编辑
 * @async
 * @function fetchEditSyncFields
 * @param {object} params - 编辑同步字段
 * @returns {object} fetch Promise
 */
export async function fetchEditSyncFields(params) {
  return request(`${HZERO_HSRH}/v1/${organizationId}/index-fields`, {
    method: 'POST',
    body: params,
  });
}
