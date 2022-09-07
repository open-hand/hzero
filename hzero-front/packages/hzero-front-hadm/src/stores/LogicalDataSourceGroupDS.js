/*
 * LogicalDataSourceGroupDS 逻辑数据源组DS
 * @date: 2020-05-06
 * @author: LiLin <lin.li03@hand-china.com>
 * @copyright Copyright (c) 2020, Hand
 */
import intl from 'utils/intl';
import { isTenantRoleLevel, getCurrentOrganizationId } from 'utils/utils';
import notification from 'utils/notification';
import { HZERO_ADM } from 'utils/config';

const organizationId = getCurrentOrganizationId();
const isTenant = isTenantRoleLevel();
const apiPrefix = isTenant ? `${HZERO_ADM}/v1/${organizationId}` : `${HZERO_ADM}/v1`;

// 表格ds
const tableDS = () => {
  return {
    autoQuery: true,
    selection: false,
    dataKey: 'content',
    queryFields: [
      {
        name: 'datasourceGroupName',
        type: 'string',
        label: intl
          .get('hadm.LogicalDataSource.model.logicalData.datasourceGroupName')
          .d('逻辑数据源组名'),
      },
      {
        name: 'description',
        type: 'string',
        label: intl.get('hzero.common.view.description').d('描述'),
      },
    ],
    fields: [
      {
        name: 'datasourceGroupName',
        label: intl
          .get('hadm.LogicalDataSource.model.logicalData.datasourceGroupName')
          .d('逻辑数据源组名'),
        type: 'string',
      },
      {
        name: 'description',
        type: 'string',
        label: intl.get('hzero.common.view.description').d('描述'),
      },
    ],
    transport: {
      read: () => ({
        url: `${apiPrefix}/sp-datasource-groups`,
        method: 'GET',
      }),
      destroy: ({ data }) => {
        const { __id, _status, ...other } = Array.isArray(data) ? data[0] : {};
        return {
          data: other,
          url: `${apiPrefix}/sp-datasource-groups`,
          method: 'DELETE',
        };
      },
    },
  };
};

