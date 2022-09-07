/*
 * DataProcessDS 数据处理DS
 * @date: 2020-07-20
 * @author: LiLin <lin.li03@hand-china.com>
 * @copyright Copyright (c) 2020, Hand
 */
import intl from 'utils/intl';
import { getCurrentOrganizationId } from 'utils/utils';
import notification from 'utils/notification';
import { HZERO_HIOT } from 'utils/config';
import { CODE_UPPER_REG } from '@/utils/constants';

const organizationId = getCurrentOrganizationId();
const apiPrefix = `${HZERO_HIOT}/v1/${organizationId}`;

// 表格ds
const tableDS = () => ({
  autoQuery: true,
  selection: false,
  dataKey: 'content',
  queryFields: [
    {
      name: 'dataProcessCode',
      type: 'string',
      label: intl.get('hiot.DataProcess.model.process.dataProcessCode').d('数据处理编码'),
    },
    {
      name: 'dataProcessName',
      type: 'string',
      label: intl.get('hiot.DataProcess.model.process.dataProcessName').d('数据处理名称'),
    },
  ],
  fields: [
    {
      name: 'dataProcessCode',
      type: 'string',
      label: intl.get('hiot.DataProcess.model.process.dataProcessCode').d('数据处理编码'),
    },
    {
      name: 'dataProcessName',
      type: 'string',
      label: intl.get('hiot.DataProcess.model.process.dataProcessName').d('数据处理名称'),
    },
    {
      name: 'remarks',
      type: 'string',
      label: intl.get('hzero.common.explain').d('说明'),
    },
    {
      name: 'enabledFlag',
      type: 'number',
      label: intl.get('hzero.common.status.enable').d('启用'),
    },
  ],
  transport: {
    read: () => ({
      url: `${apiPrefix}/data-process`,
      method: 'GET',
    }),
    destroy: ({ data }) => {
      const { __id, _status, ...other } = Array.isArray(data) ? data[0] : {};
      return {
        data: other,
        url: `${apiPrefix}/data-process`,
        method: 'DELETE',
      };
    },
  },
});

// 新建或编辑时的DS
const detailDS = () => ({
  autoQuery: false,
  autoQueryAfterSubmit: false,
  fields: [
    {
      name: 'dataProcessCode',
      type: 'string',
      label: intl.get('hiot.DataProcess.model.process.dataProcessCode').d('数据处理编码'),
      required: true,
      maxLength: 30,
      pattern: CODE_UPPER_REG,
      defaultValidationMessages: {
        patternMismatch: intl
          .get(`hiot.common.view.validation.codeMsg`)
          .d('全大写及数字，必须以字母、数字开头，可包含“_”'),
      },
    },
    {
      name: 'dataProcessName',
      type: 'intl',
      label: intl.get('hiot.DataProcess.model.process.dataProcessName').d('数据处理名称'),
      required: true,
      maxLength: 120,
    },
    {
      name: 'preProcessLov',
      type: 'object',
      label: intl.get('hiot.DataProcess.model.process.preProcessLov').d('数据预处理规则'),
      // lovCode: 'HIOT.LOV.CLOUD_PLATFORM',
      lovPara: { tenantId: organizationId },
      ignore: 'always',
    },
    {
      name: 'preProcessId',
      type: 'string',
      bind: 'preProcessLov.preProcessId',
    },
    {
      name: 'dataSinkLov',
      type: 'object',
      label: intl.get('hiot.DataProcess.model.process.dataSink').d('数据目的地'),
      lovCode: 'HIOT.DATA_SINK',
      lovPara: { tenantId: organizationId },
      ignore: 'always',
      required: true,
    },
    {
      name: 'dataSinkId',
      type: 'string',
      bind: 'dataSinkLov.dataSinkId',
    },
    {
      name: 'dataSinkName',
      type: 'string',
      bind: 'dataSinkLov.dataSinkName',
    },
    {
      name: 'remarks',
      type: 'intl',
      label: intl.get('hzero.common.explain').d('说明'),
      maxLength: 480,
    },
    {
      name: 'enabledFlag',
      type: 'boolean',
      label: intl.get('hzero.common.status.enable').d('启用'),
      trueValue: 1,
      falseValue: 0,
      defaultValue: 0,
    },
  ],
  transport: {
    read: ({ data }) => {
      const { dataProcessId } = data;
      return {
        url: `${apiPrefix}/data-process/${dataProcessId}`,
        method: 'GET',
        data: {},
        params: {},
      };
    },
    create: ({ data }) => {
      const { __id, _status, ...other } = Array.isArray(data) ? data[0] : {};
      return {
        url: `${apiPrefix}/data-process`,
        method: 'POST',
        data: other,
      };
    },
    update: ({ data }) => {
      const { __id, _status, ...other } = Array.isArray(data) ? data[0] : {};
      return {
        url: `${apiPrefix}/data-process`,
        method: 'PUT',
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
});

// 应用设备表格DS
const deviceTableDS = () => ({
  autoQuery: false,
  selection: false,
  dataKey: 'content',
  queryFields: [
    {
      name: 'deviceCode',
      type: 'string',
      label: intl.get('hiot.common.code').d('编码'),
    },
    {
      name: 'deviceName',
      type: 'string',
      label: intl.get('hiot.common.name').d('名称'),
    },
  ],
  fields: [
    {
      name: 'deviceCode',
      type: 'string',
      label: intl.get('hiot.common.code').d('编码'),
    },
    {
      name: 'deviceName',
      type: 'string',
      label: intl.get('hiot.common.name').d('名称'),
    },
    {
      name: 'guid',
      type: 'string',
      label: 'GUID',
    },
    {
      name: 'receivedTopic',
      type: 'string',
      label: 'TOPIC',
    },
  ],
  transport: {
    read: ({ dataSet }) => {
      const { dataProcessId } = dataSet;
      return {
        url: `${apiPrefix}/data-process/topic?dataProcessId=${dataProcessId}`,
        method: 'GET',
      };
    },
    destroy: ({ data }) => {
      const { __id, _status, ...other } = Array.isArray(data) ? data[0] : {};
      return {
        data: other,
        url: `${apiPrefix}/data-process/topic`,
        method: 'DELETE',
      };
    },
  },
});

// 添加设备DS
const createDeviceDS = () => ({
  paging: false,
  transport: {
    create: ({ data, dataSet }) => {
      const { dataProcessId } = dataSet;
      const { processTopicList } = Array.isArray(data) ? data[0] : {};
      return {
        url: `${apiPrefix}/data-process/topic?dataProcessId=${dataProcessId}`,
        method: 'POST',
        data: processTopicList,
      };
    },
  },
});

// 启用/禁用
const enableDS = () => ({
  paging: false,
  transport: {
    create: ({ data, dataSet }) => {
      const { isEnable } = dataSet;
      const { __id, _status, ...other } = Array.isArray(data) ? data[0] : {};
      return {
        url: `${apiPrefix}/data-process/${isEnable ? 'enable' : 'disable'}`,
        method: 'PUT',
        data: other,
      };
    },
  },
});

export { tableDS, detailDS, enableDS, deviceTableDS, createDeviceDS };
