/**
 * @Author: guanglong.sun <guanglong.sun@hand-china.com>
 * @Create time: 2019/10/18 5:33 下午
 * @Copyright: Copyright(c) 2019, Hand
 * @Description: iot告警事件DS
 */
import moment from 'moment';
import intl from 'utils/intl';
import { getCurrentOrganizationId } from 'utils/utils';
import { DEFAULT_DATETIME_FORMAT } from 'utils/constants';
import { API_PREFIX } from '@/utils/constants';
import { API_HOST, HZERO_ALT } from 'utils/config';

const organizationId = getCurrentOrganizationId();

const iotEventDS = () => ({
  selection: false,
  transport: {
    /**
     * 查询告警事件列表数据
     * @param config
     * @param data 查询参数(分页信息)
     * @returns {{method: string, url: string}}
     */
    read: ({ config, data }) => {
      const { alertEventId, ts, ...params } = data;
      return {
        ...config,
        url: alertEventId
          ? `${API_PREFIX}/v1/${organizationId}/thing-alert-events/${alertEventId}`
          : `${API_PREFIX}/v1/${organizationId}/thing-alert-events`,
        method: 'get',
        data: { ...ts, ...params },
      };
    },
  },
  queryFields: [
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
      name: 'alertLevel',
      type: 'string',
      label: intl.get('hiot.iotWarnEvent.model.iotWarn.alertLevel').d('告警级别'),
      lookupCode: 'HALT.ALERT_LEVEL',
    },
    {
      name: 'recoveredFlag',
      type: 'string',
      label: intl.get('hiot.iotWarnEvent.model.iotWarn.recoveredFlag').d('是否恢复'),
      lookupCode: 'HIOT.ALERT_STATUS',
    },
    {
      name: 'ts',
      type: 'dateTime',
      range: ['eventTimeStart', 'eventTimeEnd'],
      label: intl.get('hiot.iotWarnEvent.model.iotWarn.time').d('事件时间'),
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
      name: 'alertRangeCodeMeaning',
      type: 'string',
      label: intl.get('hiot.iotWarnEvent.model.iotWarn.alertRangeCodeMeaning').d('预警等级'),
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

const concreteTableDS = () => ({
  fields: [
    {
      name: 'propertyName',
      type: 'string',
      label: intl.get('hiot.iotWarnEvent.model.iotWarn.propertyName').d('属性名'),
    },
    {
      name: 'itemTypeMeaning',
      type: 'string',
      label: intl.get('hiot.iotWarnEvent.model.iotWarn.itemTypeMeaning').d('属性类型'),
    },
    {
      name: 'value',
      type: 'string',
      label: intl.get('hiot.iotWarnEvent.model.iotWarn.value').d('值'),
    },
    {
      name: 'unitCode',
      type: 'string',
      label: intl.get('hiot.iotWarnEvent.model.iotWarn.unitCode').d('单位'),
    },
  ],
});

// 模态框DS
const drawerDS = () => ({
  autoQuery: false,
  autoQueryAfterSubmit: false,
  fields: [
    {
      name: 'alertCode',
      label: intl.get('hiot.common.model.common.alertCode').d('配置编码'),
      type: 'string',
    },
    {
      name: 'alertName',
      type: 'string',
      label: intl.get('hiot.common.model.common.alertName').d('配置名称'),
    },
    {
      name: 'ruleCode',
      label: intl.get('hiot.iotWarnEvent.model.iotWarn.ruleCode').d('规则编码'),
      type: 'string',
    },
    {
      name: 'ruleName',
      type: 'string',
      label: intl.get('hiot.iotWarnEvent.model.iotWarn.ruleName').d('规则名称'),
    },
    {
      name: 'sourceTypeMeaning',
      type: 'string',
      label: intl.get('hzero.common.model.common.sourceTypeCode').d('数据来源'),
    },
    {
      name: 'targetTypeList',
      type: 'object',
      lookupCode: 'HALT.TARGET_TYPE',
      multiple: true,
      label: intl.get('hzero.common.model.common.targetTypeCode').d('通知方式'),
      transformResponse: (res) => {
        const data = res ? res.map((item) => item.targetTypeCode) : [];
        return data;
      },
    },
    {
      name: 'alertLevelMeaning',
      type: 'string',
      label: intl.get('hiot.iotWarnEvent.model.iotWarn.alertLevelCode').d('预警级别'),
    },
    {
      name: 'remark',
      type: 'string',
      label: intl.get('hiot.iotWarnEvent.model.iotWarn.remark').d('说明'),
    },
  ],
  transport: {
    read: ({ data }) => {
      const { alertCode } = data;
      return {
        url: `${API_HOST}${HZERO_ALT}/v1/${organizationId}/alerts/list-by-codes`,
        method: 'GET',
        data: {},
        params: {
          alertCodes: alertCode,
        },
      };
    },
  },
});

export {
  drawerDS,
  iotEventDS,
  concreteTableDS, // 告警详情-具体信息
};
