/**
 * 告警事件管理- DS
 * @date: 2020-5-20
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2020, Hand
 */

import { DataSet } from 'choerodon-ui/pro';
import intl from 'utils/intl';
import { isTenantRoleLevel, getCurrentOrganizationId } from 'utils/utils';
import { HZERO_ALT } from 'utils/config';

const organizationId = getCurrentOrganizationId();
const isTenant = isTenantRoleLevel();
const apiPrefix = isTenant ? `${HZERO_ALT}/v1/${organizationId}` : `${HZERO_ALT}/v1`;

const AlertEventDS = () => ({
  autoQuery: true,
  selection: false,
  primaryKey: 'alertEventId',
  transport: {
    read: () => ({
      url: `${apiPrefix}/alert-events`,
      method: 'get',
    }),
  },
  fields: [
    {
      name: 'alertCode',
      type: 'string',
      label: intl.get('halt.common.model.common.alertCode').d('告警代码'),
    },
    {
      name: 'alertName',
      type: 'string',
      label: intl.get('halt.common.model.common.alertName').d('告警名称'),
    },
    {
      name: 'alertRangeCode',
      type: 'string',
      label: intl.get('halt.alertEvent.model.alertEvent.alertRangeCode').d('告警级别'),
      options: new DataSet({
        selection: 'single',
        data: [
          {
            value: 'NORMAL',
            meaning: intl.get('halt.alertEvent.model.alertEvent.normal').d('正常'),
          },
          { value: 'INFO', meaning: intl.get('halt.alertEvent.model.alertEvent.info').d('提示') },
          { value: 'WARN', meaning: intl.get('halt.alertEvent.model.alertEvent.warn').d('警告') },
          { value: 'ERROR', meaning: intl.get('halt.alertEvent.model.alertEvent.error').d('错误') },
          { value: 'FETAL', meaning: intl.get('halt.alertEvent.model.alertEvent.fetal').d('致命') },
        ],
      }),
    },
    {
      name: 'dataSource',
      type: 'string',
      label: intl.get('halt.alertEvent.model.alertEvent.dataSource').d('告警来源'),
    },
    {
      name: 'alertRouteType',
      type: 'string',
      label: intl.get('halt.alertEvent.model.alertEvent.alertRouteType').d('路由方式'),
      lookupCode: 'HALT.ALERT_ROUTE_TYPE',
    },
    {
      name: 'alertTime',
      type: 'dateTime',
      label: intl.get('halt.alertEvent.model.alertEvent.alertTime').d('告警时间'),
    },
    {
      name: 'processedTime',
      type: 'dateTime',
      label: intl.get('halt.alertEvent.model.alertEvent.processedTime').d('完成时间'),
    },
    {
      name: 'alertResult',
      type: 'string',
      label: intl.get('halt.alertEvent.model.alertEvent.alertResult').d('处理结果'),
      lookupCode: 'HALT.SUCCESS_FLAG',
    },
  ],
  queryFields: [
    {
      name: 'alertCode',
      type: 'string',
      label: intl.get('halt.common.model.common.alertCode').d('告警代码'),
    },
    {
      name: 'alertName',
      type: 'string',
      label: intl.get('halt.common.model.common.alertName').d('告警名称'),
    },
    {
      name: 'alertRangeCode',
      type: 'string',
      label: intl.get('halt.alertEvent.model.alertEvent.alertRangeCode').d('告警级别'),
      options: new DataSet({
        selection: 'single',
        data: [
          {
            value: 'NORMAL',
            meaning: intl.get('halt.alertEvent.model.alertEvent.normal').d('正常'),
          },
          { value: 'INFO', meaning: intl.get('halt.alertEvent.model.alertEvent.info').d('提示') },
          { value: 'WARN', meaning: intl.get('halt.alertEvent.model.alertEvent.warn').d('警告') },
          { value: 'ERROR', meaning: intl.get('halt.alertEvent.model.alertEvent.error').d('错误') },
          { value: 'FETAL', meaning: intl.get('halt.alertEvent.model.alertEvent.fetal').d('致命') },
        ],
      }),
    },
    {
      name: 'alertResult',
      type: 'string',
      label: intl.get('halt.alertEvent.model.alertEvent.alertResult').d('处理结果'),
      lookupCode: 'HALT.SUCCESS_FLAG',
    },
    {
      name: 'alertTimeStart',
      type: 'dateTime',
      label: intl.get('halt.alertEvent.model.alertEvent.eventTimeStart').d('告警时间从'),
      max: 'eventTimeEnd',
    },
    {
      name: 'alertTimeEnd',
      type: 'dateTime',
      label: intl.get('halt.alertEvent.model.alertEvent.eventTimeEnd').d('告警时间至'),
      min: 'eventTimeStart',
    },
    {
      name: 'processedTimeStart',
      type: 'dateTime',
      label: intl.get('halt.alertEvent.model.alertEvent.creationDateStart').d('完成时间从'),
      max: 'creationDateEnd',
    },
    {
      name: 'processedTimeEnd',
      type: 'dateTime',
      label: intl.get('halt.alertEvent.model.alertEvent.creationDateEnd').d('完成时间至'),
      min: 'creationDateStart',
    },
  ],
});

// 清理日志DS
const LogDS = () => ({
  autoCreate: true,
  fields: [
    {
      name: 'clearType',
      label: intl.get('halt.alertEvent.model.alertEvent.clearType').d('类型'),
      type: 'string',
      lookupCode: 'HPFM.AUDIT_LOG.CLEAR_TYPE',
      required: true,
    },
  ],
  transport: {
    create: ({ data }) => {
      const { clearType } = Array.isArray(data) ? data[0] : {};
      return {
        url: `${apiPrefix}/alert-event-logs/clear`,
        method: 'DELETE',
        data: {},
        params: { clearType },
      };
    },
  },
});

export { AlertEventDS, LogDS };
