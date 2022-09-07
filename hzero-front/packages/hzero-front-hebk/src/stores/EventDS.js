import { HZERO_HEBK } from 'utils/config';
import { getCurrentOrganizationId } from 'utils/utils';
import intl from 'utils/intl';

const organizationId = getCurrentOrganizationId();

const tableDs = () => ({
  selection: false,
  autoQuery: true,
  queryFields: [
    {
      name: 'serviceName',
      type: 'string',
      label: intl.get('hebk.event.model.event.serviceName').d('服务名称'),
    },
    {
      name: 'code',
      type: 'string',
      label: intl.get('hebk.event.model.event.code').d('API操作代码'),
    },
    {
      name: 'requestNumber',
      type: 'string',
      label: intl.get('hebk.event.model.event.requestNumber').d('请求流水号'),
    },
    {
      name: 'timeFrom',
      type: 'dateTime',
      label: intl.get('hebk.event.model.event.timeFrom').d('发生时间从'),
    },
    {
      name: 'timeTo',
      type: 'dateTime',
      label: intl.get('hebk.event.model.event.timeTo').d('发生时间至'),
    },
    {
      name: 'referencedResources',
      type: 'string',
      label: intl.get('hebk.event.model.event.referencedResources').d('API资源列表'),
    },
  ],
  fields: [
    {
      name: 'apiVersion',
      type: 'string',
      label: intl.get('hebk.event.model.event.apiVersion').d('API版本'),
    },
    {
      name: 'serviceName',
      type: 'string',
      label: intl.get('hebk.event.model.event.serviceName').d('服务名称'),
    },
    {
      name: 'code',
      type: 'string',
      label: intl.get('hebk.event.model.event.code').d('API操作代码'),
    },
    {
      name: 'name',
      type: 'string',
      label: intl.get('hebk.event.model.event.name').d('API操作名称'),
    },
    {
      name: 'source',
      type: 'string',
      label: intl.get('hebk.event.model.event.source').d('服务端'),
    },
    {
      name: 'time',
      type: 'string',
      label: intl.get('hebk.event.model.event.time').d('发生时间'),
    },
    {
      name: 'requestNumber',
      type: 'string',
      label: intl.get('hebk.event.model.event.requestNumber').d('请求流水号'),
    },
    {
      name: 'requestNumber',
      type: 'string',
      label: intl.get('hebk.event.model.event.requestNumber').d('请求流水号'),
    },
    {
      name: 'errorCode',
      type: 'string',
      label: intl.get('hebk.event.model.event.errorCode').d('错误码'),
    },
  ],
  transport: {
    read: () => {
      const url = `${HZERO_HEBK}/v1/${organizationId}/events`;
      return {
        url,
        method: 'GET',
      };
    },
  },
});

const detailDs = () => ({
  autoQueryAfterSubmit: false,
  fields: [
    {
      name: 'apiVersion',
      type: 'string',
      label: intl.get('hebk.event.model.event.apiVersion').d('API版本'),
    },
    {
      name: 'serviceName',
      type: 'string',
      label: intl.get('hebk.event.model.event.serviceName').d('服务名称'),
    },
    {
      name: 'code',
      type: 'string',
      label: intl.get('hebk.event.model.event.code').d('API操作代码'),
    },
    {
      name: 'name',
      type: 'string',
      label: intl.get('hebk.event.model.event.name').d('API操作名称'),
    },
    {
      name: 'source',
      type: 'string',
      label: intl.get('hebk.event.model.event.source').d('服务端'),
    },
    {
      name: 'time',
      type: 'string',
      label: intl.get('hebk.event.model.event.time').d('发生时间'),
    },
    {
      name: 'requestNumber',
      type: 'string',
      label: intl.get('hebk.event.model.event.requestNumber').d('请求流水号'),
    },
    {
      name: 'requestParameters',
      type: 'string',
      label: intl.get('hebk.event.model.event.requestParameters').d('API输入参数'),
    },
    {
      name: 'responseElements',
      type: 'string',
      label: intl.get('hebk.event.model.event.responseElements').d('API响应数据'),
    },
    {
      name: 'errorCode',
      type: 'string',
      label: intl.get('hebk.event.model.event.errorCode').d('错误码'),
    },
    {
      name: 'errorMessage',
      type: 'string',
      label: intl.get('hebk.event.model.event.enabledFlag').d('错误信息'),
    },
    {
      name: 'referencedResources',
      type: 'string',
      label: intl.get('hebk.event.model.event.referencedResources').d('API资源列表'),
    },
  ],
  transport: {
    read: ({ dataSet }) => {
      const { eventId } = dataSet;
      const url = `${HZERO_HEBK}/v1/${organizationId}/events/${eventId}`;
      return {
        url,
        method: 'GET',
        params: '',
      };
    },
  },
});

export { tableDs, detailDs };
