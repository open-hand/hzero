/*
 * DataSourceApplication 数据源应用配置
 * @date: 2020-04-22
 * @author XL <liang.xiong@hand-china.com>
 * @copyright Copyright (c) 2020, Hand
 */
import intl from 'utils/intl';
import { HZERO_PLATFORM } from 'utils/config';
import { isTenantRoleLevel, getCurrentOrganizationId } from 'utils/utils';

const organizationId = getCurrentOrganizationId();
const isTenant = isTenantRoleLevel();
const apiPrefix = isTenant ? `${HZERO_PLATFORM}/v1/${organizationId}` : `${HZERO_PLATFORM}/v1`;

// 表格ds
const tableDS = () => ({
  autoQuery: true,
  selection: false,
  dataKey: 'content',
  queryFields: [
    // {
    //   name: 'dataSourceLov',
    //   type: 'object',
    //   label: intl.get('hpfm.dsRoutes.model.dsRoutes.datasourceName').d('数据源'),
    //   lovCode: 'HPFM.DATASOURCE',
    //   ignore: 'always',
    //   noCache: true,
    // },
    // {
    //   name: 'datasourceId',
    //   type: 'string',
    //   bind: 'dataSourceLov.datasourceId',
    // },
    {
      name: 'serviceLov',
      type: 'object',
      label: intl.get('hpfm.dsRoutes.model.dsRoutes.fullSourceUrl').d('服务'),
      lovCode: 'HADM.SERVICE',
      ignore: 'always',
      noCache: true,
    },
    {
      name: 'serviceCode',
      type: 'string',
      bind: 'serviceLov.serviceCode',
    },
    {
      name: 'tenantLov',
      type: 'object',
      label: intl.get('hpfm.dsRoutes.model.dsRoutes.tenantId').d('租户'),
      lovCode: 'HPFM.TENANT',
      ignore: 'always',
      noCache: true,
    },
    {
      name: 'tenantNum',
      type: 'string',
      bind: 'tenantLov.tenantNum',
    },
    {
      name: 'method',
      type: 'string',
      label: intl.get('hpfm.dsRoutes.model.dsRoutes.method').d('METHOD'),
      lookupCode: 'HIAM.REQUEST_METHOD',
    },
    {
      name: 'enabledFlag',
      type: 'string',
      label: intl.get('hzero.common.status').d('状态'),
      lookupCode: 'HPFM.ENABLED_FLAG',
    },
    {
      name: 'url',
      type: 'string',
      label: intl.get('hpfm.dsRoutes.model.dsRoutes.url').d('URL'),
    },
  ],
  fields: [
    {
      name: 'serviceName',
      type: 'string',
      label: intl.get('hpfm.dsRoutes.model.dsRoutes.fullSourceUrl').d('服务'),
    },
    {
      name: 'tenantName',
      type: 'string',
      label: intl.get('hpfm.dsRoutes.model.dsRoutes.fullTargetUrl').d('租户'),
    },
    {
      name: 'url',
      type: 'string',
      label: intl.get('hpfm.dsRoutes.model.dsRoutes.url').d('URL'),
    },
    {
      name: 'method',
      type: 'string',
      label: intl.get('hpfm.dsRoutes.model.dsRoutes.method').d('METHOD'),
    },
    {
      name: 'dsNames',
      type: 'string',
      label: intl.get('hpfm.dsRoutes.model.dsRoutes.datasourceName').d('数据源'),
    },
    {
      name: 'enabledFlag',
      type: 'boolean',
      label: intl.get('hpfm.dsRoutes.model.dsRoutes.alertName').d('状态'),
      trueValue: 1,
      falseValue: 0,
    },
  ],
  transport: {
    read: () => ({
      url: `${apiPrefix}/ds-routes`,
      method: 'GET',
    }),
    destroy: ({ data }) => {
      const { __id, _status, ...other } = Array.isArray(data) ? data[0] : {};
      return {
        data: other,
        url: `${apiPrefix}/ds-routes`,
        method: 'DELETE',
      };
    },
  },
});

