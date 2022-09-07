import intl from 'utils/intl';
import { HZERO_IM } from 'utils/config';
import {
  getResponse,
  getCurrentUserId,
  getCurrentOrganizationId,
  isTenantRoleLevel,
} from 'utils/utils';

const organizationId = getCurrentOrganizationId();
const apiPrefix = isTenantRoleLevel() ? `${HZERO_IM}/v1/${organizationId}` : `${HZERO_IM}/v1`;

const groupDs = () => ({
  transport: {
    read() {
      return {
        url: `${apiPrefix}/custom-svc/user/group/list`,
        method: 'GET',
        params: {},
      };
    },
  },
});

const iniDs = () => ({
  transport: {
    read() {
      return {
        url: `${HZERO_IM}/v1/user/init`,
        method: 'POST',
        params: {},
      };
    },
  },
});

const selfDs = () => ({
  transport: {
    read() {
      return {
        url: `${HZERO_IM}/v1/user/self`,
        method: 'POST',
        params: {},
      };
    },
  },
});

// init ds
const initDs = () => ({
  primaryKey: 'csGroupRelationId',
  selection: false,
  dataKey: 'content',
  queryFields: [],
  fields: [
    {
      name: 'csGroupId',
      type: 'string',
    },
    {
      name: 'id',
      type: 'string',
    },
    {
      name: 'userName',
      type: 'string',
    },
    {
      name: 'userEmail',
      type: 'string',
    },
    {
      name: 'clientIp',
      type: 'string',
    },
    {
      name: 'enabledFlag',
      type: 'string',
    },
    {
      name: 'createAt',
      type: 'string',
    },
    {
      name: 'updateAt',
      type: 'string',
    },
  ],
  transport: {
    read: (config) => {
      const { dataSet, params } = config;
      const { status, groupKey } = dataSet;
      return {
        ...config,
        dataSet,
        url: `${apiPrefix}/custom-svc/user/get-user-list`,
        method: 'GET',
        params: {
          ...params,
          status,
          groupKey,
        },
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
  },
});

const messageDs = () => ({
  selection: false,
  dataKey: 'content',
  queryFields: [],
  fields: [
    {
      name: 'content',
      type: 'string',
    },
    {
      name: 'from',
      type: 'string',
    },
    {
      name: 'createAt',
      type: 'string',
    },
  ],
  transport: {
    read: (config) => {
      const { dataSet, params } = config;
      const { groupKey } = dataSet;
      return {
        url: `${HZERO_IM}/v1/message/get-feedback-list`,
        method: 'GET',
        params: {
          ...params,
          groupKey,
        },
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
  },
});

const messageListDs = () => ({
  selection: false,
  paging: false,
  dataKey: 'data',
  queryFields: [],
  transport: {
    read: (config) => {
      const { dataSet } = config;
      const { groupKey, userId } = dataSet;
      return {
        url: `${HZERO_IM}/v1/message/get-cs-list`,
        method: 'GET',
        params: {
          groupKey,
          userId,
        },
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
  },
});

const messageAmountDs = () => ({
  selection: false,
  paging: false,
  dataKey: 'data',
  queryFields: [],
  transport: {
    read: (config) => {
      const { dataSet } = config;
      const { groupKey } = dataSet;
      return {
        url: `${HZERO_IM}/v1/message/get-conversation-count`,
        method: 'GET',
        params: {
          groupKey,
        },
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
  },
});

const closeConnectDs = () => ({
  selection: false,
  paging: false,
  queryFields: [],
  transport: {
    read: (config) => {
      const { dataSet } = config;
      const { groupId, to, from, content, contentType, messageType, chatType } = dataSet;
      return {
        url: `${HZERO_IM}/v1/message/cs-close`,
        method: 'POST',
        params: {
          groupId,
          to,
          from,
          content,
          contentType, // 内容类型，通知/聊天
          messageType, // 消息类型，text(1)/image(xxx)
          chatType, // 消息对象类型，用户(1)/群组(2)/临时会话(3)
        },
      };
    },
  },
});

const assessmentDs = () => ({
  selection: false,
  paging: false,
  queryFields: [],
  fields: [
    {
      name: 'remark',
      type: 'string',
      label: intl.get('hims.messageCenter.view.message.assessment.title.detail').d('评价'),
    },
  ],
  transport: {
    read: (config) => {
      const { data } = config;
      return {
        url: `${HZERO_IM}/v1/evaluation/mutual/save`,
        method: 'POST',
        data,
      };
    },
  },
});

const lovDs = () => ({
  autoCreate: true,
  autoQueryAfterSubmit: false,
  fields: [
    {
      name: 'csGroup',
      type: 'object',
      lovCode: 'HIMS.CS_GROUP',
      label: intl.get('hims.messageCenter.model.messageCenter.csGroup').d('选择群组'),
      textField: 'groupName',
      required: true,
      ignore: 'always',
      noCache: true,
    },
    {
      name: 'csGroupUserTag',
      type: 'object',
      label: intl.get('hims.messageCenter.model.messageCenter.csGroupUserTag').d('选择知识类别'),
      cascadeMap: { csGroupId: 'csGroupId' },
      lovCode: 'HIMS.CS_GROUP_USER_TAG',
      textField: 'categoryName',
      ignore: 'always',
      noCache: true,
    },
    {
      name: 'csGroupUser',
      type: 'object',
      label: intl.get('hims.messageCenter.model.messageCenter.csGroupUser').d('选择群组客服'),
      cascadeMap: { csGroupId: 'csGroupId' },
      lovCode: 'HIMS.CS_GROUP_USER',
      textField: 'realName',
      ignore: 'always',
      noCache: true,
    },
    {
      name: 'csGroupId',
      type: 'string',
      label: 'csGroupId',
      bind: 'csGroup.csGroupId',
    },
    {
      name: 'groupKey',
      type: 'string',
      label: intl.get('hims.messageCenter.model.messageCenter.csGroup').d('选择群组'),
      bind: 'csGroup.groupKey',
    },
    {
      name: 'categoryId',
      type: 'string',
      label: 'categoryId',
      bind: 'csGroupUserTag.categoryId',
    },
    {
      name: 'csUserId',
      type: 'string',
      label: 'csUserId',
      bind: 'csGroupUser.id',
    },
  ],
  transport: {
    create: ({ data, dataSet }) => {
      const { queryParameter } = dataSet;
      const { csUserId, categoryId, groupKey } = data[0];
      return {
        url: `${HZERO_IM}/v1/message/cs-transfer`,
        params: { ...queryParameter, csUserId, categoryId, groupKey },
        data: undefined,
        method: 'POST',
      };
    },
  },
});

const formDs = ({ defaultValue }) => ({
  autoCreate: true,
  paging: false,
  transport: {
    create: (config) => {
      const { data } = config;
      const { categoryId, ...others } = data[0];
      return {
        url: `${apiPrefix}/knowledge/${categoryId}/mark`,
        method: 'POST',
        data: {
          ...others,
        },
        params: {},
      };
    },
  },
  fields: [
    {
      name: 'questionTitle',
      type: 'string',
      required: true,
      label: intl.get('hims.messageCenter.model.messageCenter.questionTitle').d('问题标题'),
    },
    {
      name: 'category',
      type: 'object',
      label: intl.get('hims.messageCenter.model.messageCenter.categoryId').d('知识类别'),
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
      name: 'answerDesc',
      label: intl.get('hims.messageCenter.model.messageCenter.answerDesc').d('答案'),
      type: 'string',
      required: true,
      defaultValue,
    },
    {
      name: 'keyWord',
      label: intl.get('hims.messageCenter.model.messageCenter.keyWord').d('关键词'),
      type: 'string',
    },
  ],
});

const signDS = () => ({
  selection: false,
  dataKey: 'content',
  queryFields: [],
  fields: [
    {
      name: 'categoryName',
      type: 'string',
      bind: 'categoryLov.categoryName',
      label: intl.get('hims.messageCenter.model.messageCenter.categoryId').d('知识类别'),
    },
    {
      name: 'questionTitle',
      type: 'string',
      required: true,
      label: intl.get('hims.messageCenter.model.messageCenter.questionTitle').d('问题标题'),
    },
    {
      name: 'answerDesc',
      required: true,
      label: intl.get('hims.messageCenter.model.messageCenter.answerDesc').d('答案'),
      type: 'string',
    },
    {
      name: 'checkStatus',
      label: intl.get('hims.messageCenter.model.messageCenter.checkStatus').d('审核状态'),
      type: 'string',
    },
    {
      name: 'rejectReason',
      type: 'string',
      label: intl.get('hims.messageCenter.model.messageCenter.rejectReason').d('驳回原因'),
    },
    {
      name: 'categoryLov',
      type: 'object',
      ignore: 'always',
      required: true,
      lovCode: 'HIMS.KNOWLEDGE_CATEGORIES',
      lovPara: { type: 'qa', tenantId: organizationId },
      noCache: true,
      label: intl.get('hims.messageCenter.model.messageCenter.categoryId').d('知识类别'),
    },
    {
      name: 'categoryId',
      type: 'string',
      bind: 'categoryLov.categoryId',
    },
    {
      name: 'action',
      label: intl.get('hzero.common.button.action').d('操作'),
    },
    {
      name: 'lang',
      type: 'string',
      label: intl.get('hims.messageCenter.model.messageCenter.language').d('语言'),
      defaultValue: 'zh_CN',
    },
    {
      name: 'keyWord',
      type: 'string',
      label: intl.get('hims.messageCenter.model.messageCenter.keyWord').d('关键词'),
    },
  ],
  transport: {
    read: ({ params }) => ({
      url: `${apiPrefix}/knowledge/${getCurrentUserId()}/mark`,
      method: 'GET',
      params,
    }),
    update: ({ data }) => {
      const { lang, keyWord, submitBy, categoryId, answerDesc, questionTitle } = Array.isArray(data)
        ? data[0]
        : {};
      return {
        url: `${apiPrefix}/knowledge/${categoryId}/mark`,
        data: {
          lang,
          keyWord,
          submitBy,
          answerDesc,
          questionTitle,
          categoryId: String(categoryId),
          tenantId: organizationId,
        },
        method: 'POST',
      };
    },
  },
});

export {
  initDs,
  groupDs,
  messageDs,
  messageListDs,
  messageAmountDs,
  closeConnectDs,
  lovDs,
  assessmentDs,
  formDs,
  signDS,
  iniDs,
  selfDs,
};
