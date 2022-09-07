/*
 * DataDestinationDS 数据目的地管理DS
 * @date: 2020-07-17
 * @author: LiLin <lin.li03@hand-china.com>
 * @copyright Copyright (c) 2020, Hand
 */
import intl from 'utils/intl';
import { getResponse, getCurrentOrganizationId } from 'utils/utils';
import notification from 'utils/notification';
import { HZERO_HIOT, HZERO_PLATFORM } from 'utils/config';
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
      name: 'dataSinkCode',
      type: 'string',
      label: intl.get('hiot.dataDestination.model.destination.dataSinkCode').d('数据目的地编码'),
    },
    {
      name: 'dataSinkName',
      type: 'string',
      label: intl.get('hiot.dataDestination.model.destination.dataSinkName').d('数据目的地名称'),
    },
    {
      name: 'dataSinkTypeCode',
      type: 'string',
      label: intl.get('hiot.dataDestination.model.destination.dataSinkTypeCode').d('类型'),
      lookupCode: 'HIOT.DATA_SINK_TYPE',
    },
  ],
  fields: [
    {
      name: 'dataSinkCode',
      type: 'string',
      label: intl.get('hiot.dataDestination.model.destination.dataSinkCode').d('数据目的地编码'),
    },
    {
      name: 'dataSinkName',
      type: 'string',
      label: intl.get('hiot.dataDestination.model.destination.dataSinkName').d('数据目的地名称'),
    },
    {
      name: 'dataSinkTypeCode',
      type: 'string',
      label: intl.get('hiot.dataDestination.model.destination.dataSinkTypeCode').d('类型'),
      lookupCode: 'HIOT.DATA_SINK_TYPE',
    },
    {
      name: 'enabledFlag',
      type: 'number',
      label: intl.get('hzero.common.status.enable').d('启用'),
    },
  ],
  transport: {
    read: () => ({
      url: `${apiPrefix}/data-sinks`,
      method: 'GET',
    }),
  },
});

// 新建或编辑时的DS
const drawerDS = () => ({
  autoQuery: false,
  autoQueryAfterSubmit: false,
  fields: [
    {
      name: 'dataSinkCode',
      type: 'string',
      label: intl.get('hiot.dataDestination.model.destination.dataSinkCode').d('数据目的地编码'),
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
      name: 'dataSinkName',
      type: 'intl',
      label: intl.get('hiot.dataDestination.model.destination.dataSinkName').d('数据目的地名称'),
      required: true,
      maxLength: 120,
    },
    {
      name: 'dataSinkTypeCode',
      type: 'string',
      label: intl.get('hiot.dataDestination.model.destination.dataSinkTypeCode').d('类型'),
      lookupCode: 'HIOT.DATA_SINK_TYPE',
      required: true,
    },
    {
      name: 'serverAddress',
      type: 'string',
      multiple: ',',
    },
    {
      name: 'serverTopic',
    },
    {
      name: 'handlerCode',
    },
    {
      name: 'callbackUrl',
    },
    {
      name: 'remarks',
      type: 'intl',
      label: intl.get('hiot.dataDestination.model.destination.remarks').d('备注'),
      maxLength: 480,
    },
    {
      name: 'enabledFlag',
      type: 'boolean',
      label: intl.get('hzero.common.status.enable').d('启用'),
      trueValue: 1,
      falseValue: 0,
      defaultValue: 1,
    },
  ],
  transport: {
    read: ({ data }) => {
      const { dataSinkId } = data;
      return {
        url: `${apiPrefix}/data-sinks/${dataSinkId}`,
        method: 'GET',
        data: {},
        params: {},
        transformResponse: (res) => {
          let formatData = {};
          try {
            formatData = JSON.parse(res);
          } catch (e) {
            return e;
          }
          if (getResponse(formatData)) {
            const { dataSinkConfig } = formatData;
            if (dataSinkConfig) {
              const other = `${dataSinkConfig}`;
              formatData = {
                ...formatData,
                ...JSON.parse(`${other}`),
              };
            }
          }
          return formatData;
        },
      };
    },
    create: ({ data }) => {
      const { __id, _status, ...other } = Array.isArray(data) ? data[0] : {};
      return {
        url: `${apiPrefix}/data-sinks`,
        method: 'POST',
        data: other,
      };
    },
    update: ({ data }) => {
      const { __id, _status, ...other } = Array.isArray(data) ? data[0] : {};
      return {
        url: `${apiPrefix}/data-sinks`,
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

// 表单配置
const formConfigDS = () => ({
  paging: false,
  // queryParameter: {
  //   formCode: 'HIOT.THING.ADDITION_INFO',
  // },
  transport: {
    /**
     * 表单配置
     */
    read: () => {
      const url = `${HZERO_PLATFORM}/v1/${organizationId}/form-lines/header-code`;
      return {
        url,
        method: 'GET',
        params: {},
      };
    },
  },
});

export { tableDS, drawerDS, formConfigDS };
