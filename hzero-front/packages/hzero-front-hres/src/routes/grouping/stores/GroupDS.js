/*
 * source - 分组编辑 DataSet
 * @author: YXY <xinyu.ye@hand-china.com>
 * @date: 2019-10-16
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import { getCurrentOrganizationId } from 'utils/utils';
import { HZERO_HRES, HZERO_PLATFORM } from 'utils/config';
import intl from 'utils/intl';
import { getNodeConfig } from '@/utils/saveNode';

function submitRequest({ dataSet, data }) {
  const nodeInfo = getNodeConfig(
    {
      ...data[0],
      _status: dataSet.queryParameter.groupComponentName ? 'update' : 'create',
      id: dataSet.queryParameter.id,
    },
    'group',
    'groupComponentName'
  );
  const newData = data.filter(val => val.groupFlag === 'Y' || val.aggregationType);
  const submitDataArr = newData.length > 0 ? newData : [data[0]];
  const dataList = submitDataArr.map(val => ({
    ...val,
    tenantId: getCurrentOrganizationId(),
    fieldName: val.fieldName || val.fullName,
    _status: val.fieldName ? val._status : 'create',
  }));
  return {
    url: `${HZERO_HRES}/v1/${getCurrentOrganizationId()}/group-component/submit`,
    data: { groupComponentList: dataList, processNode: nodeInfo },
  };
}

export default () => ({
  autoQuery: false,
  selection: false,
  paging: false,
  transport: {
    read: ({ data, params }) => ({
      url: data.groupComponentName
        ? `${HZERO_HRES}/v1/${getCurrentOrganizationId()}/group-component`
        : `${HZERO_HRES}/v1/${getCurrentOrganizationId()}/variable`,
      method: 'GET',
      params: {
        tenantId: getCurrentOrganizationId(),
        ...params,
        ...data,
      },
    }),
    submit: submitRequest,
  },
  fields: [
    {
      name: 'groupComponentName',
      type: 'string',
      required: true,
      pattern: /[\u4e00-\u9fa5]|[a-z0-9A-Z\-_]/,
      label: intl.get('hres.grouping.model.grouping.groupComponentName').d('分组名称'),
    },
    {
      name: 'id',
      type: 'string',
    },
    {
      name: 'ten',
      type: 'tenantId',
    },
    {
      name: 'fieldName',
      type: 'string',
      label: intl.get('hres.common.model.hres.name').d('名称'),
    },
    {
      name: 'fullName',
      type: 'string',
      label: intl.get('hres.common.model.hres.name').d('名称'),
    },
    {
      name: 'fieldType',
      type: 'string',
      label: intl.get('hres.grouping.model.grouping.fieldType').d('类型'),
    },
    {
      name: 'groupFlag',
      type: 'boolean',
      label: intl.get('hres.grouping.model.grouping.groupFlag').d('分组字段'),
      trueValue: 'Y',
      falseValue: 'N',
    },
    {
      name: 'aggregationType',
      type: 'string',
      label: intl.get('hres.grouping.model.grouping.aggregationType').d('聚合方式'),
      lookupCode: 'HRES.AGGREGATION_METHOD',
      lookupUrl: code =>
        `${HZERO_PLATFORM}/v1/lovs/data?tenantId=${getCurrentOrganizationId()}&lovCode=${code}`,
    },
  ],
});
