import { HZERO_HORC } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import {
  CHARSET,
  ORCH_HTTP_METHOD,
  ORCH_HTTP_PARAMETER_TYPE,
  TRANSFORM_TYPE,
  ORCH_THREAD_MECHANISM,
  ORCH_FAILURE_STRATEGY,
  ORCH_WARNING_TYPE,
  ALERT_CODE,
} from '@/constants/CodeConstants';
import { ORCH_PRIORITY, ORCH_DEF_STATUS_OFFLINE } from '@/constants/constants';
import ORCHESTRATION_LANG from '@/langs/orchestrationLang';
import { joinField, joinUpperField, transformUrl } from '@/utils/utils';

const organizationId = getCurrentOrganizationId();
const level = isTenantRoleLevel() ? `/${organizationId}` : '';

const flowGraphDS = ({ ref }) => {
  return {
    autoQuery: false,
    autoCreate: true,
    pageSize: 10,
    selection: false,
    primaryKey: joinField(ref, 'id'),
    fields: [
      {
        name: joinField(ref, 'name'),
        type: 'string',
      },
      {
        name: 'statusCode',
        type: 'string',
        defaultValue: ORCH_DEF_STATUS_OFFLINE,
      },
      {
        name: 'timeout',
        type: 'number',
      },
      {
        name: 'remark',
        type: 'string',
      },
      {
        name: joinField(ref, 'json'),
        type: 'string',
      },
      {
        name: 'globalLayout',
        type: 'string',
      },
    ],
    transport: {
      read: ({ data }) => {
        const id = data[joinField(ref, 'id')];
        return {
          url: `${HZERO_HORC}/v1${level}/${transformUrl(ref)}/${id}`,
          method: 'GET',
          data: null,
          params: null,
        };
      },
      create: ({ data }) => ({
        url: `${HZERO_HORC}/v1${level}/${transformUrl(ref)}`,
        data: data[0],
        method: 'POST',
      }),
      update: ({ data }) => {
        return {
          url: `${HZERO_HORC}/v1${level}/${transformUrl(ref)}`,
          method: 'PUT',
          data: data[0],
        };
      },
      destroy: ({ data }) => ({
        url: `${HZERO_HORC}/v1${level}/${transformUrl(ref)}`,
        method: 'DELETE',
        data,
      }),
    },
  };
};

const headerFormDS = ({ ref }) => {
  return {
    primaryKey: joinField(ref, 'id'),
    autoQuery: false,
    autoCreate: false,
    selection: false,
    paging: false,
    fields: [
      {
        name: joinField(ref, 'name'),
        label: ORCHESTRATION_LANG[joinUpperField(ref, 'name')],
        type: 'string',
        required: true,
      },
      {
        name: 'remark',
        label: ORCHESTRATION_LANG.DESCRIPTION,
        type: 'string',
      },
      {
        name: 'timeoutFlag',
        label: ORCHESTRATION_LANG.ALERT_FLAG,
        type: 'boolean',
      },
      {
        name: 'timeout',
        label: ORCHESTRATION_LANG.TIMEOUT_TIME,
        type: 'number',
        defaultValue: 0,
      },
      {
        name: 'alertCodeLov',
        label: ORCHESTRATION_LANG.ALERT_CODE,
        type: 'object',
        lovCode: ALERT_CODE,
        ignore: 'always',
        valueField: 'alertCode',
        textField: 'alertName',
      },
      {
        name: 'alertCode',
        type: 'string',
        bind: 'alertCodeLov.alertCode',
      },
    ],
  };
};

