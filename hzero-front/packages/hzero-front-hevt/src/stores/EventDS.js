/*
 * @Descripttion: 事件定义--消费配置定义
 * @version: 0.0.1
 * @Author: heqiheng <qiheng.he@hand-china.com>
 * @Date: 2020-04-15 11:08:50
 * @Copyright: Copyright (c) 2020, Hand
 */
import { getCurrentOrganizationId, isTenantRoleLevel, getCurrentUser } from 'utils/utils';
import { API_PREFIX } from '@/utils/constants';
import intl from 'utils/intl';

const organizationId = getCurrentOrganizationId();
const { tenantId } = getCurrentUser();
const levelUrl = isTenantRoleLevel() ? `/${organizationId}` : '';

const EventHandleDS = (eventId) => ({
  autoQuery: true,
  transport: {
    read: () => {
      return {
        url: `${API_PREFIX}/v1${levelUrl}/event`,
        params: { eventId },
        method: 'get',
      };
    },
  },
  fields: [
    {
      name: 'eventCode',
      label: intl.get('hevt.common.model.categoryCode').d('事件编码'),
      type: 'string',
    },
    {
      name: 'eventName',
      label: intl.get('hevt.common.model.eventName').d('事件名称'),
      type: 'string',
    },
  ],
});

const EventHandleServiceDS = (eventId, eventHandleServiceId, edit) => ({
  autoQuery: true,
  autoCreate: true,
  primaryKey: 'eventHandleServiceId',
  selection: false,
  transport: {
    read: () => {
      if (edit) {
        return {
          url: `${API_PREFIX}/v1${levelUrl}/event-handle-services/${eventHandleServiceId}`,
          method: 'get',
        };
      } else {
        return {
          url: `${API_PREFIX}/v1${levelUrl}/event-handle-services`,
          params: { eventId },
          method: 'get',
        };
      }
    },
    create: ({ data }) => {
      return {
        url: `${API_PREFIX}/v1${levelUrl}/event-handle-services`,
        data: { ...data[0], tenantId },
        method: 'POST',
      };
    },
    update: ({ data }) => {
      return {
        url: `${API_PREFIX}/v1${levelUrl}/event-handle-services`,
        data: { ...data[0], tenantId },
        method: 'PUT',
      };
    },
  },
  fields: [
    {
      name: 'eventId',
      defaultValue: eventId,
    },
    {
      name: 'serviceObj',
      label: intl.get('hevt.eventHandle.model.eventHandle.serviceName').d('服务名称'),
      type: 'object',
      required: true,
      lovCode: 'HADM.SERVICE',
      ignore: 'always',
    },
    {
      name: 'serviceName',
      label: intl.get('hevt.eventHandle.model.eventHandle.serviceName').d('服务名称'),
      type: 'string',
      bind: 'serviceObj.serviceName',
      ignore: 'always',
    },
    {
      name: 'serviceCode',
      label: intl.get('hevt.eventHandle.model.eventHandle.serviceCode').d('服务编码'),
      type: 'string',
      bind: 'serviceObj.serviceCode',
    },
    {
      name: 'groupId',
      label: intl.get('hevt.eventHandle.model.eventHandle.groupId').d('分组'),
      type: 'string',
      required: true,
    },
    {
      name: 'enabledFlag',
      label: intl.get('hzero.common.status.enable').d('启用'),
      type: 'number',
      trueValue: 1,
      falseValue: 0,
      defaultValue: 1,
    },
  ],
});

