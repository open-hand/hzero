/**
 * 报文模板管理- service
 * @date: 2020-7-7
 * @author: LiLin <lin.li03@hand-china.com>
 * @copyright Copyright (c) 2020, Hand
 */

import request from 'utils/request';
import { isTenantRoleLevel, getCurrentOrganizationId } from 'utils/utils';
import { HZERO_HIOT, HZERO_PLATFORM } from 'utils/config';

const organizationId = getCurrentOrganizationId();
const isTenant = isTenantRoleLevel();
const apiPrefix = isTenant ? `${HZERO_HIOT}/v1/${organizationId}` : `${HZERO_HIOT}/v1`;

/**
 * 获取表单
 */
export async function fetchForm(params) {
  return request(`${apiPrefix}/thing-monitor/thing-property`, {
    method: 'GET',
    query: params,
  });
}

// 测试
export async function fetchTest(params = {}) {
  const { thingId, thingType, ...other } = params;
  return request(`${HZERO_HIOT}/v1/${organizationId}/msg-templates/test`, {
    method: 'POST',
    body: other,
    query: { thingId, thingType },
  });
}

/**
 * 获取模板配置提示信息详情
 */
export async function fetchMessageDetail(params) {
  return request(`${HZERO_PLATFORM}/v1/${organizationId}/static-texts/text-code`, {
    method: 'GET',
    query: params,
  });
}

// 获取模板字段默认预置内容
export async function fetchMsgContent(params) {
  return request(`${HZERO_HIOT}/v1/${organizationId}/msg-templates/format-ref`, {
    method: 'GET',
    query: params,
  });
}
