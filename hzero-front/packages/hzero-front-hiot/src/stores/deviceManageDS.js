/**
 * @Author: guanglong.sun <guanglong.sun@hand-china.com>
 * @Create time: 2019/10/18 4:23 下午
 * @Copyright: Copyright(c) 2019, Hand
 * @Description: 设备管理DS
 */
import moment from 'moment';
import { DataSet } from 'choerodon-ui/pro';

import { DEFAULT_DATETIME_FORMAT } from 'utils/constants';
import { getCurrentOrganizationId, getCurrentTenant, getResponse } from 'utils/utils';
import intl from 'utils/intl';
import { API_HOST, HZERO_HIOT, HZERO_PLATFORM } from 'utils/config';
import notification from 'utils/notification';
import {
  API_PREFIX,
  CONTROL_INT_TYPE,
  DATA_TYPE,
  DEVICE_DEFAULT_CONNECT,
  DEVICE_DEFAULT_STATUS,
  POINT_TYPE,
  STATUS_TYPE,
  VERSION_REG,
} from '@/utils/constants';

const organizationId = getCurrentOrganizationId();
const { tenantId } = getCurrentTenant();

const deviceManageDS = () => ({
  primaryKey: 'thingId',
  name: 'deviceManage',
  autoQuery: false,
  transport: {
    /**
     * 查询设备列表信息
     * @returns {{method: string, url: string}}
     */
    read: ({ config, data }) => {
      const { thingId } = data;
      return {
        ...config,
        url: thingId
          ? `${API_HOST}${HZERO_HIOT}/v1/${organizationId}/things/${thingId}`
          : `${API_HOST}${HZERO_HIOT}/v1/${organizationId}/things`,
        method: 'get',
        transformResponse: (res) => {
          let formatData = {};
          try {
            formatData = JSON.parse(res);
          } catch (e) {
            return e;
          }
          if (getResponse(formatData)) {
            const { thing = {}, thingAttribute = {} } = formatData;
            const { additionInfo = '' } = thingAttribute;
            const { authorize = '' } = thing;
            if (authorize) {
              const other = `${authorize}`;
              formatData = {
                ...formatData,
                ...thing,
                ...thingAttribute,
                ...JSON.parse(`${other}`),
              };
            }
            if (additionInfo) {
              const other = `${additionInfo}`;
              formatData = {
                ...formatData,
                ...thing,
                ...thingAttribute,
                ...JSON.parse(`${other}`),
              };
            }
            return formatData;
          }
        },
      };
    },
    /**
     * 创建设备
     * @param data
     */
    create: ({ data }) => {
      const {
        __id,
        _status,
        authType,
        secret,
        from,
        username,
        password,
        longitude,
        latitude,
        buyingTime,
        equipment,
        manufacturer,
        additionInfo,
        ...other
      } = Array.isArray(data) ? data[0] : {};
      return {
        url: `${API_PREFIX}/v1/${organizationId}/things`,
        method: 'post',
        data: data.length > 0 && {
          thing: {
            ...other,
            connected: DEVICE_DEFAULT_CONNECT,
            status: DEVICE_DEFAULT_STATUS,
            authorize: JSON.stringify({ authType, secret, from, username, password }),
          },
          thingAttribute: {
            manufacturer,
            equipment,
            buyingTime,
            longitude,
            latitude,
            additionInfo,
          },
        },
      };
    },
    /**
     * 删除设备
     * @param data
     * @returns {{method: string, data: *, url: string}}
     */
    destroy: ({ data }) => ({
      url: `${API_PREFIX}/v1/${organizationId}/things`,
      method: 'delete',
      data: data.length > 0 && data.map((record) => record.thingId),
    }),
  },
  queryFields: [
    {
      name: 'thingCode',
      type: 'string',
      label: intl.get('hiot.common.code').d('编码'),
    },
    {
      name: 'thingName',
      type: 'string',
      label: intl.get('hiot.common.name').d('名称'),
    },
    {
      name: 'deviceTemp',
      type: 'object',
      lovCode: 'HIOT.LOV.THING_MODEL',
      lovPara: { tenantId: organizationId },
      label: intl.get('hiot.common.model.device.deviceModel').d('设备模型'),
      ignore: 'always',
    },
    {
      name: 'thingModelId',
      type: 'string',
      bind: 'deviceTemp.thingModelId',
    },
    {
      name: 'connected',
      type: 'single',
      lookupCode: 'HPFM.FLAG',
      label: intl.get('hiot.deviceManage.model.deviceManage.is-online').d('是否在线'),
    },
    {
      name: 'category',
      type: 'single',
      lookupCode: 'HIOT.THING_CATEGORY',
      label: intl.get('hiot.common.device.type').d('设备类别'),
    },
    {
      name: 'gatewayNameLov',
      type: 'object',
      lovCode: 'HIOT.LOV.GATEWAY',
      lovPara: { tenantId: organizationId },
      label: intl.get(`hiot.common.model.common.belongsGateway`).d('所属网关'),
      ignore: 'always',
    },
    {
      name: 'gatewayId',
      type: 'string',
      bind: 'gatewayNameLov.gatewayId',
    },
    {
      name: 'status',
      type: 'single',
      lookupCode: 'HIOT.THING_STATUS',
      label: intl.get('hiot.deviceManage.model.deviceManage.deviceStatus').d('设备状态'),
    },
    {
      name: 'project',
      type: 'object',
      label: intl.get(`hiot.common.model.device.parentDeviceName`).d('所属设备分组'),
      lovCode: 'HIOT.THING_GROUP',
      lovPara: { tenantId: organizationId },
      ignore: 'always',
    },
    {
      name: 'thingGroupId',
      type: 'string',
      bind: 'project.thingGroupId',
    },
  ],
  fields: [
    {
      name: 'thingCode',
      type: 'string',
      label: intl.get('hiot.common.device.code').d('设备编码'),
      required: true,
    },
    {
      name: 'propertyModelCode',
      type: 'string',
      label: intl.get('hiot.common.device.code').d('设备编码'),
    },
    {
      name: 'thingName',
      type: 'intl',
      label: intl.get('hiot.common.device.name').d('设备名称'),
      required: true,
    },
    {
      name: 'connected',
      type: 'string',
      label: intl.get('hiot.deviceManage.model.deviceManage.is-online').d('是否在线'),
    },
    {
      name: 'statusMeaning',
      type: 'string',
      label: intl.get('hiot.deviceManage.model.deviceManage.deviceStatus').d('设备状态'),
    },
    {
      name: 'name',
      type: 'string',
      label: intl.get(`hiot.common.model.device.parentDeviceName`).d('所属设备分组'),
    },
    {
      name: 'project',
      type: 'object',
      label: intl.get(`hiot.common.model.device.parentDeviceName`).d('所属设备分组'),
      lovCode: 'HIOT.THING_GROUP',
      lovPara: { tenantId: organizationId },
      ignore: 'always',
      required: true,
    },
    {
      name: 'thingGroupId',
      type: 'string',
      bind: 'project.thingGroupId',
    },
    {
      name: 'gatewayName',
      type: 'string',
      label: intl.get(`hiot.common.model.common.belongsGateway`).d('所属网关'),
    },
    {
      name: 'thingModelName',
      type: 'string',
      label: intl.get('hiot.common.model.device.deviceModel').d('设备模型'),
    },
    {
      name: 'guid',
      type: 'string',
      label: 'ID',
    },
    {
      name: 'thingModel',
      type: 'object',
      lovCode: 'HIOT.LOV.THING_MODEL',
      textField: 'thingModelName',
      lovPara: { tenantId: organizationId },
      label: intl.get('hiot.common.model.device.deviceModel').d('设备模型'),
    },
    {
      name: 'thingModelId',
      type: 'string',
      bind: 'thingModel.thingModelId',
    },
    {
      name: 'equipment',
      type: 'string',
      label: intl.get('hiot.deviceManage.model.deviceManage.equipmentType').d('设备型号'),
    },
    {
      name: 'categoryMeaning',
      type: 'string',
      label: intl.get('hiot.common.device.type').d('设备类别'),
    },
    {
      name: 'category',
      type: 'string',
      bind: 'thingModel.categoryMeaning',
      label: intl.get('hiot.common.device.type').d('设备类别'),
      ignore: 'always',
    },
    {
      name: 'manufacturer',
      type: 'string',
      label: intl.get('hiot.deviceManage.model.deviceManage.deviceType').d('厂家名称'),
    },
    {
      name: 'buyingTime',
      type: 'dateTime',
      label: intl.get('hiot.deviceManage.model.deviceManage.buyTime').d('购买时间'),
    },
    {
      name: 'description',
      type: 'string',
      label: intl.get('hzero.common.explain').d('说明'),
      maxLength: 100,
    },
    {
      name: 'operation',
      type: 'string',
      label: intl.get(`hzero.common.button.action`).d('操作'),
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
      label: intl.get('hiot.deviceManage.model.deviceManage.authType').d('认证方式'),
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
      label: intl.get('hiot.deviceManage.model.deviceManage.secret').d('密钥'),
      dynamicProps: {
        required({ record }) {
          return record.get('authType') === 'JWT';
        },
      },
    },
    {
      name: 'from',
      type: 'string',
      label: intl.get('hiot.deviceManage.model.deviceManage.from').d('Token携带方式'),
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
      label: intl.get('hiot.deviceManage.model.deviceManage.productKey').d('产品Key'),
    },
    {
      name: 'deviceName',
      type: 'string',
      label: intl.get('hiot.common.device.name').d('设备名称'),
    },
    {
      name: 'deviceSecret',
      type: 'string',
      label: intl.get('hiot.deviceManage.model.deviceManage.deviceSecret').d('设备密钥'),
    },
    {
      name: 'brokerAddress',
      type: 'string',
      label: intl.get('hiot.deviceManage.model.deviceManage.brokerAddress').d('brokerAddress'),
    },
    {
      name: 'clientId',
      type: 'string',
      label: intl.get('hiot.deviceManage.model.deviceManage.clientId').d('clientId'),
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
      label: intl.get('hiot.deviceManage.model.deviceManage.port').d('port'),
    },
    {
      name: 'protocol',
      type: 'string',
      label: intl.get('hiot.deviceManage.model.deviceManage.protocol').d('协议'),
    },
    {
      name: 'username',
      type: 'string',
      label: intl.get('hiot.common.model.common.username').d('用户名'),
    },
    {
      name: 'version',
      type: 'string',
      label: intl.get('hiot.deviceManage.model.deviceManage.version').d('固件版本'),
      required: true,
      pattern: VERSION_REG,
      defaultValidationMessages: {
        patternMismatch: intl
          .get('hiot.common.view.validation.versionValMsg')
          .d('请输入正确的版本号，格式为：X.X.X'),
      },
    },
    {
      name: 'name',
      type: 'string',
      label: intl.get(`hiot.common.model.device.parentDeviceName`).d('所属设备分组'),
    },
    {
      name: 'longitude',
      type: 'number',
      label: intl.get(`hiot.common.model.device.longitude`).d('经度'),
      // label: (
      //   <>
      //     {intl.get(`hiot.common.model.device.longitude`).d('经度')}
      //     <Tooltip
      //       title={intl
      //         .get('hiot.common.view.message.title.longitude.tooptip')
      //         .d('负坐标代表西经，正坐标代表东经')}
      //     >
      //       <Icon type="question-circle" />
      //     </Tooltip>
      //   </>
      // ),
      min: -180,
      max: 180,
    },
    {
      name: 'latitude',
      type: 'number',
      label: intl.get(`hiot.common.model.device.longitude`).d('经度'),
      // label: (
      //   <>
      //     {intl.get(`hiot.common.model.device.latitude`).d('纬度')}
      //     <Tooltip
      //       title={intl
      //         .get('hiot.common.view.message.title.latitude.tooptip')
      //         .d('负坐标代表南纬，正坐标代表北纬')}
      //     >
      //       <Icon type="question-circle" />
      //     </Tooltip>
      //   </>
      // ),
      min: -90,
      max: 90,
    },
    {
      name: 'thingAttribute',
      type: 'object',
    },
  ],
});

