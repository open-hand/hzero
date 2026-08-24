/**
 * @since 2019-12-03
 * @author WT <tao13.wang@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import intl from 'utils/intl';
import { HZERO_ADM } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';

const organizationId = getCurrentOrganizationId();

// API管理查询from
const formDS = () => {
  return {
    fields: [
      {
        name: 'urlPattern',
        type: 'string',
        label: intl.get('hadm.apiLimit.model.apiLimit.urlPattern').d('匹配规则'),
        help: intl
          .get('hadm.apiLimit.model.apiLimit.urlPatternExample')
          .d('匹配规则书写格式：/** 、 /test/** 、 /test*/** 以及 /test*'),
        required: true,
      },
      {
        name: 'timeWindowSize',
        type: 'number',
        max: 2147483647,
        label: intl.get('hadm.apiLimit.model.apiLimit.timeWindowOfSize').d('时间窗口大小'),
        required: true,
      },
    ],
    transport: {
      create: (config) => {
        const { data, params } = config;
        const url = isTenantRoleLevel()
          ? `${HZERO_ADM}/v1/${organizationId}/api-monitor-rules`
          : `${HZERO_ADM}/v1/api-monitor-rules`;
        return {
          data: data[0],
          params,
          url,
          method: 'POST',
        };
      },
    },
  };
};

function tableDS() {
  return {
    autoQuery: true,
    dataKey: 'content',
    selection: 'multiple',
    queryFields: [
      {
        name: 'urlPattern',
        type: 'string',
        label: intl.get('hadm.apiLimit.model.apiLimit.urlPattern').d('匹配规则'),
      },
    ],
    fields: [
      {
        name: 'monitorRuleId',
        type: 'string',
        label: intl.get('hadm.apiLimit.model.apiLimit.monitorRuleId').d('监测规则ID'),
      },
      {
        name: 'timeWindowSize',
        type: 'number',
        label: intl.get('hadm.apiLimit.model.apiLimit.timeWindowSize').d('时间窗口大小(s)'),
      },
      {
        name: 'urlPattern',
        type: 'string',
        label: intl.get('hadm.apiLimit.model.apiLimit.urlPattern').d('匹配规则'),
      },
    ],
    transport: {
      read: (config) => {
        const { data, params } = config;
        const url = isTenantRoleLevel()
          ? `${HZERO_ADM}/v1/${organizationId}/api-monitor-rules`
          : `${HZERO_ADM}/v1/api-monitor-rules`;
        return {
          data,
          params,
          url,
          method: 'GET',
        };
      },
      update: (config) => {
        const url = isTenantRoleLevel()
          ? `${HZERO_ADM}/v1/${organizationId}/api-monitor-rules`
          : `${HZERO_ADM}/v1/api-monitor-rules`;
        const { params = {}, data = [] } = config;
        return {
          data,
          params,
          url,
          method: 'PUT',
        };
      },
      destroy: (config) => {
        const { params = {}, data = [] } = config;
        const url = isTenantRoleLevel()
          ? `${HZERO_ADM}/v1/${organizationId}/api-monitor-rules?monitorRuleIds=${data
              .map((item) => item.monitorRuleId)
              .join(',')}`
          : `${HZERO_ADM}/v1/api-monitor-rules?monitorRuleIds=${data
              .map((item) => item.monitorRuleId)
              .join(',')}`;

        return {
          data,
          params,
          url,
          method: 'DELETE',
        };
      },
    },
  };
}

const displayFormDS = () => {
  return {
    fields: [
      {
        name: 'urlPattern',
        type: 'string',
        label: intl.get('hadm.apiLimit.model.apiLimit.urlPattern').d('匹配规则'),
      },
      {
        name: 'timeWindowSize',
        type: 'number',
        label: intl.get('hadm.apiLimit.model.apiLimit.timeWindowSize').d('时间窗口大小(s)'),
      },
    ],
  };
};

