/**
 * @Author: zhengmin.liang <zhengmin.liang@hand-china.com>
 * @Create time: 2019/11/20
 * @Copyright: Copyright(c) 2019, Hand
 * @Description: 云账户配置DS
 */
import { getCurrentOrganizationId, getResponse, isTenantRoleLevel } from 'utils/utils';
import { HZERO_PLATFORM } from 'utils/config';
import intl from 'utils/intl';
import { API_PREFIX } from '@/utils/constants';

const organizationId = getCurrentOrganizationId();
const prefix = 'hiot.cloudAccount.model';
const isTenant = isTenantRoleLevel();
const apiPrefix = isTenant ? `${API_PREFIX}/v1/${organizationId}` : `${API_PREFIX}/v1`;

const cloudAccountListDS = () => ({
  autoQuery: true,
  selection: false,
  transport: {
    read: () => ({
      url: `${API_PREFIX}/v1/${organizationId}/cloud-config`,
      method: 'get',
    }),
    destroy: ({ data }) => {
      return {
        url: `${API_PREFIX}/v1/${organizationId}/cloud-config`,
        method: 'DELETE',
        data,
      };
    },
  },
  queryFields: [
    {
      name: 'configName',
      type: 'string',
      label: intl.get('hiot.common.model.common.alertName').d('配置名称'),
    },
    {
      name: 'platformInfo',
      type: 'object',
      label: intl.get('hiot.common.model.common.platform').d('云平台'),
      lovCode: 'HIOT.LOV.CLOUD_PLATFORM',
      lovPara: { tenantId: organizationId },
      noCache: true,
      ignore: 'always',
    },
    {
      name: 'platform',
      type: 'string',
      bind: 'platformInfo.value',
    },
    {
      name: 'endpoint',
      type: 'string',
      bind: 'endpointInfo.value',
    },
    {
      name: 'isReferred',
      type: 'string',
      label: intl.get(`${prefix}.use-status`).d('应用状态'),
      lookupCode: 'HIOT.REFERRED_STATUS',
    },
  ],
  fields: [
    {
      name: 'configName',
      type: 'string',
      label: intl.get('hiot.common.model.common.alertName').d('配置名称'),
    },
    {
      name: 'configCode',
      type: 'string',
      required: true,
      label: intl.get('hiot.common.model.common.alertCode').d('配置编码'),
    },
    {
      name: 'platform',
      type: 'string',
      label: intl.get('hiot.common.model.common.platform').d('云平台'),
      lookupCode: 'HIOT.CLOUD_PLATFORM',
    },
    {
      name: 'endpoint',
      type: 'string',
      label: 'Endpoint',
    },
    {
      name: 'enabledFlag',
      type: 'number',
      label: intl.get(`${prefix}.use-status`).d('应用状态'),
    },
  ],
});

const cloudAccountDetailDS = () => ({
  transport: {
    read: ({ data }) => {
      const { recordId } = data;
      return {
        url: `${API_PREFIX}/v1/${organizationId}/cloud-config/${recordId}`,
        method: 'get',
        transformResponse: (res) => {
          let formatData = {};
          try {
            formatData = JSON.parse(res);
          } catch (e) {
            return e;
          }
          if (getResponse(formatData)) {
            const { addition } = formatData;
            if (addition) {
              const other = `${addition}`;
              formatData = {
                ...formatData,
                ...JSON.parse(`${other}`),
              };
            }
          }
          return formatData;
        },
      };
    },
    create: ({ data }) => ({
      url: `${API_PREFIX}/v1/${organizationId}/cloud-config`,
      method: 'post',
      params: { isDefault: 1 },
      data: { ...data[0] },
    }),
    update: ({ data }) => {
      const { hubAddressDefault } = data;
      return {
        url: `${API_PREFIX}/v1/${organizationId}/cloud-config`,
        method: 'put',
        params: { isDefault: hubAddressDefault ? 0 : 1 },
        data: { ...data[0] },
      };
    },
  },
  fields: [
    {
      name: 'configName',
      type: 'intl',
      label: intl.get('hiot.common.model.common.alertName').d('配置名称'),
      required: true,
    },
    {
      name: 'configCode',
      type: 'string',
      required: true,
      label: intl.get('hiot.common.model.common.alertCode').d('配置编码'),
    },
    {
      name: 'platformInfo',
      type: 'object',
      label: intl.get('hiot.common.model.common.platform').d('云平台'),
      required: true,
      lovCode: 'HIOT.LOV.CLOUD_PLATFORM',
      lovPara: { tenantId: organizationId },
      ignore: 'always',
      textField: 'meaning',
    },
    {
      name: 'platform',
      type: 'string',
      bind: 'platformInfo.value',
    },
    { name: 'platformMeaning', type: 'string', bind: 'platformInfo.meaning' },
    {
      name: 'endpointInfo',
      type: 'object',
      label: 'Endpoint',
      lovPara: { tenantId: organizationId },
      cascadeMap: { parentValue: 'platformInfo.tag' },
      required: true,
      ignore: 'always',
      dynamicProps: {
        lovCode({ record }) {
          return record.get('platform') === 'ALI'
            ? 'HIOT.LOV.CLOUD_ENDPOINT_ALI'
            : 'HIOT.LOV.CLOUD_ENDPOINT';
        },
        required({ record }) {
          return record.get('platform') === 'ALI' || record.get('platform') === 'BAIDU';
        },
      },
    },
    {
      name: 'endpoint',
      type: 'string',
      bind: 'endpointInfo.value',
    },
    {
      name: 'description',
      type: 'string',
      label: intl.get('hzero.common.explain').d('说明'),
      maxLength: 100,
    },
    {
      name: 'hubAddressOriginal',
      type: 'string',
    },
    {
      name: 'hubAddressOriginalShow',
      type: 'string',
      label: intl.get(`${prefix}.iot.conn-info`).d('IOT连接信息'),
      ignore: 'always',
    },
    {
      name: 'operation',
      type: 'string',
      ignore: 'always',
    },
    {
      name: 'accessKey',
      type: 'string',
      label: 'AccessKey',
      required: true,
    },
    {
      name: 'secretKey',
      type: 'string',
      label: 'SecretKey',
      required: true,
    },
    {
      name: 'enabledFlag',
      type: 'number',
      label: intl.get(`${prefix}.use-status`).d('应用状态'),
      defaultValue: 0,
      trueValue: 1,
      falseValue: 0,
    },
  ],
  events: {
    update({ record, name, value }) {
      if (name === 'platformInfo') {
        record.set('serverCode', (value || {}).tag);
      }
    },
  },
});
const formConfigDS = () => ({
  paging: false,
  transport: {
    /**
     * 表单配置
     */
    read: () => {
      const url = isTenantRoleLevel()
        ? `${HZERO_PLATFORM}/v1/${organizationId}/form-lines/header-code`
        : `${HZERO_PLATFORM}/v1/form-lines/header-code`;
      return {
        url,
        method: 'GET',
        params: {},
      };
    },
  },
});

