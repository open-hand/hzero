import intl from 'utils/intl';
import { getCurrentOrganizationId } from 'utils/utils';
import { HZERO_PLATFORM } from 'utils/config';

const organizationId = getCurrentOrganizationId();
const treeDS = () => ({
  autoQuery: true,
  selection: 'single',
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
      const url = `${HZERO_PLATFORM}/v1/${organizationId}/units/plus/company`;
      return {
        config,
        url,
        method: 'GET',
        params: {
          customizeUnitCode: 'HPFM.ORG_LIST.COMPANY.BASIC',
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
      label: intl.get('hpfm.organization.model.company.companyCode').d('公司编码'),
    },
    {
      name: 'unitName',
      type: 'string',
      label: intl.get('hpfm.organization.model.company.companyName').d('公司名称'),
    },
    {
      name: 'unitTypeMeaning',
      type: 'string',
      label: intl.get('hpfm.organization.model.company.companyTypeCode').d('公司类型'),
    },
    {
      name: 'companyName',
      type: 'string',
      label: intl.get('hpfm.organization.model.company.connectedCompany').d('关联企业'),
    },
    {
      name: 'supervisorFlag',
      trueValue: 1,
      falseValue: 0,
      type: 'boolean',
      label: intl.get('hpfm.organization.model.company.supervisorFlag').d('主管组织'),
    },
    {
      name: 'enabledFlag',
      trueValue: 1,
      falseValue: 0,
      type: 'boolean',
      label: intl.get('hpfm.organization.model.company.status').d('状态'),
    },
  ],
});

