/**
 * service - CA证书管理
 * @date: 2019/9/10
 * @author: hulingfangzi <lingfangzi.hu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2019, Hand
 */
import request from 'utils/request';
import { HZERO_PLATFORM } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel, parseParameters } from 'utils/utils';

const organizationId = getCurrentOrganizationId();
const organizationRoleLevel = isTenantRoleLevel();

/**
 * 查询证书列表
 * @param {object} payload - 分页参数
 */
export async function queryList(payload = {}) {
  const param = parseParameters(payload);
  return request(
    organizationRoleLevel
      ? `${HZERO_PLATFORM}/v1/${organizationId}/certificates`
      : `${HZERO_PLATFORM}/v1/certificates`,
    {
      query: { ...param },
    }
  );
}

/**
 * 删除证书
 * @param {object} payload - 选中的证书
 */
export async function deleteCa(payload) {
  return request(
    organizationRoleLevel
      ? `${HZERO_PLATFORM}/v1/${organizationId}/certificates`
      : `${HZERO_PLATFORM}/v1/certificates`,
    {
      method: 'DELETE',
      body: payload,
    }
  );
}