// 消费者组表格ds
const consumerTableDS = () => ({
  autoQuery: true,
  selection: false,
  dataKey: 'content',
  autoQueryAfterSubmit: false,
  fields: [
    {
      name: 'code',
      label: intl.get('hiot.consumerGroups.model.consumer.code').d('消费者编码'),
      type: 'string',
      required: true,
    },
    {
      name: 'name',
      type: 'string',
      required: true,
      maxLength: 100,
      label: intl.get('hiot.consumerGroups.model.consumer.name').d('消费者名称'),
    },
    {
      name: 'guid',
      label: intl.get('hiot.consumerGroups.model.consumer.guid').d('消费者ID'),
      type: 'string',
      maxLength: 100,
      required: true,
    },
    {
      name: 'description',
      type: 'string',
      maxLength: 255,
      label: intl.get('hiot.consumerGroups.model.consumer.description').d('说明'),
    },
    {
      name: 'enableListenerFlag',
      label: intl.get('hiot.consumerGroups.model.consumer.enableListenerFlag').d('是否监听'),
      type: 'boolean',
      trueValue: 1,
      falseValue: 0,
      defaultValue: 0,
    },
  ],
  transport: {
    read: () => ({
      url: `${apiPrefix}/consumer-groups`,
      method: 'GET',
    }),
    create: ({ data }) => {
      const { __id, _status, ...other } = Array.isArray(data) ? data[0] : {};
      return {
        url: `${apiPrefix}/consumer-groups`,
        method: 'POST',
        data: other,
      };
    },
    update: ({ data }) => {
      const { __id, _status, ...other } = Array.isArray(data) ? data[0] : {};
      return {
        url: `${apiPrefix}/consumer-groups`,
        method: 'PUT',
        data: other,
      };
    },
    destroy: ({ data }) => {
      const { __id, _status, ...other } = Array.isArray(data) ? data[0] : {};
      return {
        url: `${apiPrefix}/consumer-groups`,
        method: 'DELETE',
        data: other,
      };
    },
  },
});

// 分配模型表格DS
const drawerTableDS = () => ({
  autoQuery: false,
  selection: 'multiple',
  dataKey: 'content',
  cacheSelection: true,
  primaryKey: 'thingModelId',
  queryFields: [
    {
      name: 'thingModelCode',
      label: intl.get('hiot.consumerGroups.model.consumer.thingModelCode').d('设备模型编码'),
      type: 'string',
    },
    {
      name: 'thingModelName',
      label: intl.get('hiot.consumerGroups.model.consumer.thingModelName').d('设备模型名称'),
      type: 'string',
    },
  ],
  fields: [
    {
      name: 'thingModelCode',
      label: intl.get('hiot.consumerGroups.model.consumer.thingModelCode').d('设备模型编码'),
      type: 'string',
    },
    {
      name: 'thingModelName',
      label: intl.get('hiot.consumerGroups.model.consumer.thingModelName').d('设备模型名称'),
      type: 'string',
    },
    {
      name: 'consumerName',
      label: intl.get('hiot.consumerGroups.model.consumer.consumerName').d('消费者名称'),
      type: 'string',
    },
  ],
  transport: {
    read: () => ({
      url: `${apiPrefix}/consumer-groups/bind-list`,
      method: 'GET',
    }),
  },
});

const bindThingDS = () => ({
  transport: {
    create: ({ data }) => {
      const { __id, _status, ...other } = Array.isArray(data) ? data[0] : {};
      return {
        url: `${apiPrefix}/consumer-groups/update-bind`,
        method: 'PUT',
        data: other,
      };
    },
  },
});

// 启动监听
const startMonitorDS = () => ({
  transport: {
    create: ({ data }) => {
      const { __id, _status, ...other } = Array.isArray(data) ? data[0] : {};
      return {
        url: `${apiPrefix}/consumer-groups`,
        method: 'PUT',
        data: {
          ...other,
          enableListenerFlag: 1,
        },
      };
    },
  },
});

export {
  cloudAccountListDS,
  cloudAccountDetailDS,
  formConfigDS,
  consumerTableDS,
  drawerTableDS,
  bindThingDS,
  startMonitorDS,
};
