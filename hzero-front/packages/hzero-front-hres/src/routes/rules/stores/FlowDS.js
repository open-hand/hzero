/**
 * 流程图 - 单例DS
 * @Author: wangke <ke.wang05@hand-china.com>
 * @Date: 2019-10-21
 * @LastEditTime: 2019-10-31 11:48
 * @Copyright: Copyright (c) 2018, Hand
 */
import { HZERO_HRES } from 'utils/config';
import { getCurrentOrganizationId } from 'utils/utils';
import { DataSet } from 'choerodon-ui/pro';

function queryRequest({ data, params }) {
  return {
    url: `${HZERO_HRES}/v1/${getCurrentOrganizationId()}/process/detail`,
    method: 'GET',
    params: { ...data, ...params },
  };
}

function submitRequest({ data }) {
  return {
    url: `${HZERO_HRES}/v1/${getCurrentOrganizationId()}/process/submit`,
    data: { ...data[0], edges: data[0].edges || [] },
  };
}

const FlowDS = {
  paging: false,
  autoQuery: false,
  primaryKey: 'id',
  transport: {
    read: queryRequest,
    submit: submitRequest,
  },
  fields: [
    { name: 'flowData', type: 'object', ignore: 'always' },
    { name: 'nodes', type: 'object' },
    { name: 'edges', type: 'object', defaultValue: [] },
    { name: 'ruleCode', type: 'string', required: true },
    { name: 'tenantId', type: 'number', required: true },
    { name: 'isChange', type: 'boolean', defaultValue: false, ignore: 'always' },
  ],
};

const flowDS = new DataSet(FlowDS);

export default flowDS;
