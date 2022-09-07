/**
 * service - 我的抄送流程
 * @date: 2018-8-14
 * @version: 1.0.0
 * @author: WH <heng.wei@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import request from 'utils/request';
import { HZERO_HWFP } from 'utils/config';
import { parseParameters, filterNullValueObject, getCurrentOrganizationId } from 'utils/utils';

const tenantId = getCurrentOrganizationId();
const prefix = `${HZERO_HWFP}/v1/${tenantId}`;

/**
 * 查询流程
 */
export async function queryProcessList(params) {
  const query = filterNullValueObject(parseParameters(params));
  return request(`${prefix}/automatic-process`, {
    method: 'GET',
    query,
  });
}

// 批量删除流程
export async function deleteProcess(params) {
  return request(`${prefix}/automatic-process/batchDelete`, {
    method: 'POST',
    body: params,
  });
}

// 更新
export async function updateProcess(params) {
  return request(`${prefix}/automatic-process`, {
    method: 'PUT',
    body: params,
  });
}

// 创建
export async function createProcess(params) {
  return request(`${prefix}/automatic-process/batchCreate`, {
    method: 'POST',
    body: params,
  });
}
