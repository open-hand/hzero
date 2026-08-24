/*
 * Maintain 运维模式管理
 * @date: 2020-04-22
 * @author XL <liang.xiong@hand-china.com>
 * @copyright Copyright (c) 2020, Hand
 */
import intl from 'utils/intl';
import { HZERO_ADM } from 'utils/config';
import { isTenantRoleLevel, getCurrentOrganizationId } from 'utils/utils';

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
        name: 'maintainVersion',
        type: 'string',
        label: intl.get('hadm.maintain.model.maintain.maintainVersion').d('运维版本'),
      },
      {
        name: 'state',
        type: 'string',
        lookupCode: 'HADM.MAINTAIN_STATE',
        label: intl.get('hadm.maintain.model.maintain.state').d('运维状态'),
      },
    ],
    fields: [
      {
        name: 'maintainVersion',
        type: 'string',
        label: intl.get('hadm.maintain.model.maintain.maintainVersion').d('运维版本'),
      },
      {
        name: 'description',
        type: 'string',
        label: intl.get('hzero.common.view.description').d('描述'),
      },
      {
        name: 'stateMeaning',
        type: 'string',
        label: intl.get('hadm.maintain.model.maintain.state').d('运维状态'),
      },
      {
        name: 'creationDate',
        type: 'dateTime',
        label: intl.get('hadm.maintain.model.maintain.creationDate').d('创建时间'),
      },
    ],
    transport: {
      read: () => ({
        url: `${apiPrefix}/maintains`,
        method: 'GET',
      }),
      destroy: ({ data }) => {
        const { __id, _status, ...other } = Array.isArray(data) ? data[0] : {};
        return {
          params: {
            maintainId: other.maintainId,
          },
          url: `${apiPrefix}/maintains`,
          method: 'DELETE',
        };
      },
    },
  };
};

const detailDS = () => {
  return {
    autoQuery: false,
    autoQueryAfterSubmit: false,
    autoCreate: true,
    dataKey: 'content',
    fields: [
      {
        name: 'maintainVersion',
        type: 'string',
        label: intl.get('hadm.maintain.model.maintain.maintainVersion').d('运维版本'),
        required: true,
      },
      {
        name: 'description',
        type: 'string',
        label: intl.get('hzero.common.view.description').d('描述'),
      },
      {
        name: 'state',
        type: 'string',
        label: intl.get('hadm.maintain.model.maintain.state').d('运维状态'),
        lookupCode: 'HADM.MAINTAIN_STATE',
      },
    ],
    transport: {
      read: ({ dataSet }) => {
        const {
          queryParameter: { maintainId },
        } = dataSet;
        return {
          url: `${apiPrefix}/maintains/${maintainId}`,
          method: 'GET',
          data: {},
          params: {},
        };
      },
      create: ({ data }) => {
        const { __id, _status, ...other } = Array.isArray(data) ? data[0] : {};
        return {
          url: `${apiPrefix}/maintains`,
          method: 'POST',
          data: {
            ...other,
            state: 'UNUSED',
          },
        };
      },
      update: ({ data }) => {
        const { __id, _status, ...other } = Array.isArray(data) ? data[0] : {};
        return {
          url: `${apiPrefix}/maintains`,
          method: 'PUT',
          data: other,
        };
      },
    },
  };
};

// 详情页面下的表格信息DS
const detailTableDS = () => {
  return {
    selection: false,
    autoQuery: false,
    autoCreate: false,
    queryFields: [
      {
        name: 'serviceCode',
        type: 'string',
        label: intl.get('hadm.maintain.model.maintain.serviceCode').d('服务名'),
      },
      {
        name: 'tableName',
        type: 'string',
        label: intl.get('hadm.maintain.model.maintain.tableName').d('表名'),
      },
    ],
    fields: [
      {
        name: 'serviceLov',
        type: 'object',
        label: intl.get('hadm.maintain.model.maintain.serviceCode').d('服务名'),
        lovCode: 'HADM.SERVICE',
        ignore: 'always',
        noCache: true,
        textField: 'serviceCode',
      },
      // {
      //   name: 'serviceId',
      //   type: 'string',
      //   bind: 'serviceLov.serviceId',
      // },
      {
        name: 'serviceCode',
        type: 'string',
        bind: 'serviceLov.serviceCode',
        required: true,
      },
      {
        name: 'tableName',
        type: 'string',
        label: intl.get('hadm.maintain.model.maintain.tableName').d('表名'),
        required: true,
      },
      {
        name: 'maintainMode',
        type: 'string',
        label: intl.get('hadm.maintain.model.maintain.maintainMode').d('读写模式'),
        lookupCode: 'HADM.TABLE_MAINTAIN_MODE',
        multiple: true,
        required: true,
      },
    ],
    transport: {
      read: () => ({
        url: `${apiPrefix}/maintain-tables`,
        method: 'GET',
        transformResponse: (res) => {
          let parsedData = {};
          try {
            parsedData = JSON.parse(res);
            const { content = [] } = parsedData;
            const newContent = content.map((item) => {
              return {
                ...item,
                maintainMode: [item.maintainMode],
              };
            });
            parsedData = {
              ...parsedData,
              content: newContent,
            };
          } catch (e) {
            // do nothing, use default error deal
          }
          return parsedData;
        },
      }),
      create: ({ data, dataSet }) => {
        const {
          queryParameter: { maintainId },
        } = dataSet;
        const newData = data.map((item) => {
          const { __id, _status, ...other } = item;
          const { maintainMode } = other;
          if (maintainMode.includes('WRITE')) {
            other.maintainMode = 'WRITE';
          } else {
            other.maintainMode = 'READ';
          }
          return {
            ...other,
            maintainId,
          };
        });
        return {
          url: `${apiPrefix}/maintain-tables/batch`,
          method: 'PUT',
          data: newData,
        };
      },
      update: ({ data }) => {
        const newData = data.map((item) => {
          const { __id, _status, ...other } = item;
          const { maintainMode } = other;
          if (maintainMode.includes('WRITE')) {
            other.maintainMode = 'WRITE';
          } else {
            other.maintainMode = 'READ';
          }
          return other;
        });

        return {
          url: `${apiPrefix}/maintain-tables/batch`,
          method: 'PUT',
          data: newData,
        };
      },
      destroy: ({ data, dataSet }) => {
        const {
          queryParameter: { maintainId },
        } = dataSet;
        const { __id, _status, ...other } = Array.isArray(data) ? data[0] : {};
        const { maintainMode } = other;
        if (maintainMode.includes('WRITE')) {
          other.maintainMode = 'WRITE';
        } else {
          other.maintainMode = 'READ';
        }
        return {
          params: {
            maintainTableId: other.maintainTableId,
            maintainId,
          },
          url: `${apiPrefix}/maintain-tables`,
          method: 'DELETE',
          data: null,
        };
      },
    },
  };
};

export { tableDS, detailDS, detailTableDS };
