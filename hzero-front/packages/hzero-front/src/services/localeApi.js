/**
 * Move locale lazyLoad here according to YUNQIANG.WU's suggestion
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-11-07
 * @copyright HAND ® 2019
 */

import { cloneDeep, set } from 'lodash';

import { getCurrentOrganizationId, getResponse, resolveRequire } from 'utils/utils';

import { queryPromptLocale } from './api';

/**
 * 获取 ui 库 的多语言, 如果传了 language 和 promptKey, 那么使用传递的 多语言来覆盖 ui库多语言
 * @param {function()} loadFileIntl - 获取文件多语言的方法: () => Promise<any>
 * @param {string} language - 语言
 * @param {string} organizationId - 租户ID
 * @param {string} promptKey - 多语言编码前缀
 * @return {Promise<*>}
 */
export async function getFileIntl(loadFileIntl = () => {}, language, organizationId, promptKey) {
  const orgId = getCurrentOrganizationId() || organizationId || 0;
  let l = {};
  try {
    // 加载文件
    l = await loadFileIntl();
    l = cloneDeep(resolveRequire(l));
  } catch (e) {
    l = {};
  }
  let iRes = {};
  if (language && promptKey && orgId !== undefined) {
    // 加载 多语言
    iRes = await queryPromptLocale(orgId, language, promptKey);
    const i = getResponse(iRes) || {};
    const prefixLen = promptKey.length + 1;
    // 覆盖 ui库 多语言
    Object.keys(i).forEach((intlCode) => {
      set(l, intlCode.substr(prefixLen), i[intlCode]);
    });
  }
  return l;
}

/**
 * 获取antd的国际化
 * hzero-ui/lib/locale-provider/{language}.js
 * @param {String} language
 * @param {String} organizationId
 */
export async function getHzeroUILocale(language, organizationId) {
  return getFileIntl(
    () => import(`hzero-ui/es/locale-provider/${language.replace('-', '_')}.js`),
    language,
    organizationId,
    'hzero.hzeroUI'
  );
}

/**
 * 获取c7n的国际化
 * choerodon-ui/es/locale-provider/{language}.js
 * @param {String} language
 * @param {String} organizationId
 */
export async function getC7nLocale(language, organizationId) {
  return getFileIntl(
    () => import(`choerodon-ui/es/locale-provider/${language.replace('-', '_')}.js`),
    language,
    organizationId,
    'hzero.c7nUI'
  );
}

/**
 * 获取c7n-pro的国际化
 * choerodon-ui/pro/lib/locale-context/{language}.js
 * @param {String} language
 * @param {String} organizationId
 */
export async function getC7nProLocale(language, organizationId) {
  return getFileIntl(
    () => import(`choerodon-ui/pro/lib/locale-context/${language.replace('-', '_')}.js`),
    language,
    organizationId,
    'hzero.c7nProUI'
  );
}
