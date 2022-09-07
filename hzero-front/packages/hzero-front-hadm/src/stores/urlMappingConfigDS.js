/*
 * URLMappingConfig URL映射配置
 * @date: 2020-04-13
 * @author JMY <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2020, Hand
 */
import intl from 'utils/intl';
import { CODE } from 'utils/regExp';
import { HZERO_ADM } from 'utils/config';
import { getCurrentTenant, isTenantRoleLevel, getCurrentOrganizationId } from 'utils/utils';

const organizationId = getCurrentOrganizationId();
const isTenant = isTenantRoleLevel();
const { tenantId } = getCurrentTenant();
const apiPrefix = isTenant ? `${HZERO_ADM}/v1/${organizationId}` : `${HZERO_ADM}/v1`;

// 表格ds
const tableDS = () => {
  return {
    autoQuery: true,
    selection: false,
    dataKey: 'content',
    queryFields: [
      {
        name: 'urlRuleCode',
        type: 'string',
        label: intl.get('hadm.urlMappingConfig.model.urlMappingConfig.urlRuleCode').d('配置编码'),
      },
      {
        name: 'description',
        type: 'string',
        label: intl.get('hadm.urlMappingConfig.model.urlMappingConfig.description').d('配置描述'),
      },
      {
        name: 'fullSourceUrl',
        type: 'string',
        label: intl
          .get('hadm.urlMappingConfig.model.urlMappingConfig.fullSourceUrl')
          .d('来源完整URL'),
      },
      {
        name: 'fullTargetUrl',
        type: 'string',
        label: intl
          .get('hadm.urlMappingConfig.model.urlMappingConfig.fullTargetUrl')
          .d('目标完整URL'),
      },
      {
        name: 'sourceTenant',
        label: intl.get('hzero.common.model.common.tenantId').d('租户'),
        type: 'object',
        lovCode: 'HPFM.TENANT',
        // lovPara: { tenantId },
        ignore: 'always',
        noCache: true,
      },
      {
        name: 'sourceTenantId',
        type: 'string',
        bind: 'sourceTenant.tenantId',
      },
    ],
    fields: [
      {
        name: 'urlRuleCode',
        type: 'string',
        label: intl.get('hadm.urlMappingConfig.model.urlMappingConfig.urlRuleCode').d('配置编码'),
      },
      {
        name: 'description',
        type: 'string',
        label: intl.get('hadm.urlMappingConfig.model.urlMappingConfig.description').d('配置描述'),
      },
      {
        name: 'fullSourceUrl',
        type: 'string',
        label: intl
          .get('hadm.urlMappingConfig.model.urlMappingConfig.fullSourceUrl')
          .d('来源完整URL'),
      },
      {
        name: 'fullTargetUrl',
        type: 'string',
        label: intl
          .get('hadm.urlMappingConfig.model.urlMappingConfig.fullTargetUrl')
          .d('目标完整URL'),
      },
      {
        name: 'enabledFlag',
        type: 'number',
        label: intl.get('hzero.common.status').d('状态'),
      },
      {
        name: 'sourceTenantName',
        label: intl.get('hzero.common.model.common.tenantId').d('租户'),
        type: 'string',
      },
    ],
    transport: {
      read: () => ({
        url: `${apiPrefix}/url-rules`,
        method: 'GET',
      }),
      destroy: ({ data }) => {
        const { __id, _status, ...other } = Array.isArray(data) ? data[0] : {};
        return {
          data: other,
          url: `${apiPrefix}/url-rules`,
          method: 'DELETE',
        };
      },
    },
  };
};

