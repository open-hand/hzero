import intl from 'utils/intl';
import { HZERO_IM } from 'utils/config';
import { isTenantRoleLevel, getCurrentOrganizationId } from 'utils/utils';

const organizationId = getCurrentOrganizationId();
const apiPrefix = isTenantRoleLevel() ? `${HZERO_IM}/v1/${organizationId}` : `${HZERO_IM}/v1`;

// 知识维护-表格ds
const detailTableDS = () => ({
  autoQuery: true,
  selection: false,
  dataKey: 'content',
  queryFields: [
    {
      name: 'category',
      type: 'object',
      label: intl.get('hims.knowledge.model.knowledge.categoryId').d('知识类别'),
      ignore: 'always',
      lovCode: 'HIMS.KNOWLEDGE_CATEGORIES',
      lovPara: { type: 'qa', tenantId: organizationId },
      noCache: true,
    },
    {
      name: 'categoryId',
      type: 'string',
      bind: 'category.categoryId',
    },
    {
      name: 'questionTitle',
      type: 'string',
      label: intl.get('hims.knowledge.model.knowledge.questionTitle').d('问题标题'),
    },
    {
      name: 'keyWord',
      type: 'string',
      label: intl.get('hims.knowledge.model.knowledge.keyWord').d('关键词'),
    },
  ],
  fields: [
    {
      name: 'category',
      type: 'object',
      label: intl.get('hims.knowledge.model.knowledge.categoryId').d('知识类别'),
      ignore: 'always',
      required: true,
      lovCode: 'HIMS.KNOWLEDGE_CATEGORIES',
      lovPara: { type: 'qa', tenantId: organizationId },
      noCache: true,
    },
    {
      name: 'categoryId',
      label: intl.get('hims.messageCenter.model.messageCenter.categoryId').d('知识类别'),
      type: 'string',
      required: true,
      bind: 'category.categoryId',
    },
    {
      name: 'categoryName',
      label: intl.get('hims.messageCenter.model.messageCenter.categoryId').d('知识类别'),
      type: 'string',
      bind: 'category.categoryName',
    },
    {
      name: 'questionTitle',
      type: 'string',
      required: true,
      label: intl.get('hims.knowledge.model.knowledge.questionTitle').d('问题标题'),
    },
    {
      name: 'keyWord',
      type: 'string',
      label: intl.get('hims.knowledge.model.knowledge.keyWord').d('关键词'),
    },
    {
      name: 'answerDesc',
      type: 'string',
      required: true,
      label: intl.get('hims.knowledge.model.knowledge.answerDesc').d('答案内容'),
    },
    {
      name: 'submitterName',
      type: 'string',
      label: intl.get('hims.knowledge.model.knowledge.submitterName').d('提交人'),
    },
    {
      name: 'submitDate',
      type: 'string',
      label: intl.get('hims.knowledge.model.knowledge.submitDate').d('提交时间'),
    },
    {
      name: 'checkerName',
      type: 'string',
      label: intl.get('hims.knowledge.model.knowledge.checkerName').d('审核人'),
    },
    {
      name: 'checkDate',
      type: 'string',
      label: intl.get('hims.knowledge.model.knowledge.checkDate').d('审核时间'),
    },
    {
      name: 'checkStatus',
      type: 'string',
      label: intl.get('hims.knowledge.model.knowledge.checkStatus').d('审核状态'),
    },
    {
      name: 'enabledFlag',
      type: 'boolean',
      trueValue: 1,
      falseValue: 0,
      defaultValue: 1,
      label: intl.get('hims.knowledge.model.knowledge.enabledFlag').d('状态'),
    },
    {
      name: 'rejectReason',
      type: 'string',
      label: intl.get('hims.knowledge.model.knowledge.rejectReason').d('驳回原因'),
    },
    {
      name: 'action',
      label: intl.get('hzero.common.button.action').d('操作'),
    },
  ],
  transport: {
    read: ({ params, data }) => {
      const { lang, ...other } = data;
      return {
        url: `${apiPrefix}/knowledge`,
        params: { ...params, ...other, lang },
        data: {},
        method: 'GET',
      };
    },
    create: ({ data, dataSet }) => {
      const {
        queryParameter: { lang },
      } = dataSet;
      const { __id, _status, submitterName, checkerName, categoryId, ...other } = Array.isArray(
        data
      )
        ? data[0]
        : {};
      return {
        url: `${apiPrefix}/knowledge`,
        data: {
          ...other,
          lang,
          tenantId: organizationId,
          categoryId: String(categoryId),
        },
        method: 'POST',
      };
    },
    update: ({ data, dataSet }) => {
      const {
        queryParameter: { lang },
      } = dataSet;
      const {
        __id,
        _status,
        createAt,
        updateAt,
        checkDate,
        checkerName,
        submitterName,
        checkStatusName,
        categoryId,
        ...other
      } = Array.isArray(data) ? data[0] : {};
      return {
        url: `${apiPrefix}/knowledge`,
        data: {
          ...other,
          lang,
          tenantId: organizationId,
          categoryId: String(categoryId),
        },
        method: 'PUT',
      };
    },
  },
});

export { detailTableDS };
