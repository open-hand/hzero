import intl from 'utils/intl';
import { getCurrentOrganizationId } from 'utils/utils';
import { HZERO_PLATFORM } from 'utils/config';

const organizationId = getCurrentOrganizationId();
const treeDS = () => ({
  autoQuery: false,
  selection: 'single',
  primaryKey: 'positionId',
  idField: 'positionId',
  parentField: 'parentPositionId',
  fields: [
    {
      name: 'positionId',
      type: 'string',
    },
    {
      name: 'positionCode',
      type: 'string',
    },
    {
      name: 'positionName',
      type: 'string',
    },
  ],
  transport: {
    read: (config) => {
      const url = `${HZERO_PLATFORM}/v1/${organizationId}/positions`;
      return {
        config,
        url,
        method: 'GET',
        params: {
          customizeUnitCode: 'HPFM.ORG_LIST.POSITION.BACIS',
        },
      };
    },
  },
});

const searchCompanyAndDepartmentDS = () => ({
  fields: [
    {
      name: 'companyLov',
      type: 'object',
      lovCode: 'HPFM.UNIT.COMPANY',
      lovPara: { tenantId: organizationId },
      label: intl.get('hpfm.organization.model.position.searchCompanyName').d('公司名称'),
      noCache: true,
      ignore: 'always',
    },
    {
      name: 'unitCompanyId',
      type: 'string',
      bind: 'companyLov.unitId',
    },
    {
      name: 'departmentLov',
      type: 'object',
      lovCode: 'HPFM.PLUS.ALL.DEPARTMENT',
      lovPara: { tenantId: organizationId },
      cascadeMap: { unitCompanyId: 'companyLov.unitId' },
      label: intl.get('hpfm.organization.model.position.searchDepartmentName').d('部门名称'),
      noCache: true,
      ignore: 'always',
    },
    {
      name: 'unitDepartmentId',
      type: 'string',
      bind: 'departmentLov.unitId',
    },
  ],
});

