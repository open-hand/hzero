/**
 * @since 2019-10-16
 * @author XL <liang.xiong@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import { HZERO_MSG, VERSION_IS_OP } from 'utils/config';

import intl from 'utils/intl';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';

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
      label: intl.get('hmsg.wechatConfig.model.wechatConfig.authType').d('授权类型'),
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
      label: intl.get('hmsg.wechatConfig.model.wechatConfig.authType').d('授权类型'),
    },
    {
      name: 'corpid',
      type: 'string',
      label: intl.get('hmsg.wechatConfig.model.wechatConfig.corpid').d('企业ID'),
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
        ? `${HZERO_MSG}/v1/${getCurrentOrganizationId()}/wechat-enterprises`
        : `${HZERO_MSG}/v1/wechat-enterprises`;
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
        ? `${HZERO_MSG}/v1/${getCurrentOrganizationId()}/wechat-enterprises`
        : `${HZERO_MSG}/v1/wechat-enterprises`;
      return {
        data: data[0],
        url,
        method: 'DELETE',
      };
    },
  },
});
