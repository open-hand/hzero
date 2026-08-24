/**
 * memberRoleService - 角色分配service
 * @date: 2018-7-6
 * @version: 0.0.1
 * @author: lijun <jun.li06@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */

import request from 'utils/request';
import { HZERO_IAM, HZERO_PLATFORM } from 'utils/config';

/**
 * 查询角色成员
 * @async
 * @function queryMembers
 * @param {object} params - 查询条件
 * @param {!number} [params.page = 0] - 数据页码
 * @param {!number} [params.size = 10] - 分页大小
 * @param {number} organizationId - 组织ID
 * @returns {object} fetch Promise
 */
export async function queryMembers(params, organizationId) {
  return request(
    `${HZERO_IAM}/hzero/v1/${
      String(organizationId) === '0' ? '' : `${organizationId}/`
    }member-roles/users/roles`,
    {
      query: params,
    }
  );
}

/**
 * 查询角色成员数量及角色信息
 * @async
 * @function queryRolesLevelUserCount
 * @param {object} params - 查询条件
 * @param {number} organizationId - 组织ID
 * @returns {object} fetch Promise
 */
export async function queryRolesLevelUserCount(params = {}, organizationId) {
  return request(
    `${HZERO_IAM}/hzero/v1/${
      String(organizationId) === '0' ? '' : `${organizationId}/`
    }member-roles/roles/roles-with-usercount`,
    {
      body: params,
    }
  );
}

/**
 * 查询角色下的成员(roleId)
 * @async
 * @function queryRoles
 * @param {object} params - 查询条件
 * @param {!number} param.roleId - 角色ID
 * @param {number} organizationId - 组织ID
 * @returns {object} fetch Promise
 */
export async function queryRoles(params = {}, organizationId) {
  return request(
    `${HZERO_IAM}/hzero/v1/${
      String(organizationId) === '0' ? '' : `${organizationId}/`
    }member-roles/users`,
    {
      query: params,
    }
  );
}

// /**
//  * 校验成员是否存在
//  * @async
//  * @function queryUser
//  * @param {object} params - 查询条件
//  * @param {!string} param.condition - 查询条件(name/email/phone)
//  * @param {number} organizationId - 组织ID
//  * @returns {object} fetch Promise
//  */
// export async function queryUser(params, organizationId) {
//   return request(`${HZERO_IAM}/hzero/v1/${organizationId === 0 ? '' : `${organizationId}/`}users`, {
//     query: params,
//   });
// }

/**
 * 查询当前用户分配角色及子角色的集合列表
 * @async
 * @function queryCreateRolesSublist
 * @param {object} params - 查询条件
 * @param {!number} [params.page = 0] - 数据页码
 * @param {!number} [params.size = 10] - 分页大小
 * @param {number} organizationId - 组织ID
 * @returns {object} fetch Promise
 */
export async function queryCreateRolesSublist(params, organizationId) {
  return request(
    `${HZERO_IAM}/hzero/v1/${
      String(organizationId) === '0' ? '' : `${organizationId}/`
    }member-roles/create-roles-sublist`,
    {
      query: params,
    }
  );
}

/**
 * 查询值集
 * @async
 * @function queryCode
 * @param {object} params - 查询条件
 * @param {!string} param.lovCode - 查询条件
 * @returns {object} fetch Promise
 */
export async function queryCode(params = {}) {
  return request(`${HZERO_PLATFORM}/v1/lovs/value`, {
    query: params,
  });
}

export async function createMember(memberIds = [], params, organizationId) {
  return request(`${HZERO_IAM}/hzero/v1/${organizationId}/member-roles`, {
    method: 'POST',
    body: params,
    query: {
      memberIds: memberIds.toString(),
    },
  });
}

export async function batchAssign(memberIds = [], params, isEdit, organizationId) {
  return request(
    `${HZERO_IAM}/hzero/v1/${
      String(organizationId) === '0' ? '' : `${organizationId}/`
    }member-roles/batch-assign`,
    {
      method: 'POST',
      body: params,
      query: {
        memberIds: memberIds.toString(),
        isEdit,
      },
    }
  );
}

export async function deleteMember(params = {}) {
  return request(`${HZERO_IAM}/hzero/v1/member-roles`, {
    method: 'DELETE',
    body: params,
  });
}

export async function deleteOrganizationMember(params = {}, organizationId) {
  return request(`${HZERO_IAM}/hzero/v1/${organizationId}/member-roles`, {
    method: 'DELETE',
    body: params,
  });
}

export async function queryHrunitsTree(params = {}, organizationId) {
  return request(`${HZERO_IAM}/hzero/v1/${organizationId}/hrunits/tree`, {
    query: params,
  });
}
