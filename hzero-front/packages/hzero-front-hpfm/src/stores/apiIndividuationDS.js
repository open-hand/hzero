import { isNull } from 'lodash';

import intl from 'utils/intl';
import { HZERO_PLATFORM } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import { CODE_UPPER } from 'utils/regExp';

import { validateDuplicateCustomizeCode } from '@/services/apiIndividuationService';

const organizationId = getCurrentOrganizationId();

const initDS = () => ({
  autoQuery: true,
  selection: false,
  dataKey: 'content',
  queryFields: [
    {
      name: 'customizeCode',
      type: 'string',
      label: intl.get('hpfm.apiIndividuation.model.apiIndividuation.customizeCode').d('个性化编码'),
    },
    {
      name: 'customizeName',
      type: 'string',
      label: intl.get('hpfm.apiIndividuation.model.apiIndividuation.customizeName').d('个性化名称'),
    },
    {
      name: 'serviceName',
      type: 'string',
      label: intl.get('hpfm.apiIndividuation.model.apiIndividuation.serviceName').d('个性化服务'),
    },
    {
      name: 'applyTenants',
      type: 'object',
      label: intl.get('hpfm.apiIndividuation.model.apiIndividuation.applyTenantIds').d('生效租户'),
      lovCode: 'HPFM.TENANT_ENCRYPT',
      ignore: 'always',
    },
    {
      name: 'applyTenantId',
      type: 'string',
      bind: 'applyTenants.tenantId',
    },
    {
      name: 'customizePosition',
      type: 'string',
      label: intl
        .get('hpfm.apiIndividuation.model.apiIndividuation.customizePosition')
        .d('切入位置'),
      lookupCode: 'HPFM.API_CUSTOMIZE_POSITION',
    },
    {
      name: 'customizeStatus',
      type: 'string',
      label: intl.get('hpfm.apiIndividuation.model.apiIndividuation.unApplied').d('应用状态'),
      lookupCode: 'HPFM.API_CUSTOMIZE_STATUS',
    },
    {
      name: 'syncFlag',
      type: 'string',
      label: intl.get('hpfm.apiIndividuation.model.apiIndividuation.syncFlag').d('执行方式'),
      lookupCode: 'HPFM.API_CUSTOMIZE.SYNC_FLAG',
    },
  ],
  fields: [
    {
      name: 'customizeCode',
      type: 'string',
      label: intl.get('hpfm.apiIndividuation.model.apiIndividuation.customizeCode').d('个性化编码'),
    },
    {
      name: 'customizeName',
      type: 'string',
      label: intl.get('hpfm.apiIndividuation.model.apiIndividuation.customizeName').d('个性化名称'),
    },
    {
      name: 'customizePosition',
      type: 'string',
      label: intl
        .get('hpfm.apiIndividuation.model.apiIndividuation.customizePosition')
        .d('切入位置'),
      lookupCode: 'HPFM.API_CUSTOMIZE_POSITION',
    },
    {
      name: 'unApplied',
      type: 'boolean',
      label: intl.get('hpfm.apiIndividuation.model.apiIndividuation.unApplied').d('应用状态'),
    },
    {
      name: 'serviceName',
      type: 'string',
      label: intl.get('hpfm.apiIndividuation.model.apiIndividuation.serviceName').d('个性化服务'),
    },
    {
      name: 'syncFlag',
      type: 'string',
      lookupCode: 'HPFM.API_CUSTOMIZE.SYNC_FLAG',
      label: intl.get('hpfm.apiIndividuation.model.apiIndividuation.syncFlag').d('执行方式'),
    },
    // {
    //   name: 'enabledFlag',
    //   type: 'number',
    //   label: intl.get('hpfm.apiIndividuation.model.apiIndividuation.enabledFlag').d('是否启用'),
    // },
    {
      name: 'action',
      label: intl.get('hzero.common.button.action').d('操作'),
    },
  ],
  transport: {
    read: ({ params, data }) => ({
      url: `${HZERO_PLATFORM}/v1${isTenantRoleLevel() ? `/${organizationId}` : ``}/api-cuszs`,
      method: 'GET',
      params: { ...params, ...data },
    }),
    destroy: ({ data }) => {
      const { customizeId } = data[0];
      return {
        url: `${HZERO_PLATFORM}/v1${
          isTenantRoleLevel() ? `/${organizationId}` : ``
        }/api-cuszs/${customizeId}`,
        method: 'DELETE',
      };
    },
  },
});

