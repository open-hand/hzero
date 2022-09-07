/*
 * dataFlowPipeline 数据流定义
 * @date: 2020-07-15
 * @author JMY <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2020, Hand
 */
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
      name: 'streamCode',
      type: 'string',
      label: intl.get('hdsc.dataDefine.model.dataDefine.streamCode').d('数据流编码'),
    },
    {
      name: 'streamTypeCode',
      lookupCode: 'HDSC.DATA_STREAM_TYPE',
      type: 'string',
      label: intl.get('hdsc.dataDefine.model.dataDefine.streamTypeCode').d('数据类型'),
    },
    {
      name: 'pipelineObj',
      lovCode: isTenant ? 'HDSC.DATA_STREAM_PIPELINE' : 'HDSC.DATA_STREAM_PIPELINE_SITE',
      type: 'object',
      label: intl.get('hdsc.dataDefine.model.dataDefine.pipelineId').d('绑定管道'),
      ignore: 'always',
    },
    {
      name: 'pipelineId',
      type: 'string',
      bind: 'pipelineObj.pipelineId',
    },
    {
      name: 'streamPurposeCode',
      lookupCode: 'HDSC.DATA_STREAM_PURPOSE',
      type: 'string',
      label: intl.get('hdsc.dataDefine.model.dataDefine.streamPurposeCodeMeaning').d('数据渠道'),
    },
  ],
  fields: [
    {
      name: 'streamCode',
      type: 'string',
      label: intl.get('hdsc.dataDefine.model.dataDefine.streamCode').d('数据流编码'),
    },
    {
      name: 'streamTypeCode',
      type: 'string',
      label: intl.get('hdsc.dataDefine.model.dataDefine.streamTypeCode').d('数据类型'),
    },
    {
      name: 'streamTypeCodeMeaning',
      type: 'string',
      label: intl.get('hdsc.dataDefine.model.dataDefine.streamTypeCode').d('数据类型'),
    },
    {
      name: 'streamPurposeCodeMeaning',
      type: 'string',
      label: intl.get('hdsc.dataDefine.model.dataDefine.streamPurposeCodeMeaning').d('数据渠道'),
    },
    {
      name: 'configValue',
      type: 'string',
      label: intl.get('hdsc.dataDefine.model.dataDefine.configValue').d('数据源配置'),
    },
  ],
  transport: {
    read: () => ({
      url: `${apiPrefix}/data-streams/page`,
      method: 'GET',
    }),
    destroy: ({ data }) => {
      const { streamId } = Array.isArray(data) ? data[0] : {};
      return {
        url: `${apiPrefix}/data-streams`,
        method: 'DELETE',
        params: { streamId },
        data: {},
      };
    },
  },
});

// 弹窗ds
const drawerDS = () => ({
  autoQuery: false,
  selection: true,
  paging: false,
  autoQueryAfterSubmit: false,
  fields: [
    {
      name: 'streamCode',
      type: 'string',
      label: intl.get('hdsc.dataDefine.model.dataDefine.streamCode').d('数据流编码'),
      pattern: CODE_UPPER,
      defaultValidationMessages: {
        patternMismatch: intl
          .get('hzero.common.validation.codeUpper')
          .d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
      },
      required: true,
    },
    {
      name: 'streamTypeCode',
      type: 'string',
      label: intl.get('hdsc.dataDefine.model.dataDefine.streamTypeCode').d('数据类型'),
      lookupCode: 'HDSC.DATA_STREAM_TYPE',
      cascadeMap: { parentValue: 'streamPurposeCode' },
      required: true,
    },
    {
      name: 'streamPurposeCode',
      type: 'string',
      label: intl.get('hdsc.dataDefine.model.dataDefine.streamPurposeCode').d('数据渠道'),
      lookupCode: 'HDSC.DATA_STREAM_PURPOSE',
      required: true,
    },
    {
      name: 'configValue',
      type: 'string',
      label: intl.get('hdsc.dataDefine.model.dataDefine.configValue').d('数据源配置'),
    },
  ],
  transport: {
    read: () => ({
      url: `${apiPrefix}/data-streams/detail`,
      method: 'GET',
    }),
    create: ({ data }) => {
      const { __id, _status, ...other } = Array.isArray(data) ? data[0] : {};
      return {
        url: `${apiPrefix}/data-streams`,
        method: 'POST',
        data: other,
      };
    },
    update: ({ data }) => {
      const { __id, _status, ...other } = Array.isArray(data) ? data[0] : {};
      return {
        url: `${apiPrefix}/data-streams`,
        method: 'PUT',
        data: other,
      };
    },
  },
});

// 表格ds
const configDS = () => ({
  autoQuery: false,
  selection: false,
  paging: false,
  fields: [
    {
      name: 'key',
      type: 'string',
      label: intl.get('hdsc.dataDefine.model.dataDefine.key').d('列'),
    },
    {
      name: 'value',
      type: 'string',
      label: intl.get('hdsc.dataDefine.model.dataDefine.value').d('值'),
    },
  ],
});

export { tableDS, drawerDS, configDS };
