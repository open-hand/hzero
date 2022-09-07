import { HZERO_HFNT } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import {
  FRONTAL_JOB_JOB_TYPE,
  FRONTAL_JOB_JOB_STATUS,
  FRONTAL_JOB_JOB_PARAM_TYPE,
  FRONTAL_SERVER,
} from '@/constants/CodeConstants';
import SCHEDULED_TASK_LANG from '@/langs/scheduledTaskLang';

const organizationId = getCurrentOrganizationId();
const level = isTenantRoleLevel() ? `/${organizationId}` : '';

const tableDS = {
  autoQuery: true,
  pageSize: 10,
  selection: false,
  primaryKey: 'jobId',
  queryFields: [
    {
      name: 'frontalLov',
      label: SCHEDULED_TASK_LANG.MACHINE_CODE,
      type: 'object',
      valueField: 'frontalId',
      textField: 'frontalCode',
      ignore: 'always',
      lovCode: FRONTAL_SERVER,
    },
    {
      name: 'frontalId',
      type: 'string',
      bind: 'frontalLov.frontalId',
    },
    {
      name: 'jobCode',
      label: SCHEDULED_TASK_LANG.SCHEDULED_TASK_CODE,
      type: 'string',
    },
    {
      name: 'jobName',
      label: SCHEDULED_TASK_LANG.SCHEDULED_TASK_NAME,
      type: 'string',
    },
    {
      name: 'jobClassName',
      label: SCHEDULED_TASK_LANG.CLASS_NAME,
      type: 'string',
    },
    {
      name: 'jobClassMethod',
      label: SCHEDULED_TASK_LANG.METHOD_NAME,
      type: 'string',
    },
    {
      name: 'statusCode',
      label: SCHEDULED_TASK_LANG.STATUS,
      type: 'string',
      lookupCode: FRONTAL_JOB_JOB_STATUS,
    },
  ],
  fields: [
    {
      name: 'seqNumber',
      label: SCHEDULED_TASK_LANG.SEQ_NUMBER,
      type: 'number',
    },
    {
      name: 'frontalCode',
      label: SCHEDULED_TASK_LANG.MACHINE_CODE,
      type: 'string',
    },
    {
      name: 'frontalName',
      label: SCHEDULED_TASK_LANG.MACHINE_NAME,
      type: 'string',
    },
    {
      name: 'jobCode',
      label: SCHEDULED_TASK_LANG.SCHEDULED_TASK_CODE,
      type: 'string',
    },
    {
      name: 'jobName',
      label: SCHEDULED_TASK_LANG.SCHEDULED_TASK_NAME,
      type: 'string',
    },
    {
      name: 'jobType',
      label: SCHEDULED_TASK_LANG.TYPE,
      type: 'string',
      lookupCode: FRONTAL_JOB_JOB_TYPE,
    },
    {
      name: 'jobCron',
      label: SCHEDULED_TASK_LANG.CRON,
      type: 'string',
    },
    {
      name: 'jobClassName',
      label: SCHEDULED_TASK_LANG.PROGRAM_NAME,
      type: 'string',
    },
    {
      name: 'jobClass',
      label: SCHEDULED_TASK_LANG.CLASS_NAME,
      type: 'string',
    },
    {
      name: 'jobClassMethod',
      label: SCHEDULED_TASK_LANG.METHOD_NAME,
      type: 'string',
    },
    {
      name: 'statusCode',
      label: SCHEDULED_TASK_LANG.STATUS,
      type: 'string',
      lookupCode: FRONTAL_JOB_JOB_STATUS,
    },
  ],
  transport: {
    read: (config) => {
      const { data, params } = config;
      return {
        url: `${HZERO_HFNT}/v1${level}/jobs`,
        params: {
          ...data,
          ...params,
        },
        method: 'GET',
      };
    },
    update: ({ data }) => {
      const { jobId, _requestType } = data[0];
      return {
        url: `${HZERO_HFNT}/v1${level}/jobs/${jobId}/${_requestType}`,
        method: 'PUT',
        data: null,
      };
    },
  },
};

