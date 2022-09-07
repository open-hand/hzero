import { HZERO_HORC } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import {
  ORCH_INSTANCE_STATUS,
  ORCH_PRIORITY,
  ORCH_TASK_TYPE,
  YES_OR_NO_FLAG,
  ORCH_FAILURE_STRATEGY,
  ORCH_THREAD_MECHANISM,
} from '@/constants/CodeConstants';
import INSTANCE_LANG from '@/langs/taskInstanceLang';
import INS_LANG from '@/langs/instanceLang';
import { DEFAULT_DATETIME_FORMAT } from 'utils/constants';
import { dateTimeRender } from 'utils/renderer';

const organizationId = getCurrentOrganizationId();
const level = isTenantRoleLevel() ? `/${organizationId}` : '';

const tableDS = () => ({
  autoQuery: true,
  pageSize: 10,
  selection: false,
  queryFields: [
    {
      name: 'taskName',
      label: INSTANCE_LANG.TASK_NAME,
      type: 'string',
    },
    {
      name: 'instanceName',
      label: INSTANCE_LANG.INSTANCE_NAME,
      type: 'string',
    },
    {
      name: 'statusCode',
      label: INSTANCE_LANG.STATUS,
      type: 'string',
      lookupCode: ORCH_INSTANCE_STATUS,
    },
    {
      name: 'submittedTimeLow',
      type: 'dateTime',
      max: 'submittedTimeHigh',
      format: DEFAULT_DATETIME_FORMAT,
      transformRequest: (value) => dateTimeRender(value),
      label: INSTANCE_LANG.SUBMITTED_TIME_LOW,
    },
    {
      name: 'submittedTimeHigh',
      type: 'dateTime',
      min: 'submittedTimeLow',
      format: DEFAULT_DATETIME_FORMAT,
      transformRequest: (value) => dateTimeRender(value),
      label: INSTANCE_LANG.SUBMITTED_TIME_HIGH,
    },
    {
      name: 'taskType',
      label: INSTANCE_LANG.TASK_TYPE,
      type: 'string',
      lookupCode: ORCH_TASK_TYPE,
    },
    {
      name: 'startTimeLow',
      type: 'dateTime',
      max: 'startTimeHigh',
      format: DEFAULT_DATETIME_FORMAT,
      transformRequest: (value) => dateTimeRender(value),
      label: INS_LANG.START_TIME_LOW,
    },
    {
      name: 'startTimeHigh',
      type: 'dateTime',
      min: 'startTimeLow',
      format: DEFAULT_DATETIME_FORMAT,
      transformRequest: (value) => dateTimeRender(value),
      label: INS_LANG.START_TIME_HIGH,
    },
    {
      name: 'host',
      label: INSTANCE_LANG.HOST,
      type: 'string',
    },
    {
      name: 'endTimeLow',
      type: 'dateTime',
      max: 'endTimeHigh',
      format: DEFAULT_DATETIME_FORMAT,
      transformRequest: (value) => dateTimeRender(value),
      label: INS_LANG.END_TIME_LOW,
    },
    {
      name: 'endTimeHigh',
      type: 'dateTime',
      min: 'endTimeLow',
      format: DEFAULT_DATETIME_FORMAT,
      transformRequest: (value) => dateTimeRender(value),
      label: INS_LANG.END_TIME_HIGH,
    },
    {
      name: 'instancePriority',
      label: INSTANCE_LANG.INSTANCE_PRIORITY,
      type: 'string',
      lookupCode: ORCH_PRIORITY,
    },
    {
      name: 'retryTimesLow',
      type: 'number',
      max: 'retryTimesHigh',
      label: INSTANCE_LANG.RETRY_TIMES_LOW,
    },
    {
      name: 'retryTimesHigh',
      type: 'number',
      min: 'retryTimesLow',
      label: INSTANCE_LANG.RETRY_TIMES_HIGH,
    },
    {
      name: 'alertFlag',
      label: INSTANCE_LANG.ALERT_FLAG,
      lookupCode: YES_OR_NO_FLAG,
    },
    {
      name: 'failureStrategy',
      label: INSTANCE_LANG.FAILURE_STRATEGY,
      type: 'string',
      lookupCode: ORCH_FAILURE_STRATEGY,
    },
    {
      name: 'threadMechanism',
      label: INSTANCE_LANG.THREAD_MECHANISM,
      type: 'string',
      lookupCode: ORCH_THREAD_MECHANISM,
    },
    {
      name: 'workerGroup',
      label: INS_LANG.WORK_GROUP,
      type: 'string',
    },
  ],
  fields: [
    {
      name: 'seqNumber',
      label: INSTANCE_LANG.SEQ_NUMBER,
      type: 'number',
    },
    {
      name: 'taskName',
      label: INSTANCE_LANG.TASK_NAME,
      type: 'string',
    },
    {
      name: 'instanceName',
      label: INSTANCE_LANG.INSTANCE_NAME,
      type: 'string',
    },
    {
      name: 'taskType',
      label: INSTANCE_LANG.TASK_TYPE,
      type: 'string',
      lookupCode: ORCH_TASK_TYPE,
    },
    {
      name: 'statusCode',
      label: INSTANCE_LANG.STATUS,
      type: 'string',
      lookupCode: ORCH_INSTANCE_STATUS,
    },
    {
      name: 'submittedTime',
      label: INSTANCE_LANG.SUBMITTED_TIME,
      type: 'string',
    },
    {
      name: 'startTime',
      label: INSTANCE_LANG.START_TIME,
      type: 'dateTime',
    },
    {
      name: 'endTime',
      label: INSTANCE_LANG.END_TIME,
      type: 'dateTime',
    },
    {
      name: 'timeConsumption',
      label: INSTANCE_LANG.TIME_CONSUMPTION,
      type: 'string',
    },
    {
      name: 'timeConsumptionDesc',
      label: INSTANCE_LANG.TIME_CONSUMPTION_DESC,
      type: 'string',
    },
    {
      name: 'failureStrategy',
      label: INSTANCE_LANG.FAILURE_STRATEGY,
      type: 'string',
      lookupCode: ORCH_FAILURE_STRATEGY,
    },
    {
      name: 'threadMechanism',
      label: INSTANCE_LANG.THREAD_MECHANISM,
      type: 'string',
      lookupCode: ORCH_THREAD_MECHANISM,
    },
    {
      name: 'threadMechanismMeaning',
      label: INSTANCE_LANG.THREAD_MECHANISM,
      type: 'string',
    },
    {
      name: 'host',
      label: INSTANCE_LANG.HOST,
      type: 'string',
    },
    {
      name: 'alertFlag',
      label: INSTANCE_LANG.ALERT_FLAG,
      type: 'boolean',
    },
    {
      name: 'retryTimes',
      label: INSTANCE_LANG.RETRY_TIMES,
      type: 'string',
    },
    {
      name: 'instancePriority',
      label: INSTANCE_LANG.INSTANCE_PRIORITY,
      type: 'string',
      lookupCode: ORCH_PRIORITY,
    },
    {
      name: 'workerGroup',
      label: INSTANCE_LANG.WORK_GROUP,
      type: 'string',
    },
    {
      name: 'remark',
      label: INSTANCE_LANG.REMARK,
      type: 'string',
    },
  ],
  transport: {
    read: (config) => {
      const { data, params } = config;
      return {
        url: `${HZERO_HORC}/v1${level}/orch-task-instances`,
        params: {
          ...data,
          ...params,
        },
        method: 'GET',
      };
    },
    update: ({ data }) => {
      const { _requestType } = data[0];
      return {
        url: `${HZERO_HORC}/v1${level}/orch-task-instances/${_requestType}`,
        method: 'PUT',
        data: data[0],
      };
    },
    destroy: ({ data }) => ({
      url: `${HZERO_HORC}/v1${level}/orch-task-instances`,
      method: 'DELETE',
      data: data[0],
    }),
  },
});

const resultTableDS = () => ({
  autoQuery: false,
  fields: [
    {
      name: 'taskName',
      label: INSTANCE_LANG.TASK_NAME,
      type: 'string',
    },
    {
      name: 'taskInstanceVOList',
      label: INSTANCE_LANG.TASK_INSTANCE_VO_LIST,
      type: 'list',
    },
    {
      name: 'textType',
      label: INSTANCE_LANG.TEXT_TYPE,
      type: 'string',
    },
    {
      name: 'contentType',
      label: INSTANCE_LANG.CONTENT_TYPE,
      type: 'string',
    },
    {
      name: 'processTime',
      label: INSTANCE_LANG.PROCESS_TIME,
      type: 'date',
    },
    {
      name: 'remark',
      label: INSTANCE_LANG.REMARK,
      type: 'string',
    },
    {
      name: 'result',
      label: INSTANCE_LANG.RESULT,
      type: 'string',
    },
  ],
  transport: {
    read: ({ data }) => {
      const { taskId } = data;
      return {
        url: `${HZERO_HORC}/v1${level}/orch-task-result/${taskId}`,
        method: 'GET',
        data: null,
      };
    },
  },
});

export { tableDS, resultTableDS };
