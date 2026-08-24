import { getCurrentOrganizationId, parseParameters } from 'utils/utils';
import { HZERO_IAM, HZERO_PLATFORM } from 'utils/config';
import request from 'utils/request';

const currentOrganizationId = getCurrentOrganizationId();
/**
 * 请求快码
 * @param {String} lovCode 快码的code
 */
export async function enumSetQueryByCode(lovCode) {
  return request(`${HZERO_PLATFORM}/v1/lovs/value`, {
    method: 'GET',
    query: {
      lovCode,
    },
  });
}

/**
 * 租户级 查询当前登录用户所拥有分配权限的角色
 * @param {!Number} organizationId 租户id
 * @param {Object} params 查询信息
 * @param {String} params.phone 手机
 * @param {String} params.email 邮箱
 * @param {String} params.realName 描述
 * @param {String} params.loginName 账号
 * @param {Number} pagination.pagination.page 分页
 * @param {Number} pagination.pagination.size 分页大小
 */
export async function querySubAccountOrgList(params) {
  const query = parseParameters(params);
  return request(`${HZERO_IAM}/hzero/v1/${currentOrganizationId}/users/paging`, {
    method: 'GET',
    query,
  });
}

/**
 * 查询当前登录用户所拥有 分配 全选的 角色
 * @param {!Number} currentOrganizationId 租户id
 */
export async function subAccountOrgRoleQueryAll(payload) {
  return request(`${HZERO_IAM}/hzero/v2/roles/self/assignable-roles`, {
    method: 'GET',
    query: {
      ...payload,
      memberType: 'user',
    },
  });
}

/**
 * 创建一个新的账号
 * @param {!Number} organizationId 当前租户id
 * @param {Object} user 账号信息
 * @param {!String} user.realName 描述
 * @param {!String} user.email 邮箱
 * @param {!String} user.phone 手机号码
 * @param {!String} user.password 密码
 * @param {!String} user.anotherPassword 确认密码
 * @param {!String} user.startDateActive 有效日期从
 * @param {String} user.endDateActive 有效日期至
 * @param {String} user.locker 冻结
 * @param {Object[]} user.memberRoleList 角色信息
 * @param {Number|String} user.memberRoleList[].assignLevel 层级 快码对应的标记的 Number
 * @param {String} user.memberRoleList[].assignLevelValue 层级值 快码对应的 value
 * @param {Number} user.memberRoleList[].roleId 角色id
 */
export async function subAccountOrgCreateOne(params = {}) {
  const { userInfo, ...others } = params;
  return request(`${HZERO_IAM}/hzero/v1/${currentOrganizationId}/users`, {
    body: userInfo,
    method: 'POST',
    query: others,
  });
}

/**
 * 查询 子账号详情
 * @param {Number} userId - 账号id
 */
export async function subAccountOrgQuery(params = {}) {
  const { userId, ...others } = params;
  return request(`${HZERO_IAM}/hzero/v1/${currentOrganizationId}/users/${userId}/info`, {
    method: 'GET',
    query: others,
  });
}

/**
 * 通过层级查询label
 * @async
 * @function queryLabelList
 * @param {object} params - 角色ID
 * @param {!boolean} [status=false] - 启用或停用
 * @returns {object} fetch Promise
 */
export async function queryLabelList(params) {
  return request(`${HZERO_IAM}/v1/${currentOrganizationId}/labels/by-type`, {
    query: params,
    method: 'GET',
  });
}

/**
 * 更新账号
 * @param {!Number} organizationId 当前租户id
 * @param {Object} user 账号信息
 * @param {!String} user.realName 描述
 * @param {!String} user.email 邮箱
 * @param {!String} user.phone 手机号码
 * @param {String} [user.password] 密码
 * @param {String} [user.anotherPassword] 确认密码
 * @param {!String} user.startDateActive 有效日期从
 * @param {String} user.endDateActive 有效日期至
 * @param {String} user.locker 冻结
 * @param {Object[]} user.memberRoleList 角色信息
 * @param {Number|String} user.memberRoleList[].assignLevel 层级 快码对应的标记的 Number
 * @param {String} user.memberRoleList[].assignLevelValue 层级值 快码对应的 value
 * @param {Number} user.memberRoleList[].roleId 角色id
 */
export async function subAccountOrgUpdateOne(params) {
  const { userInfo, ...others } = params;
  return request(`${HZERO_IAM}/hzero/v1/${currentOrganizationId}/users`, {
    body: userInfo,
    method: 'PUT',
    query: others,
  });
}

