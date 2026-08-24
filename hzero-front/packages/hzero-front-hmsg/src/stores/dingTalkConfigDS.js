import { HZERO_MSG, VERSION_IS_OP } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel, encryptPwd } from 'utils/utils';
import intl from 'utils/intl';
import { CODE_UPPER } from 'utils/regExp';

const organizationId = getCurrentOrganizationId();

function codeValidator(value) {
  if (CODE_UPPER.test(value)) {
    return true;
  }
  return intl
    .get('hzero.common.validation.codeUpper')
    .d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”');
}

/**
 * TODO: c7n中，querybar='bar'时，queryFileds使用lov时，bind字段也会出现，后期c7n会修复
 */
const tableDs = () => ({
  selection: false,
  autoQuery: true,
  queryFields: [
    !isTenantRoleLevel() && {
      name: 'organizationId',
      type: 'object',
      label: intl.get('entity.tenant.name').d('租户名称'),
      lovCode: 'HPFM.TENANT',
      noCache: true,
      // ignore: 'always',
    },
    // !isTenantRoleLevel() && {
    //   name: 'organizationId',
    //   type: 'string',
    //   bind: 'tenantLov.tenantId',
    // },
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
      label: intl.get('hmsg.dingTalkConfig.model.dingTalkConfig.authType').d('授权类型'),
      lookupCode: 'HMSG.DINGTALK.AUTH_TYPE',
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
      label: intl.get('hmsg.dingTalkConfig.model.dingTalkConfig.authType').d('授权类型'),
    },
    {
      name: 'appKey',
      type: 'string',
      label: intl.get('hmsg.dingTalkConfig.model.dingTalkConfig.appKey').d('应用Key'),
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
    /**
     * 查询钉钉配置
     */

    read: (config) => {
      const { data } = config;
      const url = isTenantRoleLevel()
        ? `${HZERO_MSG}/v1/${organizationId}/dingtalk-servers`
        : `${HZERO_MSG}/v1/dingtalk-servers`;
      return {
        url,
        method: 'GET',
        data: {
          ...data,
          organizationId: data.organizationId && data.organizationId.tenantId,
        },
      };
    },

    /**
     * 删除钉钉配置
     * @param {Array} data - 勾选的数据
     * @param {Array} params.permissionList = ['hmsg.ding-talk-config.delete']
     */
    destroy: ({ data }) => {
      const url = isTenantRoleLevel()
        ? `${HZERO_MSG}/v1/${organizationId}/dingtalk-servers`
        : `${HZERO_MSG}/v1/dingtalk-servers`;
      return {
        data: data[0],
        url,
        method: 'DELETE',
      };
    },
  },
});

const detailDs = () => ({
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
      validator: codeValidator,
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
      label: intl.get('hmsg.dingTalkConfig.model.dingTalkConfig.authType').d('授权类型'),
      lookupCode: 'HMSG.DINGTALK.AUTH_TYPE',
      required: true,
    },
    {
      name: 'appKey',
      type: 'string',
      label: intl.get('hmsg.dingTalkConfig.model.dingTalkConfig.corpid').d('应用Key'),
      dynamicProps: ({ record }) => {
        if (record.get('authType') === 'DingTalk') {
          return {
            required: true,
          };
        }
      },
      maxLength: 60,
    },
    {
      name: 'appSecret',
      type: 'string',
      label: intl.get('hmsg.dingTalkConfig.model.dingTalkConfig.appSecret').d('应用密钥'),
      maxLength: 240,
    },
    {
      name: 'agentId',
      type: 'number',
      label: intl.get('hmsg.dingTalkConfig.model.dingTalkConfig.agentId').d('默认应用ID'),
      maxLength: 240,
    },
    {
      name: 'authAddress',
      type: 'string',
      label: intl.get('hmsg.dingTalkConfig.model.dingTalkConfig.authAddress').d('第三方授权地址'),
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
    /**
     * 新建钉钉配置明细
     * @param {Array} data - 参数
     * @param {Array} params.permissionList = ['hmsg.ding-talk-config.create','hmsg.ding-talk-config.copy']
     */
    create: ({ config, data, dataSet }) => {
      const [{ __id, _status, tenantName, ...rest }] = data;
      const { publicKey } = dataSet.queryParameter;
      const newData = { ...rest };
      if (rest.appSecret) {
        newData.appSecret = encryptPwd(rest.appSecret, publicKey);
      }
      const url = isTenantRoleLevel()
        ? `${HZERO_MSG}/v1/${organizationId}/dingtalk-servers`
        : `${HZERO_MSG}/v1/dingtalk-servers`;
      return {
        ...config,
        url,
        method: 'POST',
        data: {
          ...newData,
        },
      };
    },

    /**
     * 更新钉钉配置明细
     * @param {Array} data - 勾选的数据
     * @param {Array} params.permissionList = ['hmsg.ding-talk-config.edit']
     */
    update: ({ config, data, dataSet }) => {
      const newData = Array.isArray(data) ? data[0] : {};
      const { publicKey } = dataSet.queryParameter;
      if (newData.appSecret) {
        newData.appSecret = encryptPwd(newData.appSecret, publicKey);
      }
      const url = isTenantRoleLevel()
        ? `${HZERO_MSG}/v1/${organizationId}/dingtalk-servers`
        : `${HZERO_MSG}/v1/dingtalk-servers`;
      return {
        ...config,
        url,
        method: 'PUT',
        data: newData,
      };
    },
    /**
     * 查询钉钉配置明细
     * @param {Array} data - 勾选的数据
     * @param {Array} params.permissionList = ['hmsg.ding-talk-config.edit']
     */
    read: ({ dataSet }) => {
      const { serverId } = dataSet;
      const url = isTenantRoleLevel()
        ? `${HZERO_MSG}/v1/${organizationId}/dingtalk-servers/${serverId}`
        : `${HZERO_MSG}/v1/dingtalk-servers/${serverId}`;
      return {
        url,
        method: 'GET',
        params: '',
      };
    },
  },
});

export { tableDs, detailDs };