const deviceManageSaveDS = () => ({
  transport: {
    /**
     * 更新设备信息
     * @param data
     * @returns {{method: string, data: *, url: string}}
     */
    create: ({ data }) => {
      const {
        thing: {
          thingId,
          latitude,
          longitude,
          thingAttribute,
          thing,
          equipment,
          manufacturer,
          buyingTime,
          additionInfo,
          thingName,
        },
      } = data[0];
      return {
        url: `${API_PREFIX}/v1/${organizationId}/things/${thingId}`,
        method: 'put',
        data: {
          ...data[0],
          thing: {
            ...thing,
            thingName,
            objectVersionNumber: thing.objectVersionNumber,
          },
          thingAttribute: {
            ...thingAttribute,
            additionInfo,
            equipment,
            manufacturer,
            buyingTime,
            latitude,
            longitude,
          },
        },
      };
    },
  },
});

const warnInfoDS = () => ({
  transport: {
    /**
     * 查询告警事件列表数据
     * @param config
     * @param data 查询参数(分页信息)
     * @returns {{method: string, url: string}}
     */
    read: ({ data }) => {
      const { ts, ...params } = data;
      return {
        url: `${API_PREFIX}/v1/${organizationId}/thing-alert-events`,
        method: 'get',
        data: { ...ts, ...params },
      };
    },
  },
  queryFields: [
    {
      name: 'alertCode',
      type: 'string',
      label: intl.get('hiot.common.model.common.alertCode').d('配置编码'),
    },
    {
      name: 'alertLevel',
      type: 'string',
      label: intl.get('hiot.deviceManage.model.deviceManage.alertLevel').d('告警级别'),
      lookupCode: 'HALT.ALERT_LEVEL',
    },
    {
      name: 'recoveredFlag',
      type: 'string',
      label: intl.get('hiot.deviceManage.model.deviceManage.recoveredFlag').d('是否恢复'),
      lookupCode: 'HIOT.ALERT_STATUS',
    },
    {
      name: 'ts',
      type: 'dateTime',
      range: ['eventTimeStart', 'eventTimeEnd'],
      label: intl.get('hiot.deviceManage.model.deviceManage.time').d('事件时间'),
      transformRequest: (value) => {
        if (value) {
          const { eventTimeStart, eventTimeEnd } = value;
          const param = {};
          if (eventTimeStart) {
            param.eventTimeStart = moment(eventTimeStart).format(DEFAULT_DATETIME_FORMAT);
          }
          if (eventTimeEnd) {
            param.eventTimeEnd = moment(eventTimeEnd).format(DEFAULT_DATETIME_FORMAT);
          }
          return param;
        }
      },
    },
  ],
  fields: [
    {
      name: 'thingName',
      type: 'string',
      label: intl.get('hiot.common.device.name').d('设备名称'),
    },
    {
      name: 'guid',
      type: 'string',
      label: intl.get('hiot.common.device.code').d('设备编码'),
    },
    {
      name: 'alertCode',
      type: 'string',
      label: intl.get('hiot.common.model.common.alertCode').d('配置编码'),
    },
    {
      name: 'alertLevelMeaning',
      type: 'string',
      label: intl.get('hiot.iotWarnEvent.model.iotWarn.alertLevel').d('告警级别'),
    },
    {
      name: 'eventTime',
      type: 'string',
      label: intl.get('hiot.iotWarnEvent.model.iotWarn.eventTime').d('事件时间'),
    },
    {
      name: 'recoveredFlagMeaning',
      type: 'string',
      label: intl.get('hiot.iotWarnEvent.model.iotWarnEvent.isRecover').d('是否恢复'),
    },
  ],
});

