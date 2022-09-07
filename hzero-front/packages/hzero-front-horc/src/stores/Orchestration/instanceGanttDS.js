import { HZERO_HORC } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import { tableDS } from '@/stores/Orchestration/taskInstanceDS';

const organizationId = getCurrentOrganizationId();
const level = isTenantRoleLevel() ? `/${organizationId}` : '';

const instanceGanttDS = () => ({
  autoQuery: false,
  selection: false,
  paging: false,
  fields: tableDS().fields,
  transport: {
    read: (config) => {
      const { data, params } = config;
      return {
        url: `${HZERO_HORC}/v1${level}/orch-instances/gantt`,
        params: {
          ...data,
          ...params,
        },
        method: 'GET',
      };
    },
  },
});

export { instanceGanttDS };