const applyDS = () => ({
  fields: [
    {
      name: 'applyStatus',
      type: 'string',
      label: intl.get('hpfm.apiIndividuation.model.apiIndividuation.applyStatus').d('应用状态'),
      lookupCode: 'HPFM.API_CUSTOMIZE_APPLY_STATUS',
    },
    {
      name: 'logContent',
      type: 'string',
      label: intl.get('hpfm.apiIndividuation.model.apiIndividuation.logContent').d('应用日志'),
    },
  ],
  transport: {
    read({ dataSet }) {
      return {
        url: `${HZERO_PLATFORM}/v1${
          isTenantRoleLevel() ? `/${organizationId}` : ``
        }/api-cuszs/apply`,
        method: 'PUT',
        params: {},
        data: dataSet.queryParameter.customizeIds,
      };
    },
  },
});

const unApplyDS = () => ({
  fields: [
    {
      name: 'applyStatus',
      type: 'string',
      label: intl.get('hpfm.apiIndividuation.model.apiIndividuation.applyStatus').d('应用状态'),
      lookupCode: 'HPFM.API_CUSTOMIZE_APPLY_STATUS',
    },
    {
      name: 'logContent',
      type: 'string',
      label: intl.get('hpfm.apiIndividuation.model.apiIndividuation.logContent').d('应用日志'),
    },
  ],
  transport: {
    read({ dataSet }) {
      return {
        url: `${HZERO_PLATFORM}/v1${
          isTenantRoleLevel() ? `/${organizationId}` : ``
        }/api-cuszs/cancel-apply`,
        method: 'PUT',
        params: {},
        data: dataSet.queryParameter.customizeIds,
      };
    },
  },
});

const enableDS = () => ({
  transport: {
    read({ dataSet }) {
      return {
        url: `${HZERO_PLATFORM}/v1${
          isTenantRoleLevel() ? `/${organizationId}` : ``
        }/api-cuszs/enable`,
        method: 'PUT',
        params: {},
        data: dataSet.queryParameter.customizeIds,
      };
    },
  },
});

const disableDS = () => ({
  transport: {
    read({ dataSet }) {
      return {
        url: `${HZERO_PLATFORM}/v1${
          isTenantRoleLevel() ? `/${organizationId}` : ``
        }/api-cuszs/disable`,
        method: 'PUT',
        params: {},
        data: dataSet.queryParameter.customizeIds,
      };
    },
  },
});

