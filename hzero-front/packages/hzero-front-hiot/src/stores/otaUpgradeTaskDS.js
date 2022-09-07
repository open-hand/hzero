/**
 * @Author: zhengmin.liang <zhengmin.liang@hand-china.com>
 * @Create time: 2019/11/26
 * @Copyright: Copyright(c) 2019, Hand
 * @Description: OTA升级任务DS
 */
import { getCurrentOrganizationId } from 'utils/utils';
import intl from 'utils/intl';
import { API_HOST, HZERO_HIOT } from 'utils/config';
import { CODE_UPPER_REG } from '@/utils/constants';

const prefix = 'hiot.ota';
const orgId = getCurrentOrganizationId();

const packField = (field) => (items) => ({ ...field, ...items });

const fields = {
  taskName: (items) =>
    packField({
      name: 'taskName',
      type: 'string',
      label: intl.get(`${prefix}.task.name`).d('任务名称'),
    })(items),
  templateName: (items) =>
    packField({
      name: 'packageName',
      type: 'object',
      label: intl.get(`${prefix}.template.packageName`).d('升级包名称'),
    })(items),
  templateCode: (items) =>
    packField({
      name: 'thingModelCode',
      type: 'string',
      label: intl.get(`${prefix}.device.template.code`).d('模型编码'),
    })(items),
  versionNum: (items) =>
    packField({
      name: 'version',
      type: 'string',
      label: intl.get(`${prefix}.version.number`).d('版本号'),
    })(items),
  cloudPlatform: (items) =>
    packField({
      name: 'platform',
      type: 'string',
      label: intl.get('hiot.common.model.common.platform').d('云平台'),
      lookupCode: 'HIOT.CLOUD_PLATFORM',
    })(items),
  cloudAccount: (items) =>
    packField({
      name: 'configName',
      type: 'object',
      label: intl.get('hiot.common.model.common.configName').d('云账户'),
      lovCode: 'HIOT.LOV.CLOUD_ACCOUNT',
      lovPara: { tenantId: orgId },
    })(items),
  upgradeTime: (items) =>
    packField({
      name: 'scheduledStartTime',
      type: 'dateTime',
      range: ['startTime', 'endTime'],
      label: intl.get(`${prefix}.upgrade.time`).d('升级时间'),
    })(items),
  startTime: (items) =>
    packField({
      name: 'startTime',
      type: 'dateTime',
      label: intl.get(`${prefix}.start.time`).d('开始时间'),
    })(items),
  taskCode: (items) =>
    packField({
      name: 'taskCode',
      type: 'string',
      label: intl.get(`${prefix}.task.code`).d('任务编码'),
    })(items),
  upgradeCategory: (items) =>
    packField({
      name: 'category',
      type: 'string',
      label: intl.get(`${prefix}.upgrade.type`).d('升级类别'),
      lookupCode: 'HIOT.OTA_PACKAGE_CATEGORY',
    })(items),
  description: (items) =>
    packField({
      name: 'description',
      type: 'string',
      label: intl.get('hzero.common.explain').d('说明'),
    })(items),
  sendMethod: (items) =>
    packField({
      name: 'thingRange',
      type: 'number',
    })(items),
  addDevice: (items) =>
    packField({
      name: 'addDevice',
      type: 'object',
      lovCode: 'HIOT.LOV.OTA_TASK_THING',
      lovPara: { tenantId: orgId },
      label: intl.get(`${prefix}.add.device`).d('添加设备'),
    })(items),
  addGateway: (items) =>
    packField({
      name: 'addGateway',
      type: 'object',
      lovCode: 'HIOT.LOV.OTA_TASK_GATEWAY',
      lovPara: { tenantId: orgId },
      label: intl.get(`${prefix}.add.gateway`).d('添加网关'),
    })(items),
  deviceName: (items) =>
    packField({
      name: 'thingName',
      type: 'string',
      label: intl.get('hiot.common.device.name').d('设备名称'),
    })(items),
  deviceCode: (items) =>
    packField({
      name: 'thingCode',
      type: 'string',
      label: intl.get('hiot.common.device.code').d('设备编码'),
    })(items),
  beforeUpgradingVersionNum: (items) =>
    packField({
      name: 'versionBefore',
      type: 'string',
      label: intl.get(`${prefix}.before.upgrading.version.number`).d('升级前版本号'),
    })(items),
  currentStatus: (items) =>
    packField({
      name: 'otaStatus',
      type: 'string',
      label: intl.get(`${prefix}.current.status`).d('当前状态'),
      lookupCode: 'HIOT.OTA_STATUS',
    })(items),
  currentUsedTime: (items) =>
    packField({
      name: 'consumeTime',
      type: 'string',
      label: intl.get(`${prefix}.current.used.time`).d('目前耗时'),
    })(items),
  upgradeDeviceNum: (items) =>
    packField({
      name: 'thingCount',
      type: 'string',
      label: intl.get(`${prefix}.upgrade.device.number`).d('升级设备数(个)'),
    })(items),
  upgradeSucceedNumber: (items) =>
    packField({
      name: 'successCount',
      type: 'string',
      label: intl.get(`${prefix}.upgrade.succeed.number`).d('升级成功数(个)'),
    })(items),
  upgradeFailedNumber: (items) =>
    packField({
      name: 'failCount',
      type: 'string',
      label: intl.get(`${prefix}.upgrade.failed.number`).d('升级失败数(个)'),
    })(items),
  currentVersionNum: (items) =>
    packField({
      name: 'version',
      type: 'string',
      label: intl.get(`${prefix}.current.version.number`).d('当前版本号'),
    })(items),
  belongsProject: (items) =>
    packField({
      name: 'name',
      type: 'string',
      label: intl.get(`hiot.common.model.device.parentDeviceName`).d('所属设备分组'),
    })(items),
};

