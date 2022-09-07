/*
 * source - 组件执行记录 DataSet
 * @author: NJQ <jiangqi.nan@hand-china.com>
 * @date: 2019-10-28
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import intl from 'utils/intl';
import { getCurrentOrganizationId } from 'utils/utils';
import { HZERO_HRES } from 'utils/config';

export default () => ({
  autoQuery: false,
  selection: false,
  primaryKey: 'ruleCode',
  // queryUrl: `${HZERO_HRES}/v1/${getCurrentOrganizationId()}/history-detail/detail?tenantId=${getCurrentOrganizationId()}`,
  transport: {
    read: ({ data, params }) => ({
      url: `${HZERO_HRES}/v1/${getCurrentOrganizationId()}/history-detail/detail?tenantId=${getCurrentOrganizationId()}`,
      method: 'GET',
      params: { ...data, ...params },
    }),
  },
  fields: [
    {
      name: 'componentName',
      type: 'string',
      label: intl.get('hres.record.model.record.componentName').d('组件名称'),
    },
    {
      name: 'status',
      type: 'string',
      label: intl.get('hres.record.model.record.status').d('执行状态'),
      lookupCode: 'HRES.PROCESS_STATUS',
    },
    {
      name: 'startDatetime',
      type: 'date',
      label: intl.get('hres.record.model.record.component.startDatetime').d('组件执行开始时间'),
    },
    {
      name: 'endDatetime',
      type: 'date',
      label: intl.get('hres.record.model.record.component.endDatetime').d('组件执行结束时间'),
    },
    {
      name: 'inParameter',
      type: 'string',
      label: intl.get('hres.record.model.record.inParameter').d('入参JSON'),
    },
    {
      name: 'outParameter',
      type: 'string',
      label: intl.get('hres.record.model.record.outParameter').d('出参JSON'),
    },
    {
      name: 'message',
      type: 'string',
      label: intl.get('hres.record.model.record.message').d('执行错误消息'),
    },
  ],
});
