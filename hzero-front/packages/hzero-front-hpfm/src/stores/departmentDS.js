import intl from 'utils/intl';
import { getCurrentOrganizationId } from 'utils/utils';
import { HZERO_PLATFORM } from 'utils/config';
import { EMAIL, PHONE } from 'utils/regExp';

const organizationId = getCurrentOrganizationId();

const treeDS = () => ({
  autoQuery: true,
  selection: 'single',
  primaryKey: 'unitId',
  idField: 'unitId',
  parentField: 'parentUnitId',
  fields: [
    {
      name: 'unitId',
      type: 'string',
    },
    {
      name: 'parentUnitId',
      type: 'string',
    },
    {
      name: 'unitName',
      type: 'string',
    },
  ],
  transport: {
    read: (config) => {
      const url = `${HZERO_PLATFORM}/v1/${organizationId}/units/plus/department`;
      return {
        config,
        url,
        method: 'GET',
        params: {
          customizeUnitCode: 'HPFM.ORG_LIST.DEPARTMENT.BASIC',
        },
      };
    },
  },
});

const searchDepartmentDS = () => ({
  fields: [
    {
      name: 'parentUnitIdLov',
      type: 'object',
      lovCode: 'HPFM.UNIT.COMPANY',
      lovPara: { tenantId: organizationId },
      label: intl.get('hpfm.organization.model.department.unitCompanyNameId').d('公司名称'),
      noCache: true,
    },
  ],
});

const createFormDS = () => ({
  fields: [
    {
      name: 'unitCode',
      type: 'string',
      label: intl.get('hpfm.organization.model.department.unitCode').d('部门编码'),
      format: 'uppercase',
      required: true,
    },
    {
      name: 'unitName',
      type: 'intl',
      label: intl.get('hpfm.organization.model.department.unitName').d('部门名称'),
      maxLength: 120,
      required: true,
    },
    {
      name: 'unitTypeCode',
      type: 'string',
      label: intl.get('hpfm.organization.model.department.unitTypeCode').d('部门类型'),
      defaultValue: 'D',
    },
    {
      name: 'parentUnitIdLov',
      type: 'object',
      lovCode: 'HPFM.PLUS.UNIT',
      lovPara: { tenantId: organizationId },
      label: intl.get('hpfm.organization.model.department.parentUnitId').d('上级组织'),
      noCache: true,
      ignore: 'always',
      required: true,
    },
    {
      name: 'parentUnitName',
      type: 'string',
      bind: 'parentUnitIdLov.unitName',
    },
    {
      name: 'parentUnitId',
      type: 'string',
      bind: 'parentUnitIdLov.unitId',
    },
    {
      name: 'unitCompanyId',
      type: 'string',
      bind: 'parentUnitIdLov.unitCompanyId',
    },
    {
      name: 'orderSeq',
      type: 'number',
      min: 0,
      label: intl.get('hpfm.organization.model.department.orderSeq').d('排序号'),
      required: true,
    },
    {
      name: 'supervisorFlag',
      trueValue: 1,
      falseValue: 0,
      type: 'boolean',
      label: intl.get('hpfm.organization.model.department.supervisorFlag').d('主管组织'),
      defaultValue: 0,
    },
    {
      name: 'enabledFlag',
      trueValue: 1,
      falseValue: 0,
      type: 'boolean',
      label: intl.get('hpfm.organization.model.department.status').d('状态'),
      defaultValue: 1,
    },
    {
      name: 'tenantId',
      type: 'string',
      label: intl.get('hpfm.organization.model.department.tenantId').d('租户'),
      defaultValue: organizationId,
    },
  ],
  transport: {
    create: (config) => {
      const url = `${HZERO_PLATFORM}/v1/${organizationId}/units`;
      return {
        config,
        url,
        method: 'POST',
        params: {
          customizeUnitCode: 'HPFM.ORG_LIST.DEPARTMENT.BASIC.EDIT',
        },
      };
    },
  },
});

