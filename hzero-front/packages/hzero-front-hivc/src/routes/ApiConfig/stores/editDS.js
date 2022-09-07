/**
 * @author WJC <jiacheng.wang@hand-china.com>
 * @creationDate 2019/12/18
 * @copyright 2019 ® HAND
 */

import intl from 'utils/intl';
import { HZERO_INVOICE, HZERO_PLATFORM } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';

const tenantId = getCurrentOrganizationId();
const isTenant = isTenantRoleLevel();
export const headerDS = apiConfigId => ({
  selection: false,
  autoQueryAfterSubmit: false,
  autoQuery: false,
  fields: [
    !isTenant && {
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
      name: 'tenantName',
      type: 'string',
      bind: 'tenantLov.tenantName',
    },
    {
      label: intl.get('hivc.apiConfig.model.apiConfig.apiType').d('发票接口类型'),
      name: 'apiType',
      type: 'string',
      lookupCode: 'HIVC.API_TYPE',
      required: true,
    },
    {
      label: intl.get('hivc.apiConfig.model.apiConfig.combineFlag').d('组合回调'),
      name: 'combineFlag',
      type: 'boolean',
      trueValue: 1,
      falseValue: 0,
      defaultValue: 0,
    },
    {
      label: intl.get('hivc.apiConfig.model.apiConfig.defaultFlag').d('默认调用'),
      name: 'defaultFlag',
      type: 'boolean',
      trueValue: 1,
      falseValue: 0,
      defaultValue: 0,
    },
  ].filter(Boolean),
  transport: {
    read(config) {
      return {
        ...config,
        params: {},
        method: 'GET',
        url: isTenant
          ? `${HZERO_INVOICE}/v1/${tenantId}/api-configs/${apiConfigId}`
          : `${HZERO_INVOICE}/v1/api-configs/${apiConfigId}`,
      };
    },
    create(config) {
      const { data } = config;
      const { __id, _status, ...other } = Array.isArray(data) ? data[0] : {};
      return {
        ...config,
        data: other,
        method: 'POST',
        url: isTenant
          ? `${HZERO_INVOICE}/v1/${tenantId}/api-configs`
          : `${HZERO_INVOICE}/v1/api-configs`,
      };
    },
    update(config) {
      const { data } = config;
      const { __id, _status, ...other } = Array.isArray(data) ? data[0] : {};
      return {
        ...config,
        data: other,
        method: 'PUT',
        url: isTenant
          ? `${HZERO_INVOICE}/v1/${tenantId}/api-configs`
          : `${HZERO_INVOICE}/v1/api-configs`,
      };
    },
  },
});

export const lineDS = (apiConfigId, hDS) => ({
  rowKey: 'apiConfigLineId',
  selection: false,
  autoQuery: apiConfigId !== 'create',
  fields: [
    {
      label: intl.get('hivc.apiConfig.model.apiConfig.orderSeq').d('调用顺序'),
      name: 'orderSeq',
      type: 'number',
      required: true,
    },
    {
      label: intl.get('hivc.apiConfig.model.apiConfig.serverCode').d('组合服务'),
      name: 'serverCode',
      type: 'string',
      lookupCode: 'HITF.COMPOSE_SERVER',
      valueField: 'serverCode',
      textField: 'serverName',
      required: true,
    },
    {
      label: intl.get('hivc.apiConfig.model.apiConfig.interfaceCode').d('组合接口'),
      name: 'interfaceCode',
      type: 'string',
      valueField: 'interfaceCode',
      textField: 'interfaceName',
      required: true,
      dynamicProps: {
        lookupAxiosConfig: ({ record }) => {
          if (record.get('serverCode')) {
            return {
              url: `${HZERO_PLATFORM}/v1/lovs/data`,
              params: {
                lovCode: 'HITF.COMPOSE_INTERFACE',
                serverCode: record.get('serverCode'),
              },
            };
          }
        },
      },
    },
  ],
  transport: {
    read(config) {
      return {
        ...config,
        method: 'GET',
        url: isTenant
          ? `${HZERO_INVOICE}/v1/${tenantId}/api-config-lines/${apiConfigId}`
          : `${HZERO_INVOICE}/v1/api-config-lines/${apiConfigId}`,
      };
    },
    create(config) {
      const { data } = config;
      const {
        current: { data: { tenantId: headerTenantId, apiConfigId: headerApiConfigId } } = {},
      } = hDS;
      const { __id, _status, ...other } = Array.isArray(data) ? data[0] : {};
      return {
        ...config,
        data: { ...other, tenantId: headerTenantId, apiConfigId: headerApiConfigId },
        method: 'POST',
        url: isTenant
          ? `${HZERO_INVOICE}/v1/${tenantId}/api-config-lines`
          : `${HZERO_INVOICE}/v1/api-config-lines`,
      };
    },
    update(config) {
      const { data } = config;
      const {
        current: { data: { tenantId: headerTenantId, apiConfigId: headerApiConfigId } } = {},
      } = hDS;
      const { __id, _status, ...other } = Array.isArray(data) ? data[0] : {};
      return {
        ...config,
        data: { ...other, tenantId: headerTenantId, apiConfigId: headerApiConfigId },
        method: 'PUT',
        url: isTenant
          ? `${HZERO_INVOICE}/v1/${tenantId}/api-config-lines`
          : `${HZERO_INVOICE}/v1/api-config-lines`,
      };
    },
    destroy({ data }) {
      const { __id, _status, ...other } = Array.isArray(data) ? data[0] : {};
      return {
        data: other,
        method: 'DELETE',
        url: isTenant
          ? `${HZERO_INVOICE}/v1/${tenantId}/api-config-lines`
          : `${HZERO_INVOICE}/v1/api-config-lines`,
      };
    },
  },
});