const otaUpgradeTaskListDS = () => ({
  autoQuery: true,
  transport: {
    read({ data }) {
      const upgradeTimeField = fields.upgradeTime().name;
      const { [upgradeTimeField]: { startTime, endTime } = {}, ...otherProps } = data;
      const {
        [fields.cloudPlatform().name]: { platform } = {},
        [fields.cloudAccount().name]: { configName } = {},
      } = otherProps;
      return {
        url: `${API_HOST}${HZERO_HIOT}/v1/${orgId}/ota-tasks`,
        method: 'get',
        data: {
          ...otherProps,
          platform,
          configName,
          startTime: startTime && startTime.format('YYYY-MM-DD HH:mm:ss'),
          endTime: endTime && endTime.format('YYYY-MM-DD HH:mm:ss'),
        },
      };
    },
  },
  queryFields: [
    fields.taskName(),
    fields.taskCode(),
    fields.templateName({ type: 'string' }),
    fields.versionNum(),
    fields.cloudPlatform({
      type: 'object',
      lookupCode: undefined,
      lovPara: { tenantId: orgId },
      lovCode: 'HIOT.OTA_CLOUD_ACCOUNT',
    }),
    fields.cloudAccount(),
    fields.upgradeTime(),
  ],
  fields: [
    fields.taskName(),
    fields.taskCode(),
    fields.templateName({ type: 'string' }),
    fields.versionNum(),
    fields.cloudPlatform(),
    fields.cloudAccount(),
    fields.startTime({
      name: 'scheduledStartTime',
      label: intl.get(`${prefix}.upgrade.time`).d('升级时间'),
    }),
  ],
});

const otaUpgradeTaskCreateDS = () => ({
  transport: {
    create: ({ data }) => {
      const {
        // [fields.cloudPlatform().name]: { configId },
        [fields.templateName().name]: { packageId },
        ...otherProps
      } = data[0];
      return {
        method: 'post',
        url: `${API_HOST}${HZERO_HIOT}/v1/${orgId}/ota-tasks`,
        data: { ...otherProps, packageId },
      };
    },
  },
  fields: [
    fields.cloudPlatform({
      required: true,
      type: 'object',
      lookupCode: undefined,
      lovPara: { tenantId: orgId },
      lovCode: 'HIOT.LOV.OTA_CLOUD_ACCOUNT',
    }),
    fields.cloudAccount({
      type: 'string',
      required: true,
      bind: `${fields.cloudPlatform().name}.configName`,
    }),
    {
      name: 'configId',
      bind: `${fields.cloudPlatform().name}.configId`,
    },
    {
      name: 'platformName',
      type: 'string',
      bind: `${fields.cloudPlatform().name}.platformName`,
    },
    fields.taskName({
      type: 'intl',
      required: true,
      minLength: 3,
      maxLength: 32,
      validator: (value) =>
        (value.split(' ').length >= 2 || undefined) &&
        intl.get(`${prefix}.warn.message.no-space`).d('不能包含空格'),
    }),
    fields.taskCode({
      required: true,
      minLength: 3,
      maxLength: 32,
      pattern: CODE_UPPER_REG,
      defaultValidationMessages: {
        patternMismatch: intl
          .get(`hiot.common.view.validation.codeMsg`)
          .d('全大写及数字，必须以字母、数字开头，可包含“_”'),
      },
    }),
    fields.upgradeCategory({
      required: true,
      defaultValue: 'THING',
    }),
    fields.templateName({
      required: true,
      lovPara: { tenantId: orgId },
      cascadeMap: { category: fields.upgradeCategory().name },
      dynamicProps: ({ record }) => {
        return {
          lovCode:
            record.get('category') === 'THING'
              ? 'HIOT.LOV.OTA_CREATE_TASK'
              : 'HIOT.LOV.OTA_CREATE_TASK_GW',
        };
      },
    }),
    fields.templateCode({
      bind: `${fields.templateName().name}.thingModelCode`,
    }),
    {
      name: 'thingModelName',
      type: 'string',
      label: intl.get(`${prefix}.template.thingModelName`).d('模型名称'),
      bind: `${fields.templateName().name}.thingModelName`,
    },
    fields.versionNum({
      bind: `${fields.templateName().name}.currentVersion`,
    }),
    fields.startTime(),
    fields.description(),
    fields.sendMethod({ defaultValue: 1 }),
    fields.addDevice({
      multiple: true,
      ignore: 'always',
      cascadeMap: {
        configId: `${fields.cloudPlatform().name}.configId`,
        otaVersion: fields.versionNum().name,
      },
      dynamicProps({ record }) {
        return {
          lovPara: {
            thingModelId: record.get('thingModelId'),
          },
        };
      },
    }),
    fields.addGateway({
      multiple: true,
      ignore: 'always',
      cascadeMap: {
        configId: `${fields.cloudPlatform().name}.configId`,
        otaVersion: fields.versionNum().name,
      },
      dynamicProps({ record }) {
        return {
          lovPara: {
            thingModelId: record.get('thingModelId'),
          },
        };
      },
    }),
    {
      name: 'thingModelId',
      type: 'string',
      bind: `${fields.templateName().name}.thingModelId`,
    },
    {
      name: 'templateLov',
      type: 'object',
      required: true,
      lovCode: 'HIOT.MSG_TEMPLATE',
      lovPara: { tenantId: orgId, templateTypeLike: 'DOWN_OTA_PACKAGE' },
      ignore: 'always',
      noCache: true,
      label: intl.get('hiot.ota.model.ota.msgTemplateCode').d('下发模板'),
    },
    {
      name: 'msgTemplateCode',
      type: 'string',
      bind: 'templateLov.templateCode',
    },
  ],
});

