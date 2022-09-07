/**
 * @date 2019-11-09
 * @author: na.yi <na.yi@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import intl from 'utils/intl';
import { getCurrentOrganizationId } from 'utils/utils';
import { HZERO_HIOT } from '@/utils/constants';

const orgId = getCurrentOrganizationId();

const deviceTemplateListDS = () => ({
  primaryKey: 'thingModelId',
  autoQuery: true,
  transport: {
    read: () => ({
      url: `${HZERO_HIOT}/v1/${orgId}/thing-models/query`,
      method: 'get',
    }),
    destroy: ({ data }) => {
      const deviceTempIds = data.map((item) => item.thingModelId);
      return {
        url: `${HZERO_HIOT}/v1/${orgId}/thing-models/delete`,
        method: 'delete',
        data: deviceTempIds,
      };
    },
  },
  queryFields: [
    {
      name: 'thingModelCode',
      type: 'string',
      label: intl.get('hiot.common.code').d('编码'),
    },
    {
      name: 'thingModelName',
      type: 'string',
      label: intl.get('hiot.common.name').d('名称'),
    },
    {
      name: 'category',
      type: 'string',
      label: intl.get('hiot.common.device.type').d('设备类别'),
      lookupCode: 'HIOT.THING_CATEGORY',
    },
    {
      name: 'enabled',
      type: 'string',
      label: intl.get('hzero.common.status.enable').d('启用'),
      lookupCode: 'HPFM.ENABLED_FLAG',
      selection: 'single',
    },
    {
      name: 'dataPoint',
      type: 'object',
      label: intl.get('hiot.common.data.point').d('数据点'),
      lovCode: 'HIOT.LOV.PROPERTY_MODEL',
      lovPara: { tenantId: orgId },
      ignore: 'always',
    },
    {
      name: 'propertyModelId',
      type: 'string',
      label: intl.get('hiot.common.data.point').d('数据点'),
      bind: 'dataPoint.propertyModelId',
    },
    {
      name: 'isReferred',
      type: 'string',
      label: intl.get('hiot.common.isReferred').d('是否被引用'),
      lookupCode: 'HIOT.REFERRED_STATUS',
    },
  ],
  fields: [
    {
      name: 'thingModelCode',
      type: 'string',
      label: intl.get('hiot.common.code').d('编码'),
      required: true, // 当表格可编辑时可设置该属性使该字段为必输
    },
    {
      name: 'thingModelName',
      type: 'string',
      label: intl.get('hiot.common.name').d('名称'),
    },
    {
      name: 'categoryMeaning',
      type: 'string',
      label: intl.get('hiot.common.device.type').d('设备类别'),
    },
    {
      name: 'enabled',
      type: 'boolean', // 为boolean将在表格中展示checkbox
      label: intl.get('hzero.common.status.enable').d('启用'),
      trueValue: 1,
      falseValue: 0,
    },
    {
      name: 'description',
      type: 'string',
      label: intl.get('hzero.common.explain').d('说明'),
    },
    {
      name: 'consumerName',
      type: 'string',
      label: intl.get('hiot.deviceTemplate.model.device.consumerName').d('消费者组'),
    },
    {
      name: 'isReferred',
      type: 'string',
      label: intl.get('hiot.common.isReferred').d('是否被引用'),
      lookupCode: 'HIOT.REFERRED_STATUS',
    },
    {
      name: 'statusMeaning',
      type: 'string',
      label: intl.get('hiot.deviceTemplate.model.device.statusMeaning').d('设备状态'),
    },
    {
      name: 'configName',
      type: 'string',
      label: intl.get('hiot.common.model.common.configName').d('云账户'),
    },
    {
      name: 'platformMeaning',
      type: 'string',
      label: intl.get('hiot.common.model.common.platform').d('云平台'),
    },
  ],
});

const detailDS = () => ({
  primaryKey: 'thingModelId',
  autoQuery: false,
  paging: false,
  transport: {
    read: ({ data }) => {
      const { id } = data;
      return {
        url: `${HZERO_HIOT}/v1/${orgId}/thing-models/${id}`,
        method: 'get',
      };
    },
  },
  fields: [
    {
      name: 'thingModelCode',
      type: 'string',
      required: true,
    },
    {
      name: 'thingModelName',
      type: 'intl',
      required: true,
      maxLength: 45,
    },
    {
      name: 'category',
      type: 'string',
      lookupCode: 'HIOT.THING_CATEGORY',
      required: true,
    },
    {
      name: 'enabled',
      type: 'boolean',
      trueValue: 1,
      falseValue: 0,
      defaultValue: 1,
    },
    {
      name: 'description',
      type: 'string',
      maxLength: 100,
    },
    {
      name: 'cloudModelName',
      type: 'string',
      label: intl.get('hiot.common.ID').d('ID'),
    },
    {
      name: 'configLov',
      label: intl.get('hiot.common.model.common.platform').d('云平台'),
      type: 'object',
      lovCode: 'HIOT.LOV.CLOUD_ACCOUNT',
      lovPara: { tenantId: orgId },
      ignore: 'always',
      textField: 'platformName',
      noCache: true,
      required: true,
    },
    {
      name: 'configId',
      type: 'string',
      bind: 'configLov.configId',
    },
    {
      name: 'configName',
      type: 'string',
      bind: 'configLov.configName',
      ignore: 'always',
      required: true,
      label: intl.get('hiot.common.model.common.configName').d('云账户'),
    },
    {
      name: 'platformMeaning',
      required: true,
      type: 'string',
      label: intl.get('hiot.common.model.common.platform').d('云平台'),
      bind: 'configLov.platformName',
      ignore: 'always',
    },
  ],
});

const dataPointInfoTableDS = () => ({
  autoQuery: false,
  selection: false,
  idField: 'id',
  parentField: 'parentId',
  expandField: 'expand',
  transport: {
    read: () => ({
      url: `${HZERO_HIOT}/v1/${orgId}/thing-models/assign-prop`,
      method: 'get',
    }),
    destroy: ({ data }) => {
      const { thingModel = {}, deletePropertyModelList } = Array.isArray(data) ? data[0] : {};
      const { thingModelId, configId } = thingModel;
      return {
        data: deletePropertyModelList,
        url: `${HZERO_HIOT}/v1/${orgId}/thing-models/delete/property-model`,
        method: 'DELETE',
        params: {
          thingModelId,
          configId,
        },
      };
    },
  },
  fields: [
    {
      name: 'propertyModelCode',
      type: 'string',
      label: intl.get('hiot.common.code').d('编码'),
    },
    {
      name: 'propertyModelName',
      type: 'string',
      label: intl.get('hiot.common.name').d('名称'),
    },
    {
      name: 'category',
      type: 'string',
      label: intl.get('hzero.common.category').d('分类'),
      lookupCode: 'HIOT.PROPERTY_TYPE_CATEGORY',
    },
    {
      name: 'dataType',
      type: 'string',
      label: intl.get('hiot.common.data.type').d('数据类型'),
      lookupCode: 'HIOT.DATA_TYPE',
    },
    {
      name: 'unitCode',
      type: 'string',
      label: intl.get('hiot.common.measure.unit').d('测量单位'),
    },
    {
      name: 'minValue',
      type: 'number',
      label: intl.get('hzero.common.min.value').d('合理最小值'),
    },
    {
      name: 'maxValue',
      type: 'number',
      label: intl.get('hzero.common.max.value').d('合理最大值'),
    },
    {
      name: 'reportInterval',
      type: 'number',
      label: intl.get('hiot.common.collect.interval').d('采集间隔(S)'),
    },
  ],
});

const earlyWarningTableDS = () => ({
  primaryKey: 'predictRuleId',
  selection: false,
  fields: [
    {
      name: 'predictCode',
      type: 'string',
      label: intl.get('hiot.common.code').d('编码'),
      required: true,
    },
    {
      name: 'predictName',
      type: 'string',
      label: intl.get('hiot.common.name').d('名称'),
    },
    {
      name: 'formular',
      type: 'string',
      label: intl.get('hzero.common.title.formula').d('公式'),
    },
    {
      name: 'propertyName',
      type: 'string',
      label: intl.get('hiot.common.relevant.data.point').d('相关数据点'),
    },
  ],
});

const dataPointModalDS = () => ({
  primaryKey: 'propertyModelId',
  cacheSelection: true,
  transport: {
    read: ({ config, data }) => {
      const { type } = data;
      const url =
        type === 'group'
          ? `${HZERO_HIOT}/v1/${orgId}/thing-models/prop-groups`
          : `${HZERO_HIOT}/v1/${orgId}//thing-models/properties`;
      return {
        ...config,
        url,
        method: 'get',
      };
    },
  },
  fields: [
    {
      name: 'propertyModelName',
      type: 'string',
      label: intl.get('hiot.common.name').d('名称'),
    },
    {
      name: 'propertyModelCode',
      type: 'string',
      label: intl.get('hiot.common.code').d('编码'),
    },
    {
      name: 'description',
      type: 'string',
      label: intl.get('hzero.common.explain').d('说明'),
    },
  ],
  queryFields: [
    {
      name: 'keyWord',
      type: 'string',
      label: intl.get(`hiot.common.input.key-word`).d('输入关键字'),
    },
  ],
});

const operatorDS = () => ({
  transport: {
    create: ({ data }) => {
      const { method, ...restFields } = data[0];
      return {
        url: `${HZERO_HIOT}/v1/${orgId}/thing-models/${method === 'put' ? 'update' : 'create'}`,
        method,
        data: restFields,
      };
    },
  },
});

// 设备模板注册启停
const registerInfoDS = () => ({
  paging: false,
  transport: {
    read: () => {
      return {
        url: `${HZERO_HIOT}/v1/${orgId}/cloud-operate/thing-model/register-switch`,
        method: 'get',
      };
    },
  },
});

const actionDataPointDS = () => ({
  autoQuery: false,
  autoQueryAfterSubmit: false,
  autoCreate: false,
  transport: {
    create: ({ data }) => {
      const { propertyModelList, thingModel = {} } = Array.isArray(data) ? data[0] : {};
      const { thingModelId, configId } = thingModel;
      return {
        url: `${HZERO_HIOT}/v1/${orgId}/thing-models/create/property-model`,
        method: 'POST',
        data: propertyModelList,
        params: {
          thingModelId,
          configId,
        },
      };
    },
  },
});

export {
  deviceTemplateListDS,
  detailDS,
  dataPointInfoTableDS,
  earlyWarningTableDS,
  dataPointModalDS,
  operatorDS,
  registerInfoDS,
  actionDataPointDS,
};
