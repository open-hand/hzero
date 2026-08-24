import { HZERO_ADM } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import intl from 'utils/intl';

const organizationId = getCurrentOrganizationId();

const tableDs = () => {
  return {
    selection: false,
    autoQuery: true,
    queryFields: [
      {
        name: 'serviceCodeLov',
        type: 'object',
        label: intl.get('hadm.common.model.common.serviceCode').d('服务编码'),
        lovPara: { tenantId: getCurrentOrganizationId() },
        lovCode: 'HADM.SERVICE',
        ignore: 'always',
        noCache: true,
      },
      {
        name: 'serviceCode',
        bind: 'serviceCodeLov.serviceCode',
      },
      {
        name: 'dsUrl',
        type: 'string',
        label: intl.get('hadm.dsMapping.model.dsMapping.dsUrl').d('数据源URL'),
      },
    ],
    fields: [
      {
        name: 'serviceCode',
        type: 'string',
        label: intl.get('hadm.common.model.common.serviceCode').d('服务编码'),
      },
      {
        name: 'serviceVersion',
        type: 'string',
        label: intl.get('hadm.common.model.common.versionNumber').d('服务版本'),
      },
      {
        name: 'dsUrl',
        type: 'string',
        label: intl.get('hadm.dsMapping.model.dsMapping.dsUrl').d('数据源URL'),
      },
    ],
    transport: {
      read: () => {
        const url = isTenantRoleLevel()
          ? `${HZERO_ADM}/v1/${organizationId}/sp-service-datasource`
          : `${HZERO_ADM}/v1/sp-service-datasource`;
        return {
          url,
          method: 'GET',
        };
      },
      destroy: ({ data }) => {
        const { __id, _status, ...other } = Array.isArray(data) ? data[0] : {};
        return {
          data: other,
          url: isTenantRoleLevel()
            ? `${HZERO_ADM}/v1/${organizationId}/sp-service-datasource`
            : `${HZERO_ADM}/v1/sp-service-datasource`,
          method: 'DELETE',
        };
      },
    },
  };
};

const detailDs = () => {
  return {
    autoQueryAfterSubmit: false,
    fields: [
      {
        name: 'serviceCodeLov',
        type: 'object',
        label: intl.get('hadm.dsMapping.model.dsMapping.serviceCode').d('服务编码'),
        required: true,
        lovPara: { tenantId: getCurrentOrganizationId() },
        lovCode: 'HADM.SERVICE',
        ignore: 'always',
        noCache: true,
      },
      {
        name: 'serviceCode',
        bind: 'serviceCodeLov.serviceCode',
      },
      {
        name: 'serviceVersion',
        type: 'string',
        label: intl.get('hadm.common.model.common.versionNumber').d('服务版本'),
        required: true,
      },
      {
        name: 'dsUrl',
        type: 'string',
        label: intl.get('hadm.dsMapping.model.dsMapping.dsUrl').d('数据源URL'),
        required: true,
      },
      {
        name: 'dsUsername',
        type: 'string',
        label: intl.get('hadm.dsMapping.model.dsMapping.dsUsername').d('用户名'),
      },
      {
        name: 'dsPassword',
        type: 'string',
        label: intl.get('hadm.dsMapping.model.dsMapping.dsPassword').d('密码'),
      },

      {
        name: 'connectionTimeoutMilliseconds',
        type: 'string',
        label: intl
          .get('hadm.dsMapping.model.dsMapping.connectionTimeoutMilliseconds')
          .d('连接超时毫秒'),
      },
      {
        name: 'idleTimeoutMilliseconds',
        type: 'string',
        label: intl.get('hadm.dsMapping.model.dsMapping.idleTimeoutMilliseconds').d('空闲超时毫秒'),
      },
      {
        name: 'maxLifetimeMilliseconds',
        type: 'string',
        label: intl.get('hadm.dsMapping.model.dsMapping.maxLifetimeMilliseconds').d('最大存活毫秒'),
      },
      {
        name: 'maxPoolSize',
        type: 'string',
        label: intl.get('hadm.dsMapping.model.dsMapping.maxPoolSize').d('最大连接大小'),
      },
    ],
    transport: {
      create: ({ config, data }) => {
        const url = isTenantRoleLevel()
          ? `${HZERO_ADM}/v1/${organizationId}/sp-service-datasource`
          : `${HZERO_ADM}/v1/sp-service-datasource`;
        return {
          ...config,
          url,
          method: 'POST',
          data: data[0],
        };
      },
      update: ({ config, data }) => {
        const url = isTenantRoleLevel()
          ? `${HZERO_ADM}/v1/${organizationId}/sp-service-datasource`
          : `${HZERO_ADM}/v1/sp-service-datasource`;
        return {
          ...config,
          url,
          method: 'PUT',
          data: data[0],
        };
      },
      read: ({ dataSet }) => {
        const {
          queryParameter: { serviceDatasourceId },
        } = dataSet;
        const url = isTenantRoleLevel()
          ? `${HZERO_ADM}/v1/${organizationId}/sp-service-datasource/${serviceDatasourceId}`
          : `${HZERO_ADM}/v1/sp-service-datasource/${serviceDatasourceId}`;
        return {
          url,
          method: 'GET',
          params: '',
        };
      },
    },
  };
};

export { tableDs, detailDs };