const formDS = () => ({
  fields: [
    {
      name: 'unitCode',
      type: 'string',
      label: intl.get('hpfm.organization.model.company.companyCode').d('公司编码'),
      format: 'uppercase',
      required: true,
    },
    {
      name: 'unitName',
      type: 'intl',
      label: intl.get('hpfm.organization.model.company.companyName').d('公司名称'),
      maxLength: 120,
      required: true,
    },
    {
      name: 'unitTypeCode',
      type: 'string',
      required: 'true',
      label: intl.get('hpfm.organization.model.company.companyTypeCode').d('公司类型'),
    },
    {
      name: 'parentUnitIdLov',
      type: 'object',
      lovCode: 'HPFM.UNIT.COMPANY',
      lovPara: { tenantId: organizationId },
      label: intl.get('hpfm.organization.model.company.parentUnit').d('上级组织'),
      noCache: true,
      ignore: 'always',
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
      bind: 'parentUnitIdLov.unitId',
    },
    {
      name: 'companyLov',
      type: 'object',
      lovCode: 'HPFM.COMPANY',
      lovPara: { enabledFlag: 1, tenantId: organizationId },
      label: intl.get('hpfm.organization.model.company.companyLov').d('关联企业'),
      noCache: true,
      ignore: 'always',
    },
    {
      name: 'companyId',
      type: 'string',
      bind: 'companyLov.companyId',
    },
    {
      name: 'companyName',
      type: 'string',
      bind: 'companyLov.companyName',
    },
    {
      name: 'orderSeq',
      type: 'number',
      min: 0,
      label: intl.get('hpfm.organization.model.company.orderSeq').d('排序号'),
      required: true,
    },
    {
      name: 'supervisorFlag',
      trueValue: 1,
      falseValue: 0,
      type: 'boolean',
      label: intl.get('hpfm.organization.model.company.supervisorFlag').d('主管组织'),
      defaultValue: 0,
    },
    {
      name: 'enabledFlag',
      trueValue: 1,
      falseValue: 0,
      type: 'boolean',
      label: intl.get('hpfm.organization.model.company.status').d('状态'),
      defaultValue: 1,
    },
    {
      name: 'tenantId',
      type: 'string',
      label: intl.get('hpfm.organization.model.company.tenantId').d('租户'),
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
        // params: {
        //   customizeUnitCode: 'HPFM.ORG_LIST.COMPANY.BASIC.EDIT',
        // },
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
      label: intl.get('hpfm.organization.model.company.employeeCode').d('员工编码'),
    },
    {
      name: 'name',
      type: 'string',
      label: intl.get('hpfm.organization.model.company.name').d('姓名'),
    },
    {
      name: 'quickIndex',
      type: 'string',
      label: intl.get('hpfm.organization.model.company.quickIndex').d('快速索引'),
    },
    {
      name: 'phoneticize',
      type: 'string',
      label: intl.get('hpfm.organization.model.company.phoneticize').d('拼音'),
    },
    {
      name: 'email',
      type: 'string',
      label: intl.get('hpfm.organization.model.company.email').d('邮箱'),
    },
    {
      name: 'mobile',
      type: 'string',
      label: intl.get('hpfm.organization.model.company.mobile').d('手机号码'),
    },
    {
      name: 'joinDate',
      type: 'string',
      label: intl.get('hpfm.organization.model.company.joinDate').d('入职日期'),
    },
    {
      name: 'status',
      type: 'string',
      label: intl.get('hpfm.organization.model.company.employeeStatus').d('员工状态'),
    },
    {
      name: 'statusName',
      type: 'string',
      label: intl.get('hpfm.organization.model.company.statusName ').d('状态名称'),
    },
    {
      name: 'unitId',
      type: 'string',
      label: intl.get('hpfm.organization.model.company.unitId').d('部门ID'),
    },
    {
      name: 'unitName',
      type: 'string',
      label: intl.get('hpfm.organization.model.company.unitName').d('部门名称'),
    },
    {
      name: 'bornDate',
      type: 'string',
      label: intl.get('hpfm.organization.model.company.bornDate').d('出生日期'),
    },
    {
      name: 'certificateId',
      type: 'string',
      label: intl.get('hpfm.organization.model.company.certificateId').d('证件ID'),
    },
    {
      name: 'certificateType',
      type: 'string',
      label: intl.get('hpfm.organization.model.company.certificateType ').d('证件类型'),
    },
    {
      name: 'effectiveEndDate',
      type: 'string',
      label: intl.get('hpfm.organization.model.company.effectiveEndDate').d('证件失效日期'),
    },
    {
      name: 'effectiveStartDate',
      type: 'string',
      label: intl.get('hpfm.organization.model.company.effectiveStartDate').d('证件生效日期'),
    },
    {
      name: 'employeeId',
      type: 'string',
      label: intl.get('hpfm.organization.model.company.employeeId').d('员工表ID'),
    },
    {
      name: 'primaryPositionFlag',
      type: 'number',
      label: intl.get('hpfm.organization.model.department.isPrimaryPositionFlag').d('主岗'),
    },
    {
      name: 'enabledFlag',
      type: 'boolean',
      trueValue: 1,
      falseValue: 0,
      label: intl.get('hpfm.organization.model.company.enabledFlag').d('状态'),
    },
    {
      name: 'gender',
      type: 'string',
      lookupCode: 'HPFM.GENDER',
      label: intl.get('hpfm.organization.model.company.gender').d('性别'),
    },
    {
      name: 'nameEn',
      type: 'string',
      label: intl.get('hpfm.organization.model.company.nameEn ').d('英文名'),
    },

    {
      name: 'phoneticize',
      type: 'string',
      label: intl.get('hpfm.organization.model.company.phoneticize').d('拼音'),
    },
    {
      name: 'positionId',
      type: 'string',
      label: intl.get('hpfm.organization.model.company.positionId').d('岗位ID'),
    },
    {
      name: 'positionName',
      type: 'string',
      label: intl.get('hpfm.organization.model.company.positionName').d('岗位名称'),
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

const positionTableDS = () => ({
  selection: false,
  dataKey: 'content',
  fields: [
    {
      name: 'name',
      type: 'string',
      label: intl.get('hpfm.organization.model.company.positionName').d('岗位名称'),
    },
    {
      name: 'unitName',
      type: 'string',
      label: intl.get('hpfm.organization.model.company.unitName').d('部门名称'),
    },
    {
      name: 'enabledFlag',
      type: 'boolean',
      trueValue: 1,
      falseValue: 0,
      label: intl.get('hpfm.organization.model.company.enabledFlag').d('状态'),
    },
    {
      name: 'supervisorFlag',
      type: 'boolean',
      trueValue: 1,
      falseValue: 0,
      label: intl.get('hpfm.organization.model.company.supervisorUnitFlag').d('主管岗位标记'),
    },
    {
      name: 'orderSeq',
      type: 'number',
      label: intl.get('hpfm.organization.model.company.orderSeq').d('排序号'),
    },
    {
      name: 'description',
      type: 'string',
      label: intl.get('hpfm.organization.model.company.description').d('岗位描述'),
    },
    {
      name: 'positionName',
      type: 'string',
      label: intl.get('hpfm.organization.model.company.positionName').d('岗位名称'),
    },
    {
      name: 'positionCode',
      type: 'string',
      label: intl.get('hpfm.organization.model.company.positionCode').d('岗位编码'),
    },
    {
      name: 'parentPositionId',
      type: 'string',
      label: intl.get('hpfm.organization.model.company.parentPositionId').d('父岗位ID'),
    },
    {
      name: 'unitId',
      type: 'string',
      label: intl.get('hpfm.organization.model.company.unitId').d('部门ID'),
    },
    {
      name: 'unitCompanyId',
      type: 'string',
      label: intl.get('hpfm.organization.model.company.unitCompanyId').d('公司ID'),
    },
    {
      name: 'positionId',
      type: 'string',
      label: intl.get('hpfm.organization.model.company.positionId').d('岗位ID'),
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

const departmentTableDS = () => ({
  selection: false,
  dataKey: 'content',
  fields: [
    {
      name: 'unitId',
      type: 'string',
      label: intl.get('hpfm.organization.model.company.unitId').d('部门ID'),
    },
    {
      name: 'unitCode',
      type: 'string',
      label: intl.get('hpfm.organization.model.company.unitCode').d('部门编码'),
    },
    {
      name: 'unitName',
      type: 'intl',
      label: intl.get('hpfm.organization.model.company.unitName').d('部门名称'),
    },
    {
      name: 'unitTypeCode',
      type: 'string',
      label: intl.get('hpfm.organization.model.company.unitTypeCode').d('部门类型'),
    },
    {
      name: 'quickIndex',
      type: 'string',
      label: intl.get('hpfm.organization.model.company.quickIndex').d('快速索引'),
    },
    {
      name: 'phoneticize',
      type: 'string',
      label: intl.get('hpfm.organization.model.company.phoneticize').d('拼音'),
    },
    {
      name: 'parentUnitName',
      label: intl.get('hpfm.organization.model.company.parentUnitName').d('上级部门'),
      type: 'string',
    },
    {
      name: 'parentUnitId',
      label: intl.get('hpfm.organization.model.company.parentUnitId').d('上级部门ID'),
      type: 'string',
    },
    {
      name: 'unitCompanyId',
      label: intl.get('hpfm.organization.model.company.departmentCompanyId').d('部门所属公司ID'),
      type: 'string',
    },
    {
      name: 'companyId',
      label: intl.get('hpfm.organization.model.company.companyId').d('关联企业ID'),
      type: 'string',
    },
    {
      name: 'orderSeq',
      type: 'number',
      label: intl.get('hpfm.organization.model.company.orderSeq').d('排序号'),
    },
    {
      name: 'supervisorFlag',
      type: 'boolean',
      trueValue: 1,
      falseValue: 0,
      label: intl.get('hpfm.organization.model.company.supervisorFlag').d('主管组织'),
    },
    {
      name: 'enabledFlag',
      type: 'boolean',
      trueValue: 1,
      falseValue: 0,
      label: intl.get('hpfm.organization.model.company.status').d('状态'),
    },
    {
      name: 'tenantId',
      type: 'string',
      label: intl.get('hpfm.organization.model.company.tenantId').d('租户'),
    },
    {
      name: 'levelPath',
      type: 'string',
      label: intl.get('hpfm.organization.model.company.levelPath').d('层级路径'),
    },
    {
      name: 'objectVersionNumber',
      type: 'number',
      label: intl.get('hpfm.organization.model.company.objectVersionNumber').d('行版本号'),
    },
  ],
  transport: {
    read: (config) => {
      const { data, params } = config;
      const { unitId } = data;
      const url = `${HZERO_PLATFORM}/v1/${organizationId}/units/plus/department/${unitId}`;
      return {
        params,
        data,
        url,
        method: 'GET',
      };
    },
  },
});

export { treeDS, formDS, employeeTableDS, positionTableDS, departmentTableDS, baseInfoDs };
