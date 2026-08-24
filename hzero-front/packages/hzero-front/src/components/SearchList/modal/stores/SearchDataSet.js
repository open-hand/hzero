import { getEnvConfig } from 'utils/iocUtils';
import { getCurrentOrganizationId, getCurrentUserId } from 'utils/utils';

const organizationId = getCurrentOrganizationId();
const userId = getCurrentUserId();

const { HZERO_PLATFORM } = getEnvConfig();

export default ({ searchCode }) => ({
  paging: false,
  primaryKey: 'searchId',
  transport: {
    create: ({ data }) => ({
      url: `${HZERO_PLATFORM}/v1/${organizationId}/search-config`,
      data: data[0],
    }),
    update: ({ data }) => ({
      url: `${HZERO_PLATFORM}/v1/${organizationId}/search-config/${data[0].searchId}`,
      method: 'put',
      data: data[0],
    }),
  },
  fields: [
    { name: 'searchName', type: 'string', label: '名称', required: true },
    {
      name: 'defaultFlag',
      type: 'boolean',
      label: '默认',
      defaultValue: 0,
      trueValue: 1,
      falseValue: 0,
    },
    { name: 'remark', type: 'string', label: '描述' },
    { name: 'userId', type: 'number', defaultValue: userId },
    { name: 'searchId', type: 'number' },
    { name: 'searchCode', type: 'string', defaultValue: searchCode },
  ],
});
