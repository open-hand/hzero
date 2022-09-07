import { HZERO_HITF } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel, getCurrentTenant } from 'utils/utils';
import getLang from '@/langs/interfacesLang';
import {
  AUTH_LEVEL,
  AUTH_TYPE,
  SELF_TENANT,
  USER_ROLE,
  APPLICATION_CLIENT,
} from '@/constants/CodeConstants';

const organizationId = getCurrentOrganizationId();
const level = isTenantRoleLevel() ? `/${organizationId}` : '';

const basicFormDS = (props = {}) => {
  const { onFieldUpdate = () => {} } = props;
  return {
    autoQuery: false,
    autoCreate: true,
    selection: false,
    paging: false,
    fields: [
      {
        name: 'authLevel',
        label: getLang('AUTH_LEVEL'),
        type: 'string',
        required: true,
        lookupCode: AUTH_LEVEL,
      },
      {
        name: 'authLevelValue',
      },
      {
        name: 'authLevelValueMeaning',
      },
      {
        name: 'authLevelValueTenantLov',
        label: getLang('AUTH_LEVEL_VALUE'),
        type: 'object',
        ignore: 'always',
        lovCode: SELF_TENANT,
        valueField: 'tenantId',
        textField: 'tenantName',
        lovPara: {
          tenantNum: isTenantRoleLevel() ? getCurrentTenant().tenantNum : null,
        },
        dynamicProps: {
          required: ({ record }) => record.get('authLevel') === 'TENANT',
        },
      },
      {
        name: 'authLevelValueTenant',
        bind: 'authLevelValueTenantLov.tenantId',
      },
      {
        name: 'authLevelValueMeaningTenant',
        bind: 'authLevelValueTenantLov.tenantName',
      },
      {
        name: 'authLevelValueRoleLov',
        label: getLang('AUTH_LEVEL_VALUE'),
        type: 'object',
        ignore: 'always',
        lovCode: USER_ROLE,
        valueField: 'id',
        textField: 'name',
        lovPara: {
          tenantId: isTenantRoleLevel() ? organizationId : null,
        },
        dynamicProps: {
          required: ({ record }) => record.get('authLevel') === 'ROLE',
        },
      },
      {
        name: 'authLevelValueRole',
        bind: 'authLevelValueRoleLov.id',
      },
      {
        name: 'authLevelValueMeaningRole',
        bind: 'authLevelValueRoleLov.name',
      },
      {
        name: 'roleId',
        bind: 'authLevelValueRoleLov.id',
      },
      {
        name: 'authLevelValueClientLov',
        label: getLang('AUTH_LEVEL_VALUE'),
        type: 'object',
        ignore: 'always',
        lovCode: APPLICATION_CLIENT,
        valueField: 'clientName',
        textField: 'clientName',
        lovPara: {
          tenantId: isTenantRoleLevel() ? organizationId : null,
        },
        dynamicProps: {
          required: ({ record }) => record.get('authLevel') === 'CLIENT',
        },
      },
      {
        name: 'authLevelValueClient',
        bind: 'authLevelValueClientLov.clientName',
      },
      {
        name: 'authLevelValueMeaningClient',
        bind: 'authLevelValueClientLov.clientName',
      },
      {
        name: 'authType',
        label: getLang('AUTH_TYPE'),
        type: 'string',
        required: true,
        lookupCode: AUTH_TYPE,
        defaultValue: 'NONE',
      },
      {
        name: 'remark',
        label: getLang('REMARK'),
        type: 'string',
      },
    ],
    transport: {
      read: ({ data }) => {
        const { interfaceId, interfaceAuthId } = data;
        return {
          url: `${HZERO_HITF}/v1${level}/${interfaceId}/auth/${interfaceAuthId}`,
          method: 'GET',
          data: null,
        };
      },
    },
    events: {
      update: onFieldUpdate,
    },
  };
};

export { basicFormDS };
