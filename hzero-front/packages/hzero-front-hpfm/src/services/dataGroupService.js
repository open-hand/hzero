/**
 * service - 数据组管理
 * @date: 2019/7/11
 * @author: hulingfangzi <lingfangzi.hu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import request from 'utils/request';
import { HZERO_PLATFORM } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel, parseParameters } from 'utils/utils';

const organizationId = getCurrentOrganizationId();
const organizationRoleLevel = isTenantRoleLevel();

/**
 * 查询数据组列表
 * @param {any} params - 查询参数
 */
export async function queryList(params = {}) {
  return request(
    organizationRoleLevel
      ? `${HZERO_PLATFORM}/v1/${organizationId}/data-groups`
      : `${HZERO_PLATFORM}/v1/data-groups`,
    {
      query: parseParameters(params),
    }
  );
}

/**
 * 新建数据组
 * @param {object} data - 新建表单
 */
export async function createDataGroup(data) {
  return request(
    organizationRoleLevel
      ? `${HZERO_PLATFORM}/v1/${organizationId}/data-groups`
      : `${HZERO_PLATFORM}/v1/data-groups`,
    {
      method: 'POST',
      body: { ...data },
    }
  );
}

/**
 * 查询数据组头信息
 * @param {string} params - 数据组ID
 */
export async function queryDataGroupHead(dataGroupId) {
  return request(
    organizationRoleLevel
      ? `${HZERO_PLATFORM}/v1/${organizationId}/data-groups/${dataGroupId}`
      : `${HZERO_PLATFORM}/v1/data-groups/${dataGroupId}`
  );
}

/**
 * 更新数据组头信息
 * @param {object} data - 表单数据
 */
export async function updateDataGroupHead(data) {
  return request(
    organizationRoleLevel
      ? `${HZERO_PLATFORM}/v1/${organizationId}/data-groups`
      : `${HZERO_PLATFORM}/v1/data-groups`,
    {
      method: 'PUT',
      body: { ...data },
    }
  );
}

/**
 * 查询数据组行列表数据
 * @param {string} params - 数据组ID
 */
export async function queryLineList(params) {
  const { groupId } = params;
  return request(
    organizationRoleLevel
      ? `${HZERO_PLATFORM}/v1/${organizationId}/data-lines/${groupId}`
      : `${HZERO_PLATFORM}/v1/data-lines/${groupId}`,
    {
      query: parseParameters(params),
    }
  );
}

// 查询选择维度代码弹窗数据
export async function queryModalList(params) {
  return request(
    organizationRoleLevel
      ? `${HZERO_PLATFORM}/v1/${organizationId}/data-group-dimension`
      : `${HZERO_PLATFORM}/v1/data-group-dimension`,
    {
      query: parseParameters(params),
    }
  );
}

/**
 * 新建数据组行
 * @param {object} data - 请求数据
 */
export async function createDataGroupLine(data) {
  const { dataGroupId, dataGroupLine } = data;
  return request(
    organizationRoleLevel
      ? `${HZERO_PLATFORM}/v1/${organizationId}/data-lines/${dataGroupId}`
      : `${HZERO_PLATFORM}/v1/data-lines/${dataGroupId}`,
    {
      method: 'POST',
      body: [...dataGroupLine],
    }
  );
}

/**
 * 删除数据组行
 * @param {object} params - 请求数据
 */
export async function deleteDataGroupLine(data) {
  const { dataGroupId, dataGroupLine } = data;
  return request(
    organizationRoleLevel
      ? `${HZERO_PLATFORM}/v1/${organizationId}/data-lines/${dataGroupId}`
      : `${HZERO_PLATFORM}/v1/data-lines/${dataGroupId}`,
    {
      method: 'DELETE',
      body: [...dataGroupLine],
    }
  );
}

// 查询分配值列表数据
export async function queryAssignedValueList(params) {
  const { groupLineId } = params;
  return request(
    organizationRoleLevel
      ? `${HZERO_PLATFORM}/v1/${organizationId}/data-dtls/${groupLineId}`
      : `${HZERO_PLATFORM}/v1/data-dtls/${groupLineId}`,
    {
      query: parseParameters(params),
    }
  );
}

/**
 * 新建分配值
 * @param {object} data - 请求数据
 */
export async function createAssignedValue(data) {
  const { groupLineId, dataGroupDtlList } = data;
  return request(
    organizationRoleLevel
      ? `${HZERO_PLATFORM}/v1/${organizationId}/data-dtls/${groupLineId}`
      : `${HZERO_PLATFORM}/v1/data-dtls/${groupLineId}`,
    {
      method: 'POST',
      body: [...dataGroupDtlList],
    }
  );
}

// 查询选择维度值的弹窗数据
export async function queryValueModalList(params) {
  const { lovId } = params;
  return request(
    organizationRoleLevel
      ? `${HZERO_PLATFORM}/v1/${organizationId}/data-group-dimension/${lovId}`
      : `${HZERO_PLATFORM}/v1/data-group-dimension/${lovId}`,
    {
      query: parseParameters(params),
    }
  );
}

/**
 * 删除分配值
 * @param {object} params - 请求数据
 */
export async function deleteAssignedValue(data) {
  const { groupLineId, dataGroupDtlList } = data;
  return request(
    organizationRoleLevel
      ? `${HZERO_PLATFORM}/v1/${organizationId}/data-dtls/${groupLineId}`
      : `${HZERO_PLATFORM}/v1/data-dtls/${groupLineId}`,
    {
      method: 'DELETE',
      body: [...dataGroupDtlList],
    }
  );
}
