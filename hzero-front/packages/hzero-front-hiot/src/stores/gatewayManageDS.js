/**
 * @Author: guanglong.sun <guanglong.sun@hand-china.com>
 * @Create time: 2019/10/18 5:06 下午
 * @Copyright: Copyright(c) 2019, Hand
 * @Description: 网关管理DS
 */
import { DataSet } from 'choerodon-ui/pro';

import intl from 'utils/intl';
import { getCurrentOrganizationId, getResponse } from 'utils/utils';
import { API_PREFIX, DEVICE_DEFAULT_CONNECT, DEVICE_DEFAULT_STATUS } from '@/utils/constants';

const organizationId = getCurrentOrganizationId();

const gatewayPrefix = 'hiot.gatewayManage.model.gateway';

const commonSubDevice = [
  {
    name: 'thingName',
    type: 'object',
    label: intl.get('hiot.common.device.name').d('设备名称'),
    lovCode: 'HIOT.LOV.GATEWAY_THING',
    lovPara: { tenantId: organizationId },
    required: true,
    cascadeMap: { thingGroupId: 'thingGroupId' },
  },
  {
    name: 'thingCode',
    type: 'string',
    label: intl.get('hiot.common.device.code').d('设备编码'),
    bind: 'thingName.thingCode',
    required: true,
  },
  {
    name: 'thingId',
    type: 'string',
    bind: 'thingName.thingId',
    required: true,
  },
  {
    name: 'endpointProtocol',
    type: 'string',
    label: intl.get('hiot.gatewayManage.model.connect.method').d('连接方式'),
    lookupCode: 'HIOT.ENDPOINT_PROTOCOL',
    required: true,
    defaultValue: 'EDGINK',
  },
  {
    name: 'description',
    type: 'string',
    label: intl.get(`${gatewayPrefix}.description`).d('描述'),
    maxLength: 100,
  },
];

const commonModbus = [
  ...commonSubDevice,
  {
    name: 'slaveStation',
    type: 'number',
    label: intl.get(`${gatewayPrefix}.slaveStation`).d('从站'),
    required: true,
    min: 1,
    max: 247,
    step: 1,
    pattern: '^[0-9]*[1-9][0-9]*$',
  },
  {
    name: 'connectionMode',
    type: 'string',
    lookupCode: 'HIOT.CONNECTION_MODE',
    label: intl.get(`${gatewayPrefix}.connectionMode`).d('传输模式'),
    required: true,
    defaultValue: 'TCP',
  },
];

const commonSubDeviceRead = () => ({
  /**
   * 网关子设备信息
   * @param config 参数
   * @param data
   * @returns {{method: string, data: *, url: string}}
   */
  read: ({ data }) => {
    const { deviceType, thingId } = data;
    return {
      url: `${API_PREFIX}/v1/${organizationId}/gateways/sub-device/${deviceType}/${thingId}`,
      method: 'get',
      data: {},
    };
  },
});

const commonSubParseRead = () => ({
  /**
   * 网关子设备信息
   * @param config 参数
   * @param data
   * @returns {{method: string, data: *, url: string}}
   */
  read: ({ data }) => {
    const { deviceType, thingId } = data;
    return {
      url: `${API_PREFIX}/v1/${organizationId}/gateways/sub-device-parser-list/${deviceType}/${thingId}`,
      method: 'get',
    };
  },
});

