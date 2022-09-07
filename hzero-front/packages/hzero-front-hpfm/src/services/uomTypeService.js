/*
 * uomTypeService - 单位类型定义
 * @date: 2018/10/13 10:42:04
 * @author: HB <bin.huang02@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import request from 'utils/request';
import { HZERO_PLATFORM } from 'utils/config';
import { parseParameters, getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';

/**
 * 查询单位类型定义列表
 */
export async function fetchUomList(params) {
  return request(
    `${HZERO_PLATFORM}/v1${isTenantRoleLevel() ? `/${getCurrentOrganizationId()}` : ''}/uom-types`,
    {
      method: 'GET',
      query: parseParameters(params),
    }
  );
}
/**
 * @export
 * @param {*} params
 * @returns 请求添加单位类型定义的结果
 */
export async function addUom(params) {
  return request(
    `${HZERO_PLATFORM}/v1${isTenantRoleLevel() ? `/${getCurrentOrganizationId()}` : ''}/uom-types`,
    {
      method: 'POST',
      body: [params],
    }
  );
}
