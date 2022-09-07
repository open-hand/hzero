/**
 * 公式 - 公式DataSet
 * @Author: wangke <ke.wang05@hand-china.com>
 * @Date: 2019-10-14
 * @LastEditTime: 2019-10-23 13:07
 * @Copyright: Copyright (c) 2018, Hand
 */
import { HZERO_HRES } from 'utils/config';
import { getCurrentOrganizationId } from 'utils/utils';
import intl from 'utils/intl';
import { getNodeConfig } from '@/utils/saveNode';

function queryRequest({ data, params }) {
  return {
    url: `${HZERO_HRES}/v1/${getCurrentOrganizationId()}/formula-component/detail`,
    method: 'GET',
    params: { ...data, ...params },
  };
}

function submitRequest({ dataSet, data }) {
  const nodeInfo = getNodeConfig(
    { ...data[0], id: dataSet.current.get('id') },
    'formula',
    'formulaName'
  );
  return {
    url: `${HZERO_HRES}/v1/${getCurrentOrganizationId()}/formula-component/submit`,
    data: { formulaComponentList: data, processNode: nodeInfo },
  };
}

export default () => ({
  paging: false,
  autoQuery: false,
  primaryKey: 'id',
  queryParameter: { tenantId: getCurrentOrganizationId() },
  transport: {
    read: queryRequest,
    submit: submitRequest,
  },
  fields: [
    {
      name: 'id',
      type: 'string',
      ignore: 'always',
    },
    {
      name: 'formulaName',
      type: 'string',
      pattern: /[\u4e00-\u9fa5]|[a-z0-9A-Z\-_]/,
      label: intl.get('hres.flow.model.flow.formulaName').d('公式名称'),
      required: true,
    },
    {
      name: 'formulaType',
      type: 'string',
      label: intl.get('hres.flow.model.flow.formulaType').d('公式类型'),
      lookupCode: 'HRES.FIELD_TYPE',
      required: true,
    },
    {
      name: 'formulaText',
      type: 'string',
      label: intl.get('hres.flow.model.flow.formulaContent').d('公式内容'),
      required: true,
    },
    { name: 'batchFlag', type: 'string' },
    { name: 'ruleCode', type: 'string' },
    { name: 'tenantId', type: 'number' },
  ],
});