const detailDS = () => {
  return {
    autoCreate: true,
    paging: false,
    autoQueryAfterSubmit: false,
    autoQuery: false,
    fields: [
      {
        name: 'current',
        type: 'number',
      },
      {
        name: 'customizeCode',
        type: 'string',
        label: intl
          .get('hpfm.apiIndividuation.model.apiIndividuation.customizeCode')
          .d('个性化编码'),
        dynamicProps: {
          required({ record }) {
            return !record.get('current');
          },
        },
        validator: async (value, _, record) => {
          // if (!value) {
          //   return intl
          //   .get('hpfm.apiIndividuation.view.message.customizeCode.unique')
          //   .d('个性化编码重复');
          // }
          if (!CODE_UPPER.test(value)) {
            return intl
              .get('hzero.common.validation.codeUpper')
              .d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”');
          }
          if (record.get('customizeId')) {
            return true;
          }
          const res = await validateDuplicateCustomizeCode(value);
          if (!res) {
            return intl
              .get('hpfm.apiIndividuation.view.message.customizeCode.unique')
              .d('个性化编码重复');
          }
          return true;
        },
      },
      {
        name: 'customizeName',
        type: 'intl',
        label: intl
          .get('hpfm.apiIndividuation.model.apiIndividuation.customizeName')
          .d('个性化名称'),
        dynamicProps: {
          required({ record }) {
            return !record.get('current');
          },
        },
      },
      {
        name: 'applyTenants',
        type: 'object',
        label: intl
          .get('hpfm.apiIndividuation.model.apiIndividuation.applyTenantIds')
          .d('生效租户'),
        lovCode: 'HPFM.TENANT_ENCRYPT',
      },
      {
        name: 'applyTenantIds',
        type: 'string',
        label: intl
          .get('hpfm.apiIndividuation.model.apiIndividuation.applyTenantIds')
          .d('生效租户'),
        bind: 'applyTenants.tenantId',
      },
      {
        name: 'serviceObj',
        type: 'object',
        label: intl.get('hpfm.apiIndividuation.model.apiIndividuation.serviceName').d('个性化服务'),
        dynamicProps: {
          required({ record }) {
            return !record.get('current');
          },
        },
        lovCode: 'HADM.INSTANCE',
      },
      {
        name: 'serviceName',
        type: 'string',
        bind: 'serviceObj.service',
        label: intl.get('hpfm.apiIndividuation.model.apiIndividuation.serviceName').d('个性化服务'),
      },
      {
        name: 'syncFlag',
        type: 'string',
        lookupCode: 'HPFM.API_CUSTOMIZE.SYNC_FLAG',
        label: intl.get('hpfm.apiIndividuation.model.apiIndividuation.syncFlag').d('执行方式'),
        dynamicProps: {
          required({ record }) {
            return !record.get('current');
          },
        },
        defaultValue: '1',
      },
      // {
      //   name: 'enabledFlag',
      //   type: 'number',
      //   label: intl.get('hpfm.apiIndividuation.model.apiIndividuation.enabledFlag').d('是否启用'),
      //   trueValue: 1,
      //   falseValue: 0,
      //   defaultValue: 1,
      // },
      {
        name: 'customizePosition',
        type: 'string',
        label: intl
          .get('hpfm.apiIndividuation.model.apiIndividuation.customizePosition')
          .d('切入位置'),
        lookupCode: 'HPFM.API_CUSTOMIZE_POSITION',
        dynamicProps: {
          required({ record }) {
            return record.get('current') === 1;
          },
        },
      },

      {
        name: 'packageName',
        type: 'string',
        label: intl.get('hpfm.apiIndividuation.model.apiIndividuation.packageName').d('包路径'),
        dynamicProps: {
          required({ record }) {
            return record.get('current') === 1;
          },
        },
      },
      {
        name: 'className',
        type: 'string',
        label: intl.get('hpfm.apiIndividuation.model.apiIndividuation.className').d('类名'),
        dynamicProps: {
          required({ record }) {
            return record.get('current') === 1;
          },
        },
      },
      {
        name: 'methodName',
        type: 'string',
        label: intl.get('hpfm.apiIndividuation.model.apiIndividuation.methodName').d('方法名'),
        dynamicProps: {
          required({ record }) {
            return record.get('current') === 1;
          },
        },
      },
      {
        name: 'methodArgs',
        type: 'string',
        label: intl
          .get('hpfm.apiIndividuation.model.apiIndividuation.methodArgs')
          .d('方法参数列表'),
      },
      {
        name: 'methodReturn',
        type: 'string',
        label: intl
          .get('hpfm.apiIndividuation.model.apiIndividuation.methodReturn')
          .d('方法返回值类型'),
      },
      {
        name: 'contentType',
        type: 'string',
        label: intl.get('hpfm.apiIndividuation.model.apiIndividuation.contentType').d('个性化类别'),
        lookupCode: 'HPFM.API_CUSTOMIZE_CODE_TYPE',
        dynamicProps: {
          required({ record }) {
            return record.get('current') === 2;
          },
        },
      },
      {
        name: 'versionNumber',
        type: 'string',
        label: intl.get('hpfm.apiIndividuation.model.apiIndividuation.versionNumber').d('版本'),
        // pattern: CODE_UPPER,
        // defaultValidationMessages: {
        //   patternMismatch: intl
        //     .get('hzero.common.validation.codeUpper')
        //     .d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
        // },
      },
      // {
      //   name: 'newVersionNumber',
      //   type: 'string',
      //   label: intl.get('hpfm.apiIndividuation.model.apiIndividuation.newVersionNumber').d('新版本'),
      //   validator: async (value, _, record) => {
      //     if (!value) {
      //       return true;
      //     }
      //     if (!CODE_UPPER.test(value)) {
      //       return intl
      //         .get('hzero.common.validation.codeUpper')
      //         .d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”');
      //     }
      //     const customizeId = record.get('customizeId');
      //     if (customizeId) {
      //       const res = await validateDuplicateVersion({ versionNumber: value, customizeId });
      //       if (!res) {
      //         return intl
      //           .get('hpfm.apiIndividuation.view.message.versionNumber.unique')
      //           .d('版本号不可用');
      //       }
      //     }
      //     return true;
      //   },
      // },
      {
        name: 'customizeContent',
        type: 'string',
        label: intl
          .get('hpfm.apiIndividuation.model.apiIndividuation.customizeContent')
          .d('个性化代码'),
        dynamicProps: {
          required({ record }) {
            return record.get('current') === 2;
          },
        },
      },
      {
        name: 'quickPoint',
        type: 'object',
        lovCode: 'HADM.INSTANCE',
      },
      {
        name: 'template',
        type: 'object',
        lovCode: 'HPFM.SITE_COMMON_TEMPLATE',
        lovPara: { templateCategoryCode: 'API_CUSZ', enabledFlag: 1, tenantId: 0 },
      },
    ],
    transport: {
      read: ({ data }) => {
        return {
          url: `${HZERO_PLATFORM}/v1${isTenantRoleLevel() ? `/${organizationId}` : ``}/api-cuszs/${
            data.customizeId
          }`,
          method: 'GET',
          params: {},
          data: {},
        };
      },
      create: ({ data }) => {
        const {
          _status,
          __id,
          current,
          serviceObj,
          applyTenantObjs,
          applyTenants,
          template,
          ...others
        } = data[0];
        return {
          url: `${HZERO_PLATFORM}/v1${isTenantRoleLevel() ? `/${organizationId}` : ``}/api-cuszs`,
          method: 'POST',
          data: others,
        };
      },
      update: ({ data }) => {
        const {
          _status,
          __id,
          current,
          serviceObj,
          applyTenantObjs,
          applyTenants,
          template,
          versionNumber,
          newVersionNumber,
          ...others
        } = data[0];
        return {
          url: `${HZERO_PLATFORM}/v1${isTenantRoleLevel() ? `/${organizationId}` : ``}/api-cuszs/${
            data[0].customizeId
          }`,
          method: 'PUT',
          data: {
            ...others,
            versionNumber: !isNull(newVersionNumber) ? newVersionNumber : versionNumber,
          },
        };
      },
    },
  };
};

