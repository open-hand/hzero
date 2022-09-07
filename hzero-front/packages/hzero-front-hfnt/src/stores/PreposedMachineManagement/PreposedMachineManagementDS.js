import { HZERO_HFNT } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import FRONTAL_MANAGEMENT_LANG from '@/langs/frontalManagementLang';

const organizationId = getCurrentOrganizationId();
const level = isTenantRoleLevel() ? `/${organizationId}` : '';

const issueDetailDS = {
  autoQuery: false,
  pageSize: 10,
  selection: false,
  primaryKey: 'frontalManagementId',
  fields: [
    {
      name: 'tenantName',
      type: 'string',
      label: FRONTAL_MANAGEMENT_LANG.TENANT_NAME,
    },
    {
      name: 'frontalCode',
      type: 'string',
      label: FRONTAL_MANAGEMENT_LANG.FRONTAL_CODE,
    },
    {
      name: 'requestUrl',
      type: 'string',
      label: FRONTAL_MANAGEMENT_LANG.FRONTAL_ADDRESS,
    },
    {
      name: 'issueStatus',
      type: 'string',
      label: FRONTAL_MANAGEMENT_LANG.STATUS,
      lookupCode: 'HFNT.FRONTAL_TASK.STATUS',
    },
    {
      name: 'errorStack',
      type: 'string',
      label: FRONTAL_MANAGEMENT_LANG.ERROR_STACK,
    },
    {
      name: 'issueDate',
      type: 'date',
      label: FRONTAL_MANAGEMENT_LANG.ISSUE_DATE,
      format: 'YYYY-MM-DD HH:mm:ss',
    },
  ],
  transport: {
    read: () => ({
      url: `${HZERO_HFNT}/v1${level}/frontal-servers/issued-details`,
      method: 'GET',
    }),
  },
};

const issueDS = {
  autoQuery: false,
  pageSize: 10,
  selection: 'multiple',
  primaryKey: 'frontalManagementId',
  fields: [
    {
      name: 'tenantName',
      type: 'string',
      label: FRONTAL_MANAGEMENT_LANG.TENANT_NAME,
    },
    {
      name: 'frontalCode',
      type: 'string',
      label: FRONTAL_MANAGEMENT_LANG.FRONTAL_CODE,
    },
    {
      name: 'requestUrl',
      type: 'string',
      label: FRONTAL_MANAGEMENT_LANG.FRONTAL_ADDRESS,
    },
    {
      name: 'statusCode',
      type: 'string',
      label: FRONTAL_MANAGEMENT_LANG.STATUS,
      lookupCode: 'HFNT.FRONTAL_SERVER.STATUS',
    },
  ],
  queryFields: [
    {
      name: 'tenantLov',
      label: FRONTAL_MANAGEMENT_LANG.TENANT_NAME,
      type: 'object',
      ignore: 'always',
      lovCode: isTenantRoleLevel() ? 'HPFM.TENANT.ORG' : 'HPFM.TENANT',
      valueFiled: 'tenantId',
      textField: 'tenantName',
    },
    {
      name: 'tenantId',
      type: 'string',
      bind: 'tenantLov.tenantId',
    },
    {
      name: 'frontalCode',
      type: 'string',
      label: FRONTAL_MANAGEMENT_LANG.FRONTAL_CODE,
    },
  ],
  transport: {
    read: () => ({
      url: `${HZERO_HFNT}/v1${level}/frontal-servers/issued?statusCode=ONLINE`,
      method: 'GET',
    }),
    create: ({ data }) => ({
      url: `${HZERO_HFNT}/v1${level}/frontal-task/batch-update`,
      data,
      method: 'POST',
    }),
  },
};

