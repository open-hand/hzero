import { HZERO_MNT } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import intl from 'utils/intl';
import { CODE_UPPER } from 'utils/regExp';

const organizationId = getCurrentOrganizationId();
const isTenant = isTenantRoleLevel();
function documentCodeValidator(value) {
  const reg = CODE_UPPER;
  if (!reg.test(value)) {
    return intl
      .get('hzero.common.validation.codeUpper')
      .d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”');
  }
  return true;
}

/**
 * 表格DS
 */
const tableDs = () => ({
  selection: false,
  autoQuery: true,
  queryFields: [
    !isTenant && {
      name: 'tenantLov',
      type: 'object',
      label: intl.get('hmnt.documentAuditConfig.model.documentAuditConfig.tenantName').d('租户'),
      ignore: 'always',
      noCache: true,
      lovCode: 'HPFM.TENANT',
    },
    !isTenant && {
      name: 'tenantId',
      type: 'string',
      label: intl.get('hmnt.documentAuditConfig.model.documentAuditConfig.tenantName').d('租户'),
      bind: 'tenantLov.tenantId',
    },
    {
      name: 'documentCode',
      type: 'string',
      label: intl
        .get('hmnt.documentAuditConfig.model.documentAuditConfig.documentCode')
        .d('单据编码'),
    },
    {
      name: 'documentName',
      type: 'string',
      label: intl
        .get('hmnt.documentAuditConfig.model.documentAuditConfig.documentName')
        .d('单据名称'),
    },
  ],
  fields: [
    {
      name: 'tenantName',
      type: 'string',
      label: intl.get('hmnt.documentAuditConfig.model.documentAuditConfig.tenantName').d('租户'),
    },
    {
      name: 'documentCode',
      type: 'string',
      label: intl
        .get('hmnt.documentAuditConfig.model.documentAuditConfig.documentCode')
        .d('单据编码'),
    },
    {
      name: 'documentName',
      type: 'string',
      label: intl
        .get('hmnt.documentAuditConfig.model.documentAuditConfig.documentName')
        .d('单据名称'),
    },
    {
      name: 'documentDescription',
      type: 'string',
      label: intl
        .get('hmnt.documentAuditConfig.model.documentAuditConfig.documentDescription')
        .d('单据描述'),
    },
    {
      name: 'enabledFlag',
      type: 'boolean',
      label: intl
        .get('hmnt.documentAuditConfig.model.documentAuditConfig.enabledFlag')
        .d('是否启用'),
      defaultValue: 1,
      trueValue: 1,
      falseValue: 0,
    },
    {
      name: 'source',
      label: intl.get('hmnt.documentAuditConfig.model.documentAuditConfig.source').d('来源'),
    },
  ],
  transport: {
    read: () => {
      const url = isTenant
        ? `${HZERO_MNT}/v1/${organizationId}/audit-documents/tenant`
        : `${HZERO_MNT}/v1/audit-documents`;
      return {
        url,
        method: 'GET',
      };
    },
    destroy: ({ config, data }) => {
      const url = isTenant
        ? `${HZERO_MNT}/v1/${organizationId}/audit-documents`
        : `${HZERO_MNT}/v1/audit-documents`;
      return {
        ...config,
        url,
        method: 'DELETE',
        data: data[0],
      };
    },
  },
});

/**
 * 编辑头DS
 */
