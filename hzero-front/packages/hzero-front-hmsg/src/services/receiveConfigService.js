import request from 'utils/request';
import { HZERO_MSG } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';

const organizationId = getCurrentOrganizationId();
const organizationRoleLevel = isTenantRoleLevel();

/**
 * 查询配置列表数据
 * @async
 * @function fetchSMSList
 * @param {object} params - 查询条件
 * @param {!number} [params.page = 0] - 数据页码
 * @param {!number} [params.size = 10] - 分页大小
 * @returns {object} fetch Promise
 */
export async function fetchReceiveConfig(params) {
  return request(
    organizationRoleLevel
      ? `${HZERO_MSG}/v1/${organizationId}/receive-configs`
      : `${HZERO_MSG}/v1/receive-configs`,
    {
      method: 'GET',
      query: params,
    }
  );
}

/**
 * 保存下级配置
 * @async
 * @function saveConfig
 * @param {object} params - 请求参数
 * @returns {object} fetch Promise
 */
export async function saveConfig(params) {
  const { checkedTenantId, payloadParams } = params;
  return request(
    organizationRoleLevel
      ? `${HZERO_MSG}/v1/${organizationId}/receive-configs`
      : `${HZERO_MSG}/v1/receive-configs?tenantId=${checkedTenantId}`,
    {
      method: 'POST',
      body: payloadParams,
    }
  );
}

/**
 * 编辑配置信息
 * @async
 * @function createSMS
 * @param {object} params - 请求参数
 * @returns {object} fetch Promise
 */
export async function editConfig(params) {
  return request(
    organizationRoleLevel
      ? `${HZERO_MSG}/v1/${organizationId}/sms/servers`
      : `${HZERO_MSG}/v1/sms/servers`,
    {
      method: 'PUT',
      body: params,
    }
  );
}

/**
 * 删除配置
 * @async
 * @function deleteHeader
 * @param {object} params - 请求参数
 */
export async function deleteConfig(params) {
  const { checkedTenantId, ...other } = params;
  return request(
    organizationRoleLevel
      ? `${HZERO_MSG}/v1/${organizationId}/receive-configs`
      : `${HZERO_MSG}/v1/receive-configs?tenantId=${checkedTenantId}`,
    {
      method: 'DELETE',
      body: other,
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
  const pathMap = levelPath;
  const renderTree = collections.map(item => {
    const temp = item;
    pathMap[temp.receiveCode] = [...(pathMap[temp.parentReceiveCode] || []), temp.receiveCode];
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
