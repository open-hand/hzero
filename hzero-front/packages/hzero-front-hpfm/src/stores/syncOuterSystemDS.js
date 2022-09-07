import { HZERO_PLATFORM } from 'utils/config';
import { getCurrentOrganizationId, encryptPwd } from 'utils/utils';
import intl from 'utils/intl';

const organizationId = getCurrentOrganizationId();

const tableDs = () => ({
  selection: false,
  autoQuery: true,
  fields: [
    {
      name: 'syncTypeCode',
      type: 'string',
      required: true,
      lookupCode: 'HPFM.HR_SYNC_TYPE',
    },
    {
      name: 'appId',
      type: 'string',
      required: true,
    },
    {
      name: 'authTypeMeaning',
      type: 'string',
      required: true,
    },
    {
      name: 'authAddress',
      type: 'string',
      required: true,
    },
    {
      name: 'enabledFlag',
      type: 'boolean',
      required: true,
      falseValue: 0,
      trueValue: 1,
      defaultValue: 1,
    },
  ],
  queryFields: [],
  transport: {
    read() {
      return {
        url: `${HZERO_PLATFORM}/v1/${organizationId}/hr-syncs`,
        method: 'GET',
      };
    },
  },
});

const logDs = () => ({
  selection: false,
  autoQuery: false,
  fields: [
    {
      name: 'lastUpdateDate',
      type: 'string',
      required: true,
    },
    {
      name: 'syncDirectionMeaning',
      type: 'string',
    },
    {
      name: 'syncTypeMeaning',
      type: 'string',
      required: true,
    },
    {
      name: 'deptStatusMeaning',
      type: 'string',
      required: true,
    },
    {
      name: 'empStatusMeaning',
      type: 'string',
      required: true,
    },
  ],
  queryFields: [
    {
      name: 'startDate',
      type: 'dateTime',
      label: intl.get('hpfm.syncOuterSystem.model.sync.startDate').d('开始日期'),
    },
    {
      name: 'endDate',
      type: 'dateTime',
      label: intl.get('hpfm.syncOuterSystem.model.sync.endDate').d('结束日期'),
    },
  ],
  transport: {
    read({ dataSet }) {
      const { syncId } = dataSet;
      return {
        url: `${HZERO_PLATFORM}/v1/${organizationId}/hr-syncs/${syncId}`,
        method: 'GET',
      };
    },
  },
});

const tableDetailDs = () => ({
  autoQueryAfterSubmit: false,
  fields: [
    {
      name: 'syncTypeCode',
      type: 'string',
      label: intl.get('hpfm.syncOuterSystem.model.sync.syncTypeCodeMeaning').d('同步类型'),
      required: true,
      lookupCode: 'HPFM.HR_SYNC_TYPE',
    },
    {
      name: 'appId',
      type: 'string',
      label: intl.get('hpfm.syncOuterSystem.model.sync.appId').d('用户凭证'),
      required: true,
      maxLength: 200,
    },
    {
      name: 'appSecret',
      type: 'string',
      label: intl.get('hpfm.syncOuterSystem.model.sync.appSecret').d('用户密钥'),
      maxLength: 200,
    },
    {
      name: 'authType',
      type: 'string',
      label: intl.get('hpfm.syncOuterSystem.model.sync.authTypeMeaning').d('授权类型'),
      required: true,
      lookupCode: 'HPFM.HR_AUTH_TYPE',
    },
    {
      name: 'authAddress',
      type: 'string',
      label: intl.get('hpfm.syncOuterSystem.model.sync.authAddress').d('三方授权地址'),
      maxLength: 240,
    },
    {
      name: 'enabledFlag',
      type: 'boolean',
      label: intl.get('hzero.common.status.enable').d('启用'),
      required: true,
      defaultValue: 1,
      falseValue: 0,
      trueValue: 1,
    },
  ],
  transport: {
    read({ dataSet }) {
      const { syncId } = dataSet;
      return {
        url: `${HZERO_PLATFORM}/v1/${organizationId}/hr-syncs/detail/${syncId}`,
        method: 'GET',
        params: '',
      };
    },
    create: ({ config, data, dataSet }) => {
      const [{ __id, _status, ...rest }] = data;
      const { publicKey } = dataSet.queryParameter;
      const newData = { ...rest };
      if (rest.appSecret) {
        newData.appSecret = encryptPwd(rest.appSecret, publicKey);
      }
      const url = `${HZERO_PLATFORM}/v1/${organizationId}/hr-syncs`;
      return {
        ...config,
        url,
        method: 'POST',
        data: {
          ...newData,
          tenantId: organizationId,
        },
      };
    },
    update: ({ config, data, dataSet }) => {
      const newData = Array.isArray(data) ? data[0] : {};
      const { publicKey } = dataSet.queryParameter;
      if (newData.appSecret) {
        newData.appSecret = encryptPwd(newData.appSecret, publicKey);
      }
      const url = `${HZERO_PLATFORM}/v1/${organizationId}/hr-syncs`;
      return {
        ...config,
        url,
        method: 'PUT',
        data: newData,
      };
    },
  },
});

const logDetailDs = () => ({
  transport: {
    read({ dataSet }) {
      const { logId } = dataSet;
      return {
        url: `${HZERO_PLATFORM}/v1/${organizationId}/hr-syncs/logs/${logId}`,
        method: 'GET',
      };
    },
  },
});

const handleUpdateDs = () => ({
  transport: {
    read({ dataSet }) {
      const { requestData } = dataSet;
      return {
        url: `${HZERO_PLATFORM}/v1/${organizationId}/hr-syncs/do`,
        method: 'POST',
        params: '',
        data: {
          ...requestData,
        },
      };
    },
  },
});

const handleUpdateDsLocal = () => ({
  transport: {
    read({ dataSet }) {
      const { requestData } = dataSet;
      return {
        url: `${HZERO_PLATFORM}/v1/${organizationId}/hr-syncs/local`,
        method: 'POST',
        params: '',
        data: {
          ...requestData,
        },
      };
    },
  },
});

export { tableDs, tableDetailDs, logDs, logDetailDs, handleUpdateDs, handleUpdateDsLocal };