const detailDs = () => ({
  autoQueryAfterSubmit: false,
  fields: [
    !isTenant && {
      name: 'tenantLov',
      type: 'object',
      label: intl.get('hmnt.documentAuditConfig.model.documentAuditConfig.tenantName').d('租户'),
      ignore: 'always',
      noCache: true,
      lovCode: 'HPFM.TENANT',
      textField: 'tenantName',
      required: true,
    },
    !isTenant && {
      name: 'tenantId',
      type: 'string',
      label: intl.get('hmnt.documentAuditConfig.model.documentAuditConfig.tenantName').d('租户'),
      bind: 'tenantLov.tenantId',
      required: true,
    },
    !isTenant && {
      name: 'tenantName',
      type: 'string',
      label: intl.get('hmnt.documentAuditConfig.model.documentAuditConfig.tenantName').d('租户'),
      bind: 'tenantLov.tenantName',
      required: true,
    },
    {
      name: 'documentCode',
      type: 'string',
      label: intl
        .get('hmnt.documentAuditConfig.model.documentAuditConfig.documentCode')
        .d('单据编码'),
      validator: documentCodeValidator,
      maxLength: 60,
      required: true,
    },
    {
      name: 'documentName',
      type: 'string',
      label: intl
        .get('hmnt.documentAuditConfig.model.documentAuditConfig.documentName')
        .d('单据名称'),
      required: true,
      maxLength: 120,
    },
    {
      name: 'documentDescription',
      type: 'string',
      label: intl
        .get('hmnt.documentAuditConfig.model.documentAuditConfig.documentDescription')
        .d('单据描述'),
      maxLength: 480,
    },
    {
      name: 'enabledFlag',
      type: 'boolean',
      label: intl
        .get('hmnt.documentAuditConfig.model.documentAuditConfig.enabledFlag')
        .d('是否启用'),
      defaultValue: 1,
      trueValue: 1,
      falseValue: 0,
    },
  ],
  transport: {
    create: ({ config, data }) => {
      const url = isTenant
        ? `${HZERO_MNT}/v1/${organizationId}/audit-documents`
        : `${HZERO_MNT}/v1/audit-documents`;
      return {
        ...config,
        url,
        method: 'POST',
        data: data[0],
      };
    },
    update: ({ config, data }) => {
      const url = isTenant
        ? `${HZERO_MNT}/v1/${organizationId}/audit-documents`
        : `${HZERO_MNT}/v1/audit-documents`;
      return {
        ...config,
        url,
        method: 'PUT',
        data: data[0],
      };
    },
    read: ({ dataSet }) => {
      const { auditDocumentId } = dataSet;
      const url = isTenant
        ? `${HZERO_MNT}/v1/${organizationId}/audit-documents/${auditDocumentId}`
        : `${HZERO_MNT}/v1/audit-documents/${auditDocumentId}`;
      return {
        url,
        method: 'GET',
        params: '',
      };
    },
  },
});

/**
 * 编辑行DS
 */
const detailTableDs = () => ({
  autoQueryAfterSubmit: false,
  selection: false,
  fields: [
    {
      name: 'tenantName',
      type: 'string',
      label: intl.get('hmnt.documentAuditConfig.model.documentAuditConfig.tenantId').d('租户'),
    },
    {
      name: 'auditTypeMeaning',
      type: 'string',
      label: intl
        .get('hmnt.documentAuditConfig.model.documentAuditConfig.auditTypeMeaning')
        .d('审计类型'),
    },
    {
      name: 'auditType',
      type: 'string',
      label: intl.get('hmnt.documentAuditConfig.model.documentAuditConfig.auditType').d('审计内容'),
      required: true,
    },
  ],
  transport: {
    destroy: ({ config, data, dataSet }) => {
      const { auditDocumentId } = dataSet;
      const url = isTenant
        ? `${HZERO_MNT}/v1/${organizationId}/audit-documents/del-op/${auditDocumentId}`
        : `${HZERO_MNT}/v1/audit-documents/del-op/${auditDocumentId}`;
      return {
        ...config,
        url,
        method: 'DELETE',
        data: data[0],
      };
    },
    read: ({ config, dataSet }) => {
      const { auditDocumentId } = dataSet;
      const url = isTenant
        ? `${HZERO_MNT}/v1/${organizationId}/audit-documents/op/${auditDocumentId}`
        : `${HZERO_MNT}/v1/audit-documents/op/${auditDocumentId}`;
      return {
        ...config,
        url,
        method: 'GET',
      };
    },
  },
});

/**
 * 新建行DS
 */
