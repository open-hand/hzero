/**
 * codeRuleService - 编码规则 - service
 * @date: 2018-6-29
 * @author: lokya <kan.li01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import request from 'utils/request';
import { parseParameters, isTenantRoleLevel } from 'utils/utils';
import { HZERO_PLATFORM } from 'utils/config';

const isTenant = isTenantRoleLevel();
/**
 * 删除organizationId
 *
 * @param {*} params 传递的参数
 * @returns 删除后的参数
 */
function deleteOrgId(params) {
  const paramsData = params;
  if (params.organizationId) {
    delete paramsData.organizationId;
  }
  return { ...paramsData };
}
/**
 * 删除organizationId
 *
 * @param {*} params 传递的参数
 * @returns 删除后的参数
 */
function deleteOrgIdArr(params) {
  const paramsData = params;
  if (params.organizationId) {
    delete paramsData.organizationId;
  }
  return paramsData.selectedRows;
}

/**
 * ruleCode查询
 * @async
 * @function queryCodeList
 * @param {object} params - 查询条件
 * @param {?string} params.ruleCode - 规则代码
 * @param {?string} params.ruleName - 规则名称
 * @param {!number} [params.page = 0] - 数据页码
 * @param {!number} [params.size = 10] - 分页大小
 * @returns {object} fetch Promise
 */
export async function queryCodeList(params = {}) {
  const pagination = parseParameters(params);
  return request(`${HZERO_PLATFORM}/v1/${isTenant ? `${params.organizationId}/` : ''}code-rule`, {
    method: 'GET',
    query: deleteOrgId(pagination),
  });
}

/**
 * ruleCode删除
 * @async
 * @function deleteCodeRule
 * @param {object[]} params.selectedRows - 选中批量删除数据
 * @param {!string} params.selectedRows[].ruleId - 编码规则id
 * @param {!string} params.selectedRows[].objectVersionNumber - 版本号
 * @returns {object} fetch Promise
 */
export async function deleteCodeRule(params) {
  return request(`${HZERO_PLATFORM}/v1/${isTenant ? `${params.organizationId}/` : ''}code-rule`, {
    method: 'DELETE',
    body: deleteOrgIdArr(params),
  });
}

/**
 *
 * ruleCode新增
 * @async
 * @function addCodeValue
 * @param {object} params.data - 待保存数据
 * @param {!string} params.data.ruleCode - 规则编码
 * @param {!string} params.data.ruleName - 规则名称
 * @param {!string} params.data.meaning - 层级
 * @param {!string} params.data.tenantName - 租户
 * @returns {object} fetch Promise
 */
export async function addCodeValue(params) {
  return request(`${HZERO_PLATFORM}/v1/${isTenant ? `${params.organizationId}/` : ''}code-rule`, {
    method: 'POST',
    body: deleteOrgId(params),
  });
}

/**
 *
 * ruleCodeDist查询头数据
 * @async
 * @function queryDist
 * @param {object} params - 查询条件
 * @param {!string} params.ruleId - 规则id
 * @param {!number} [params.page = 0] - 数据页码
 * @param {!number} [params.size = 10] - 分页大小
 * @returns {object} fetch Promise
 */
export async function queryDist(params) {
  const param = parseParameters(params);
  return request(
    `${HZERO_PLATFORM}/v1/${isTenant ? `${params.organizationId}/` : ''}code-rule/${params.ruleId}`,
    {
      method: 'GET',
      query: param,
    }
  );
}

/**
 *
 * ruleCodeDist删除行数据
 * @async
 * @function deleteDist
 * @param {object[]} params.selectedRows - 选中批量删除数据
 * @param {!string} params.selectedRows[].ruleDistId - 编码规则分发id
 * @param {!string} params.selectedRows[].objectVersionNumber - 版本号
 * @returns {object} fetch Promise
 */
export async function deleteDist(params) {
  return request(
    `${HZERO_PLATFORM}/v1/${isTenant ? `${params.organizationId}/` : ''}code-rule-dist`,
    {
      method: 'DELETE',
      body: deleteOrgIdArr(params),
    }
  );
}

/**
 * 编码详情查询
 * @async
 * @function queryDetail
 * @param {object} params - 查询条件
 * @param {?string} params.ruleDistId - 规则分发id
 * @param {!number} [params.page = 0] - 数据页码
 * @param {!number} [params.size = 10] - 分页大小
 * @returns {object} fetch Promise
 */
export async function queryDetail(params) {
  return request(
    `${HZERO_PLATFORM}/v1/${isTenant ? `${params.organizationId}/` : ''}code-rule-detail/${
      params.ruleDistId
    }`,
    {
      method: 'GET',
    }
  );
}

/**
 *
 * ruleCodeDist 更新保存数据
 * @async
 * @function addDistValue
 * @param {object} params.data - 待保存数据
 * @param {!string} params.data.meaning - 层级
 * @param {!string} params.data.levelValue - 值
 * @param {?string} params.data.description - 描述
 * @param {!string} params.data.enabledFlag - 是否启用
 * @returns {object} fetch Promise
 */
export async function addDistValue(params) {
  return request(
    `${HZERO_PLATFORM}/v1/${isTenant ? `${params.organizationId}/` : ''}code-rule-dist`,
    {
      method: 'POST',
      body: deleteOrgId(params),
    }
  );
}

/**
 *
 * ruleCodeDetail 保存更新行数据
 * @async
 * @function addCodeDetail
 * @param {object} params.data - 待保存数据
 * @param {!number} params.data.orderSeq - 序号
 * @param {!string} params.data.fieldType - 段类型
 * @param {!string} params.data.fieldValue - 段值
 * @param {!string} params.data.dateMask - 日期掩码
 * @param {!string} params.data.resetFrequency - 重置频率
 * @param {!number} params.data.seqLength - 位数
 * @param {!number} params.data.startValue - 开始值
 * @returns {object} fetch Promise
 */
export async function addCodeDetail(params) {
  return request(
    `${HZERO_PLATFORM}/v1/${isTenant ? `${params.organizationId}/` : ''}code-rule-detail`,
    {
      method: 'POST',
      body: deleteOrgId(params),
    }
  );
}

/**
 *
 * roleCodeDetail 删除详细行数据
 * @async
 * @function deleteCodeDetail
 * @param {object[]} params.selectedRows - 选中批量删除数据
 * @param {!string} params.selectedRows[].ruleDetailId - 编码规则段码id
 * @param {!string} params.selectedRows[].objectVersionNumber - 版本号
 * @returns {object} fetch Promise
 */
export async function deleteCodeDetail(params) {
  return request(
    `${HZERO_PLATFORM}/v1/${isTenant ? `${params.organizationId}/` : ''}code-rule-detail`,
    {
      method: 'DELETE',
      body: deleteOrgIdArr(params),
    }
  );
}

/**
 *
 * 获取块码
 * @async
 * @function queryCode
 * @param {object} params - 查询条件
 * @param {!string} params.lovCode - 块码code
 * @returns {object} fetch Promise
 */
export async function queryCode(params) {
  return request(`${HZERO_PLATFORM}/v1/lovs/value`, {
    method: 'GET',
    query: params,
  });
}