// 新建或编辑时的DS
const formDS = () => {
  return {
    autoQuery: false,
    autoQueryAfterSubmit: false,
    fields: [
      {
        name: 'datasourceGroupName',
        type: 'string',
        required: true,
        label: intl.get('hadm.LogicalDataSource.model.logicalData.datasourceGroup').d('数据源组名'),
        maxLength: 30,
      },
      {
        name: 'description',
        type: 'string',
        label: intl.get('hzero.common.view.description').d('描述'),
        maxLength: 240,
      },
      {
        name: 'defaultDatasourceName',
        type: 'string',
        label: intl.get('hadm.LogicalDataSource.model.logicalData.dataSourceName').d('默认数据源'),
        maxLength: 60,
      },
    ],
    transport: {
      read: ({ data }) => {
        const { datasourceGroupId } = data;
        return {
          url: `${apiPrefix}/sp-datasource-groups/${datasourceGroupId}`,
          method: 'GET',
          data: {},
          params: {},
        };
      },
      create: ({ data }) => {
        const { __id, _status, ...other } = Array.isArray(data) ? data[0] : {};
        return {
          url: `${apiPrefix}/sp-datasource-groups`,
          method: 'POST',
          data: other,
        };
      },
      update: ({ data }) => {
        const { __id, _status, ...other } = Array.isArray(data) ? data[0] : {};
        return {
          url: `${apiPrefix}/sp-datasource-groups`,
          method: 'POST',
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
  };
};

const drawerDS = () => {
  return {
    autoCreate: false,
    autoQuery: false,
    fields: [
      {
        name: 'dataType',
        label: intl.get('hadm.LogicalDataSource.model.logicalData.dataType').d('数据源类型'),
        type: 'string',
      },
      {
        name: 'loadBalanceAlgorithmType',
        type: 'string',
        lookupCode: 'HADM.SP_DATASOURCE_LOADBALANCE',
        label: intl.get('hadm.LogicalDataSource.model.logicalData.loadBA').d('负载策略'),
        dynamicProps: {
          required({ record }) {
            return record.get('isRequired');
          },
        },
      },
      {
        name: 'isRequired', // 用于判断字段是否必输
        type: 'boolean',
        defaultValue: false,
      },
    ],
    transport: {
      read: ({ data }) => {
        const { datasourceId } = data;
        return {
          url: `${apiPrefix}/sp-datasource/${datasourceId}`,
          method: 'GET',
          params: {},
          data: {},
        };
      },
    },
  };
};
const dataSourceListDS = () => {
  return {
    autoQuery: false,
    selection: false,
    autoQueryAfterSubmit: false,
    dataKey: 'content',
    fields: [
      {
        name: 'datasourceOrder',
        type: 'number',
        label: intl.get('hzero.common.view.serialNumber').d('序号'),
      },
      {
        name: 'datasourceName',
        label: intl
          .get('hadm.LogicalDataSource.model.logicalData.datasourceName')
          .d('逻辑数据源名称'),
        type: 'string',
      },
      {
        name: 'datasourceInfo',
        type: 'string',
        label: intl
          .get('hadm.LogicalDataSource.model.logicalData.datasourceInfo')
          .d('数据源连接信息'),
      },
    ],
    transport: {
      read: () => ({
        url: `${apiPrefix}/sp-datasource`,
        method: 'GET',
      }),
      destroy: ({ data }) => {
        const { __id, _status, ...other } = Array.isArray(data) ? data[0] : {};
        return {
          data: other,
          url: `${apiPrefix}/sp-datasource`,
          method: 'DELETE',
        };
      },
      create: ({ data }) => {
        const { __id, _status, ...other } = Array.isArray(data) ? data[0] : {};
        return {
          url: `${apiPrefix}/sp-datasource`,
          method: 'POST',
          data: other,
        };
      },
      update: ({ data }) => {
        const { __id, _status, ...other } = Array.isArray(data) ? data[0] : {};
        return {
          url: `${apiPrefix}/sp-datasource`,
          method: 'PUT',
          data: other,
        };
      },
    },
  };
};

const dataSourceFormDS = () => {
  return {
    fields: [
      {
        name: 'url',
        label: intl.get('hadm.LogicalDataSource.model.logicalData.url').d('数据源URL'),
        type: 'string',
        maxLength: 120,
        dynamicProps: {
          required({ record }) {
            return record.get('isRequired');
          },
        },
      },
      {
        name: 'username',
        type: 'string',
        maxLength: 60,
        label: intl.get('hadm.LogicalDataSource.model.logicalData.username').d('数据源用户名'),
        dynamicProps: {
          required({ record }) {
            return record.get('isRequired');
          },
        },
      },
      {
        name: 'password',
        maxLength: 60,
        label: intl.get('hadm.LogicalDataSource.model.logicalData.password').d('数据源密码'),
        type: 'string',
        dynamicProps: {
          required({ record }) {
            return record.get('isRequired');
          },
        },
      },
      {
        name: 'connectionTimeoutMilliseconds',
        type: 'number',
        min: 0,
        max: 999999,
        label: intl
          .get('hadm.LogicalDataSource.model.logicalData.connectionTime')
          .d('连接超时时间'),
      },
      {
        name: 'idleTimeoutMilliseconds',
        label: intl.get('hadm.LogicalDataSource.model.logicalData.idleTimeout').d('空闲超时时间'),
        type: 'number',
        min: 0,
        max: 999999,
      },
      {
        name: 'maxLifetimeMilliseconds',
        type: 'number',
        min: 0,
        // max: 999999,
        label: intl.get('hadm.LogicalDataSource.model.logicalData.maxLifetime').d('最长生命周期'),
      },
      {
        name: 'maxPoolSize',
        type: 'number',
        min: 0,
        max: 999999,
        label: intl.get('hadm.LogicalDataSource.model.logicalData.maxPoolSize').d('最大连接池大小'),
      },
      {
        name: 'isRequired', // 用于判断字段是否必输
        type: 'boolean',
        defaultValue: false,
      },
    ],
  };
};

// 更改数据序号DS
const orderDS = () => {
  return {
    autoCreate: false,
    autoQuery: false,
    autoQueryAfterSubmit: false,
    fields: [],
    transport: {
      create: ({ data }) => {
        const { dstOrder, srcDatasourceId } = Array.isArray(data) ? data[0] : {};
        return {
          url: `${apiPrefix}/sp-datasource/${srcDatasourceId}/order/${dstOrder}`,
          method: 'PUT',
          params: {},
          data: {},
        };
      },
    },
  };
};

// 设为默认DS
const setDefaultDS = () => {
  return {
    autoCreate: false,
    autoQuery: false,
    autoQueryAfterSubmit: false,
    fields: [],
    transport: {
      create: ({ data }) => {
        const { __id, _status, ...other } = Array.isArray(data) ? data[0] : {};
        return {
          url: `${apiPrefix}/sp-datasource-groups`,
          method: 'PUT',
          params: {},
          data: other,
        };
      },
    },
  };
};

export { formDS, tableDS, orderDS, drawerDS, setDefaultDS, dataSourceListDS, dataSourceFormDS };