const lovDS = () => {
  return {
    autoQuery: false,
    // fields: [

    // ],
    transport: {
      read() {
        return {
          url: `${HZERO_PLATFORM}/v1${
            isTenantRoleLevel() ? `/${organizationId}` : ``
          }/api-cusz-contents/template-render`,
          method: 'GET',
          params: {},
        };
      },
    },
  };
};

const pointDS = () => {
  return {
    autoQuery: true,
    selection: false,
    dataKey: 'content',
    queryFields: [
      {
        name: 'packageName',
        type: 'string',
        label: intl.get('hpfm.apiIndividuation.model.apiIndividuation.packageName').d('包路径'),
      },
      {
        name: 'className',
        type: 'string',
        label: intl.get('hpfm.apiIndividuation.model.apiIndividuation.className').d('类名'),
      },
      {
        name: 'methodName',
        type: 'string',
        label: intl.get('hpfm.apiIndividuation.model.apiIndividuation.methodName').d('方法名'),
      },
    ],
    fields: [
      {
        name: 'packageName',
        type: 'string',
        label: intl.get('hpfm.apiIndividuation.model.apiIndividuation.packageName').d('包路径'),
      },
      {
        name: 'className',
        type: 'string',
        label: intl.get('hpfm.apiIndividuation.model.apiIndividuation.className').d('类名'),
      },
      {
        name: 'methodName',
        type: 'string',
        label: intl.get('hpfm.apiIndividuation.model.apiIndividuation.methodName').d('方法名'),
      },
      {
        name: 'methodArgs',
        type: 'string',
        label: intl
          .get('hpfm.apiIndividuation.model.apiIndividuation.methodArgs')
          .d('方法参数列表'),
      },
      {
        name: 'methodReturn',
        type: 'string',
        label: intl
          .get('hpfm.apiIndividuation.model.apiIndividuation.methodReturn')
          .d('方法返回值类型'),
      },
      {
        name: 'action',
        label: intl.get('hzero.common.button.action').d('操作'),
      },
    ],
    transport: {
      read: ({ params, data }) => ({
        url: `${HZERO_PLATFORM}/v1${
          isTenantRoleLevel() ? `/${organizationId}` : ``
        }/api-cusz-points`,
        method: 'GET',
        params: { ...params, ...data },
      }),
    },
  };
};

const pointDrawerDS = () => {
  return {
    autoQuery: true,
    selection: false,
    paging: false,
    fields: [
      {
        name: 'packageNames',
        type: 'string',
        required: true,
        label: intl.get('hpfm.apiIndividuation.model.apiIndividuation.packageNames').d('包名'),
      },
      {
        name: 'serviceName',
        type: 'string',
      },
    ],
    transport: {
      create: ({ data }) => {
        const { packageNames, serviceName } = data[0];
        return {
          url: `${HZERO_PLATFORM}/v1${
            isTenantRoleLevel() ? `/${organizationId}` : ``
          }/api-cusz-points/refresh`,
          method: 'POST',
          params: { packageNames, serviceName },
          data: {},
        };
      },
    },
  };
};

