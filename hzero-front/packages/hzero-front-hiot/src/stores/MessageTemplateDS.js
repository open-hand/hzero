/**
 * 报文模板管理- DS
 * @date: 2020-7-6
 * @author: LiLin <lin.li03@hand-china.com>
 * @copyright Copyright (c) 2020, Hand
 */
import intl from 'utils/intl';
import { HZERO_HIOT } from 'utils/config';
import { isTenantRoleLevel, getCurrentOrganizationId } from 'utils/utils';
import { VERSION_REG, CODE_UPPER_REG } from '@/utils/constants';

const organizationId = getCurrentOrganizationId();
const isTenant = isTenantRoleLevel();
const apiPrefix = isTenant ? `${HZERO_HIOT}/v1/${organizationId}` : `${HZERO_HIOT}/v1`;

const tableDS = () => ({
  autoQuery: true,
  selection: false,
  transport: {
    read: () => ({
      url: `${apiPrefix}/msg-templates`,
      method: 'get',
    }),
    destroy: ({ data }) => {
      const { __id, _status, ...other } = Array.isArray(data) ? data[0] : {};
      return {
        url: `${apiPrefix}/msg-templates`,
        method: 'DELETE',
        data: other,
      };
    },
  },
  fields: [
    {
      name: 'templateCode',
      type: 'string',
      label: intl.get('hiot.messageTemplate.model.messageTemp.templateCode').d('报文模板编码'),
    },
    {
      name: 'templateName',
      type: 'string',
      label: intl.get('hiot.messageTemplate.model.messageTemp.templateName').d('报文模板名称'),
    },
    {
      name: 'templateTypeCode',
      type: 'string',
      lookupCode: 'HIOT.MSG_TEMPLATE_TYPE',
      label: intl.get('hiot.messageTemplate.model.messageTemp.templateTypeCode').d('类型'),
    },
  ],
  queryFields: [
    {
      name: 'templateCode',
      type: 'string',
      label: intl.get('hiot.messageTemplate.model.messageTemp.templateCode').d('报文模板编码'),
    },
    {
      name: 'templateName',
      type: 'string',
      label: intl.get('hiot.messageTemplate.model.messageTemp.templateName').d('报文模板名称'),
    },
    {
      name: 'templateTypeCode',
      type: 'string',
      label: intl.get('hiot.messageTemplate.model.messageTemp.templateTypeCode').d('类型'),
      lookupCode: 'HIOT.MSG_TEMPLATE_TYPE',
    },
  ],
});

const detailDS = () => ({
  autoQuery: false,
  autoQueryAfterSubmit: false,
  transport: {
    read: ({ dataSet }) => {
      const { templateId } = dataSet;
      return {
        url: `${apiPrefix}/msg-templates/${templateId}`,
        method: 'get',
        params: {},
      };
    },
    create: ({ data }) => {
      const { __id, _status, ...other } = Array.isArray(data) ? data[0] : {};
      return {
        url: `${apiPrefix}/msg-templates`,
        method: 'POST',
        data: other,
      };
    },
    update: ({ data }) => {
      const { __id, _status, ...other } = Array.isArray(data) ? data[0] : {};
      return {
        url: `${apiPrefix}/msg-templates`,
        method: 'PUT',
        data: other,
      };
    },
  },
  fields: [
    {
      name: 'templateCode',
      type: 'string',
      required: true,
      maxLength: 30,
      label: intl.get('hiot.messageTemplate.model.messageTemp.templateCode').d('报文模板编码'),
      pattern: CODE_UPPER_REG,
      defaultValidationMessages: {
        patternMismatch: intl
          .get(`hiot.common.view.validation.codeMsg`)
          .d('全大写及数字，必须以字母、数字开头，可包含“_”'),
      },
    },
    {
      name: 'templateName',
      maxLength: 120,
      type: 'intl',
      required: true,
      label: intl.get('hiot.messageTemplate.model.messageTemp.templateName').d('报文模板名称'),
    },
    {
      name: 'templateTypeCode',
      required: true,
      type: 'string',
      lookupCode: 'HIOT.MSG_TEMPLATE_TYPE',
      label: intl.get('hiot.messageTemplate.model.messageTemp.templateTypeCode').d('类型'),
    },
    {
      name: 'description',
      type: 'string',
      maxLength: 240,
      label: intl.get('hzero.common.explain').d('说明'),
    },
    {
      name: 'msgContent',
      type: 'string',
      label: intl.get('hiot.messageTemplate.model.messageTemp.msgContent').d('模板'),
      required: true,
    },
    {
      name: 'help',
      type: 'string',
      label: intl.get('hiot.messageTemplate.model.messageTemp.help').d('帮助'),
    },
  ],
});

const testDrawDS = () => ({
  autoQuery: false,
  autoQueryAfterSubmit: false,
  transport: {},
  fields: [
    {
      name: 'guidLov',
      type: 'object',
      required: true,
      lovCode: 'HIOT.THING_GATEWAY_LIST',
      lovPara: { tenantId: organizationId },
      ignore: 'always',
      noCache: true,
      label: intl.get('hiot.messageTemplate.model.messageTemp.guid').d('设备'),
    },
    {
      name: 'guid',
      type: 'string',
      bind: 'guidLov.guid',
    },
    {
      name: 'thingId',
      type: 'string',
      bind: 'guidLov.thingId',
    },
    {
      name: 'thingType',
      type: 'string',
      bind: 'guidLov.thingType',
    },
    { name: 'templateTypeCode' },
    {
      name: 'otaTask',
      lovCode: 'HIOT.OTA_TASK',
      lovPara: { tenantId: organizationId },
      ignore: 'always',
      type: 'object',
      noCache: true,
      label: intl.get('hiot.messageTemplate.model.messageTemp.otaTask').d('OTA升级任务'),
      dynamicProps: ({ record }) => ({
        required: ['DOWN_OTA_PACKAGE', 'UP_OTA_STEP'].includes(record.get('templateTypeCode')),
      }),
    },
    {
      name: 'otaTaskId',
      type: 'string',
      bind: 'otaTask.taskId',
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
    {
      name: 'otaStep',
      type: 'number',
      label: intl.get('hiot.messageTemplate.model.messageTemp.otaStep').d('进度'),
      dynamicProps: ({ record }) => ({
        required: ['UP_OTA_STEP'].includes(record.get('templateTypeCode')),
      }),
      max: 100,
      step: 1,
    },
    {
      name: 'otaDesc',
      type: 'string',
      label: intl.get('hiot.messageTemplate.model.messageTemp.otaDesc').d('描述'),
    },
  ],
});
export { tableDS, detailDS, testDrawDS };
