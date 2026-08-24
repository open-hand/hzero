import request from 'utils/request';
import { HZERO_FILE, HZERO_MSG, HZERO_PLATFORM, VERSION_IS_OP } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel, parseParameters } from 'utils/utils';

function prefixUrl(head, suffixUrl) {
  const isTenant = VERSION_IS_OP || isTenantRoleLevel();
  const organizationId = getCurrentOrganizationId();
  if (isTenant) {
    return `${head}/v1/${organizationId}/${suffixUrl}`;
  }
  return `${head}/v1/${suffixUrl}`;
}

/**
 * 查询公告列表数据
 * @param {Object} params - 查询参数
 * @param {String} [params.page = 0] - 页码
 * @param {String} [params.size = 0] - 页数
 */
export async function fetchNotice(params) {
  const { organizationId, ...other } = params;
  return request(prefixUrl(HZERO_MSG, 'notices'), {
    method: 'GET',
    query: other,
  });
}

/**
 * 创建公告基础信息
 * @async
 * @function fetchEmailData
 * @param {Object} params - 查询参数
 * @param {String} params.page - 页码
 * @param {String} params.size - 页数
 * @param {String} params.enabledFlag - 是否启用
 * @param {String} params.port - 端口
 * @param {String} params.sender - 发送人
 * @param {String} params.serverCode - 账户代码
 * @param {String} params.serverName - 账户名称
 * @param {String} params.serverId - 服务器ID
 */
export async function createNotice(params) {
  return request(prefixUrl(HZERO_MSG, 'notices'), {
    method: 'POST',
    body: params,
  });
}

/**
 * 修改公告基础信息
 * @async
 * @function fetchEmailData
 * @param {Object} params - 查询参数
 * @param {String} params.page - 页码
 * @param {String} params.size - 页数
 * @param {String} params.enabledFlag - 是否启用
 * @param {String} params.port - 端口
 * @param {String} params.sender - 发送人
 * @param {String} params.serverCode - 账户代码
 * @param {String} params.serverName - 账户名称
 * @param {String} params.serverId - 服务器ID
 * @param {String} params.host -邮件服务器
 * @param {String} params.tenantId - 租户ID
 * @param {String} params.tenantName - 租户名称
 * @param {String} params.tryTimes - 重试次数
 * @param {String} params.userName - 用户名称
 * @param {String} params.password - 密码
 * @param {Array} params.emailProperties - 服务器配置属性
 * @param {String} params.emailProperties.propertyCode - 属性编码
 * @param {String} params.emailProperties.propertyId - 属性ID
 * @param {String} params.emailProperties.propertyValue - 属性值
 * @param {String} params.emailProperties.serverId - 服务器ID
 * @param {String} params.emailProperties.tenantId - 租户ID
 */
export async function updateNotice(params) {
  return request(prefixUrl(HZERO_MSG, 'notices'), {
    method: 'PUT',
    body: params,
  });
}

/**
 * 删除公告基础信息
 * @async
 * @function fetchEmailData
 * @param {Object} params - 查询参数
 * @param {String} params.page - 页码
 * @param {String} params.size - 页数
 * @param {String} params.enabledFlag - 是否启用
 * @param {String} params.port - 端口
 * @param {String} params.sender - 发送人
 * @param {String} params.serverCode - 账户代码
 * @param {String} params.serverName - 账户名称
 * @param {String} params.serverId - 服务器ID
 * @param {String} params.host -邮件服务器
 * @param {String} params.tenantId - 租户ID
 * @param {String} params.tenantName - 租户名称
 * @param {String} params.tryTimes - 重试次数
 * @param {String} params.userName - 用户名称
 * @param {String} params.password - 密码
 * @param {Array} params.emailProperties - 服务器配置属性
 * @param {String} params.emailProperties.propertyCode - 属性编码
 * @param {String} params.emailProperties.propertyId - 属性ID
 * @param {String} params.emailProperties.propertyValue - 属性值
 * @param {String} params.emailProperties.serverId - 服务器ID
 * @param {String} params.emailProperties.tenantId - 租户ID
 * @param {Array} params.permissionList = ['hmsg.notices.list.button.delete']
 */
