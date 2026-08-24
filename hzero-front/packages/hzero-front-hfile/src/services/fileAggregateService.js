import request from 'utils/request';
import { HZERO_FILE } from 'utils/config';
import { isTenantRoleLevel } from 'utils/utils';

function apiSource(param) {
  return isTenantRoleLevel() ? `${param.organizationId}/files/summary` : `files/summary`;
}

/**
 * 查询文件汇总列表数据
 * @async
 * @function queryFileList
 * @param {object} param - 查询条件
 * @param {!number} [param.page = 0] - 数据页码
 * @param {!number} [param.size = 10] - 分页大小
 * @returns {object} fetch Promise
 */
export async function queryFileList(param) {
  const { organizationId, ...other } = param;
  return request(`${HZERO_FILE}/v1/${apiSource(param)}`, {
    method: 'GET',
    query: { ...other },
  });
}
