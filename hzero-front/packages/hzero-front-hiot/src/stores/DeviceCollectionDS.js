/**
 * DeviceCollectionDS - 设备采集
 * @date 2020-06-02
 * @version 0.0.1
 * @copyright Copyright (c) 2019, Hand
 */

import { isArray } from 'lodash';
import intl from 'utils/intl';
import { API_PREFIX } from '@/utils/constants';
import { getCurrentUser, isTenantRoleLevel, getCurrentOrganizationId } from 'utils/utils';

const tenantId = getCurrentOrganizationId();
const isTenant = isTenantRoleLevel();
const apiPrefix = isTenant ? `${API_PREFIX}/v1/${tenantId}` : `${API_PREFIX}/v1`;

const modelPrompt = 'hiot.deviceCollection.model.device';

const userId = getCurrentUser().id;

const tableDS = () => ({
  name: 'deviceTableDS',
  primaryKey: 'dcDeviceId',
  autoQuery: true,
  autoCreate: true,
  autoQueryAfterSubmit: false,
  transport: {
    read: () => {
      const url = `${apiPrefix}/egk-dc-device/list`;
      const serviceConfig = {
        url,
        method: 'GET',
      };
      return serviceConfig;
    },
    destroy: ({ data }) => {
      const deviceIds = [];
      if (isArray(data)) {
        data.forEach((sel) => {
          deviceIds.push(sel.dcDeviceId);
        });
      }
      return {
        data: deviceIds,
        url: `${apiPrefix}/egk-dc-device/delete`,
        method: 'POST',
      };
    },
    create: ({ data }) => {
      return {
        url: `${apiPrefix}/egk-dc-device/save`,
        data,
        method: 'POST',
      };
    },
    update: ({ data }) => {
      return {
        url: `${apiPrefix}/egk-dc-device/save`,
        data,
        method: 'POST',
      };
    },
  },
  fields: [
    {
      name: 'gatewayCode',
      label: intl.get('hiot.common.model.common.gatewayCode').d('网关编码'),
      type: 'string',
      required: false,
    },
    {
      name: 'dcDeviceCode',
      label: intl.get(`${modelPrompt}.dcDeviceCode`).d('Edgink设备编码'),
      type: 'string',
      required: false,
    },
    {
      name: 'description',
      label: intl.get(`${modelPrompt}.description`).d('描述'),
      type: 'string',
      required: false,
    },
    {
      name: 'heartbeatCycle',
      label: intl.get(`${modelPrompt}.heartbeatCycle`).d('重连周期(ms)'),
      type: 'string',
      required: false,
    },
    {
      name: 'packageName',
      label: intl.get(`${modelPrompt}.packageName`).d('OTA升级包'),
      type: 'string',
      required: false,
    },
    {
      name: 'connectInfo',
      label: intl.get(`${modelPrompt}.connectInfo`).d('通讯属性'),
      type: 'string',
      required: false,
    },
    {
      name: 'simulatorFlag',
      label: intl.get(`${modelPrompt}.simulatorFlag`).d('模拟设备'),
      type: 'boolean',
      required: false,
      trueValue: 1,
      falseValue: 0,
    },
    {
      name: 'enableFlag',
      label: intl.get(`${modelPrompt}.enableFlag`).d('启用状态'),
      type: 'boolean',
      required: false,
      trueValue: 1,
      falseValue: 0,
    },
  ],
  queryFields: [
    {
      name: 'gatewayCodeObject',
      label: intl.get('hiot.common.model.common.gatewayCode').d('网关编码'),
      type: 'object',
      lovCode: 'HIOT.LOV.GATEWAY',
      lovPara: { tenantId },
      textField: 'gatewayCode',
      valueField: 'gatewayId',
      noCache: true,
      required: false,
      ignore: 'always',
    },
    {
      name: 'gatewayId',
      label: intl.get('hiot.common.model.common.gatewayCode').d('网关编码'),
      type: 'string',
      bind: 'gatewayCodeObject.gatewayId',
    },
    {
      name: 'dcDeviceCode',
      label: intl.get(`${modelPrompt}.dcDeviceCode`).d('Edgink设备编码'),
      type: 'string',
    },
    {
      name: 'desc',
      label: intl.get(`${modelPrompt}.deviceDescription`).d('设备描述'),
      type: 'string',
    },
  ],
});

