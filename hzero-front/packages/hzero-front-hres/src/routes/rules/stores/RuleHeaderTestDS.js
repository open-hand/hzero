/*
 * source - 结算规则 DataSet
 * @author: NJQ <jiangqi.nan@hand-china.com>
 * @date: 2019-10-11
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import intl from 'utils/intl';
import { HZERO_HRES } from 'utils/config';
import { getCurrentOrganizationId } from 'utils/utils';

export default () => ({
  autoQuery: true,
  paging: false,
  selection: false,
  transport: {
    read: ({ data, params }) => ({
      url: `${HZERO_HRES}/v1/${getCurrentOrganizationId()}/rule/detail`,
      method: 'GET',
      params: { ...params, ...data },
    }),
  },
  primaryKey: 'ruleCode',
  fields: [
    {
      name: 'ruleCode',
      type: 'string',
      label: intl.get('hres.rule.model.rule.ruleCode').d('规则编码'),
      unique: true,
      required: true,
    },
    {
      name: 'ruleName',
      type: 'string',
      label: intl.get('hres.rule.model.rule.ruleName').d('规则名称'),
      required: true,
    },
    {
      name: 'serviceRouteLov',
      type: 'object',
      label: intl.get('hres.rule.model.rule.serviceRoute').d('服务路由'),
      lovCode: 'HRES.SERVICE_ROUTE',
      required: true,
      ignore: 'always',
    },
    {
      name: 'serviceCode',
      type: 'string',
      label: intl.get('hres.rule.model.rule.serviceRoute').d('服务路由'),
      bind: 'serviceRouteLov.serviceCode',
      required: true,
      ignore: 'never',
    },
    { name: 'jsonInput', type: 'string' },
    { name: 'runId', type: 'string' },
    { name: 'outParam', type: 'string' },
    { name: 'errorMessage', type: 'string' },
    { name: 'status', type: 'string', lookupCode: 'HRES.PROCESS_STATUS' },
  ],
});
