/*
 * dataFlowPipeline 数据流管道
 * @date: 2020-07-15
 * @author JMY <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2020, Hand
 */
import { isEmpty } from 'lodash';
import intl from 'utils/intl';
import { CODE_UPPER } from 'utils/regExp';
import { HZERO_HDSC } from 'utils/config';
import { isTenantRoleLevel, getCurrentOrganizationId } from 'utils/utils';

const organizationId = getCurrentOrganizationId();
const isTenant = isTenantRoleLevel();
const apiPrefix = isTenant ? `${HZERO_HDSC}/v1/${organizationId}` : `${HZERO_HDSC}/v1`;

// 表格ds
const tableDS = () => ({
  autoQuery: false,
  selection: false,
  dataKey: 'content',
  queryFields: [
    {
      name: 'pipelineCode',
      type: 'string',
      label: intl
        .get('hdsc.dataFlowPipeline.model.dataFlowPipeline.pipelineCode')
        .d('数据流管道编码'),
    },
    {
      name: 'streamObj',
      lovCode: isTenant ? 'HDSC.DATA_STREAM' : 'HDSC.DATA_STREAM_SITE',
      type: 'object',
      label: intl.get('hdsc.dataDefine.model.dataDefine.streamId').d('绑定数据流'),
      ignore: 'always',
    },
    {
      name: 'streamId',
      type: 'string',
      bind: 'streamObj.streamId',
    },
  ],
  fields: [
    {
      name: 'pipelineCode',
      type: 'string',
      label: intl
        .get('hdsc.dataFlowPipeline.model.dataFlowPipeline.pipelineCode')
        .d('数据流管道编码'),
    },
    {
      name: 'description',
      type: 'string',
      label: intl.get('hdsc.dataFlowPipeline.model.dataFlowPipeline.description').d('描述'),
    },
    {
      name: 'enabledFlag',
      type: 'number',
      label: intl.get('hdsc.dataFlowPipeline.model.dataFlowPipeline.enabledFlag').d('状态'),
    },
  ],
  transport: {
    read: () => ({
      url: `${apiPrefix}/data-stream-pipelines/page`,
      method: 'GET',
    }),
    destroy: ({ data }) => {
      const { pipelineId } = Array.isArray(data) ? data[0] : {};
      return {
        url: `${apiPrefix}/data-stream-pipelines`,
        method: 'DELETE',
        params: { pipelineId },
        data: {},
      };
    },
  },
});

// 弹窗ds
const drawerDS = () => ({
  autoQuery: false,
  selection: true,
  autoQueryAfterSubmit: false,
  fields: [
    {
      name: 'pipelineCode',
      type: 'string',
      label: intl
        .get('hdsc.dataFlowPipeline.model.dataFlowPipeline.pipelineCode')
        .d('数据流管道编码'),
      pattern: CODE_UPPER,
      defaultValidationMessages: {
        patternMismatch: intl
          .get('hzero.common.validation.codeUpper')
          .d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
      },
      required: true,
    },
    {
      name: 'description',
      type: 'string',
      label: intl.get('hdsc.dataFlowPipeline.model.dataFlowPipeline.description').d('描述'),
    },
    {
      name: 'enabledFlag',
      type: 'number',
      falseValue: 0,
      trueValue: 1,
      defaultValue: 0,
      label: intl.get('hdsc.dataFlowPipeline.model.dataFlowPipeline.enabledFlag').d('状态'),
    },
  ],
  transport: {
    read: () => ({
      url: `${apiPrefix}/data-stream-pipelines/detail`,
      method: 'GET',
      // data: {},
      params: {},
    }),
    create: ({ data }) => {
      const { __id, _status, ...other } = Array.isArray(data) ? data[0] : {};
      return {
        url: `${apiPrefix}/data-stream-pipelines`,
        method: 'POST',
        data: { ...other, tenantId: isTenant ? organizationId : undefined },
      };
    },
    update: ({ data }) => {
      const { __id, _status, ...other } = Array.isArray(data) ? data[0] : {};
      return {
        url: `${apiPrefix}/data-stream-pipelines`,
        method: 'PUT',
        data: other,
      };
    },
  },
});

const inputDS = () => ({
  autoQuery: false,
  selection: 'multiple',
  primaryKey: 'pipelineStreamId',
  dataKey: 'content',
  fields: [
    {
      name: 'streamCode',
      type: 'string',
      label: intl.get('hdsc.dataFlowPipeline.model.dataFlowPipeline.streamCode').d('数据流编码'),
    },
  ],
  transport: {
    read: () => ({
      url: `${apiPrefix}/pipeline-stream-relations/page`,
      method: 'GET',
      params: { streamPurposeCode: 'INPUT' },
    }),
    destroy: ({ data }) => {
      const pipelineStreamIds = data.map((item) => {
        return item.pipelineStreamId;
      });
      return {
        url: `${apiPrefix}/pipeline-stream-relations`,
        method: 'DELETE',
        data: {},
        params: { pipelineStreamIds: pipelineStreamIds.join(',') },
      };
    },
  },
});

const outputDS = () => ({
  selection: 'multiple',
  autoQuery: false,
  dataKey: 'content',
  fields: [
    {
      name: 'streamCode',
      type: 'string',
      label: intl.get('hdsc.dataFlowPipeline.model.dataFlowPipeline.streamCode').d('数据流编码'),
    },
  ],
  transport: {
    read: () => ({
      url: `${apiPrefix}/pipeline-stream-relations/page`,
      method: 'GET',
      params: { streamPurposeCode: 'OUTPUT' },
    }),
    destroy: ({ data }) => {
      const pipelineStreamIds = data.map((item) => {
        return item.pipelineStreamId;
      });
      return {
        url: `${apiPrefix}/pipeline-stream-relations`,
        method: 'DELETE',
        data: {},
        params: { pipelineStreamIds: pipelineStreamIds.join(',') },
      };
    },
  },
});

