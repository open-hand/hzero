/**
 * @author WJC <jiacheng.wang@hand-china.com>
 * @creationDate 2019/12/18
 * @copyright 2019 ® HAND
 */

import intl from 'utils/intl';
import { HZERO_INVOICE } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';

const tenantId = getCurrentOrganizationId();
const isTenant = isTenantRoleLevel();
export const apiConfigDS = () => ({
  rowKey: 'apiConfigId',
  selection: false,
  autoQuery: true,
  queryFields: [
    !isTenant && {
      name: 'tenantLov',
      lovCode: 'HPFM.TENANT',
      type: 'object',
      label: intl.get('hivc.apiConfig.model.apiConfig.tenantId').d('租户'),
      ignore: 'always',
      noCache: true,
    },
    !isTenant && {
      name: 'tenantId',
      type: 'string',
      bind: 'tenantLov.tenantId',
    },
    {
      label: intl.get('hivc.apiConfig.model.apiConfig.apiType').d('发票接口类型'),
      name: 'apiType',
      type: 'string',
      lookupCode: 'HIVC.API_TYPE',
    },
  ].filter(Boolean),
  fields: [
    {
      name: 'tenantLov',
      lovCode: 'HPFM.TENANT',
      type: 'object',
      label: intl.get('hivc.apiConfig.model.apiConfig.tenantId').d('租户'),
      ignore: 'always',
      required: true,
    },
    {
      name: 'tenantId',
      type: 'string',
      bind: 'tenantLov.tenantId',
    },
    {
      label: intl.get('hivc.apiConfig.model.apiConfig.apiType').d('发票接口类型'),
      name: 'apiTypeMeaning',
      type: 'string',
    },
    {
      label: intl.get('hivc.apiConfig.model.apiConfig.combineFlag').d('组合回调'),
      name: 'combineFlag',
      type: 'number',
    },
    {
      label: intl.get('hivc.apiConfig.model.apiConfig.defaultFlag').d('默认调用'),
      name: 'defaultFlag',
      type: 'string',
    },
  ],
  transport: {
    read: (config) => ({
      ...config,
      method: 'GET',
      url: isTenant
        ? `${HZERO_INVOICE}/v1/${tenantId}/api-configs`
        : `${HZERO_INVOICE}/v1/api-configs`,
    }),
    destroy({ data }) {
      const { __id, _status, ...other } = Array.isArray(data) ? data[0] : {};
      return {
        data: other,
        method: 'DELETE',
        url: isTenant
          ? `${HZERO_INVOICE}/v1/${tenantId}/api-configs`
          : `${HZERO_INVOICE}/v1/api-configs`,
      };
    },
  },
});
