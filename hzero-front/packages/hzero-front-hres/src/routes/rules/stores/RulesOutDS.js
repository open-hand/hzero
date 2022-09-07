/*
 * source - 结算规则-出参 DataSet
 * @author: NJQ <jiangqi.nan@hand-china.com>
 * @date: 2019-10-17
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import intl from 'utils/intl';
import { HZERO_HRES } from 'utils/config';
import { getCurrentOrganizationId } from 'utils/utils';

export default () => ({
  autoQuery: true,
  selection: false,
  transport: {
    read: ({ data, params }) => ({
      url: `${HZERO_HRES}/v1/${getCurrentOrganizationId()}/out-parameter`,
      params: { tenantId: getCurrentOrganizationId(), ...params, ...data },
      method: 'GET',
    }),
    submit: `${HZERO_HRES}/v1/${getCurrentOrganizationId()}/out-parameter/submit`,
    destroy: () => ({
      url: `${HZERO_HRES}/v1/${getCurrentOrganizationId()}/out-parameter`,
      method: 'DELETE',
    }),
  },
  primaryKey: 'fieldCode',
  fields: [
    {
      name: 'fieldCode',
      type: 'string',
      label: intl.get('hres.rule.model.rule.code').d('编码'),
      required: true,
      unique: true,
    },
    { name: 'ruleCode', type: 'string' },
    { name: 'tenantId', type: 'number' },
    {
      name: 'fieldName',
      type: 'string',
      label: intl.get('hres.common.model.hres.name').d('名称'),
      required: true,
    },
    {
      name: 'fieldType',
      type: 'string',
      label: intl.get('hres.rule.model.rule.type').d('类型'),
      required: true,
      lookupCode: 'HRES.FIELD_TYPE',
    },
    {
      name: 'formula',
      type: 'string',
      label: intl.get('hres.rule.model.rule.formula').d('公式'),
      readOnly: true,
      required: true,
    },
  ],
});
