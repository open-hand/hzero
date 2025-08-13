/**
 * @date: 2020-03-25
 * @author: HQ <qi.he@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import intl from 'utils/intl';
import { HZERO_ADM } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import { dateTimeRender } from 'utils/renderer';

const organizationId = getCurrentOrganizationId();

function tableDS() {
  return {
    autoQuery: true,
    selection: false,
    dataKey: 'content',
    queryFields: [
      {
        name: 'module',
        type: 'string',
        label: intl.get('hadm.seataMonitor.model.seataMonitor.module').d('模块'),
      },
      {
        name: 'startDate',
        label: intl.get('hadm.seataMonitor.model.seataMonitor.startDate').d('开始时间戳'),
        type: 'dateTime',
        dynamicProps: {
          max: ({ record }) => record.get('endDate'),
        },
        transformRequest: (value) => dateTimeRender(value),
      },
      {
        name: 'endDate',
        label: intl.get('hadm.seataMonitor.model.seataMonitor.endDate').d('结束时间戳'),
        type: 'dateTime',
        dynamicProps: {
          min: ({ record }) => record.get('endDate'),
        },
        transformRequest: (value) => dateTimeRender(value),
      },
      {
        name: 'labelCondition',
        type: 'string',
        label: intl.get('hadm.seataMonitor.model.seataMonitor.labelMap').d('标签'),
      },
    ],
    fields: [
      {
        name: 'namespace',
        type: 'string',
        label: intl.get('hadm.seataMonitor.model.seataMonitor.namespace').d('命名空间'),
      },
      {
        name: 'module',
        type: 'string',
        label: intl.get('hadm.seataMonitor.model.seataMonitor.module').d('模块'),
      },
      {
        name: 'labelMap',
        type: 'string',
        label: intl.get('hadm.seataMonitor.model.seataMonitor.labelMap').d('标签'),
      },
      {
        name: 'timestamp',
        type: 'string',
        label: intl.get('hadm.seataMonitor.model.seataMonitor.timestamp').d('时间戳'),
      },
      {
        name: 'value',
        type: 'string',
        label: intl.get('hadm.seataMonitor.model.seataMonitor.value').d('值'),
      },
    ],
    transport: {
      read: (config) => {
        const { data, params } = config;
        const url = isTenantRoleLevel()
          ? `${HZERO_ADM}/v1/${organizationId}/measurements`
          : `${HZERO_ADM}/v1/measurements`;
        return {
          data,
          params,
          url,
          method: 'GET',
        };
      },
      update: (config) => {
        const url = isTenantRoleLevel()
          ? `${HZERO_ADM}/v1/${organizationId}/measurements`
          : `${HZERO_ADM}/v1/measurements`;
        const { params = {}, data = [] } = config;
        return {
          data,
          params,
          url,
          method: 'PUT',
        };
      },
    },
  };
}

export { tableDS };
