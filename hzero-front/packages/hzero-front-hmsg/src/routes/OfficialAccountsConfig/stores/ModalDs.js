import intl from 'utils/intl';
import { getCurrentOrganizationId, isTenantRoleLevel, encryptPwd } from 'utils/utils';
import { HZERO_MSG } from 'utils/config';
import { CODE_UPPER } from 'utils/regExp';

export default () => {
  function accountCodeValidator(value) {
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
        textField: 'tenantName',
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
        validator: accountCodeValidator,
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
        label: intl.get('hmsg.wechatOfficials.model.wechatOfficials.authType').d('授权类型'),
        lookupCode: 'HMSG.WECHAT.AUTH_TYPE',
        required: true,
      },
      {
        name: 'appid',
        type: 'string',
        label: intl.get('hmsg.wechatOfficials.model.wechatOfficials.appid').d('用户凭证'),
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
        name: 'secret',
        type: 'string',
        label: intl.get('hmsg.wechatOfficials.model.wechatOfficials.secret').d('用户密钥'),
        maxLength: 120,
      },
      {
        name: 'authAddress',
        type: 'string',
        label: intl
          .get('hmsg.wechatOfficials.model.wechatOfficials.authAddress')
          .d('第三方授权地址'),
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
        defaultValue: 1,
        trueValue: 1,
        falseValue: 0,
        required: true,
        align: 'left',
      },
    ],
    transport: {
      create: ({ config, data, dataSet }) => {
        const [{ __id, _status, tenantName, ...rest }] = data;
        const { publicKey } = dataSet.queryParameter;
        const newData = { ...rest };
        if (rest.secret) {
          newData.secret = encryptPwd(rest.secret, publicKey);
        }
        const url = isTenantRoleLevel()
          ? `${HZERO_MSG}/v1/${getCurrentOrganizationId()}/wechat-officials`
          : `${HZERO_MSG}/v1/wechat-officials`;
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
        if (newData.secret) {
          newData.secret = encryptPwd(newData.secret, publicKey);
        }
        const url = isTenantRoleLevel()
          ? `${HZERO_MSG}/v1/${getCurrentOrganizationId()}/wechat-officials`
          : `${HZERO_MSG}/v1/wechat-officials`;
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
          ? `${HZERO_MSG}/v1/${getCurrentOrganizationId()}/wechat-officials/${serverId}`
          : `${HZERO_MSG}/v1/wechat-officials/${serverId}`;
        return {
          url,
          method: 'GET',
          params: '',
        };
      },
    },
  };
};
