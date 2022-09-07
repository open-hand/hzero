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
    url: `${HZERO_HRES}/v1/${getCurrentOrganizationId()}/mapping-component-inparameter`,
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
      url: `${HZERO_HRES}/v1/${getCurrentOrganizationId()}/mapping-component-inparameter`,
      method: 'DELETE',
    }),
  },
  fields: [
    {
      name: 'inparameterNameLov',
      type: 'object',
      label: intl.get('hres.common.model.hres.name').d('名称'),
      lovCode: 'HRES.VARIABLE',
      required: true,
      ignore: 'always',
      lovPara: {
        tenantId: getCurrentOrganizationId(),
      },
    },
    {
      name: 'inparameterName',
      type: 'string',
      bind: 'inparameterNameLov.fullName',
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
