/**
 * 告警规则配置 - DS
 * @date: 2020-5-18
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2020, Hand
 */

import { DataSet } from 'choerodon-ui/pro';
import intl from 'utils/intl';
import { CODE } from 'utils/regExp';
import { isTenantRoleLevel, getCurrentOrganizationId, getCurrentTenant } from 'utils/utils';
import { HZERO_ALT, HZERO_PLATFORM } from 'utils/config';

const organizationId = getCurrentOrganizationId();
const { tenantId } = getCurrentTenant();
const isTenant = isTenantRoleLevel();
const apiPrefix = isTenant ? `${HZERO_ALT}/v1/${organizationId}` : `${HZERO_ALT}/v1`;

const alertRuleDS = () => ({
  autoQuery: true,
  transport: {
    read: ({ params, data }) => {
      return {
        url: `${apiPrefix}/alerts`,
        params: { ...params, ...data },
        method: 'get',
      };
    },
    create: ({ data }) => {
      return {
        url: `${apiPrefix}/alerts`,
        data: data[0],
        method: 'post',
      };
    },
    update: ({ data }) => {
      return {
        url: `${apiPrefix}/alerts`,
        data: data[0],
        method: 'put',
      };
    },
    destroy: ({ data }) => {
      return {
        url: `${apiPrefix}/alerts`,
        data,
        method: 'delete',
      };
    },
  },
  fields: [
    {
      name: 'alertCode',
      label: intl.get('halt.common.model.common.alertCode').d('告警代码'),
      type: 'string',
      maxLength: 30,
      required: true,
      pattern: CODE,
      defaultValidationMessages: {
        patternMismatch: intl
          .get('hzero.common.validation.code')
          .d('大小写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
      },
    },
    {
      name: 'alertName',
      label: intl.get('halt.common.model.common.alertName').d('告警名称'),
      type: 'string',
      maxLength: 255,
      required: true,
    },
    {
      name: 'alertLevel',
      label: intl.get('halt.alertRule.model.alertRule.alertLevel').d('告警级别'),
      type: 'string',
      lookupCode: 'HALT.ALERT_LEVEL',
      required: true,
      lookupAxiosConfig: ({ lookupCode }) => ({
        method: 'get',
        url: `${HZERO_PLATFORM}/v1/${organizationId}/lovs/data?lovCode=${lookupCode}`,
        transformResponse(data) {
          let handledData = [];
          if (data) {
            handledData = data;
            if (typeof data === 'string') {
              handledData = JSON.parse(data);
            }
          }
          handledData = handledData.filter((item) => item.value !== 'NORMAL');
          return handledData;
        },
      }),
    },
    {
      name: 'alertSourceTypeMeaning',
      label: intl.get('halt.alertRule.model.alertRule.alertSourceType').d('数据来源'),
      type: 'string',
      ignore: 'always',
    },
    {
      name: 'alertTargetType',
      label: intl.get('halt.alertRule.model.alertRule.alertTargetType').d('通知方式'),
      type: 'string',
      lookupCode: 'HALT.ALERT_TARGET_TYPE',
      ignore: 'always',
    },
    {
      name: 'status',
      type: 'string',
      lookupCode: 'HALT.STARTUP_STATUS',
      label: intl.get('halt.alertRule.model.alertRule.status').d('调度状态'),
    },
    {
      name: 'errorMsg',
      type: 'string',
      label: intl.get('halt.alertRule.model.alertRule.errorMsg').d('调度信息'),
    },
    {
      name: 'enabledFlag',
      label: intl.get('hzero.common.button.enable').d('启用'),
      type: 'boolean',
      trueValue: 1,
      falseValue: 0,
      defaultValue: 1,
    },
    {
      name: 'remark',
      label: intl.get('hzero.common.button.explain').d('说明'),
      type: 'string',
    },
  ],
  queryFields: [
    {
      name: 'alertCode',
      type: 'string',
      label: intl.get('halt.common.model.common.alertCode').d('告警代码'),
      maxLength: 30,
    },
    {
      name: 'alertName',
      type: 'string',
      label: intl.get('halt.common.model.common.alertName').d('告警名称'),
      maxLength: 255,
    },
    {
      name: 'alertSourceType',
      label: intl.get('halt.alertRule.model.alertRule.alertSourceType').d('数据来源'),
      type: 'string',
      options: new DataSet({
        selection: 'single',
        data: [
          {
            value: 'DIRECT',
            meaning: intl.get('halt.alertRule.model.alertRule.direct').d('直连'),
          },
          {
            value: 'DATASET',
            meaning: intl.get('halt.alertRule.model.alertRule.dataset').d('数据集'),
          },
          {
            value: 'INFRASTRUCTURE',
            meaning: intl.get('halt.alertRule.model.alertRule.infrastructure').d('基础设施'),
          },
          {
            value: 'APPLICATION',
            meaning: intl.get('halt.alertRule.model.alertRule.application').d('应用中间件'),
          },
          {
            value: 'LOGGING',
            meaning: intl.get('halt.alertRule.model.alertRule.logging').d('应用日志'),
          },
        ],
      }),
    },
  ],
});

// 数据处理表格DS
const dataHandleTableDS = () => ({
  selection: false,
  transport: {
    read: ({ params, data }) => {
      const { alertSourceId, isRefresh } = data;
      return {
        url:
          isRefresh === 'false'
            ? `${apiPrefix}/alert-rule-param-maps/pull`
            : `${apiPrefix}/alert-rule-param-maps/${alertSourceId}/reload`,
        params: { ...params, ...data, alertSourceId: '', isRefresh: '' },
        data: {},
        method: isRefresh === 'false' ? 'get' : 'post',
      };
    },
  },
  fields: [
    {
      name: 'sourceKey',
      type: 'string',
      label: intl.get('halt.alertRule.model.alertRule.sourceKey').d('来源标识'),
    },
    {
      name: 'targetKey',
      type: 'string',
      label: intl.get('halt.alertRule.model.alertRule.targetKey').d('目标标识'),
    },
  ],
});

// 标签/注解表格DS
const triggerTableDS = () => ({
  paging: false,
  fields: [
    {
      name: 'addonType',
      type: 'string',
      required: true,
    },
    {
      name: 'addonCode',
      unique: true,
      type: 'string',
      required: true,
      validator: (value) => {
        const pattern = /^[a-zA-Z_][a-zA-Z0-9_]*$/;
        if (!pattern.test(value)) {
          return intl.get('halt.alertRule.view.message.addonCode.warning').d('目标标识');
        }
      },
    },
    {
      name: 'addonValue',
      type: 'string',
      required: true,
    },
  ],
});

// 目标DrawerDs
const targetDS = (alertId) => ({
  transport: {
    read: () => {
      return {
        url: `${apiPrefix}/alert-targets/${alertId}`,
        method: 'GET',
        params: {},
      };
    },
    submit: ({ data }) => {
      const {
        apiCallback,
        apiSignKey,
        mqTopic,
        msgSendConfigCode,
        msgReceiverCode,
        alertRouteType,
        alertTargetType,
        ...other
      } = data[0];
      let initData = {
        apiCallback: null,
        apiSignKey: null,
        mqTopic: null,
        msgSendConfigCode: null,
        msgReceiverCode: null,
        alertTargetType: null,
        alertId,
        alertRouteType,
        ...other,
      };
      if (alertRouteType === 'SIMPLE') {
        initData = { ...initData, alertTargetType };
        const nextAlertTargetType = alertTargetType.split(',');
        const apiTab = { apiCallback, apiSignKey };
        const mqTab = { mqTopic };
        const msgTab = { msgSendConfigCode, msgReceiverCode };
        if (nextAlertTargetType.includes('API')) {
          initData = { ...initData, ...apiTab };
        }
        if (nextAlertTargetType.includes('MQ')) {
          initData = { ...initData, ...mqTab };
        }
        if (nextAlertTargetType.includes('MSG')) {
          initData = { ...initData, ...msgTab };
        }
      }

      return {
        url: `${apiPrefix}/alert-targets`,
        method: 'POST',
        data: initData,
      };
    },
  },
  fields: [
    {
      name: 'alertRouteType',
      type: 'string',
      label: intl.get('halt.alertRule.model.alertRule.alertRouteType').d('路由方式'),
      lookupCode: 'HALT.ALERT_ROUTE_TYPE',
      defaultValue: 'SIMPLE',
    },
    {
      name: 'alertTargetType',
      lookupCode: 'HALT.TARGET_TYPE',
      multiple: ',',
      label: intl.get('halt.alertRule.model.alertRule.alertTargetType').d('通知方式'),
    },
    {
      name: 'apiCallback',
      type: 'string',
      label: intl.get('halt.alertRule.model.alertRule.apiCallback').d('API回调路径'),
      maxLength: 150,
      dynamicProps: {
        required: ({ record }) => {
          const { alertTargetType, alertRouteType } = record.toData();
          return alertTargetType.includes('API') && alertRouteType === 'SIMPLE';
        },
      },
    },
    {
      name: 'apiSignKey',
      type: 'string',
      label: intl.get('halt.alertRule.model.alertRule.apiSignKey').d('回调签名key'),
      maxLength: 150,
      dynamicProps: {
        required: ({ record }) => {
          const { alertTargetType, alertRouteType } = record.toData();
          return alertTargetType.includes('API') && alertRouteType === 'SIMPLE';
        },
      },
    },
    {
      name: 'mqTopicLov',
      type: 'object',
      label: intl.get('halt.alertRule.model.alertRule.mqTopic').d('接收topic'),
      lovCode: 'HALT.ALERT_TARGET_EVENT',
      ignore: 'always',
      dynamicProps: {
        required: ({ record }) => {
          const { alertTargetType, alertRouteType } = record.toData();
          return alertTargetType.includes('MQ') && alertRouteType === 'SIMPLE';
        },
      },
    },
    {
      name: 'mqTopic',
      type: 'string',
      label: intl.get('halt.alertRule.model.alertRule.mqTopic').d('接收topic'),
      bind: 'mqTopicLov.eventCode',
    },
    {
      name: 'sendConfigLov',
      type: 'object',
      label: intl.get('halt.alertRule.model.alertRule.sendConfig').d('发送配置'),
      lovCode: 'HALT.MSG_SEND_CONFIG',
      lovPara: { tenantId },
      ignore: 'always',
      noCache: true,
      dynamicProps: {
        required: ({ record }) => {
          const { alertTargetType, alertRouteType } = record.toData();
          return alertTargetType.includes('MSG') && alertRouteType === 'SIMPLE';
        },
      },
    },
    {
      name: 'msgSendConfigCode',
      type: 'string',
      bind: 'sendConfigLov.messageCode',
    },
    {
      name: 'msgSendConfigName',
      type: 'string',
      bind: 'sendConfigLov.messageName',
      ignore: 'always',
    },
    {
      name: 'receiveGroupLov',
      type: 'object',
      label: intl.get('halt.alertRule.model.alertRule.receiveGroup').d('接收组'),
      lovCode: 'HALT.MSG_RECEIVER_GROUP',
      ignore: 'always',
      lovPara: { tenantId },
      noCache: true,
      dynamicProps: {
        required: ({ record }) => {
          const { alertTargetType, alertRouteType } = record.toData();
          return alertTargetType.includes('MSG') && alertRouteType === 'SIMPLE';
        },
      },
    },
    {
      name: 'msgReceiverCode',
      type: 'string',
      bind: 'receiveGroupLov.typeCode',
    },
    {
      name: 'msgReceiverName',
      bind: 'receiveGroupLov.typeName',
      ignore: 'always',
    },
  ],
});

// 来源DrawerDs
const sourceDS = (alertId) => ({
  transport: {
    read: () => {
      return {
        url: `${apiPrefix}/alert-sources/${alertId}`,
        params: {},
        method: 'get',
      };
    },
    submit: ({ data }) => {
      return {
        url: `${apiPrefix}/alert-sources`,
        data: data[0],
        method: 'post',
      };
    },
  },
  fields: [
    {
      name: 'alertId',
      type: 'string',
    },
    {
      name: 'alertSourceId',
      type: 'string',
    },
    {
      name: 'isRefresh',
      type: 'string',
      defaultValue: 'false',
    },
    {
      name: 'alertSourceType',
      type: 'string',
      defaultValue: 'DIRECT',
      lookupCode: 'HALT.ALERT_SOURCE_TYPE',
      label: intl.get('halt.alertRule.model.alertRule.alertSourceType').d('数据来源'),
      required: true,
    },
    {
      name: 'ruleLov',
      type: 'object',
      lovPara: { tenantId },
      lovCode: 'HALT.ALERT_RULE',
      label: intl.get('halt.alertRule.model.alertRule.alertRuleCode').d('规则代码'),
      ignore: 'always',
    },
    {
      name: 'alertRuleCode',
      type: 'string',
      bind: 'ruleLov.ruleCode',
    },
    {
      name: 'alertRuleName',
      type: 'string',
      bind: 'ruleLov.ruleName',
      ignore: 'always',
    },
    {
      name: 'autoRecoverFlag',
      type: 'boolean',
      label: intl.get('halt.alertRule.model.alertRule.autoRecoverFlag').d('告警恢复'),
      trueValue: 1,
      falseValue: 0,
      defaultValue: 0,
    },
    {
      name: 'alertAddonList', // 来源标签列表
      type: 'object',
    },
    {
      name: 'upgradeFlag',
      label: intl.get('halt.alertRule.model.alertRule.upgradeFlag').d('告警升级'),
      type: 'boolean',
      trueValue: 1,
      falseValue: 0,
      defaultValue: 0,
    },
    {
      name: 'alertRulePrometheus',
      type: 'object', // 应用中间件
    },
    {
      name: 'alertDataset',
      type: 'object',
    },
  ],
});

// 数据集DS
const dataSetDS = () => ({
  fields: [
    {
      name: 'datasetLov',
      type: 'object',
      label: intl.get('halt.alertRule.model.alertRule.datasetCode').d('数据集代码'),
      lovCode: 'HALT.DATASET',
      lovPara: { tenantId },
      valueField: 'datasetCode',
      noCache: true,
      required: true,
      ignore: 'always',
    },
    {
      name: 'datasetCode',
      type: 'string',
      bind: 'datasetLov.datasetCode',
    },
    {
      name: 'datasetName',
      type: 'string',
      bind: 'datasetLov.datasetName',
      ignore: 'always',
    },
    {
      name: 'startTime',
      type: 'string',
      max: 'endTime',
      label: intl.get('halt.alertRule.model.alertRule.startTime').d('周期开始时间'),
    },
    {
      name: 'endTime',
      type: 'string',
      min: 'startTime',
      label: intl.get('halt.alertRule.model.alertRule.endTime').d('周期结束时间'),
    },
    {
      name: 'intervalType',
      type: 'string',
      lookupCode: 'HSDR.REQUEST.INTERVAL_TYPE',
      label: intl.get('halt.alertRule.model.alertRule.intervalType').d('间隔类型'),
    },
    {
      name: 'intervalNumber',
      type: 'number',
      label: intl.get('halt.alertRule.model.alertRule.intervalNumber').d('间隔大小'),
      min: 0,
      step: 1,
      dynamicProps: {
        max: ({ record }) => {
          const { intervalType } = record.toData();
          let max;
          switch (intervalType) {
            case 'DAY':
              max = 31;
              break;
            case 'HOUR':
              max = 23;
              break;
            case 'MINUTE':
              max = 59;
              break;
            case 'SECOND':
              max = 59;
              break;
            default:
              break;
          }
          return max;
        },
      },
    },
    {
      name: 'intervalHour',
      type: 'number',
      label: intl.get('halt.alertRule.model.alertRule.intervalHour').d('固定间隔-时'),
      min: 0,
      max: 23,
      step: 1,
      dynamicProps: {
        required: ({ record }) => {
          return record && record.get('intervalType') === 'DAY';
        },
      },
    },
    {
      name: 'intervalMinute',
      type: 'number',
      label: intl.get('halt.alertRule.model.alertRule.intervalMinute').d('固定间隔-分'),
      min: 0,
      max: 59,
      step: 1,
      dynamicProps: {
        required: ({ record }) => {
          return record && ['DAY', 'HOUR'].includes(record.get('intervalType'));
        },
      },
    },
    {
      name: 'intervalSecond',
      type: 'number',
      label: intl.get('halt.alertRule.model.alertRule.intervalSecond').d('固定间隔-秒'),
      min: 0,
      max: 59,
      step: 1,
      dynamicProps: {
        required: ({ record }) => {
          return record && ['DAY', 'HOUR', 'MINUTE'].includes(record.get('intervalType'));
        },
      },
    },
    {
      name: 'alertDatasetParams',
      type: 'object',
    },
  ],
});

// 数据集下的参数信息DS
const paramTableDS = () => ({
  transport: {
    read: ({ params, data }) => {
      const { alertDatasetId, isRefreshParam } = data;
      return {
        url: isRefreshParam
          ? `${apiPrefix}/alert-dataset-params/${alertDatasetId}/reload`
          : `${apiPrefix}/alert-dataset-params/pull`,
        params: { ...params, ...data, alertDatasetId: '', isRefreshParam: '' },
        data: {},
        method: isRefreshParam ? 'post' : 'get',
      };
    },
  },
  selection: false,
  fields: [
    {
      name: 'paramName',
      type: 'string',
      label: intl.get('halt.alertRule.model.alertRule.paramName').d('参数名'),
    },
    {
      name: 'paramType',
      type: 'string',
      lookupCode: 'HRPT.PARAM_DATA_TYPE',
      label: '参数类型',
    },
    {
      name: 'paramValue',
      type: 'string',
      label: intl.get('halt.alertRule.model.alertRule.paramValue').d('默认值'),
    },
  ],
});

// 基础设施DS
const infrastructureDS = () => ({
  // autoCreate: true,
  fields: [
    // 基础设施告警相关字段
    {
      name: 'ruleTargetType',
      label: intl.get('halt.alertRule.model.alertRule.ruleTargetType').d('应用目标'),
      type: 'string',
      defaultValue: 'HOST',
      lookupCode: 'HALT.ALERT_ZABBIX_TARGET',
      labelWidth: 119,
      required: true,
    },
    {
      name: 'hostLov',
      label: intl.get('halt.alertRule.model.alertRule.host').d('主机名称'),
      type: 'object',
      lovCode: 'HALT.ALERT_HOST',
      ignore: 'always',
      dynamicProps: {
        required: ({ dataSet }) => {
          if (dataSet.current && dataSet.current.get('ruleTargetType') === 'HOST') {
            return true;
          } else {
            return false;
          }
        },
      },
    },
    {
      name: 'templateLov',
      label: intl.get('halt.alertRule.model.alertRule.template').d('模板名称'),
      type: 'object',
      lovCode: 'HALT.ALERT_TEMPLATE',
      ignore: 'always',
      lovPara: { includeZeroTenant: false },
      dynamicProps: {
        required: ({ dataSet }) => {
          if (dataSet.current && dataSet.current.get('ruleTargetType') === 'TEMPLATE') {
            return true;
          } else {
            return false;
          }
        },
      },
    },
    {
      name: 'zabbixHostId',
      type: 'number',
      dynamicProps: {
        bind: ({ dataSet }) => {
          if (dataSet.current && dataSet.current.get('ruleTargetType') === 'HOST') {
            return 'hostLov.hostid';
          } else {
            return 'templateLov.templateid';
          }
        },
      },
    },
    {
      name: 'hostName',
      type: 'string',
      ignore: 'always',
      dynamicProps: {
        bind: ({ dataSet }) => {
          if (dataSet.current && dataSet.current.get('ruleTargetType') === 'HOST') {
            return 'hostLov.host';
          } else {
            return 'templateLov.host';
          }
        },
      },
    },
    {
      name: 'alertExpression',
      type: 'string',
      label: intl.get('halt.alertRule.model.alertRule.alertExpression').d('Zabbix告警表达式'),
      required: true,
    },
  ],
});

// 应用中间件DS
const applicationDS = () => ({
  // autoCreate: true,
  fields: [
    {
      name: 'forDuration',
      label: intl.get('halt.alertRule.model.alertRule.forDuration').d('最近持续间隔'),
      labelWidth: 118,
      type: 'number',
      step: 1,
      min: 0,
    },
    {
      name: 'durationUnit',
      label: intl.get('halt.alertRule.model.alertRule.forDuration').d('告警配置单位'),
      labelWidth: 118,
      type: 'string',
      required: true,
      lookupCode: 'HALT.ALERT_PROM_UNIT',
    },
    {
      name: 'promQl',
      label: intl.get('halt.alertRule.model.alertRule.promQl').d('PromQL表达式'),
      type: 'string',
      required: true,
    },
  ],
});

// 日志告警相关字段
const loggingDS = () => ({
  // autoCreate: true,
  fields: [
    {
      name: 'logAlertType',
      label: intl.get('halt.alertRule.model.alertRule.logAlertType').d('触发规则类型'),
      labelWidth: 118,
      type: 'string',
      lookupCode: 'HALT.LOG_ALERT_TYPE',
      required: true,
    },
    {
      name: 'timeframe',
      label: intl.get('halt.alertRule.model.alertRule.timeframe').d('时间窗口'),
      type: 'number',
      step: 1,
      min: 0,
      required: true,
    },
    {
      name: 'indexPattern',
      label: intl.get('halt.alertRule.model.alertRule.indexPattern').d('日志索引'),
      type: 'string',
      required: true,
      lookupUrl: `${apiPrefix}/alerts/elastic/indexs`,
      lookupAxiosConfig: {
        transformResponse(data) {
          let nextData;
          try {
            nextData = JSON.parse(data);
            nextData = nextData.map((item, index) => ({
              value: item,
              meaning: item,
              orderSeq: index,
            }));
            return nextData;
          } catch (err) {
            return data;
          }
        },
      },
    },
    {
      name: 'timestampField',
      label: intl.get('halt.alertRule.model.alertRule.timestampField').d('索引中时间字段'),
      type: 'string',
      lookupCode: 'HALT.LOG_INDEX_FIELD',
    },
    {
      name: 'timestampType',
      label: intl.get('halt.alertRule.model.alertRule.timestampType').d('时间字段类型'),
      type: 'string',
      lookupCode: 'HALT.LOG_TIMESTAMP_TYPE',
    },
    {
      name: 'timestampFormat',
      label: intl.get('halt.alertRule.model.alertRule.timestampFormat').d('自定义时间类型'),
      type: 'string',
      dynamicProps: {
        ignore: ({ dataSet }) => {
          if (dataSet.current.get('timestampType') === 'custom') {
            return 'never';
          } else {
            return 'always';
          }
        },
      },
    },
    {
      name: 'compareKey',
      label: intl.get('halt.alertRule.model.alertRule.compareKey').d('比较索引字段'),
      type: 'string',
      dynamicProps: {
        lookupAxiosConfig: ({ record }) => {
          if (record.get('indexPattern')) {
            return {
              url: `${apiPrefix}/alerts/elastic/indexs/fields`,
              params: {
                indexName: record.get('indexPattern'),
              },
              transformResponse(data) {
                let nextData;
                try {
                  nextData = JSON.parse(data);
                  nextData = nextData.map((item, index) => ({
                    value: item,
                    meaning: item,
                    orderSeq: index,
                  }));
                  return nextData;
                } catch (err) {
                  return data;
                }
              },
            };
          }
        },
        required: ({ dataSet }) => {
          if (['blacklist', 'whitelist'].includes(dataSet.current.get('logAlertType'))) {
            return true;
          } else {
            return false;
          }
        },
        ignore: ({ dataSet }) => {
          if (['blacklist', 'whitelist'].includes(dataSet.current.get('logAlertType'))) {
            return 'never';
          } else {
            return 'always';
          }
        },
      },
    },
    {
      name: 'compareValueList',
      labelWidth: 118,
      type: 'string',
      multiple: ',',
      dynamicProps: {
        required: ({ dataSet }) => {
          if (['blacklist', 'whitelist'].includes(dataSet.current.get('logAlertType'))) {
            return true;
          } else {
            return false;
          }
        },
        ignore: ({ dataSet }) => {
          if (['blacklist', 'whitelist'].includes(dataSet.current.get('logAlertType'))) {
            return 'never';
          } else {
            return 'always';
          }
        },
      },
      // validator: value => {
      //   const pattern = /^[A-Za-z0-9][A-Za-z0-9-_.]*$/;
      //   if (!pattern.test(value)) {
      //     return '请输入字母及数字，只能以字母或数字开头，可包含“-”、“_”、“.”';
      //   }
      // },
    },
    {
      name: 'ignoreNull',
      label: intl.get('halt.alertRule.model.alertRule.ignoreNull').d('忽略无索引字段'),
      type: 'boolean',
      trueValue: 1,
      falseValue: 0,
      defaultValue: 1,
      dynamicProps: {
        ignore: ({ dataSet }) => {
          if (dataSet.current.get('logAlertType') === 'whitelist') {
            return 'never';
          } else {
            return 'always';
          }
        },
      },
    },
    {
      name: 'filterDsl',
      label: intl.get('halt.alertRule.model.alertRule.filterDsl').d('ES DSL表达式'),
      type: 'string',
      validator: (value) => {
        if (typeof value === 'string') {
          try {
            const obj = JSON.parse(value);
            if (typeof obj !== 'object' && obj) {
              return intl
                .get('halt.alertRule.view.message.filterDsl.warning')
                .d('请输入正确的日志表达式');
            }
          } catch (e) {
            return intl
              .get('halt.alertRule.view.message.filterDsl.warning')
              .d('请输入正确的日志表达式');
          }
        }
      },
      dynamicProps: {
        required: ({ dataSet }) => {
          if (
            ['frequency', 'spike'].includes(dataSet.current.get('alertRuleLogging.logAlertType'))
          ) {
            return true;
          } else {
            return false;
          }
        },
        ignore: ({ dataSet }) => {
          if (
            ['frequency', 'spike'].includes(dataSet.current.get('alertRuleLogging.logAlertType'))
          ) {
            return 'never';
          } else {
            return 'always';
          }
        },
      },
    },
    {
      name: 'numEvents',
      label: intl.get('halt.alertRule.model.alertRule.numEvents').d('超过'),
      type: 'number',
      step: 1,
      min: 0,
      dynamicProps: {
        required: ({ dataSet }) => {
          if (dataSet.current.get('logAlertType') === 'frequency') {
            return true;
          } else {
            return false;
          }
        },
        ignore: ({ dataSet }) => {
          if (dataSet.current.get('logAlertType') === 'frequency') {
            return 'never';
          } else {
            return 'always';
          }
        },
      },
    },
    {
      name: 'spikeType',
      label: intl.get('halt.alertRule.model.alertRule.spikeType').d('环比上一时间窗口'),
      labelWidth: 118,
      type: 'string',
      lookupCode: 'HALT.LOG_SPIKE_TYPE',
      dynamicProps: {
        required: ({ dataSet }) => {
          if (dataSet.current.get('logAlertType') === 'spike') {
            return true;
          } else {
            return false;
          }
        },
        ignore: ({ dataSet }) => {
          if (dataSet.current.get('logAlertType') === 'spike') {
            return 'never';
          } else {
            return 'always';
          }
        },
      },
    },
    {
      name: 'spikeHeight',
      label: intl.get('halt.alertRule.model.alertRule.spikeHeight').d('倍次'),
      type: 'number',
      step: 1,
      min: 0,
      dynamicProps: {
        required: ({ dataSet }) => {
          if (dataSet.current.get('logAlertType') === 'spike') {
            return true;
          } else {
            return false;
          }
        },
        ignore: ({ dataSet }) => {
          if (dataSet.current.get('logAlertType') === 'spike') {
            return 'never';
          } else {
            return 'always';
          }
        },
      },
    },
    {
      name: 'thresholdRef',
      label: intl.get('halt.alertRule.model.alertRule.thresholdRef').d('上一时间窗口至少'),
      type: 'number',
      step: 1,
      min: 0,
      dynamicProps: {
        ignore: ({ dataSet }) => {
          if (dataSet.current.get('logAlertType') === 'spike') {
            return 'never';
          } else {
            return 'always';
          }
        },
      },
    },
    {
      name: 'thresholdCur',
      label: intl.get('halt.alertRule.model.alertRule.thresholdCur').d('此时间窗口至少'),
      type: 'number',
      step: 1,
      min: 0,
      dynamicProps: {
        ignore: ({ dataSet }) => {
          if (dataSet.current.get('logAlertType') === 'spike') {
            return 'never';
          } else {
            return 'always';
          }
        },
      },
    },
  ],
});

export {
  alertRuleDS,
  targetDS,
  sourceDS,
  triggerTableDS,
  dataHandleTableDS,
  dataSetDS,
  paramTableDS,
  infrastructureDS,
  applicationDS,
  loggingDS,
};
