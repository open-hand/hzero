/**
 * 流程图 - 组件节点ds
 * @Author: wangke <ke.wang05@hand-china.com>
 * @Date: 2019-10-30
 * @LastEditTime: 2019-10-30 16:57
 * @Copyright: Copyright (c) 2018, Hand
 */
import { HZERO_HRES } from 'utils/config';
import { getCurrentOrganizationId } from 'utils/utils';
import { DataSet } from 'choerodon-ui/pro';

function submitRequest({ data }) {
  return {
    url: `${HZERO_HRES}/v1/${getCurrentOrganizationId()}/process-node/submit`,
    data: data[0],
  };
}

function deleteRequest({ data }) {
  return {
    url: `${HZERO_HRES}/v1/${getCurrentOrganizationId()}/process-node`,
    method: 'DELETE',
    data: data[0],
  };
}

function queryRequest({ data, params }) {
  return {
    url: `${HZERO_HRES}/v1/${getCurrentOrganizationId()}/process-node`,
    method: 'GET',
    params: { ...data, ...params },
  };
}

const NodeDS = {
  paging: false,
  autoQuery: false,
  primaryKey: 'id',
  autoQueryAfterSubmit: false,
  transport: {
    read: queryRequest,
    submit: submitRequest,
    destroy: deleteRequest,
  },
  fields: [
    { name: 'id', type: 'string' },
    { name: 'label', type: 'string' },
    { name: 'labelOffsetY', type: 'number', defaultValue: 40 },
    { name: 'shape', type: 'string', defaultValue: 'custom-node' },
    { name: 'icon', type: 'string' },
    { name: 'type', type: 'string', defaultValue: 'node' },
    { name: 'componentName', type: 'string' },
    { name: 'componentType', type: 'string' },
    { name: 'componentTypeDesc', type: 'string' },
    { name: 'size', type: 'string', defaultValue: '80*48' },
    { name: 'x', type: 'number', defaultValue: 60 },
    { name: 'y', type: 'number', defaultValue: 170 },
    { name: 'color', type: 'string', defaultValue: '#1890FF' },
    { name: 'ruleCode', type: 'string' },
    { name: 'tenantId', type: 'number' },
    { name: 'objectVersionNumber', type: 'number' },
    { name: 'isChange', type: 'boolean', ignore: 'always' },
    { name: '_token', type: 'string' },
  ],
  feedback: {
    submitSuccess: () => {},
  },
};

const nodeDS = new DataSet(NodeDS);

export default nodeDS;