/**
 * 查询当前租户下的用户所拥有的角色
 * @param {!Number} currentOrganizationId 租户id
 * @param {!Number} userId 用户id
 */
export async function subAccountOrgRoleCurrent({ userId, ...params }) {
  const parsedParams = parseParameters(params);
  return request(
    `${HZERO_IAM}/hzero/v1/${currentOrganizationId}/member-roles/user-roles/${userId}`,
    {
      method: 'GET',
      query: parsedParams,
    }
  );
}

/**
 * 查询组织
 * @param {Number} currentOrganizationId 租户id
 * @param {Object} params 其他参数
 * @return {Promise<Object>}
 */
export async function queryUnitsTree(params) {
  return request(`${HZERO_IAM}/hzero/v1/${currentOrganizationId}/hrunits/tree`, {
    query: params,
  });
}

/**
 * 修改密码
 * @param {!Number} id - 账号 id
 * @param {!Number} organizationId - 租户id
 * @param {!Object} body
 * @param {!String} body.originalPassword - 原密码
 * @param {!String} body.password - 密码
 * @param {!String} query
 * @param {!Number} query.organizationId - 用户 租户id
 */
export async function subAccountOrgUpdatePassword(id, organizationId, body, query) {
  return request(`${HZERO_IAM}/hzero/v1/${organizationId}/users/${id}/admin-password`, {
    body,
    query,
    method: 'PUT',
  });
}

/**
 * 更新子账户的密码
 * @param {number} userId 用户id
 * @param {number} organizationId 用户所属租户id
 * @param {string} params.password 密码
 * @return {Promise}
 */
export async function resetPassword(id, organizationId, body) {
  return request(`${HZERO_IAM}/hzero/v1/${organizationId}/users/${id}/admin-password-reset`, {
    body,
    method: 'PUT',
  });
}

/**
 * 向对应的手机号发送验证码修改密码
 * @param {Object} params 验证手机号
 * @param {String} params.phone 手机号
 */
export async function postCaptcha(params) {
  return request(`${HZERO_IAM}/hzero/v1/users/phone/send-captcha`, {
    method: 'GET',
    query: {
      ...params,
      businessScope: 'UPDATE_PASSWORD',
    },
  });
}

/**
 * 修改密码
 * @param {Object} params 验证信息
 * @param {String} params.password 新密码
 * @param {String} params.originalPassword 旧密码
 */
export async function subAccountOrgUpdateSelfPassword({ password, originalPassword }) {
  return request(`${HZERO_IAM}/hzero/v1/users/password`, {
    method: 'PUT',
    body: {
      originalPassword,
      password,
    },
  });
}

/**
 * 解锁用户
 * @param {Number} userId 用户id
 * @param {Number} organizationId 用户 租户 id
 */
export async function subAccountSiteUserUnlock(userId, organizationId) {
  return request(`${HZERO_IAM}/hzero/v1/${organizationId}/users/${userId}/unlocked`, {
    method: 'POST',
  });
}

/**
 * 删除角色
 * @param {Number} memberRoleList 用户角色列表
 */
export async function deleteRoles(memberRoleList) {
  return request(`${HZERO_IAM}/hzero/v1/${currentOrganizationId}/member-roles/batch-delete`, {
    method: 'DELETE',
    body: memberRoleList,
  });
}

/**
 * 查询 可分配的用户组
 * @param {number} params.tenantId - 用户所属租户id
 * @param {number} params.name - 角色名
 * @param {number} params.excludeRoleIds - 新分配 角色id
 * @param {number} params.excludeUserIds - 已分配 角色id
 * @return {Promise<void>}
 */
export async function subAccountOrgGroupQueryAll(params) {
  const parsedParams = parseParameters(params);
  return request(
    `${HZERO_IAM}/v1/${currentOrganizationId}/${parsedParams.userId}/user-group-assigns/exclude-groups`,
    {
      method: 'GET',
      query: parsedParams,
    }
  );
}

/**
 * 查询 已分配的用户组
 * @return {Promise<void>}
 */
export async function subAccountOrgGroupCurrent({ userId, ...params }) {
  const parsedParams = parseParameters(params);
  return request(`${HZERO_IAM}/v1/${currentOrganizationId}/${userId}/user-group-assigns`, {
    method: 'GET',
    query: parsedParams,
  });
}

/**
 * 添加用户组
 * @return {Promise<void>}
 */
