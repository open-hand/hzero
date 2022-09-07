/**
 * @since 2019-10-16
 * @author XL <liang.xiong@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import { HZERO_MSG, VERSION_IS_OP } from 'utils/config';

import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import intl from 'utils/intl';

export default () => ({
  queryFields: [
    !isTenantRoleLevel() && {
      name: 'tenantId',
      type: 'object',
      label: intl.get('entity.tenant.name').d('租户名称'),
      lovCode: 'HPFM.TENANT',
      noCache: true,
    },
    {
      name: 'serverCode',
      type: 'string',
      label: intl.get('hmsg.common.view.serverCode').d('配置编码'),
    },
    {
      name: 'serverName',
      type: 'string',
      label: intl.get('hmsg.common.view.serverName').d('配置名称'),
    },
    {
      name: 'authType',
      type: 'string',
      label: intl.get('hmsg.wechatOfficials.model.wechatOfficials.authType').d('授权类型'),
      lookupCode: 'HMSG.WECHAT.AUTH_TYPE',
    },
  ].filter(Boolean),
  fields: [
    {
      name: 'tenantName',
      type: 'string',
      label: intl.get('entity.tenant.name').d('租户名称'),
    },
    {
      name: 'serverCode',
      type: 'string',
      label: intl.get('hmsg.common.view.serverCode').d('配置编码'),
    },
    {
      name: 'serverName',
      type: 'string',
      label: intl.get('hmsg.common.view.serverName').d('配置名称'),
    },
    {
      name: 'authTypeMeaning',
      type: 'string',
      label: intl.get('hmsg.wechatOfficials.model.wechatOfficials.authType').d('授权类型'),
    },
    {
      name: 'appid',
      type: 'string',
      label: intl.get('hmsg.wechatOfficials.model.wechatOfficials.appid').d('用户凭证'),
    },
    isTenantRoleLevel() &&
      !VERSION_IS_OP && {
        name: 'tenantId',
        type: 'string',
        label: intl.get('hmsg.common.view.source').d('来源'),
      },
    {
      name: 'enabledFlag',
      type: 'boolean',
      label: intl.get('hmsg.common.view.enabledFlag').d('启用标识'),
      trueValue: 1,
      falseValue: 0,
      defaultValue: 1,
    },
  ],
  transport: {
    read: (config) => {
      const { data } = config;
      const { tenantId } = data;
      const url = isTenantRoleLevel()
        ? `${HZERO_MSG}/v1/${getCurrentOrganizationId()}/wechat-officials`
        : `${HZERO_MSG}/v1/wechat-officials`;
      return {
        ...config,
        data: {
          ...data,
          tenantId: tenantId && tenantId.tenantId,
        },
        url,
        method: 'GET',
      };
    },
    destroy: ({ data }) => {
      const url = isTenantRoleLevel()
        ? `${HZERO_MSG}/v1/${getCurrentOrganizationId()}/wechat-officials`
        : `${HZERO_MSG}/v1/wechat-officials`;
      return {
        data: data[0],
        url,
        method: 'DELETE',
      };
    },
  },
});
