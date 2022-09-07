import request from 'utils/request';
import { HZERO_MSG } from 'utils/config';

/**
 * 查询配置列表数据
 * @async
 * @function fetchSMSList
 * @param {object} params - 查询条件
 * @param {!number} [params.page = 0] - 数据页码
 * @param {!number} [params.size = 10] - 分页大小
 * @returns {object} fetch Promise
 */
export async function fetchReceiveConfig() {
  return request(`${HZERO_MSG}/v1/user-receive-configs`, {
    method: 'GET',
  });
}

/**
 * 保存
 * @async
 * @function saveConfig
 * @param {object} params - 请求参数
 * @returns {object} fetch Promise
 */
export async function saveConfig(params) {
  return request(`${HZERO_MSG}/v1/user-receive-configs`, {
    method: 'POST',
    body: params,
  });
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
    pathMap[temp.receiveId] = [...(pathMap[temp.parentReceiveId] || []), temp.receiveId];
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