const drawerFormDS = {
  primaryKey: 'jobId',
  autoQuery: false,
  autoCreate: false,
  selection: false,
  paging: false,
  fields: [
    {
      name: 'frontalLov',
      label: SCHEDULED_TASK_LANG.MACHINE_CODE,
      type: 'object',
      valueField: 'frontalId',
      textField: 'frontalName',
      ignore: 'always',
      required: true,
      lovCode: FRONTAL_SERVER,
    },
    {
      name: 'frontalId',
      type: 'string',
      bind: 'frontalLov.frontalId',
    },
    {
      name: 'frontalCode',
      type: 'string',
      bind: 'frontalLov.frontalCode',
    },
    {
      name: 'frontalName',
      type: 'string',
      bind: 'frontalLov.frontalName',
    },
    {
      name: 'jobCode',
      label: SCHEDULED_TASK_LANG.SCHEDULED_TASK_CODE,
      type: 'string',
      required: true,
    },
    {
      name: 'jobName',
      label: SCHEDULED_TASK_LANG.SCHEDULED_TASK_NAME,
      type: 'string',
      required: true,
    },
    {
      name: 'jobType',
      label: SCHEDULED_TASK_LANG.TYPE,
      type: 'string',
      lookupCode: FRONTAL_JOB_JOB_TYPE,
      required: true,
    },
    {
      name: 'jobCron',
      label: SCHEDULED_TASK_LANG.CRON,
      type: 'string',
      required: true,
    },
    {
      name: 'jobClassName',
      label: SCHEDULED_TASK_LANG.PROGRAM_NAME,
      type: 'string',
      required: true,
    },
    {
      name: 'jobClass',
      label: SCHEDULED_TASK_LANG.CLASS_NAME,
      type: 'string',
      required: true,
    },
    {
      name: 'jobClassMethod',
      label: SCHEDULED_TASK_LANG.METHOD_NAME,
      type: 'string',
      required: true,
    },
    {
      name: 'statusCode',
      label: SCHEDULED_TASK_LANG.STATUS,
      type: 'string',
      lookupCode: FRONTAL_JOB_JOB_STATUS,
      disabled: true,
    },
  ],
  transport: {
    read: (config) => {
      const { data } = config;
      const { jobId } = data;
      return {
        url: `${HZERO_HFNT}/v1${level}/jobs/${jobId}`,
        params: { ...data },
        method: 'GET',
      };
    },
    create: ({ data }) => ({
      url: `${HZERO_HFNT}/v1${level}/jobs`,
      data: data[0],
      method: 'POST',
    }),
    update: ({ data }) => ({
      url: `${HZERO_HFNT}/v1${level}/jobs`,
      data: data[0],
      method: 'PUT',
    }),
  },
};

const modalTableDS = {
  autoQuery: false,
  pageSize: 10,
  primaryKey: 'paramId',
  fields: [
    {
      name: 'seqNum',
      label: SCHEDULED_TASK_LANG.SEQ_NUMBER,
      type: 'number',
      required: true,
      unique: true,
    },
    {
      name: 'paramType',
      label: SCHEDULED_TASK_LANG.PARAM_TYPE,
      type: 'string',
      lookupCode: FRONTAL_JOB_JOB_PARAM_TYPE,
      required: true,
    },
    {
      name: 'paramName',
      label: SCHEDULED_TASK_LANG.PARAM_NAME,
      type: 'string',
      required: true,
    },
    {
      name: 'paramValue',
      label: SCHEDULED_TASK_LANG.PARAM_VALUE,
      type: 'string',
    },
  ],
  transport: {
    read: (config) => {
      const { data, params } = config;
      return {
        url: `${HZERO_HFNT}/v1${level}/job-params`,
        params: {
          ...data,
          ...params,
        },
        method: 'GET',
      };
    },
    create: ({ data }) => ({
      url: `${HZERO_HFNT}/v1${level}/job-params/batch-create`,
      data,
      method: 'POST',
    }),
    update: ({ data }) => ({
      url: `${HZERO_HFNT}/v1${level}/job-params/batch-update`,
      data,
      method: 'PUT',
    }),
    destroy: ({ data }) => ({
      url: `${HZERO_HFNT}/v1${level}/job-params/batch-delete`,
      data,
      method: 'DELETE',
    }),
  },
};

export { drawerFormDS, tableDS, modalTableDS };
