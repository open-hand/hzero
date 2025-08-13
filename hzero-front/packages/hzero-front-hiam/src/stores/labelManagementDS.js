import intl from 'utils/intl';
import { HZERO_IAM } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import { CODE } from 'utils/regExp';

const organizationId = getCurrentOrganizationId();
const apiPrefix = isTenantRoleLevel() ? `${HZERO_IAM}/v1/${organizationId}` : `${HZERO_IAM}/v1`;

const initDS = () => ({
  autoQuery: true,
  selection: false,
  dataKey: 'content',
  queryFields: [
    {
      name: 'name',
      type: 'string',
      label: intl.get('hiam.labelManagement.model.labelManagement.code').d('标签编码'),
    },
    {
      name: 'type',
      type: 'string',
      label: intl.get('hiam.labelManagement.model.labelManagement.type').d('类型'),
      lookupCode: 'HIAM.TAG_TYPE',
    },
    // {
    //   name: 'fdLevel',
    //   type: 'string',
    //   label: intl.get('hiam.labelManagement.model.labelManagement.level').d('层级'),
    //   lookupCode: 'HPFM.DATA_TENANT_LEVEL',
    // },
  ],
  fields: [
    {
      name: 'name',
      type: 'string',
      label: intl.get('hiam.labelManagement.model.labelManagement.code').d('标签编码'),
    },
    {
      name: 'typeMeaning',
      type: 'string',
      label: intl.get('hiam.labelManagement.model.labelManagement.type').d('类型'),
      lookupCode: 'HIAM.TAG_TYPE',
    },
    {
      name: 'levelMeaning',
      type: 'string',
      label: intl.get('hiam.labelManagement.model.labelManagement.level').d('层级'),
      lookupCode: 'HPFM.DATA_TENANT_LEVEL',
    },
    {
      name: 'tag',
      type: 'string',
      label: intl.get('hiam.labelManagement.model.labelManagement.tag').d('标识'),
    },
    {
      name: 'tagMeaning',
      type: 'string',
      label: intl.get('hiam.labelManagement.model.labelManagement.tag').d('标识'),
    },
    {
      name: 'enabledFlag',
      type: 'boolean',
      label: intl.get('hiam.labelManagement.model.labelManagement.enabledFlag').d('状态'),
      trueValue: 1,
      falseValue: 0,
      defaultValue: 1,
    },
    {
      name: 'description',
      type: 'string',
      label: intl.get('hiam.labelManagement.model.labelManagement.description').d('描述'),
    },
    {
      name: 'action',
      label: intl.get('hzero.common.button.action').d('操作'),
    },
    {
      name: 'presetFlag',
      type: 'boolean',
      label: intl.get('hiam.labelManagement.model.labelManagement.presetFlag').d('是否内置标签'),
      trueValue: 1,
      falseValue: 0,
      defaultValue: 1,
    },
    {
      name: 'inheritFlag',
      type: 'boolean',
      label: intl.get('hiam.labelManagement.model.labelManagement.inheritFlag').d('是否可继承'),
      trueValue: 1,
      falseValue: 0,
      defaultValue: 1,
    },
    {
      name: 'visibleFlag',
      type: 'boolean',
      label: intl.get('hiam.labelManagement.model.labelManagement.visibleFlag').d('是否页面可见'),
      trueValue: 1,
      falseValue: 0,
      defaultValue: 1,
    },
  ],
  transport: {
    read: ({ params, data }) => ({
      url: `${apiPrefix}/labels`,
      method: 'GET',
      params: { ...params, ...data },
    }),
  },
});

// 模态框ds
const drawerDS = () => ({
  autoQuery: false,
  autoQueryAfterSubmit: false,
  fields: [
    {
      name: 'name',
      type: 'string',
      label: intl.get('hiam.labelManagement.model.labelManagement.code').d('标签编码'),
      required: true,
      pattern: CODE,
      defaultValidationMessages: {
        patternMismatch: intl
          .get('hzero.common.validation.code')
          .d('大小写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
      },
    },
    {
      name: 'type',
      type: 'string',
      label: intl.get('hiam.labelManagement.model.labelManagement.type').d('类型'),
      lookupCode: 'HIAM.TAG_TYPE',
      required: true,
    },
    // {
    //   name: 'fdLevel',
    //   type: 'string',
    //   label: intl.get('hiam.labelManagement.model.labelManagement.level').d('层级'),
    //   lookupCode: 'HPFM.DATA_TENANT_LEVEL',
    //   required: true,
    // },
    {
      name: 'tag',
      type: 'string',
      label: intl.get('hiam.labelManagement.model.labelManagement.tag').d('标识'),
      lookupCode: 'HIAM.API_TAG_TYPE',
    },
    {
      name: 'enabledFlag',
      type: 'boolean',
      label: intl.get('hiam.labelManagement.model.labelManagement.enabledFlag').d('状态'),
      trueValue: 1,
      falseValue: 0,
      defaultValue: 1,
    },
    {
      name: 'inheritFlag',
      type: 'boolean',
      label: intl.get('hiam.labelManagement.model.labelManagement.inheritFlag').d('是否可继承'),
      trueValue: 1,
      falseValue: 0,
      defaultValue: 0,
    },
    {
      name: 'visibleFlag',
      type: 'boolean',
      label: intl.get('hiam.labelManagement.model.labelManagement.visibleFlag').d('是否页面可见'),
      trueValue: 1,
      falseValue: 0,
      defaultValue: 1,
    },
    {
      name: 'description',
      type: 'intl',
      label: intl.get('hiam.labelManagement.model.labelManagement.description').d('描述'),
      maxLength: 128,
    },
  ],
  transport: {
    read: ({ data }) => {
      const { id } = data;
      return {
        url: `${apiPrefix}/labels/${id}`,
        method: 'GET',
        data: {},
        params: {},
      };
    },
    create: ({ data }) => {
      const { __id, _status, ...other } = Array.isArray(data) ? data[0] : {};
      return {
        url: `${apiPrefix}/labels`,
        method: 'POST',
        data: other,
      };
    },
    update: ({ data }) => {
      const { __id, _status, ...other } = Array.isArray(data) ? data[0] : {};
      return {
        url: `${apiPrefix}/labels`,
        method: 'PUT',
        data: other,
      };
    },
  },
});

export { initDS, drawerDS };
