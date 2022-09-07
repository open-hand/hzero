/*
 * @Descripttion: 事件源
 * @version: 0.0.1
 * @Author: heqiheng <qiheng.he@hand-china.com>
 * @Date: 2020-06-01 16:26:06
 * @Copyright: Copyright (c) 2020, Hand
 */
import request from 'utils/request';
// import { SRM_PLATFORM } from '_utils/config';
import { isTenantRoleLevel, getCurrentOrganizationId } from 'utils/utils';
import { API_PREFIX } from '@/utils/constants';

const organizationId = getCurrentOrganizationId();

function eventApi() {
  return isTenantRoleLevel()
    ? `${organizationId}/event-sources/test-connection`
    : `event-sources/test-connection`;
}

/**
 * 测试连接
 * @export
 * @param {object} params 查询参数
 * @returns
 */
export async function fetchConnectTest(params) {
  return request(`${API_PREFIX}/v1/${eventApi()}`, {
    method: 'POST',
    body: params,
  });
}
