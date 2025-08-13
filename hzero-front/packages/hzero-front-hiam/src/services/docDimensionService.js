/**
 * docDimension-单据维度
 * @date: 2019-09-19
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */

import request from 'utils/request';
import { parseParameters, getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import { HZERO_IAM } from 'utils/config';

const organizationId = getCurrentOrganizationId();

/**
 * 查询单据维度定义
 * @param {Object} params - 查询参数
 */
export async function query(params) {
  return request(
    `${HZERO_IAM}/v1/${isTenantRoleLevel() ? `${organizationId}/` : ``}doc-type/dimensions`,
    {
      method: 'GET',
      query: parseParameters(params),
    }
  );
}

/**
 * 查询单据维度定义详情
 * @param {Object} params - 查询参数
 */
export async function queryDetail(params) {
  const { dimensionId } = params;
  return request(
    `${HZERO_IAM}/v1/${
      isTenantRoleLevel() ? `${organizationId}/` : ``
    }doc-type/dimensions/${dimensionId}`,
    {
      method: 'GET',
    }
  );
}

/**
 * 新建单据维度
 * @param {Object} params - 查询参数
 */
export async function create(params) {
  return request(
    `${HZERO_IAM}/v1/${isTenantRoleLevel() ? `${organizationId}/` : ``}doc-type/dimensions`,
    {
      method: 'POST',
      body: params,
    }
  );
}

/**
 * 修改单据维度
 * @param {Object} params - 查询参数
 */
export async function update(params) {
  return request(
    `${HZERO_IAM}/v1/${isTenantRoleLevel() ? `${organizationId}/` : ``}doc-type/dimensions`,
    {
      method: 'PUT',
      body: params,
    }
  );
}
