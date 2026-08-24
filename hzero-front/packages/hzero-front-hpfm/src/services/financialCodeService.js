/**
 * financialCodeService - 财务代码设置
 * @date: 2019-3-7
 * @author: lixiaolong <xiaolong.li02@hand-china.com>
 * @version: 0.0.1
 * @copyright: Copyright (c) 2019, Hand
 */
import { getCurrentOrganizationId, parseParameters, filterNullValueObject } from 'utils/utils';
import request from 'utils/request';
import { HZERO_PLATFORM } from 'utils/config';

const organizationId = getCurrentOrganizationId();

/**
 * 查询财务代码设置
 * @param {object} param - 查询参数对象
 */
export async function queryList(params) {
  const { type, ...rest } = filterNullValueObject(parseParameters(params));
  return request(`${HZERO_PLATFORM}/v1/${organizationId}/fin-codes`, {
    method: 'GET',
    query: { ...rest, type: (type || []).join(',') },
  });
}

/**
 * 保存创建的财务代码设置
 * @param {object} param - 新增的财务代码
 */
export async function saveCreate(params) {
  return request(`${HZERO_PLATFORM}/v1/${organizationId}/fin-codes`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 更新财务代码设置
 * @param {object} params - 修改的财务代码
 */
export async function saveUpdate(params) {
  return request(`${HZERO_PLATFORM}/v1/${organizationId}/fin-codes`, {
    method: 'PUT',
    body: params,
  });
}
