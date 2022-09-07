import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';

const HZERO_HFNT = '/hpfm';

const organizationId = getCurrentOrganizationId();
const level = isTenantRoleLevel() ? `/${organizationId}` : '';

const lineDS = () => {
  return {
    autoQuery: true,
    primaryKey: 'formLineId',
    paging: false,
    fields: [
      {
        name: 'itemCode',
        type: 'string',
      },
      {
        name: 'itemName',
        type: 'string',
      },
      {
        name: 'itemTypeCode',
        type: 'string',
      },
    ],
    transport: {
      read: (config) => {
        const { data, params } = config;
        if (data.formCode) {
          return {
            url: `${HZERO_HFNT}/v1${level}/form-lines/header-code`,
            params: {
              ...data,
              ...params,
            },
            method: 'GET',
          };
        }
      },
    },
  };
};

const authenticationDetailDS = () => {
  return {
    autoCreate: true,
    primaryKey: 'formCode',
    fields: [
      {
        name: 'formCode',
        type: 'string',
        required: true,
        lookupCode: 'HITF.AUTH_TYPE',
      },
    ],
  };
};

export { lineDS, authenticationDetailDS };