export async function addUserGroup(params) {
  const { userId, memberGroupList } = params;
  return request(`${HZERO_IAM}/v1/${currentOrganizationId}/${userId}/user-group-assigns`, {
    method: 'POST',
    body: memberGroupList,
  });
}

/**
 * 删除用户组
 * @param {object[]} memberRoleList - 删除的角色
 */
export async function deleteUserGroup(params) {
  const { userId, remoteRemoveDataSource } = params;
  return request(`${HZERO_IAM}/v1/${currentOrganizationId}/${userId}/user-group-assigns`, {
    method: 'DELETE',
    body: remoteRemoveDataSource,
  });
}

/**
 * 接口字段维护 服务
 * apiFieldService
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-07-09
 * @copyright 2019-07-09 © HAND
 */

/**
 * 接口 model 不能修改
 * @typedef {object} API
 * @property {number} fieldPermissionId - 主键
 * @property {number} fieldId - 字段id
 * @property {string} permissionType - 权限类型 值集
 * @property {string} permissionRule - 权限规则 值集
 * @property {string} permissionDimension - 权限维度 值集
 * @property {number} dimensionValue - 权限维度值
 */

/**
 * @description 字段权限 model 可以 查增改删
 * @typedef {Object} FIELD_PERMISSION
 * @property {number} fieldId - 主键
 * @property {string} fieldDescription - 字段描述 480
 * @property {string} fieldName - 字段名称 120
 * @property {string} fieldType - 字段类型 值集
 * @property {number} orderSeq - 排序
 * @property {number} permissionId - 接口id
 */

/**
 * @typedef {Object} Page<T>
 * @property {number} number - 分页
 * @property {number} size - 分页大小
 * @property {number} totalElements - 总数据量
 * @property {T[]} content - 数据
 */

/**
 * @typedef {Object} PageInfo
 * @property {number} page - 分页
 * @property {number} size - 分页大小
 */

/**
 * 查询接口
 * @param params
 * @return {Promise<API[]>}
 */
export async function apiFieldApiQuery(params) {
  const query = parseParameters(params);
  return request(`${HZERO_IAM}/v1/${getCurrentOrganizationId()}/apis`, {
    method: 'GET',
    query,
  });
}

/**
 * 查询对应接口的字段权限
 * @param {number} userId - 用户id
 * @param {number} permissionId - 接口id
 * @param {PageInfo} params - 查询分页参数
 * @param {FIELD_PERMISSION} params - 查询实体
 * @return {Promise<API[]>}
 */
export async function apiFieldFieldPermissionQuery(userId, permissionId, params) {
  const query = parseParameters(params);
  return request(
    `${HZERO_IAM}/v1/${getCurrentOrganizationId()}/apis/${permissionId}/user/${userId}`,
    {
      method: 'GET',
      query,
    }
  );
}

/**
 * 修改对应接口的字段权限
 * @param {number} userId - 用户id
 * @param {number} permissionId - 接口id
 * @param {FIELD_PERMISSION} record - 修改实体
 */
export async function apiFieldFieldPermissionUpdate(userId, permissionId, record) {
  return request(
    `${HZERO_IAM}/v1/${getCurrentOrganizationId()}/apis/${permissionId}/user/${userId}`,
    {
      method: 'PUT',
      body: record,
    }
  );
}

/**
 * 新增对应接口的字段权限
 * @param {number} userId - 用户id
 * @param {number} permissionId - 接口id
 * @param {FIELD_PERMISSION} record - 修改实体
 */
export async function apiFieldFieldPermissionCreate(userId, permissionId, record) {
  return request(
    `${HZERO_IAM}/v1/${getCurrentOrganizationId()}/apis/${permissionId}/user/${userId}`,
    {
      method: 'POST',
      body: record,
    }
  );
}

/**
 * 删除对应接口的字段权限
 * @param {number} permissionId - 接口id
 * @param {FIELD_PERMISSION} record - 修改实体
 */
export async function apiFieldFieldPermissionRemove(permissionId, record) {
  return request(`${HZERO_IAM}/v1/${getCurrentOrganizationId()}/apis/${permissionId}/permission`, {
    method: 'DELETE',
    body: record,
  });
}

/**
 * 查询已分配的安全组
 * @param {object} params - 查询参数
 */
export async function querySecurityGroup(params) {
  const { roleId } = params;
  const param = parseParameters(params);
  return request(
    `${HZERO_IAM}/v1/${currentOrganizationId}/sec-grp-user-assign/${roleId}/assigned-sec-grp`,
    {
      method: 'GET',
      query: { ...param },
    }
  );
}