const detailDS = () => ({
  autoQuery: false,
  autoQueryAfterSubmit: false,
  autoCreate: true,
  fields: [
    {
      name: 'serviceLov',
      type: 'object',
      label: intl.get('hpfm.dsRoutes.model.dsRoutes.fullSourceUrl').d('服务'),
      lovCode: 'HADM.SERVICE',
      textField: 'serviceName',
      ignore: 'always',
      noCache: true,
    },
    {
      name: 'applicationServiceId',
      type: 'string',
      bind: 'serviceLov.serviceId',
    },
    {
      name: 'serviceName',
      type: 'string',
      bind: 'serviceLov.serviceName',
    },
    {
      name: 'serviceCode',
      type: 'string',
      bind: 'serviceLov.serviceCode',
    },
    {
      name: 'tenantLov',
      type: 'object',
      label: intl.get('hpfm.dsRoutes.model.dsRoutes.tenantId').d('租户'),
      lovCode: 'HPFM.TENANT',
      ignore: 'always',
      textField: 'tenantName',
      noCache: true,
    },
    {
      name: 'applicationTenantId',
      type: 'string',
      bind: 'tenantLov.tenantId',
    },
    {
      name: 'tenantId',
      type: 'string',
      bind: 'tenantLov.tenantId',
    },
    {
      name: 'tenantName',
      type: 'string',
      bind: 'tenantLov.tenantName',
    },
    {
      name: 'tenantNum',
      type: 'string',
      bind: 'tenantLov.tenantNum',
    },
    {
      name: 'enabledFlag',
      type: 'boolean',
      label: intl.get('hpfm.dsRoutes.model.dsRoutes.enabledFlag').d('是否启用'),
      defaultValue: 0,
      trueValue: 1,
      falseValue: 0,
    },
    {
      name: 'url',
      type: 'string',
      label: intl.get('hpfm.dsRoutes.model.dsRoutes.url').d('URL'),
      maxLength: 240,
    },
    {
      name: 'methodList',
      type: 'string',
      label: intl.get('hpfm.dsRoutes.model.dsRoutes.method').d('METHOD'),
      lookupCode: 'HIAM.REQUEST_METHOD',
    },
  ],
  transport: {
    read: ({ dataSet }) => {
      const {
        queryParameter: { dsRouteId },
      } = dataSet;
      return {
        url: `${apiPrefix}/ds-routes/${dsRouteId}`,
        method: 'GET',
        data: {},
        params: {},
      };
    },
    create: ({ data }) => {
      const { __id, _status, ...other } = Array.isArray(data) ? data[0] : {};
      return {
        url: `${apiPrefix}/ds-routes/save`,
        method: 'POST',
        data: other,
      };
    },
    update: ({ data }) => {
      const { __id, _status, ...other } = Array.isArray(data) ? data[0] : {};
      return {
        url: `${apiPrefix}/ds-routes/save`,
        method: 'PUT',
        data: other,
      };
    },
  },
});

// 详情页面下的表格信息DS
const detailTableDS = () => ({
  selection: false,
  autoQuery: false,
  autoCreate: false,
  fields: [
    {
      name: 'datasourceCode',
      type: 'string',
      label: intl.get('hpfm.dsRoutes.model.dsRoutes.datasourceName').d('数据源'),
    },
    {
      name: 'masterFlag',
      type: 'boolean',
      label: intl.get('hpfm.dsRoutes.model.dataSourceApplication.masterFlag').d('是否主数据源'),
      trueValue: 1,
      falseValue: 0,
      defaultValue: 0,
    },
  ],
  transport: {
    read: () => ({
      url: `${apiPrefix}/ds`,
      method: 'GET',
    }),
    destroy: ({ data }) => {
      return {
        url: `${apiPrefix}/ds`,
        method: 'DELETE',
        data: data[0],
      };
    },
  },
});

const detailTableDetailDS = () => ({
  selection: false,
  autoQuery: false,
  autoCreate: false,
  fields: [
    {
      name: 'datasourceLov',
      label: intl.get('hpfm.dsRoutes.model.dsRoutes.datasourceName').d('数据源'),
      type: 'object',
      required: true,
      lovCode: 'HPFM.DATASOURCE',
      noCache: true,
      ignore: 'always',
    },
    {
      name: 'datasourceId',
      type: 'string',
      bind: 'datasourceLov.datasourceId',
    },
    {
      name: 'datasourceCode',
      type: 'string',
      bind: 'datasourceLov.datasourceCode',
    },
    {
      name: 'masterFlag',
      type: 'boolean',
      label: intl.get('hpfm.dsRoutes.model.dataSourceApplication.masterFlag').d('是否主数据源'),
      trueValue: 1,
      falseValue: 0,
      defaultValue: 0,
    },
  ],
  transport: {
    read: () => ({
      url: `${apiPrefix}/ds`,
      method: 'GET',
    }),
    destroy: ({ data }) => {
      return {
        url: `${apiPrefix}/ds`,
        method: 'DELETE',
        data: data[0],
      };
    },
  },
});

const pushTableDS = () => ({
  selection: 'multiple',
  autoQuery: false,
  autoCreate: false,
  queryFields: [
    {
      name: 'serviceLov',
      type: 'object',
      label: intl.get('hpfm.dsRoutes.model.dsRoutes.fullSourceUrl').d('服务'),
      lovCode: 'HADM.SERVICE',
      ignore: 'always',
      noCache: true,
    },
    {
      name: 'serviceCode',
      type: 'string',
      bind: 'serviceLov.serviceCode',
    },
    {
      name: 'tenantLov',
      type: 'object',
      label: intl.get('hpfm.dsRoutes.model.dsRoutes.tenantId').d('租户'),
      lovCode: 'HPFM.TENANT',
      ignore: 'always',
      noCache: true,
    },
    {
      name: 'tenantNum',
      type: 'string',
      bind: 'tenantLov.tenantNum',
    },
  ],
  fields: [
    {
      name: 'serviceCode',
      type: 'string',
      label: intl.get('hpfm.dsRoutes.model.dsRoutes.serviceCode').d('服务编码'),
    },
  ],
  transport: {
    read: () => ({
      url: `${apiPrefix}/ds-routes/all-service`,
      method: 'GET',
    }),
  },
});

export { tableDS, detailDS, detailTableDS, detailTableDetailDS, pushTableDS };