const lovInputDS = () => ({
  autoCreate: true,
  fields: [
    {
      name: 'code',
      type: 'object',
      lovCode: isTenant ? 'HDSC.DATA_STREAM' : 'HDSC.DATA_STREAM_SITE',
      lovPara: { streamPurposeCode: 'INPUT' },
    },
  ],
  cacheSelection: true,
  transport: {
    create: ({ data, dataSet }) => {
      const { code } = Array.isArray(data) ? data[0] : {};
      const { pipelineId } = dataSet.queryParameter;
      return {
        url: `${apiPrefix}/pipeline-stream-relations`,
        method: 'POST',
        data: { ...code, streamPurposeCode: 'INPUT', __dirty: undefined, pipelineId },
      };
    },
  },
});

const lovOutputDS = () => ({
  autoCreate: true,
  fields: [
    {
      name: 'code',
      type: 'object',
      lovCode: isTenant ? 'HDSC.DATA_STREAM' : 'HDSC.DATA_STREAM_SITE',
      lovPara: { streamPurposeCode: 'OUTPUT' },
    },
  ],
  cacheSelection: true,
  transport: {
    create: ({ data, dataSet }) => {
      const { code } = Array.isArray(data) ? data[0] : {};
      const { pipelineId } = dataSet.queryParameter;
      return {
        url: `${apiPrefix}/pipeline-stream-relations`,
        method: 'POST',
        data: { ...code, streamPurposeCode: 'OUTPUT', __dirty: undefined, pipelineId },
      };
    },
  },
});

const enabledDS = () => ({
  autoCreate: false,
  autoQuery: false,
  autoQueryAfterSubmit: false,
  fields: [],
  transport: {
    create: ({ data }) => {
      const { pipelineId } = Array.isArray(data) ? data[0] : {};
      return {
        url: `${apiPrefix}/data-stream-pipelines/restart`,
        method: 'POST',
        params: { pipelineId },
        data: {},
      };
    },
  },
});

const instanceDS = () => ({
  autoCreate: false,
  autoQuery: false,
  autoQueryAfterSubmit: false,
  fields: [],
  transport: {
    read: () => {
      return {
        url: `${apiPrefix}/data-stream-pipelines/holding-instance`,
        method: 'GET',
        params: {},
      };
    },
  },
});

const BindTableDS = () => ({
  autoQuery: false,
  selection: 'multiple',
  primaryKey: 'pipelineStreamId',
  paging: false,
  dataKey: 'content',
  fields: [
    {
      name: 'streamCode',
      type: 'string',
      label: intl.get('hdsc.dataFlowPipeline.model.dataFlowPipeline.streamCode').d('数据流编码'),
    },
  ],
  transport: {
    read: () => ({
      url: `${apiPrefix}/pipeline-stream-relations/detail`,
      method: 'GET',
    }),
  },
});

// 编辑绑定组合
const EditBindTableDS = () => ({
  paging: false,
  transport: {
    create: ({ data }) => {
      const { relations } = Array.isArray(data) ? data[0] : {};
      return {
        url: `${apiPrefix}/pipeline-stream-relations/save`,
        method: 'PUT',
        data: relations,
      };
    },
  },
});

const DeleteBindTableDS = () => ({
  paging: false,
  transport: {
    create: ({ data }) => {
      const { pipelineStreamIds = [] } = Array.isArray(data) ? data[0] : {};
      const queryStr = pipelineStreamIds.join(',');
      return {
        url: `${apiPrefix}/pipeline-stream-relations${
          !isEmpty(pipelineStreamIds) ? `?pipelineStreamIds=${queryStr}` : ''
        }`,
        method: 'DELETE',
        data: {},
      };
    },
  },
});

// 启动/停止数据核对DS
const CheckDataDS = () => ({
  paging: false,
  transport: {
    create: ({ data, dataSet }) => {
      const { isEnable } = dataSet;
      const { pipelineId } = Array.isArray(data) ? data[0] : {};
      return {
        url: `${apiPrefix}/data-stream-pipelines/${
          isEnable ? 'enable' : 'disable'
        }-verify?pipelineId=${pipelineId}`,
        method: 'POST',
        data: {},
      };
    },
  },
});

// 查看日志DS
const logViewDS = () => ({
  paging: false,
  autoQuery: false,
  transport: {
    read: () => ({
      url: `${apiPrefix}/pipeline-logs/scrolling-paging-query`,
      method: 'GET',
    }),
  },
});

// 日志查询表单
const logFormDS = () => ({
  autoCreate: true,
  fields: [
    {
      name: 'namespace',
      type: 'string',
      label: intl.get('hdsc.dataFlowPipeline.model.dataFlowPipeline.namespace').d('命名空间'),
    },
    {
      name: 'logLevel',
      type: 'string',
      label: intl.get('hdsc.dataFlowPipeline.model.dataFlowPipeline.logLevel').d('日志级别'),
      lookupCode: 'HDSC.LOG_LEVEL',
    },
    {
      name: 'after',
      type: 'dateTime',
      label: intl.get('hdsc.dataFlowPipeline.model.dataFlowPipeline.after').d('发生时间'),
    },
  ],
});
export {
  tableDS,
  CheckDataDS,
  drawerDS,
  outputDS,
  inputDS,
  logViewDS,
  logFormDS,
  lovOutputDS,
  lovInputDS,
  enabledDS,
  instanceDS,
  BindTableDS,
  EditBindTableDS,
  DeleteBindTableDS,
};
