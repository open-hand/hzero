/**
 * 客户化管理-DS
 * @since 2019-12-18
 * @author LiLin <lin.li03@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import intl from 'utils/intl';
import { HZERO_IAM } from 'utils/config';
import { filterNullValueObject } from 'utils/utils';

// 共有的from字段
const fromFields = [
  {
    name: 'customPointCode',
    type: 'string',
    label: intl.get('hiam.customerManage.model.customerManage.customPointCode').d('端点编码'),
  },
  {
    name: 'description',
    type: 'string',
    label: intl.get('hiam.customerManage.model.customerManage.description').d('描述'),
  },
  {
    name: 'className',
    type: 'string',
    label: intl.get('hiam.customerManage.model.customerManage.className').d('类名'),
  },
  {
    name: 'methodName',
    type: 'string',
    label: intl.get('hiam.customerManage.model.customerManage.methodName').d('方法名'),
  },
  {
    name: 'serviceNameLov',
    type: 'object',
    lovCode: 'HADM.SERVICE_CODE',
    label: intl.get('hiam.customerManage.model.customerManage.serviceName').d('服务'),
    ignore: 'always',
    noCache: true,
  },
  {
    name: 'serviceName',
    type: 'string',
    bind: 'serviceNameLov.serviceName',
  },
];

// 共有的table字段
const tableFields = [
  {
    name: 'customPointCode',
    type: 'string',
    label: intl.get('hiam.customerManage.model.customerManage.customPointCode').d('端点编码'),
  },
  {
    name: 'priority',
    type: 'number',
    required: true,
    max: 2147483647,
    label: intl.get('hiam.customerManage.model.customerManage.priority').d('优先级'),
  },
  {
    name: 'description',
    type: 'intl',
    required: true,
    maxLength: 480,
    label: intl.get('hiam.customerManage.model.customerManage.description').d('描述'),
  },
  {
    name: 'className',
    type: 'string',
    label: intl.get('hiam.customerManage.model.customerManage.className').d('类名'),
  },
  {
    name: 'methodName',
    type: 'string',
    label: intl.get('hiam.customerManage.model.customerManage.methodName').d('方法名'),
  },
  {
    name: 'serviceName',
    type: 'string',
    label: intl.get('hiam.customerManage.model.customerManage.serviceName').d('服务'),
  },
];

// 查询from
const formDS = () => ({
  fields: fromFields,
});

// 管理化table DS
function tableDS(formRecord) {
  return {
    dataKey: 'content',
    cacheSelection: true,
    fields: tableFields,
    events: {
      query: ({ dataSet }) => {
        dataSet.unSelectAll();
        dataSet.clearCachedSelected();
      },
    },
    transport: {
      read: (config) => {
        const { params } = config;
        const url = `${HZERO_IAM}/v1/tenant-custom/points`;
        let data = {};
        if (formRecord.toData()[0]) {
          const {
            customPointCode = '',
            priority = '',
            serviceName = '',
            description = '',
            className = '',
            methodName = '',
          } = formRecord.toData()[0];
          data = filterNullValueObject({
            customPointCode,
            priority,
            serviceName,
            description,
            className,
            methodName,
          });
        }
        return {
          data,
          params,
          url,
          method: 'GET',
        };
      },
      update: (config) => {
        const { __id, _status, ...other } = Array.isArray(config.data) ? config.data[0] : {};
        return {
          ...config,
          data: other,
          url: `${HZERO_IAM}/v1/tenant-custom/points`,
          method: 'PUT',
        };
      },
      destroy: (config) => {
        const { params = {}, data = [] } = config;
        return {
          data,
          params,
          url: `${HZERO_IAM}/v1/tenant-custom/points`,
          method: 'DELETE',
        };
      },
    },
  };
}

const tenantDS = () => ({
  autoQuery: true,
  dataKey: 'content',
  cacheSelection: true,
  queryFields: [
    {
      name: 'tenantNum',
      type: 'string',
      label: intl.get('hiam.customerManage.model.customerManage.tenantNum').d('租户编码'),
      unique: true,
    },
    {
      name: 'tenantName',
      type: 'string',
      label: intl.get('hiam.customerManage.model.customerManage.tenantName').d('租户名称'),
      unique: true,
    },
  ],
  fields: [
    {
      name: 'tenantId',
      type: 'number',
      label: intl.get('hiam.customerManage.model.customerManage.tenantId').d('租户id'),
      unique: true,
    },
    {
      name: 'tenantNum',
      type: 'string',
      label: intl.get('hiam.customerManage.model.customerManage.tenantNum').d('租户编码'),
      unique: true,
    },
    {
      name: 'tenantName',
      type: 'string',
      label: intl.get('hiam.customerManage.model.customerManage.tenantName').d('租户名称'),
      unique: true,
    },
  ],
  events: {
    query: ({ dataSet }) => {
      dataSet.unSelectAll();
      dataSet.clearCachedSelected();
    },
  },
  transport: {
    read: (config) => {
      const url = `${HZERO_IAM}/v1/tenants`;
      return {
        ...config,
        url,
        method: 'GET',
      };
    },
  },
});

// 租户客户化管理查询form
const tenantFormDS = () => ({
  fields: [
    {
      name: 'tenantIdLov',
      type: 'object',
      lovCode: 'HPFM.TENANT',
      label: intl.get('hiam.customerManage.model.customerManage.tenant').d('租户'),
      noCache: true,
      ignore: 'always',
    },
    {
      name: 'tenantName',
      type: 'string',
      bind: 'tenantIdLov.tenantName',
    },
    {
      name: 'tenantId',
      type: 'string',
      bind: 'tenantIdLov.tenantId',
    },
    ...fromFields,
  ],
});

// 租户客户化管理表格DS
function tenantTableDS(record) {
  return {
    dataKey: 'content',
    cacheSelection: true,
    fields: [
      {
        name: 'tenantName',
        type: 'string',
        label: intl.get('hiam.customerManage.model.customerManage.tenantName').d('租户名称'),
      },
      ...tableFields,
    ],
    events: {
      query: ({ dataSet }) => {
        dataSet.unSelectAll();
        dataSet.clearCachedSelected();
      },
    },
    transport: {
      read: (config) => {
        const url = `${HZERO_IAM}/v1/tenant-custom/points/tenant`;
        const { params } = config;
        let data = {};
        if (record.toData()[0]) {
          const {
            tenantId = '',
            customPointCode = '',
            priority = '',
            serviceName = '',
            description = '',
            className = '',
            methodName = '',
          } = record.toData()[0];
          data = filterNullValueObject({
            tenantId,
            customPointCode,
            priority,
            serviceName,
            description,
            className,
            methodName,
          });
        }
        return {
          data,
          params,
          url,
          method: 'GET',
        };
      },
      destroy: (config) => {
        const url = `${HZERO_IAM}/v1/tenant-custom/points/revoke`;
        const { params = {}, data = [] } = config;
        return {
          data,
          params,
          url,
          method: 'DELETE',
        };
      },
    },
  };
}

// 刷新客户化表单DS
const refreshDS = () => ({
  autoQueryAfterSubmit: false,
  fields: [
    {
      name: 'serviceLov',
      label: intl.get('hiam.customerManage.model.customerManage.refreshServiceName').d('服务名'),
      required: true,
      type: 'object',
      ignore: 'always',
      noCache: true,
      lovCode: 'HADM.SERVICE',
    },
    {
      name: 'serviceName',
      type: 'string',
      bind: 'serviceLov.serviceCode',
    },
    {
      name: 'metaVersion',
      label: intl.get('hiam.customerManage.model.customerManage.metaVersion').d('服务标记版本'),
      required: true,
      type: 'string',
    },
    {
      name: 'cleanPermission',
      label: intl
        .get('hiam.customerManage.model.customerManage.cleanPermission')
        .d('是否清除过期权限'),
      type: 'boolean',
      defaultValue: false,
    },
  ],
  transport: {
    create: ({ data }) => {
      const { __id, _status, ...other } = Array.isArray(data) ? data[0] : {};
      return {
        url: `${HZERO_IAM}/v1/tool/permission/fresh-custom-point`,
        method: 'POST',
        params: other,
        data: {},
      };
    },
  },
});

export { formDS, tableDS, tenantDS, refreshDS, tenantFormDS, tenantTableDS };
