/**
 * API - 全局通用 API
 * @date: 2018-6-20
 * @author: niujiaqing <njq.niu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import request from 'utils/request';
import { getEnvConfig } from 'utils/iocUtils';
import {
  getCurrentOrganizationId,
  getCurrentRole,
  getResponse,
  // isTenantRoleLevel,
  isEqualOrganization,
} from 'utils/utils';

const {
  HZERO_IAM,
  HZERO_PLATFORM,
  HZERO_MNT,
  TOP_MENU_LABELS,
  TOP_MENU_UNION_LABEL,
} = getEnvConfig();

// primary 私有的api(组件, 页面无关的api)

export interface HistoryParams {
  auditDocumentId: string;
  businessKey: string;
  page: number;
  size: number;
}

/**
 * 查询菜单.
 * {HZERO_IAM}/hzero/v1/menus/tree
 * @param {object} query - 现在菜单会包含语言
 */
export async function queryMenu(query = {}) {
  const role = getCurrentRole();
  const params =
    TOP_MENU_LABELS && TOP_MENU_LABELS !== 'undefined'
      ? {
          ...query,
          roldId: role.id,
          labels: TOP_MENU_LABELS,
          unionLabel: TOP_MENU_UNION_LABEL === 'undefined' ? false : TOP_MENU_UNION_LABEL || false,
        }
      : {
          ...query,
          roldId: role.id,
          unionLabel: TOP_MENU_UNION_LABEL === 'undefined' ? false : TOP_MENU_UNION_LABEL || false,
        };
  return request(`${HZERO_IAM}/hzero/v1/menus/tree`, {
    method: 'GET',
    query: params,
  });
}

/**
 * 查询平台多语言国际化
 * {HZERO_PLATFORM}/v1/{organizationId}/prompt/{language}
 * @param {Number} organizationId
 * @param {String} language
 * @param {String} promptKey - 这里只用到了 hzero.common
 */
export async function queryPromptLocale(organizationId, language, promptKey) {
  return request(
    `${HZERO_PLATFORM}/v1/${organizationId}/prompt/${language}?promptKey=${promptKey}`
  );
}

/**
 * public路由下查询平台多语言国际化
 * {HZERO_PLATFORM}/v1/{organizationId}/prompt/{language}
 * @param {Number} organizationId
 * @param {String} language
 * @param {String} promptKey - 这里只用到了 hzero.common
 */
export async function queryPublicPromptLocale(language, promptKey) {
  return request(`${HZERO_PLATFORM}/v1/prompt/${language}?promptKey=${promptKey}`);
}

/**
 * 查询 LOV 配置.
 * {HZERO_PLATFORM}/v1/lov-view/info
 * @param {Object} params 参数
 */
export async function queryLov(params) {
  const { publicMode, ...otherParams } = params;
  const res = request(
    `${HZERO_PLATFORM}/v1/${
      // eslint-disable-next-line no-nested-ternary
      publicMode ? 'pub/' : isEqualOrganization() ? `${getCurrentOrganizationId()}/` : ''
    }lov-view/info`,
    {
      method: 'GET',
      query: otherParams,
    }
  );
  // FIXME: @WJC utils need fix
  // @ts-ignore
  return getResponse(res);
}

/**
 * 查询 LOV 数据.
 * @param {string} url URL
 * @param {Object} params 参数
 */
export async function queryLovData(url, params, method) {
  const options: {
    method?: string;
    body?: any;
    query?: any;
  } = {};
  if (method) {
    options.method = method.toUpperCase();
    if (method.toUpperCase() !== 'GET') {
      options.body = params;
    } else {
      options.query = params;
    }
  } else {
    options.query = params;
  }
  const res = request(url, options);

  // FIXME: @WJC utils need fix
  // @ts-ignore
  return getResponse(res);
}

/**
 * 查询动态表格
 * @param {object} query
 * @returns {Promise<void>}
 */
export async function queryUiTables(query = {}) {
  const organizationId = getCurrentOrganizationId();
  return request(`${HZERO_PLATFORM}/v1/${organizationId}/ui-table`, {
    method: 'GET',
    query,
  });
}

/**
 * 查询动态表格
 * @param {object} query
 * @returns {Promise<void>}
 */
export async function queryHistoryData(query: HistoryParams) {
  const organizationId = getCurrentOrganizationId();
  const url = `${HZERO_MNT}/v1/${organizationId}/audit-documents/log/detail`;
  return request(url, {
    method: 'GET',
    query,
  });
}

// primary 私有的api(组件, 页面无关的api)