const baseInfoDs = () => ({
  fields: [
    {
      name: 'unitCode',
      type: 'string',
      label: intl.get('hpfm.organization.model.department.unitCode').d('部门编码'),
    },
    {
      name: 'unitName',
      type: 'string',
      label: intl.get('hpfm.organization.model.department.unitName').d('部门名称'),
    },
    {
      name: 'enabledFlag',
      trueValue: 1,
      falseValue: 0,
      type: 'boolean',
      label: intl.get('hpfm.organization.model.department.status').d('状态'),
      defaultValue: 1,
    },
    {
      name: 'parentUnitName',
      type: 'string',
      label: intl.get('hpfm.organization.model.department.parentUnitId').d('上级组织'),
    },
    {
      name: 'unitCompanyName',
      type: 'string',
      label: intl.get('hpfm.organization.model.department.company').d('公司'),
    },
  ],
});

const formDS = () => ({
  fields: [
    {
      name: 'unitCode',
      type: 'string',
      label: intl.get('hpfm.organization.model.department.unitCode').d('部门编码'),
      format: 'uppercase',
      required: true,
    },
    {
      name: 'unitName',
      type: 'intl',
      maxLength: 120,
      label: intl.get('hpfm.organization.model.department.unitName').d('部门名称'),
      required: true,
    },
    {
      name: 'unitTypeCode',
      type: 'string',
      label: intl.get('hpfm.organization.model.department.unitTypeCode').d('部门类型'),
      defaultValue: 'D',
    },
    {
      name: 'parentUnitIdLov',
      type: 'object',
      lovCode: 'HPFM.UNIT.DEPARTMENT',
      lovPara: { organizationId },
      label: intl.get('hpfm.organization.model.department.parentUnitId').d('上级组织'),
      noCache: true,
      ignore: 'always',
      required: true,
    },
    {
      name: 'parentUnitName',
      type: 'string',
      bind: 'parentUnitIdLov.unitName',
    },
    {
      name: 'parentUnitId',
      type: 'string',
      bind: 'parentUnitIdLov.unitId',
    },
    {
      name: 'unitCompanyId',
      type: 'string',
    },
    {
      name: 'orderSeq',
      type: 'number',
      min: 0,
      label: intl.get('hpfm.organization.model.department.orderSeq').d('排序号'),
      required: true,
    },
    {
      name: 'supervisorFlag',
      trueValue: 1,
      falseValue: 0,
      type: 'boolean',
      label: intl.get('hpfm.organization.model.department.primaryUnitTypeCode').d('主管组织'),
      defaultValue: 0,
    },
    {
      name: 'enabledFlag',
      trueValue: 1,
      falseValue: 0,
      type: 'boolean',
      label: intl.get('hpfm.organization.model.department.status').d('状态'),
      defaultValue: 1,
    },
    {
      name: 'tenantId',
      type: 'string',
      label: intl.get('hpfm.organization.model.department.tenantId').d('租户'),
      defaultValue: organizationId,
    },
  ],
  transport: {
    create: (config) => {
      const url = `${HZERO_PLATFORM}/v1/${organizationId}/units`;
      return {
        config,
        url,
        method: 'POST',
        params: {
          customizeUnitCode: 'HPFM.ORG_LIST.COMPANY.DEPARTMENT.EDIT',
        },
      };
    },
  },
});

