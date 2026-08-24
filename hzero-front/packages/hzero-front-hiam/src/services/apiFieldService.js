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
 * @property {number} fieldId - 主键
 * @property {string} serviceName - 服务名 Lov
 * @property {string} method - 请求方式 值集
 * @property {string} path - 请求路径
 * @property {number} description - 请求描述
 */

/**
 * @description 字段 model 可以 查增改删
 * @typedef {Object} FIELD
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

import { HZERO_IAM } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel, parseParameters } from 'utils/utils';
import request from 'utils/request';

/**
 * 查询接口
 * @param params
 * @return {Promise<API[]>}
 */
export async function apiFieldApiQuery(params) {
  const query = parseParameters(params);
  const reqUrl = isTenantRoleLevel()
    ? `${HZERO_IAM}/v1/${getCurrentOrganizationId()}/apis`
    : `${HZERO_IAM}/v1/apis`;
  return request(reqUrl, {
    method: 'GET',
    query: {
      ...query,
      includeAll: true,
    },
  });
}

/**
 * 查询对应接口的字段
 * @param {number} permissionId - 接口id
 * @param {PageInfo} params - 查询分页参数
 * @param {FIELD} params - 查询实体
 * @return {Promise<API[]>}
 */
export async function apiFieldFieldQuery(permissionId, params) {
  const query = parseParameters(params);
  const reqUrl = isTenantRoleLevel()
    ? `${HZERO_IAM}/v1/${getCurrentOrganizationId()}/apis/${permissionId}/fields`
    : `${HZERO_IAM}/v1/apis/${permissionId}/fields`;
  return request(reqUrl, {
    method: 'GET',
    query,
  });
}

/**
 * 修改对应接口的字段
 * @param {number} permissionId - 接口id
 * @param {FIELD} record - 修改实体
 */
export async function apiFieldFieldUpdate(permissionId, record) {
  const reqUrl = isTenantRoleLevel()
    ? `${HZERO_IAM}/v1/${getCurrentOrganizationId()}/apis/${permissionId}/fields`
    : `${HZERO_IAM}/v1/apis/${permissionId}/fields`;
  return request(reqUrl, {
    method: 'PUT',
    body: record,
  });
}

/**
 * 新增对应接口的字段
 * @param {number} permissionId - 接口id
 * @param {FIELD} record - 修改实体
 */
export async function apiFieldFieldCreate(permissionId, record) {
  const reqUrl = isTenantRoleLevel()
    ? `${HZERO_IAM}/v1/${getCurrentOrganizationId()}/apis/${permissionId}/fields`
    : `${HZERO_IAM}/v1/apis/${permissionId}/fields`;
  return request(reqUrl, {
    method: 'POST',
    body: record,
  });
}

/**
 * 删除对应接口的字段
 * @param {number} permissionId - 接口id
 * @param {FIELD} record - 修改实体
 */
export async function apiFieldFieldRemove(permissionId, record) {
  const reqUrl = isTenantRoleLevel()
    ? `${HZERO_IAM}/v1/${getCurrentOrganizationId()}/apis/${permissionId}/fields`
    : `${HZERO_IAM}/v1/apis/${permissionId}/fields`;
  return request(reqUrl, {
    method: 'DELETE',
    body: record,
  });
}
