/**
 * @since 2019-12-20
 * @author WT <tao13.wang@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import request from 'utils/request';
import { HZERO_IAM, VERSION_IS_OP } from 'utils/config';
import { getCurrentOrganizationId } from 'utils/utils';

const organizationId = getCurrentOrganizationId();

/**
 * 清理缺失权限记录
 * @async
 * @function clearPermissionRecordInterface
 * @param {object} params - 参数对象
 */
export async function clearPermissionRecordInterface(params) {
  const { checkType, clearType } = params;
  const url = VERSION_IS_OP
    ? `${HZERO_IAM}/v1/${organizationId}/permission-check/clear?checkType=${checkType}&clearType=${clearType}`
    : `${HZERO_IAM}/v1/permission-check/clear?checkType=${checkType}&clearType=${clearType}`;
  return request(url, {
    method: 'DELETE',
  });
}

/**
 * 批量添加权限集
 * @async
 * @function refreshPermissionInterface
 * @param {object} params - 参数对象
 */
export async function patchAddPermissionInterface(params) {
  const url = VERSION_IS_OP
    ? `${HZERO_IAM}/v1/${organizationId}/permission-check/not-pass-api/batch-assign`
    : `${HZERO_IAM}/v1/permission-check/not-pass-api/batch-assign`;
  return request(url, {
    body: params,
    method: 'POST',
  });
}

/**
 * 刷新权限集合
 * @async
 * @function refreshPermissionSet
 * @param {object} params - 参数对象
 */
export async function refreshPermissionSet(params) {
  const url = VERSION_IS_OP
    ? `${HZERO_IAM}/v1/${organizationId}/permission-check/mismatch-api/batch-refresh`
    : `${HZERO_IAM}/v1/permission-check/mismatch-api/batch-refresh`;

  return request(url, {
    body: params,
    method: 'POST',
  });
}

/**
 * 刷新权限集合
 * @async
 * @function refreshPermissionSet
 * @param {object} params - 参数对象
 */
export async function refreshMenuPermissionSet(params) {
  const url = VERSION_IS_OP
    ? `${HZERO_IAM}/v1/${organizationId}/permission-check/menu-permission-api/batch-assign`
    : `${HZERO_IAM}/v1/permission-check/menu-permission-api/batch-assign`;

  return request(url, {
    body: params,
    method: 'POST',
  });
}