const employeeTableDS = () => ({
  selection: false,
  dataKey: 'content',
  fields: [
    {
      name: 'employeeCode',
      type: 'string',
      label: intl.get('hpfm.organization.model.department.employeeCode').d('员工编码'),
    },
    {
      name: 'name',
      type: 'string',
      label: intl.get('hpfm.organization.model.department.name').d('姓名'),
    },
    {
      name: 'quickIndex',
      type: 'string',
      label: intl.get('hpfm.organization.model.department.quickIndex').d('快速检索'),
    },
    {
      name: 'phoneticize',
      type: 'string',
      label: intl.get('hpfm.organization.model.department.phoneticize').d('拼音'),
    },
    {
      name: 'email',
      type: 'string',
      label: intl.get('hpfm.organization.model.department.email').d('邮箱'),
    },
    {
      name: 'mobile',
      type: 'string',
      label: intl.get('hpfm.organization.model.department.mobile').d('手机号码'),
    },
    {
      name: 'joinDate',
      type: 'string',
      label: intl.get('hpfm.organization.model.department.joinDate').d('入职日期'),
    },
    {
      name: 'status',
      type: 'string',
      label: intl.get('hpfm.organization.model.department.employeeStatus').d('员工状态'),
    },
    {
      name: 'statusName',
      type: 'string',
      label: intl.get('hpfm.organization.model.department.statusName ').d('状态名称'),
    },
    {
      name: 'unitId',
      type: 'string',
      label: intl.get('hpfm.organization.model.department.unitId').d('部门ID'),
    },
    {
      name: 'unitName',
      type: 'string',
      label: intl.get('hpfm.organization.model.department.unitName').d('部门名称'),
    },
    {
      name: 'bornDate',
      type: 'string',
      label: intl.get('hpfm.organization.model.department.bornDate').d('出生日期'),
    },
    {
      name: 'certificateId',
      type: 'string',
      label: intl.get('hpfm.organization.model.department.certificateId').d('证件ID'),
    },
    {
      name: 'certificateType',
      type: 'string',
      label: intl.get('hpfm.organization.model.department.certificateType ').d('证件类型'),
    },
    {
      name: 'effectiveEndDate',
      type: 'string',
      label: intl.get('hpfm.organization.model.department.effectiveEndDate').d('证件失效日期'),
    },
    {
      name: 'effectiveStartDate',
      type: 'string',
      label: intl.get('hpfm.organization.model.department.effectiveStartDate').d('证件生效日期'),
    },
    {
      name: 'employeeId',
      type: 'string',
      label: intl.get('hpfm.organization.model.department.employeeId').d('员工表ID'),
    },
    {
      name: 'enabledFlag',
      type: 'string',
      label: intl.get('hpfm.organization.model.department.enabledFlag').d('启用状态'),
    },
    {
      name: 'gender',
      type: 'string',
      lookupCode: 'HPFM.GENDER',
      label: intl.get('hpfm.organization.model.department.gender').d('性别'),
    },
    {
      name: 'nameEn',
      type: 'string',
      label: intl.get('hpfm.organization.model.department.nameEn ').d('英文名'),
    },

    {
      name: 'phoneticize',
      type: 'string',
      label: intl.get('hpfm.organization.model.department.phoneticize').d('拼音'),
    },
    {
      name: 'positionId',
      type: 'string',
      label: intl.get('hpfm.organization.model.department.positionId').d('岗位ID'),
    },
    {
      name: 'primaryPositionFlag',
      type: 'number',
      label: intl.get('hpfm.organization.model.department.isPrimaryPositionFlag').d('主岗'),
    },
    {
      name: 'positionName',
      type: 'string',
      label: intl.get('hpfm.organization.model.department.positionName').d('岗位名称'),
    },
  ],
  transport: {
    read: (config) => {
      const { data, params } = config;
      const { unitId } = data;
      const url = `${HZERO_PLATFORM}/v1/${organizationId}/units/plus/user/${unitId}`;
      return {
        params,
        url,
        method: 'GET',
      };
    },
  },
});

