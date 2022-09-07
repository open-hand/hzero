import { HZERO_MNT } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import intl from 'utils/intl';

const organizationId = getCurrentOrganizationId();

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
      label: intl.get('hmnt.threeAuditQuery.model.threeAuditQuery.tenantName').d('租户'),
      ignore: 'always',
      noCache: true,
      lovCode: 'HPFM.TENANT',
    },
    !isTenantRoleLevel() && {
      name: 'tenantId',
      type: 'string',
      label: intl.get('hmnt.threeAuditQuery.model.threeAuditQuery.tenantName').d('租户'),
      bind: 'tenantLov.tenantId',
    },
    {
      name: 'serviceLov',
      type: 'object',
      label: intl.get('hmnt.threeAuditQuery.model.threeAuditQuery.tenantName').d('服务名称'),
      ignore: 'always',
      noCache: true,
      lovCode: !isTenantRoleLevel() ? 'HADM.ROUTE.SERVICE_CODE' : 'HADM.ROUTE.SERVICE_CODE.ORG',
    },
    {
      name: 'serviceName',
      type: 'string',
      label: intl.get('hmnt.threeAuditQuery.model.threeAuditQuery.tenantName').d('服务名称'),
      bind: 'serviceLov.serviceName',
    },
    {
      name: 'userLov',
      type: 'object',
      label: intl.get('hmnt.threeAuditQuery.model.threeAuditQuery.user').d('用户'),
      ignore: 'always',
      noCache: true,
      lovCode: !isTenantRoleLevel() ? 'HIAM.SITE.USER' : 'HIAM.TENANT.USER',
      lovPara: isTenantRoleLevel()
        ? {
            organizationId: getCurrentOrganizationId(),
          }
        : {},
    },
    {
      name: 'userId',
      type: 'string',
      label: intl.get('hmnt.threeAuditQuery.model.threeAuditQuery.user').d('用户'),
      bind: 'userLov.id',
    },
    {
      name: 'roleLov',
      type: 'object',
      label: intl.get('hmnt.threeAuditQuery.model.threeAuditQuery.role').d('角色'),
      ignore: 'always',
      noCache: true,
      lovCode: 'HMNT.AUDIT.TR_ROLE',
      lovPara: isTenantRoleLevel() && {
        tenantId: getCurrentOrganizationId(),
      },
    },
    {
      name: 'roleId',
      type: 'string',
      label: intl.get('hmnt.threeAuditQuery.model.threeAuditQuery.role').d('角色'),
      bind: 'roleLov.id',
    },
    {
      name: 'clientLov',
      type: 'object',
      label: intl.get('hmnt.threeAuditQuery.model.threeAuditQuery.client').d('客户端'),
      ignore: 'always',
      noCache: true,
      lovCode: 'HIAM.OAUTH_CLIENT',
    },
    {
      name: 'clientName',
      type: 'string',
      label: intl.get('hmnt.threeAuditQuery.model.threeAuditQuery.client').d('客户端'),
      bind: 'clientLov.name',
    },
    {
      name: 'auditContent',
      type: 'string',
      label: intl.get('hmnt.threeAuditQuery.model.threeAuditQuery.auditContent').d('操作内容'),
    },
    {
      name: 'auditResult',
      type: 'string',
      label: intl.get('hmnt.threeAuditQuery.model.threeAuditQuery.auditResult').d('操作结果'),
      lookupCode: 'HMNT.AUDIT_RESULT',
    },
    {
      name: 'auditDateStart',
      type: 'dateTime',
      label: intl.get('hmnt.threeAuditQuery.model.threeAuditQuery.auditDateStart').d('操作日期从'),
    },
    {
      name: 'auditDateEnd',
      type: 'dateTime',
      label: intl.get('hmnt.threeAuditQuery.model.threeAuditQuery.auditDateEnd').d('操作日期至'),
    },
    {
      name: 'requestMethod',
      type: 'string',
      label: intl.get('hmnt.threeAuditQuery.model.threeAuditQuery.requestMethod').d('请求方式'),
      lookupCode: 'HIAM.REQUEST_METHOD',
    },
    {
      name: 'requestUrl',
      type: 'string',
      label: intl.get('hmnt.threeAuditQuery.model.threeAuditQuery.requestUrl').d('请求路径'),
    },
    {
      name: 'requestIp',
      type: 'string',
      label: intl.get('hmnt.threeAuditQuery.model.threeAuditQuery.requestIp').d('请求IP'),
    },
    {
      name: 'businessKey',
      type: 'string',
      label: intl.get('hmnt.threeAuditQuery.model.threeAuditQuery.businessKey').d('业务主键'),
    },
  ],
  fields: [
    {
      name: 'tenantName',
      type: 'string',
      label: intl.get('hmnt.threeAuditQuery.model.threeAuditQuery.tenantName').d('租户'),
    },
    {
      name: 'serviceName',
      type: 'string',
      label: intl.get('hmnt.threeAuditQuery.model.threeAuditQuery.serviceName').d('服务名称'),
    },
    {
      name: 'realName',
      type: 'string',
      label: intl.get('hmnt.threeAuditQuery.model.threeAuditQuery.realName').d('用户'),
    },
    {
      name: 'auditContent',
      type: 'string',
      label: intl.get('hmnt.threeAuditQuery.model.threeAuditQuery.auditContent').d('操作内容'),
    },
    {
      name: 'auditDatetime',
      type: 'dateTime',
      label: intl.get('hmnt.threeAuditQuery.model.threeAuditQuery.auditDatetime').d('操作时间'),
    },
    {
      name: 'timeConsuming',
      type: 'string',
      label: intl.get('hmnt.threeAuditQuery.model.threeAuditQuery.timeConsuming').d('操作耗时'),
    },
    {
      name: 'auditResult',
      type: 'string',
      label: intl.get('hmnt.threeAuditQuery.model.threeAuditQuery.auditResult').d('操作结果'),
    },
    {
      name: 'menuName',
      type: 'string',
      label: intl.get('hmnt.threeAuditQuery.model.threeAuditQuery.menuName').d('菜单名称'),
    },
    {
      name: 'requestIp',
      type: 'string',
      label: intl.get('hmnt.threeAuditQuery.model.threeAuditQuery.requestIp').d('请求IP'),
    },
    {
      name: 'requestMethod',
      type: 'string',
      label: intl.get('hmnt.threeAuditQuery.model.threeAuditQuery.requestMethod').d('请求方式'),
    },
    {
      name: 'clientName',
      type: 'string',
      label: intl.get('hmnt.threeAuditQuery.model.threeAuditQuery.clientName').d('客户端'),
    },
    {
      name: 'roleName',
      type: 'string',
      label: intl.get('hmnt.threeAuditQuery.model.threeAuditQuery.roleName').d('角色名称'),
    },
    {
      name: 'requestUrl',
      type: 'string',
      label: intl.get('hmnt.threeAuditQuery.model.threeAuditQuery.requestUrl').d('请求路径'),
    },
    {
      name: 'requestUserAgent',
      type: 'string',
      label: intl.get('hmnt.threeAuditQuery.model.threeAuditQuery.requestUserAgent').d('用户代理'),
    },
    {
      name: 'requestReferrer',
      type: 'string',
      label: intl.get('hmnt.threeAuditQuery.model.threeAuditQuery.requestReferrer').d('Referrer'),
    },
    {
      name: 'businessKey',
      type: 'string',
      label: intl.get('hmnt.threeAuditQuery.model.threeAuditQuery.businessKey').d('业务主键'),
    },
  ],
  transport: {
    read: () => {
      const url = isTenantRoleLevel()
        ? `${HZERO_MNT}/v1/${organizationId}/three-role/audit/operational/logs`
        : `${HZERO_MNT}/v1/three-role/audit/operational/logs`;
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
  transport: {
    read: ({ dataSet }) => {
      const { logLineId } = dataSet;
      const url = isTenantRoleLevel()
        ? `${HZERO_MNT}/v1/${organizationId}/audit/operational/logs/line/${logLineId}`
        : `${HZERO_MNT}/v1/audit/operational/logs/line/${logLineId}`;
      return {
        url,
        method: 'GET',
        params: '',
      };
    },
  },
});

export { tableDs, detailDs };
