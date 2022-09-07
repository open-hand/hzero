import { HZERO_HORC } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import FIELD_MAPPING_LANG from '@/langs/fieldMappingLang';
import { TRANSFORM_TYPE, TRANSFORM_STATUS } from '@/constants/CodeConstants';

const organizationId = getCurrentOrganizationId();
const level = isTenantRoleLevel() ? `/${organizationId}` : '';

const tableDS = () => ({
  autoQuery: true,
  pageSize: 10,
  selection: false,
  primaryKey: 'transformId',
  queryFields: [
    {
      name: 'transformCode',
      label: FIELD_MAPPING_LANG.TRANSFORM_CODE,
      type: 'string',
    },
    {
      name: 'transformName',
      label: FIELD_MAPPING_LANG.TRANSFORM_NAME,
      type: 'string',
    },
    {
      name: 'transformType',
      label: FIELD_MAPPING_LANG.TRANSFORM_TYPE,
      type: 'string',
      lookupCode: TRANSFORM_TYPE,
    },
  ],
  fields: [
    {
      name: 'transformCode',
      label: FIELD_MAPPING_LANG.TRANSFORM_CODE,
      type: 'string',
    },
    {
      name: 'transformName',
      label: FIELD_MAPPING_LANG.TRANSFORM_NAME,
      type: 'string',
    },
    {
      name: 'transformType',
      label: FIELD_MAPPING_LANG.TRANSFORM_TYPE,
      type: 'string',
      lookupCode: TRANSFORM_TYPE,
    },
    {
      name: 'versionDesc',
      label: FIELD_MAPPING_LANG.VERSION,
      type: 'string',
    },
    {
      name: 'fromVersionDesc',
      label: FIELD_MAPPING_LANG.FROM_VERSION,
      type: 'string',
    },
    {
      name: 'statusCode',
      label: FIELD_MAPPING_LANG.STATUS,
      type: 'string',
      lookupCode: TRANSFORM_STATUS,
    },
  ],
  transport: {
    read: (config) => {
      const { data, params } = config;
      return {
        url: `${HZERO_HORC}/v1${level}/transforms`,
        params: {
          ...data,
          ...params,
        },
        method: 'GET',
      };
    },
    destroy: ({ data }) => {
      return {
        url: `${HZERO_HORC}/v1${level}/transforms`,
        method: 'DELETE',
        data: data[0],
      };
    },
    update: ({ data }) => {
      const { _requestType, transformId } = data[0];
      return {
        url: `${HZERO_HORC}/v1${level}/transforms/${transformId}/${_requestType}`,
        method: 'POST',
        data: data[0],
      };
    },
  },
});

const historyTableDS = () => ({
  autoQuery: false,
  pageSize: 10,
  selection: false,
  primaryKey: 'transformHistoryId',
  fields: [
    {
      name: 'transformCode',
      label: FIELD_MAPPING_LANG.TRANSFORM_CODE,
      type: 'string',
    },
    {
      name: 'transformName',
      label: FIELD_MAPPING_LANG.TRANSFORM_NAME,
      type: 'string',
    },
    {
      name: 'transformType',
      label: FIELD_MAPPING_LANG.TRANSFORM_TYPE,
      type: 'string',
      lookupCode: TRANSFORM_TYPE,
    },
    {
      name: 'versionDesc',
      label: FIELD_MAPPING_LANG.VERSION,
      type: 'string',
    },
    {
      name: 'fromVersionDesc',
      label: FIELD_MAPPING_LANG.FROM_VERSION,
      type: 'string',
    },
  ],
  transport: {
    read: (config) => {
      const { data } = config;
      const { transformId } = data;
      return {
        url: `${HZERO_HORC}/v1${level}/transforms/${transformId}/former-version`,
        method: 'GET',
      };
    },
  },
});

const formDS = (props) => {
  const { _required = true } = props;
  return {
    primaryKey: 'transformId',
    autoQuery: false,
    autoCreate: false,
    selection: false,
    paging: false,
    fields: [
      {
        name: 'transformCode',
        label: FIELD_MAPPING_LANG.TRANSFORM_CODE,
        type: 'string',
        required: _required,
      },
      {
        name: 'transformName',
        label: FIELD_MAPPING_LANG.TRANSFORM_NAME,
        type: 'string',
        required: _required,
      },
      {
        name: 'transformType',
        label: FIELD_MAPPING_LANG.TRANSFORM_TYPE,
        type: 'string',
        required: true,
        lookupCode: TRANSFORM_TYPE,
      },
      {
        name: 'statusCode',
        label: FIELD_MAPPING_LANG.STATUS,
        type: 'string',
        lookupCode: TRANSFORM_STATUS,
        defaultValue: 'NEW',
      },
      {
        name: 'version',
        type: 'number',
        defaultValue: 1,
      },
      {
        name: 'versionDesc',
        label: FIELD_MAPPING_LANG.VERSION,
        type: 'string',
      },
      {
        name: 'transformScript',
        label: FIELD_MAPPING_LANG.TRANSFORM_SCRIPT,
        type: 'string',
        required: true,
      },
      {
        name: 'sourceStructure',
        label: FIELD_MAPPING_LANG.SOURCE_STRUCTURE,
        type: 'string',
        required: true,
      },
      {
        name: 'targetStructure',
        label: FIELD_MAPPING_LANG.TARGET_STRUCTURE,
        type: 'string',
        required: true,
      },
    ],
    transport: {
      read: ({ data }) => {
        const { transformId, _historyFlag, version } = data;
        return {
          url: !_historyFlag
            ? `${HZERO_HORC}/v1${level}/transforms/${transformId}`
            : `${HZERO_HORC}/v1${level}/transforms/${transformId}/former-version/${version}`,
          data: null,
          method: 'GET',
        };
      },
      create: ({ data }) => ({
        url: `${HZERO_HORC}/v1${level}/transforms`,
        data: data[0],
        method: 'POST',
      }),
      update: ({ data }) => {
        const { _historyFlag, transformId, version } = data[0];
        return _historyFlag
          ? {
              url: `${HZERO_HORC}/v1${level}/transforms/${transformId}/former-version/override/${version}`,
              data: null,
              method: 'POST',
            }
          : {
              url: `${HZERO_HORC}/v1${level}/transforms`,
              data: data[0],
              method: 'POST',
            };
      },
    },
    events: {
      load: ({ dataSet }) => {
        dataSet.current.setState('version', dataSet.current.get('version'));
      },
    },
  };
};

const fieldDataDrawerDS = () => ({
  autoQuery: false,
  autoCreate: false,
  selection: false,
  paging: false,
  fields: [
    {
      name: 'inputData',
      type: 'string',
      required: true,
    },
  ],
});

export { tableDS, historyTableDS, formDS, fieldDataDrawerDS };
