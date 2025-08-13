/**
 * 安全组维护 - 相关DS
 * @date: 2019-11-1
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import intl from 'utils/intl';
import { isEmpty } from 'lodash';
import { CODE_UPPER } from 'utils/regExp';
import { HZERO_IAM, VERSION_IS_OP } from 'utils/config';

import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import { validateDuplicate } from '@/services/securityGroupService';

const CARD_MAX_HEIGHT = 200;
const organizationId = getCurrentOrganizationId();
const levelUrl = isTenantRoleLevel() ? `/${organizationId}` : '';

// 安全组列表
const secGrpDS = () => ({
  transport: {
    read: ({ data, params }) => ({
      url: `${HZERO_IAM}/v1${levelUrl}/sec-grps`,
      params: { ...data, ...params },
      method: 'get',
    }),
    submit: ({ data, dataSet }) => {
      const { queryParameter = {} } = dataSet;
      const { roleId } = queryParameter;
      return {
        url: `${HZERO_IAM}/v1${levelUrl}/sec-grps/create`,
        data: {
          ...data[0],
          roleId,
        },
      };
    },
  },
  queryParameter: { secGrpSource: 'self' },
  autoQuery: true,
  pageSize: 10,
  selection: false,
  primaryKey: 'secGrpId',
  fields: [
    {
      name: 'secGrpId',
      type: 'string',
    },
    {
      name: 'secGrpCode',
      label: intl.get('hiam.securityGroup.model.securityGroup.code').d('安全组代码'),
      type: 'string',
      required: true,
      unique: true,
      maxLength: 60,
      validator: async (value, name) => {
        if (!CODE_UPPER.test(value)) {
          return intl
            .get('hzero.common.validation.codeUpper')
            .d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”');
        }
        const data = {
          [name]: value,
          level: isTenantRoleLevel() ? 'organization' : 'site',
          tenantId: organizationId,
        };
        const res = await validateDuplicate(data);
        if (!isEmpty(res) && res.failed && res.message) {
          return intl.get('hiam.securityGroup.view.message.code.unique').d('安全组代码重复');
        }
        if (!isEmpty(res) && res.success) {
          return true;
        }
      },
    },
    {
      name: 'secGrpName',
      label: intl.get('hiam.securityGroup.model.securityGroup.name').d('安全组名称'),
      type: 'intl',
      required: true,
      maxLength: 60,
    },
    {
      name: 'secGrpLevelMeaning',
      label: intl.get('hiam.securityGroup.model.securityGroup.level').d('层级'),
      type: 'string',
    },
    {
      name: 'remark',
      label: intl.get('hiam.securityGroup.model.securityGroup.description').d('安全组描述'),
      type: 'intl',
      maxLength: 240,
    },
    {
      name: 'enabledFlag',
      label: intl.get('hiam.securityGroup.model.securityGroup.enabledFlag').d('是否启用'),
      type: 'number',
      falseValue: 0,
      trueValue: 1,
      defaultValue: 1,
    },
    {
      name: 'createRoleName',
      label: intl.get('hiam.securityGroup.model.securityGroup.secGrpSource').d('来源'),
      type: 'string',
    },
    {
      name: 'tenantId',
      type: 'number',
    },
  ],
  queryFields: [
    {
      name: 'secGrpCode',
      label: intl.get('hiam.securityGroup.model.securityGroup.code').d('安全组代码'),
      type: 'string',
    },
    {
      name: 'secGrpName',
      label: intl.get('hiam.securityGroup.model.securityGroup.name').d('安全组名称'),
      type: 'string',
    },
  ],
});

// 安全组分配角色
const assignRoleDS = (secGrpId) => ({
  transport: {
    read: ({ data, params }) => ({
      url: `${HZERO_IAM}/v1${levelUrl}/sec-grp-role-assign/${secGrpId}/assigned-role`,
      params: { ...data, ...params },
      method: 'get',
    }),
    destroy: ({ data }) => {
      const roleIds = data.map((item) => item.id);
      return {
        url: `${HZERO_IAM}/v1${levelUrl}/sec-grp-role-assign/${secGrpId}/recycle-role`,
        data: roleIds,
        method: 'delete',
      };
    },
  },
  autoQuery: true,
  pageSize: 10,
  fields: [
    {
      name: 'name',
      label: intl.get('hiam.roleManagement.model.roleManagement.name').d('角色名称'),
      type: 'string',
    },
    {
      name: 'parentRoleName',
      label: intl.get('hiam.roleManagement.model.roleManagement.topRole').d('上级角色'),
      type: 'string',
    },
    {
      name: 'tenantName',
      label: intl.get('hiam.roleManagement.model.roleManagement.tenant').d('所属租户'),
      type: 'string',
    },
  ],
  queryFields: [
    {
      name: 'name',
      label: intl.get('hiam.roleManagement.model.roleManagement.name').d('角色名称'),
      type: 'string',
    },
  ],
});

// 详情页表单
const secGrpDetailDS = (id) => ({
  autoQuery: true,
  paging: false,
  selection: false,
  transport: {
    read: ({ data, params }) => ({
      url: `${HZERO_IAM}/v1${levelUrl}/sec-grps/${id}`,
      params: { ...data, ...params },
      method: 'get',
    }),
    submit: ({ data }) => ({
      url: `${HZERO_IAM}/v1${levelUrl}/sec-grps`,
      data: data[0],
      method: 'PUT',
    }),
  },
  fields: [
    {
      name: 'secGrpCode',
      label: intl.get('hiam.securityGroup.model.securityGroup.code').d('安全组代码'),
      type: 'string',
    },
    {
      name: 'secGrpName',
      label: intl.get('hiam.securityGroup.model.securityGroup.name').d('安全组名称'),
      type: 'intl',
      required: true,
    },
    {
      name: 'secGrpLevelMeaning',
      label: intl.get('hiam.securityGroup.model.securityGroup.level').d('层级'),
      type: 'string',
    },
    {
      name: 'remark',
      label: intl.get('hiam.securityGroup.model.securityGroup.description').d('安全组描述'),
      type: 'intl',
    },
    {
      name: 'createRoleName',
      label: intl.get('hiam.securityGroup.model.securityGroup.secGrpSource').d('来源'),
      type: 'string',
    },
    {
      name: 'enabledFlag',
      label: intl.get('hiam.securityGroup.model.securityGroup.enabledFlag').d('是否启用'),
      type: 'number',
      falseValue: 0,
      trueValue: 1,
      defaultValue: 1,
    },
    {
      name: 'tenantId',
      type: 'number',
    },
  ],
  queryFields: [
    {
      name: 'secGrpCode',
      label: intl.get('hiam.securityGroup.model.securityGroup.code').d('安全组代码'),
      type: 'string',
    },
    {
      name: 'secGrpName',
      label: intl.get('hiam.securityGroup.model.securityGroup.name').d('安全组名称'),
      type: 'string',
    },
  ],
});

// 访问权限
const visitPermissionDS = (secGrpId) => ({
  pageSize: 10,
  selection: false,
  checkField: 'checkedFlag',
  autoQuery: true,
  primaryKey: 'id',
  idField: 'id',
  parentField: 'parentId',
  expandField: 'expand',
  transport: {
    read: ({ data, params }) => ({
      url: `${HZERO_IAM}/v1${levelUrl}/sec-grp-acls/${secGrpId}`,
      params: {
        ...data,
        ...params,
        secGrpId,
        level: isTenantRoleLevel() ? 'organization' : 'site',
      },
      method: 'get',
    }),
    submit: ({ data }) => {
      let addPermissionIds = [];
      let deletePermissionIds = [];
      if (data && data.length) {
        const ids = data.map((item) => item.id);
        addPermissionIds = ids.filter((item) => item.checkedFlag === 'Y' && item.type === 'ps');
        deletePermissionIds = ids.filter((item) => item.checkedFlag === 'N' && item.type === 'ps');
      }
      return {
        url: `${HZERO_IAM}/v1${levelUrl}/sec-grp-acls/${secGrpId}`,
        data: {
          addPermissionIds,
          deletePermissionIds,
        },
      };
    },
  },
  fields: [
    { name: 'id', type: 'number' },
    {
      name: 'name',
      label: intl.get(`hiam.roleManagement.model.roleManagement.permissionName`).d('权限名称'),
      type: 'string',
    },
    {
      name: 'permissionType',
      label: intl.get(`hiam.roleManagement.model.roleManagement.permission.Type`).d('权限类型'),
      type: 'string',
    },
    {
      name: 'checkedFlag',
      label: intl.get('hzero.common.button.action').d('操作'),
      type: 'boolean',
      trueValue: 'Y',
      falseValue: 'N',
    },
    { name: 'parentId', type: 'number', parentFieldName: 'id' },
    {
      name: 'expand',
      label: intl.get('hiam.securityGroup.model.securityGroup.expandFlag').d('是否展开'),
      type: 'boolean',
      defaultValue: false,
    },
  ],
});

// 字段权限列表
const fieldPermissionDS = () => ({
  pageSize: 10,
  selection: false,
  autoQuery: true,
  primaryKey: 'id',
  fields: [
    {
      name: 'serviceName',
      label: intl.get('hiam.roleManagement.model.api.serviceName').d('服务名'),
      type: 'string',
    },
    {
      name: 'fieldCount',
      label: intl.get('hiam.roleManagement.model.api.fieldCount').d('已配置'),
      type: 'number',
    },
    {
      name: 'method',
      label: intl.get('hiam.roleManagement.model.api.method').d('请求方式'),
      type: 'string',
    },
    {
      name: 'path',
      label: intl.get('hiam.roleManagement.model.api.path').d('请求路径'),
      type: 'string',
    },
    {
      name: 'description',
      label: intl.get('hiam.roleManagement.model.api.description').d('请求描述'),
      type: 'string',
    },
  ],
  queryFields: [
    {
      name: 'serviceNameLov',
      label: intl.get('hiam.roleManagement.model.api.serviceName').d('服务名'),
      type: 'object',
      lovCode:
        VERSION_IS_OP || isTenantRoleLevel()
          ? 'HADM.ROUTE.SERVICE_CODE.ORG'
          : 'HADM.ROUTE.SERVICE_CODE',
      noCache: true,
      ignore: 'always',
    },
    {
      name: 'serviceName',
      type: 'string',
      bind: 'serviceNameLov.serviceCode',
    },
    {
      name: 'method',
      label: intl.get('hiam.roleManagement.model.api.method').d('请求方式'),
      type: 'string',
      lookupCode: 'HIAM.REQUEST_METHOD',
    },
    {
      name: 'path',
      label: intl.get('hiam.roleManagement.model.api.path').d('请求路径'),
      type: 'string',
    },
    {
      name: 'description',
      label: intl.get('hiam.roleManagement.model.api.description').d('请求描述'),
      type: 'string',
    },
  ],
});

// 字段权限侧滑
const fieldPermissionDrawerDS = () => ({
  pageSize: 10,
  selection: false,
  autoQuery: true,
  fields: [
    {
      name: 'fieldLov',
      label: intl.get('hiam.roleManagement.model.fieldPermission.fieldName').d('字段名称'),
      type: 'object',
      lovCode: 'HIAM.SEC_GRP.FIELD.ASSIGNABLE',
      required: true,
      ignore: 'always',
      unique: true,
    },
    {
      name: 'fieldId',
      type: 'string',
      bind: 'fieldLov.fieldId',
    },
    {
      name: 'fieldName',
      label: intl.get('hiam.roleManagement.model.fieldPermission.fieldName').d('字段名称'),
      type: 'string',
      bind: 'fieldLov.fieldName',
      ignore: 'always',
    },
    {
      name: 'fieldTypeMeaning',
      label: intl.get('hiam.roleManagement.model.fieldPermission.fieldType').d('字段类型'),
      type: 'string',
      lookupCode: 'HIAM.FIELD.TYPE',
      bind: 'fieldLov.fieldType',
    },
    {
      name: 'permissionType',
      label: intl.get('hiam.roleManagement.model.fieldPermission.rule').d('权限规则'),
      type: 'string',
      lookupCode: 'HIAM.FIELD.PERMISSION_TYPE',
      required: true,
    },
    {
      name: 'permissionTypeMeaning',
      ignore: 'always',
    },
    {
      name: 'permissionRule',
      label: intl.get('hiam.roleManagement.model.fieldPermission.desensitize').d('脱敏规则'),
      type: 'string',
      dynamicProps: {
        required: ({ record }) => record.get('permissionType') === 'DESENSITIZE',
      },
    },
    {
      name: 'remark',
      label: intl.get('hiam.securityGroup.model.securityGroup.remark').d('说明'),
      type: 'string',
    },
    {
      name: 'editing',
      type: 'boolean',
      ignore: 'always',
    },
  ],
  queryFields: [
    {
      name: 'fieldName',
      label: intl.get('hiam.roleManagement.model.fieldPermission.fieldName').d('字段名称'),
      type: 'string',
    },
    {
      name: 'permissionType',
      label: intl.get('hiam.roleManagement.model.fieldPermission.rule').d('权限规则'),
      type: 'string',
      lookupCode: 'HIAM.FIELD.PERMISSION_TYPE',
    },
  ],
});

// 工作台列表
const cardDS = () => ({
  pageSize: 10,
  autoQuery: true,
  fields: [
    {
      name: 'cardLov',
      label: intl.get('hiam.roleManagement.model.tenantAssignCards.cardCode').d('卡片代码'),
      type: 'object',
      lovCode: isTenantRoleLevel() ? 'HPFM.ROLE_ASSIGN_CARD.ORG' : 'HPFM.ROLE_ASSIGN_CARD',
      lovPara: isTenantRoleLevel() ? { tenantId: organizationId } : {},
      ignore: 'always',
      required: true,
    },
    {
      name: 'cardId',
      type: 'number',
      bind: 'cardLov.cardId',
    },
    {
      name: 'code',
      label: intl.get('hiam.roleManagement.model.tenantAssignCards.cardCode').d('卡片代码'),
      type: 'string',
      bind: 'cardLov.code',
    },
    {
      name: 'name',
      label: intl.get('hiam.roleManagement.model.tenantAssignCards.cardName').d('卡片名称'),
      type: 'string',
      bind: 'cardLov.name',
    },
    {
      name: 'catalogMeaning',
      label: intl.get('hiam.roleManagement.model.tenantAssignCards.cardType').d('卡片类别'),
      type: 'string',
      bind: 'cardLov.catalogMeaning',
    },
    {
      name: 'h',
      label: intl.get('hiam.roleManagement.model.tenantAssignCards.cardH').d('高度'),
      type: 'number',
      bind: 'cardLov.h',
    },
    {
      name: 'w',
      label: intl.get('hiam.roleManagement.model.tenantAssignCards.cardW').d('长度'),
      type: 'number',
      bind: 'cardLov.w',
    },
    {
      name: 'x',
      label: intl.get('hiam.roleManagement.model.tenantAssignCards.cardX').d('位置X'),
      type: 'number',
      required: true,
      step: 1,
      min: 0,
      max: CARD_MAX_HEIGHT,
    },
    {
      name: 'y',
      label: intl.get('hiam.roleManagement.model.tenantAssignCards.cardY').d('位置Y'),
      type: 'number',
      required: true,
      step: 1,
      min: 0,
      max: CARD_MAX_HEIGHT,
    },
    {
      name: 'defaultDisplayFlag',
      label: intl.get('hiam.securityGroup.model.securityGroup.defaultDisplayFlag').d('是否初始化'),
      type: 'number',
      falseValue: 0,
      trueValue: 1,
      defaultValue: 1,
    },
    {
      name: 'remark',
      label: intl.get('hzero.common.explain').d('说明'),
      type: 'string',
    },
  ],
  queryFields: [
    {
      name: 'code',
      label: intl.get('hiam.roleManagement.model.tenantAssignCards.cardCode').d('卡片代码'),
      type: 'string',
    },
    {
      name: 'name',
      label: intl.get('hiam.roleManagement.model.tenantAssignCards.cardName').d('卡片名称'),
      type: 'string',
    },
  ],
});

// 数据权限维度
const dimensionDS = () => ({
  pageSize: 10,
  selection: false,
  autoQuery: true,
  primaryKey: 'docTypeId',
  fields: [
    {
      name: 'docTypeName',
      label: intl.get('hiam.roleManagement.model.role.docType').d('单据'),
      type: 'string',
    },
    {
      name: 'authScopeCode',
      label: intl.get('hiam.roleManagement.model.role.authRang').d('权限维度范围'),
      type: 'string',
      defaultValue: 'BIZ',
      lookUpCode: 'HIAM.AUTHORITY_SCOPE_CODE',
    },
    {
      name: 'changingSourceList',
      type: 'object',
    },
  ],
  queryFields: [
    {
      name: 'docTypeName',
      label: intl.get('hiam.roleManagement.model.role.docType').d('单据'),
      type: 'string',
    },
  ],
});

// 分配角色添加角色弹窗
const addRoleDS = ({ secGrpId }) => ({
  transport: {
    read: ({ data, params }) => ({
      url: `${HZERO_IAM}/v1${levelUrl}/sec-grp-role-assign/${secGrpId}/assignable-role`,
      params: { ...data, ...params },
      method: 'get',
    }),
    submit: ({ data }) => {
      const roleIds = data.map((item) => item.id);
      return {
        url: `${HZERO_IAM}/v1${levelUrl}/sec-grp-role-assign/${secGrpId}/assign-role`,
        data: roleIds,
      };
    },
  },
  autoQuery: true,
  selection: false,
  pageSize: 10,
  fields: [
    {
      name: 'isSelected',
      type: 'boolean',
      ignore: 'always',
    },
    {
      name: 'name',
      label: intl.get('hiam.roleManagement.model.roleManagement.name').d('角色名称'),
      type: 'string',
    },
    {
      name: 'code',
      label: intl.get('hiam.roleManagement.model.roleManagement.code').d('角色编码'),
      type: 'string',
    },
    {
      name: 'tenantName',
      label: intl.get('hiam.roleManagement.model.roleManagement.tenantName').d('租户名称'),
      type: 'string',
    },
  ],
  queryFields: [
    {
      name: 'name',
      label: intl.get('hiam.roleManagement.model.roleManagement.name').d('角色名称'),
      type: 'string',
    },
    {
      name: 'code',
      label: intl.get('hiam.roleManagement.model.roleManagement.code').d('角色编码'),
      type: 'string',
    },
    !isTenantRoleLevel() && {
      name: 'tenantLov',
      label: intl.get('hiam.roleManagement.model.roleManagement.tenantName').d('租户名称'),
      type: 'object',
      lovCode: 'HPFM.TENANT',
      ignore: 'always',
      noCache: true,
    },
    !isTenantRoleLevel() && {
      name: 'tenantId',
      label: intl.get('hiam.roleManagement.model.roleManagement.tenantName').d('租户名称'),
      bind: 'tenantLov.tenantId',
    },
  ].filter(Boolean),
});

// 数据权限除公司外的添加弹窗
const addModalDS = ({
  nameTitle,
  codeTitle,
  authorityTypeCode,
  secGrpId,
  roleId,
  secGrpSource,
}) => ({
  transport: {
    read: ({ data, params }) => ({
      url: `${HZERO_IAM}/v1${levelUrl}/sec-grp-dcls/${secGrpId}/authority/assignable`,
      params: { ...data, ...params, roleId, authorityTypeCode, secGrpSource },
      method: 'get',
      transformResponse: (res) => {
        const newData = JSON.parse(res);
        return newData.secGrpDclLineList;
      },
    }),
    submit: ({ data }) => ({
      url: `${HZERO_IAM}/v1${levelUrl}/sec-grp-dcls/${secGrpId}/authority?authorityTypeCode=${authorityTypeCode}`,
      data,
    }),
  },
  autoQuery: true,
  selection: false,
  pageSize: 8,
  dataKey: 'content',
  fields: [
    {
      name: 'isSelected',
      type: 'boolean',
      ignore: 'always',
    },
    {
      name: 'tenantName',
      label: intl.get('hzero.common.model.common.tenantId').d('租户'),
      type: 'string',
    },
    {
      name: 'dataName',
      label: nameTitle,
      type: 'string',
    },
    {
      name: 'dataCode',
      label: codeTitle,
      type: 'string',
    },
    {
      name: 'secGrpId',
      type: 'string',
    },
  ],
  queryFields: [
    {
      name: 'dataName',
      label: nameTitle,
      type: 'string',
    },
    {
      name: 'dataCode',
      label: codeTitle,
      type: 'string',
    },
  ],
});

// 工作台-新建工作台弹窗
const addCardDS = ({ roleId, secGrpId }) => ({
  transport: {
    read: ({ data, params }) => ({
      url: `${HZERO_IAM}/v1${levelUrl}/sec-grp-acl-dashboards/${secGrpId}/assignable`,
      params: { ...data, ...params, roleId },
      method: 'get',
    }),
    submit: ({ data }) => {
      const selectedAddRows = data.map((item) => ({
        ...item,
        x: 0,
        y: 0,
        defaultDisplayFlag: 0,
      }));
      return {
        url: `${HZERO_IAM}/v1${levelUrl}/sec-grp-acl-dashboards/${secGrpId}`,
        data: selectedAddRows,
        method: 'post',
      };
    },
  },
  autoQuery: true,
  selection: false,
  pageSize: 8,
  fields: [
    {
      name: 'isSelected',
      type: 'boolean',
      ignore: 'always',
    },
    {
      name: 'name',
      label: intl.get('hiam.roleManagement.model.roleManagement.cardName').d('卡片名称'),
      type: 'string',
    },
    {
      name: 'code',
      label: intl.get('hiam.roleManagement.model.roleManagement.cardCode').d('卡片代码'),
      type: 'string',
    },
    {
      name: 'catalogMeaning',
      label: intl.get('hiam.roleManagement.model.roleManagement.catalogMeaning').d('卡片类型'),
      type: 'string',
    },
  ],
  queryFields: [
    {
      name: 'name',
      label: intl.get('hiam.roleManagement.model.roleManagement.cardName').d('卡片名称'),
      type: 'string',
    },
    {
      name: 'code',
      label: intl.get('hiam.roleManagement.model.roleManagement.cardCode').d('卡片代码'),
      type: 'string',
    },
  ],
});

const roleLovDS = () => ({
  autoCreate: true,
  fields: [
    {
      name: 'roleLov',
      lovCode: 'HIAM.CURRENT_TENANT_ROLES',
      required: true,
      type: 'object',
      label: intl.get('hiam.securityGroup.model.securityGroup.roleLov').d('当前角色'),
      ignore: 'always',
      noCache: true,
    },
    {
      name: 'roleName',
      type: 'string',
      bind: 'roleLov.name',
    },
    {
      name: 'roleId',
      type: 'string',
      bind: 'roleLov.id',
    },
  ],
});

export {
  secGrpDS,
  assignRoleDS,
  secGrpDetailDS,
  visitPermissionDS,
  fieldPermissionDS,
  fieldPermissionDrawerDS,
  cardDS,
  dimensionDS,
  addRoleDS,
  addModalDS,
  addCardDS,
  roleLovDS,
};
