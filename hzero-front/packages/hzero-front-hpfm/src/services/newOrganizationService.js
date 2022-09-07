/**
 * NewOrganization 新版组织架构
 * @date: 2019-12-13
 * @author:  WT <tao04.wang@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import request from 'utils/request';
import { HZERO_PLATFORM } from 'utils/config';
import { getCurrentOrganizationId } from 'utils/utils';

const organizationId = getCurrentOrganizationId();

/**
 * 更新平级部门
 * @param {Object} params - 参数
 */
export async function updateDepartmentInformation({ customizeUnitCode, ...other }) {
  return request(`${HZERO_PLATFORM}/v1/${organizationId}/units`, {
    method: 'PUT',
    body: other,
    query: {
      customizeUnitCode,
    },
  });
}

/**
 * 禁用部门
 * @param {Object} params - 参数
 */
export async function disableDepartment(params) {
  const { tenantId, unitId, objectVersionNumber, _token } = params;
  return request(`${HZERO_PLATFORM}/v1/${organizationId}/units/disable`, {
    method: 'POST',
    body: { tenantId, unitId, objectVersionNumber, _token },
  });
}

/**
 * 启用部门
 * @param {Object} params - 参数
 */
export async function enableDepartment(params) {
  const { tenantId, unitId, objectVersionNumber, _token } = params;
  return request(`${HZERO_PLATFORM}/v1/${organizationId}/units/enable`, {
    method: 'POST',
    body: { tenantId, unitId, objectVersionNumber, _token },
  });
}

/**
 * 更新平级部门
 * @param {Object} params - 参数
 */
export async function updatePositionInformation(params) {
  const { unitCompanyId, unitId, positionId, customizeUnitCode } = params;
  return request(
    `${HZERO_PLATFORM}/v1/${organizationId}/companies/${unitCompanyId}/units/${unitId}/positions/${positionId}`,
    {
      method: 'PUT',
      body: params,
      query: {
        customizeUnitCode,
      },
    }
  );
}

/**
 * 禁用岗位
 * @param {Object} params - 参数
 */
export async function disablePosition(params) {
  const { unitCompanyId, unitId, positionId } = params;
  return request(
    `${HZERO_PLATFORM}/v1/${organizationId}/companies/${unitCompanyId}/units/${unitId}/positions/disable/${positionId}`,
    {
      method: 'POST',
      body: params,
    }
  );
}

/**
 * 启用岗位
 * @param {Object} params - 参数
 */
export async function enablePosition(params) {
  const { unitCompanyId, unitId, positionId } = params;
  return request(
    `${HZERO_PLATFORM}/v1/${organizationId}/companies/${unitCompanyId}/units/${unitId}/positions/enable/${positionId}`,
    {
      method: 'POST',
      body: params,
    }
  );
}

/**
 * 新增员工详情
 * @param {Object} params - 参数
 */
export async function addEmployeeDetail({ customizeUnitCode, ...other }) {
  return request(`${HZERO_PLATFORM}/v1/${organizationId}/plus/employees`, {
    method: 'POST',
    body: other,
    query: {
      customizeUnitCode,
    },
  });
}

/**
 * 新增员工详情
 * @param {Object} params - 参数
 */
export async function updateEmployeeDetail({ customizeUnitCode, ...other }) {
  return request(`${HZERO_PLATFORM}/v1/${organizationId}/plus/employees`, {
    method: 'PUT',
    body: other,
    query: {
      customizeUnitCode,
    },
  });
}
/**
 * 获取员工数据
 * @param {Object} params - 参数
 */
export async function fetchEmployeeDetail(params) {
  const { employeeId, customizeUnitCode } = params;
  return request(`${HZERO_PLATFORM}/v1/${organizationId}/plus/employees/${employeeId}`, {
    method: 'GET',
    query: {
      customizeUnitCode,
    },
  });
}
