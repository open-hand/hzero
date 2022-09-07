import intl from 'utils/intl';
import { getCurrentOrganizationId, isTenantRoleLevel, encryptPwd } from 'utils/utils';
import { HZERO_MSG } from 'utils/config';
import { CODE_UPPER } from 'utils/regExp';

export default () => {
  function wechatCodeValidator(value) {
    if (CODE_UPPER.test(value)) {
      return true;
    }
    return intl
      .get('hzero.common.validation.codeUpper')
      .d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”');
  }
  return {
    autoQueryAfterSubmit: false,
    fields: [
      !isTenantRoleLevel() && {
        name: 'tenantIdLov',
        type: 'object',
        label: intl.get('entity.tenant.name').d('租户名称'),
        lovCode: 'HPFM.TENANT',
        required: true,
        ignore: 'always',
        noCache: true,
      },
      !isTenantRoleLevel() && {
        name: 'tenantName',
        type: 'string',
        bind: 'tenantIdLov.tenantName',
        required: true,
      },
      !isTenantRoleLevel() && {
        name: 'tenantId',
        type: 'string',
        bind: 'tenantIdLov.tenantId',
        required: true,
      },
      {
        name: 'serverCode',
        type: 'string',
        label: intl.get('hmsg.common.view.serverCode').d('配置编码'),
        required: true,
        maxLength: 30,
        validator: wechatCodeValidator,
      },
      {
        name: 'serverName',
        type: 'intl',
        label: intl.get('hmsg.common.view.serverName').d('配置名称'),
        required: true,
        maxLength: 30,
      },
      {
        name: 'authType',
        type: 'string',
        label: intl.get('hmsg.wechatConfig.model.wechatConfig.authType').d('授权类型'),
        lookupCode: 'HMSG.WECHAT.AUTH_TYPE',
        required: true,
      },
      {
        name: 'corpid',
        type: 'string',
        label: intl.get('hmsg.wechatConfig.model.wechatConfig.corpid').d('企业ID'),
        dynamicProps: ({ record }) => {
          if (record.get('authType') === 'WeChat') {
            return {
              required: true,
            };
          }
        },
        maxLength: 60,
      },
      {
        name: 'agentId',
        type: 'number',
        label: intl.get('hmsg.wechatConfig.model.wechatConfig.agentId').d('默认应用ID'),
        maxLength: 240,
      },
      {
        name: 'corpsecret',
        type: 'string',
        label: intl.get('hmsg.wechatConfig.model.wechatConfig.corpsecret').d('凭证密钥'),
        maxLength: 120,
      },
      {
        name: 'authAddress',
        type: 'string',
        label: intl.get('hmsg.wechatConfig.model.wechatConfig.authAddress').d('第三方授权地址'),
        dynamicProps: ({ record }) => {
          if (record.get('authType') === 'Third') {
            return {
              required: true,
            };
          }
        },
        maxLength: 240,
      },
      {
        name: 'enabledFlag',
        type: 'boolean',
        label: intl.get('hmsg.common.view.enabledFlag').d('启用标识'),
        trueValue: 1,
        falseValue: 0,
        defaultValue: 1,
        required: true,
      },
    ],
    transport: {
      create: ({ config, data, dataSet }) => {
        const [{ __id, _status, ...rest }] = data;
        const { publicKey } = dataSet.queryParameter;
        const newData = { ...rest };
        if (rest.corpsecret) {
          newData.corpsecret = encryptPwd(rest.corpsecret, publicKey);
        }
        const url = isTenantRoleLevel()
          ? `${HZERO_MSG}/v1/${getCurrentOrganizationId()}/wechat-enterprises`
          : `${HZERO_MSG}/v1/wechat-enterprises`;
        return {
          ...config,
          url,
          method: 'POST',
          data: {
            ...newData,
          },
        };
      },
      update: ({ config, data, dataSet }) => {
        const newData = Array.isArray(data) ? data[0] : {};
        const { publicKey } = dataSet.queryParameter;
        if (newData.corpsecret) {
          newData.corpsecret = encryptPwd(newData.corpsecret, publicKey);
        }

        const url = isTenantRoleLevel()
          ? `${HZERO_MSG}/v1/${getCurrentOrganizationId()}/wechat-enterprises`
          : `${HZERO_MSG}/v1/wechat-enterprises`;
        return {
          ...config,
          url,
          method: 'PUT',
          data: newData,
        };
      },
      read: ({ dataSet }) => {
        const { serverId } = dataSet;
        const url = isTenantRoleLevel()
          ? `${HZERO_MSG}/v1/${getCurrentOrganizationId()}/wechat-enterprises/${serverId}`
          : `${HZERO_MSG}/v1/wechat-enterprises/${serverId}`;
        return {
          url,
          method: 'GET',
          params: '',
        };
      },
    },
  };
};
