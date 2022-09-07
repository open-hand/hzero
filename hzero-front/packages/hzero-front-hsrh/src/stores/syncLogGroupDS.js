import intl from 'utils/intl';
import { getCurrentOrganizationId } from 'utils/utils';
import { HZERO_HSRH } from 'hzero-front/lib/utils/config';

const organizationId = getCurrentOrganizationId();

const tableDS = () => ({
  autoQuery: true,
  dataKey: 'content',
  selection: false,
  queryFields: [
    {
      name: 'syncConfSet',
      type: 'object',
      lovCode: 'HSRH.INDEX_SYNC_CONFIG_LIST',
      label: intl.get('hsrh.syncLog.model.syncLog.syncConfSet').d('同步配置编码'),
      ignore: 'always',
      noCache: true,
    },
    {
      name: 'syncConfCode',
      type: 'string',
      bind: 'syncConfSet.syncConfCode',
    },
    {
      name: 'syncStatusCode',
      type: 'string',
      lookupCode: 'HSRH.SYNC_STATUS',
      label: intl.get('hsrh.syncLog.model.syncLog.syncStatusCode').d('同步状态'),
    },
    {
      name: 'indexCodeLov',
      type: 'object',
      lovCode: 'HSRH.INDEX_VERSIONS_LIST',
      label: intl.get('hsrh.syncLog.model.syncLog.indexCode').d('索引'),
      ignore: 'always',
      noCache: true,
    },
    {
      name: 'indexCode',
      type: 'string',
      bind: 'indexCodeLov.indexCode',
    },
    {
      name: 'syncStartTimeStart',
      type: 'dateTime',
      label: intl.get('hsrh.syncLog.model.syncLog.syncStartTimeStart').d('同步开始时间从'),
    },
    {
      name: 'syncStartTimeEnd',
      type: 'dateTime',
      label: intl.get('hsrh.syncLog.model.syncLog.syncStartTimeEnd').d('同步开始时间至'),
    },
    {
      name: 'syncEndTimeStart',
      type: 'dateTime',
      label: intl.get('hsrh.syncLog.model.syncLog.syncEndTimeStart').d('同步结束时间从'),
    },
    {
      name: 'syncEndTimeEnd',
      type: 'dateTime',
      label: intl.get('hsrh.syncLog.model.syncLog.syncEndTimeEnd').d('同步结束时间至'),
    },
  ],
  fields: [
    // {
    //   name: 'triggerEventMeaning',
    //   type: 'string',
    //   label: intl.get('hsrh.syncLog.model.syncLog.triggerEvent').d('触发事件'),
    // },
    {
      name: 'syncConfCode',
      type: 'string',
      label: intl.get('hsrh.syncLog.model.syncLog.syncConfCode').d('同步配置编码'),
    },
    {
      name: 'syncStatusMeaning',
      type: 'string',
      label: intl.get('hsrh.syncLog.model.syncLog.syncStatusMeaning').d('同步状态'),
    },
    {
      name: 'indexCode',
      type: 'string',
      label: intl.get('hsrh.syncLog.model.syncLog.indexCode').d('索引'),
    },
    {
      name: 'syncStartTime',
      type: 'string',
      label: intl.get('hsrh.syncLog.model.syncLog.syncStartTime').d('同步开始时间'),
    },
    {
      name: 'syncEndTime',
      type: 'string',
      label: intl.get('hsrh.syncLog.model.syncLog.syncEndTime').d('同步结束时间'),
    },
  ],

  transport: {
    read: (config) => {
      const url = `${HZERO_HSRH}/v1/${organizationId}/data-sync-logs`;
      return {
        config,
        url,
        method: 'GET',
      };
    },
  },
});

const detailFormDS = () => ({
  fields: [
    {
      name: 'syncConfCode',
      type: 'string',
      label: intl.get('hsrh.syncLog.model.syncLog.syncConfCode').d('同步配置编码'),
    },
    {
      name: 'syncStartTime',
      type: 'string',
      label: intl.get('hsrh.syncLog.model.syncLog.syncStartTime').d('同步开始时间'),
    },
    {
      name: 'syncStatusMeaning',
      type: 'string',
      label: intl.get('hsrh.syncLog.model.syncLog.syncStatusCode').d('同步状态'),
    },
    {
      name: 'indexCode',
      type: 'string',
      label: intl.get('hsrh.syncLog.model.syncLog.indexCode').d('索引'),
    },
    {
      name: 'syncEndTime',
      type: 'string',
      label: intl.get('hsrh.syncLog.model.syncLog.syncEndTime').d('同步结束时间'),
    },
    {
      name: 'syncDataUrl',
      type: 'string',
      label: intl.get('hsrh.syncLog.model.syncLog.syncDataUrl').d('同步数据路径'),
    },
    {
      name: 'syncLog',
      type: 'string',
      label: intl.get('hsrh.syncLog.model.syncLog.syncLog').d('同步日志'),
    },
  ],
  transport: {
    read: (config) => {
      const url = `${HZERO_HSRH}/v1/${organizationId}/data-sync-logs/${config.data.syncLogId}`;
      return {
        url,
        method: 'GET',
      };
    },
  },
});
export { tableDS, detailFormDS };