const formDS = () => ({
  name: 'formDs',
  pageSize: 10,
  primaryKey: 'dcDeviceId',
  autoCreate: true,
  autoQuery: false,
  dataToJSON: 'all',
  autoQueryAfterSubmit: false,
  transport: {
    read: (config) => {
      const { dcDeviceId } = config.dataSet.current.toData();
      const params = { dcDeviceId };
      const url = `${apiPrefix}/egk-dc-device/list`;
      const serviceConfig = {
        params,
        config,
        url,
        method: 'GET',
      };
      return serviceConfig;
    },
    create: ({ dataSet }) => {
      const params = dataSet.queryParameter;
      delete params.gatewayObject;
      params.tenantId = tenantId;
      params.userId = userId;
      return {
        url: `${apiPrefix}/egk-dc-device/save`,
        data: [params],
        method: 'POST',
      };
    },
    update: ({ dataSet }) => {
      const params = dataSet.queryParameter;
      delete params.gatewayObject;
      params.tenantId = tenantId;
      params.userId = userId;
      return {
        url: `${apiPrefix}/egk-dc-device/save`,
        data: [params],
        method: 'POST',
      };
    },
  },
  fields: [
    {
      name: 'gatewayObject',
      label: intl.get('hiot.common.model.common.gatewayCode').d('网关编码'),
      type: 'object',
      required: true,
      ignore: 'always',
      lovCode: 'HIOT.LOV.GATEWAY',
      textField: 'gatewayCode',
      lovPara: { tenantId },
    },
    {
      name: 'gatewayId',
      label: intl.get(`${modelPrompt}.gatewayId`).d('服务器Id'),
      type: 'string',
      bind: 'gatewayObject.gatewayId',
    },
    {
      name: 'gatewayCode',
      label: intl.get('hiot.common.model.common.gatewayCode').d('网关编码'),
      type: 'string',
      bind: 'gatewayObject.gatewayCode',
    },
    {
      name: 'dcDeviceCode',
      label: intl.get(`${modelPrompt}.dcDeviceCode`).d('Edgink设备编码'),
      type: 'string',
      required: true,
    },
    {
      name: 'description',
      label: intl.get(`${modelPrompt}.description`).d('描述'),
      type: 'intl',
      required: true,
    },
    {
      name: 'heartbeatCycle',
      label: intl.get(`${modelPrompt}.heartbeatCycle`).d('重连周期(ms)'),
      type: 'string',
      required: true,
      pattern: /[\d]/g,
      defaultValue: '10000',
      defaultValidationMessages: {
        patternMismatch: intl
          .get('hiot.deviceCollection.view.validation.numberMsg')
          .d('请输入数字'),
      },
    },
    {
      name: 'packageId',
      label: intl.get(`${modelPrompt}.packageName`).d('OTA升级包'),
      type: 'string',
      required: true,
      lookupCode: 'HIOT.EDGINK.PACKAGE',
      lovPara: { tenantId },
      textField: 'packageName',
      valueField: 'packageId',
    },
    {
      name: 'packageName',
      label: intl.get(`${modelPrompt}.packageName`).d('OTA升级包'),
      type: 'string',
    },
    {
      name: 'connectInfo',
      label: intl.get(`${modelPrompt}.connectInfo`).d('通讯属性'),
      type: 'string',
      required: false,
      ignore: 'always',
    },
    {
      name: 'simulatorFlag',
      label: intl.get(`${modelPrompt}.simulatorFlag`).d('模拟设备'),
      type: 'boolean',
      required: false,
      trueValue: 1,
      falseValue: 0,
      defaultValue: 0,
      ignore: 'always',
    },
    {
      name: 'enableFlag',
      label: intl.get(`${modelPrompt}.enableFlag`).d('启用状态'),
      type: 'boolean',
      required: false,
      trueValue: 1,
      falseValue: 0,
      defaultValue: 1,
      ignore: 'always',
    },
  ],
});

