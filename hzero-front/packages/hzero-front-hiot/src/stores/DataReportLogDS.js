/**
 * 数据上报日志监控- DS
 * @date: 2020-6-15
 * @author: LiLin <lin.li03@hand-china.com>
 * @copyright Copyright (c) 2020, Hand
 */
import intl from 'utils/intl';
import { HZERO_HIOT } from 'utils/config';
import { isTenantRoleLevel, getCurrentOrganizationId } from 'utils/utils';

const organizationId = getCurrentOrganizationId();
const isTenant = isTenantRoleLevel();
const apiPrefix = isTenant ? `${HZERO_HIOT}/v1/${organizationId}` : `${HZERO_HIOT}/v1`;

const tableDS = () => ({
  autoQuery: true,
  selection: false,
  transport: {
    read: () => ({
      url: `${apiPrefix}/log-msg/up`,
      method: 'get',
    }),
  },
  fields: [
    {
      name: 'serviceInstIp',
      type: 'string',
      label: intl.get('hiot.dataReport.model.dataReport.serviceInstIp').d('服务实例IP'),
    },
    {
      name: 'topicName',
      type: 'string',
      label: intl.get('hiot.dataReport.model.dataReport.topicName').d('Topic名称'),
    },
    {
      name: 'processStatus',
      type: 'string',
      label: intl.get('hiot.dataReport.model.dataReport.processStatus').d('处理结果'),
      lookupCode: 'HIOT.SUCCESS_FLAG',
    },
    {
      name: 'creationDate',
      type: 'dateTime',
      label: intl.get('hiot.dataReport.model.dataReport.creationDate').d('记录日期'),
    },
    {
      name: 'deviceName',
      type: 'string',
      label: intl.get('hiot.common.model.common.deviceName').d('设备/网关名称'),
    },
    {
      name: 'deviceCode',
      type: 'string',
      label: intl.get('hiot.common.model.common.deviceCode').d('设备/网关编码'),
    },
  ],
  queryFields: [
    {
      name: 'serviceInstIp',
      type: 'string',
      label: intl.get('hiot.dataReport.model.dataReport.serviceInstIp').d('服务实例IP'),
    },
    {
      name: 'topicName',
      type: 'string',
      label: intl.get('hiot.dataReport.model.dataReport.topicName').d('Topic名称'),
    },
    {
      name: 'guidLov',
      type: 'object',
      lovCode: 'HIOT.THING_GATEWAY_LIST',
      lovPara: { tenantId: organizationId },
      ignore: 'always',
      noCache: true,
      label: intl.get('hiot.common.model.common.guidLov').d('所属设备'),
    },
    {
      name: 'guid',
      type: 'string',
      bind: 'guidLov.guid',
    },
    {
      name: 'processStatus',
      type: 'string',
      label: intl.get('hiot.dataReport.model.dataReport.processStatus').d('处理结果'),
      lookupCode: 'HIOT.SUCCESS_FLAG',
    },
    {
      name: 'startDate',
      type: 'dateTime',
      label: intl.get('hiot.dataReport.model.dataReport.startDate').d('记录日期从'),
      max: 'endDate',
    },
    {
      name: 'endDate',
      type: 'dateTime',
      label: intl.get('hiot.dataReport.model.dataReport.endDate').d('记录日期至'),
      min: 'startDate',
    },
  ],
});

export { tableDS };