const historyRollBackDS = () => {
  return {
    transport: {
      read: ({ data }) => {
        return {
          url: `${HZERO_PLATFORM}/v1${isTenantRoleLevel() ? `/${organizationId}` : ``}/api-cuszs/${
            data.customizeContentId
          }/rollback`,
          method: 'PUT',
          params: {},
          data: {},
        };
      },
    },
  };
};

const logDS = () => {
  return {
    fields: [
      {
        name: 'applyStatus',
        type: 'string',
        label: intl.get('hpfm.apiIndividuation.model.apiIndividuation.applyStatus').d('应用状态'),
        lookupCode: 'HPFM.API_CUSTOMIZE_APPLY_STATUS',
      },
      {
        name: 'logContent',
        type: 'string',
        label: intl.get('hpfm.apiIndividuation.model.apiIndividuation.logContent').d('应用日志'),
      },
    ],
    transport: {
      read: ({ dataSet }) => {
        return {
          url: `${HZERO_PLATFORM}/v1${
            isTenantRoleLevel() ? `/${organizationId}` : ``
          }/api-cuszs/apply`,
          method: 'PUT',
          params: {},
          data: dataSet.queryParameter.customizeIds,
        };
      },
    },
  };
};

const historyDS = () => {
  return {
    autoQuery: false,
    selection: false,
    dataKey: 'content',
    fields: [
      {
        name: 'contentType',
        type: 'string',
        label: intl
          .get('hpfm.apiIndividuation.model.apiIndividuation.customizeName')
          .d('个性化类别'),
      },
      {
        name: 'versionNumber',
        type: 'string',
        label: intl.get('hpfm.apiIndividuation.model.apiIndividuation.versionNumber').d('版本'),
      },
      {
        name: 'customizeContent',
        type: 'string',
        label: intl
          .get('hpfm.apiIndividuation.model.apiIndividuation.customizeContent')
          .d('个性化代码'),
      },
      {
        name: 'lastUpdateDate',
        type: 'string',
        label: intl
          .get('hpfm.apiIndividuation.model.apiIndividuation.lastUpdateDate')
          .d('最后更新时间'),
      },

      {
        name: 'action',
        label: intl.get('hzero.common.button.action').d('操作'),
      },
    ],
    transport: {
      read: ({ data }) => {
        return {
          url: `${HZERO_PLATFORM}/v1${
            isTenantRoleLevel() ? `/${organizationId}` : ``
          }/api-cusz-contents/${data.customizeId}/history`,
          method: 'GET',
          data: {},
        };
      },
      destroy: ({ data }) => {
        return {
          url: `${HZERO_PLATFORM}/v1${
            isTenantRoleLevel() ? `/${organizationId}` : ``
          }/api-cusz-contents/${data[0].customizeContentId}`,
          method: 'DELETE',
          params: {},
          data: {},
        };
      },
    },
  };
};

const logDrawerDS = () => {
  return {
    selection: false,
    dataKey: 'content',
    autoQuery: false,
    fields: [
      {
        name: 'creationDate',
        type: 'string',
        label: intl.get('hpfm.apiIndividuation.model.apiIndividuation.creationDate').d('创建时间'),
      },
      {
        name: 'applyStatus',
        type: 'string',
        label: intl.get('hpfm.apiIndividuation.model.apiIndividuation.applyStatus').d('应用状态'),
        lookupCode: 'HPFM.API_CUSTOMIZE_APPLY_STATUS',
      },
      {
        name: 'logContent',
        type: 'string',
        label: intl.get('hpfm.apiIndividuation.model.apiIndividuation.logContent').d('应用日志'),
      },

      {
        name: 'action',
        label: intl.get('hzero.common.button.action').d('操作'),
      },
    ],
    transport: {
      read: ({ data }) => {
        return {
          url: `${HZERO_PLATFORM}/v1${
            isTenantRoleLevel() ? `/${organizationId}` : ``
          }/api-cusz-logs/${data.customizeId}`,
          method: 'GET',
        };
      },
      destroy: ({ data }) => {
        return {
          url: `${HZERO_PLATFORM}/v1${
            isTenantRoleLevel() ? `/${organizationId}` : ``
          }/api-cusz-logs`,
          method: 'DELETE',
          params: {},
          data: data[0],
        };
      },
    },
  };
};

export {
  initDS,
  detailDS,
  logDS,
  lovDS,
  historyDS,
  logDrawerDS,
  historyRollBackDS,
  applyDS,
  unApplyDS,
  enableDS,
  disableDS,
  pointDS,
  pointDrawerDS,
};