/**
 * 角色分配安全组
 * @param {number} roleId - 角色id
 * @param {number} permissionId - 接口id
 * @param {FIELD_PERMISSION} record - 修改实体
 */
export async function assignSecGrp(secGrpList, roleId) {
  return request(
    `${HZERO_IAM}/v1/${currentOrganizationId}/sec-grp-user-assign/${roleId}/assign-sec-grp`,
    {
      method: 'POST',
      body: secGrpList,
    }
  );
}

/**
 * 角色取消分配安全组
 * @param {object} payload - 删除的安全组集合
 */
export async function deleteSecGrp(payload) {
  const { roleId, secGrpList } = payload;
  return request(
    `${HZERO_IAM}/v1/${currentOrganizationId}/sec-grp-user-assign/${roleId}/recycle-sec-grp`,
    {
      method: 'DELETE',
      body: secGrpList,
    }
  );
}

/**
 * 查询角色已分配的指定安全组的字段权限
 * @param {object} params - 请求参数
 */
export async function queryFieldPermission(params) {
  const { roleId, secGrpId } = params;
  const param = parseParameters(params);
  return request(
    `${HZERO_IAM}/v1/${currentOrganizationId}/sec-grp-user-assign/${roleId}/${secGrpId}/field-api`,
    {
      method: 'GET',
      query: { ...param },
    }
  );
}

/**
 * 查询角色已分配的指定安全组的字段权限配置
 * @param {number} params - 请求参数
 */
export async function queryFieldConfig(params) {
  const { roleId, secGrpId, permissionId } = params;
  const param = parseParameters(params);
  return request(
    `${HZERO_IAM}/v1/${currentOrganizationId}/sec-grp-user-assign/${roleId}/${secGrpId}/field-api/${permissionId}/fields`,
    {
      method: 'GET',
      query: { ...param },
    }
  );
}

/**
 * 查询角色已分配的指定安全组的数据权限维度
 * @param {object} params - 请求参数
 */
export async function queryDataDimension(params) {
  const { roleId, secGrpId } = params;
  const param = parseParameters(params);
  return request(
    `${HZERO_IAM}/v1/${currentOrganizationId}/sec-grp-user-assign/${roleId}/${secGrpId}/dim`,
    {
      method: 'GET',
      query: { ...param },
    }
  );
}

/**
 * 查询安全组数据权限tab页
 * @param {number} secGrpId - 安全组ID
 */
export async function queryTabList(secGrpId) {
  return request(`${HZERO_IAM}/v1/sec-grp-dcls/${secGrpId}/dims/assigned`);
}

/**
 * 查询可分配的安全组
 * @param {object} params - 查询参数
 */
export async function queryAssignableSecGrp(params) {
  const { roleId } = params;
  const param = parseParameters(params);
  return request(
    `${HZERO_IAM}/v1/${currentOrganizationId}/sec-grp-user-assign/${roleId}/assignable-sec-grp`,
    {
      method: 'GET',
      query: { ...param },
    }
  );
}

/**
 * 查询角色已分配的指定安全组的数据权限-除公司外
 * @param {object} params - 请求参数
 */
export async function queryData(params) {
  const { roleId, secGrpId } = params;
  const param = parseParameters(params);
  return request(
    `${HZERO_IAM}/v1/${currentOrganizationId}/sec-grp-user-assign/${roleId}/${secGrpId}/dcl`,
    {
      method: 'GET',
      query: { ...param },
    }
  );
}

/**
 * 查询角色已分配的指定安全组的数据权限-公司
 * @param {object} params - 请求参数
 */
export async function queryCompanyPermission(params) {
  const { roleId, secGrpId } = params;
  const param = parseParameters(params);
  return request(
    `${HZERO_IAM}/v1/${currentOrganizationId}/sec-grp-user-assign/${roleId}/${secGrpId}/dcl`,
    {
      method: 'GET',
      query: param,
    }
  );
}

export async function queryDimension() {
  return request(`${HZERO_IAM}/v1/${currentOrganizationId}/doc-type/dimensions/biz`, {
    method: 'GET',
  });
}

export async function queryEmployee(params) {
  const parsedParams = parseParameters(params);
  const { userId, ...res } = parsedParams;
  return request(
    `${HZERO_IAM}/hzero/v1/${currentOrganizationId}/users/${userId}/user-employee-assigns`,
    {
      query: res,
      method: 'GET',
    }
  );
}