const gatewayManageDS = () => ({
  primaryKey: 'gatewayId',
  transport: {
    /**
     * 网关列表信息查询
     * @param config 参数
     * @param data
     * @returns {{method: string, data: *, url: string}}
     */
    read: ({ config, data }) => {
      const { gatewayId } = data;
      return {
        ...config,
        url: gatewayId
          ? `${API_PREFIX}/v1/${organizationId}/gateways/${gatewayId}`
          : `${API_PREFIX}/v1/${organizationId}/gateways`,
        method: 'get',
        data,
        transformResponse: (res) => {
          let formatData = {};
          try {
            formatData = JSON.parse(res);
          } catch (e) {
            return e;
          }
          if (getResponse(formatData)) {
            const { authorize = '{}' } = formatData;
            if (authorize) {
              const other = `${authorize}`;
              formatData = {
                ...formatData,
                ...JSON.parse(`${other}`),
              };
            }
            return formatData;
          }
        },
      };
    },
    /**
     * 创建网关
     * @param data 创建网关的数据
     * @returns {{method: string, data: (boolean|{connected: *, status: *}), url: string}}
     */
    create: ({ data }) => {
      const {
        __id,
        _status,
        gateway = {},
        project: { thingGroupId },
        authType,
        secret,
        from,
        username,
        password,
        ...params
      } = data[0];
      const { thingModelId } = gateway;
      return {
        url: `${API_PREFIX}/v1/${organizationId}/gateways`,
        method: 'post',
        data: data.length > 0 && {
          thingModelId,
          thingGroupId,
          ...params,
          connected: DEVICE_DEFAULT_CONNECT,
          status: DEVICE_DEFAULT_STATUS,
          organizationId,
          authorize: JSON.stringify({ authType, secret, from, username, password }),
        },
      };
    },
    /**
     * 删除选中的网关
     * @param data 待删除的网关列表
     * @returns {{method: string, data: (boolean|*), url: string}}
     */
    destroy: ({ data }) => ({
      url: `${API_PREFIX}/v1/${organizationId}/gateways`,
      method: 'delete',
      data: data.length > 0 && data.map((record) => record.gatewayId),
    }),
    /**
     * 更新网关基本信息
     * @param data 待更新的数据
     * @returns {{method: string, url: string}}
     */
    update: ({ data }) => {
      const {
        gateway = {},
        project: { thingGroupId },
        ...restFields
      } = data[0];
      const { thingModelId } = gateway;
      return {
        url: `${API_PREFIX}/v1/${organizationId}/gateways`,
        method: 'put',
        data: {
          gateway: {
            thingModelId,
            thingGroupId,
            ...restFields,
          },
        },
      };
    },
  },
  queryFields: [
    {
      name: 'gatewayCode',
      type: 'string',
      label: intl.get('hiot.common.model.common.gatewayCode').d('网关编码'),
    },
    {
      name: 'gatewayName',
      type: 'string',
      label: intl.get('hiot.common.gateway.name').d('网关名称'),
    },
    {
      name: 'connected',
      type: 'number',
      lookupCode: 'HPFM.FLAG',
      label: intl.get('hiot.common.is-online').d('是否在线'),
    },
    {
      name: 'status',
      type: 'string',
      lookupCode: 'HIOT.GATEWAY_REGISTER_STATUS',
      label: intl.get('hiot.gatewayManage.model.gateway.status').d('网关状态'),
    },
    {
      name: 'thingModelLov',
      lovCode: 'HIOT.LOV.GATEWAY_MODEL',
      type: 'object',
      label: intl.get('hiot.common.gateway.model').d('网关模型'),
      lovPara: { tenantId: organizationId },
      ignore: 'always',
      noCache: true,
    },
    {
      name: 'thingModelId',
      type: 'string',
      bind: 'thingModelLov.thingModelId',
    },
  ],
  fields: [
    {
      name: 'gatewayCode',
      type: 'string',
      label: intl.get('hiot.common.model.common.gatewayCode').d('网关编码'),
      required: true,
    },
    {
      name: 'gatewayName',
      type: 'intl',
      label: intl.get('hiot.common.gateway.name').d('网关名称'),
      maxLength: 45,
      required: true,
    },
    {
      name: 'connected',
      type: 'number',
      label: intl.get('hiot.common.is-online').d('是否在线'),
    },
    {
      name: 'statusMeaning',
      type: 'string',
      label: intl.get('hiot.gatewayManage.model.gateway.status').d('网关状态'),
    },
    {
      name: 'gateway',
      type: 'object',
      lovCode: 'HIOT.LOV.GATEWAY_MODEL',
      lovPara: { tenantId: organizationId },
      label: intl.get('hiot.common.gateway.model').d('网关模型'),
    },
    {
      name: 'thingModelName',
      type: 'string',
      label: intl.get('hiot.common.gateway.model').d('网关模型'),
    },
    {
      name: 'project',
      type: 'object',
      lovCode: 'HIOT.THING_GROUP',
      lovPara: { tenantId: organizationId },
      label: intl.get(`hiot.common.model.device.parentDeviceName`).d('所属设备分组'),
      required: true,
    },
    {
      name: 'thingGroupId',
      type: 'string',
      bind: 'project.thingGroupId',
    },
    {
      name: 'name',
      type: 'string',
      label: intl.get(`hiot.common.model.device.parentDeviceName`).d('所属设备分组'),
    },
    {
      name: 'subNumber',
      type: 'string',
      label: intl.get(`${gatewayPrefix}.subNumber`).d('子设备数量'),
    },
    {
      name: 'operation',
      type: 'string',
      label: intl.get(`hzero.common.button.action`).d('操作'),
    },
    {
      name: 'model',
      type: 'string',
      label: intl.get(`${gatewayPrefix}.model`).d('网关型号'),
    },
    {
      name: 'version',
      type: 'string',
      label: intl.get(`${gatewayPrefix}.version`).d('固件版本'),
    },
    {
      name: 'description',
      type: 'string',
      label: intl.get('hzero.common.explain').d('说明'),
      maxLength: 100,
    },
    {
      name: 'guid',
      type: 'string',
      label: intl.get('hiot.common.ID').d('ID'),
    },
    {
      name: 'configLov',
      label: intl.get('hiot.common.model.common.platform').d('云平台'),
      type: 'object',
      lovCode: 'HIOT.LOV.CLOUD_ACCOUNT',
      lovPara: { tenantId: organizationId },
      ignore: 'always',
      textField: 'platformName',
      noCache: true,
      required: true,
    },
    {
      name: 'configId',
      type: 'string',
      bind: 'configLov.configId',
    },
    {
      name: 'configName',
      type: 'string',
      bind: 'configLov.configName',
      ignore: 'always',
      required: true,
      label: intl.get('hiot.common.model.common.configName').d('云账户'),
    },
    {
      name: 'platformMeaning',
      required: true,
      type: 'string',
      label: intl.get('hiot.common.model.common.platform').d('云平台'),
      bind: 'configLov.platformName',
      ignore: 'always',
    },
    {
      name: 'platform',
      type: 'string',
      bind: 'configLov.platform',
    },
    {
      name: 'authType',
      type: 'string',
      label: intl.get('hiot.gatewayManage.model.gatewayManage.authType').d('认证方式'),
      options: new DataSet({
        selection: 'single',
        data: [
          { value: 'JWT', meaning: 'JWT' },
          { value: 'REDIS', meaning: 'REDIS' },
        ],
      }),
      dynamicProps: {
        required({ record }) {
          return record.get('platform') === 'OWN';
        },
      },
    },
    {
      name: 'secret',
      type: 'string',
      label: intl.get('hiot.gatewayManage.model.gatewayManage.secret').d('密钥'),
      dynamicProps: {
        required({ record }) {
          return record.get('authType') === 'JWT';
        },
      },
    },
    {
      name: 'from',
      type: 'string',
      label: intl.get('hiot.gatewayManage.model.gatewayManage.from').d('Token携带方式'),
      options: new DataSet({
        selection: 'single',
        data: [
          {
            value: 'username',
            meaning: intl.get('hiot.common.model.common.username').d('用户名'),
          },
          {
            value: 'password',
            meaning: intl.get('hiot.common.model.common.password').d('密码'),
          },
        ],
      }),
      dynamicProps: {
        required({ record }) {
          return record.get('authType') === 'JWT';
        },
      },
    },
    {
      name: 'productKey',
      type: 'string',
      label: intl.get('hiot.gatewayManage.model.gatewayManage.productKey').d('产品Key'),
    },
    {
      name: 'deviceName',
      type: 'string',
      label: intl.get('hiot.common.device.name').d('设备名称'),
    },
    {
      name: 'deviceSecret',
      type: 'string',
      label: intl.get('hiot.gatewayManage.model.gatewayManage.deviceSecret').d('设备密钥'),
    },
    {
      name: 'brokerAddress',
      type: 'string',
      label: intl.get('hiot.gatewayManage.model.gatewayManage.brokerAddress').d('brokerAddress'),
    },
    {
      name: 'clientId',
      type: 'string',
      label: intl.get('hiot.gatewayManage.model.gatewayManage.clientId').d('clientId'),
    },
    {
      name: 'password',
      type: 'string',
      dynamicProps: {
        label({ record }) {
          return record.get('platform') === 'OWN'
            ? intl.get('hiot.common.model.common.password').d('密码')
            : intl.get('hiot.gatewayManage.model.gatewayManage.MQTTPassword').d('MQTT密码');
        },
      },
    },
    {
      name: 'port',
      type: 'string',
      label: intl.get('hiot.gatewayManage.model.gatewayManage.port').d('port'),
    },
    {
      name: 'protocol',
      type: 'string',
      label: intl.get('hiot.gatewayManage.model.gatewayManage.protocol').d('协议'),
    },
    {
      name: 'username',
      type: 'string',
      label: intl.get('hiot.common.model.common.username').d('用户名'),
    },
    {
      name: 'gatewayIp',
      type: 'string',
      label: intl.get('hiot.gatewayManage.model.gatewayManage.gatewayIp').d('网关ip'),
      pattern: /^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/,
      defaultValidationMessages: {
        patternMismatch: intl.get('hiot.gatewayManage.view.validation.ipMsg').d('请输入有效的ip'),
      },
    },
    // {
    //   name: 'configId',
    // },
  ],
});