const warnRuleDS = () => ({
  paging: false,
  transport: {
    /**
     * 查询设备模板对应的预警规则
     * @param config
     * @param data 查询参数(分页信息等)
     * @returns {{method: string, url: string}}
     */
    read: ({ config, data }) => {
      const { thingId } = data;
      return {
        ...config,
        url: thingId
          ? `${API_PREFIX}/v1/${organizationId}/things/predict-rule-tab`
          : `${API_PREFIX}/v1/${organizationId}/thing-models/predict-rule-tab`,
        method: 'get',
        data,
      };
    },
  },
  queryFields: [
    {
      name: 'predictCode',
      type: 'string',
      label: intl.get('hiot.common.code').d('编码'),
    },
    {
      name: 'predictName',
      type: 'string',
      label: intl.get('hiot.common.name').d('名称'),
    },
    {
      name: 'property',
      type: 'object',
      lovCode: 'HIOT.LOV.PROPERTY',
      lovPara: {
        tenantId: organizationId,
        category: 'MEASURING_POINT',
      },
      label: intl.get('hiot.deviceManage.model.deviceManage.relevant.data.point').d('相关数据点'),
      ignore: 'always',
    },
    {
      name: 'propertyId',
      type: 'string',
      bind: 'property.propertyId',
    },
  ],
  fields: [
    {
      name: 'predictCode',
      type: 'string',
      label: intl.get('hiot.common.code').d('编码'),
    },
    {
      name: 'predictName',
      type: 'string',
      label: intl.get('hiot.common.name').d('名称'),
    },
    {
      name: 'formular',
      type: 'string',
      label: intl.get('hzero.common.title.formula').d('公式'),
    },
    {
      name: 'propertyNames',
      type: 'string',
      label: intl.get('hiot.deviceManage.model.deviceManage.relevant.data.point').d('相关数据点'),
    },
    {
      name: 'propertyName',
      type: 'string',
      label: intl.get('hiot.deviceManage.model.deviceManage.relevant.data.point').d('相关数据点'),
    },
    {
      name: 'operation',
      type: 'string',
      label: intl.get(`hzero.common.button.action`).d('操作'),
    },
    {
      name: 'unusual',
      type: 'number',
      label: intl.get('hiot.iotWarnEvent.model.iotWarnEvent.unusual').d('异常'),
    },
    {
      name: 'fault',
      type: 'number',
      label: intl.get('hzero.common.status.abnormal').d('故障'),
    },
    {
      name: 'earlyWarning',
      type: 'number',
      label: intl.get('hiot.deviceManage.model.deviceManage.early.warning').d('预警'),
    },
  ],
});

