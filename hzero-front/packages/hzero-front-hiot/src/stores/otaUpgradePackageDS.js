/**
 * @Author: zhengmin.liang <zhengmin.liang@hand-china.com>
 * @Create time: 2019/11/26
 * @Copyright: Copyright(c) 2019, Hand
 * @Description: OTA升级包DS
 */
import { getCurrentOrganizationId } from 'utils/utils';
import intl from 'utils/intl';
import { API_HOST, HZERO_HIOT } from 'utils/config';
import { VERSION_REG } from '@/utils/constants';

const prefix = 'hiot.otaPackage.model';
const orgId = getCurrentOrganizationId();

const packField = (field) => (items) => ({ ...field, ...items });

const fields = {
  templateName: (items) =>
    packField({
      name: 'thingModelName',
      type: 'string',
      label: intl.get(`${prefix}.device.template.name`).d('模型名称'),
    })(items),
  templateNameGateWay: (items) =>
    packField({
      name: 'templateNameGateWay',
      label: intl.get(`${prefix}.device.template.name`).d('模型名称'),
    })(items),
  templateCode: (items) =>
    packField({
      name: 'thingModelCode',
      type: 'string',
      label: intl.get(`${prefix}.device.template.code`).d('模型编码'),
    })(items),
  versionNum: (items) =>
    packField({
      name: 'currentVersion',
      type: 'string',
      label: intl.get(`${prefix}.currentVersion.number`).d('版本号'),
    })(items),
  upgradeCategory: (items) =>
    packField({
      name: 'categoryCode',
      type: 'string',
      label: intl.get(`${prefix}.upgrade.type`).d('升级类别'),
      lookupCode: 'HIOT.OTA_PACKAGE_CATEGORY',
    })(items),
  versionDesc: (items) =>
    packField({
      name: 'updateLogs',
      type: 'string',
      label: intl.get(`${prefix}.version.description`).d('版本说明'),
    })(items),
  addingTime: (items) =>
    packField({
      name: 'creationDate',
      type: 'string',
      label: intl.get(`${prefix}.add.time`).d('添加时间'),
    })(items),
  chooseUpgradePackage: (items) =>
    packField({
      name: 'attachmentUrl',
      type: 'string',
      label: intl.get(`${prefix}.custom.url`).d('选择升级包'),
    })(items),
  signatureAlgorithm: (items) =>
    packField({
      name: 'signMethod',
      type: 'string',
      label: intl.get(`${prefix}.signature.algorithm`).d('签名算法'),
      lookupCode: 'HIOT.OTA_SIGN_METHOD',
    })(items),
  taskName: (items) =>
    packField({
      name: 'taskName',
      type: 'string',
      label: intl.get(`${prefix}.task.name`).d('任务名称'),
    })(items),
  startTime: (items) =>
    packField({
      name: 'realStartTime',
      type: 'string',
      label: intl.get(`${prefix}.start.time`).d('开始时间'),
    })(items),
  upgradePackage: (items) =>
    packField({
      name: 'fileName',
      type: 'string',
      label: intl.get(`${prefix}.upgrade.package`).d('升级包'),
    })(items),
  upgradePackageSize: (items) =>
    packField({
      name: 'fileSize',
      type: 'string',
      label: intl.get(`${prefix}.upgrade.package.size`).d('升级包大小'),
    })(items),
  signatureValue: (items) =>
    packField({
      name: 'signValue',
      type: 'string',
      label: intl.get(`${prefix}.signature.value`).d('签名值'),
    })(items),
  status: (items) =>
    packField({
      name: 'enabledFlag',
      type: 'boolean',
      label: intl.get('hzero.common.status').d('状态'),
      trueValue: 1,
      falseValue: 0,
      defaultValue: 1,
    })(items),
  description: (items) =>
    packField({
      name: 'updateLogs',
      type: 'string',
      label: intl.get('hzero.common.explain').d('说明'),
    })(items),
};

const otaUpgradePackageListDS = () => ({
  autoQuery: true,
  transport: {
    read() {
      return {
        url: `${API_HOST}${HZERO_HIOT}/v1/${orgId}/ota-packages`,
        method: 'get',
      };
    },
  },
  queryFields: [
    fields.templateName(), // 设备模型名称
    fields.templateCode(), // 设备模型编码
    fields.versionNum(), // 版本号
  ],
  fields: [
    {
      name: 'packageName',
      type: 'string',
      label: intl.get(`${prefix}.device.template.packageName`).d('升级包名称'),
    },
    {
      name: 'categoryCodeMeaning',
      type: 'string',
      label: intl.get(`${prefix}.upgrade.type`).d('升级类别'),
    },
    {
      name: 'protocolService',
      type: 'string',
      label: intl.get(`${prefix}.device.template.protocolService`).d('协议入口'),
    },
    fields.templateName(), // 设备模型名称
    fields.templateCode(), // 设备模型编码
    fields.versionNum(), // 版本号
    fields.upgradeCategory(), // 升级类别
    fields.status(), // 状态
    fields.versionDesc(), // 版本说明
    fields.addingTime(), // 添加时间
  ],
});