const edginkTableDS = () => ({
  transport: {
    ...commonSubParseRead,
  },
  fields: [
    {
      name: 'property',
      type: 'object',
      lovCode: 'HIOT.LOV.PROPERTY',
      label: intl.get('hiot.common.data.point.name').d('数据点名称'),
      required: true,
      lovPara: {
        category: 'CONTROL_PARAMETER',
        tenantId: organizationId,
      },
      cascadeMap: { thingId: 'thingId' },
    },
    {
      name: 'propertyName',
      type: 'string',
      label: intl.get('hiot.common.data.point.name').d('数据点名称'),
      required: true,
      bind: 'property.propertyName',
    },
    {
      name: 'propertyCode',
      type: 'string',
      label: intl.get('hiot.common.data.point.code').d('数据点编码'),
      bind: 'property.propertyCode',
      required: true,
      unique: true,
    },
    {
      name: 'requestInterval',
      type: 'number',
      label: intl.get('hiot.common.request.interval').d('请求间隔(S)'),
      bind: 'property.requestInterval',
    },
    {
      name: 'thingId',
      type: 'string',
    },
    {
      name: 'edginkItem',
      type: 'string',
      label: intl.get('hiot.gatewayManage.model.gatewayManage.edginkItem').d('Edgink采集项名'),
      required: true,
    },
    {
      name: 'dcDeviceTagAddress',
      type: 'string',
      label: intl.get('hiot.gatewayManage.model.gateway.dcDeviceTagAddress').d('Edgink采集项地址'),
    },
  ],
});