const dataPointInfoDS = () => ({
  primaryKey: 'propertyId',
  name: 'dataPointInfo',
  transport: {
    /**
     * 查询设备模板对应的数据点信息
     * @param config
     * @param data 查询参数(分页信息等)
     * @returns {{method: string, url: string}}
     */
    read: ({ config, data }) => {
      const { thingId } = data;
      return {
        ...config,
        url: thingId
          ? `${API_PREFIX}/v1/${organizationId}/things/property-tab`
          : `${API_PREFIX}/v1/${organizationId}/thing-models/assign-prop`,
        method: 'get',
      };
    },
  },
  fields: [
    {
      name: 'propertyCode',
      type: 'string',
      label: intl.get('hiot.common.code').d('编码'),
    },
    {
      name: 'guid',
      type: 'string',
      label: intl.get('hiot.deviceManage.model.deviceManage.guidId').d('数据点ID'),
    },
    {
      name: 'propertyModelCode',
      type: 'string',
      label: intl.get('hiot.common.code').d('编码'),
    },
    {
      name: 'propertyName',
      type: 'string',
      label: intl.get('hiot.common.name').d('名称'),
    },
    {
      name: 'propertyModelName',
      type: 'string',
      label: intl.get('hiot.common.name').d('名称'),
    },
    {
      name: 'categoryMeaning',
      type: 'string',
      label: intl.get('hzero.common.category').d('分类'),
    },
    {
      name: 'typeNameMeaning',
      type: 'string',
      label: intl.get('hiot.deviceManage.model.deviceManage.data.type').d('数据类型'),
    },
    {
      name: 'typeMeaning',
      type: 'string',
      label: intl.get('hiot.deviceManage.model.deviceManage.data.type').d('数据类型'),
    },
    {
      name: 'dataTypeMeaning',
      type: 'string',
      label: intl.get('hiot.deviceManage.model.deviceManage.data.type').d('数据类型'),
    },
    {
      name: 'unitCode',
      type: 'string',
      label: intl.get('hiot.deviceManage.model.deviceManage.measure.unit').d('测量单位'),
    },
    {
      name: 'minValue',
      type: 'string',
      label: intl.get('hzero.common.min.value').d('合理最小值'),
    },
    {
      name: 'maxValue',
      type: 'string',
      label: intl.get('hzero.common.max.value').d('合理最大值'),
    },
    {
      name: 'reportInterval',
      type: 'string',
      label: intl.get('hiot.deviceManage.model.deviceManage.collect.interval').d('采集间隔(S)'),
    },
    {
      name: 'operation',
      type: 'string',
      label: intl.get(`hzero.common.button.action`).d('操作'),
    },
  ],
});

