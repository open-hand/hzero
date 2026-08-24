/**
 * service 数据模型
 * @date: 2019-12-16
 * @author: xiogngjg
 * @copyright Copyright (c) 2019, Hands
 */

import request from 'utils/request';
import { HZERO_PLATFORM } from 'utils/config';
import {
  parseParameters,
  filterNullValueObject,
  isTenantRoleLevel,
  getCurrentOrganizationId,
} from 'utils/utils';

const prefix = isTenantRoleLevel()
  ? `${HZERO_PLATFORM}/v1/${getCurrentOrganizationId()}`
  : `${HZERO_PLATFORM}/v1`;
/**
 * 查询弹性域模型
 */
export async function queryList(params) {
  const query = filterNullValueObject(parseParameters(params));
  if (query.noPaging) {
    delete query.noPaging;
    delete query.page;
    delete query.size;
  }
  return request(`${prefix}/customize/model/list`, {
    query,
  });
}

/**
 *
 * @param {modelId} 根据modelId查询模型
 */

export async function queryModelDetail(params) {
  return request(`${prefix}/customize/model/detail`, {
    query: params,
  });
}

/**
 * 新建弹性域模型
 */
export async function createModel(params) {
  return request(`${prefix}/customize/model/create`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 更新弹性域模型
 */
export async function modifyModel(params) {
  return request(`${prefix}/customize/model/modify`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 获取特定服务下的所有表名
 */
export async function queryTablesList(params) {
  const query = filterNullValueObject(params);
  return request(`${prefix}/customize/model/list/tables`, {
    query,
  });
}

/**
 * 获取模型下的所有字段
 */
export async function queryFieldsList(params) {
  const query = filterNullValueObject(params);
  return request(`${prefix}/model/field/list`, {
    query,
  });
}

/**
 * 刷新字段
 */
export async function refreshField(params) {
  const query = filterNullValueObject(params);
  return request(`${prefix}/model/field/refresh`, {
    query,
  });
}

/**
 * 新建字段
 */
export async function createField(params) {
  return request(`${prefix}/model/field/create`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 查询关联模型
 */
export async function queryModelRelationList(params) {
  const query = filterNullValueObject(params);
  return request(`${prefix}/model/relation/list`, {
    query,
  });
}

/**
 * 修改字段
 */
export async function modifyField(params) {
  return request(`${prefix}/model/field/modify`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 刪除字段
 */
export async function removeField(params) {
  return request(`${prefix}/model/field`, {
    method: 'DELETE',
    query: params,
  });
}

/**
 * 新建关系模型
 */
export async function createRelation(params) {
  return request(`${prefix}/model/relation/create`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 删除关系模型
 */
export async function deleteRelation(params) {
  return request(`${prefix}/model/relation`, {
    method: 'DELETE',
    query: params,
  });
}

/**
 * 保存关系模型
 */
export async function saveRelationn(params) {
  return request(`${prefix}/model/relation/modify`, {
    method: 'POST',
    body: params,
  });
}
