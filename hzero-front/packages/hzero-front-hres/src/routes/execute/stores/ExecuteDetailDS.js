/*
 * source - 执行记录 DataSet
 * @author: NJQ <jiangqi.nan@hand-china.com>
 * @date: 2019-10-23
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import intl from 'utils/intl';
import { getCurrentOrganizationId } from 'utils/utils';
import { HZERO_HRES } from 'utils/config';

export default () => ({
  autoQuery: false,
  paging: false,
  selection: false,
  transport: {
    read: ({ data, params }) => ({
      url: `${HZERO_HRES}/v1/${getCurrentOrganizationId()}/history-detail?tenantId=${getCurrentOrganizationId()}`,
      method: 'GET',
      params: { ...data, ...params },
    }),
  },
  primaryKey: 'ruleCode',
  fields: [
    {
      name: 'ruleCode',
      type: 'string',
      label: '',
      unique: true,
    },
    {
      name: 'lineId',
      type: 'string',
      label: intl.get('hres.record.model.record.lineId').d('行id'),
    },
    {
      name: 'ruleName',
      type: 'string',
      label: intl.get('hres.record.model.record.ruleName').d('规则名称'),
    },
    {
      name: 'componentName',
      type: 'string',
      label: intl.get('hres.record.model.component.name').d('组件名称'),
    },
    {
      name: 'componentTypeDesc',
      type: 'string',
      label: intl.get('hres.record.model.component.type').d('组件类型'),
    },
    {
      name: 'status',
      type: 'string',
      label: intl.get('hres.record.model.record.status').d('执行状态'),
      lookupCode: 'HRES.PROCESS_STATUS',
    },
    {
      name: 'message',
      type: 'string',
      label: intl.get('hres.record.model.record.message').d('执行错误消息'),
    },
    {
      name: 'startDatetime',
      type: 'dateTime',
      label: intl.get('hres.record.model.record.startDatetime').d('执行开始时间'),
    },
    {
      name: 'endDatetime',
      type: 'dateTime',
      label: intl.get('hres.record.model.record.endDatetime').d('执行结束时间'),
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
  ],
  queryFields: [
    {
      name: 'filterErrorFlag',
      type: 'boolean',
      trueValue: 1,
      falseValue: 0,
      defaultValue: 1,
      label: intl.get('hres.rule.view.title.component.filter.failure').d('筛选失败记录'),
    },
  ],
});