const controlParamDS = () => ({
  transport: {
    /**
     * 查询控制参数信息
     * @param config 查询参数(分页信息等)
     * @param data 查询数据
     * @returns {{method: string, url: string}}
     */
    read: ({ config, data }) => ({
      ...config,
      url: `${API_PREFIX}/v1/${organizationId}/thing-monitor/thing-property?controlsParameterFlag=1`,
      method: 'get',
      params: {},
      data,
    }),
    /**
     * 下发控制参数
     * @param data 需下发数据
     * @returns {{method: string, data: [*, *], url: string}}
     */
    update: ({ data, dataSet }) => {
      const { configId } = data[0];
      const { msgTemplateCode, thingId } = dataSet;
      return {
        url: `${API_PREFIX}/v1/${organizationId}/cmd/desired`,
        method: 'post',
        data: data
          .map(({ guid, value }) => ({ [guid]: value }))
          .reduce((sumItem, item) => ({ ...sumItem, ...item })),
        params: { configId, msgTemplateCode, thingId },
      };
    },
  },
  feedback: {
    submitFailed: (error) => {
      if (error && error.failed) {
        notification.error({
          message: error.message,
        });
      }
    },
  },
  fields: [
    {
      name: 'value',
      dynamicProps: ({ record }) => {
        let fieldType = '';
        switch (record.get('dataType')) {
          case DATA_TYPE.DATE_TIME:
            fieldType = 'dateTime';
            break;
          case DATA_TYPE.DATE:
            fieldType = 'date';
            break;
          case DATA_TYPE.NUMBER:
            fieldType = 'number';
            break;
          case DATA_TYPE.BOOL:
            fieldType = 'boolean';
            break;
          case DATA_TYPE.ENUM:
            fieldType = 'string';
            break;
          default:
            fieldType = 'string';
            break;
        }
        return {
          label: record.get('propertyName'),
          type: fieldType,
        };
      },
    },
  ],
});

