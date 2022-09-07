/*
 * WebHookConfigDS WebHook配置DS
 * @date: 2020-04-28
 * @author: LiLin <lin.li03@hand-china.com>
 * @copyright Copyright (c) 2020, Hand
 */
import intl from 'utils/intl';
import { CODE } from 'utils/regExp';
import { isTenantRoleLevel, getCurrentOrganizationId } from 'utils/utils';
import notification from 'utils/notification';
import { HZERO_MSG } from 'utils/config';

const organizationId = getCurrentOrganizationId();
const isTenant = isTenantRoleLevel();
const apiPrefix = isTenant ? `${HZERO_MSG}/v1/${organizationId}` : `${HZERO_MSG}/v1`;

// 表格ds
const tableDS = () => ({
  autoQuery: true,
  selection: false,
  dataKey: 'content',
  queryFields: [
    !isTenant && {
      name: 'tenantLov',
      lovCode: 'HPFM.TENANT',
      type: 'object',
      label: intl.get('hzero.common.model.common.tenantId').d('租户'),
      ignore: 'always',
      noCache: true,
    },
    !isTenant && {
      name: 'tenantId',
      type: 'string',
      bind: 'tenantLov.tenantId',
    },
    {
      name: 'serverCode',
      type: 'string',
      label: intl.get('hmsg.webhookConfig.view.webhookConfig.serverCode').d('接收方编码'),
    },
    {
      name: 'serverName',
      type: 'string',
      label: intl.get('hmsg.webhookConfig.view.webhookConfig.serverName').d('接收方名称'),
    },
  ].filter(Boolean),
  fields: [
    !isTenant && {
      name: 'tenantName',
      label: intl.get('hzero.common.model.tenantName').d('租户'),
      type: 'string',
    },
    {
      name: 'serverCode',
      label: intl.get('hmsg.webhookConfig.view.webhookConfig.serverCode').d('接收方编码'),
      type: 'string',
    },
    {
      name: 'serverName',
      type: 'string',
      label: intl.get('hmsg.webhookConfig.view.webhookConfig.serverName').d('接收方名称'),
    },
    {
      name: 'serverTypeMeaning',
      type: 'string',
      label: intl.get('hmsg.common.view.type').d('类型'),
    },
    {
      name: 'description',
      type: 'string',
      label: intl.get('hmsg.webhookConfig.model.webhookConfig.description').d('描述'),
    },
    {
      name: 'tenantId',
      type: 'string',
      label: intl.get('hmsg.common.view.source').d('来源'),
    },
    {
      name: 'enabledFlag',
      type: 'number',
      label: intl.get('hzero.common.status.enable').d('启用'),
    },
  ].filter(Boolean),
  transport: {
    read: () => ({
      url: `${apiPrefix}/webhook-servers`,
      method: 'GET',
    }),
    destroy: ({ data }) => {
      const { __id, _status, ...other } = Array.isArray(data) ? data[0] : {};
      return {
        data: other,
        url: `${apiPrefix}/webhook-servers`,
        method: 'DELETE',
      };
    },
  },
});

// 新建或编辑时的DS
const drawerDS = () => ({
  autoQuery: false,
  autoQueryAfterSubmit: false,
  fields: [
    !isTenant && {
      name: 'tenantLov',
      lovCode: 'HPFM.TENANT',
      required: true,
      type: 'object',
      label: intl.get('hzero.common.model.common.tenantId').d('租户'),
      ignore: 'always',
      noCache: true,
    },
    !isTenant && {
      name: 'tenantId',
      type: 'string',
      bind: 'tenantLov.tenantId',
    },
    !isTenant && {
      name: 'tenantName',
      type: 'string',
      bind: 'tenantLov.tenantName',
    },
    {
      name: 'serverCode',
      required: true,
      type: 'string',
      maxLength: 30,
      label: intl.get('hmsg.webhookConfig.view.webhookConfig.serverCode').d('接收方编码'),
      pattern: CODE,
      defaultValidationMessages: {
        patternMismatch: intl
          .get('hzero.common.validation.code')
          .d('大小写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
      },
    },
    {
      name: 'serverName',
      type: 'intl',
      required: true,
      label: intl.get('hmsg.webhookConfig.view.webhookConfig.serverName').d('接收方名称'),
      maxLength: 60,
    },
    {
      name: 'serverType',
      type: 'string',
      label: intl.get('hmsg.common.view.type').d('类型'),
      lookupCode: 'HMSG.WEBHOOK_TYPE',
      required: true,
    },
    {
      name: 'webhookAddress',
      type: 'url',
      required: true,
      label: intl.get('hmsg.webhookConfig.model.webhookConfig.webhookAddress').d('WebHook地址'),
      maxLength: 480,
      dynamicProps: {
        required({ record }) {
          return record.get('isCreate');
        },
      },
    },
    {
      name: 'secret',
      type: 'string',
      label: intl.get('hmsg.webhookConfig.model.webhookConfig.secret').d('秘钥'),
      maxLength: 240,
    },
    {
      name: 'description',
      type: 'string',
      label: intl.get('hmsg.webhookConfig.model.webhookConfig.description').d('描述'),
      maxLength: 240,
    },
    {
      name: 'enabledFlag',
      type: 'boolean',
      required: true,
      label: intl.get('hzero.common.status.enable').d('启用'),
      trueValue: 1,
      falseValue: 0,
      defaultValue: 1,
    },
    {
      name: 'isCreate', // 用于判断字段是否必输
      type: 'boolean',
      defaultValue: true,
      ignore: 'always',
    },
  ].filter(Boolean),
  transport: {
    read: ({ data }) => {
      const { serverId } = data;
      return {
        url: `${apiPrefix}/webhook-servers/${serverId}`,
        method: 'GET',
        data: {},
        params: {},
      };
    },
    create: ({ data }) => {
      const { __id, _status, ...other } = Array.isArray(data) ? data[0] : {};
      return {
        url: `${apiPrefix}/webhook-servers`,
        method: 'POST',
        data: other,
      };
    },
    update: ({ data }) => {
      const { __id, _status, ...other } = Array.isArray(data) ? data[0] : {};
      return {
        url: `${apiPrefix}/webhook-servers`,
        method: 'PUT',
        data: other,
      };
    },
  },
  feedback: {
    submitFailed: (error) => {
      if (error && error.failed) {
        notification.error({
          message: error.message,
        });
      }
    },
  },
});

export { tableDS, drawerDS };
