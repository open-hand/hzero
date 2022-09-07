import { HZERO_HFNT } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import FONTAL_LOGS_LANG from '@/langs/frontalLogsLang';

const organizationId = getCurrentOrganizationId();
const level = isTenantRoleLevel() ? `/${organizationId}` : '';

const tableDS = {
  autoQuery: true,
  pageSize: 10,
  selection: false,
  primaryKey: 'frontalLogId',
  fields: [
    {
      name: 'frontalLogId',
      type: 'string',
    },
    {
      name: 'creationDate',
      type: 'date',
      format: 'YYYY-MM-DD HH:mm:ss',
      label: FONTAL_LOGS_LANG.CREATION_DATE,
    },
    {
      name: 'cacheDate',
      type: 'string',
      label: FONTAL_LOGS_LANG.CACHE_DATE,
    },
    {
      name: 'sourceType',
      type: 'string',
      label: FONTAL_LOGS_LANG.SOURCE_TYPE,
    },
    {
      name: 'cacheFolder',
      type: 'string',
      label: FONTAL_LOGS_LANG.CACHE_FOLDER,
    },
    {
      name: 'statusCode',
      type: 'string',
      label: FONTAL_LOGS_LANG.STATUS_CODE,
    },
    {
      name: 'statusDesc',
      type: 'string',
      label: FONTAL_LOGS_LANG.STATUS_DESC,
    },
    {
      name: 'tenantName',
      type: 'string',
      label: FONTAL_LOGS_LANG.TENANT_NAME,
    },
    {
      name: 'frontalCode',
      type: 'string',
      label: FONTAL_LOGS_LANG.FRONTAL_CODE,
    },
    {
      name: 'frontalName',
      type: 'string',
      label: FONTAL_LOGS_LANG.FRONTAL_NAME,
    },
    {
      name: 'className',
      type: 'string',
      label: FONTAL_LOGS_LANG.CLASS_NAME,
    },
    {
      name: 'methodName',
      type: 'string',
      label: FONTAL_LOGS_LANG.METHOD_NAME,
    },
    {
      name: 'paramText',
      type: 'string',
      label: FONTAL_LOGS_LANG.METHOD_PARAM_VALUE,
    },
    {
      name: 'errorStack',
      type: 'string',
      label: FONTAL_LOGS_LANG.ERROR_STACK,
    },
  ],
  queryFields: [
    {
      name: 'frontalCode',
      type: 'string',
      label: FONTAL_LOGS_LANG.FRONTAL_CODE,
    },
    {
      name: 'frontalName',
      type: 'string',
      label: FONTAL_LOGS_LANG.FRONTAL_NAME,
    },
    {
      name: 'creationDate',
      type: 'date',
      range: ['start', 'end'],
      label: FONTAL_LOGS_LANG.CREATION_DATE,
      ignore: 'always',
    },
    {
      name: 'creationDateFrom',
      type: 'date',
      bind: 'creationDate.start',
    },
    {
      name: 'creationDateTo',
      type: 'date',
      bind: 'creationDate.end',
    },
  ],
  transport: {
    read: (config) => {
      const { dataSet, data, params } = config;
      if (data.retry) {
        const { frontalLogId } = data;
        delete data.frontalLogId;
        delete data.retry;
        delete params.page;
        delete params.size;
        dataSet.setQueryParameter('retry', false);
        return {
          url: `${HZERO_HFNT}/v1${level}/frontal-logs/retry?frontalLogId=${frontalLogId}`,
          method: 'GET',
        };
      }
      delete data.frontalLogId;
      delete data.retry;
      return {
        url: `${HZERO_HFNT}/v1${level}/frontal-logs`,
        params: {
          ...data,
          ...params,
        },
        method: 'GET',
      };
    },
  },
};

export { tableDS };
