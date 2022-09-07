import request from 'utils/request';
import { HZERO_PLATFORM } from 'utils/config';
import { parseParameters, getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';

const organizationId = getCurrentOrganizationId();
const urlPrefix = `${HZERO_PLATFORM}/v1${isTenantRoleLevel() ? `/${organizationId}/` : `/`}`;

/**
 * 查询规则引擎表数据
 * @async
 * @function fetchTableList
 * @param {object} params - 查询条件
 * @param {!number} [params.page = 0] - 数据页码
 * @param {!number} [params.size = 10] - 分页大小
 * @returns {object} fetch Promise
 */
export async function fetchTableList(params) {
  const param = parseParameters(params);
  return request(`${urlPrefix}rule-scripts`, {
    method: 'GET',
    query: { ...param },
  });
}
/**
 * 查询规则引擎明细
 * @async
 * @function searchDetail
 * @param {object} params - 查询条件
 * @param {?string} params.ruleScriptId - 规则引擎Id
 * @returns {object} fetch Promise
 */

export async function searchDetail(params) {
  return request(`${urlPrefix}rule-scripts/${params.ruleScriptId}`, {
    method: 'GET',
  });
}
/**
 * 添加规则引擎
 * @async
 * @function createRuleEngine
 * @param {object} params - 请求参数
 * @param {!object} params.tenantId - 租户
 * @param {!string} params.serverName - 服务名称
 * @param {?number} params.scriptCode - 脚本编码
 * @param {!object} params.tenantId - 租户
 * @param {!string} params.scriptDescription - 描述
 * @param {?number} params.scriptTypeCode - 类型
 * @param {!string} params.enabledFlag - 启用标记
 * @param {?number} params.scriptContent - 脚本内容
 * @returns {object} fetch Promise
 */
export async function createRuleEngine(params) {
  return request(`${urlPrefix}rule-scripts`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 编辑规则引擎
 * @async
 * @function editRuleEngine
 * @param {object} params - 请求参数
 * @param {!object} params.tenantId - 租户
 * @param {!string} params.serverName - 服务名称
 * @param {?number} params.scriptCode - 脚本编码
 * @param {!object} params.tenantId - 租户
 * @param {!string} params.scriptDescription - 描述
 * @param {?number} params.scriptTypeCode - 类型
 * @param {!string} params.enabledFlag - 启用标记
 * @param {?number} params.scriptContent - 脚本内容
 * @param {!string} params.ruleScriptId - ruleScriptId
 * @param {!string} params.objectVersionNumber - 版本号
 * @returns {object} fetch Promise
 */
export async function editRuleEngine(params) {
  return request(`${urlPrefix}rule-scripts`, {
    method: 'PUT',
    body: params,
  });
}
/**
 * 删除规则引擎
 * @async
 * @function deleteRuleEngine
 * @param {object} params - 规则引擎
 * @returns {object} fetch Promise
 */
export async function deleteRuleEngine(params) {
  return request(`${urlPrefix}rule-scripts`, {
    method: 'DELETE',
    body: params,
  });
}
/**
 * 测试规则引擎
 * @param {object} params - 请求参数
 */
export async function testRuleEngine(params) {
  return request(`${urlPrefix}rule-scripts/test`, {
    method: 'POST',
    body: { ...params },
  });
}