const otaUpgradeTaskDeviceFilterList = () => ({
  fields: [
    fields.deviceName(),
    fields.deviceCode(),
    fields.currentVersionNum(),
    fields.belongsProject(),
    {
      name: 'category',
      type: 'string',
      label: intl.get('hiot.common.device.type').d('设备类别'),
    },
    {
      name: 'gatewayName',
      type: 'string',
      label: intl.get(`hiot.common.model.common.belongsGateway`).d('所属网关'),
    },
  ],
});
/**
 gatewayCode: "hd_01"
 gatewayId: 27
 gatewayName: "网关01"
 projectName: "test1"
 thingModelId: 62
 version: "0.0.1"
 * @type {{fields: [{name: string}]}}
 */
const otaUpgradeTaskGatewayFilterList = () => ({
  fields: [
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
    fields.currentVersionNum(),
    fields.belongsProject(),
  ],
});

const otaUpgradeTaskDetailInfoDS = () => ({
  transport: {
    read: ({ data }) => {
      const { taskId } = data;
      return {
        method: 'get',
        url: `${API_HOST}${HZERO_HIOT}/v1/${orgId}/ota-tasks/${taskId}`,
      };
    },
  },
  fields: [
    fields.currentUsedTime(),
    fields.upgradeDeviceNum(),
    fields.upgradeSucceedNumber(),
    fields.upgradeFailedNumber(),
    fields.cloudPlatform(),
    fields.cloudAccount(),
    fields.taskName(),
    fields.taskCode(),
    fields.upgradeCategory(),
    fields.templateName(),
    fields.templateCode(),
    fields.versionNum(),
    fields.upgradeTime({ range: false }),
    fields.description(),
  ],
});

const otaUpgradeTaskDetailListDS = () => ({
  transport: {
    read: ({ data }) => {
      const { taskId } = data;
      return {
        method: 'get',
        url: `${API_HOST}${HZERO_HIOT}/v1/${orgId}/ota-tasks/thing/${taskId}`,
      };
    },
  },
  queryFields: [
    fields.deviceName(),
    fields.deviceCode(),
    fields.beforeUpgradingVersionNum(),
    fields.currentStatus(),
  ],
  fields: [
    fields.deviceName(),
    fields.deviceCode(),
    fields.beforeUpgradingVersionNum(),
    fields.startTime(),
    {
      name: 'endTime',
      type: 'string',
      label: intl.get(`${prefix}.end.time`).d('结束时间'),
    },
    fields.currentStatus(),
    {
      name: 'errorMessage',
      type: 'string',
      label: intl.get(`${prefix}.model.otaUpgradeErrorMsg`).d('失败原因'),
    },
    {
      name: 'percentage',
      type: 'string',
      label: intl.get(`${prefix}.model.percentage`).d('升级进度'),
    },
  ],
});

// 重试DS
const retryDS = () => ({
  transport: {
    create: ({ data }) => {
      const { __id, _status, ...other } = Array.isArray(data) ? data[0] : {};
      return {
        url: `${API_HOST}${HZERO_HIOT}/v1/${orgId}/ota-tasks/retry`,
        method: 'POST',
        data: other,
      };
    },
  },
});

export {
  fields,
  retryDS,
  otaUpgradeTaskListDS,
  otaUpgradeTaskCreateDS,
  otaUpgradeTaskDetailInfoDS,
  otaUpgradeTaskDetailListDS,
  otaUpgradeTaskDeviceFilterList,
  otaUpgradeTaskGatewayFilterList,
};
