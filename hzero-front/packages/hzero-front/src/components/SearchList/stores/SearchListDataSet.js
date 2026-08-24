import { getEnvConfig } from 'utils/iocUtils';
import { getCurrentOrganizationId } from 'utils/utils';

const organizationId = getCurrentOrganizationId();

const { HZERO_PLATFORM } = getEnvConfig();

export default ({ searchCode }) => ({
  paging: false,
  autoQuery: false,
  primaryKey: 'searchId',
  transport: {
    read: () => ({
      url: `${HZERO_PLATFORM}/v1/${organizationId}/search-config?searchCode=${searchCode}`,
      method: 'get',
    }),
    destroy: ({ data }) => ({
      url: `${HZERO_PLATFORM}/v1/${organizationId}/search-config/${data[0].searchId}`,
      data: data[0],
      method: 'delete',
    }),
  },
  fields: [
    { name: 'searchName', type: 'string' },
    { name: 'searchId', type: 'number' },
    { name: 'searchCode', type: 'string' },
    { name: 'queryModified', type: 'boolean', ignore: 'always', defaultValue: false },
  ],
});