const addLineDs = () => ({
  autoQueryAfterSubmit: false,
  selection: 'multiple',
  queryFields: [
    // !isTenant && {
    //   name: 'tenantLov',
    //   type: 'object',
    //   label: intl.get('hmnt.documentAuditConfig.model.documentAuditConfig.tenantName').d('租户'),
    //   ignore: 'always',
    //   noCache: true,
    //   lovCode: 'HPFM.TENANT',
    // },
    // !isTenant && {
    //   name: 'tenantId',
    //   type: 'string',
    //   label: intl.get('hmnt.documentAuditConfig.model.documentAuditConfig.tenantName').d('租户'),
    //   bind: 'tenantLov.tenantId',
    // },
    {
      name: 'auditType',
      type: 'string',
      label: intl.get('hmnt.documentAuditConfig.model.documentAuditConfig.auditType').d('审计类型'),
      lookupCode: 'HMNT.AUDIT_OP.TYPE',
    },
    {
      name: 'auditContent',
      type: 'string',
      label: intl.get('hmnt.documentAuditConfig.model.documentAuditConfig.content').d('操作内容'),
    },
  ].filter(Boolean),
  fields: [
    {
      name: 'tenantName',
      type: 'string',
      label: intl.get('hmnt.documentAuditConfig.model.documentAuditConfig.tenantName').d('租户'),
    },
    {
      name: 'auditTypeMeaning',
      type: 'string',
      label: intl.get('hmnt.documentAuditConfig.model.documentAuditConfig.auditType').d('审计类型'),
    },
    {
      name: 'auditType',
      type: 'string',
      label: intl
        .get('hmnt.documentAuditConfig.model.documentAuditConfig.auditContent')
        .d('审计内容'),
    },
    {
      name: 'auditContent',
      type: 'string',
      label: intl
        .get('hmnt.documentAuditConfig.model.documentAuditConfig.auditContent')
        .d('操作内容'),
    },
    {
      name: 'description',
      type: 'string',
      label: intl
        .get('hmnt.documentAuditConfig.model.documentAuditConfig.description')
        .d('审计接口'),
      required: true,
    },
  ],
  transport: {
    create: ({ config, data, dataSet }) => {
      const { auditDocumentId } = dataSet;
      const url = isTenant
        ? `${HZERO_MNT}/v1/${organizationId}/add-op/${auditDocumentId}`
        : `${HZERO_MNT}/v1/add-op/${auditDocumentId}`;
      return {
        ...config,
        url,
        method: 'POST',
        data,
      };
    },
    read: () => {
      const url = isTenant
        ? `${HZERO_MNT}/v1/${organizationId}/audit-op-configs/page`
        : `${HZERO_MNT}/v1/audit-op-configs/page`;
      return {
        url,
        method: 'GET',
      };
    },
  },
});

// 查看明细表单DS
const viewFormDS = () => ({
  paging: false,
  autoQuery: false,
  fields: [
    {
      name: 'tenantName',
      type: 'string',
      label: intl.get('hzero.common.model.common.tenantId').d('租户'),
    },
    {
      name: 'auditType',
      type: 'string',
      lookupCode: 'HMNT.AUDIT_OP.TYPE',
      label: intl.get('hmnt.documentAuditConfig.model.auditConfig.auditType').d('审计类型'),
    },
    {
      name: 'serviceName',
      type: 'string',
      label: intl.get('hmnt.documentAuditConfig.model.auditConfig.serviceName').d('服务'),
    },
    {
      name: 'description',
      type: 'string',
      label: intl.get('hmnt.documentAuditConfig.model.auditConfig.permissionId').d('审计接口'),
    },
    {
      name: 'username',
      type: 'string',
      label: intl.get('hmnt.documentAuditConfig.model.auditConfig.userName').d('用户名'),
    },

    {
      name: 'roleName',
      type: 'string',
      label: intl.get('hmnt.documentAuditConfig.model.auditConfig.roleName').d('角色名'),
    },
    {
      name: 'clientName',
      type: 'string',
      label: intl.get('hmnt.documentAuditConfig.model.auditConfig.clientName').d('客户端'),
    },
    {
      name: 'auditArgsFlag',
      type: 'boolean',
      label: intl.get(`hmnt.documentAuditConfig.model.auditConfig.auditArgsFlag`).d('记录请求参数'),
      defaultValue: 1,
      trueValue: 1,
      falseValue: 0,
    },
    {
      name: 'auditResultFlag',
      type: 'boolean',
      label: intl
        .get(`hmnt.documentAuditConfig.model.auditConfig.auditResultFlag`)
        .d('记录响应参数'),
      defaultValue: 1,
      trueValue: 1,
      falseValue: 0,
    },
    {
      name: 'auditDataFlag',
      type: 'boolean',
      label: intl.get(`hmnt.documentAuditConfig.model.auditConfig.auditDataFlag`).d('记录操作数据'),
      defaultValue: 1,
      trueValue: 1,
      falseValue: 0,
    },
    {
      name: 'businessKey',
      type: 'string',
      label: intl.get('hmnt.documentAuditConfig.model.auditConfig.businessKey').d('业务主键'),
    },
    {
      name: 'auditContent',
      type: 'string',
      label: intl.get('hmnt.documentAuditConfig.model.auditConfig.auditContent').d('操作内容'),
    },
  ],
  transport: {
    read: ({ dataSet }) => {
      const { auditOpConfigId } = dataSet;
      const url = isTenant
        ? `${HZERO_MNT}/v1/${organizationId}/audit-op-configs/${auditOpConfigId}`
        : `${HZERO_MNT}/v1/audit-op-configs/${auditOpConfigId}`;
      return {
        url,
        method: 'GET',
      };
    },
  },
});