const historyMonitorDS = () => ({
  paging: false,
  transport: {
    /**
     * 查询历史监控信息
     * @param config 查询参数(分页信息等)
     * @param data 查询数据
     * @returns {{method: string, url: string}}
     */
    read: ({ config, data }) => ({
      ...config,
      url: `${API_PREFIX}/v1/${organizationId}/thing-monitor/property-history`,
      method: 'get',
      data,
    }),
  },
});

const pointMonitorDS = () => ({
  transport: {
    /**
     * 查询测量点信息
     */
    read: ({ config, data }) => ({
      ...config,
      url: `${API_PREFIX}/v1/${organizationId}/thing-monitor/measuring-point-data`,
      method: 'get',
      data,
    }),
  },
});

const mqConfigDS = () => ({
  transport: {
    read: () => ({
      url: `${API_PREFIX}/v1/${organizationId}/thing-monitor/binder-message`,
      method: 'get',
    }),
  },
});

const statusMonitorDS = () => ({
  transport: {
    /**
     * 查询状态点信息
     */
    read: ({ config, data }) => ({
      ...config,
      url: `${API_PREFIX}/v1/${organizationId}/thing-monitor/status`,
      method: 'get',
      data,
    }),
  },
});

const dataPointEditDS = () => ({
  fields: [
    {
      name: 'propertyCode',
      type: 'string',
      label: intl.get('hiot.common.code').d('编码'),
      required: true,
    },
    {
      name: 'guid',
      type: 'string',
      label: 'ID',
    },
    {
      name: 'propertyName',
      type: 'string',
      label: intl.get('hiot.common.name').d('名称'),
      required: true,
    },
    {
      name: 'dataPointType',
      type: 'object',
      lovCode: '',
    },
    {
      name: 'typeNameCode',
      type: 'string',
      label: intl.get('hiot.dataPointTemplate.model.dpt.dataPointType').d('数据点类型'),
      required: true,
      maxLength: 45,
    },
    {
      name: 'typeNameMeaning',
      type: 'string',
      bind: 'dataPointType.description',
    },
    {
      name: 'description',
      type: 'string',
      label: intl.get('hzero.common.explain').d('说明'),
      maxLength: 100,
    },
    {
      name: 'reportInterval',
      type: 'number',
      label: intl.get('hiot.dataPointTemplate.model.dpt.collectInterval').d('采集间隔'),
      dynamicProps: ({ record }) => ({
        required: [STATUS_TYPE, POINT_TYPE].includes(record.get('typeNameMeaning')),
      }),
    },
    {
      name: 'validNumArea',
      type: 'string',
      label: intl.get('hiot.dataPointTemplate.model.dpt.validNumArea').d('合理值范围'),
    },
    {
      name: 'valuePrecision',
      type: 'number',
      label: intl.get('hiot.dataPointTemplate.model.dpt.accuracyDigit').d('精度位数'),
      dynamicProps: ({ record }) => ({
        required: [POINT_TYPE, CONTROL_INT_TYPE].includes(record.get('typeNameMeaning')),
      }),
    },
    {
      name: 'unitCode',
      type: 'string',
      label: intl.get('hiot.dataPointTemplate.model.dpt.unit').d('计量单位'),
      dynamicProps: ({ record }) => ({
        required: [POINT_TYPE, CONTROL_INT_TYPE].includes(record.get('typeNameMeaning')),
      }),
    },
    {
      name: 'warnTemplate',
      type: 'string',
      label: intl.get('hiot.dataPointTemplate.model.dpt.warnTemplate').d('异常告警模板'),
    },
    {
      name: 'typeCode',
      type: 'string',
      label: intl.get('hiot.dataPointTemplate.model.dpt.dataTypeCode').d('数据点类型编码'),
      bind: 'dataPointType.typeCode',
    },
    {
      name: 'typeName',
      type: 'string',
      label: intl.get('hiot.dataPointTemplate.model.dpt.dataTypeName').d('数据点类型名称'),
      bind: 'dataPointType.typeName',
    },
    {
      name: 'categoryMeaning',
      type: 'string',
      label: intl.get('hzero.common.category').d('分类'),
      bind: 'dataPointType.category',
    },
    {
      name: 'dataTypeMeaning',
      type: 'string',
      label: intl.get('hiot.deviceManage.model.deviceManage.data.type').d('数据类型'),
      bind: 'dataPointType.dataType',
    },
    {
      name: 'minValue',
      type: 'number',
      label: intl.get('hzero.common.min.value').d('合理最小值'),
    },
    {
      name: 'maxValue',
      type: 'number',
      label: intl.get('hzero.common.max.value').d('合理最大值'),
    },
  ],
});

