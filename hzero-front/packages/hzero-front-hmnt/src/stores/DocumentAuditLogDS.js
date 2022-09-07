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
    !isTenantRoleLevel() && {
      name: 'tenantLov',
      type: 'object',
      label: intl.get('hmnt.documentAuditLog.model.documentAuditLog.tenantName').d('租户'),
      ignore: 'always',
      noCache: true,
      lovCode: 'HPFM.TENANT',
    },
    !isTenantRoleLevel() && {
      name: 'tenantId',
      type: 'string',
      label: intl.get('hmnt.documentAuditLog.model.documentAuditLog.tenantName').d('租户'),
      bind: 'tenantLov.tenantId',
    },
    {
      name: 'documentCode',
      type: 'string',
      label: intl.get('hmnt.documentAuditLog.model.documentAuditLog.documentCode').d('单据编码'),
    },
    {
      name: 'documentName',
      type: 'string',
      label: intl.get('hmnt.documentAuditLog.model.documentAuditLog.documentName').d('单据名称'),
    },
    {
      name: 'businessKey',
      type: 'string',
      label: intl.get('hmnt.documentAuditLog.model.documentAuditLog.businessKey').d('业务主键'),
    },
    {
      name: 'auditDatetimeStart',
      type: 'dateTime',
      label: intl
        .get('hmnt.documentAuditLog.model.documentAuditLog.auditDatetimeStart')
        .d('操作时间从'),
    },
    {
      name: 'auditDatetimeEnd',
      type: 'dateTime',
      label: intl
        .get('hmnt.documentAuditLog.model.documentAuditLog.auditDatetimeEnd')
        .d('操作时间至'),
    },
    {
      name: 'userLov',
      type: 'object',
      label: intl.get('hmnt.documentAuditLog.model.documentAuditLog.userLov').d('最后操作人'),
      ignore: 'always',
      noCache: true,
      lovCode: isTenant ? 'HIAM.TENANT.USER' : 'HIAM.SITE.USER',
      lovPara: {
        organizationId: isTenant ? organizationId : '',
      },
    },
    {
      name: 'userId',
      bind: 'userLov.id',
    },
    {
      name: 'auditResult',
      type: 'string',
      lookupCode: 'HMNT.AUDIT_RESULT',
      label: intl.get('hmnt.documentAuditLog.model.documentAuditLog.auditResult').d('操作结果'),
    },
  ],
  fields: [
    {
      name: 'tenantName',
      type: 'string',
      label: intl.get('hmnt.documentAuditLog.model.documentAuditLog.tenantName').d('租户'),
    },
    {
      name: 'documentCode',
      type: 'string',
      label: intl.get('hmnt.documentAuditLog.model.documentAuditLog.documentCode').d('单据编码'),
    },
    {
      name: 'documentName',
      type: 'string',
      label: intl.get('hmnt.documentAuditLog.model.documentAuditLog.documentName').d('单据名称'),
    },
    {
      name: 'businessKey',
      type: 'string',
      label: intl.get('hmnt.documentAuditLog.model.documentAuditLog.businessKey').d('业务主键'),
    },
    {
      name: 'documentDescription',
      type: 'string',
      label: intl
        .get('hmnt.documentAuditLog.model.documentAuditLog.documentDescription')
        .d('单据描述'),
    },
    {
      name: 'menuName',
      type: 'string',
      label: intl.get('hmnt.documentAuditLog.model.documentAuditLog.menuName').d('菜单'),
    },
    {
      name: 'auditDatetime',
      type: 'dateTime',
      label: intl
        .get('hmnt.documentAuditLog.model.documentAuditLog.auditDatetime')
        .d('最后操作时间'),
    },
    {
      name: 'realName',
      type: 'string',
      label: intl.get('hmnt.documentAuditLog.model.documentAuditLog.realName').d('最后操作人'),
    },
    {
      name: 'auditResult',
      type: 'string',
      label: intl.get('hmnt.documentAuditLog.model.documentAuditLog.auditResult').d('操作结果'),
    },
  ],
  transport: {
    read: () => {
      const url = isTenantRoleLevel()
        ? `${HZERO_MNT}/v1/${organizationId}/audit-documents/log`
        : `${HZERO_MNT}/v1/audit-documents/log`;
      return {
        url,
        method: 'GET',
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
    {
      name: 'tenantLov',
      type: 'object',
      label: intl.get('hmnt.documentAuditLog.model.documentAuditLog.tenantName').d('租户'),
      ignore: 'always',
      noCache: true,
      lovCode: 'HPFM.TENANT',
      textField: 'tenantName',
      required: true,
    },
    {
      name: 'tenantId',
      type: 'string',
      label: intl.get('hmnt.documentAuditLog.model.documentAuditLog.tenantName').d('租户'),
      bind: 'tenantLov.tenantId',
      required: true,
    },
    {
      name: 'tenantName',
      type: 'string',
      label: intl.get('hmnt.documentAuditLog.model.documentAuditLog.tenantName').d('租户'),
      bind: 'tenantLov.tenantName',
      required: true,
    },
    {
      name: 'documentCode',
      type: 'string',
      label: intl.get('hmnt.documentAuditLog.model.documentAuditLog.documentCode').d('单据编码'),
      validator: documentCodeValidator,
      maxLength: 60,
      required: true,
    },
    {
      name: 'documentName',
      type: 'string',
      label: intl.get('hmnt.documentAuditLog.model.documentAuditLog.documentName').d('单据名称'),
      required: true,
      maxLength: 120,
    },
    {
      name: 'documentDescription',
      type: 'string',
      label: intl
        .get('hmnt.documentAuditLog.model.documentAuditLog.documentDescription')
        .d('单据描述'),
      maxLength: 480,
    },
    {
      name: 'enabledFlag',
      type: 'boolean',
      label: intl.get('hmnt.documentAuditLog.model.documentAuditLog.enabledFlag').d('是否启用'),
      defaultValue: 1,
      trueValue: 1,
      falseValue: 0,
    },
  ],
  transport: {
    read: ({ dataSet }) => {
      const { auditOpConfigId } = dataSet;
      const url = isTenantRoleLevel()
        ? `${HZERO_MNT}/v1/${organizationId}/audit-documents/log/detail/${auditOpConfigId}`
        : `${HZERO_MNT}/v1/audit-documents/log/detail/${auditOpConfigId}`;
      return {
        url,
        method: 'GET',
      };
    },
  },
});

export { tableDs, detailDs };
