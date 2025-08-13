/**
 * 值集 值集视图
 * @email WY <yang.wang06@hand-china.com>
 * @creationDate 2019/12/25
 * @copyright HAND ® 2019
 */

import request from 'utils/request';
import { getEnvConfig } from 'utils/iocUtils';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';

const { HZERO_PLATFORM } = getEnvConfig();
/**
 * 统一查询独立、SQL、URL类型的值集
 * {HZERO_PLATFORM}/v1/lovs/data
 * @param {String} lovCode - 值集code
 * @param {Object} params - 额外的查询参数
 */
export async function queryUnifyIdpValue(lovCode, params = {}) {
  return request(
    `${HZERO_PLATFORM}/v1/${isTenantRoleLevel() ? `${getCurrentOrganizationId()}/` : ''}lovs/data`,
    {
      query: {
        lovCode,
        ...params,
      },
    }
  );
}

/**
 * 查询单个独立值集值
 * {HZERO_PLATFORM}/v1/lovs/value
 * @param {String} lovCode
 */
export async function queryIdpValue(lovCode) {
  return request(
    `${HZERO_PLATFORM}/v1/${isTenantRoleLevel() ? `${getCurrentOrganizationId()}/` : ''}lovs/value`,
    {
      query: {
        lovCode,
      },
    }
  );
}

/**
 * 批量查询独立值集值
 * {HZERO_PLATFORM}/v1/lovs/value/batch
 * @param {Object} params
 * @example queryMapIdpValue({ level: 'HPFM.LEVEL', dir: 'HPFM.DIRECTION' })
 */
export async function queryMapIdpValue(params) {
  const { publicMode, ...otherParams } = params;
  return request(
    `${HZERO_PLATFORM}/v1/${
      // eslint-disable-next-line no-nested-ternary
      publicMode ? 'pub/' : isTenantRoleLevel() ? `${getCurrentOrganizationId()}/` : ''
    }lovs/value/batch`,
    {
      query: otherParams,
    }
  );
}

// 没有用到的 api 先注释
// /**
//  * 根据父值集值查询子值集值
//  * {HZERO_PLATFORM}/v1/lovs/value/parent-value
//  * @param {Object} params
//  * @param {String} params.lovCode 子值集编码
//  * @param {String} params.parentValue 父值集值
//  */
// export async function queryParentIdpValue(params) {
//   return request(`${HZERO_PLATFORM}/v1/lovs/value/parent-value`, {
//     query: params,
//   });
// }

// 没有用到的 api 先注释
// /**
//  * 根据 tag 获取值集值
//  * {HZERO_PLATFORM}/v1/lovs/value/tag
//  * @param {Object} params
//  * @param {String} params.lovCode 值集编码
//  * @param {String} params.tag tag
//  */
// export async function queryTagIdpValue(params) {
//   return request(`${HZERO_PLATFORM}/v1/lovs/value/tag`, {
//     query: params,
//   });
// }