const alertRuleDS = () => ({
  selection: false,
  fields: [
    {
      name: 'targetKey',
      type: 'string',
      label: intl.get('hiot.deviceManage.model.deviceManage.targetKey').d('目标标识'),
    },
    {
      name: 'sourceLov',
      type: 'object',
      label: intl.get('hiot.deviceManage.model.deviceManage.sourceKey').d('来源标识'),
      ignore: 'always',
      noCache: true,
      required: true,
      lovCode: 'HIOT.LOV.PROPERTY',
      textField: 'guid',
      dynamicProps: ({ dataSet }) => ({
        lovPara: { tenantId, thingId: dataSet.thingId },
      }),
    },
    {
      name: 'sourceKey',
      type: 'string',
      bind: 'sourceLov.guid',
    },
  ],
  transport: {
    read: ({ data, dataSet }) => {
      const { alertRelId } = data;
      const { refresh } = dataSet;
      return {
        url: `${API_HOST}${HZERO_HIOT}/v1/${organizationId}/things/${
          // eslint-disable-next-line no-nested-ternary
          refresh ? 'alert-mapping/fresh' : alertRelId ? 'thing-mapping-list' : 'alert-mapping-list'
        }`,
        method: 'GET',
      };
    },
  },
});

const alertRuleShowDS = () => ({
  autoQueryAfterSubmit: false,
  queryFields: [
    {
      name: 'alertCode',
      type: 'string',
      label: intl.get('hiot.deviceManage.model.deviceManage.ruleCode').d('规则编码'),
    },
    {
      name: 'alertName',
      type: 'string',
      label: intl.get('hiot.deviceManage.model.deviceManage.alertName').d('规则名称'),
    },
    {
      name: 'alertLevelMeaning',
      type: 'string',
      label: intl.get('hiot.deviceManage.model.deviceManage.alertLevelMeaning').d('告警级别'),
      lookupCode: 'HALT.ALERT_LEVEL',
    },
  ],
  fields: [
    {
      name: 'alertCode',
      type: 'string',
      label: intl.get('hiot.deviceManage.model.deviceManage.ruleCode').d('规则编码'),
    },
    {
      name: 'alertName',
      type: 'string',
      label: intl.get('hiot.deviceManage.model.deviceManage.alertName').d('规则名称'),
    },
    {
      name: 'alertLevelMeaning',
      type: 'string',
      label: intl.get('hiot.deviceManage.model.deviceManage.alertLevelMeaning').d('告警级别'),
    },
    {
      name: 'sourceTypeMeaning',
      type: 'string',
      label: intl.get('hiot.deviceManage.model.deviceManage.sourceTypeCode').d('数据来源'),
    },
    {
      name: 'targetTypeList',
      type: 'object',
      label: intl.get('hiot.deviceManage.model.deviceManage.targetTypeCode').d('通知方式'),
    },
    {
      name: 'itemMappingList',
      type: 'object',
      label: intl.get('hiot.deviceManage.model.deviceManage.itemMappingList').d('相关数据点'),
    },
  ],
  transport: {
    read: ({ config, data }) => ({
      ...config,
      url: `${API_PREFIX}/v1/${organizationId}/things/thing-alert-tab`,
      method: 'get',
      data,
    }),
    update: ({ data }) => {
      const { __id, _status, ...other } = Array.isArray(data) ? data[0] : {};
      return {
        url: `${API_PREFIX}/v1/${organizationId}/thing-alert-rels`,
        method: 'PUT',
        data: other,
      };
    },
    destroy: ({ data }) => {
      const { __id, _status, ...other } = Array.isArray(data) ? data[0] : {};
      return {
        url: `${API_PREFIX}/v1/${organizationId}/thing-alert-rels`,
        method: 'DELETE',
        data: other,
      };
    },
  },
});

