import intl from 'utils/intl';
import { HZERO_HITF } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';

const organizationId = getCurrentOrganizationId();
const levelUrl = isTenantRoleLevel() ? `/${organizationId}` : '';

export default () => ({
  transport: {
    read: ({ data, params }) => ({
      url: `${HZERO_HITF}/v1${levelUrl}/import-historys`,
      params: { ...data, ...params },
      method: 'get',
    }),
  },
  autoQuery: true,
  pageSize: 10,
  selection: false,
  fields: [
    {
      name: 'tenantName',
      label: intl.get('hitf.importHistory.model.importHistory.tenant').d('所属租户'),
      type: 'string',
    },
    {
      name: 'requestNum',
      label: intl.get('hitf.importHistory.model.importHistory.requestNum').d('请求编号'),
      type: 'string',
    },
    {
      name: 'importUser',
      label: intl.get('hitf.importHistory.model.importHistory.importUser').d('导入人'),
      type: 'string',
    },
    {
      name: 'importUrl',
      label: intl.get('hitf.importHistory.model.importHistory.importUrl').d('导入地址'),
      type: 'string',
    },
    {
      name: 'importStatus',
      label: intl.get('hitf.importHistory.model.importHistory.importStatus').d('导入状态'),
      type: 'string',
      lookupCode: 'HITF.IMPORT_STATUS',
    },
    {
      name: 'importMessage',
      label: intl.get('hitf.importHistory.model.importHistory.importMessage').d('导入消息'),
      type: 'string',
    },
  ],
  queryFields: [
    !isTenantRoleLevel() && {
      name: 'tenantLov',
      label: intl.get('hitf.importHistory.model.importHistory.tenant').d('所属租户'),
      type: 'object',
      lovCode: 'HPFM.TENANT',
      ignore: 'always',
    },
    !isTenantRoleLevel() && {
      name: 'tenantId',
      label: intl.get('hitf.importHistory.model.importHistory.tenant').d('所属租户'),
      type: 'number',
      bind: 'tenantLov.tenantId',
    },
    {
      name: 'requestNum',
      label: intl.get('hitf.importHistory.model.importHistory.requestNum').d('请求编号'),
      type: 'string',
    },
    {
      name: 'importUser',
      label: intl.get('hitf.importHistory.model.importHistory.importUser').d('导入人'),
      type: 'string',
    },
    {
      name: 'importUrl',
      label: intl.get('hitf.importHistory.model.importHistory.importUrl').d('导入地址'),
      type: 'string',
    },
    {
      name: 'importStatus',
      label: intl.get('hitf.importHistory.model.importHistory.importStatus').d('导入状态'),
      type: 'string',
      lookupCode: 'HITF.IMPORT_STATUS',
    },
  ].filter(Boolean),
});
