import intl from 'utils/intl';
import { HZERO_IM } from 'utils/config';
import { getResponse, getCurrentOrganizationId } from 'utils/utils';

const organizationId = getCurrentOrganizationId();
const apiPrefix = `${HZERO_IM}/v1/${organizationId}`;

// 初始表格ds
const InitDs = () => ({
  autoQuery: true,
  selection: false,
  dataKey: 'content',
  queryFields: [
    {
      name: 'groupName',
      type: 'string',
      label: intl.get('hims.innerCustomerGroup.model.innerCustomerGroup.groupName').d('群组名称'),
    },
    {
      name: 'description',
      type: 'string',
      label: intl.get('hims.innerCustomerGroup.model.innerCustomerGroup.description').d('群组描述'),
    },
  ],
  fields: [
    {
      name: 'groupKey',
      type: 'string',
      label: intl.get('hims.innerCustomerGroup.model.innerCustomerGroup.groupKey').d('群组标识'),
      required: true,
      maxLength: 60,
    },
    {
      name: 'groupName',
      type: 'string',
      label: intl.get('hims.innerCustomerGroup.model.innerCustomerGroup.groupName').d('群组名称'),
      required: true,
      maxLength: 60,
    },
    {
      name: 'description',
      type: 'string',
      label: intl.get('hims.innerCustomerGroup.model.innerCustomerGroup.description').d('群组描述'),
      maxLength: 480,
    },
    {
      name: 'enabledFlag',
      type: 'boolean',
      label: intl.get('hims.innerCustomerGroup.model.innerCustomerGroup.enabledFlag').d('状态'),
      trueValue: 1,
      falseValue: 0,
      defaultValue: 1,
    },
  ],
  transport: {
    read: (config) => ({
      ...config,
      method: 'GET',
      url: `${apiPrefix}/inner-cs-groups`,
    }),
  },
});

// 客服人员ds
const CustomerDS = () => ({
  selection: false,
  autoQuery: false,
  fields: [
    {
      label: intl.get('hims.innerCustomerGroup.model.innerCustomerGroup.user').d('客服人员'),
      name: 'userLov',
      type: 'object',
      lovCode: 'HIAM.USER',
      ignore: 'always',
      required: true,
      noCache: true,
    },
    {
      name: 'userId',
      type: 'string',
      bind: 'userLov.id',
    },
    {
      name: 'realName',
      type: 'string',
      bind: 'userLov.realName',
    },
  ],
  transport: {
    read: (config) => {
      const { params, data } = config;
      const { innerCsGroupId } = data;
      return {
        ...config,
        method: 'get',
        data: {},
        url: `${apiPrefix}/inner-cs-group-users/${innerCsGroupId}`,
        params: {
          ...params,
        },
        // 配置该属性后需要拦截错误
        transformResponse: (value) => {
          let formatData = {};
          try {
            formatData = JSON.parse(value);
          } catch (e) {
            return e;
          }
          if (getResponse(formatData)) {
            return formatData.content.map((item) => ({
              id: item.id,
              realName: item.nick,
              ...item,
            }));
          }
        },
      };
    },
    create: (config) => {
      const {
        dataSet: { queryParameter = {} },
      } = config;
      const { innerCsGroupId } = queryParameter;
      const { userId } = Array.isArray(config.data) ? config.data[0] : {};
      return {
        ...config,
        data: { userId, innerCsGroupId },
        method: 'post',
        url: `${apiPrefix}/inner-cs-group-users`,
      };
    },
    destroy: (config) => {
      const {
        dataSet: { queryParameter = {} },
      } = config;
      const { innerCsGroupId } = queryParameter;
      const { id } = Array.isArray(config.data) ? config.data[0] : {};
      return {
        ...config,
        data: { userId: id, innerCsGroupId },
        method: 'delete',
        url: `${apiPrefix}/inner-cs-group-users`,
      };
    },
  },
});

// 内部群组详情ds
const InnerGroupDS = () => ({
  dataKey: 'content',
  autoQuery: false,
  fields: [
    {
      name: 'groupKey',
      type: 'string',
      label: intl.get('hims.innerCustomerGroup.model.innerCustomerGroup.groupKey').d('群组标识'),
      required: true,
      maxLength: 60,
    },
    {
      name: 'groupName',
      type: 'string',
      label: intl.get('hims.innerCustomerGroup.model.innerCustomerGroup.groupName').d('群组名称'),
      required: true,
      maxLength: 60,
    },
    {
      name: 'description',
      type: 'string',
      label: intl.get('hims.innerCustomerGroup.model.innerCustomerGroup.description').d('群组描述'),
      maxLength: 480,
    },
    {
      name: 'enabledFlag',
      type: 'boolean',
      label: intl.get('hims.innerCustomerGroup.model.innerCustomerGroup.enabledFlag').d('状态'),
      trueValue: 1,
      falseValue: 0,
      defaultValue: 1,
    },
  ],
  transport: {
    read: ({ data }) => {
      const { innerCsGroupId } = data;
      return {
        method: 'get',
        data: {},
        url: `${apiPrefix}/inner-cs-groups/${innerCsGroupId}`,
        params: {},
      };
    },
    create: (config) => {
      const { __id, _status, ...other } = Array.isArray(config.data) ? config.data[0] : {};
      return {
        ...config,
        data: { ...other, tenantId: organizationId },
        method: 'post',
        url: `${apiPrefix}/inner-cs-groups`,
      };
    },
    update: (config) => {
      const { __id, _status, ...other } = Array.isArray(config.data) ? config.data[0] : {};
      return {
        ...config,
        data: { ...other, tenantId: organizationId },
        method: 'put',
        url: `${apiPrefix}/inner-cs-groups`,
      };
    },
  },
});

export { InitDs, CustomerDS, InnerGroupDS };