const flowLimitFormDS = () => {
  return {
    fields: [
      {
        name: 'listMode',
        type: 'string',
        label: intl.get('hadm.apiLimit.model.apiLimit.listMode').d('名单方式'),
        required: true,
      },
      {
        name: 'valueList',
        type: 'string',
        multiple: true,
        pattern: /^((\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.){3}(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])$/,
        label: intl.get('hadm.apiLimit.model.apiLimit.valueList ').d('ip名单列表'),
      },
      {
        name: 'enabledFlag',
        falseValue: 0,
        trueValue: 1,
        type: 'number',
        label: intl.get('hadm.apiLimit.model.apiLimit.enabledFlag ').d('启用'),
        defaultValue: 0,
      },
      {
        name: 'blacklistThreshold',
        type: 'number',
        label: intl.get('hadm.apiLimit.model.apiLimit.blacklistThreshold').d('黑名单阈值'),
      },
    ],
    transport: {
      read: (config) => {
        const { data, params } = config;
        const url = isTenantRoleLevel()
          ? `${HZERO_ADM}/v1/${organizationId}/api-limits`
          : `${HZERO_ADM}/v1/api-limits`;
        return {
          data,
          params,
          url,
          method: 'GET',
        };
      },
    },
  };
};

// 租户API管理查询列
const detailFormDS = () => {
  return {
    fields: [
      {
        name: 'urlPattern',
        type: 'string',
        label: intl.get('hadm.apiLimit.model.apiLimit.urlPattern').d('匹配规则'),
      },
      {
        name: 'timeWindowSize',
        type: 'number',
        label: intl.get('hadm.apiLimit.model.apiLimit.timeWindowSize').d('时间窗口大小(s)'),
      },
    ],
  };
};

// 租户API管理表格DS
const detailTableDS = () => {
  return {
    dataKey: 'content',
    selection: false,
    fields: [
      {
        name: 'apiMonitorId',
        type: 'string',
        label: intl.get('hadm.apiLimit.model.apiLimit.apiMonitorId').d('ID'),
      },
      {
        name: 'monitorKey',
        type: 'string',
        label: intl.get('hadm.apiLimit.model.apiLimit.monitorKey').d('请求源地址'),
      },
      {
        name: 'monitorUrl',
        type: 'string',
        label: intl.get('hadm.apiLimit.model.apiLimit.monitorUrl').d('URL'),
      },
      {
        name: 'avgFailedStatistics',
        type: 'number',
        label: intl.get('hadm.apiLimit.model.apiLimit.avgFailedStatistics').d('平均失败请求数'),
      },
      {
        name: 'avgStatistics',
        type: 'number',
        label: intl.get('hadm.apiLimit.model.apiLimit.avgStatistics').d('平均请求数'),
      },
      {
        name: 'endDate',
        type: 'string',
        label: intl.get('hadm.apiLimit.model.apiLimit.endDate').d('结束时间'),
      },
      {
        name: 'maxStatistics',
        type: 'number',
        label: intl.get('hadm.apiLimit.model.apiLimit.maxStatistics').d('最大请求数'),
      },
      {
        name: 'minStatistics',
        type: 'number',
        label: intl.get('hadm.apiLimit.model.apiLimit.minStatistics').d('最小请求数'),
      },
      {
        name: 'monitorRuleId',
        type: 'string',
        label: intl.get('hadm.apiLimit.model.apiLimit.apiMonitorConfigId').d('api监控配置ID'),
      },
      {
        name: 'startDate',
        type: 'string',
        label: intl.get('hadm.apiLimit.model.apiLimit.startDate').d('开始时间'),
      },
      {
        name: 'sumFailedStatistics',
        type: 'number',
        label: intl.get('hadm.apiLimit.model.apiLimit.sumFailedStatistics').d('总失败请求数'),
      },
      {
        name: 'sumStatistics',
        type: 'number',
        label: intl.get('hadm.apiLimit.model.apiLimit.sumStatistics').d('总请求数'),
      },
    ],
    transport: {
      read: (config) => {
        const url = isTenantRoleLevel()
          ? `${HZERO_ADM}/v1/${organizationId}/api-monitors`
          : `${HZERO_ADM}/v1/api-monitors`;
        return {
          config,
          url,
          method: 'GET',
        };
      },
    },
  };
};

export { formDS, tableDS, displayFormDS, flowLimitFormDS, detailFormDS, detailTableDS };