const packageDS = () => ({
  name: 'packageDs',
  pageSize: 10,
  primaryKey: 'deviceAttId',
  autoCreate: true,
  autoQuery: false,
  transport: {
    read: (config) => {
      const url = `${apiPrefix}/egk-dc-device-attr/list`;
      const serviceConfig = {
        config,
        url,
        method: 'GET',
      };
      return serviceConfig;
    },
  },
  queryFields: [],
  fields: [],
});

const packageFormDS = () => ({
  name: 'packageFormDs',
  pageSize: 10,
  primaryKey: 'deviceAttId',
  autoCreate: true,
  autoQuery: false,
  queryFields: [],
  fields: [],
});

const saveFormDS = () => ({
  name: 'saveFormDs',
  pageSize: 10,
  primaryKey: 'dcDeviceId',
  autoCreate: false,
  autoQuery: false,
  transport: {
    create: ({ data, params }) => {
      return {
        url: `${apiPrefix}/egk-dc-device/save`,
        data,
        params,
        method: 'POST',
      };
    },
  },
  queryFields: [],
  fields: [],
});

const detailFormDS = () => ({
  pageSize: 10,
  primaryKey: 'dcDeviceId',
  autoCreate: false,
  autoQuery: false,
  dataToJSON: 'all',
  autoQueryAfterSubmit: false,
  transport: {
    read: ({ data }) => {
      const url = `${apiPrefix}/egk-dc-device/list`;
      const serviceConfig = {
        data,
        url,
        method: 'GET',
      };
      return serviceConfig;
    },
  },
  fields: [
    {
      name: 'gatewayObject',
      label: intl.get('hiot.common.model.common.gatewayCode').d('网关编码'),
      type: 'object',
      ignore: 'always',
      lovCode: 'HIOT.LOV.GATEWAY',
      lovPara: { tenantId },
      textField: 'gatewayCode',
    },
    {
      name: 'gatewayId',
      label: intl.get(`${modelPrompt}.gatewayId`).d('服务器Id'),
      type: 'string',
      bind: 'gatewayObject.gatewayId',
    },
    {
      name: 'gatewayCode',
      label: intl.get('hiot.common.model.common.gatewayCode').d('网关编码'),
      type: 'string',
      bind: 'gatewayObject.gatewayCode',
    },
    {
      name: 'dcDeviceCode',
      label: intl.get('hiot.common.device.code').d('设备编码'),
      type: 'string',
    },
    {
      name: 'description',
      label: intl.get(`${modelPrompt}.description`).d('描述'),
      type: 'intl',
    },
    {
      name: 'heartbeatCycle',
      label: intl.get(`${modelPrompt}.heartbeatCycle`).d('重连周期(ms)'),
      type: 'number',
      defaultValue: 10000,
    },
    {
      name: 'packageId',
      label: intl.get(`${modelPrompt}.packageName`).d('OTA升级包'),
      type: 'string',
      lookupCode: 'HIOT.EDGINK.PACKAGE',
      lovPara: { tenantId },
      textField: 'packageName',
      valueField: 'packageId',
    },
    {
      name: 'packageName',
      label: intl.get(`${modelPrompt}.packageName`).d('OTA升级包'),
      type: 'string',
    },
    {
      name: 'connectInfo',
      label: intl.get(`${modelPrompt}.connectInfo`).d('通讯属性'),
      type: 'string',
      ignore: 'always',
    },
    {
      name: 'simulatorFlag',
      label: intl.get(`${modelPrompt}.simulatorFlag`).d('模拟设备'),
      type: 'boolean',
      trueValue: 1,
      falseValue: 0,
    },
    {
      name: 'enableFlag',
      label: intl.get(`${modelPrompt}.enableFlag`).d('启用状态'),
      type: 'boolean',
      trueValue: 1,
      falseValue: 0,
    },
  ],
});

