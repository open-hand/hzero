/**
 * securityGroupService - 安全组service
 * @date: 2019-11-20
 * @version: 0.0.1
 * @author: hulingfangzi <lingfangzi.hu01@@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import queryString from 'query-string';
import request from 'utils/request';
import { HZERO_IAM } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel, parseParameters } from 'utils/utils';

const organizationId = getCurrentOrganizationId();
const organizationRoleLevel = isTenantRoleLevel();

/**
 * 校验数据组编码唯一性
 * @param {object} payload - 编码
 */
export async function validateDuplicate(payload) {
  const params = queryString.stringify(payload);
  return request(`${HZERO_IAM}/v1/sec-grps/check-duplicate?${params}`, {
    method: 'POST',
  });
}

/**
 * 复制安全组
 * @async
 * @function batchAssignPermissionSets
 * @param {!number} secGrpId - 安全组ID
 * @param {!object} data - 保存参数
 * @returns {object} fetch Promise
 */
export async function copyPermissionSets(secGrpId, data) {
  return request(
    organizationRoleLevel
      ? `${HZERO_IAM}/v1/${organizationId}/sec-grps/${secGrpId}/copy`
      : `${HZERO_IAM}/v1/sec-grps/${secGrpId}/copy`,
    {
      method: 'POST',
      body: data,
    }
  );
}

/**
 * 查询安全组可分配权限的菜单子树（我的安全组）
 * @async
 * @function queryPermissionMenus
 * @param {object} params - 查询参数
 */
export async function queryPermissionMenus(params) {
  const { secGrpId } = params;
  return request(
    organizationRoleLevel
      ? `${HZERO_IAM}/v1/${organizationId}/sec-grp-acls/${secGrpId}`
      : `${HZERO_IAM}/v1/sec-grp-acls/${secGrpId}`,
    {
      method: 'GET',
      query: params,
    }
  );
}

/**
 * 查询安全组可分配权限的菜单子树(上级、下级创建的安全组)
 * @async
 * @function queryPermissionMenus
 * @param {object} params - 查询参数
 */
export async function queryOthersPermissionMenus(params) {
  const { secGrpId, roleId, secGrpSource } = params;
  return request(
    organizationRoleLevel
      ? `${HZERO_IAM}/v1/${organizationId}/sec-grp-acls/${secGrpId}/assigned`
      : `${HZERO_IAM}/v1/sec-grp-acls/${secGrpId}/assigned`,
    {
      method: 'GET',
      query: {
        roleId,
        secGrpId,
        secGrpSource,
      },
    }
  );
}

/**
 * 批量分配权限集至安全组
 * @async
 * @function batchAssignPermissionSets
 * @param {!number} secGrpId - 安全组ID
 * @param {!object} data - 保存参数
 * @returns {object} fetch Promise
 */
export async function batchAssignPermissionSets(secGrpId, data) {
  return request(
    organizationRoleLevel
      ? `${HZERO_IAM}/v1/${organizationId}/sec-grp-acls/${secGrpId}`
      : `${HZERO_IAM}/v1/sec-grp-acls/${secGrpId}`,
    {
      method: 'POST',
      body: data,
    }
  );
}

/**
 * 批量取消分配权限集至安全组
 * @async
 * @function batchAssignPermissionSets
 * @param {!number} secGrpId - 安全组ID
 * @param {!object} data - 保存参数
 * @returns {object} fetch Promise
 */
export async function batchUnassignPermissionSets(secGrpId, data) {
  return request(
    organizationRoleLevel
      ? `${HZERO_IAM}/v1/${organizationId}/sec-grp-acls/${secGrpId}`
      : `${HZERO_IAM}/v1/sec-grp-acls/${secGrpId}`,
    {
      method: 'DELETE',
      body: data,
    }
  );
}

/**
 * 查询安全组单据类型定义维度分配列表
 * @async
 * @function querySecGrpDimension
 * @param {object} params - 查询参数
 */
export async function querySecGrpDimension(secGrpId) {
  return request(`${HZERO_IAM}/v1/sec-grp-dcls/${secGrpId}/dims/assigned`);
}

// 查询数据权限-公司tab页的数据
export async function queryCompanyPermission(params) {
  const { secGrpId, isSelf, ...other } = params;
  return request(
    organizationRoleLevel
      ? `${HZERO_IAM}/v1/${organizationId}/sec-grp-dcls/${secGrpId}/authority${
          isSelf ? '' : '/assigned'
        }`
      : `${HZERO_IAM}/v1/sec-grp-dcls/${secGrpId}/authority${isSelf ? '' : '/assigned'}`,
    {
      method: 'GET',
      query: other,
    }
  );
}

// // 查询数据权限-公司tab页的数据(上级或下级角色)
// export async function queryOthersCompanyPermission(params) {
//   const { secGrpId, roleId } = params;
//   const queryParams = queryString.stringify(params);
//   return request(
//     `${HZERO_IAM}/hzero/v1/sec-grps/${roleId}/assign-sec-grp/${secGrpId}/dcl-tree?${queryParams}`
//   );
// }

/**
 * 更新安全组新增公司/业务实体/库存组织权限
 */
export async function updateCompanyPermission(payload) {
  const { secGrpId, authorityTypeCode } = payload;
  return request(
    organizationRoleLevel
      ? `${HZERO_IAM}/v1/${organizationId}/sec-grp-dcls/${secGrpId}/authority?authorityTypeCode=${authorityTypeCode}`
      : `${HZERO_IAM}/v1/sec-grp-dcls/${secGrpId}/authority?authorityTypeCode=${authorityTypeCode}`,
    {
      method: 'POST',
      body: payload.checkedList,
    }
  );
}

/**
 * 加入全部/取消加入全部
 * @param {objetc} payload - 请求数据
 */
export async function updateIncludeAll(payload) {
  const { secGrpId } = payload;
  return request(
    organizationRoleLevel
      ? `${HZERO_IAM}/v1/${organizationId}/sec-grp-dcls/${secGrpId}/authority`
      : `${HZERO_IAM}/v1/sec-grp-dcls/${secGrpId}/authority`,
    {
      method: 'POST',
      body: payload,
    }
  );
}

/**
 * 添加数据
 * @param {object} payload - 请求数据
 */
export async function addData(payload) {
  const { secGrpId, data, authorityTypeCode } = payload;
  return request(
    organizationRoleLevel
      ? `${HZERO_IAM}/v1/${organizationId}/sec-grp-dcls/${secGrpId}/authority?authorityTypeCode=${authorityTypeCode}`
      : `${HZERO_IAM}/v1/sec-grp-dcls/${secGrpId}/authority?authorityTypeCode=${authorityTypeCode}`,
    {
      method: 'POST',
      body: data,
    }
  );
}

/**
 * 查询除公司外的数据权限tab列表
 * @param {obejct} params - 查询参数
 */
export async function queryData(params) {
  const { secGrpId } = params;
  const param = parseParameters(params);
  return request(
    organizationRoleLevel
      ? `${HZERO_IAM}/v1/${organizationId}/sec-grp-dcls/${secGrpId}/authority`
      : `${HZERO_IAM}/v1/sec-grp-dcls/${secGrpId}/authority`,
    {
      method: 'GET',
      query: { ...param },
    }
  );
}