const modbusTableDS = () => ({
  transport: {
    ...commonSubParseRead,
  },
  fields: [
    {
      name: 'property',
      type: 'object',
      label: intl.get('hiot.common.data.point.name').d('数据点名称'),
      lovCode: 'HIOT.LOV.PROPERTY',
      required: true,
      lovPara: {
        category: 'CONTROL_PARAMETER',
        tenantId: organizationId,
      },
      cascadeMap: { thingId: 'thingId' },
    },
    {
      name: 'propertyName',
      type: 'string',
      label: intl.get('hiot.common.data.point.name').d('数据点名称'),
      required: true,
      bind: 'property.propertyName',
    },
    {
      name: 'propertyCode',
      type: 'string',
      label: intl.get('hiot.common.data.point.code').d('数据点编码'),
      bind: 'property.propertyCode',
      required: true,
      unique: true,
    },
    {
      name: 'thingId',
      type: 'string',
    },
    {
      name: 'requestInterval',
      type: 'string',
      label: intl.get('hiot.common.request.interval').d('请求间隔(S)'),
      bind: 'property.requestInterval',
    },
    {
      name: 'propertyModelCode',
      type: 'string',
      label: intl.get('hiot.gatewayManage.model.gatewayManage.templateCode').d('数据点模版CODE'),
      bind: 'property.propertyModelCode',
      required: true,
    },
    {
      name: 'dataType',
      type: 'string',
      label: intl.get('hiot.common.data.type').d('数据类型'),
      lookupCode: 'HIOT.PARSER_DATA_TYPE',
      required: true,
    },
    {
      name: 'dataLength',
      type: 'string',
      label: intl.get('hiot.gatewayManage.model.gatewayManage.dataLength').d('数据长度(BIT)'),
      lookupCode: 'HIOT.DATA_LENGTH',
      required: true,
    },
    {
      name: 'operationCode',
      type: 'string',
      label: intl.get('hiot.gatewayManage.model.gatewayManage.operationCode').d('操作码'),
      lookupCode: 'HIOT.OPERATION_CODE',
      required: true,
    },
    {
      name: 'registerStartAddress',
      type: 'string',
      label: intl
        .get('hiot.gatewayManage.model.gatewayManage.registerStartAddress')
        .d('寄存器开始地址'),
      required: true,
      pattern: /^\d{4}$/,
      defaultValidationMessages: {
        patternMismatch: intl.get(`${gatewayPrefix}.validationNumberMsg`).d('请输入4位数字'),
      },
    },
    {
      name: 'readLength',
      type: 'number',
      label: intl.get('hiot.gatewayManage.model.gatewayManage.readLength').d('读取长度'),
      required: true,
      min: 1,
      max: 256,
    },
    {
      name: 'bitSegment',
      type: 'number',
      label: intl.get('hiot.gatewayManage.model.gatewayManage.bitSegment').d('BIT位'),
      min: 0,
      max: 16,
    },
  ],
});