// eslint-disable-next-line no-unused-vars  事件处理服务配置
const EventHandleWaysDS = (eventId) => ({
  transport: {
    read: () => {
      return {
        url: `${API_PREFIX}/v1${levelUrl}/event-handle-methods`,
        params: { tenantId },
        method: 'get',
      };
    },
    destroy: ({ data }) => {
      return {
        url: `${API_PREFIX}/v1${levelUrl}/event-handle-methods`,
        data,
        method: 'DELETE',
      };
    },
  },
  fields: [
    {
      name: 'eventId',
      defaultValue: eventId,
    },
    {
      name: 'handleFunctionLov',
      label: intl.get('hevt.eventHandle.model.eventHandle.handleFunction').d('处理方法'),
      type: 'object',
      textField: 'handleFunction',
      lovCode: 'HEVT.HANDLE_METHOD',
      required: true,
      dynamicProps: {
        lovPara: ({ dataSet }) => {
          const serviceCode = dataSet.parent.current.get('serviceCode');
          return {
            serviceCode,
          };
        },
      },
      igonre: 'always',
    },
    {
      name: 'beanName',
      type: 'string',
      bind: 'handleFunctionLov.beanName',
      igonre: 'always',
    },
    {
      name: 'handleFunction',
      label: intl.get('hevt.eventHandle.model.eventHandle.handleFunction').d('处理方法'),
      type: 'string',
      bind: 'handleFunctionLov.handleFunction',
      required: true,
    },
    {
      name: 'orderSeq',
      label: intl.get('hevt.eventHandle.model.eventHandle.orderSeq').d('排序号'),
      type: 'string',
      required: true,
    },
    {
      name: 'levelCode',
      label: intl.get('hevt.common.model.levelCode').d('层级'),
      type: 'string',
      lookupCode: 'HEVT.EVENT_LEVEL_CODE',
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
});

// eslint-disable-next-line no-unused-vars  事件处理参数配置
const EventHandleParamsDS = (eventHandleServiceId) => ({
  transport: {
    read: ({ data, params }) => {
      return {
        url: `${API_PREFIX}/v1${levelUrl}/event-configs`,
        params: {
          ...params,
          ...data,
          sourceId: eventHandleServiceId,
          sourceType: 'CONSUMER',
          tenantId,
        },
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
      defaultValue: 'CONSUMER',
    },
    {
      name: 'configCodeLov',
      label: intl.get('hevt.common.model.configCode').d('参数名称'),
      type: 'object',
      lovCode: 'HEVT.CONSUMER_CONFIG',
      required: true,
      ignore: 'always',
    },
    {
      name: 'configCode',
      label: intl.get('hevt.common.model.configCode').d('参数名称'),
      type: 'string',
      required: true,
      bind: 'configCodeLov.value',
    },
    {
      name: 'configCodeMeaning',
      label: intl.get('hevt.eventSource.model.eventSource.name').d('变量名'),
      type: 'string',
      bind: 'configCodeLov.meaning',
      ignore: 'always',
    },
    {
      name: 'configValue',
      label: intl.get('hevt.common.model.configValue').d('参数值'),
      type: 'string',
      required: true,
    },
  ],
});

// 事件参数配置
const EventParamsDS = () => ({
  transport: {
    read: ({ data, params }) => {
      return {
        url: `${API_PREFIX}/v1${levelUrl}/event-configs`,
        params: { ...params, ...data, sourceType: 'PRODUCER' },
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
      defaultValue: 'PRODUCER',
    },
    {
      name: 'configCodeLov',
      label: intl.get('hevt.common.model.handleFunction').d('参数名称'),
      type: 'object',
      lovCode: 'HEVT.PRODUCER_CONFIG',
      required: true,
      ignore: 'always',
    },
    {
      name: 'configCode',
      label: intl.get('hevt.common.model.configCode').d('参数名称'),
      type: 'string',
      required: true,
      bind: 'configCodeLov.value',
    },
    {
      name: 'configCodeMeaning',
      label: intl.get('hevt.eventSource.model.eventSource.name').d('变量名'),
      type: 'string',
      bind: 'configCodeLov.meaning',
      ignore: 'always',
    },
    {
      name: 'configValue',
      label: intl.get('hevt.common.model.configValue').d('参数值'),
      type: 'string',
      required: true,
    },
  ],
});

export {
  EventHandleDS,
  EventHandleServiceDS,
  EventHandleWaysDS,
  EventHandleParamsDS,
  EventParamsDS,
};
