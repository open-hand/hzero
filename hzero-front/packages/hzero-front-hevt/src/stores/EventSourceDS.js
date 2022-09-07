/*
 * @Descripttion: 事件源定义
 * @version: 0.0.1
 * @Author: heqiheng <qiheng.he@hand-china.com>
 * @Date: 2020-05-18 21:20:14
 * @Copyright: Copyright (c) 2020, Hand
 */
import { getCurrentOrganizationId, isTenantRoleLevel, getCurrentUser } from 'utils/utils';
import { API_PREFIX } from '@/utils/constants';
import intl from 'utils/intl';

const organizationId = getCurrentOrganizationId();
const { tenantId } = getCurrentUser();
const levelUrl = isTenantRoleLevel() ? `/${organizationId}` : '';

const eventSourceDS = (isCreate, eventSourceId) => ({
  selection: false,
  autoQuery: true,
  autoCreate: true,
  primaryKey: 'eventSourceId',
  transport: {
    read: ({ data, params }) => {
      if (isCreate) {
        return {
          url: `${API_PREFIX}/v1${levelUrl}/event-sources`,
          params: { ...params, ...data },
          method: 'get',
        };
      } else {
        return {
          url: `${API_PREFIX}/v1${levelUrl}/event-sources/${eventSourceId}`,
          params: { ...params, ...data },
          method: 'get',
        };
      }
    },
    create: ({ data }) => {
      return {
        url: `${API_PREFIX}/v1${levelUrl}/event-sources`,
        data: { ...data[0] },
        method: 'POST',
      };
    },
    update: ({ data }) => {
      return {
        url: `${API_PREFIX}/v1${levelUrl}/event-sources`,
        data: { ...data[0] },
        method: 'PUT',
      };
    },
  },
  fields: [
    {
      name: 'eventSourceCode',
      label: intl.get('hevt.eventSource.model.eventSource.eventSourceCode').d('事件源编码'),
      required: true,
      type: 'string',
      validator: (value) => {
        const pattern = /^[A-Za-z0-9][A-Za-z0-9-_./]*$/;
        if (!pattern.test(value)) {
          return intl
            .get('hevt.common.validator.code2')
            .d('请输入字母及数字，只能以字母或数字开头，可包含“-”、“_”、“.”、“/”');
        }
      },
    },
    {
      name: 'eventSourceName',
      required: true,
      label: intl.get('hevt.eventSource.model.eventSource.eventSourceName').d('事件源名称'),
      type: 'string',
      maxLength: 255,
    },
    {
      name: 'serviceAddress',
      required: true,
      label: intl.get('hevt.eventSource.model.eventSource.serviceAddress').d('服务地址'),
      type: 'string',
      multiple: ',',
    },
    {
      name: 'eventSourceType',
      label: intl.get('hevt.eventSource.model.eventSource.eventSourceType').d('事件源类型'),
      required: true,
      type: 'string',
      lookupCode: 'HEVT.EVENT_SOURCE_TYPE',
    },
    {
      name: 'username',
      label: intl.get('hevt.eventSource.model.eventSource.username').d('用户名'),
      type: 'string',
    },
    {
      name: 'password',
      label: intl.get('hevt.eventSource.model.eventSource.password').d('密码'),
      type: 'string',
    },
    {
      name: 'enabledFlag',
      label: intl.get('hzero.common.status').d('状态'),
      type: 'number',
      trueValue: 1,
      falseValue: 0,
      defaultValue: 1,
    },
  ],
  queryFields: [
    {
      name: 'eventSourceCode',
      label: intl.get('hevt.eventSource.model.eventSource.eventSourceCode').d('事件源编码'),
      type: 'string',
    },
    {
      name: 'eventSourceName',
      label: intl.get('hevt.eventSource.model.eventSource.eventSourceName').d('事件源名称'),
      type: 'string',
      maxLength: 255,
    },
  ],
});

const EventHandleParamsDS = (eventSourceId) => ({
  transport: {
    read: ({ data, params }) => {
      return {
        url: `${API_PREFIX}/v1${levelUrl}/event-configs`,
        params: { ...params, ...data, sourceId: eventSourceId, sourceType: 'BINDER', tenantId },
        method: 'get',
      };
    },
    destroy: ({ data }) => {
      return {
        url: `${API_PREFIX}/v1${levelUrl}/event-configs`,
        data,
        method: 'DELETE',
      };
    },
  },
  fields: [
    {
      name: 'sourceType',
      defaultValue: 'BINDER',
    },
    {
      name: 'configCodeLov',
      label: intl.get('hevt.eventSource.model.eventSource.configCode').d('变量编码'),
      type: 'object',
      lovCode: 'HEVT.BINDER_ROCKETMQ_CONFIG',
      dynamicProps: {
        lovCode: ({ dataSet }) => {
          switch (dataSet.parent.current.get('eventSourceType')) {
            case 'kafka':
              return 'HEVT.BINDER_KAFKA_CONFIG';
            case 'rocketmq':
              return 'HEVT.BINDER_ROCKETMQ_CONFIG';
            case 'rabbit':
              return 'HEVT.BINDER_RABBIT_CONFIG';
            default:
              return 'HEVT.BINDER_KAFKA_CONFIG';
          }
        },
      },
      required: true,
      ignore: 'always',
    },
    {
      name: 'configCode',
      label: intl.get('hevt.eventSource.model.eventSource.configCode').d('变量编码'),
      type: 'string',
      required: true,
      bind: 'configCodeLov.value',
    },
    {
      name: 'configCodeMeaning',
      label: intl.get('hevt.eventSource.model.eventSource.configCodeMeaning').d('变量名'),
      type: 'string',
      bind: 'configCodeLov.meaning',
      ignore: 'always',
    },
    {
      name: 'configValue',
      label: intl.get('hevt.eventSource.model.eventSource.configValue').d('参数值'),
      type: 'string',
      required: true,
    },
  ],
});

export { eventSourceDS, EventHandleParamsDS };
