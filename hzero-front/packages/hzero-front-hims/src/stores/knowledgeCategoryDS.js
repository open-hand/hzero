import intl from 'utils/intl';
import { HZERO_IM } from 'utils/config';
import { CODE } from 'utils/regExp';
import { getCurrentOrganizationId, getResponse, isTenantRoleLevel } from 'utils/utils';

const organizationId = getCurrentOrganizationId();
const apiPrefix = isTenantRoleLevel() ? `${HZERO_IM}/v1/${organizationId}` : `${HZERO_IM}/v1`;
function getItem(arr = [], target) {
  arr.forEach((item) => {
    target.push(item);
    if (Array.isArray(item.children)) {
      getItem(item.children, target);
    }
  });
}

const initDS = () => ({
  autoQuery: true,
  selection: false,
  dataKey: 'content',
  parentField: 'parentId',
  idField: 'categoryId',
  queryFields: [
    {
      name: 'categoryCode',
      type: 'string',
      label: intl.get('hims.knowledgeCategory.model.kc.categoryCode').d('知识类别编码'),
    },
    {
      name: 'categoryName',
      type: 'string',
      label: intl.get('hims.knowledgeCategory.model.kc.categoryName').d('知识类别名称'),
    },
  ],
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
    {
      name: 'typeMeaning',
      type: 'string',
      label: intl.get('hims.knowledgeCategory.model.kc.typeMeaning').d('类型'),
    },
    {
      name: 'enabledFlag',
      type: 'boolean',
      label: intl.get('hims.knowledgeCategory.model.kc.enabledFlag').d('状态'),
      trueValue: 1,
      falseValue: 0,
      defaultValue: 1,
    },
    {
      name: 'action',
      label: intl.get('hzero.common.button.action').d('操作'),
    },
  ],
  transport: {
    read: ({ params }) => ({
      url: `${apiPrefix}/knowledge-categories`,
      params,
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
});

// 模态框ds
const drawerDS = () => ({
  autoQuery: false,
  autoQueryAfterSubmit: false,
  fields: [
    {
      name: 'categoryCode',
      type: 'string',
      required: true,
      pattern: CODE,
      label: intl.get('hims.knowledgeCategory.model.kc.categoryCode').d('知识类别编码'),
      maxLength: 30,
      defaultValidationMessages: {
        patternMismatch: intl
          .get('hzero.common.validation.code')
          .d('大小写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
      },
    },
    {
      name: 'categoryName',
      type: 'intl',
      required: true,
      label: intl.get('hims.knowledgeCategory.model.kc.categoryName').d('知识类别名称'),
      maxLength: 120,
    },
    {
      name: 'type',
      type: 'string',
      required: true,
      label: intl.get('hims.knowledgeCategory.model.kc.typeMeaning').d('类型'),
    },
    {
      name: 'parentCategoryLov',
      type: 'object',
      ignore: 'always',
      lovCode: 'HIMS.KNOWLEDGE_CATEGORIES',
      noCache: true,
      label: intl.get('hims.knowledgeCategory.model.kc.parentCategory').d('父级知识类别'),
      dynamicProps: {
        lovPara: ({ record }) => ({
          type: 'dir',
          tenantId: organizationId,
          categoryId: record.get('categoryId'),
        }),
      },
    },
    {
      name: 'parentCategoryName',
      type: 'string',
      bind: 'parentCategoryLov.categoryName',
    },
    {
      name: 'parentId',
      type: 'string',
      bind: 'parentCategoryLov.categoryId',
    },
  ],
  transport: {
    read: ({ data }) => {
      const { categoryId } = data;
      return {
        url: `${apiPrefix}/knowledge-categories/${categoryId}`,
        method: 'GET',
        data: {},
        params: {},
      };
    },
    create: ({ data }) => {
      const { __id, _status, ...other } = Array.isArray(data) ? data[0] : {};
      return {
        url: `${apiPrefix}/knowledge-categories`,
        method: 'POST',
        data: {
          parentId: null,
          ...other,
        },
      };
    },
    update: ({ data }) => {
      const { __id, _status, ...other } = Array.isArray(data) ? data[0] : {};
      return {
        url: `${apiPrefix}/knowledge-categories`,
        method: 'PUT',
        data: {
          parentId: null,
          ...other,
        },
      };
    },
  },
});

export { initDS, drawerDS };
