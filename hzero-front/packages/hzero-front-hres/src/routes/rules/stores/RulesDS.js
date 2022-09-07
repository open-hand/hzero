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
  // paging: false,
  selection: 'multiple',
  exportUrl: `${HZERO_HRES}/v1/${getCurrentOrganizationId()}/rule/config/export?tenantId=${getCurrentOrganizationId()}`,
  transport: {
    read: () => ({
      url: `${HZERO_HRES}/v1/${getCurrentOrganizationId()}/rule`,
      // params: { ...params, ...data },
      method: 'GET',
    }),
    destroy: () => ({
      url: `${HZERO_HRES}/v1/${getCurrentOrganizationId()}/rule`,
      method: 'DELETE',
    }),
    submit: `${HZERO_HRES}/v1/${getCurrentOrganizationId()}/rule`,
    update: () => ({
      url: `${HZERO_HRES}/v1/${getCurrentOrganizationId()}/rule`,
      method: 'PUT',
    }),
  },
  primaryKey: 'ruleCode',
  dataKey: 'content',
  fields: [
    {
      name: 'ruleCode',
      type: 'string',
      label: intl.get('hres.rule.model.rule.ruleCode').d('规则编码'),
      unique: true,
      required: true,
    },
    { name: 'tenantId', type: 'number' },
    {
      name: 'ruleName',
      type: 'string',
      label: intl.get('hres.rule.model.rule.ruleName').d('规则名称'),
      required: true,
      dynamicProps: ({ record }) => ({
        readOnly: record.get('frozenFlag') === 'Y',
      }),
    },
    {
      name: 'enableFlag',
      type: 'boolean',
      label: intl.get('hzero.common.button.enable').d('启用'),
      trueValue: 'Y',
      falseValue: 'N',
      dynamicProps: ({ record }) => ({
        readOnly: record.get('frozenFlag') === 'Y',
      }),
    },
    {
      name: 'frozenFlag',
      type: 'boolean',
      label: intl.get('hres.rule.model.rule.frozen').d('冻结'),
      trueValue: 'Y',
      falseValue: 'N',
    },
  ],
  queryFields: [
    {
      name: 'ruleCode',
      type: 'string',
      label: intl.get('hres.rule.model.rule.ruleCode').d('规则编码'),
    },
    {
      name: 'ruleName',
      type: 'string',
      label: intl.get('hres.rule.model.rule.ruleName').d('规则名称'),
    },
  ],
});
