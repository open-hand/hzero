/**
 * @since 2019-12-03
 * @author WT <tao13.wang@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import intl from 'utils/intl';
import { HZERO_IAM, VERSION_IS_OP } from 'utils/config';
import { getCurrentOrganizationId } from 'utils/utils';

const organizationId = getCurrentOrganizationId();

const checkStatusFormConfigDS = () => ({
  autoCreate: true,
  fields: [
    {
      name: 'checkState',
      type: 'string',
      lookupCode: 'HIAM.PERMISSION_CHECK.STATE',
      defaultValue: 'PERMISSION_MISMATCH',
    },
  ],
});

const searchFormConfigDS = () => ({
  fields: [
    {
      name: 'permissionType',
      type: 'string',
      lookupCode: 'HIAM.PERMISSION_CHECK.PERMISSION_TYPE',
      label: intl.get('hiam.missPermission.model.missPermission.permissionType').d('权限类型'),
    },
    {
      name: 'menuName',
      type: 'string',
      label: intl.get('hiam.missPermission.model.missPermission.menuName').d('菜单名称'),
    },

    {
      name: 'fdLevel',
      type: 'string',
      lookupCode: 'HIAM.PERMISSION_LEVEL',
      label: intl.get('hiam.missPermission.model.missPermission.levelMeaning').d('权限层级'),
    },
    {
      name: 'permissionCode',
      type: 'string',
      label: intl.get('hiam.missPermission.model.missPermission.permissionCode').d('权限编码'),
    },
    {
      name: 'serviceNameLov',
      type: 'object',
      lovCode: 'HADM.SERVICE_CODE',
      label: intl.get('hiam.missPermission.model.missPermission.serviceName').d('服务名称'),
      noCache: true,
      ignore: 'always',
    },
    {
      name: 'serviceName',
      type: 'string',
      bind: 'serviceNameLov.serviceCode',
    },
    {
      name: 'handleStatus',
      type: 'string',
      lookupCode: 'HIAM.PERMISSION_CHECK.HANDLE_STATUS',
      label: intl.get('hiam.missPermission.model.missPermission.handleStatus').d('处理状态'),
    },
    {
      name: 'apiPath',
      type: 'string',
      label: intl.get('hiam.missPermission.model.missPermission.apiPath').d('API路径'),
    },
    {
      name: 'apiMethod',
      type: 'string',
      lovPara: { tag: 'show' },
      lookupCode: 'HIAM.REQUEST_METHOD',
      label: intl.get('hiam.missPermission.model.missPermission.apiMethod').d('请求方式'),
    },
  ],
});

function tableConfigDS(checkStatusRecord, searchRecord) {
  return {
    autoQuery: true,
    dataKey: 'content',
    selection: 'multiple',
    fields: [
      {
        name: 'permissionCode',
        type: 'string',
        label: intl.get('hiam.missPermission.model.missPermission.permissionCode').d('权限编码'),
      },
      {
        name: 'menuName',
        type: 'string',
        label: intl.get('hiam.missPermission.model.missPermission.menuName').d('菜单名称'),
      },
      {
        name: 'apiPath',
        type: 'string',
        label: intl.get('hiam.missPermission.model.missPermission.apiPath').d('API路径'),
      },
      {
        name: 'checkState',
        type: 'string',
        label: intl.get('hiam.missPermission.model.missPermission.checkState').d('检查状态'),
      },
      {
        name: 'handleStatusMeaning',
        type: 'string',
        label: intl
          .get('hiam.missPermission.model.missPermission.handleStatusMeaning')
          .d('处理状态'),
      },
      {
        name: 'checkStateMeaning',
        type: 'string',
        label: intl
          .get('hiam.missPermission.model.missPermission.checkStateMeaning')
          .d('检查状态描述'),
      },
      {
        name: 'serviceName',
        type: 'string',
        label: intl.get('hiam.missPermission.model.missPermission.serviceName').d('服务名称'),
      },
      {
        name: 'apiMethodMeaning',
        type: 'string',
        label: intl.get('hiam.missPermission.model.missPermission.apiMethodMeaning').d('请求方式'),
      },
      {
        name: 'permissionType',
        type: 'string',
        label: intl.get('hiam.missPermission.model.missPermission.permissionType').d('权限类型'),
      },
      {
        name: 'creationDate',
        type: 'string',
        label: intl.get('hiam.missPermission.model.missPermission.creationDate').d('日期'),
      },
      {
        name: 'levelMeaning',
        type: 'string',
        label: intl.get('hiam.missPermission.model.missPermission.levelMeaning').d('权限层级'),
      },
    ],
    transport: {
      read: (config) => {
        const { checkState = null } = checkStatusRecord.toJSONData()[0] || {};
        const {
          permissionCode = null,
          serviceName = null,
          handleStatus = null,
          apiPath = null,
          apiMethod = null,
          permissionType = null,
          fdLevel = null,
          menuName = null,
        } = searchRecord.toJSONData()[0] || {};
        const { params } = config;
        let { data } = config;
        data = {
          checkState,
          permissionCode,
          serviceName,
          handleStatus,
          apiPath,
          apiMethod,
          permissionType,
          fdLevel,
          menuName,
        };
        const url = VERSION_IS_OP
          ? `${HZERO_IAM}/v1/${organizationId}/permission-check`
          : `${HZERO_IAM}/v1/permission-check`;
        return {
          data,
          params,
          url,
          method: 'GET',
        };
      },
    },
  };
}

const permissionSetDS = () => ({
  dataKey: 'content',
  cacheSelection: true,
  queryFields: [
    {
      name: 'code',
      type: 'string',
      label: intl.get('hiam.missPermission.model.missPermission.code').d('权限集编码'),
    },
    {
      name: 'parentName',
      type: 'string',
      label: intl.get('hiam.missPermission.model.missPermission.parentName').d('父级菜单'),
    },
  ],
  fields: [
    {
      name: 'code',
      type: 'string',
      label: intl.get('hiam.missPermission.model.missPermission.code').d('权限集编码'),
    },
    {
      name: 'name',
      type: 'string',
      label: intl.get('hiam.missPermission.model.missPermission.name').d('权限集名称'),
    },

    {
      name: 'levelMeaning',
      type: 'string',
      label: intl.get('hiam.missPermission.model.missPermission.levelMeaning').d('权限层级'),
    },
    {
      name: 'parentName',
      type: 'string',
      label: intl.get('hiam.missPermission.model.missPermission.parentName').d('父级菜单'),
    },
  ],
  transport: {
    read: (config) => {
      const { params, data } = config;
      const url = VERSION_IS_OP
        ? `${HZERO_IAM}/v1/${organizationId}/permission-check/permission-set`
        : `${HZERO_IAM}/v1/permission-check/permission-set`;
      return {
        data,
        params,
        url,
        method: 'GET',
      };
    },
  },
});

const clearPermissionRecordFormDS = () => ({
  fields: [
    {
      name: 'checkType',
      type: 'string',
      lookupCode: 'HIAM.PERMISSION_CHECK.STATE',
      label: intl.get('hiam.missPermission.model.missPermission.checkType').d('检查状态'),
    },
    {
      name: 'clearType',
      type: 'string',
      lookupCode: 'HIAM.PERMISSION_CHECK.CLEAR_TYPE',
      label: intl.get('hiam.missPermission.model.missPermission.clearType').d('范围'),
      required: true,
    },
  ],
});

const detailFormDS = () => ({
  dateKey: 'content',
  fields: [
    {
      name: 'permissionCode',
      type: 'string',
      label: intl.get('hiam.missPermission.model.missPermission.permissionCode').d('权限编码'),
    },
    {
      name: 'serviceName',
      type: 'string',
      label: intl.get('hiam.missPermission.model.missPermission.serviceName').d('服务名称'),
    },
    {
      name: 'apiPath',
      type: 'string',
      label: intl.get('hiam.missPermission.model.missPermission.apiPath').d('API路径'),
    },
    {
      name: 'checkState',
      type: 'string',
      label: intl.get('hiam.missPermission.model.missPermission.checkState').d('检查状态'),
    },
    {
      name: 'checkStateMeaning',
      type: 'string',
      label: intl
        .get('hiam.missPermission.model.missPermission.checkStateMeaning')
        .d('检查状态描述'),
    },
    {
      name: 'apiMethodMeaning',
      type: 'string',
      label: intl.get('hiam.missPermission.model.missPermission.apiMethodMeaning').d('请求方式'),
    },
    {
      name: 'permissionType',
      type: 'string',
      label: intl.get('hiam.missPermission.model.missPermission.permissionType').d('权限类型'),
    },
    {
      name: 'creationDate',
      type: 'string',
      label: intl.get('hiam.missPermission.model.missPermission.creationDate').d('日期'),
    },
    {
      name: 'permissionDetails',
      type: 'string',
      label: intl
        .get('hiam.missPermission.model.missPermission.permissionDetails')
        .d('权限详细信息'),
    },
    {
      name: 'routeDetails',
      type: 'string',
      label: intl.get('hiam.missPermission.model.missPermission.routeDetails').d('路由详细信息'),
    },
    {
      name: 'userDetails',
      type: 'string',
      label: intl.get('hiam.missPermission.model.missPermission.userDetails').d('用户详细信息'),
    },
  ],
  transport: {
    read: (config) => {
      const { params, data } = config;
      const { permissionCheckId } = data;
      const url = VERSION_IS_OP
        ? `${HZERO_IAM}/v1/${organizationId}/permission-check/${permissionCheckId}`
        : `${HZERO_IAM}/v1/permission-check/${permissionCheckId}`;
      return {
        data,
        params,
        url,
        method: 'GET',
      };
    },
  },
});

export {
  checkStatusFormConfigDS,
  searchFormConfigDS,
  tableConfigDS,
  permissionSetDS,
  clearPermissionRecordFormDS,
  detailFormDS,
};
