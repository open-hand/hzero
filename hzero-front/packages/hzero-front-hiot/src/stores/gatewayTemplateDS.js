/**
 * @date 2019-12-02
 * @author: na.yi <na.yi@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import intl from 'utils/intl';
import { getCurrentOrganizationId } from 'utils/utils';
import { HZERO_HIOT } from '@/utils/constants';

const orgId = getCurrentOrganizationId();

const commonFields = [
  {
    name: 'thingModelCode',
    type: 'string',
    label: intl.get('hiot.common.code').d('编码'),
    required: true,
  },
  {
    name: 'thingModelName',
    type: 'intl',
    label: intl.get('hiot.common.name').d('名称'),
    maxLength: 20,
    required: true,
  },
  {
    name: 'enabled',
    type: 'boolean', // 为boolean将在表格中展示checkbox
    label: intl.get('hzero.common.status.enable').d('启用'),
    trueValue: 1,
    falseValue: 0,
    defaultValue: 1,
  },
  {
    name: 'description',
    type: 'string',
    label: intl.get('hzero.common.explain').d('说明'),
    maxLength: 100,
  },
];

const gatewayTemplateListDS = () => ({
  autoQuery: true,
  transport: {
    read: () => ({
      url: `${HZERO_HIOT}/v1/${orgId}/gateway-models/query`,
      method: 'get',
    }),
    destroy: ({ data }) => {
      const deviceTempIds = data.map((item) => item.thingModelId);
      return {
        url: `${HZERO_HIOT}/v1/${orgId}/gateway-models/delete`,
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
      name: 'enabled',
      type: 'string',
      label: intl.get('hzero.common.status.enable').d('启用'),
      lookupCode: 'HPFM.ENABLED_FLAG',
      selection: 'single',
    },
    {
      name: 'isReferred',
      type: 'string',
      label: intl.get('hiot.common.isReferred').d('是否被引用'),
      lookupCode: 'HIOT.REFERRED_STATUS',
    },
  ],
  fields: [
    ...commonFields,
    {
      name: 'consumerName',
      type: 'string',
      label: intl.get('hiot.gatewayTemplate.model.gateway.consumerName').d('消费者组'),
    },
    {
      name: 'isReferred',
      type: 'number',
      label: intl.get('hiot.common.isReferred').d('是否被引用'),
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
  paging: false,
  autoQueryAfterSubmit: false,
  autoQuery: false,
  fields: [
    ...commonFields,
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
      type: 'string',
      label: intl.get('hiot.common.model.common.platform').d('云平台'),
      bind: 'configLov.platformName',
      ignore: 'always',
    },
  ],
  transport: {
    read: ({ data }) => {
      // 根据id查询详情
      const { id } = data;
      return {
        url: `${HZERO_HIOT}/v1/${orgId}/gateway-models/${id}`,
        method: 'get',
        data: {},
      };
    },
    create: ({ data }) => {
      const { method, ...restFields } = data[0];
      return {
        url: `${HZERO_HIOT}/v1/${orgId}/gateway-models/${
          method === 'put' || method === 'PUT' ? 'update' : 'create'
        }`,
        data: { thingModel: { ...restFields, category: 'GATEWAY' } },
        method: method || 'post',
      };
    },
    update: ({ data }) => ({
      url: `${HZERO_HIOT}/v1/${orgId}/gateway-models/update`,
      data: { thingModel: { ...data[0], category: 'GATEWAY' } },
      method: 'put',
    }),
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

export {
  gatewayTemplateListDS, // 列表界面
  detailDS, // 编辑修改界面
  registerInfoDS,
};