export async function deleteNotice(params) {
  return request(prefixUrl(HZERO_MSG, 'notices'), {
    method: 'DELETE',
    body: params,
  });
}

/**
 * 查询公告明细数据
 * @param {Object} params - 查询参数
 * @param {String} [params.page = 0] - 页码
 * @param {String} [params.size = 0] - 页数
 * @param {Array} params.permissionList = ['hmsg.notices.list.button.release2']
 */
export async function queryNotice(params) {
  return request(prefixUrl(HZERO_MSG, `notices/${params.noticeId}`), {
    method: 'GET',
  });
}

/**
 * 发布公告
 * @param {Object} params - 查询参数
 * @param {String} [params.page = 0] - 页码
 * @param {String} [params.size = 0] - 页数
 */
export async function publicNotice(params) {
  return request(prefixUrl(HZERO_MSG, `notices/${params.noticeId}/publish`), {
    method: 'POST',
    query: params,
  });
}

/**
 * 撤销删除公告
 * @param {Object} params - 查询参数
 * @param {String} [params.organizationId] - 租户ID
 * @param {String} [params.noticeId] - 公告ID
 * @param {Array} params.permissionList = ['hmsg.notices.list.button.revoke2']
 */
export async function revokeNotice(params) {
  return request(prefixUrl(HZERO_MSG, `notices/${params.noticeId}/revoke`), {
    method: 'POST',
    body: params.record,
  });
}

/**
 * 富文本上传
 * @async
 * @function queryList
 * @param {object} params - 查询条件
 * @param {!string} [params.bucketName = 0] - 数据页码
 * @param {!string} [params.fileName = 10] - 分页大小
 * @returns {object} fetch Promise
 */
export async function uploadImage(params = {}, file) {
  const data = new FormData();
  Object.keys(params).forEach(paramKey => {
    data.append(paramKey, params[paramKey]);
  });
  data.append('file', file, file.name);
  return request(`${HZERO_FILE}/v1/files/multipart`, {
    processData: false, // 不会将 data 参数序列化字符串
    method: 'POST',
    body: data,
    responseType: 'text',
  });
}

// FIXME: 没有使用公共的 service
export async function queryNoticeType(params) {
  return request(`${HZERO_PLATFORM}/v1/lovs/value/tree`, {
    method: 'GET',
    query: params,
  });
}

/**
 * 删除公告接收记录
 * @returns {Promise<void>}
 */
export async function removeDraftReceive(record) {
  const requestUrl = prefixUrl(HZERO_MSG, `notices/receivers`);
  return request(requestUrl, {
    method: 'DELETE',
    body: record,
  });
}

/**
 * 查询 历史发布信息
 */
export async function querySystemHistory(noticeId, query) {
  const requestUrl = prefixUrl(HZERO_MSG, `notices/published-records/${noticeId}`);
  return request(requestUrl, {
    method: 'GET',
    query: parseParameters(query),
  });
}

/**
 * 查询 历史发布信息 对应的接收配置
 * @returns {Promise<void>}
 */
export async function queryReceiver(noticeId, query, publishedIds) {
  const requestUrl = prefixUrl(HZERO_MSG, `notices/receivers/${noticeId}`);

  return request(requestUrl, {
    method: 'GET',
    query: {
      ...parseParameters(query),
      publishedIds,
    },
  });
}

/**
 * 新增 接收配置
 * @returns {Promise<void>}
 */
export async function createReceiver(noticeId, records) {
  const requestUrl = prefixUrl(HZERO_MSG, `notices/receivers/${noticeId}`);
  return request(requestUrl, {
    method: 'POST',
    body: records,
  });
}

/**
 * 发布系统消息公告
 * @returns {Promise<void>}
 */
export async function publishSystemNotice(noticeId, records) {
  const requestUrl = prefixUrl(HZERO_MSG, `notices/published-records/${noticeId}/publish`);
  return request(requestUrl, {
    method: 'POST',
    body: records,
  });
}