const CollectionDS = () => ({
  autoCreate: true,
  autoQuery: false,
  dataToJSON: 'all',
  autoQueryAfterSubmit: false,
  transport: {
    read: ({ data }) => {
      const url = `${apiPrefix}/egk-dc-device-tag/list`;
      const serviceConfig = {
        data,
        url,
        method: 'GET',
      };
      return serviceConfig;
    },
    destroy: ({ data }) => {
      return {
        url: `${apiPrefix}/egk-dc-device-tag/delete`,
        data: data.map((item) => item.tagId),
        method: 'POST',
      };
    },
    create: ({ data, dataSet }) => {
      const params = dataSet.queryParameter;

      const datas = data.map((item) => ({
        ...item,
        ...params,
      }));

      return {
        url: `${apiPrefix}/egk-dc-device-tag/save`,
        data: datas,
        method: 'POST',
      };
    },
    update: ({ data }) => {
      return {
        url: `${apiPrefix}/egk-dc-device-tag/save`,
        data,
        method: 'POST',
      };
    },
  },
  fields: [
    {
      name: 'orderCode',
      label: intl.get(`${modelPrompt}.orderCode`).d('排序号'),
      type: 'string',
      required: true,
      defaultValue: '1',
    },
    {
      name: 'address',
      label: intl.get(`${modelPrompt}.address`).d('采集项地址'),
      type: 'string',
      // pattern: /^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/,
      required: true,
    },
    {
      name: 'description',
      label: intl.get(`${modelPrompt}.collectionDesc`).d('采集项描述'),
      type: 'intl',
      required: true,
    },
    {
      name: 'equipmentCode',
      label: intl.get(`${modelPrompt}.equipmentCode`).d('设备别名'),
      type: 'string',
    },
    {
      name: 'parameter',
      label: intl.get(`${modelPrompt}.parameter`).d('采集项名称'),
      type: 'string',
      required: true,
    },
    {
      name: 'dataType',
      label: intl.get(`${modelPrompt}.dataType`).d('数据类型'),
      type: 'string',
      lookupCode: 'HIOT.EDGINK.DC.DATA_TYPE',
      defaultValue: 'Short',
      required: true,
    },
    {
      name: 'clientAccess',
      label: intl.get(`${modelPrompt}.clientAccess`).d('读写权限'),
      type: 'string',
      lookupCode: 'HIOT.EDGINK.DC.CLIENT_ACCESS',
      defaultValue: 'Read_Write',
      required: true,
    },
    {
      name: 'frequency',
      label: intl.get(`${modelPrompt}.frequency`).d('采集频率(ms)'),
      type: 'number',
      lookupCode: 'HIOT.EDGINK.DC.FREQUENCY',
      defaultValue: '1000',
      required: true,
    },
    {
      name: 'multiple',
      label: intl.get(`${modelPrompt}.multiple`).d('缩放倍数'),
      type: 'number',
      defaultValue: '1.0',
      required: true,
    },
    {
      name: 'triggerFlag',
      label: intl.get(`${modelPrompt}.triggerFlag`).d('轮巡采集'),
      type: 'boolean',
      trueValue: 1,
      falseValue: 0,
      defaultValue: 1,
    },
    {
      name: 'recordChangesFlag',
      label: intl.get(`${modelPrompt}.recordChangesFlag`).d('记录变化值'),
      type: 'boolean',
      trueValue: 1,
      falseValue: 0,
      defaultValue: 1,
    },
    {
      name: 'publishedFlag',
      label: intl.get(`${modelPrompt}.publishedFlag`).d('Redis推送'),
      type: 'boolean',
      trueValue: 1,
      falseValue: 0,
      defaultValue: 0,
    },
    {
      name: 'enableFlag',
      label: intl.get(`${modelPrompt}.enableFlag`).d('启用状态'),
      type: 'boolean',
      trueValue: 1,
      falseValue: 0,
      defaultValue: 1,
    },
  ],
});

export { tableDS, formDS, packageDS, packageFormDS, saveFormDS, CollectionDS, detailFormDS };