const subDeviceEdglinkDS = () => ({
  paging: false,
  transport: {
    ...commonSubDeviceRead,
  },
  fields: [
    ...commonSubDevice,
    {
      name: 'thingName',
      type: 'object',
      label: intl.get('hiot.common.device.name').d('设备名称'),
      lovCode: 'HIOT.LOV.GATEWAY_THING',
      lovPara: { tenantId: organizationId },
      required: true,
    },
    {
      name: 'edginkName',
      type: 'string',
      label: intl.get('hiot.gatewayManage.model.gatewayManage.edginkDeviceName').d('Edgink子设备'),
      required: true,
    },
    {
      name: 'reportedWay',
      type: 'string',
      lookupCode: 'HIOT.COMMUNICATION_WAY',
      label: intl.get('hiot.gatewayManage.model.gatewayManage.dataPostMethod').d('数据上报方式'),
      required: true,
    },
    {
      name: 'reportedTopic',
      type: 'string',
      label: intl.get('hiot.gatewayManage.model.gatewayManage.dataPostTopic').d('数据上报Topic'),
      required: true,
    },
    {
      name: 'reportedTopicLov',
      type: 'object',
      label: intl.get('hiot.gatewayManage.model.gatewayManage.dataPostTopic').d('数据上报Topic'),
      required: true,
      lovCode: 'HIOT.EDGINK_DC_DEVICE',
      ignore: 'always',
    },
    {
      name: 'desiredWay',
      type: 'string',
      lookupCode: 'HIOT.COMMUNICATION_WAY',
      label: intl.get('hiot.gatewayManage.model.gatewayManage.dataGetMethod').d('数据下控方式'),
      required: true,
    },
    {
      name: 'desiredTopic',
      type: 'string',
      defaultValue: 'WritePlc',
      label: intl.get('hiot.gatewayManage.model.gatewayManage.dataGetTopic').d('数据下控Topic'),
      required: true,
    },
  ],
});

