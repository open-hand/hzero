/*
 * source - 结算规则-公示编辑 DataSet
 * @author: NJQ <jiangqi.nan@hand-china.com>
 * @date: 2019-10-21
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import intl from 'utils/intl';
import { HZERO_HRES } from 'utils/config';
import { getCurrentOrganizationId } from 'utils/utils';

function submitRequest({ data }) {
  const { tenantId, ruleCode, formulaText } = data[0] || [];
  return {
    url: `${HZERO_HRES}/v1/${getCurrentOrganizationId()}/rule/validate-formula`,
    params: { tenantId, ruleCode, formulaText },
  };
}

export default () => ({
  exportUrl: `${HZERO_HRES}/v1/${getCurrentOrganizationId()}/formula-component`,
  transport: {
    read: ({ data, params }) => ({
      url: `${HZERO_HRES}/v1/${getCurrentOrganizationId()}/formula-component`,
      method: 'GET',
      params: { tenantId: getCurrentOrganizationId(), ...params, ...data },
    }),
    submit: submitRequest,
  },
  fields: [
    {
      name: 'formulaText',
      type: 'string',
      label: intl.get('hres.rule.model.rule.formulaText').d('公式内容'),
      required: true,
    },
  ],
});
