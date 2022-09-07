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

const commonDescription = [
  {
    name: 'description',
    type: 'string',
    label: intl.get('hzero.common.explain').d('说明'),
    maxLength: 100,
  },
];

const commonFields = [
  {
    name: 'typeCode',
    type: 'string',
    required: true,
    label: intl.get('hiot.common.code').d('编码'),
    pattern: CODE_UPPER_REG,
    defaultValidationMessages: {
      patternMismatch: intl
        .get('hiot.common.view.validation.codeMsg')
        .d('全大写及数字，必须以字母、数字开头，可包含“_”'),
    },
  },
  {
    name: 'typeName',
    type: 'intl',
    required: true,
    label: intl.get('hiot.common.name').d('名称'),
    maxLength: 45,
  },
  {
    name: 'category',
    type: 'string',
    required: true,
    label: intl.get('hzero.common.category').d('分类'),
    lookupCode: 'HIOT.PROPERTY_TYPE_CATEGORY',
  },
  {
    name: 'dataType',
    type: 'string',
    required: true,
    label: intl.get('hiot.common.data.type').d('数据类型'),
    lookupCode: 'HIOT.DATA_TYPE',
  },
];

const dataPointTypeListDS = () => ({
  primaryKey: 'typeId',
  autoQuery: true,
  transport: {
    read: () => ({
      url: `${HZERO_HIOT}/v1/${orgId}/property-types`,
      method: 'get',
    }),
    destroy: ({ data }) => {
      const ids = data.map(({ typeId }) => typeId);
      return {
        url: `${HZERO_HIOT}/v1/${orgId}/property-types`,
        method: 'delete',
        data: ids,
      };
    },
  },
  queryFields: [
    {
      name: 'typeCode',
      type: 'string',
      label: intl.get('hiot.common.code').d('编码'),
    },
    {
      name: 'typeName',
      type: 'string',
      label: intl.get('hiot.common.name').d('名称'),
    },
    {
      name: 'category',
      type: 'string',
      label: intl.get('hzero.common.category').d('分类'),
      lookupCode: 'HIOT.PROPERTY_TYPE_CATEGORY',
      selection: 'single',
    },
    {
      name: 'dataType',
      type: 'string',
      label: intl.get('hiot.common.data.type').d('数据类型'),
      lookupCode: 'HIOT.DATA_TYPE',
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
    ...commonDescription,
    {
      name: 'isReferred',
      type: 'string',
      label: intl.get('hiot.common.isReferred').d('是否被引用'),
      lookupCode: 'HIOT.REFERRED_STATUS',
    },
  ],
});

const dataPointTypeDetailDS = () => ({
  primaryKey: 'typeId',
  autoQuery: false,
  paging: false,
  transport: {
    read: ({ config, data }) => {
      // 根据id查询详情
      const { typeId } = data;
      return {
        ...config,
        url: `${HZERO_HIOT}/v1/${orgId}/property-types/${typeId}`,
        method: 'get',
      };
    },
    create: ({ data }) => ({
      url: `${HZERO_HIOT}/v1/${orgId}/property-types`,
      data: data[0],
      method: 'post',
    }),
    update: ({ data }) => ({
      url: `${HZERO_HIOT}/v1/${orgId}/property-types`,
      data: data[0],
      method: 'put',
    }),
  },
  fields: [...commonFields, ...commonDescription],
});

export {
  dataPointTypeListDS, // 数据点类型
  dataPointTypeDetailDS, // 数据点类型增删改查
};
