/**
 * service - 流程设置/流程定义
 * @date: 2018-8-16
 * @version: 1.0.0
 * @author: WH <heng.wei@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */

import request from 'utils/request';
import {
  parseParameters,
  filterNullValueObject,
  getCurrentOrganizationId,
  isTenantRoleLevel,
} from 'utils/utils';
import { HZERO_HWFP } from 'utils/config';

const tenantId = getCurrentOrganizationId();
const isSiteFlag = !isTenantRoleLevel();
const prefix = isSiteFlag ? `${HZERO_HWFP}/v1` : `${HZERO_HWFP}/v1/${tenantId}`;

/**
 * 获取流程分类
 * @async
 * @function fetchCategory
 * @param {object} params - 查询条件
 * @returns {object} fetch Promise
 */
export async function fetchCategory(params) {
  return request(`${prefix}/process/categories/list`, {
    method: 'GET',
    query: { tenantId: params.tenantId },
  });
}
/**
 * 流程查询
 * @async
 * @function fetchProcessList
 * @param {object} params - 查询条件
 * @param {?string} params.category - 流程分类
 * @param {?string} params.key - 流程编码
 * @param {?string} params.name - 流程名称
 * @param {!string} params.tenantId - 租户ID
 * @param {!number} [params.page = 0] - 数据页码
 * @param {!number} [params.size = 10] - 分页大小
 * @returns {object} fetch Promise
 */
export async function fetchProcessList(params) {
  const param = parseParameters(params);
  return request(`${prefix}/process/models`, {
    method: 'GET',
    query: param,
  });
}
/**
 * 流程部署记录查询
 * @async
 * @function fetchDeployHistory
 * @param {object} params - 查询条件
 * @param {?string} params.modelId - 流程Id
 * @param {!string} params.tenantId - 租户ID
 * @returns {object} fetch Promise
 */
export async function fetchDeployHistory(params) {
  return request(`${prefix}/process/models/definitions`, {
    method: 'GET',
    query: params,
  });
}
/**
 * 新增流程
 * @async
 * @function addProcess
 * @param {object} params - 请求条件
 * @param {!string} params.tenantId - 租户ID
 * @param {!object} params.process - 流程对象
 * @returns {object} fetch Promise
 */
export async function addProcess(params) {
  return request(`${prefix}/process/models`, {
    method: 'POST',
    body: { ...params.process },
  });
}

/**
 * 删除流程
 * @async
 * @function deleteProcess
 * @param {object} params - 请求条件
 * @param {!string} params.tenantId - 租户ID
 * @param {!string} parmas.modelId - 流程Id
 * @returns {object} fetch Promise
 */
export async function deleteProcess(params) {
  const { modelId, record } = params;
  return request(`${prefix}/process/models/${modelId}`, {
    method: 'DELETE',
    body: record,
  });
}

/**
 * 发布流程
 * @async
 * @function releaseProcess
 * @param {object} params - 请求条件
 * @param {!string} params.tenantId - 租户ID
 * @param {!string} parmas.modelId - 流程Id
 * @returns {object} fetch Promise
 */
export async function releaseProcess(params) {
  return request(`${prefix}/process/models/${params.modelId}/deploy`, {
    method: 'POST',
  });
}
/**
 * 流程编码唯一性校验
 * @async
 * @function checkProcessKey
 * @param {*} params - 请求参数
 * @param {!string} params.tenantId - 租户ID
 * @param {!object} params.values - 流程Id
 */
export async function checkProcessKey(params) {
  return request(`${prefix}/process/models/check`, {
    method: 'POST',
    body: { ...params.values },
    // responseType: 'text',
  });
}
/**
 * 流程部署详情-部署信息查询
 * @async
 * @function fetchDeployDetail
 * @param {object} params - 请求参数
 * @param {?string} params.deploymentId - 流程部署Id
 * @param {!string} params.tenantId - 租户ID
 */