const employeeFormDS = () => ({
  fields: [
    {
      name: 'employeeCode',
      type: 'string',
      label: intl.get('hpfm.organization.model.department.employeeCode').d('员工编码'),
      format: 'uppercase',
      required: true,
    },
    {
      name: 'name',
      type: 'string',
      label: intl.get('hpfm.organization.model.department.name').d('姓名'),
      required: true,
    },
    {
      name: 'gender',
      type: 'string',
      label: intl.get('hpfm.organization.model.department.gender').d('性别'),
      lookupCode: 'HPFM.GENDER',
      required: true,
    },
    {
      name: 'mobile',
      type: 'string',
      pattern: PHONE,
      label: intl.get('hpfm.organization.model.department.mobile').d('手机号码'),
      required: true,
    },
    {
      name: 'email',
      type: 'string',
      pattern: EMAIL,
      label: intl.get('hpfm.organization.model.department.email').d('邮箱'),
      required: true,
    },
    {
      name: 'departmentLov',
      type: 'object',
      lovCode: 'HPFM.PLUS.ALL.DEPARTMENT',
      lovPara: { tenantId: organizationId },
      ignore: 'always',
      noCache: true,
      label: intl.get('hpfm.organization.model.department.primaryUnitName').d('主岗部门名称'),
      required: true,
    },
    {
      name: 'unitId',
      type: 'string',
      bind: 'departmentLov.unitId',
    },
    {
      name: 'unitName',
      type: 'string',
      bind: 'departmentLov.unitName',
    },
    {
      name: 'positionLov',
      type: 'object',
      label: intl.get('hpfm.organization.model.department.position').d('岗位'),
      required: true,
      dynamicProps: ({ record }) => ({
        disabled: !record.get('unitId'),
      }),
      lovCode: 'LOV_POSITION',
      ignore: 'always',
      noCache: true,
    },
    {
      name: 'positionId',
      type: 'string',
      bind: 'positionLov.positionId',
    },
    {
      name: 'positionName',
      type: 'string',
      bind: 'positionLov.positionName',
    },
    {
      name: 'primaryPositionFlag',
      type: 'number',
      defaultValue: 1,
    },
    {
      name: 'status',
      type: 'string',
      label: intl.get('hpfm.organization.model.department.employeeStatus').d('员工状态'),
      required: true,
      defaultValue: 'ON',
    },
    {
      name: 'enabledFlag',
      type: 'string',
      trueValue: '1',
      falseValue: '0',
      defaultValue: '1',
      required: true,
      label: intl.get('hpfm.organization.model.department.enabledFlag').d('启用状态'),
    },
  ],
});

const employeePositionFormDS = () => ({
  fields: [
    {
      name: 'partTimeDepartmentLov',
      type: 'object',
      lovCode: 'HPFM.PLUS.ALL.DEPARTMENT',
      lovPara: { tenantId: organizationId },
      ignore: 'always',
      noCache: true,
      label: intl.get('hpfm.organization.model.department.primaryDepName').d('主岗部门名称'),
    },
    {
      name: 'unitId',
      type: 'string',
      bind: 'partTimeDepartmentLov.unitId',
    },
    {
      name: 'unitName',
      type: 'string',
      bind: 'partTimeDepartmentLov.unitName',
    },
    // {
    //   name: 'partTimePositionId',
    //   type: 'string',
    //   lookupCode: 'LOV_POSITION',
    //   // cascadeMap: { unitId: 'unitId' },
    //   dynamicProps: ({ record }) => ({
    //     disabled: !record.get('unitId'),
    //   }),
    //   noCache: true,
    //   label: intl.get('hpfm.organization.model.department.partTimePosition').d('岗位'),
    // },
    {
      name: 'partTimePositionLov',
      type: 'object',
      label: intl.get('hpfm.organization.model.department.position').d('岗位'),
      required: true,
      dynamicProps: ({ record }) => ({
        disabled: !record.get('unitId'),
      }),
      lovCode: 'LOV_POSITION',
      ignore: 'always',
      noCache: true,
    },
    {
      name: 'partTimePositionId',
      type: 'string',
      bind: 'positionLov.positionId',
    },
    {
      name: 'partTimePositionName',
      type: 'string',
      bind: 'positionLov.positionName',
    },
    {
      name: 'primaryPositionFlag',
      type: 'number',
      defaultValue: 0,
    },
  ],
});