const tableDS = {
  autoQuery: true,
  pageSize: 10,
  selection: false,
  primaryKey: 'frontalManagementId',
  fields: [
    {
      name: 'programId',
      type: 'string',
    },
    {
      name: 'tenantLov',
      type: 'object',
      ignore: 'always',
      lovCode: isTenantRoleLevel() ? 'HPFM.TENANT.ORG' : 'HPFM.TENANT',
      valueFiled: 'tenantId',
      textField: 'tenantName',
    },
    {
      name: 'tenantName',
      type: 'string',
      label: FRONTAL_MANAGEMENT_LANG.TENANT_NAME,
      bind: 'tenantLov.tenantName',
    },
    {
      name: 'programCode',
      type: 'string',
      label: FRONTAL_MANAGEMENT_LANG.PROGRAM_CODE,
    },
    {
      name: 'programType',
      type: 'string',
      label: FRONTAL_MANAGEMENT_LANG.PROGRAM_TYPE,
      lookupCode: 'HFNT.FRONTAL_PROGRAM.TYPE',
    },
    {
      name: 'programName',
      type: 'string',
      label: FRONTAL_MANAGEMENT_LANG.PROGRAM_NAME,
    },
    {
      name: 'programDesc',
      type: 'string',
      label: FRONTAL_MANAGEMENT_LANG.PROGRAM_DESC,
    },
    {
      name: 'creationDate',
      type: 'date',
      label: FRONTAL_MANAGEMENT_LANG.CREATION_DATE,
      format: 'YYYY-MM-DD HH:mm:ss',
    },
    {
      name: 'statusCode',
      type: 'string',
      label: FRONTAL_MANAGEMENT_LANG.STATUS,
      lookupCode: 'HFNT.FRONTAL_PROGRAM.STATUS',
    },
  ],
  queryFields: [
    {
      name: 'programCode',
      type: 'string',
      label: FRONTAL_MANAGEMENT_LANG.PROGRAM_CODE,
    },
    {
      name: 'programType',
      type: 'string',
      label: FRONTAL_MANAGEMENT_LANG.PROGRAM_TYPE,
      lookupCode: 'HFNT.FRONTAL_PROGRAM.TYPE',
    },
    {
      name: 'programName',
      type: 'string',
      label: FRONTAL_MANAGEMENT_LANG.PROGRAM_NAME,
    },
    {
      name: 'programDesc',
      type: 'string',
      label: FRONTAL_MANAGEMENT_LANG.PROGRAM_DESC,
    },
    {
      name: 'creationDate',
      type: 'date',
      range: ['start', 'end'],
      label: FRONTAL_MANAGEMENT_LANG.CREATION_DATE,
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
    read: () => ({
      url: `${HZERO_HFNT}/v1${level}/frontal-program/`,
      method: 'GET',
    }),
    update: ({ data }) => ({
      url: `${HZERO_HFNT}/v1${level}/frontal-program/update-status`,
      data: data[0],
      method: 'PUT',
    }),
  },
};

const editDS = {
  autoQuery: false,
  autoCreate: false,
  paging: false,
  selection: false,
  primaryKey: 'programId',
  fields: [
    {
      name: 'tenantLov',
      type: 'object',
      label: FRONTAL_MANAGEMENT_LANG.TENANT,
      ignore: 'always',
      lovCode: isTenantRoleLevel() ? 'HPFM.TENANT.ORG' : 'HPFM.TENANT',
      valueFiled: 'tenantId',
      textField: 'tenantName',
    },
    {
      name: 'tenantId',
      type: 'number',
      bind: 'tenantLov.tenantId',
    },
    {
      name: 'tenantName',
      type: 'string',
      label: FRONTAL_MANAGEMENT_LANG.TENANT_NAME,
      bind: 'tenantLov.tenantName',
    },
    {
      name: 'programType',
      type: 'string',
      label: FRONTAL_MANAGEMENT_LANG.PROGRAM_TYPE,
      lookupCode: 'HFNT.FRONTAL_PROGRAM.TYPE',
    },
    {
      name: 'programCode',
      type: 'string',
      label: FRONTAL_MANAGEMENT_LANG.PROGRAM_CODE,
      required: true,
    },
    {
      name: 'programName',
      type: 'string',
      label: FRONTAL_MANAGEMENT_LANG.PROGRAM_NAME,
    },
    {
      name: 'programDesc',
      type: 'string',
      label: FRONTAL_MANAGEMENT_LANG.PROGRAM_DESC,
    },
    {
      name: 'statusCode',
      type: 'string',
      label: FRONTAL_MANAGEMENT_LANG.STATUS,
      // defaultValue: 'DISABLED',
      lookupCode: 'HFNT.FRONTAL_PROGRAM.STATUS',
    },
  ],
  transport: {
    read: ({ data }) => {
      const { programId } = data;
      return {
        url: `${HZERO_HFNT}/v1${level}/frontal-program/${programId}`,
        method: 'GET',
      };
    },
    create: ({ data }) => ({
      url: `${HZERO_HFNT}/v1${level}/frontal-program`,
      data: data[0],
      method: 'POST',
    }),
    update: ({ data }) => ({
      url: `${HZERO_HFNT}/v1${level}/frontal-program`,
      data: data[0],
      method: 'PUT',
    }),
  },
};

const treeDS = {
  primaryKey: 'tenantId',
  autoQuery: false,
  selection: false,
  fields: [
    { name: 'tenantId', type: 'number' },
    { name: 'tenantName', type: 'string' },
    { name: 'tenantNum', type: 'string' },
  ],
  transport: {
    read: () => ({
      url: `${HZERO_HFNT}/v1${level}/frontal-program/frontal-program-tenant`,
      method: 'GET',
    }),
  },
};

export { tableDS, issueDetailDS, issueDS, editDS, treeDS };