const alertRuleServerDS = () => ({
  autoQueryAfterSubmit: false,
  transport: {
    create: ({ data }) => {
      const { __id, _status, alertLov, ...other } = Array.isArray(data) ? data[0] : {};
      return {
        url: `${API_PREFIX}/v1/${organizationId}/thing-alert-rels`,
        method: 'POST',
        data: other,
      };
    },
  },
});

const editRuleDrawerDS = () => ({
  fields: [
    {
      name: 'alertLov',
      type: 'object',
      label: intl.get('hiot.deviceManage.model.deviceManage.alertLov').d('预警配置'),
      lovCode: 'HALT.ALERT',
      ignore: 'always',
      lovPara: { tenantId },
      noCache: true,
      required: true,
    },
    {
      name: 'alertCode',
      bind: 'alertLov.alertCode',
    },
    {
      name: 'alertName',
      bind: 'alertLov.alertName',
    },
    {
      name: 'alertLevel',
      bind: 'alertLov.alertLevel',
    },
    {
      name: 'alertLevelMeaning',
      bind: 'alertLov.alertLevelMeaning',
    },
    {
      name: 'alertId',
      bind: 'alertLov.alertId',
    },
  ],
});

const addDataPointDrawerDS = () => ({
  autoCreate: false,
  autoQuery: false,
  selection: 'multiple',
  cacheSelection: true,
  primaryKey: 'propertyModelId',
  queryFields: [
    {
      name: 'propertyModelCode',
      type: 'string',
      label: intl.get('hiot.common.code').d('编码'),
    },
    {
      name: 'propertyModelName',
      type: 'string',
      label: intl.get('hiot.common.name').d('名称'),
    },
  ],
  fields: [
    {
      name: 'propertyModelCode',
      type: 'string',
      label: intl.get('hiot.common.code').d('编码'),
    },
    {
      name: 'propertyModelName',
      type: 'string',
      label: intl.get('hiot.common.name').d('名称'),
    },
  ],
  transport: {
    read: () => ({
      url: `${API_PREFIX}/v1/${organizationId}/thing-models/properties`,
      method: 'GET',
    }),
  },
});
const addDataPointDS = () => ({
  autoCreate: false,
  autoQuery: false,
  autoQueryAfterSubmit: false,
  transport: {
    create: ({ data, dataSet }) => {
      const { propertyModelIds } = Array.isArray(data) ? data[0] : {};
      return {
        url: `${API_PREFIX}/v1/${organizationId}/things/property`,
        method: 'POST',
        data: propertyModelIds,
        params: dataSet.queryParameter,
      };
    },
  },
});

// 下发模板
const issueTemplateDS = () => ({
  autoCreate: true,
  fields: [
    {
      name: 'templateLov',
      type: 'object',
      required: true,
      lovCode: 'HIOT.MSG_TEMPLATE',
      lovPara: { tenantId: organizationId, templateTypeLike: 'DOWN%PROPERTY' },
      ignore: 'always',
      noCache: true,
      label: intl.get('hiot.deviceManage.model.deviceManage.msgTemplateCode').d('下发模板'),
    },
    {
      name: 'msgTemplateCode',
      type: 'string',
      bind: 'templateLov.templateCode',
    },
  ],
});

// 表单配置
const formConfigDS = () => ({
  paging: false,
  queryParameter: {
    formCode: 'HIOT.THING.ADDITION_INFO',
  },
  transport: {
    /**
     * 表单配置
     */
    read: () => {
      const url = `${HZERO_PLATFORM}/v1/${organizationId}/form-lines/header-code`;
      return {
        url,
        method: 'GET',
        params: {},
      };
    },
  },
});

export {
  deviceManageDS, // 设备管理
  warnInfoDS, // 告警情况
  warnRuleDS, // 告警规则
  addDataPointDS, // 添加数据点
  addDataPointDrawerDS, // 添加数据点模态框
  dataPointInfoDS, // 数据点信息
  controlParamDS, // 设备详情-控制参数
  historyMonitorDS, // 设备详情-历史监控
  pointMonitorDS, // 设备详情-测量点
  statusMonitorDS, // 设备详情-状态点
  dataPointEditDS, // 设备编辑-数据点编辑
  mqConfigDS, // 消息队列配置查询
  deviceManageSaveDS, // 设备编辑保存
  alertRuleDS, // 新建/编辑预警规则表格DS
  alertRuleShowDS, // 编辑页的展示预警规则表格
  editRuleDrawerDS, // 新建/编辑预警规则模态框内的表单
  alertRuleServerDS, // 发送保存新建预警规则
  issueTemplateDS,
  formConfigDS,
};
