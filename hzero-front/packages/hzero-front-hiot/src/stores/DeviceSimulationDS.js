/**
 * 设备模拟上报- DS
 * @date: 2020-7-7
 * @author: LiLin <lin.li03@hand-china.com>
 * @copyright Copyright (c) 2020, Hand
 */
import intl from 'utils/intl';
import { HZERO_HIOT } from 'utils/config';
import { isTenantRoleLevel, getCurrentOrganizationId } from 'utils/utils';
import { VERSION_REG } from '@/utils/constants';

const organizationId = getCurrentOrganizationId();
const isTenant = isTenantRoleLevel();
const apiPrefix = isTenant ? `${HZERO_HIOT}/v1/${organizationId}` : `${HZERO_HIOT}/v1`;

// 上方表单
const formDS = () => ({
  autoCreate: true,
  fields: [
    {
      name: 'guidLov',
      label: intl.get('hiot.deviceSimulation.model.simulation.guidLov').d('设备'),
      type: 'object',
      lovCode: 'HIOT.THING_GATEWAY_LIST',
      lovPara: { tenantId: organizationId, excludeNonRegistered: 1 },
      ignore: 'always',
      noCache: true,
      required: true,
    },
    {
      name: 'guid',
      type: 'string',
      bind: 'guidLov.guid',
    },
    {
      name: 'qos',
      type: 'string',
      label: 'Qos',
      defaultValue: '0',
    },
  ],
});

// 上报日志表格
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
  ],
});

// 数据点上报表单
const dataPointDS = () => ({
  autoQueryAfterSubmit: false,
  autoCreate: true,
  fields: [
    {
      name: 'templateLov',
      required: true,
      label: intl.get('hiot.deviceSimulation.model.simulation.template').d('上报模板'),
      type: 'object',
      lovCode: 'HIOT.MSG_TEMPLATE',
      lovPara: { tenantId: organizationId, templateTypeLike: 'UP%PROPERTY' },
      ignore: 'always',
      noCache: true,
    },
    {
      name: 'msgTemplateId',
      type: 'string',
      bind: 'templateLov.templateId',
    },
    {
      name: 'msgTemplateCode',
      type: 'string',
      bind: 'templateLov.templateCode',
    },
  ],
});

// OTA上报表单
const otaDS = () => ({
  autoQueryAfterSubmit: false,
  autoCreate: true,
  fields: [
    {
      name: 'templateLov',
      required: true,
      label: intl.get('hiot.deviceSimulation.model.simulation.template').d('上报模板'),
      type: 'object',
      lovCode: 'HIOT.MSG_TEMPLATE',
      lovPara: { tenantId: organizationId, templateTypeLike: 'UP_OTA%' },
      ignore: 'always',
      noCache: true,
    },
    {
      name: 'msgTemplateId',
      type: 'string',
      bind: 'templateLov.templateId',
    },
    {
      name: 'msgTemplateCode',
      type: 'string',
      bind: 'templateLov.templateCode',
    },
    {
      name: 'templateTypeCode',
      type: 'string',
      ignore: 'always',
      bind: 'templateLov.templateTypeCode',
    },
    {
      name: 'otaTask',
      lovCode: 'HIOT.OTA_TASK',
      lovPara: { tenantId: organizationId },
      ignore: 'always',
      type: 'object',
      noCache: true,
      label: intl.get('hiot.deviceSimulation.model.simulation.otaTask').d('升级任务'),
      dynamicProps: ({ record }) => ({
        required: record.get('templateTypeCode') === 'UP_OTA_STEP',
      }),
    },
    {
      name: 'otaTaskId',
      type: 'string',
      bind: 'otaTask.taskId',
    },
    {
      name: 'otaStep',
      type: 'number',
      max: 100,
      step: 1,
      label: intl.get('hiot.deviceSimulation.model.simulation.otaStep').d('进度'),
      dynamicProps: ({ record }) => ({
        required: record.get('templateTypeCode') === 'UP_OTA_STEP',
      }),
    },
    {
      name: 'otaDesc',
      type: 'string',
      label: intl.get('hiot.deviceSimulation.model.simulation.otaDesc').d('描述'),
    },
    {
      name: 'version',
      type: 'string',
      label: intl.get('hiot.messageTemplate.model.messageTemp.version').d('当前版本'),
      pattern: VERSION_REG,
      defaultValidationMessages: {
        patternMismatch: intl
          .get('hiot.common.view.validation.versionValMsg')
          .d('请输入正确的版本号，格式为：X.X.X'),
      },
    },
  ],
});

// 自定义上报表单
const customDS = () => ({
  autoQueryAfterSubmit: false,
  autoCreate: true,
  fields: [
    {
      name: 'customContent',
      type: 'string',
      label: intl.get('hiot.deviceSimulation.model.simulation.customContent').d('报文'),
    },
  ],
});

export { tableDS, otaDS, customDS, dataPointDS, formDS };