const detailDS = () => {
  return {
    autoQuery: false,
    autoQueryAfterSubmit: false,
    // autoCreate: true,
    fields: [
      {
        name: 'urlRuleCode',
        type: 'string',
        required: true,
        label: intl.get('hadm.urlMappingConfig.model.urlMappingConfig.urlRuleCode').d('配置编码'),
        pattern: CODE,
        defaultValidationMessages: {
          patternMismatch: intl
            .get('hzero.common.validation.code')
            .d('大小写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
        },
        maxLength: 60,
      },
      {
        name: 'description',
        type: 'string',
        label: intl.get('hadm.urlMappingConfig.model.urlMappingConfig.description').d('配置描述'),
        maxLength: 240,
      },
      {
        name: 'sourceUrl',
        type: 'string',
        required: true,
        label: intl.get('hadm.urlMappingConfig.model.urlMappingConfig.sourceUrl').d('来源URL'),
        dynamicProps: ({ record }) => {
          return {
            required: !record.get('sourceService'),
          };
        },
        maxLength: 240,
      },
      {
        name: 'targetUrl',
        type: 'string',
        required: true,
        label: intl.get('hadm.urlMappingConfig.model.urlMappingConfig.targetUrl').d('目标URL'),
        dynamicProps: ({ record }) => {
          return {
            required: !record.get('targetService'),
          };
        },
        maxLength: 240,
      },
      {
        name: 'sourceService',
        type: 'object',
        lovCode: 'HADM.ROUTE.SERVICE_PATH',
        lovPara: { tenantId },
        ignore: 'always',
        noCache: true,
        required: true,
        textField: 'serviceCode',
        label: intl.get('hadm.urlMappingConfig.model.urlMappingConfig.sourceService').d('来源路由'),
        dynamicProps: ({ record }) => {
          return {
            required: !record.get('sourceUrl'),
          };
        },
      },
      {
        name: 'targetService',
        type: 'object',
        lovCode: 'HADM.ROUTE.SERVICE_PATH',
        lovPara: { tenantId },
        ignore: 'always',
        noCache: true,
        required: true,
        textField: 'serviceCode',
        label: intl.get('hadm.urlMappingConfig.model.urlMappingConfig.targetService').d('目标路由'),
        dynamicProps: ({ record }) => {
          return {
            required: !record.get('targetUrl'),
          };
        },
      },
      {
        name: 'sourceServiceId',
        type: 'string',
        bind: 'sourceService.serviceId',
      },
      {
        name: 'targetServiceId',
        type: 'string',
        bind: 'targetService.serviceId',
      },
      {
        name: 'targetServiceName',
        type: 'string',
        bind: 'targetService.name',
        // ignore: 'always',
      },
      {
        name: 'sourceServiceName',
        type: 'string',
        bind: 'sourceService.name',
        // ignore: 'always',
      },
      {
        name: 'sourceServiceCode',
        type: 'string',
        bind: 'sourceService.serviceCode',
      },
      {
        name: 'targetServiceCode',
        type: 'string',
        bind: 'targetService.serviceCode',
      },
      {
        name: 'fullSourceUrl',
        type: 'string',
        readOnly: true,
        label: intl
          .get('hadm.urlMappingConfig.model.urlMappingConfig.fullSourceUrl')
          .d('来源完整URL'),
      },
      {
        name: 'fullTargetUrl',
        type: 'string',
        readOnly: true,
        label: intl
          .get('hadm.urlMappingConfig.model.urlMappingConfig.fullTargetUrl')
          .d('目标完整URL'),
      },
      {
        name: 'sourceTenant',
        label: intl.get('hzero.common.model.common.tenantId').d('租户'),
        type: 'object',
        lovCode: 'HPFM.TENANT',
        // lovPara: { tenantId },
        ignore: 'always',
        noCache: true,
      },
      {
        name: 'sourceTenantId',
        type: 'string',
        bind: 'sourceTenant.tenantId',
      },
      {
        name: 'sourceTenantName',
        type: 'string',
        bind: 'sourceTenant.tenantName',
      },
    ],
    transport: {
      read: ({ dataSet }) => {
        const { urlRuleId } = dataSet;
        return {
          url: `${apiPrefix}/url-rules/${urlRuleId}`,
          method: 'GET',
          data: {},
          params: {},
        };
      },
      create: ({ data }) => {
        const { __id, _status, ...other } = Array.isArray(data) ? data[0] : {};
        return {
          url: `${apiPrefix}/url-rules`,
          method: 'POST',
          data: { ...other, tenantId, enabledFlag: 0 },
        };
      },
      update: ({ data }) => {
        const { __id, _status, ...other } = Array.isArray(data) ? data[0] : {};
        return {
          url: `${apiPrefix}/url-rules`,
          method: 'PUT',
          data: other,
        };
      },
    },
  };
};

// 详情页面下的表格信息DS
const detailTableDS = () => {
  return {
    selection: 'multiple',
    autoQuery: false,
    autoCreate: false,
    cacheSelection: true,
    queryFields: [
      {
        name: 'realName',
        label: intl.get('hzero.common1.model.common.tenantId').d('用户'),
        type: 'string',
      },
      {
        name: 'loginName',
        type: 'string',
        label: intl.get('hadm.urlMappingConfig.model.urlMappingConfig.loginName').d('登录名'),
      },
    ],
    fields: [
      {
        name: 'realName',
        label: intl.get('hzero.common1.model.common.tenantId').d('用户'),
        type: 'string',
      },
      {
        name: 'loginName',
        type: 'string',
        label: intl.get('hadm.urlMappingConfig.model.urlMappingConfig.loginName').d('登录名'),
      },
    ],
    transport: {
      read: ({ params, dataSet }) => {
        const { urlRuleId, sourceTenantId } = dataSet;
        return {
          url: `${apiPrefix}/url-rule-users/user-list`,
          method: 'GET',
          params: {
            ...params,
            urlRuleId,
            tenantId: sourceTenantId,
          },
        };
      },
      destroy: ({ data }) => {
        return {
          url: `${apiPrefix}/url-rule-users/batch-delete`,
          method: 'DELETE',
          data,
        };
      },
    },
  };
};

const enabledDS = () => {
  return {
    autoCreate: false,
    autoQuery: false,
    autoQueryAfterSubmit: false,
    fields: [],
    transport: {
      create: ({ data }) => {
        const { __id, _status, ...other } = Array.isArray(data) ? data[0] : {};
        return {
          url: `${apiPrefix}/url-rules/enabled`,
          method: 'POST',
          data: other,
        };
      },
    },
  };
};

const tableDrawerDS = () => {
  return {
    autoCreate: false,
    cacheSelection: true,
    selection: 'multiple',
    autoQuery: false,
    dataKey: 'content',
    autoQueryAfterSubmit: false,
    queryFields: [
      {
        name: 'realName',
        label: intl.get('hzero.common1.model.common.tenantId').d('用户'),
        type: 'string',
      },
      {
        name: 'loginName',
        type: 'string',
        label: intl.get('hadm.urlMappingConfig.model.urlMappingConfig.loginName').d('登录名'),
      },
    ],
    fields: [
      {
        name: 'realName',
        label: intl.get('hzero.common1.model.common.tenantId').d('用户'),
        type: 'string',
      },
      {
        name: 'loginName',
        type: 'string',
        label: intl.get('hadm.urlMappingConfig.model.urlMappingConfig.loginName').d('登录名'),
      },
    ],
    transport: {
      read: ({ dataSet, params }) => {
        const { urlRuleId, sourceTenantId } = dataSet;
        return {
          url: `${HZERO_ADM}/v1/url-rule-users/users`,
          method: 'GET',
          params: {
            ...params,
            urlRuleId,
            tenantId: sourceTenantId,
          },
        };
      },
      create: ({ data, dataSet }) => {
        const { urlRuleId } = dataSet;
        const { sourceTenant = [] } = Array.isArray(data) ? data[0] : [];
        const newData = sourceTenant.map((item) => {
          return {
            sourceUserId: item.sourceUserId,
            urlRuleId,
          };
        });
        return {
          url: `${apiPrefix}/url-rule-users/batch-create`,
          method: 'POST',
          data: newData,
        };
      },
    },
  };
};

const addDS = () => {
  return {
    autoCreate: false,
    autoQuery: false,
    autoQueryAfterSubmit: false,
    fields: [],
    transport: {
      create: ({ data, dataSet }) => {
        const { urlRuleId } = dataSet;
        const { sourceTenant = [] } = Array.isArray(data) ? data[0] : [];
        const newData = sourceTenant.map((item) => {
          return {
            sourceUserId: item.sourceUserId,
            urlRuleId,
          };
        });
        return {
          url: `${apiPrefix}/url-rule-users/batch-create`,
          method: 'POST',
          data: newData,
        };
      },
    },
  };
};

export { tableDS, detailDS, enabledDS, detailTableDS, tableDrawerDS, addDS };
