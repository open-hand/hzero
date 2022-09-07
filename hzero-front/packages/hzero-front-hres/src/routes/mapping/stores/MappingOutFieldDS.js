/**
 * mapping - 映射定义列表/头 DataSet
 * @Author: dufangjun <fangjun.du@hand-china.com>
 * @Date: 2019/10/13 10:03
 * @LastEditTime: 2019/10/15 10:03
 * @Copyright: Copyright (c) 2018, Hand
 */
import { getCurrentOrganizationId } from 'utils/utils';
import { HZERO_HRES } from 'utils/config';
import intl from 'utils/intl';

function queryRequest({ data, params }) {
  return {
    url: `${HZERO_HRES}/v1/${getCurrentOrganizationId()}/mapping-component-outparameter`,
    method: 'GET',
    params: { ...data, ...params },
  };
}
export default () => ({
  autoQuery: false,
  selection: false,
  transport: {
    read: queryRequest,
    destroy: () => ({
      url: `${HZERO_HRES}/v1/${getCurrentOrganizationId()}/mapping-component-outparameter`,
      method: 'DELETE',
    }),
  },
  fields: [
    {
      name: 'outparameterName',
      type: 'string',
      label: intl.get('hres.common.model.hres.name').d('名称'),
      required: true,
    },
    {
      name: 'columnNameLov',
      type: 'object',
      label: intl.get('hres.mapping.model.mapping.lovView').d('值集字段'),
      ignore: 'always',
      required: true,
      lovCode: 'HRES.LOV_VALUE',
      dynamicProps: ({ record }) => ({
        lovPara: {
          viewCode: record.dataSet.parent.current.get('viewCode'),
        },
      }),
    },
    {
      name: 'lovColumnName',
      type: 'string',
      bind: 'columnNameLov.display',
    },
    {
      name: 'lovColumnCode',
      type: 'string',
      bind: 'columnNameLov.fieldName',
    },
    {
      name: 'fieldType',
      type: 'string',
      label: intl.get('hres.mapping.model.mapping.type').d('字段类型'),
      required: true,
      lookupCode: 'HRES.FIELD_TYPE',
    },
    {
      name: 'maskCode',
      type: 'string',
      label: intl.get('hres.mapping.model.mapping.maskCode').d('日期掩码'),
      lookupCode: 'HRES.DATE_MASK',
      dynamicProps: ({ record }) => {
        if (record.get('fieldType') === 'DATE') {
          return {
            required: true,
          };
        }
      },
    },
    {
      name: 'mappingName',
      type: 'string',
    },
    {
      name: 'ruleCode',
      type: 'string',
      label: intl.get('hres.rule.model.rule.ruleCode').d('规则编码'),
      ignore: 'never',
    },
    { name: 'tenantId', type: 'number' },
  ],
});
