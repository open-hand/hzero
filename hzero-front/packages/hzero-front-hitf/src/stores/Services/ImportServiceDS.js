import { isTenantRoleLevel, getCurrentOrganizationId, getCurrentTenant } from 'utils/utils';
import { CODE_UPPER } from 'utils/regExp';
import { HZERO_HITF } from 'utils/config';
import { TENANT, YES_OR_NO_FLAG, REQUEST_METHOD } from '@/constants/CodeConstants';
import getLang from '@/langs/serviceLang';

const organizationId = getCurrentOrganizationId();
const level = isTenantRoleLevel() ? `/${organizationId}` : '';

const basicDS = () => {
  return {
    autoCreate: true,
    fields: [
      !isTenantRoleLevel() && {
        name: 'tenantLov',
        label: getLang('TENANT_NAME'),
        type: 'object',
        required: true,
        lovCode: TENANT,
        noCache: true,
        valueField: 'tenantId',
        textField: 'tenantName',
        ignore: 'always',
      },
      !isTenantRoleLevel() && {
        name: 'tenantId',
        type: 'string',
        bind: 'tenantLov.tenantId',
      },
      {
        name: 'namespace',
        type: 'string',
        bind: 'tenantLov.tenantNum',
        defaultValue: isTenantRoleLevel() ? getCurrentTenant()?.tenantNum : undefined,
      },
      {
        name: 'importUrl',
        type: 'string',
        label: getLang('IMPORT_URL'),
        required: true,
      },
      {
        name: 'serverName',
        type: 'string',
        label: getLang('SERVER_NAME'),
        required: true,
      },
      {
        name: 'serverCode',
        type: 'string',
        label: getLang('SERVER_CODE'),
        required: true,
        format: 'uppercase',
        pattern: CODE_UPPER,
        defaultValidationMessages: {
          patternMismatch: getLang('CODE_UPPER'),
        },
      },
      {
        name: 'publicFlag',
        type: 'string',
        label: getLang('PUBLIC_FLAG'),
        lookupCode: YES_OR_NO_FLAG,
        defaultValue: '0',
        transformRequest: (value) => value === '1',
      },
    ],
    transport: {
      create: ({ data }) => {
        const { otherWsdlInfo, ...otherData } = data[0];
        const { importUrl } = otherWsdlInfo;
        return {
          url: `${HZERO_HITF}/v1${level}/interface-servers/import-soap`,
          method: 'POST',
          data: {
            ...otherWsdlInfo,
            ...otherData,
            importUrl,
          },
        };
      },
    },
  };
};

const advanceDS = () => {
  return {
    autoCreate: true,
    fields: [
      {
        name: 'username',
        type: 'string',
        label: getLang('USERNAME'),
      },
      {
        name: 'password',
        type: 'string',
        label: getLang('AuthPASSWORD'),
      },
    ],
  };
};

const restfulDS = () => {
  return {
    autoCreate: true,
    fields: [
      !isTenantRoleLevel() && {
        name: 'tenantLov',
        label: getLang('TENANT_NAME'),
        type: 'object',
        required: true,
        lovCode: TENANT,
        noCache: true,
        ignore: 'always',
        valueField: 'tenantId',
        textField: 'tenantName',
      },
      !isTenantRoleLevel() && {
        name: 'tenantId',
        type: 'string',
        bind: 'tenantLov.tenantId',
      },
      {
        name: 'namespace',
        type: 'string',
        bind: 'tenantLov.tenantNum',
        defaultValue: isTenantRoleLevel() ? getCurrentTenant()?.tenantNum : undefined,
      },
      {
        name: 'serverName',
        type: 'string',
        label: getLang('SERVER_NAME'),
        required: true,
      },
      {
        name: 'serverCode',
        type: 'string',
        label: getLang('SERVER_CODE'),
        required: true,
        format: 'uppercase',
        pattern: CODE_UPPER,
        defaultValidationMessages: {
          patternMismatch: getLang('CODE_UPPER'),
        },
      },
      {
        name: 'requestMethod',
        type: 'string',
        label: getLang('REQUEST'),
        defaultValue: 'POST',
        lookupCode: REQUEST_METHOD,
      },
      {
        name: 'importUrl',
        type: 'url',
        label: getLang('SWAGGER'),
        required: true,
      },
      {
        name: 'publicFlag',
        type: 'string',
        label: getLang('PUBLIC_FLAG'),
        lookupCode: YES_OR_NO_FLAG,
        defaultValue: '0',
        transformRequest: (value) => value === '1',
      },
      {
        name: 'swaggerFlag',
        type: 'boolean',
        defaultValue: false,
      },
    ],
    transport: {
      create: ({ data }) => {
        return {
          url: `${HZERO_HITF}/v1${level}/interface-servers/import-rest`,
          method: 'POST',
          data: data[0],
        };
      },
    },
  };
};

const treeDS = () => {
  return {
    autoCreate: true,
    primaryKey: 'id',
    parentField: 'parentId',
    idField: 'id',
    selection: false,
    checkable: true,
    fields: [
      { name: 'id', type: 'string' },
      { name: 'parentId', type: 'string' },
      { name: 'name', type: 'string' },
    ],
  };
};
export { basicDS, advanceDS, restfulDS, treeDS };