const commonNodeFormDS = () => ({
  autoQuery: false,
  autoCreate: false,
  selection: false,
  paging: false,
  fields: [
    {
      name: 'name',
      label: ORCHESTRATION_LANG.NODE_NAME,
      type: 'string',
      required: true,
    },
    {
      name: 'statusCode',
      label: ORCHESTRATION_LANG.STATUS,
      type: 'string',
    },
    {
      name: 'description',
      label: ORCHESTRATION_LANG.DESCRIPTION,
      type: 'string',
    },
    {
      name: 'workGroup',
      label: ORCHESTRATION_LANG.WORK_GROUP,
      type: 'string',
      defaultValue: 'default',
      required: true,
    },
    {
      name: 'taskInstancePriority',
      label: ORCHESTRATION_LANG.PRIORITY,
      type: 'number',
      required: true,
      defaultValue: 50,
      transformRequest: (value) => ORCH_PRIORITY[value],
    },
    {
      name: 'retryTimes',
      label: ORCHESTRATION_LANG.RETRY_TIMES,
      type: 'number',
      required: true,
      defaultValue: 0,
    },
    {
      name: 'retryInterval',
      label: ORCHESTRATION_LANG.RETRY_INTERVAL,
      type: 'number',
      required: true,
      defaultValue: 1,
    },
    {
      name: 'threadMechanism',
      label: ORCHESTRATION_LANG.THREAD_MECHANISM,
      type: 'string',
      lookupCode: ORCH_THREAD_MECHANISM,
      defaultValue: 'SYNC',
      required: true,
    },
    {
      name: 'failureStrategy',
      label: ORCHESTRATION_LANG.FAILURE_STRATEGY,
      type: 'string',
      lookupCode: ORCH_FAILURE_STRATEGY,
    },
    {
      name: 'alertFlag',
      label: ORCHESTRATION_LANG.ALERT_FLAG,
      type: 'boolean',
    },
    {
      name: 'alertCodeLov',
      label: ORCHESTRATION_LANG.ALERT_CODE,
      type: 'object',
      lovCode: ALERT_CODE,
      ignore: 'always',
      valueField: 'alertCode',
      textField: 'alertName',
    },
    {
      name: 'alertCode',
      type: 'string',
      bind: 'alertCodeLov.alertCode',
    },
    {
      name: 'alertName',
      type: 'string',
      bind: 'alertCodeLov.alertName',
    },
    {
      name: 'strategy',
      label: ORCHESTRATION_LANG.TIMEOUT_STRATEGY,
      multiple: true,
    },
    {
      name: 'interval',
      label: ORCHESTRATION_LANG.TIMEOUT_TIME,
      type: 'string',
      dynamicProps: {
        required({ record }) {
          return record.get('enable');
        },
      },
    },
  ],
});

const requestSettingFormDS = () => ({
  autoQuery: false,
  autoCreate: false,
  selection: false,
  paging: false,
  fields: [
    {
      name: 'httpMethod',
      label: ORCHESTRATION_LANG.REQUEST_METHOD,
      type: 'string',
      lookupCode: ORCH_HTTP_METHOD,
      defaultValue: 'GET',
      required: true,
    },
    {
      name: 'url',
      label: ORCHESTRATION_LANG.REQUEST_ADDRESS,
      type: 'url',
      required: true,
    },
    {
      name: 'httpRequestCharset',
      label: ORCHESTRATION_LANG.REQUEST_CHARSET,
      type: 'string',
      lookupCode: CHARSET,
      defaultValue: 'UTF-8',
      required: true,
    },
    {
      name: 'httpResponseCharset',
      label: ORCHESTRATION_LANG.RESPONSE_CHARSET,
      type: 'string',
      lookupCode: CHARSET,
      defaultValue: 'UTF-8',
      required: true,
    },
    {
      name: 'readTimeout',
      label: ORCHESTRATION_LANG.READ_TIMEOUT,
      type: 'number',
      min: 0,
      defaultValue: 30000,
    },
    {
      name: 'connectionTimeout',
      label: ORCHESTRATION_LANG.CONNECTION_TIMEOUT,
      type: 'number',
      min: 0,
      defaultValue: 30000,
    },
    {
      name: 'enableResultPropagation',
      label: ORCHESTRATION_LANG.ENABLE_RESULT_PROPAGATION,
      type: 'boolean',
      defaultValue: true,
    },
  ],
});

const requestHeaderDS = () => ({
  autoQuery: false,
  paging: false,
  selection: false,
  fields: [
    {
      name: 'key',
      label: ORCHESTRATION_LANG.KEY,
      type: 'string',
      required: true,
    },
    {
      name: 'value',
      label: ORCHESTRATION_LANG.VALUE,
      type: 'string',
      required: true,
    },
    {
      name: 'httpParameterType',
      type: 'string',
      required: true,
      defaultValue: 'HEADER',
    },
    {
      name: 'exprEnabled',
      label: ORCHESTRATION_LANG.EXPR_ENABLED,
      type: 'boolean',
      defaultValue: false,
    },
  ],
});

const requestQueryDS = () => ({
  autoQuery: false,
  paging: false,
  selection: false,
  fields: [
    {
      name: 'key',
      label: ORCHESTRATION_LANG.KEY,
      type: 'string',
      required: true,
    },
    {
      name: 'value',
      label: ORCHESTRATION_LANG.VALUE,
      type: 'string',
      required: true,
    },
    {
      name: 'httpParameterType',
      type: 'string',
      required: true,
      defaultValue: 'QUERY',
    },
    {
      name: 'exprEnabled',
      label: ORCHESTRATION_LANG.EXPR_ENABLED,
      type: 'boolean',
      defaultValue: false,
    },
  ],
});

