import { HZERO_HFNT } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel, getCurrentRole } from 'utils/utils';
import { FRONTAL_MACHINE_STATUS, TENANT, CLIENT } from '@/constants/CodeConstants';
import PREPOSED_MACHINE_LANG from '@/langs/preposedMachineLang';
import FRONTAL_MANAGEMENT_LANG from '@/langs/frontalManagementLang';

const organizationId = getCurrentOrganizationId();
const level = isTenantRoleLevel() ? `/${organizationId}` : '';

const tableDS = {
  autoQuery: true,
  pageSize: 10,
  selection: false,
  primaryKey: 'frontalId',
  queryFields: [
    {
      name: 'frontalCode',
      label: PREPOSED_MACHINE_LANG.MACHINE_CODE,
      type: 'string',
    },
    {
      name: 'frontalName',
      label: PREPOSED_MACHINE_LANG.MACHINE_NAME,
      type: 'string',
    },
    {
      name: 'statusCode',
      label: PREPOSED_MACHINE_LANG.STATUS,
      type: 'string',
      lookupCode: FRONTAL_MACHINE_STATUS,
    },
    {
      name: 'clientLov',
      label: PREPOSED_MACHINE_LANG.CLIENT,
      type: 'object',
      ignore: 'always',
      lovCode: CLIENT,
      lovPara: { roleId: getCurrentRole().id },
      valueFiled: 'id',
      textField: 'name',
    },
    {
      name: 'clientId',
      type: 'string',
      bind: 'clientLov.id',
    },
    {
      name: 'requestUrl',
      label: PREPOSED_MACHINE_LANG.REQUEST_URL,
      type: 'string',
    },
  ],
  fields: [
    {
      name: 'seqNumber',
      label: PREPOSED_MACHINE_LANG.SEQ_NUMBER,
      type: 'number',
    },
    {
      name: 'frontalCode',
      label: PREPOSED_MACHINE_LANG.MACHINE_CODE,
      type: 'string',
    },
    {
      name: 'frontalName',
      label: PREPOSED_MACHINE_LANG.MACHINE_NAME,
      type: 'string',
    },
    {
      name: 'requestUrl',
      label: PREPOSED_MACHINE_LANG.REQUEST_URL,
      type: 'string',
    },
    {
      name: 'statusCode',
      label: PREPOSED_MACHINE_LANG.STATUS,
      type: 'string',
      lookupCode: FRONTAL_MACHINE_STATUS,
    },
    {
      name: 'clientName',
      label: PREPOSED_MACHINE_LANG.CLIENT,
      type: 'string',
    },
    {
      name: 'tenantName',
      label: PREPOSED_MACHINE_LANG.TENANT,
      type: 'string',
    },
    {
      name: 'remark',
      label: PREPOSED_MACHINE_LANG.REMARK,
      type: 'string',
    },
    {
      name: '_requestType',
      type: 'string',
    },
  ],
  transport: {
    read: (config) => {
      const { data, params } = config;
      return {
        url: `${HZERO_HFNT}/v1${level}/frontal-servers`,
        params: {
          ...data,
          ...params,
        },
        method: 'GET',
      };
    },
    update: ({ data }) => {
      const { _requestType, frontalId } = data[0];
      return {
        url: `${HZERO_HFNT}/v1${level}/frontal-servers/${frontalId}/${_requestType}`,
        method: 'PUT',
      };
    },
  },
};

const drawerFormDS = {
  primaryKey: 'frontalId',
  autoQuery: false,
  autoCreate: false,
  selection: false,
  paging: false,
  fields: [
    {
      name: 'frontalCode',
      label: PREPOSED_MACHINE_LANG.MACHINE_CODE,
      type: 'string',
      required: true,
    },
    {
      name: 'frontalName',
      label: PREPOSED_MACHINE_LANG.MACHINE_NAME,
      type: 'string',
      required: true,
    },
    {
      name: 'requestUrl',
      label: PREPOSED_MACHINE_LANG.REQUEST_URL,
      type: 'string',
      required: true,
    },
    {
      name: 'statusCode',
      label: PREPOSED_MACHINE_LANG.STATUS,
      type: 'string',
      lookupCode: FRONTAL_MACHINE_STATUS,
      disabled: true,
    },
    {
      name: 'clientLov',
      label: PREPOSED_MACHINE_LANG.CLIENT,
      // label: HIAM.ORGANIZATION.CLIENT,
      type: 'object',
      lovCode: CLIENT,
      required: true,
      ignore: 'always',
      valueFiled: 'id',
      textField: 'name',
      dynamicProps: ({ record }) => {
        return {
          lovPara: {
            roleId: getCurrentRole().id,
            tenantId: isTenantRoleLevel() ? getCurrentOrganizationId() : record.get('tenantId'),
          },
        };
      },
    },
    {
      name: 'clientId',
      type: 'string',
      bind: 'clientLov.id',
    },
    {
      name: 'clientName',
      type: 'string',
      bind: 'clientLov.name',
    },
    {
      name: 'tenantLov',
      label: PREPOSED_MACHINE_LANG.TENANT,
      type: 'object',
      lovCode: TENANT,
      ignore: 'always',
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
      bind: 'tenantLov.tenantName',
    },
    {
      name: 'remark',
      label: PREPOSED_MACHINE_LANG.REMARK,
      type: 'string',
    },
  ],
  transport: {
    read: (config) => {
      const { data } = config;
      const { frontalId } = data;
      return {
        url: `${HZERO_HFNT}/v1${level}/frontal-servers/${frontalId}`,
        params: { ...data },
        method: 'GET',
      };
    },
    create: ({ data }) => ({
      url: `${HZERO_HFNT}/v1${level}/frontal-servers`,
      data: data[0],
      method: 'POST',
    }),
    update: ({ data }) => ({
      url: `${HZERO_HFNT}/v1${level}/frontal-servers`,
      data: data[0],
      method: 'PUT',
    }),
  },
};

const programListDS = {
  autoQuery: false,
  pageSize: 10,
  selection: false,
  primaryKey: 'taskId',
  fields: [
    {
      name: 'taskId',
      type: 'string',
    },
    {
      name: 'programId',
      type: 'string',
    },
    {
      name: 'tenantLov',
      type: 'object',
      ignore: 'always',
      lovCode: 'HPFM.TENANT',
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
      name: 'statusCode',
      type: 'string',
      label: FRONTAL_MANAGEMENT_LANG.STATUS,
      lookupCode: 'HFNT.FRONTAL_TASK.STATUS',
    },
    {
      name: 'errorStack',
      type: 'string',
      label: FRONTAL_MANAGEMENT_LANG.ERROR_STACK,
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
  ],
  transport: {
    read: ({ data }) => {
      const { frontalId } = data;
      return {
        url: `${HZERO_HFNT}/v1${level}/frontal-task/${frontalId}`,
        method: 'GET',
      };
    },
    update: ({ data }) => {
      const { isLoad } = data[0];
      if (isLoad) {
        return {
          url: `${HZERO_HFNT}/v1${level}/frontal-task/unload`,
          data: data[0],
          method: 'POST',
        };
      } else {
        return {
          url: `${HZERO_HFNT}/v1${level}/frontal-task/load`,
          data: data[0],
          method: 'POST',
        };
      }
    },
  },
};

export { drawerFormDS, tableDS, programListDS };