// 查看明细表格DS
const viewTableDS = () => ({
  paging: false,
  autoQuery: false,
  fields: [
    !isTenantRoleLevel && {
      label: intl.get('hzero.common.model.common.tenantId').d('租户'),
      type: 'string',
      name: 'tenantName',
    },
    {
      label: intl.get('hmnt.documentAuditConfig.model.auditConfig.tableName').d('表名'),
      type: 'string',
      name: 'tableName',
    },
    {
      label: intl.get('hmnt.documentAuditConfig.model.auditConfig.displayName').d('展示名称'),
      type: 'string',
      name: 'displayName',
    },
  ].filter(Boolean),
  transport: {
    read: () => {
      const url = isTenant
        ? `${HZERO_MNT}/v1/${organizationId}/audit-rel/operational/data`
        : `${HZERO_MNT}/v1/audit-rel/operational/data`;
      return {
        url,
        method: 'GET',
      };
    },
  },
});

// 租户级审计内容子表格
const tenChildTableDS = () => ({
  selection: 'multiple',
  paging: false,
  fields: [
    {
      label: intl.get('hmnt.documentAuditConfig.model.auditConfig.tableName').d('表名'),
      type: 'string',
      name: 'tableName',
    },
    {
      label: intl.get('hmnt.documentAuditConfig.model.auditConfig.displayName').d('展示名称'),
      type: 'string',
      name: 'displayName',
    },
    {
      name: 'selected',
      type: 'number',
    },
  ],
});

// 租户级审计内容父表格
const tenParentTableDS = () => ({
  selection: 'multiple',
  paging: false,
  fields: [
    {
      name: 'auditTypeMeaning',
      type: 'string',
      label: intl
        .get('hmnt.documentAuditConfig.model.documentAuditConfig.auditTypeMeaning')
        .d('审计类型'),
    },
    {
      name: 'operationalContent',
      type: 'string',
      label: intl.get('hmnt.documentAuditConfig.model.documentAuditConfig.auditType').d('审计内容'),
      required: true,
    },
    {
      name: 'selected',
      type: 'number',
    },
  ],
  transport: {
    read: ({ dataSet }) => {
      const { auditDocumentId } = dataSet;
      const url = isTenant
        ? `${HZERO_MNT}/v1/${organizationId}/audit-documents/op-data/${auditDocumentId}`
        : `${HZERO_MNT}/v1/audit-documents/op-data/${auditDocumentId}`;
      return {
        url,
        method: 'GET',
      };
    },
  },
});

// 租户级保存审计内容DS
const saveAuditContentDS = () => ({
  transport: {
    create: ({ data, dataSet }) => {
      const { auditDocumentId } = dataSet;
      const { opDataConfig } = Array.isArray(data) ? data[0] : {};
      return {
        url: isTenant
          ? `${HZERO_MNT}/v1/${organizationId}/audit-documents/op-data/${auditDocumentId}`
          : `${HZERO_MNT}/v1/audit-documents/op-data/${auditDocumentId}`,
        method: 'PUT',
        data: opDataConfig,
      };
    },
  },
});

// 复制DS
const copyDS = () => ({
  transport: {
    create: ({ data }) => {
      const { auditDocumentId } = Array.isArray(data) ? data[0] : {};
      return {
        url: isTenant
          ? `${HZERO_MNT}/v1/${organizationId}/audit-documents/copy?sourceAuditDocumentId=${auditDocumentId}`
          : `${HZERO_MNT}/v1/audit-documents/copy?sourceAuditDocumentId=${auditDocumentId}`,
        method: 'POST',
        data: {},
      };
    },
  },
});

export {
  copyDS,
  tableDs,
  detailDs,
  detailTableDs,
  addLineDs,
  viewFormDS,
  viewTableDS,
  tenChildTableDS,
  tenParentTableDS,
  saveAuditContentDS,
};
