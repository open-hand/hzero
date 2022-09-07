import intl from 'utils/intl';
import { HZERO_IM } from 'utils/config';
import { getCurrentOrganizationId, getResponse } from 'utils/utils';

const organizationId = getCurrentOrganizationId();
const apiPrefix = `${HZERO_IM}/v1/${organizationId}`;
function getItem(arr = [], target) {
  arr.forEach((item) => {
    target.push(item);
    if (item.children) {
      getItem(item.children, target);
    }
  });
}

function getChildren(arr, target) {
  if (arr.children) {
    target.push(arr.children);
    getItem(arr.children, target);
  }
}

// init ds
const InitDs = () => ({
  autoQuery: true,
  selection: false,
  dataKey: 'content',
  queryFields: [
    {
      name: 'groupName',
      type: 'string',
      label: intl.get('hims.customerGroup.model.customerGroup.groupName').d('群组名称'),
    },
    {
      name: 'description',
      type: 'string',
      label: intl.get('hims.customerGroup.model.customerGroup.description').d('群组描述'),
    },
  ],
  fields: [
    {
      name: 'groupKey',
      type: 'string',
      label: intl.get('hims.customerGroup.model.customerGroup.id').d('群组标识'),
      required: true,
      maxLength: 60,
    },
    {
      name: 'groupName',
      type: 'string',
      label: intl.get('hims.customerGroup.model.customerGroup.groupName').d('群组名称'),
      required: true,
      maxLength: 60,
    },
    {
      name: 'description',
      type: 'string',
      label: intl.get('hims.customerGroup.model.customerGroup.description').d('群组描述'),
      maxLength: 480,
    },
    {
      name: 'enabledFlag',
      type: 'boolean',
      label: intl.get('hims.customerGroup.model.customerGroup.status').d('状态'),
      trueValue: 1,
      falseValue: 0,
      defaultValue: 1,
    },
  ],
  transport: {
    read: (config) => {
      const { dataSet } = config;
      return {
        ...config,
        dataSet,
        method: 'GET',
        url: `${apiPrefix}/custom-svc/groups`,
        // 拦截数据
        transformResponse: (data) => {
          let formatData = {};
          try {
            formatData = JSON.parse(data);
          } catch (e) {
            return e;
          }
          if (getResponse(formatData)) {
            return formatData.data;
          }
        },
      };
    },
    create: (config) => {
      const { __id, _status, ...other } = Array.isArray(config.data) ? config.data[0] : {};
      return {
        ...config,
        data: other,
        method: 'POST',
        url: `${apiPrefix}/custom-svc/groups`,
      };
    },
    /**
     * @param {Array} params.permissionList = ['hims.customer-group.button.edit']
     */
    update: (config) => {
      const { __id, _status, ...other } = Array.isArray(config.data) ? config.data[0] : {};
      return {
        ...config,
        data: other,
        method: 'PUT',
        url: `${apiPrefix}/custom-svc/groups`,
      };
    },
  },
});

// 客服人员ds
const CustomerDS = () => ({
  selection: false,
  autoQuery: false,
  fields: [
    {
      label: intl.get('hims.customerGroup.model.customerGroup.user').d('客服人员'),
      name: 'userLov',
      type: 'object',
      lovCode: 'HIAM.USER.ORG',
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
      const { dataSet = {}, params } = config;
      const { csGroupId } = dataSet;
      return {
        ...config,
        method: 'get',
        url: `${apiPrefix}/custom-svc/groups/user`,
        params: {
          csGroupId,
          ...params,
        },
        // 配置该属性后需要拦截错误
        transformResponse: (data) => {
          let formatData = {};
          try {
            formatData = JSON.parse(data);
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
      const { dataSet = {} } = config;
      const { csGroupId } = dataSet;
      const { __id, _status, ...other } = Array.isArray(config.data) ? config.data[0] : {};
      return {
        ...config,
        data: other,
        method: 'post',
        url: `${apiPrefix}/custom-svc/groups/${csGroupId}/user/${other.userId}`,
      };
    },
    destroy: (config) => {
      const { dataSet = {} } = config;
      const { csGroupId } = dataSet;
      const { __id, _status, ...other } = Array.isArray(config.data) ? config.data[0] : {};
      return {
        ...config,
        data: other,
        method: 'delete',
        url: `${apiPrefix}/custom-svc/groups/${csGroupId}/user/${other.id}`,
      };
    },
  },
});

// 知识标签ds
const TagDS = () => ({
  selection: 'multiple',
  fields: [
    {
      name: 'categoryCode',
      type: 'string',
      label: intl.get('hims.knowledgeCategory.model.kc.categoryCode').d('知识类别编码'),
      maxLength: 30,
    },
    {
      name: 'categoryName',
      type: 'string',
      label: intl.get('hims.knowledgeCategory.model.kc.categoryName').d('知识类别名称'),
      maxLength: 120,
    },
  ],
  transport: {
    read: (config) => {
      const { dataSet = {} } = config;
      const { csGroupId, userId } = dataSet;
      return {
        ...config,
        method: 'get',
        url: `${apiPrefix}/cs-group-user-tags/${csGroupId}/${userId}`,
      };
    },
    destroy: (config) => {
      const { data } = config;
      return {
        url: `${apiPrefix}/cs-group-user-tags`,
        data,
        method: 'DELETE',
      };
    },
  },
});

const knowledgeDs = () => ({
  autoQuery: true,
  selection: 'multiple',
  dataKey: 'content',
  parentField: 'parentId',
  idField: 'categoryId',
  fields: [
    {
      name: 'categoryCode',
      type: 'string',
      label: intl.get('hims.knowledgeCategory.model.kc.categoryCode').d('知识类别编码'),
      maxLength: 30,
    },
    {
      name: 'categoryName',
      type: 'string',
      label: intl.get('hims.knowledgeCategory.model.kc.categoryName').d('知识类别名称'),
      maxLength: 120,
    },
  ],
  transport: {
    read: () => ({
      url: `${HZERO_IM}/v1/knowledge-categories/get-tree`,
      params: { enabledFlag: 1 },
      method: 'GET',
      transformResponse: (res) => {
        const list = [];
        let formatData = [];
        try {
          formatData = JSON.parse(res);
        } catch (e) {
          return e;
        }
        if (getResponse(formatData)) {
          getItem(formatData, list);
        }
        return list;
      },
    }),
  },
  events: {
    select: ({ dataSet, record }) => {
      const list = [];
      if (record.children && record.isSelected) {
        getChildren(record, list);
        list.forEach((item) => {
          dataSet.select(item);
        });
      }
    },
    unselect: ({ dataSet, record }) => {
      const list = [];
      if (record.children) {
        getChildren(record, list);
        list.forEach((item) => {
          dataSet.unSelect(item);
        });
      }
    },
  },
});

export { InitDs, CustomerDS, TagDS, knowledgeDs };
