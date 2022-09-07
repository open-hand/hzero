import { HZERO_HORC } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import {
  ORCH_DEF_STATUS,
  ORCH_WARNING_TYPE,
  ORCH_PRIORITY,
  ALERT_CODE,
} from '@/constants/CodeConstants';
import ORCHESTRATION_LANG from '@/langs/orchestrationLang';

const organizationId = getCurrentOrganizationId();
const level = isTenantRoleLevel() ? `/${organizationId}` : '';

const tableDS = () => ({
  autoQuery: true,
  pageSize: 10,
  selection: false,
  primaryKey: 'definitionId',
  queryFields: [
    {
      name: 'definitionName',
      label: ORCHESTRATION_LANG.DEFINITION_NAME,
      type: 'string',
    },
    {
      name: 'statusCode',
      label: ORCHESTRATION_LANG.STATUS,
      type: 'string',
      lookupCode: ORCH_DEF_STATUS,
    },
    {
      name: 'remark',
      label: ORCHESTRATION_LANG.DESCRIPTION,
      type: 'string',
    },
  ],
  fields: [
    {
      name: 'seqNumber',
      label: ORCHESTRATION_LANG.SEQ_NUMBER,
      type: 'number',
    },
    {
      name: 'definitionName',
      label: ORCHESTRATION_LANG.DEFINITION_NAME,
      type: 'string',
    },
    {
      name: 'statusCode',
      label: ORCHESTRATION_LANG.STATUS,
      type: 'string',
      lookupCode: ORCH_DEF_STATUS,
    },
    {
      name: 'remark',
      label: ORCHESTRATION_LANG.DESCRIPTION,
      type: 'string',
    },
  ],
  transport: {
    read: (config) => {
      const { data, params } = config;
      return {
        url: `${HZERO_HORC}/v1${level}/orch-definitions`,
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
        url: `${HZERO_HORC}/v1${level}/orch-definitions/${_requestType}`,
        method: 'PUT',
        data: data[0],
      };
    },
    destroy: ({ data }) => ({
      url: `${HZERO_HORC}/v1${level}/orch-definitions`,
      method: 'DELETE',
      data: data[0],
    }),
  },
});

const executeFormDS = () => ({
  primaryKey: 'definitionId',
  autoQuery: false,
  autoCreate: false,
  selection: false,
  paging: false,
  fields: [
    {
      name: 'definitionId',
      type: 'string',
    },
    {
      name: 'definitionName',
      label: ORCHESTRATION_LANG.DEFINITION_NAME,
      type: 'string',
    },
    {
      name: 'dependencyType',
      type: 'string',
      defaultValue: 'FORWARD',
    },
    {
      name: 'failureStrategy',
      label: ORCHESTRATION_LANG.FAILED_STRATEGY,
      type: 'string',
      defaultValue: 'CONTINUE',
    },
    {
      name: 'warningType',
      label: ORCHESTRATION_LANG.WARNING_TYPE,
      type: 'string',
      defaultValue: 'NONE',
      lookupCode: ORCH_WARNING_TYPE,
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
      name: 'instancePriority',
      label: ORCHESTRATION_LANG.PRIORITY,
      type: 'string',
      lookupCode: ORCH_PRIORITY,
      defaultValue: 'MEDIUM',
    },
    {
      name: 'workerGroup',
      label: ORCHESTRATION_LANG.WORKER_GROUP,
      type: 'string',
      defaultValue: 'default',
    },
    {
      name: 'preference',
      type: 'string',
      label: ORCHESTRATION_LANG.PREFERENCE,
      placeholder: ORCHESTRATION_LANG.PREFERENCE_PLACEHOLDER,
    },
  ],
  transport: {
    create: ({ data }) => ({
      url: `${HZERO_HORC}/v1${level}/orch-statements`,
      data: data[0],
      method: 'POST',
    }),
  },
});

export { tableDS, executeFormDS };
