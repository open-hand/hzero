/**
 * rule-component规则组件通用字段 DadaSet
 * @Author: zhangzhicen <zhicen.zhang@hand-china.com>
 * @Date: 2019/10/17 15:47
 * @LastEditTime: 2019/10/25 9:37
 * @Copyright: Copyright (c) 2018, Hand
 */
import { HZERO_HRES } from 'utils/config';
import { getCurrentOrganizationId } from 'utils/utils';
import intl from 'utils/intl';

export default () => ({
  autoQuery: false,
  selection: false,
  paging: false,
  primaryKey: 'fieldName',
  transport: {
    read: ({ data, params }) => ({
      url: `${HZERO_HRES}/v1/${getCurrentOrganizationId()}/rule-component-field`,
      method: 'GET',
      params: { ...params, ...data },
    }),
    destroy: () => ({
      url: `${HZERO_HRES}/v1/${getCurrentOrganizationId()}/rule-component-field`,
      method: 'DELETE',
    }),
  },
  fields: [
    {
      name: 'fieldName',
      type: 'string',
      label: intl.get('hres.ruleComponent.model.ruleCmp.fieldName').d('字段名称'),
    },
    {
      name: 'fieldType',
      type: 'string',
      label: intl.get('hres.ruleComponent.model.ruleCmp.fieldType').d('字段类型'),
      lookupCode: 'HRES.FIELD_TYPE',
    },
    {
      name: 'editType',
      type: 'string',
      label: intl.get('hres.ruleComponent.model.ruleCmp.editType').d('编辑类型'),
      lookupCode: 'HRES.EDIT_TYPE',
    },
    {
      name: 'businessModelName',
      type: 'string',
      label: intl.get('hres.ruleComponent.model.ruleCmp.businessModel').d('业务模型'),
    },
    {
      name: 'businessModel',
      type: 'string',
    },
    {
      name: 'valueField',
      type: 'string',
    },
    {
      name: 'formula',
      type: 'string',
      label: intl.get('hres.ruleComponent.model.ruleCmp.formula').d('公式'),
    },
    {
      name: 'isRequired',
      type: 'boolean',
      trueValue: 'Y',
      falseValue: 'N',
      label: intl.get('hres.ruleComponent.model.ruleCmp.isRequired').d('是否必输'),
      defaultValue: 'N',
    },
    {
      name: 'tenantId',
      type: 'string',
    },
    {
      name: 'ruleCode',
      type: 'string',
    },
    {
      name: 'ruleComponentName',
      type: 'string',
    },
  ],
});
