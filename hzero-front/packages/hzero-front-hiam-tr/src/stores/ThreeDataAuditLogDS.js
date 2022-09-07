/*
 * ThreeDataAuditLogDS 三员数据审计日志DS
 * @date: 2020-07-21
 * @author: LiLin <lin.li03@hand-china.com>
 * @copyright Copyright (c) 2020, Hand
 */
import intl from 'utils/intl';
import { HZERO_MNT } from 'utils/config';
import { isTenantRoleLevel, getCurrentOrganizationId } from 'utils/utils';

const organizationId = getCurrentOrganizationId();
const isTenant = isTenantRoleLevel();
const apiPrefix = isTenant ? `${HZERO_MNT}/v1/${organizationId}` : `${HZERO_MNT}/v1`;

// 查询表单DS
const formDS = () => ({
  autoQuery: false,
  autoCreate: true,
  selection: false,
  dataKey: 'content',
  fields: [
    {
      name: 'serviceName',
      type: 'string',
      maxLength: 60,
      label: intl.get('hmnt.threeDataAudit.model.dataAudit.serviceName').d('服务名'),
    },
    {
      name: 'entityCode',
      type: 'string',
      maxLength: 128,
      label: intl.get('hmnt.threeDataAudit.model.dataAudit.entityCode').d('审计实体'),
    },
    {
      name: 'tableName',
      type: 'string',
      maxLength: 30,
      label: intl.get('hmnt.threeDataAudit.model.dataAudit.tableName').d('审计表'),
    },
    {
      name: 'processUserName',
      type: 'string',
      label: intl.get('hmnt.threeDataAudit.model.dataAudit.processUserName').d('操作用户'),
    },
    {
      name: 'startProcessTime',
      type: 'dateTime',
      label: intl.get('hmnt.threeDataAudit.model.dataAudit.startProcessTime').d('操作时间从'),
      max: 'endProcessTime',
    },
    {
      name: 'endProcessTime',
      type: 'dateTime',
      label: intl.get('hmnt.threeDataAudit.model.dataAudit.endProcessTime').d('操作时间至'),
      min: 'startProcessTime',
    },
    !isTenant && {
      name: 'tenantLov',
      lovCode: 'HPFM.TENANT',
      type: 'object',
      label: intl.get('hzero.common.model.common.tenantId').d('租户'),
      ignore: 'always',
      noCache: true,
    },
    !isTenant && {
      name: 'tenantId',
      type: 'string',
      bind: 'tenantLov.tenantId',
    },
    {
      name: 'auditBatchNumber',
      type: 'string',
      label: intl.get('hmnt.threeDataAudit.model.dataAudit.auditBatchNumber').d('操作审计批次'),
    },
  ].filter(Boolean),
});

// 表格ds
const tableDS = () => ({
  autoQuery: false,
  selection: false,
  dataKey: 'content',
  fields: [
    {
      name: 'tenantName',
      type: 'string',
      label: intl.get('hzero.common.model.tenantName').d('租户'),
    },
    {
      name: 'serviceName',
      type: 'string',
      label: intl.get('hmnt.threeDataAudit.model.dataAudit.serviceName').d('服务名'),
    },
    {
      name: 'entityCode',
      type: 'string',
      label: intl.get('hmnt.threeDataAudit.model.dataAudit.entityCode').d('审计实体'),
    },
    {
      name: 'tableName',
      type: 'string',
      label: intl.get('hmnt.threeDataAudit.model.dataAudit.tableName').d('审计表'),
    },
    {
      name: 'entityId',
      type: 'string',
      label: intl.get('hmnt.threeDataAudit.model.dataAudit.entityId').d('主键ID'),
    },
    {
      name: 'auditTypeMeaning',
      type: 'string',
      label: intl.get('hmnt.threeDataAudit.model.dataAudit.auditType').d('操作类型'),
    },
    {
      name: 'menuName',
      type: 'string',
      label: intl.get('hmnt.threeDataAudit.model.dataAudit.menuName').d('菜单名称'),
    },
    {
      name: 'entityVersion',
      type: 'string',
      label: intl.get('hmnt.threeDataAudit.model.dataAudit.entityVersion').d('版本'),
    },
    {
      name: 'processUserName',
      type: 'string',
      label: intl.get('hmnt.threeDataAudit.model.dataAudit.processUserName').d('操作用户'),
    },
    {
      name: 'processTime',
      type: 'string',
      label: intl.get('hmnt.threeDataAudit.model.dataAudit.processTime').d('操作时间'),
    },
    {
      name: 'remark',
      type: 'string',
      label: intl.get('hzero.common.remark').d('备注'),
    },
  ].filter(Boolean),
  transport: {
    read: () => ({
      url: `${apiPrefix}/tr/audit-data/list`,
      method: 'GET',
    }),
  },
});

// 详情表格ds
const detailTableDS = () => ({
  autoQuery: false,
  selection: false,
  fields: [
    {
      label: intl.get('hmnt.threeDataAudit.model.dataAudit.fieldCode').d('审计字段名'),
      name: 'displayName',
      type: 'string',
    },
    {
      label: intl.get('hmnt.threeDataAudit.model.dataAudit.columnName').d('审计列名'),
      name: 'columnName',
      type: 'string',
    },
    {
      label: intl.get('hmnt.threeDataAudit.model.dataAudit.lang').d('语言'),
      name: 'lang',
      type: 'string',
    },
    {
      label: intl.get('hmnt.threeDataAudit.model.dataAudit.fieldValueOld').d('原值'),
      name: 'fieldValueOldMeaning',
      type: 'string',
    },
    {
      label: intl.get('hmnt.threeDataAudit.model.dataAudit.fieldValueNew').d('现值'),
      name: 'fieldValueNewMeaning',
      type: 'string',
    },
    {
      name: 'remark',
      type: 'string',
      label: intl.get('hzero.common.remark').d('备注'),
    },
  ],
});

export { tableDS, formDS, detailTableDS };
