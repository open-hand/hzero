/**
 * @since 2019-12-03
 * @author WT <tao13.wang@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */

import { isUndefined, isArray, isEmpty, uniq, difference } from 'lodash';
import { DataSet } from 'choerodon-ui/pro';
import intl from 'utils/intl';
import { HZERO_IAM } from 'utils/config';
import { filterNullValueObject, getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';

const organizationId = getCurrentOrganizationId();
const optionDs = new DataSet({
  selection: 'single',
  autoQuery: true,
  paging: false,
  transport: {
    read: () => ({
      url: isTenantRoleLevel()
        ? `${HZERO_IAM}/v1/${organizationId}/labels/by-type`
        : `${HZERO_IAM}/v1/labels/by-type`,
      method: 'GET',
      params: {
        type: 'API',
      },
    }),
  },
});
// API管理查询from
const formDS = () => ({
  fields: [
    {
      name: 'code',
      type: 'string',
      label: intl.get('hiam.apiManagement.model.apiManagement.code').d('权限编码'),
    },
    {
      name: 'path',
      type: 'string',
      label: intl.get('hiam.apiManagement.model.apiManagement.path').d('路径'),
    },
    {
      name: 'serviceNameLov',
      type: 'object',
      lovCode: 'HADM.SERVICE_CODE',
      label: intl.get('hiam.apiManagement.model.apiManagement.serviceName').d('服务名称'),
      ignore: 'always',
      noCache: true,
    },
    {
      name: 'serviceName',
      type: 'string',
      bind: 'serviceNameLov.serviceName',
    },
    {
      name: 'fdLevel',
      type: 'string',
      lookupCode: 'HIAM.PERMISSION_LEVEL',
      label: intl.get('hiam.apiManagement.model.apiManagement.fdLevel').d('权限层级'),
    },
    {
      name: 'method',
      type: 'string',
      lookupCode: 'HIAM.REQUEST_METHOD',
      lovPara: { tag: 'show' },
      label: intl.get('hiam.apiManagement.model.apiManagement.method').d('请求方式'),
    },
    {
      name: 'labels',
      type: 'string',
      label: intl.get('hiam.apiManagement.model.apiManagement.tag').d('标签'),
      valueField: 'name',
      textField: 'name',
      transformRequest: (value) => {
        return value.join(',');
      },
      multiple: true,
      options: optionDs,
    },
    {
      name: 'publicAccess',
      type: 'number',
      lookupCode: 'HPFM.FLAG',
      defaultValue: undefined,
      label: intl.get('hiam.apiManagement.model.apiManagement.publicAccess').d('是否公开接口'),
    },
    {
      name: 'loginAccess',
      type: 'number',
      lookupCode: 'HPFM.FLAG',
      defaultValue: undefined,
      label: intl.get('hiam.apiManagement.model.apiManagement.loginAccess').d('是否登录可访问'),
    },
    {
      name: 'within',
      type: 'number',
      lookupCode: 'HPFM.FLAG',
      defaultValue: undefined,
      label: intl.get('hiam.apiManagement.model.apiManagement.within').d('是否内部接口'),
    },
    {
      name: 'signAccess',
      type: 'number',
      lookupCode: 'HPFM.FLAG',
      defaultValue: undefined,
      label: intl.get('hiam.apiManagement.model.apiManagement.signAccess').d('是否签名接口'),
    },
  ],
});

function tableDS(formRecord) {
  return {
    dataKey: 'content',
    cacheSelection: true,
    fields: [
      {
        name: 'id',
        type: 'string',
        label: intl.get('hiam.apiManagement.model.apiManagement.id').d('权限id'),
        unique: true,
      },
      {
        name: 'code',
        type: 'string',
        label: intl.get('hiam.apiManagement.model.apiManagement.code').d('权限编码'),
      },
      {
        name: 'path',
        type: 'string',
        label: intl.get('hiam.apiManagement.model.apiManagement.path').d('路径'),
      },
      {
        name: 'method',
        type: 'string',
        label: intl.get('hiam.apiManagement.model.apiManagement.method').d('请求方式'),
      },
      {
        name: 'methodMeaning',
        type: 'string',
        label: intl.get('hiam.apiManagement.model.apiManagement.methodMeaning').d('请求方式'),
      },
      {
        name: 'fdLevel',
        type: 'string',
        label: intl.get('hiam.apiManagement.model.apiManagement.fieldLevel').d('权限层级'),
      },
      {
        name: 'levelMeaning',
        type: 'string',
        label: intl.get('hiam.apiManagement.model.apiManagement.levelMeaning').d('权限层级'),
      },
      {
        name: 'description',
        type: 'intl',
        label: intl.get('hiam.apiManagement.model.apiManagement.description').d('描述'),
        maxLength: 1024,
      },
      {
        name: 'action',
        type: 'string',
        label: intl.get('hiam.apiManagement.model.apiManagement.action').d('方法名'),
      },
      {
        name: 'resource',
        type: 'string',
        label: intl.get('hiam.apiManagement.model.apiManagement.resource').d('资源类型'),
      },
      {
        name: 'serviceName',
        type: 'string',
        label: intl.get('hiam.apiManagement.model.apiManagement.serviceName').d('服务名称'),
      },
      {
        name: 'publicAccess',
        type: 'number',
        label: intl.get('hiam.apiManagement.model.apiManagement.publicAccess').d('是否公开接口'),
      },
      {
        name: 'loginAccess',
        type: 'number',
        label: intl.get('hiam.apiManagement.model.apiManagement.loginAccess').d('是否登录可访问'),
      },
      {
        name: 'within',
        type: 'number',
        label: intl.get('hiam.apiManagement.model.apiManagement.within').d('是否内部接口'),
      },
      {
        name: 'signAccess',
        type: 'number',
        label: intl.get('hiam.apiManagement.model.apiManagement.signAccess').d('是否签名接口'),
      },
      {
        name: 'objectVersionNumber',
        type: 'number',
        label: intl.get('hiam.apiManagement.model.apiManagement.objectVersionNumber').d('版本'),
      },
      {
        name: 'tag',
        type: 'string',
        label: intl.get('hiam.apiManagement.model.apiManagement.tag').d('标签'),
      },
      {
        name: 'pageTag',
        type: 'string',
        label: intl.get('hiam.apiManagement.model.apiManagement.pageTag').d('API标签'),
        bind: 'apiTags.PAGE',
      },
      // {
      //   name: 'backgroundTag',
      //   type: 'string',
      //   label: intl.get('hiam.apiManagement.model.apiManagement.backgroundTag').d('后端API标识'),
      //   bind: 'apiTags.BACKEND',
      // },
    ],
    events: {
      query: ({ dataSet }) => {
        dataSet.unSelectAll();
        dataSet.clearCachedSelected();
      },
      update: ({ record, dataSet, value, oldValue }) => {
        // if (!isEmpty(dataSet.frontLabels)) {
        if (isArray(value) || isArray(oldValue)) {
          if ((value || []).sort().toString() !== (oldValue || []).sort().toString()) {
            if (isEmpty(value)) {
              const PAGE = uniq(
                oldValue.filter((item) => {
                  return (dataSet.frontLabels || []).includes(item);
                })
              );
              record.set('apiTags', { PAGE, BACKEND: [] });
            } else if ((dataSet.frontLabels || []).includes(difference(oldValue, value)[0])) {
              record.set('apiTags', { PAGE: oldValue, BACKEND: [] });
            } else {
              record.set('apiTags', { PAGE: value.sort(), BACKEND: [] });
            }
          }
        } else if (isArray(value) && !isArray(oldValue)) {
          record.set('apiTags', { PAGE: value.sort(), BACKEND: [] });
        }
        // }
      },
    },
    transport: {
      read: (config) => {
        const { params } = config;
        const url = isTenantRoleLevel()
          ? `${HZERO_IAM}/hzero/v1/${organizationId}/permissions`
          : `${HZERO_IAM}/hzero/v1/permissions`;
        let data = {};
        if (formRecord.toData()[0]) {
          const {
            code = '',
            path = '',
            serviceName = '',
            fdLevel = '',
            method = '',
            labels = '',
            publicAccess = undefined,
            loginAccess = undefined,
            within = undefined,
            signAccess = undefined,
          } = formRecord.toData()[0];
          const obj = {
            publicAccess:
              isUndefined(publicAccess) || publicAccess === null ? undefined : !!publicAccess,
            loginAccess:
              isUndefined(loginAccess) || loginAccess === null ? undefined : !!loginAccess,
            within: isUndefined(within) || within === null ? undefined : !!within,
            signAccess: isUndefined(signAccess) || signAccess === null ? undefined : !!signAccess,
          };
          data = filterNullValueObject({
            code,
            path,
            serviceName,
            fdLevel,
            method,
            labels,
            ...obj,
          });
        }
        return {
          data,
          params,
          url,
          method: 'GET',
        };
      },
      update: (config) => {
        const url = isTenantRoleLevel()
          ? `${HZERO_IAM}/hzero/v1/${organizationId}/permissions/update`
          : `${HZERO_IAM}/hzero/v1/permissions/update`;
        const { data = [], dataSet } = config;
        const { _status, apiTags, pageTag, ...other } = data[0];
        const tags = (dataSet.labelList || [])
          .filter((item) => {
            return (data[0].apiTags.PAGE || []).includes(item.name);
          })
          .map((item) => ({ id: item.id, _token: item._token }));
        return {
          data: {
            ...other,
            labels: tags,
          },
          url,
          method: 'PUT',
        };
      },
      destroy: (config) => {
        const url = isTenantRoleLevel()
          ? `${HZERO_IAM}/hzero/v1/${organizationId}/permissions`
          : `${HZERO_IAM}/hzero/v1/permissions`;
        const { params = {}, data = [] } = config;
        return {
          data,
          params,
          url,
          method: 'DELETE',
        };
      },
    },
  };
}

const tenantDS = () => ({
  autoQuery: true,
  dataKey: 'content',
  cacheSelection: true,
  queryFields: [
    {
      name: 'tenantNum',
      type: 'string',
      label: intl.get('hiam.apiManagement.model.apiManagement.tenantNum').d('租户编码'),
      unique: true,
    },
    {
      name: 'tenantName',
      type: 'string',
      label: intl.get('hiam.apiManagement.model.apiManagement.tenantName').d('租户名称'),
      unique: true,
    },
  ],
  fields: [
    {
      name: 'tenantId',
      type: 'number',
      label: intl.get('hiam.apiManagement.model.apiManagement.tenantId').d('租户id'),
      unique: true,
    },
    {
      name: 'tenantNum',
      type: 'string',
      label: intl.get('hiam.apiManagement.model.apiManagement.tenantNum').d('租户编码'),
      unique: true,
    },
    {
      name: 'tenantName',
      type: 'string',
      label: intl.get('hiam.apiManagement.model.apiManagement.tenantName').d('租户名称'),
      unique: true,
    },
  ],
  events: {
    query: ({ dataSet }) => {
      dataSet.unSelectAll();
      dataSet.clearCachedSelected();
    },
  },
  transport: {
    read: (config) => {
      const url = isTenantRoleLevel()
        ? `${HZERO_IAM}/v1/${organizationId}/tenants`
        : `${HZERO_IAM}/v1/tenants`;
      return {
        ...config,
        url,
        method: 'GET',
      };
    },
  },
});

// 租户API管理查询列
const tenantFormDS = () => ({
  fields: [
    {
      name: 'tenantIdLov',
      type: 'object',
      lovCode: 'HPFM.TENANT',
      label: intl.get('hiam.apiManagement.model.apiManagement.tenant').d('租户'),
      noCache: true,
      ignore: 'always',
    },
    {
      name: 'tenantName',
      type: 'string',
      bind: 'tenantIdLov.tenantName',
    },
    {
      name: 'tenantId',
      type: 'string',
      bind: 'tenantIdLov.tenantId',
    },
    {
      name: 'code',
      type: 'string',
      label: intl.get('hiam.apiManagement.model.apiManagement.code').d('权限编码'),
    },
    {
      name: 'path',
      type: 'string',
      label: intl.get('hiam.apiManagement.model.apiManagement.path').d('路径'),
    },
    {
      name: 'serviceNameLov',
      type: 'object',
      lovCode: 'HADM.SERVICE_CODE',
      label: intl.get('hiam.apiManagement.model.apiManagement.serviceName').d('服务名称'),
      ignore: 'always',
      noCache: true,
    },
    {
      name: 'serviceName',
      type: 'string',
      bind: 'serviceNameLov.serviceName',
    },
    {
      name: 'method',
      type: 'string',
      lookupCode: 'HIAM.REQUEST_METHOD',
      lovPara: { tag: 'show' },
      label: intl.get('hiam.apiManagement.model.apiManagement.method').d('请求方式'),
    },
    {
      name: 'publicAccess',
      type: 'number',
      lookupCode: 'HPFM.FLAG',
      defaultValue: undefined,
      label: intl.get('hiam.apiManagement.model.apiManagement.publicAccess').d('是否公开接口'),
    },
    {
      name: 'loginAccess',
      type: 'number',
      lookupCode: 'HPFM.FLAG',
      defaultValue: undefined,
      label: intl.get('hiam.apiManagement.model.apiManagement.loginAccess').d('是否登录可访问'),
    },
    {
      name: 'within',
      type: 'number',
      lookupCode: 'HPFM.FLAG',
      defaultValue: undefined,
      label: intl.get('hiam.apiManagement.model.apiManagement.within').d('是否内部接口'),
    },
    {
      name: 'signAccess',
      type: 'number',
      lookupCode: 'HPFM.FLAG',
      defaultValue: undefined,
      label: intl.get('hiam.apiManagement.model.apiManagement.signAccess').d('是否签名接口'),
    },
    {
      name: 'labels',
      type: 'string',
      label: intl.get('hiam.apiManagement.model.apiManagement.tag').d('标签'),
      valueField: 'name',
      textField: 'name',
      transformRequest: (value) => {
        return value.join(',');
      },
      multiple: true,
      options: optionDs,
    },
  ],
});

// 租户API管理表格DS
function tenantTableDS(record) {
  return {
    dataKey: 'content',
    cacheSelection: true,
    fields: [
      {
        name: 'id',
        type: 'string',
        label: intl.get('hiam.apiManagement.model.apiManagement.id').d('权限id'),
        unique: true,
      },
      {
        name: 'code',
        type: 'string',
        label: intl.get('hiam.apiManagement.model.apiManagement.code').d('权限编码'),
      },
      {
        name: 'path',
        type: 'string',
        label: intl.get('hiam.apiManagement.model.apiManagement.path').d('路径'),
      },
      {
        name: 'method',
        type: 'string',
        label: intl.get('hiam.apiManagement.model.apiManagement.method').d('请求方式'),
      },
      {
        name: 'methodMeaning',
        type: 'string',
        label: intl.get('hiam.apiManagement.model.apiManagement.methodMeaning').d('请求方式'),
      },
      {
        name: 'fdLevel',
        type: 'string',
        label: intl.get('hiam.apiManagement.model.apiManagement.fieldLevel').d('权限层级'),
      },
      {
        name: 'levelMeaning',
        type: 'string',
        label: intl.get('hiam.apiManagement.model.apiManagement.levelMeaning').d('权限层级'),
      },
      {
        name: 'description',
        type: 'intl',
        label: intl.get('hiam.apiManagement.model.apiManagement.description').d('描述'),
      },
      {
        name: 'action',
        type: 'string',
        label: intl.get('hiam.apiManagement.model.apiManagement.action').d('方法名'),
      },
      {
        name: 'resource',
        type: 'string',
        label: intl.get('hiam.apiManagement.model.apiManagement.resource').d('资源类型'),
      },
      {
        name: 'serviceName',
        type: 'string',
        label: intl.get('hiam.apiManagement.model.apiManagement.serviceName').d('服务名称'),
      },
      {
        name: 'publicAccess',
        type: 'number',
        label: intl.get('hiam.apiManagement.model.apiManagement.publicAccess').d('是否公开接口'),
      },
      {
        name: 'loginAccess',
        type: 'number',
        label: intl.get('hiam.apiManagement.model.apiManagement.loginAccess').d('是否登录可访问'),
      },
      {
        name: 'within',
        type: 'number',
        label: intl.get('hiam.apiManagement.model.apiManagement.within').d('是否内部接口'),
      },
      {
        name: 'signAccess',
        type: 'boolean',
        label: intl.get('hiam.apiManagement.model.apiManagement.signAccess').d('是否签名接口'),
      },
      {
        name: 'objectVersionNumber',
        type: 'number',
        label: intl.get('hiam.apiManagement.model.apiManagement.objectVersionNumber').d('版本'),
      },
      {
        name: 'tenantName',
        type: 'string',
        label: intl.get('hiam.apiManagement.model.apiManagement.tenantName').d('租户名称'),
      },
    ],
    events: {
      query: ({ dataSet }) => {
        dataSet.unSelectAll();
        dataSet.clearCachedSelected();
      },
    },
    transport: {
      read: (config) => {
        const url = isTenantRoleLevel()
          ? `${HZERO_IAM}/v1/${organizationId}/permissions/tenant`
          : `${HZERO_IAM}/hzero/v1/permissions/tenant`;
        const { params } = config;
        let data = {};
        if (record.toData()[0]) {
          const {
            tenantId = '',
            code = '',
            path = '',
            serviceName = '',
            method = '',
            publicAccess = undefined,
            loginAccess = undefined,
            within = undefined,
            signAccess = undefined,
            labels = '',
          } = record.toData()[0];
          const obj = {
            publicAccess:
              isUndefined(publicAccess) || publicAccess === null ? undefined : !!publicAccess,
            loginAccess:
              isUndefined(loginAccess) || loginAccess === null ? undefined : !!loginAccess,
            within: isUndefined(within) || within === null ? undefined : !!within,
            signAccess: isUndefined(signAccess) || signAccess === null ? undefined : !!signAccess,
          };
          data = filterNullValueObject({
            tenantId,
            code,
            path,
            serviceName,
            method,
            labels,
            ...obj,
          });
        }
        return {
          data,
          params,
          url,
          method: 'GET',
        };
      },
      destroy: (config) => {
        const url = isTenantRoleLevel()
          ? `${HZERO_IAM}/v1/${organizationId}/permissions/revoke`
          : `${HZERO_IAM}/hzero/v1/permissions/revoke`;
        const { params = {}, data = [] } = config;
        return {
          data,
          params,
          url,
          method: 'DELETE',
        };
      },
    },
  };
}

// 刷新api表单DS
const refreshDS = () => ({
  autoQueryAfterSubmit: false,
  fields: [
    {
      name: 'serviceName',
      label: intl.get('hiam.apiManagement.model.apiManagement.refreshServiceName').d('服务名'),
      required: true,
      type: 'string',
    },
    {
      name: 'metaVersion',
      label: intl.get('hiam.apiManagement.model.apiManagement.metaVersion').d('服务标记版本'),
      required: true,
      type: 'string',
    },
    {
      name: 'cleanPermission',
      label: intl
        .get('hiam.apiManagement.model.apiManagement.cleanPermission')
        .d('是否清除过期权限'),
      type: 'boolean',
      defaultValue: false,
    },
  ],
  transport: {
    create: ({ data }) => {
      const { __id, _status, ...other } = Array.isArray(data) ? data[0] : {};
      const url = `${HZERO_IAM}/v1/tool/permission/fresh`;
      return {
        url,
        method: 'POST',
        params: other,
        data: {},
      };
    },
  },
});

const drawerDS = () => ({
  autoQuery: false,
  autoQueryAfterSubmit: false,
  fields: [
    {
      name: 'description',
      type: 'intl',
      label: intl.get('hiam.apiManagement.model.apiManagement.description').d('描述'),
      maxLength: 1024,
    },
    {
      name: 'tag',
      type: 'string',
      label: intl.get('hiam.apiManagement.model.apiManagement.tag').d('标签'),
    },
  ],
  transport: {
    read: () => {
      const url = isTenantRoleLevel()
        ? `${HZERO_IAM}/v1/${organizationId}/labels/by-type`
        : `${HZERO_IAM}/v1/labels/by-type`;
      return {
        url,
        method: 'GET',
        params: { type: 'API' },
      };
    },
  },
});

export { formDS, tableDS, tenantDS, refreshDS, tenantFormDS, tenantTableDS, drawerDS };
