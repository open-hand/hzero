import request from 'utils/request';
import { HZERO_FILE } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';

const organizationId = getCurrentOrganizationId();

function apiSource() {
  return isTenantRoleLevel() ? `${organizationId}/file-edit-logs` : `file-edit-logs`;
}

/**
 * 查询文件汇总列表数据
 * @async
 * @function queryLogList
 * @param {object} params - 查询条件
 */
export async function queryLogList(params) {
  return request(`${HZERO_FILE}/v1/${apiSource()}`, {
    method: 'GET',
    query: params,
  });
}
