import { HZERO_HORC } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import {
  ORCH_INSTANCE_STATUS,
  ORCH_STATEMENT_TYPE,
  ORCH_FAILURE_STRATEGY,
  YES_OR_NO_FLAG,
} from '@/constants/CodeConstants';
import { DEFAULT_DATETIME_FORMAT } from 'utils/constants';
import { dateTimeRender } from 'utils/renderer';
import INSTANCE_LANG from '@/langs/instanceLang';

const organizationId = getCurrentOrganizationId();
const level = isTenantRoleLevel() ? `/${organizationId}` : '';

const tableDS = () => ({
  autoQuery: true,
  pageSize: 10,
  selection: false,
  queryFields: [
    {
      name: 'instanceName',
      label: INSTANCE_LANG.INSTANCE_NAME,
      type: 'string',
    },
    {
      name: 'statusCode',
      label: INSTANCE_LANG.INSTANCE_STATUS,
      type: 'string',
      lookupCode: ORCH_INSTANCE_STATUS,
    },
    {
      name: 'statementType',
      label: INSTANCE_LANG.STATEMENT_TYPE,
      type: 'string',
      lookupCode: ORCH_STATEMENT_TYPE,
    },
    {
      name: 'statementStartTimeLow',
      type: 'dateTime',
      max: 'statementStartTimeHigh',
      format: DEFAULT_DATETIME_FORMAT,
      transformRequest: (value) => dateTimeRender(value),
      label: INSTANCE_LANG.STATEMENT_START_TIME_LOW,
    },
    {
      name: 'statementStartTimeHigh',
      type: 'dateTime',
      min: 'statementStartTimeLow',
      format: DEFAULT_DATETIME_FORMAT,
      transformRequest: (value) => dateTimeRender(value),
      label: INSTANCE_LANG.STATEMENT_START_TIME_HIGH,
    },
    {
      name: 'failoverFlag',
      label: INSTANCE_LANG.FAILOVER_FLAG,
      lookupCode: YES_OR_NO_FLAG,
    },
    {
      name: 'startTimeLow',
      type: 'dateTime',
      max: 'startTimeHigh',
      format: DEFAULT_DATETIME_FORMAT,
      transformRequest: (value) => dateTimeRender(value),
      label: INSTANCE_LANG.START_TIME_LOW,
    },
    {
      name: 'startTimeHigh',
      type: 'dateTime',
      min: 'startTimeLow',
      format: DEFAULT_DATETIME_FORMAT,
      transformRequest: (value) => dateTimeRender(value),
      label: INSTANCE_LANG.START_TIME_HIGH,
    },
    {
      name: 'failureStrategy',
      label: INSTANCE_LANG.FAILURE_STRATEGY,
      type: 'string',
      lookupCode: ORCH_FAILURE_STRATEGY,
    },
    {
      name: 'endTimeLow',
      type: 'dateTime',
      max: 'endTimeHigh',
      format: DEFAULT_DATETIME_FORMAT,
      transformRequest: (value) => dateTimeRender(value),
      label: INSTANCE_LANG.END_TIME_LOW,
    },
    {
      name: 'endTimeHigh',
      type: 'dateTime',
      min: 'endTimeLow',
      format: DEFAULT_DATETIME_FORMAT,
      transformRequest: (value) => dateTimeRender(value),
      label: INSTANCE_LANG.END_TIME_HIGH,
    },
    {
      name: 'host',
      label: INSTANCE_LANG.HOST,
      type: 'string',
    },
    {
      name: 'timeConsumptionLow',
      type: 'number',
      max: 'timeConsumptionHigh',
      label: INSTANCE_LANG.TIME_CONSUMPTION_LOW,
    },
    {
      name: 'timeConsumptionHigh',
      type: 'number',
      min: 'timeConsumptionLow',
      label: INSTANCE_LANG.TIME_CONSUMPTION_HIGH,
    },
    {
      name: 'workerGroup',
      label: INSTANCE_LANG.WORK_GROUP,
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
      name: 'instanceName',
      label: INSTANCE_LANG.INSTANCE_NAME,
      type: 'string',
    },
    {
      name: 'statusCode',
      label: INSTANCE_LANG.INSTANCE_STATUS,
      type: 'string',
      lookupCode: ORCH_INSTANCE_STATUS,
    },
    {
      name: 'statementType',
      label: INSTANCE_LANG.STATEMENT_TYPE,
      type: 'string',
      lookupCode: ORCH_STATEMENT_TYPE,
    },
    {
      name: 'statementStartTime',
      label: INSTANCE_LANG.STATEMENT_START_TIME,
      type: 'dateTime',
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
      name: 'failoverFlag',
      label: INSTANCE_LANG.FAILOVER_FLAG,
      type: 'boolean',
    },
    {
      name: 'failureStrategy',
      label: INSTANCE_LANG.FAILURE_STRATEGY,
      type: 'string',
      lookupCode: ORCH_FAILURE_STRATEGY,
    },
    {
      name: 'host',
      label: INSTANCE_LANG.HOST,
      type: 'string',
    },
    {
      name: 'workerGroup',
      label: INSTANCE_LANG.WORK_GROUP,
      type: 'string',
    },
  ],
  transport: {
    read: (config) => {
      const { data, params } = config;
      return {
        url: `${HZERO_HORC}/v1${level}/orch-instances`,
        params: {
          ...data,
          ...params,
        },
        method: 'GET',
      };
    },
    update: ({ data }) => {
      const { _buttonType } = data[0];
      return {
        url: `${HZERO_HORC}/v1${level}/orch-instances/${_buttonType}`,
        method: 'PUT',
        data: data[0],
      };
    },
    destroy: ({ data }) => ({
      url: `${HZERO_HORC}/v1${level}/orch-instances`,
      method: 'DELETE',
      data: data[0],
    }),
  },
});

export { tableDS };
