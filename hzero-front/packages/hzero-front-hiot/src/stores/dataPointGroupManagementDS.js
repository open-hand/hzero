/**
 * @date 2019-11-09
 * @author: na.yi <na.yi@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import intl from 'utils/intl';
import { getCurrentOrganizationId } from 'utils/utils';
import { HZERO_HIOT, CODE_UPPER_REG } from '@/utils/constants';

const orgId = getCurrentOrganizationId();
const prefix = 'hiot.dataPointGroupManagement.model.dpgm';

const commonFields = [
  {
    name: 'groupModelCode',
    type: 'string',
    label: intl.get('hiot.common.code').d('编码'),
    required: true,
    pattern: CODE_UPPER_REG,
    defaultValidationMessages: {
      patternMismatch: intl
        .get('hiot.common.view.validation.codeMsg')
        .d('全大写及数字，必须以字母、数字开头，可包含“_”'),
    },
  },
  {
    name: 'groupModelName',
    type: 'intl',
    label: intl.get('hiot.common.name').d('名称'),
    required: true,
    maxLength: 20,
  },
  {
    name: 'enabled',
    type: 'boolean',
    label: intl.get('hzero.common.status.enable').d('启用'),
    trueValue: 1,
    falseValue: 0,
  },
  {
    name: 'description',
    type: 'string',
    label: intl.get('hzero.common.explain').d('说明'),
    maxLength: 100,
  },
];

const dataPointGroupListDS = () => ({
  primaryKey: 'groupModelId',
  autoQuery: true,
  transport: {
    read: ({ data }) => ({
      url: `${HZERO_HIOT}/v1/${orgId}/prop-group-models`,
      method: 'get',
      data,
    }),
    destroy: ({ data }) => {
      const propGroupModelIds = data.map((item) => item.groupModelId);
      return {
        url: `${HZERO_HIOT}/v1/${orgId}/prop-group-models`,
        method: 'delete',
        data: propGroupModelIds,
      };
    },
  },
  queryFields: [
    {
      name: 'groupModelCode',
      type: 'string',
      label: intl.get('hiot.common.code').d('编码'),
    },
    {
      name: 'groupModelName',
      type: 'string',
      label: intl.get('hiot.common.name').d('名称'),
    },
    {
      name: 'enabled',
      type: 'string',
      label: intl.get('hzero.common.status.enable').d('启用'),
      lookupCode: 'HPFM.ENABLED_FLAG',
    },
    {
      name: 'dataPoint',
      type: 'object',
      label: intl.get('hiot.common.data.pointTemplate').d('数据点模板'),
      lovCode: 'HIOT.LOV.PROPERTY_MODEL',
      lovPara: { tenantId: orgId },
      ignore: 'always',
    },
    {
      name: 'propertyModelName',
      type: 'string',
      label: intl.get('hiot.common.data.point.name').d('数据点名称'),
      bind: 'dataPoint.propertyModelName',
    },
    {
      name: 'isReferred',
      type: 'string',
      label: intl.get('hiot.common.isReferred').d('是否被引用'),
      lookupCode: 'HIOT.REFERRED_STATUS',
    },
  ],
  // 表格里的字段
  fields: [
    ...commonFields,
    {
      name: 'isReferred',
      type: 'string',
      label: intl.get('hiot.common.isReferred').d('是否被引用'),
      lookupCode: 'HIOT.REFERRED_STATUS',
    },
  ],
});

const detailDS = () => ({
  primaryKey: 'groupModelId',
  autoQuery: false,
  autoQueryAfterSubmit: false,
  paging: false,
  transport: {
    read: ({ data }) => {
      const { groupId } = data;
      return {
        url: `${HZERO_HIOT}/v1/${orgId}/prop-group-models/${groupId}`,
        method: 'get',
        data: {},
      };
    },
    create: ({ data }) => {
      const param = { ...data[0] };
      const { __id, _status, ...restParam } = param;
      return {
        url: `${HZERO_HIOT}/v1/${orgId}/prop-group-models`,
        data: restParam,
        method: 'post',
      };
    },
    update: ({ data }) => ({
      url: `${HZERO_HIOT}/v1/${orgId}/prop-group-models`,
      data: data[0],
      method: 'put',
    }),
  },
  fields: [...commonFields],
});

const detailTableDS = () => ({
  primaryKey: 'dataPointId',
  autoQuery: false,
  selection: false,
  transport: {
    read: ({ data }) => {
      const { groupId } = data;
      return {
        url: `${HZERO_HIOT}/v1/${orgId}/prop-group-models/prop-model-rel/${groupId}`,
        method: 'get',
      };
    },
    submit: ({ data }) => ({
      url: `/${HZERO_HIOT}/v1/${orgId}/prop-group-models/prop-group-model-rel`,
      method: 'put',
      data,
    }),
  },
  fields: [
    {
      name: 'propertyModelCode',
      type: 'string',
      label: intl.get('hiot.common.code').d('编码'),
      pattern: CODE_UPPER_REG,
      defaultValidationMessages: {
        patternMismatch: intl
          .get('hiot.common.view.validation.codeMsg')
          .d('全大写及数字，必须以字母、数字开头，可包含“_”'),
      },
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

const modalDS = () => ({
  autoQuery: false,
  transport: {
    read: () => ({
      url: `${HZERO_HIOT}/v1/${orgId}/property-models/properties`,
      method: 'get',
    }),
  },
  queryFields: [
    {
      name: 'keyWord',
      type: 'string',
      label: intl.get(`hiot.common.input.key-word`).d('输入关键字'),
    },
  ],
  fields: [
    {
      name: 'propertyModelCode',
      type: 'string',
      label: intl.get('hiot.common.data.point.code').d('数据点编码'),
    },
    {
      name: 'propertyModelName',
      type: 'string',
      label: intl.get('hiot.common.data.point.name').d('数据点名称'),
    },
    {
      name: 'dataTypeMeaning',
      type: 'string',
      label: intl.get(`${prefix}.dp.type.name`).d('数据点类型名称'),
    },
  ],
});

const operatorDS = () => ({
  transport: {
    create: ({ data }) => {
      const { __id, _status, ...restFields } = data[0];
      return {
        url: `${HZERO_HIOT}/v1/${orgId}/prop-group-models/prop-group-model-rel`,
        method: 'put',
        data: restFields,
      };
    },
  },
  feedback: {
    submitSuccess: () => {
      // 拦截提交成功后的弹出信息
    },
  },
});

export {
  dataPointGroupListDS, // 数据点组模板列表DS
  detailDS,
  detailTableDS,
  modalDS,
  operatorDS,
};
