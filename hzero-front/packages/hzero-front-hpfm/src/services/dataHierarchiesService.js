/**
 * DataHierarchies 数据层级配置
 * @date: 2019-8-14
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import request from 'utils/request';
import { HZERO_PLATFORM } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';

const organizationId = getCurrentOrganizationId();
const organizationRoleLevel = isTenantRoleLevel();

/**
 * 查询数据组列表
 * @param {any} params - 查询参数
 */
export async function queryConfig(params = {}) {
  return request(
    organizationRoleLevel
      ? `${HZERO_PLATFORM}/v1/${organizationId}/data-hierarchies`
      : `${HZERO_PLATFORM}/v1/data-hierarchies`,
    {
      method: 'GET',
      query: params,
    }
  );
}

/**
 * 查询数据组列表
 * @param {any} params - 查询参数
 */
export async function getConfigDetail(params = {}) {
  const { dataHierarchyId, tenantId } = params;
  return request(
    organizationRoleLevel
      ? `${HZERO_PLATFORM}/v1/${organizationId}/data-hierarchies/${dataHierarchyId}`
      : `${HZERO_PLATFORM}/v1/data-hierarchies/${dataHierarchyId}`,
    {
      method: 'GET',
      query: { tenantId },
    }
  );
}

/**
 * 查询数据组列表
 * @param {any} params - 查询参数
 */
export async function createConfig(params = {}) {
  return request(
    organizationRoleLevel
      ? `${HZERO_PLATFORM}/v1/${organizationId}/data-hierarchies`
      : `${HZERO_PLATFORM}/v1/data-hierarchies`,
    {
      method: 'POST',
      body: params,
    }
  );
}

/**
 * 查询数据组列表
 * @param {any} params - 查询参数
 */
export async function editConfig(params = {}) {
  return request(
    organizationRoleLevel
      ? `${HZERO_PLATFORM}/v1/${organizationId}/data-hierarchies`
      : `${HZERO_PLATFORM}/v1/data-hierarchies`,
    {
      method: 'PUT',
      body: params,
    }
  );
}

/**
 * 处理接口获取的数据，提取每个节点的层次路径
 * @param {array} collections - 页面展示数据
 * @param {array} levelPath - 特定组织的层级路径
 * @returns {object} 节点树和层次路径组成的对象
 */
export function renderTreeData(collections = [], levelPath = {}) {
  // debugger;
  const pathMap = levelPath;
  const renderTree = collections.map(item => {
    const temp = item;
    pathMap[temp.dataHierarchyId] = [...(pathMap[temp.parentid] || []), temp.dataHierarchyId];
    if (temp.children) {
      temp.children = [...renderTreeData(temp.children || [], pathMap).renderTree];
    }
    return temp;
  });
  return {
    renderTree,
    pathMap,
  };
}
