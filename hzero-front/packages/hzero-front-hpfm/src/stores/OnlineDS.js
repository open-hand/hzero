/**
 *
 * @date: 2019-10-16
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import intl from 'utils/intl';
import { HZERO_PLATFORM } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import uuid from 'uuid/v4';

const organizationId = getCurrentOrganizationId();

const listDS = () => ({
  primaryKey: uuid(),
  dataKey: 'content',
  selection: false,
  autoQuery: true,
  queryFields: [
    !isTenantRoleLevel() && {
      name: 'tenantId',
      type: 'object',
      label: intl.get('hpfm.online.model.online.tenantName').d('当前租户'),
      lovCode: 'HPFM.TENANT',
      noCache: true,
    },
  ].filter(Boolean),
  fields: [
    {
      name: 'loginName',
      type: 'string',
      label: intl.get('hpfm.online.model.online.loginName').d('登录名'),
    },
    {
      name: 'tenantName',
      type: 'string',
      label: intl.get('hpfm.online.model.online.tenantName').d('当前租户'),
    },
    {
      name: 'organizationName',
      type: 'string',
      label: intl.get('hpfm.online.model.online.organizationName').d('所属租户'),
    },
    {
      name: 'loginDate',
      type: 'dateTime',
      label: intl.get('hpfm.online.model.online.loginDate').d('登陆时间'),
    },
    {
      name: 'loginIp',
      type: 'string',
      label: intl.get('hpfm.online.model.online.loginIp').d('登录地址'),
    },
    {
      name: 'phone',
      type: 'string',
      label: intl.get('hpfm.online.model.online.phone').d('电话'),
    },
    {
      name: 'email',
      type: 'string',
      label: intl.get('hpfm.online.model.online.email').d('邮箱'),
    },
  ],
  transport: {
    read: (config) => {
      const { data } = config;
      const { tenantId } = data;
      return {
        ...config,
        method: 'get',
        url: `${HZERO_PLATFORM}/v1${isTenantRoleLevel() ? `/${organizationId}/` : `/`}online-users`,
        data: {
          ...data,
          tenantId: tenantId && tenantId.tenantId,
        },
      };
    },
  },
});

export { listDS };