const baseInfoDs = () => ({
  fields: [
    {
      name: 'positionCode',
      type: 'string',
      label: intl.get('hpfm.organization.model.position.positionCode').d('岗位编码'),
      format: 'uppercase',
    },
    {
      name: 'positionName',
      type: 'intl',
      label: intl.get('hpfm.organization.model.position.positionName').d('岗位名称'),
    },
    {
      name: 'unitCompanyName',
      type: 'string',
      label: intl.get('hpfm.organization.model.position.company').d('公司'),
    },
    {
      name: 'parentPositionName',
      type: 'string',
      label: intl.get('hpfm.organization.model.position.parentPositionName').d('上级岗位'),
    },
    {
      name: 'unitName',
      type: 'string',
      label: intl.get('hpfm.organization.model.position.department').d('部门'),
    },
    {
      name: 'supervisorFlag',
      trueValue: 1,
      falseValue: 0,
      type: 'boolean',
      label: intl.get('hpfm.organization.model.position.supervisorFlag').d('主管岗位'),
    },
    {
      name: 'enabledFlag',
      trueValue: 1,
      falseValue: 0,
      type: 'boolean',
      label: intl.get('hpfm.organization.model.position.status').d('状态'),
    },
  ],
});
const formDS = () => ({
  fields: [
    {
      name: 'positionId',
      type: 'string',
      label: intl.get('hpfm.organization.model.position.positionId').d('岗位ID'),
    },

    {
      name: 'positionCode',
      type: 'string',
      label: intl.get('hpfm.organization.model.position.positionCode').d('岗位编码'),
      format: 'uppercase',
      required: true,
    },
    {
      name: 'positionName',
      type: 'intl',
      label: intl.get('hpfm.organization.model.position.positionName').d('岗位名称'),
      required: true,
    },
    {
      name: 'companyLov',
      type: 'object',
      lovCode: 'HPFM.UNIT.COMPANY',
      lovPara: { tenantId: organizationId },
      label: intl.get('hpfm.organization.model.position.suoShuCompanyName').d('所属公司'),
      noCache: true,
      ignore: 'always',
      required: true,
    },
    {
      name: 'unitCompanyName',
      type: 'string',
      bind: 'companyLov.unitName',
    },
    {
      name: 'unitCompanyId',
      type: 'string',
      bind: 'companyLov.unitId',
    },
    {
      name: 'tempUnitId',
      type: 'string',
      bind: 'companyLov.unitId',
    },
    {
      name: 'departmentLov',
      type: 'object',
      lovCode: 'HPFM.PLUS.UNIT.DEPARTMENT',
      cascadeMap: { unitCompanyId: 'tempUnitId' },
      label: intl.get('hpfm.organization.model.position.suoShuDepartmentName').d('所属部门'),
      noCache: true,
      ignore: 'always',
      required: true,
    },
    {
      name: 'unitName',
      type: 'string',
      bind: 'departmentLov.unitName',
    },
    {
      name: 'parentPositionLov',
      type: 'object',
      lovCode: 'HPFM.PARENT_POSITION',
      cascadeMap: { positionId: 'positionId' },
      label: intl.get('hpfm.organization.model.position.SuperiorPost').d('上级岗位'),
      noCache: true,
      ignore: 'always',
    },
    {
      name: 'parentPositionId',
      type: 'string',
      bind: 'parentPositionLov.positionId',
    },
    {
      name: 'parentPositionName',
      type: 'string',
      bind: 'parentPositionLov.positionName',
    },
    {
      name: 'unitId',
      type: 'string',
      bind: 'departmentLov.unitId',
    },
    {
      name: 'orderSeq',
      type: 'number',
      min: 0,
      label: intl.get('hpfm.organization.model.position.orderSeq').d('排序号'),
      required: true,
    },
    {
      name: 'description ',
      type: 'string',
      label: intl.get('hpfm.organization.model.position.description').d('描述'),
    },
    {
      name: 'supervisorFlag',
      trueValue: 1,
      falseValue: 0,
      type: 'boolean',
      label: intl.get('hpfm.organization.model.position.supervisorFlag').d('主管岗位'),
    },
    {
      name: 'enabledFlag',
      trueValue: 1,
      falseValue: 0,
      type: 'boolean',
      label: intl.get('hpfm.organization.model.position.status').d('状态'),
    },
  ],
  transport: {
    create: (config) => {
      const { data, params, dataSet } = config;
      const { unitId = '' } = data[0];
      const {
        queryParameter: { unitCompanyId, parentPositionId },
      } = dataSet;
      const url = `${HZERO_PLATFORM}/v1/${organizationId}/companies/${unitCompanyId}/units/${unitId}/position`;
      return {
        data: {
          parentPositionId,
          ...data[0],
          unitCompanyId,
        },
        params: {
          ...params,
          customizeUnitCode: 'HPFM.ORG_LIST.DEPARTMENT.POSITION.EDIT',
        },
        url,
        method: 'POST',
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
      label: intl.get('hpfm.organization.model.position.employeeCode').d('员工编码'),
    },
    {
      name: 'name',
      type: 'string',
      label: intl.get('hpfm.organization.model.position.name').d('姓名'),
    },
    {
      name: 'quickIndex',
      type: 'string',
      label: intl.get('hpfm.organization.model.position.quickIndex').d('快速检索'),
    },
    {
      name: 'phoneticize',
      type: 'string',
      label: intl.get('hpfm.organization.model.position.phoneticize').d('拼音'),
    },
    {
      name: 'email',
      type: 'string',
      label: intl.get('hpfm.organization.model.position.email').d('邮箱'),
    },
    {
      name: 'mobile',
      type: 'string',
      label: intl.get('hpfm.organization.model.position.mobile').d('手机号码'),
    },
    {
      name: 'joinDate',
      type: 'string',
      label: intl.get('hpfm.organization.model.position.joinDate').d('入职日期'),
    },
    {
      name: 'status',
      type: 'string',
      label: intl.get('hpfm.organization.model.position.employeeStatus').d('员工状态'),
    },
    {
      name: 'statusName',
      type: 'string',
      label: intl.get('hpfm.organization.model.position.statusName ').d('状态名称'),
    },
    {
      name: 'unitId',
      type: 'string',
      label: intl.get('hpfm.organization.model.position.unitId').d('部门ID'),
    },
    {
      name: 'unitName',
      type: 'string',
      label: intl.get('hpfm.organization.model.position.unitName').d('部门名称'),
    },
    {
      name: 'bornDate',
      type: 'string',
      label: intl.get('hpfm.organization.model.position.bornDate').d('出生日期'),
    },
    {
      name: 'certificateId',
      type: 'string',
      label: intl.get('hpfm.organization.model.position.certificateId').d('证件ID'),
    },
    {
      name: 'certificateType',
      type: 'string',
      label: intl.get('hpfm.organization.model.position.certificateType ').d('证件类型'),
    },
    {
      name: 'effectiveEndDate',
      type: 'string',
      label: intl.get('hpfm.organization.model.position.effectiveEndDate').d('证件失效日期'),
    },
    {
      name: 'effectiveStartDate',
      type: 'string',
      label: intl.get('hpfm.organization.model.position.effectiveStartDate').d('证件生效日期'),
    },
    {
      name: 'employeeId',
      type: 'string',
      label: intl.get('hpfm.organization.model.position.employeeId').d('员工表ID'),
    },
    {
      name: 'enabledFlag',
      type: 'string',
      label: intl.get('hpfm.organization.model.position.enabledFlag').d('员工表ID'),
    },
    {
      name: 'gender',
      type: 'string',
      lookupCode: 'HPFM.GENDER',
      label: intl.get('hpfm.organization.model.position.gender').d('性别'),
    },
    {
      name: 'nameEn',
      type: 'string',
      label: intl.get('hpfm.organization.model.position.nameEn ').d('英文名'),
    },

    {
      name: 'phoneticize',
      type: 'string',
      label: intl.get('hpfm.organization.model.position.phoneticize').d('拼音'),
    },
    {
      name: 'positionId',
      type: 'string',
      label: intl.get('hpfm.organization.model.position.positionId').d('岗位ID'),
    },
    {
      name: 'primaryPositionFlag',
      type: 'number',
      label: intl.get('hpfm.organization.model.position.isPrimaryPositionFlag').d('主岗'),
    },
    {
      name: 'positionName',
      type: 'string',
      label: intl.get('hpfm.organization.model.position.positionName').d('岗位名称'),
    },
  ],
  transport: {
    read: (config) => {
      const { data, params, dataSet } = config;
      const { positionId } = data;
      const url = `${HZERO_PLATFORM}/v1/${organizationId}/positions/user/${positionId}`;
      if (positionId !== 0) {
        return {
          params,
          url,
          method: 'GET',
        };
      } else {
        dataSet.loadData([]);
      }
    },
  },
});

export { treeDS, searchCompanyAndDepartmentDS, formDS, employeeTableDS, baseInfoDs };