const requestBodyDS = () => ({
  autoQuery: false,
  paging: false,
  selection: false,
  primaryKey: 'key',
  fields: [
    {
      name: 'key',
      label: ORCHESTRATION_LANG.KEY,
      type: 'string',
      required: true,
    },
    {
      name: 'httpParameterType',
      label: ORCHESTRATION_LANG.TYPE,
      type: 'string',
      lookupCode: ORCH_HTTP_PARAMETER_TYPE,
      required: true,
    },
    {
      name: 'value',
      label: ORCHESTRATION_LANG.VALUE,
      type: 'string',
      required: true,
    },
    {
      name: 'exprEnabled',
      label: ORCHESTRATION_LANG.EXPR_ENABLED,
      type: 'boolean',
      defaultValue: false,
    },
  ],
});

const assertionDS = (props) => {
  const { onAddAssertionFormItem, onRemoveAssertionFormItem, onFiledUpdate } = props;
  return {
    autoQuery: false,
    paging: false,
    selection: false,
    fields: [
      {
        name: 'subject',
        label: ORCHESTRATION_LANG.KEY,
        type: 'string',
        required: true,
      },
      {
        name: 'field',
        label: ORCHESTRATION_LANG.VALUE,
        type: 'string',
      },
      {
        name: 'condition',
        label: ORCHESTRATION_LANG.TYPE,
        type: 'string',
        required: true,
      },
      {
        name: 'expectation',
        label: ORCHESTRATION_LANG.KEY,
        type: 'string',
        required: true,
      },
    ],
    events: {
      create: ({ record }) => onAddAssertionFormItem(record),
      remove: ({ dataSet }) => onRemoveAssertionFormItem(dataSet),
      update: ({ dataSet, record, name, value }) => onFiledUpdate(dataSet, record, name, value),
    },
  };
};

const fieldDataModalDS = () => ({
  autoQuery: false,
  autoCreate: true,
  selection: false,
  paging: false,
  fields: [
    {
      name: 'structureName',
      label: ORCHESTRATION_LANG.STRUCTURE_NAME,
      type: 'string',
      required: true,
    },
  ],
});

const fieldDataDrawerDS = () => ({
  autoQuery: false,
  autoCreate: false,
  selection: false,
  paging: false,
  fields: [
    {
      name: 'inputData',
      type: 'string',
      required: true,
    },
  ],
});

const fieldMappingDrawerDS = () => ({
  autoQuery: false,
  autoCreate: false,
  selection: false,
  paging: false,
  fields: [
    {
      name: 'sourceStructure',
      type: 'string',
      required: true,
    },
    {
      name: 'targetStructure',
      type: 'string',
      required: true,
    },
    {
      name: 'script',
      type: 'string',
      required: true,
    },
  ],
});

const fieldMappingFormDS = () => ({
  autoQuery: false,
  autoCreate: false,
  selection: false,
  paging: false,
  fields: [
    {
      name: 'type',
      label: ORCHESTRATION_LANG.TRANSFORM_TYPE,
      type: 'string',
      required: true,
      lookupCode: TRANSFORM_TYPE,
    },
  ],
});

const paramInfoDS = () => ({
  autoQuery: false,
  autoCreate: false,
  selection: false,
  paging: false,
  fields: [
    {
      name: 'dependencyType',
      type: 'string',
    },
    {
      name: 'failureStrategyMeaning',
      label: ORCHESTRATION_LANG.FAILED_STRATEGY,
      type: 'string',
    },
    {
      name: 'warningTypeMeaning',
      label: ORCHESTRATION_LANG.WARNING_TYPE,
      type: 'string',
      lookupCode: ORCH_WARNING_TYPE,
    },
    {
      name: 'instancePriorityMeaning',
      label: ORCHESTRATION_LANG.PRIORITY,
      type: 'string',
      lookupCode: ORCH_PRIORITY,
    },
    {
      name: 'workerGroup',
      label: ORCHESTRATION_LANG.WORKER_GROUP,
      type: 'string',
    },
    {
      name: 'preference',
      type: 'string',
      label: ORCHESTRATION_LANG.PREFERENCE,
      placeholder: ORCHESTRATION_LANG.PREFERENCE_PLACEHOLDER,
    },
  ],
});

const conditionDS = () => ({
  autoQuery: false,
  paging: false,
  selection: false,
  fields: [
    {
      name: 'dependency',
      label: ORCHESTRATION_LANG.DEPENDENCY,
      type: 'string',
    },
    {
      name: 'successNode',
      label: ORCHESTRATION_LANG.SUCCESS_NODE,
      type: 'string',
      multiple: true,
    },
    {
      name: 'failedNode',
      label: ORCHESTRATION_LANG.FAILED_NODE,
      type: 'string',
      multiple: true,
    },
  ],
});

export {
  headerFormDS,
  requestSettingFormDS,
  flowGraphDS,
  requestHeaderDS,
  requestQueryDS,
  requestBodyDS,
  assertionDS,
  fieldDataModalDS,
  fieldDataDrawerDS,
  fieldMappingDrawerDS,
  fieldMappingFormDS,
  paramInfoDS,
  commonNodeFormDS,
  conditionDS,
};
