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
  autoQuery: true,
  pageSize: 10,
  selection: false,
  primaryKey: 'uuid',
  queryUrl: `${HZERO_HRES}/v1/${getCurrentOrganizationId()}/history?tenantId=${getCurrentOrganizationId()}`,
  fields: [
    {
      name: 'uuid',
      type: 'string',
      label: '',
      unique: true,
    },
    {
      name: 'ruleCode',
      type: 'string',
      label: intl.get('hres.record.model.record.ruleCode').d('规则编码'),
      unique: true,
      required: true,
    },
    {
      name: 'ruleName',
      type: 'string',
      label: intl.get('hres.record.model.record.ruleName').d('规则名称'),
      required: true,
    },
    {
      name: 'inParameter',
      type: 'string',
      label: intl.get('hres.record.model.record.inParameter').d('入参JSON'),
      required: true,
    },
    {
      name: 'outParameter',
      type: 'string',
      label: intl.get('hres.record.model.record.outParameter').d('出参JSON'),
      required: true,
    },
    {
      name: 'variable',
      type: 'string',
      label: intl.get('hres.record.model.record.variable').d('变量JSON'),
      required: true,
    },
    {
      name: 'status',
      type: 'string',
      label: intl.get('hres.record.model.record.status').d('执行状态'),
      required: true,
      lookupCode: 'HRES.PROCESS_STATUS',
    },
    {
      name: 'message',
      type: 'string',
      label: intl.get('hres.record.model.record.message').d('执行错误消息'),
      required: true,
    },
    {
      name: 'startDatetime',
      type: 'dateTime',
      label: intl.get('hres.record.model.record.startDatetime').d('执行开始时间'),
      required: true,
    },
    {
      name: 'endDatetime',
      type: 'dateTime',
      label: intl.get('hres.record.model.record.endDatetime').d('执行结束时间'),
      required: true,
    },
    {
      name: 'uuid',
      type: 'string',
      label: intl.get('hres.record.model.record.uuid').d('执行ID'),
      required: true,
    },
  ],
  queryFields: [
    {
      name: 'ruleCode',
      type: 'string',
      label: intl.get('hres.record.model.record.ruleCode').d('规则编码'),
    },
    {
      name: 'ruleName',
      type: 'string',
      label: intl.get('hres.record.model.record.ruleName').d('规则名称'),
    },
  ],
  transport: {
    read: () => ({
      url: `${HZERO_HRES}/v1/${getCurrentOrganizationId()}/history?tenantId=${getCurrentOrganizationId()}`,
      method: 'GET',
    }),
  },
});
