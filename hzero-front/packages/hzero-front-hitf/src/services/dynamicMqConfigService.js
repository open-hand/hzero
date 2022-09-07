import request from 'utils/request';
import { HZERO_HITF } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';

const organizationId = getCurrentOrganizationId();
const organizationRoleLevel = isTenantRoleLevel();

/**
 * 批量激活绑定中间件
 * @param params
 * @returns {Promise<void>}
 */
export async function batchActivateBinder(params) {
  return request(
    organizationRoleLevel
      ? `${HZERO_HITF}/v1/${organizationId}/dynamic-mq-binders/activation`
      : `${HZERO_HITF}/v1/dynamic-mq-binders/activation`,
    {
      method: 'PATCH',
      body: params,
    }
  );
}

/**
 * 批量取消绑定中间件
 * @param params
 * @returns {Promise<void>}
 */
export async function batchDeactivateBinder(params) {
  return request(
    organizationRoleLevel
      ? `${HZERO_HITF}/v1/${organizationId}/dynamic-mq-binders/deactivation`
      : `${HZERO_HITF}/v1/dynamic-mq-binders/deactivation`,
    {
      method: 'PATCH',
      body: params,
    }
  );
}

/**
 * 启用绑定
 * @param params
 * @returns {Promise<void>}
 */
export async function enabledBinding(params) {
  return request(
    organizationRoleLevel
      ? `${HZERO_HITF}/v1/${organizationId}/dynamic-mq-bindings/enabled`
      : `${HZERO_HITF}/v1/dynamic-mq-bindings/enabled`,
    {
      method: 'PUT',
      body: params,
    }
  );
}

/**
 * 禁用绑定
 * @param params
 * @returns {Promise<void>}
 */
export async function disabledBinding(params) {
  return request(
    organizationRoleLevel
      ? `${HZERO_HITF}/v1/${organizationId}/dynamic-mq-bindings/disabled`
      : `${HZERO_HITF}/v1/dynamic-mq-bindings/disabled`,
    {
      method: 'PUT',
      body: params,
    }
  );
}

/**
 * 启用参数
 * @param params
 * @returns {Promise<void>}
 */
export async function enabledParam(params) {
  return request(
    organizationRoleLevel
      ? `${HZERO_HITF}/v1/${organizationId}/dynamic-mq-options/enabled`
      : `${HZERO_HITF}/v1/dynamic-mq-options/enabled`,
    {
      method: 'PUT',
      body: params,
    }
  );
}

/**
 * 禁用参数
 * @param params
 * @returns {Promise<void>}
 */
export async function disabledParam(params) {
  return request(
    organizationRoleLevel
      ? `${HZERO_HITF}/v1/${organizationId}/dynamic-mq-options/disabled`
      : `${HZERO_HITF}/v1/dynamic-mq-options/disabled`,
    {
      method: 'PUT',
      body: params,
    }
  );
}