export async function fetchDeployDetail(params) {
  return request(`${prefix}/repository/deployments/${params.deploymentId}`, {
    method: 'GET',
  });
}
/**
 * 流程部署详情-流程信息查询
 * @async
 * @function fetchProcessDetail
 * @param {object} params - 请求参数
 * @param {?string} params.processId - 流程Id
 * @param {!string} params.tenantId - 租户ID
 */
export async function fetchProcessDetail(params) {
  return request(`${prefix}/process/models/definitions/${params.processId}`, {
    method: 'GET',
  });
}
/**
 * 流程部署详情-预览图查询
 * @async
 * @function fetchProcessImage
 * @param {object} params - 请求参数
 * @param {?string} params.processId - 流程Id
 * @param {!string} params.tenantId - 租户ID
 */
export async function fetchProcessImage(params) {
  return request(`${prefix}/process/models/definitions/image/${params.processId}`, {
    method: 'GET',
    responseType: 'text',
  });
}
/**
 * 删除流程部署记录
 * @async
 * @function deleteDeploy
 * @param {object} params - 请求参数
 * @param {!string} params.tenantId - 租户ID
 * @param {!string} params.deploymentId - 流程部署Id
 */
export async function deleteDeploy(params) {
  return request(`${prefix}/repository/deployments/${params.deploymentId}`, {
    method: 'DELETE',
  });
}

export async function importProcess(params) {
  return request(`${prefix}/process/models/upload`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 复制流程
 * @async
 * @function fetchDocuments
 * @param {object} params - 查询条件
 * @returns {object} fetch Promise
 */
export async function copyValue(params) {
  return request(`${prefix}/process/models/reproduction`, {
    method: 'POST',
    query: params,
  });
}

/**
 * 获取流程单据
 * @async
 * @function fetchDocuments
 * @param {object} params - 查询条件
 * @returns {object} fetch Promise
 */
export async function fetchDocuments(params) {
  return request(`${prefix}/process/categories/${params}`, {
    method: 'GET',
  });
}

/**
 * 获取审批链节点
 */
export async function fetchProcessModelNodes(params) {
  return request(`${prefix}/process/models/node`, {
    method: 'GET',
    query: params,
  });
}

/**
 * 根据流程id查询流程详情
 */
export async function fetchProcessModelDetail(params) {
  return request(`${prefix}/approve/chain/line/proc-info/${params}`, {
    method: 'GET',
  });
}

/**
 * 分页查询审批节点的行信息
 */
export async function fetchNodesLine(params) {
  const query = parseParameters(params);
  return request(`${prefix}/approve/chain/line`, {
    method: 'GET',
    query,
  });
}

/**
 * 删除审批节点的行信息
 */
export async function deleteApproveChainLine(params) {
  return request(`${prefix}/approve/chain/line/delete`, {
    method: 'DELETE',
    body: params,
  });
}

/**
 * 新建审批节点行信息
 */
export async function saveApproveChainLine(params) {
  return request(`${prefix}/approve/chain/line/insert-or-update`, {
    method: 'POST',
    body: params,
  });
}

/**
 *  根据审批链id查询审批链信息
 *
 * */
export async function queryApproveChainLine(params) {
  return request(`${prefix}/approve/chain/line/${params}/info`);
}

/**
 * 查询审批链详情
 */
export async function queryApproveChainLineDetail(params) {
  const query = filterNullValueObject(parseParameters(params));
  return request(`${prefix}/approve/chain/detail`, {
    query,
  });
}

/**
 *  保存审批链详情数据
 */
export async function saveApproveChainLineDetail(params) {
  return request(`${prefix}/approve/chain/detail/insert-or-update`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 删除审批链详情数据
 */
export async function deleteApprovalLineDetail(params) {
  return request(`${prefix}/approve/chain/detail`, {
    method: 'DELETE',
    body: params,
  });
}

/**
 * 保存超时设置
 */
export async function saveOverTimeSetting({ modelId, ...other }) {
  return request(`${prefix}/process/models/${modelId}`, {
    method: 'POST',
    body: other,
  });
}