const createOTAUpgradePackageDS = () => ({
  transport: {
    create({ data }) {
      const { templateName, templateNameGateWay, ...otherData } = data[0];
      return {
        method: 'post',
        url: `${API_HOST}${HZERO_HIOT}/v1/${orgId}/ota-packages`,
        data: { ...otherData, ...templateName },
      };
    },
  },
  fields: [
    {
      name: 'packageName',
      type: 'intl',
      required: true,
      label: intl.get(`${prefix}.device.template.packageName`).d('升级包名称'),
    },
    fields.upgradeCategory({
      required: true,
      defaultValue: 'THING',
    }), // 升级类别
    fields.templateName({
      type: 'object',
      lovCode: 'HIOT.LOV.THING_MODEL',
      lovPara: { tenantId: orgId },
      ignore: 'always',
    }), // 模型名称-设备
    fields.templateNameGateWay({
      type: 'object',
      lovCode: 'HIOT.LOV.GATEWAY_MODEL',
      lovPara: { tenantId: orgId },
      ignore: 'always',
    }), // 模型名称-网关
    {
      name: 'thingModelId',
      type: 'string',
      bind: `${fields.templateName().name}.thingModelId`,
    },
    fields.templateCode({
      // 模型编码
      bind: `${fields.templateName().name}.thingModelCode`,
    }),
    fields.versionNum({
      required: true,
      pattern: VERSION_REG,
      defaultValidationMessages: {
        patternMismatch: intl
          .get('hiot.common.view.validation.versionValMsg')
          .d('请输入正确的版本号，格式为：X.X.X'),
      },
    }), // 版本号
    fields.chooseUpgradePackage({ required: true }), // 选择升级包
    fields.signatureAlgorithm({ required: true }), // 签名算法
    fields.status(), // 状态
    fields.versionDesc({ maxLength: 255 }), // 版本说明
    {
      name: 'protocolService',
      type: 'string',
      label: intl.get(`${prefix}.device.template.protocolService`).d('协议入口'),
    },
  ],
});

const otaUpgradePackageDetailBasicDS = () => ({
  transport: {
    read: ({ data }) => {
      const { packageId } = data;
      return {
        method: 'get',
        url: `${API_HOST}${HZERO_HIOT}/v1/${orgId}/ota-packages/${packageId}`,
      };
    },
    update: ({ data }) => {
      const { __id, _status, ...other } = Array.isArray(data) ? data[0] : {};
      return {
        url: `${API_HOST}${HZERO_HIOT}/v1/${orgId}/ota-packages`,
        method: 'PUT',
        data: other,
      };
    },
  },
  fields: [
    {
      name: 'packageName',
      type: 'string',
      label: intl.get(`${prefix}.device.template.packageName`).d('升级包名称'),
    },
    fields.upgradeCategory(),
    fields.templateName(),
    fields.templateCode(),
    fields.versionNum({
      required: true,
      pattern: VERSION_REG,
      defaultValidationMessages: {
        patternMismatch: intl
          .get('hiot.common.view.validation.versionValMsg')
          .d('请输入正确的版本号，格式为：X.X.X'),
      },
    }), // 版本号
    fields.upgradePackage(),
    fields.upgradePackageSize({ name: 'fileSizeMB' }),
    fields.signatureAlgorithm({ required: true }),
    fields.chooseUpgradePackage({ required: true }), // 选择升级包
    fields.signatureValue(),
    {
      name: 'categoryCodeMeaning',
      type: 'string',
      label: intl.get(`${prefix}.upgrade.type`).d('升级类别'),
    },
    fields.status(),
    fields.description(),
    {
      name: 'protocolService',
      type: 'string',
      label: intl.get(`${prefix}.device.template.protocolService`).d('协议入口'),
    },
  ],
});

const otaUpgradePackageCreatedTaskDS = () => ({
  transport: {
    read: ({ data }) => {
      const { packageId } = data;
      return {
        method: 'get',
        url: `${API_HOST}${HZERO_HIOT}/v1/${orgId}/ota-tasks/package-task/${packageId}`,
      };
    },
  },
  fields: [fields.taskName(), fields.startTime()],
});

const otaUpgradePackageStatusCtrlDS = () => ({
  transport: {
    create: ({ data }) => {
      const subData = data[0];
      const statusFlag = subData[fields.status().name];
      const status = Number(statusFlag) ? 'disable' : 'enable';
      const url = `${API_HOST}${HZERO_HIOT}/v1/${orgId}/ota-packages/${status}`;
      return { url, method: 'put', data: subData };
    },
  },
});

export {
  fields,
  otaUpgradePackageListDS,
  createOTAUpgradePackageDS,
  otaUpgradePackageDetailBasicDS,
  otaUpgradePackageCreatedTaskDS,
  otaUpgradePackageStatusCtrlDS,
};
