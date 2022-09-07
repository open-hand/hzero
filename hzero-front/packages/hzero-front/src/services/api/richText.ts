/**
 * @email WY <yang.wang06@hand-china.com>
 * @creationDate 2019/12/25
 * @copyright HAND ® 2019
 */

import request from 'utils/request';
import { getEnvConfig } from 'utils/iocUtils';
import { getCurrentLanguage, getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';

const { HZERO_PLATFORM } = getEnvConfig();
/**
 * 查询静态文本数据 不会报 400 异常, 异常全是 200
 * 需要自己处理异常(自己调用getResponse)
 * {HZERO_PLATFORM}/v1/static-texts/text/by-code/nullable
 * @param {string} textCode - 静态文本编码
 * @param {string} [lang=currentLanguage] - 语言 默认是系统当前语言
 * @return {Promise}
 */
export async function queryStaticText(textCode, lang = getCurrentLanguage()) {
  const organizationId = getCurrentOrganizationId();
  const organizationRoleLevel = isTenantRoleLevel();
  return request(
    `${HZERO_PLATFORM}/v1/${
      organizationRoleLevel ? `${organizationId}/` : ''
    }static-texts/text/by-code/nullable`,
    {
      query: {
        textCode,
        lang,
      },
      method: 'GET',
    }
  );
}

/**
 * 获取模板配置提示信息详情
 */
export async function queryToolTip(params) {
  const organizationId = getCurrentOrganizationId();
  return request(
    isTenantRoleLevel()
      ? `${HZERO_PLATFORM}/v1/${organizationId}/static-texts/text-code`
      : `${HZERO_PLATFORM}/v1/static-texts/text-code`,
    {
      method: 'GET',
      query: params,
    }
  );
}