const employeeDetailFormDS = () => ({
  fields: [
    {
      name: 'employeeCode',
      type: 'string',
      label: intl.get('hpfm.organization.model.department.employeeCode').d('员工编码'),
    },
    {
      name: 'name',
      type: 'string',
      label: intl.get('hpfm.organization.model.department.name').d('姓名'),
    },
    {
      name: 'gender',
      type: 'string',
      label: intl.get('hpfm.organization.model.department.gender').d('性别'),
    },
    {
      name: 'mobile',
      type: 'string',
      label: intl.get('hpfm.organization.model.department.mobile').d('手机号码'),
    },
    {
      name: 'email',
      type: 'string',
      label: intl.get('hpfm.organization.model.department.email').d('邮箱'),
    },
    {
      name: 'unitId',
      type: 'string',
      label: intl.get('hpfm.organization.model.department.unitId').d('部门ID'),
    },
    {
      name: 'unitName',
      type: 'string',
      label: intl.get('hpfm.organization.model.department.primaryUnitName').d('主岗部门名称'),
    },
    {
      name: 'positionName',
      type: 'string',
      label: intl.get('hpfm.organization.model.department.positionMing').d('岗位'),
    },
    {
      name: 'primaryPositionFlag',
      type: 'number',
      defaultValue: 1,
    },
    {
      name: 'status',
      type: 'string',
      label: intl.get('hpfm.organization.model.department.employeeStatus').d('员工状态'),
    },
    {
      name: 'enabledFlag',
      type: 'string',
      trueValue: '1',
      falseValue: '0',
      label: intl.get('hpfm.organization.model.department.enabledFlag').d('启用状态'),
    },
  ],
});

const positionTableDS = () => ({
  selection: false,
  dataKey: 'content',
  fields: [
    {
      name: 'name',
      type: 'string',
      label: intl.get('hpfm.organization.model.department.departmentId').d('部门ID'),
    },
    {
      name: 'unitName',
      type: 'string',
      label: intl.get('hpfm.organization.model.department.unitName').d('部门名称'),
    },
    {
      name: 'enabledFlag',
      type: 'number',
      label: intl.get('hpfm.organization.model.department.enabledFlag').d('启用状态'),
    },
    {
      name: 'supervisorFlag',
      type: 'boolean',
      trueValue: 1,
      falseValue: 0,
      label: intl.get('hpfm.organization.model.department.supervisorDepFlag').d('主管岗位标记'),
    },
    {
      name: 'orderSeq',
      type: 'number',
      label: intl.get('hpfm.organization.model.department.orderSeq').d('排序号'),
    },
    {
      name: 'description',
      type: 'string',
      label: intl.get('hpfm.organization.model.department.description').d('岗位描述'),
    },
    {
      name: 'positionName',
      type: 'string',
      label: intl.get('hpfm.organization.model.department.positionName').d('岗位名称'),
    },
    {
      name: 'positionCode',
      type: 'string',
      label: intl.get('hpfm.organization.model.department.positionCode').d('岗位编码'),
    },
    {
      name: 'parentPositionId',
      type: 'string',
      label: intl.get('hpfm.organization.model.department.parentPositionId').d('父岗位ID'),
    },
    {
      name: 'unitId',
      type: 'string',
      label: intl.get('hpfm.organization.model.department.unitId').d('部门ID'),
    },
    {
      name: 'unitCompanyId',
      type: 'string',
      label: intl.get('hpfm.organization.model.department.unitCompanyId').d('公司ID'),
    },
    {
      name: 'positionId',
      type: 'string',
      label: intl.get('hpfm.organization.model.department.positionId').d('岗位ID'),
    },
  ],
  transport: {
    read: (config) => {
      const { data, params } = config;
      const { unitId } = data;
      const url = `${HZERO_PLATFORM}/v1/${organizationId}/positions/${unitId}`;
      return {
        params,
        url,
        method: 'GET',
      };
    },
  },
});

export {
  treeDS,
  searchDepartmentDS,
  createFormDS,
  formDS,
  employeeTableDS,
  employeeFormDS,
  employeePositionFormDS,
  employeeDetailFormDS,
  positionTableDS,
  baseInfoDs,
};
