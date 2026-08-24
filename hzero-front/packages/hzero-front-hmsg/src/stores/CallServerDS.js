import { HZERO_MSG, VERSION_IS_OP, HZERO_PLATFORM } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
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

const tableDs = () => ({
  selection: false,
  autoQuery: true,
  queryFields: [
    !isTenantRoleLevel() && {
      name: 'tenantLov',
      type: 'object',
      label: intl.get('entity.tenant.name').d('租户名称'),
      lovCode: 'HPFM.TENANT',
      noCache: true,
      ignore: 'always',
    },
    !isTenantRoleLevel() && {
      name: 'tenantId',
      type: 'string',
      bind: 'tenantLov.tenantId',
    },
    {
      name: 'serverCode',
      type: 'string',
      label: intl.get('hmsg.callServer.model.callServer.serverCode').d('服务编码'),
    },
    {
      name: 'serverName',
      type: 'string',
      label: intl.get('hmsg.callServer.model.callServer.serverName').d('服务名称'),
    },
    {
      name: 'enabledFlag',
      type: 'string',
      label: intl.get('hzero.common.status').d('状态'),
      lookupCode: 'HPFM.ENABLED_FLAG',
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
      label: intl.get('hmsg.callServer.model.callServer.serverCode').d('服务编码'),
    },
    {
      name: 'serverName',
      type: 'string',
      label: intl.get('hmsg.callServer.model.callServer.serverName').d('服务名称'),
    },
    {
      name: 'serverTypeMeaning',
      type: 'string',
      label: intl.get('hmsg.callServer.model.callServer.serverTypeCode').d('服务类型'),
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
     * 查询语音电话配置
     */
    read: () => {
      const url = isTenantRoleLevel()
        ? `${HZERO_MSG}/v1/${organizationId}/call-servers`
        : `${HZERO_MSG}/v1/call-servers`;
      return {
        url,
        method: 'GET',
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
      label: intl.get('hmsg.callServer.model.callServer.serverCode').d('服务编码'),
      required: true,
      maxLength: 30,
      validator: codeValidator,
    },
    {
      name: 'serverName',
      type: 'intl',
      label: intl.get('hmsg.callServer.model.callServer.serverName').d('服务名称'),
      required: true,
      maxLength: 60,
    },
    {
      name: 'serverTypeCode',
      type: 'string',
      label: intl.get('hmsg.callServer.model.callServer.serverTypeCode').d('服务类型'),
      lookupCode: 'HMSG.CALL.SERVER_TYPE',
      required: true,
    },
    {
      name: 'accessKey',
      type: 'string',
      label: intl.get('hmsg.callServer.model.callServer.accessKey').d('appId'),
      required: true,
      maxLength: 240,
    },
    {
      name: 'accessSecret',
      type: 'string',
      label: intl.get('hmsg.callServer.model.callServer.accessSecret').d('密钥'),
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
     * 新建语音电话配置明细
     * @param {Array} data - 参数
     */
    create: ({ config, data }) => {
      const [{ __id, _status, tenantName, serverId: primaryId, ...rest }] = data;
      const newData = { ...rest };
      const {
        tenantId,
        serverCode,
        serverName,
        serverTypeCode,
        serverId,
        accessSecret,
        accessKey,
        enabledFlag,
        objectVersionNumber,
        _token,
        tenantName: tn,
        serverTypeMeaning,
        _status: _st,
        extParam,
        ...r
      } = newData;
      newData.extParam = JSON.stringify(r);
      const url = isTenantRoleLevel()
        ? `${HZERO_MSG}/v1/${organizationId}/call-servers`
        : `${HZERO_MSG}/v1/call-servers`;
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
    update: ({ config, data }) => {
      const newData = Array.isArray(data) ? data[0] : {};
      const {
        tenantId,
        serverCode,
        serverName,
        serverTypeCode,
        serverId,
        accessSecret,
        accessKey,
        enabledFlag,
        objectVersionNumber,
        _token,
        tenantName,
        serverTypeMeaning,
        _status,
        extParam,
        ...rest
      } = newData;
      newData.extParam = JSON.stringify(rest);
      const url = isTenantRoleLevel()
        ? `${HZERO_MSG}/v1/${organizationId}/call-servers`
        : `${HZERO_MSG}/v1/call-servers`;
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
      const {
        queryParameter: { serverId },
      } = dataSet;
      const url = isTenantRoleLevel()
        ? `${HZERO_MSG}/v1/${organizationId}/call-servers/${serverId}`
        : `${HZERO_MSG}/v1/call-servers/${serverId}`;
      return {
        url,
        method: 'GET',
        params: '',
      };
    },
  },
});

const formConfigDs = () => ({
  transport: {
    /**
     * 表单配置
     */
    read: () => {
      const url = isTenantRoleLevel()
        ? `${HZERO_PLATFORM}/v1/${organizationId}/form-lines/header-code`
        : `${HZERO_PLATFORM}/v1/form-lines/header-code`;
      return {
        url,
        method: 'GET',
        params: {},
      };
    },
  },
});

export { tableDs, detailDs, formConfigDs };