const subDeviceModbusDS = () => ({
  paging: false,
  transport: {
    ...commonSubDeviceRead,
  },
  fields: [
    ...commonModbus,
    {
      name: 'thingName',
      type: 'object',
      label: intl.get('hiot.common.device.name').d('设备名称'),
      lovCode: 'HIOT.LOV.GATEWAY_THING',
      lovPara: { tenantId: organizationId },
      required: true,
    },
    {
      name: 'tcpHost',
      type: 'string',
      label: intl.get('hiot.gatewayManage.model.gatewayManage.ipAddress').d('IP地址'),
      dynamicProps: { required: ({ record }) => record.get('connectionMode') === 'TCP' },
    },
    {
      name: 'tcpPort',
      type: 'number',
      label: intl.get('hiot.gatewayManage.model.gatewayManage.tcpPort').d('端口号'),
      dynamicProps: { required: ({ record }) => record.get('connectionMode') === 'TCP' },
    },
    {
      name: 'rtuSerialPort',
      type: 'string',
      label: intl.get('hiot.gatewayManage.model.gatewayManage.serialPort').d('串口号'),
      dynamicProps: { required: ({ record }) => record.get('connectionMode') === 'RTU' },
    },
    {
      name: 'rtuBaudRate',
      type: 'number',
      label: intl.get('hiot.gatewayManage.model.gatewayManage.baudRate').d('波特率'),
      dynamicProps: { required: ({ record }) => record.get('connectionMode') === 'RTU' },
    },
    {
      name: 'rtuDataBit',
      type: 'number',
      label: intl.get('hiot.gatewayManage.model.gatewayManage.dataBit').d('数据位'),
      dynamicProps: { required: ({ record }) => record.get('connectionMode') === 'RTU' },
      defaultValue: 8,
    },
    {
      name: 'rtuCalibrationMethod',
      type: 'string',
      label: intl.get('hiot.gatewayManage.model.gatewayManage.check').d('校验'),
      lookupCode: 'HIOT.CALIBRATION_METHOD',
      dynamicProps: { required: ({ record }) => record.get('connectionMode') === 'RTU' },
    },
    {
      name: 'rtuStopBit',
      type: 'number',
      label: intl.get('hiot.gatewayManage.model.gatewayManage.stopBit').d('停止位'),
      dynamicProps: { required: ({ record }) => record.get('connectionMode') === 'RTU' },
      defaultValue: 1,
    },
  ],
});

const subDeviceOperatorDS = () => ({
  transport: {
    create: ({ data }) => {
      const { __id, _status, type, ...restFields } = data[0];
      return type === 'bind'
        ? {
            url: `${API_PREFIX}/v1/${organizationId}/gateways/binder-device`,
            data: restFields,
          }
        : {
            url: `${API_PREFIX}/v1/${organizationId}/gateways/update-sub-device`,
            data: restFields,
            method: 'put',
          };
    },
  },
});

const subDeviceTypeDS = () => ({
  fields: [
    {
      name: 'terminalType',
      type: 'string',
      label: intl.get('hiot.gatewayManage.model.gatewayManage.terminalType').d('终端类型'),
      lookupCode: 'HIOT.TERMINAL_TYPE',
    },
    {
      name: 'endpointProtocol',
      type: 'string',
      label: intl
        .get('hiot.gatewayManage.model.gatewayManage.endpointProtocol')
        .d('通信协议/软件名称'),
      lookupCode: 'HIOT.ENDPOINT_PROTOCOL',
    },
  ],
});

