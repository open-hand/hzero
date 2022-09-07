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
import { getNodeConfig } from '@/utils/saveNode';

function submitRequest({ dataSet, data }) {
  const nodeInfo = getNodeConfig(
    { ...data[0], id: dataSet.current.get('id') },
    'mapping',
    'mappingName'
  );
  return {
    url: `${HZERO_HRES}/v1/${getCurrentOrganizationId()}/mapping-component/submit`,
    data: { mappingComponentList: data, processNode: nodeInfo },
  };
}

function queryRequest({ data, params }) {
  return {
    url: `${HZERO_HRES}/v1/${getCurrentOrganizationId()}/mapping-component/detail`,
    method: 'GET',
    params: { ...data, ...params },
  };
}

export default () => ({
  autoQuery: false,
  paging: false,
  selection: false,
  primaryKey: 'mappingName',
  transport: {
    read: queryRequest,
    submit: submitRequest,
  },
  fields: [
    {
      name: 'mappingName',
      type: 'string',
      label: intl.get('hres.mapping.model.mapping.mappingName').d('映射名称'),
      ignore: 'never',
      required: true,
      pattern: /[\u4e00-\u9fa5]|[a-z0-9A-Z\-_]/,
    },
    {
      name: 'viewNameLov',
      type: 'object',
      label: intl.get('hres.mapping.model.mapping.lovName').d('值集名称'),
      ignore: 'always',
      lovCode: 'HRES.LOV_VIEW',
      required: true,
    },
    {
      name: 'lovViewName',
      type: 'string',
      label: intl.get('hres.mapping.model.mapping.lovName').d('值集名称'),
      bind: 'viewNameLov.viewName',
    },

    {
      name: 'viewCode',
      type: 'string',
      bind: 'viewNameLov.viewCode',
    },
    {
      name: 'lovViewCode',
      type: 'string',
      bind: 'viewNameLov.viewCode',
    },
    {
      name: 'lovCode',
      type: 'string',
      bind: 'viewNameLov.lovCode',
    },
    {
      name: 'frozenFlag',
      type: 'boolean',
      label: intl.get('hres.mapping.model.mapping.frozen').d('冻结'),
      trueValue: 'Y',
      falseValue: 'N',
    },
    {
      name: 'ruleCode',
      type: 'string',
      label: intl.get('hres.rule.model.rule.ruleCode').d('规则编码'),
      ignore: 'never',
    },
    { name: 'tenantId', type: 'number' },
    {
      name: 'cacheFlag',
      type: 'boolean',
      trueValue: 'Y',
      falseValue: 'N',
      label: intl.get('hres.rule.model.mapping.isCache').d('写入缓存'),
      defaultValue: 'N',
      ignore: 'never',
    },
  ],
});
