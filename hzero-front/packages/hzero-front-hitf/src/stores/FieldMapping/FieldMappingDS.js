import { HZERO_HORC } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import getLang from '@/langs/fieldMappingLang';
import { TRANSFORM_TYPE, TRANSFORM_STATUS } from '@/constants/CodeConstants';

const organizationId = getCurrentOrganizationId();
const level = isTenantRoleLevel() ? `/${organizationId}` : '';

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
        label: getLang('TRANSFORM_CODE'),
        type: 'string',
        required: _required,
      },
      {
        name: 'transformName',
        label: getLang('TRANSFORM_NAME'),
        type: 'string',
        required: _required,
      },
      {
        name: 'transformType',
        label: getLang('TRANSFORM_TYPE'),
        type: 'string',
        required: true,
        lookupCode: TRANSFORM_TYPE,
      },
      {
        name: 'statusCode',
        label: getLang('STATUS'),
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
        label: getLang('VERSION'),
        type: 'string',
      },
      {
        name: 'transformScript',
        label: getLang('TRANSFORM_SCRIPT'),
        type: 'string',
        required: true,
      },
      {
        name: 'sourceStructure',
        label: getLang('SOURCE_STRUCTURE'),
        type: 'string',
        required: true,
      },
      {
        name: 'targetStructure',
        label: getLang('TARGET_STRUCTURE'),
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

const fieldDataDrawerDS = () => {
  return {
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
  };
};

export { formDS, fieldDataDrawerDS };
