import { HZERO_MNT } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import intl from 'utils/intl';

const organizationId = getCurrentOrganizationId();
const isTenant = isTenantRoleLevel();

// 搜索表单DS
const formDS = () => ({
  fields: [
    !isTenant && {
      name: 'tenantLov',
      type: 'object',
      label: intl.get('hmnt.docLogSummary.model.summary.tenantName').d('租户'),
      ignore: 'always',
      noCache: true,
      lovCode: 'HPFM.TENANT',
    },
    !isTenant && {
      name: 'tenantId',
      type: 'string',
      label: intl.get('hmnt.docLogSummary.model.summary.tenantName').d('租户'),
      bind: 'tenantLov.tenantId',
    },
    {
      name: 'serviceName',
      type: 'string',
      label: intl.get('hmnt.docLogSummary.model.summary.serviceName').d('服务'),
    },
    {
      name: 'userLov',
      type: 'object',
      label: intl.get('hmnt.docLogSummary.model.summary.userLov').d('用户'),
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
      name: 'businessKey',
      type: 'string',
      label: intl.get('hmnt.docLogSummary.model.summary.businessKey').d('业务主键'),
    },
    {
      name: 'auditDatetimeStart',
      type: 'dateTime',
      label: intl.get('hmnt.docLogSummary.model.summary.auditDatetimeStart').d('操作时间从'),
    },
    {
      name: 'auditDatetimeEnd',
      type: 'dateTime',
      label: intl.get('hmnt.docLogSummary.model.summary.auditDatetimeEnd').d('操作时间至'),
    },
    {
      name: 'auditResult',
      type: 'string',
      lookupCode: 'HMNT.AUDIT_RESULT',
      label: intl.get('hmnt.docLogSummary.model.summary.auditResult').d('操作结果'),
    },
    {
      name: 'tableName',
      type: 'string',
      label: intl.get('hmnt.docLogSummary.model.summary.tableName').d('操作表'),
    },
    {
      name: 'auditType',
      type: 'string',
      label: intl.get('hmnt.docLogSummary.model.summary.auditType').d('操作类型'),
    },
    {
      name: 'entityId',
      type: 'string',
      label: intl.get('hmnt.docLogSummary.model.summary.entityId').d('数据主键'),
    },
    {
      name: 'fieldCode',
      type: 'string',
      label: intl.get('hmnt.docLogSummary.model.summary.fieldCode').d('操作字段'),
    },
    {
      name: 'lang',
      type: 'string',
      label: intl.get('hmnt.docLogSummary.model.summary.lang').d('语言'),
      lookupCode: 'HPFM.LANGUAGE',
    },
    {
      name: 'fieldValueOld',
      type: 'string',
      label: intl.get('hmnt.docLogSummary.model.summary.fieldValueOld').d('原值'),
    },
    {
      name: 'fieldValueNew',
      type: 'string',
      label: intl.get('hmnt.docLogSummary.model.summary.fieldValueNew').d('现值'),
    },
    {
      name: 'documentLov',
      type: 'object',
      label: intl.get('hmnt.docLogSummary.model.summary.auditDocument').d('单据'),
      ignore: 'always',
      noCache: true,
      lovCode: 'HMNT.DOCUMENT',
      dynamicProps: {
        lovPara({ record }) {
          return { tenantId: isTenant ? organizationId : record.get('tenantId') };
        },
      },
    },
    {
      name: 'auditDocumentId',
      type: 'string',
      label: intl.get('hmnt.docLogSummary.model.summary.auditDocument').d('单据'),
      bind: 'documentLov.auditDocumentId',
    },
    {
      name: 'auditContent',
      type: 'string',
      label: intl.get('hmnt.docLogSummary.model.summary.auditContent').d('操作内容'),
    },
    {
      name: 'includeAuditField',
      type: 'string',
      label: intl.get('hmnt.docLogSummary.model.summary.includeAuditField').d('包含审计字段'),
      lookupCode: 'HPFM.FLAG',
    },
  ].filter(Boolean),
});

/**
 * 表格DS
 */
const tableDS = () => ({
  selection: false,
  autoQuery: true,
  fields: [
    {
      name: 'tenantName',
      type: 'string',
      label: intl.get('hmnt.docLogSummary.model.summary.tenantName').d('租户'),
    },
    {
      name: 'serviceName',
      type: 'string',
      label: intl.get('hmnt.docLogSummary.model.summary.serviceName').d('服务'),
    },
    {
      name: 'loginName',
      type: 'string',
      label: intl.get('hmnt.docLogSummary.model.summary.userLov').d('用户'),
    },

    {
      name: 'businessKey',
      type: 'string',
      label: intl.get('hmnt.docLogSummary.model.summary.businessKey').d('业务主键'),
    },
    {
      name: 'auditDatetime',
      type: 'string',
      label: intl.get('hmnt.docLogSummary.model.summary.auditDatetime').d('操作时间'),
    },
    {
      name: 'auditResultMeaning',
      type: 'string',
      label: intl.get('hmnt.docLogSummary.model.summary.auditResult').d('操作结果'),
    },
    {
      name: 'tableName',
      type: 'string',
      label: intl.get('hmnt.docLogSummary.model.summary.tableName').d('操作表'),
    },
    {
      name: 'auditTypeMeaning',
      type: 'string',
      label: intl.get('hmnt.docLogSummary.model.summary.auditType').d('操作类型'),
    },
    {
      name: 'entityId',
      type: 'string',
      label: intl.get('hmnt.docLogSummary.model.summary.entityId').d('数据主键'),
    },
    {
      name: 'fieldCode',
      type: 'string',
      label: intl.get('hmnt.docLogSummary.model.summary.fieldCode').d('操作字段'),
    },
    {
      name: 'lang',
      type: 'string',
      lookupCode: 'HPFM.LANGUAGE',
      label: intl.get('hmnt.docLogSummary.model.summary.lang').d('语言'),
    },
    {
      name: 'fieldValueOld',
      type: 'string',
      label: intl.get('hmnt.docLogSummary.model.summary.fieldValueOld').d('原值'),
    },
    {
      name: 'fieldValueNew',
      type: 'string',
      label: intl.get('hmnt.docLogSummary.model.summary.fieldValueNew').d('现值'),
    },
    {
      name: 'auditDocumentId',
      type: 'string',
      label: intl.get('hmnt.docLogSummary.model.summary.auditDocument').d('单据'),
    },
    {
      name: 'auditContent',
      type: 'string',
      label: intl.get('hmnt.docLogSummary.model.summary.auditContent').d('操作内容'),
    },
  ],
  transport: {
    read: () => {
      const url = isTenant
        ? `${HZERO_MNT}/v1/${organizationId}/audit-documents/log/detail/summary`
        : `${HZERO_MNT}/v1/audit-documents/log/detail/summary`;
      return {
        url,
        method: 'GET',
      };
    },
  },
});

export { tableDS, formDS };