const subDeviceInfoDS = () => ({
  selection: false,
  transport: {
    read: ({ data }) => {
      const { gatewayId } = data;
      return {
        url: `${API_PREFIX}/v1/${organizationId}/gateways/sub-device/${gatewayId}`,
        method: 'get',
        data,
      };
    },
    destroy: ({ data }) => {
      const { endpointProtocol, modbusId, thingId, edginkId, gatewayId } = data[0];
      const id = modbusId || edginkId;
      return {
        url: `${API_PREFIX}/v1/${organizationId}/gateways/sub-device/${endpointProtocol}/${id}?gatewayId=${gatewayId}&thingId=${thingId}`,
        method: 'delete',
      };
    },
    update: ({ data, dataSet }) => {
      const { configId } = dataSet;
      const { __id, _status, ...restFields } = data[0];
      return {
        url: `${API_PREFIX}/v1/${organizationId}/gateways/is-disable-device`,
        data: { ...restFields, configId },
        method: 'post',
      };
    },
  },
  queryFields: [
    {
      name: 'thingCode',
      type: 'string',
      label: intl.get('hiot.common.device.code').d('设备编码'),
    },
    {
      name: 'thingName',
      type: 'string',
      label: intl.get('hiot.common.device.name').d('设备名称'),
    },
    {
      name: 'enabled',
      type: 'string',
      label: intl.get('hiot.common.is-enable').d('是否启用'),
      lookupCode: 'HPFM.ENABLED_FLAG',
    },
  ],
  fields: [
    {
      name: 'thingCode',
      type: 'string',
      label: intl.get('hiot.common.device.code').d('设备编码'),
    },
    {
      name: 'thingName',
      type: 'string',
      label: intl.get('hiot.common.device.name').d('设备名称'),
    },
    {
      name: 'configName',
      type: 'string',
      label: intl.get('hiot.gatewayManage.model.gatewayManage.configName.registry').d('注册云账户'),
    },
    {
      name: 'bindPropNum',
      type: 'number',
      label: intl.get('hiot.gatewayManage.model.gatewayManage.bindPropNum').d('已分配数据点'),
    },
    {
      name: 'enabled',
      type: 'boolean',
      label: intl.get('hiot.common.is-enable').d('是否启用'),
      trueValue: 1,
      falseValue: 0,
    },
    ...subDeviceTypeDS().fields,
  ],
});

const registerInfoDS = () => ({
  paging: false,
  transport: {
    /**
     * 获取注册物信息(网关/设备)
     * @param config
     * @param isRegisterOp
     * @returns {{method: string, url: string}}
     */
    read: ({ config = {}, data: { flag } = {} }) => {
      const url =
        flag !== undefined
          ? `${API_PREFIX}/v1/${organizationId}/cloud-operate/register-switch`
          : `${API_PREFIX}/v1/${organizationId}/cloud-operate/registration-info`;
      return {
        ...config,
        url,
        method: 'get',
      };
    },
  },
  fields: [
    {
      name: 'platformMeaning',
      type: 'string',
      label: intl.get('hiot.common.model.common.platform').d('云平台'),
    },
    {
      name: 'configName',
      type: 'string',
      label: intl.get('hiot.common.model.common.configName').d('云账户'),
    },
    {
      name: 'upTopic',
      type: 'string',
      label: intl.get('hiot.gatewayManage.model.gatewayManage.reportTopic').d('上报Topic'),
    },
    {
      name: 'downTopic',
      type: 'string',
      label: intl.get('hiot.gatewayManage.model.gatewayManage.receiveTopic').d('下行Topic'),
    },
  ],
});

const gatewayConfigDS = () => ({
  transport: {
    /**
     * 导出网关配置信息
     * @param config
     * @param data 查询参数
     * @returns {{method: string, url: string}}
     */
    read: ({ config, data }) => {
      const { gatewayId } = data;
      return {
        ...config,
        url: `${API_PREFIX}/v1/${organizationId}/gateways/download-access-detail`,
        method: 'get',
        data: { gatewayId },
      };
    },
  },
});

const sendConfigDS = () => ({
  paging: false,
  transport: {
    read: ({ data }) => ({
      url: `${API_PREFIX}/v1/${organizationId}/gateways/configuration-delivery`,
      method: 'get',
      data,
    }),
  },
});

export {
  gatewayManageDS, // 网关管理
  edginkTableDS,
  modbusTableDS,
  subDeviceEdglinkDS, // Edglink子设备详情
  subDeviceModbusDS, // modbusRTU
  subDeviceInfoDS, // 网关详情页面-子设备列表
  registerInfoDS, // 网关注册信息
  gatewayConfigDS, // 导出网关配置以及配置下发用ds
  subDeviceOperatorDS, // 子设备新增编辑发送请求用
  sendConfigDS, // 下发配置DS
  subDeviceTypeDS, // 子设备类型
};
