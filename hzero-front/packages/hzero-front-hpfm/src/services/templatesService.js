/**
 * 系统管理--模板维护
 * @date 2019-6-26
 * @author: XL <liang.xiong@hand-china.com>
 */
import request from 'utils/request';
import { HZERO_PLATFORM } from 'utils/config';
import { parseParameters, isTenantRoleLevel, getCurrentOrganizationId } from 'utils/utils';

const tenantId = getCurrentOrganizationId();

function templatesApi() {
  return isTenantRoleLevel() ? `${tenantId}/content-templates` : 'content-templates';
}

/**
 * 数据查询
 * @async
 * @function fetchTemplates
 * @param {object} params - 查询条件
 * @param {?string} params.templateName - 模板名称
 * @param {!number} [params.page = 0] - 数据页码
 * @param {!number} [params.size = 10] - 分页大小
 * @returns {object} fetch Promise
 */
export async function fetchTemplates(params) {
  const param = parseParameters(params);
  return request(`${HZERO_PLATFORM}/v1/${templatesApi()}`, {
    method: 'GET',
    query: { ...param },
  });
}

/**
 * 添加模板信息
 * @async
 * @function createTemplate
 * @param {object} params - 请求参数
 * @param {?string} params.templateCode - 模板编码
 * @param {?string} params.templateName - 模板名称
 * @param {?string} params.templateAvatar - 模板缩略图
 * @param {?string} params.templateCode - 流程分类描述
 * @param {?string} params.templatePath - 模板路径
 * @param {?string} params.enable - 启用
 * @returns {object} fetch Promise
 */
export async function createTemplate(params) {
  return request(`${HZERO_PLATFORM}/v1/${templatesApi()}`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 编辑模板
 * @async
 * @function editTemplate
 * @param {object} params - 请求参数
 * @param {?string} params.templateName - 模板名称
 * @param {?string} params.templateAvatar - 模板缩略图
 * @param {?string} params.templateCode - 流程分类描述
 * @param {?string} params.templatePath - 模板路径
 * @param {?string} params.enable - 启用
 * @param {!string} params.objectVersionNumber - 版本号
 * @returns {object} fetch Promise
 */
export async function editTemplate(params) {
  return request(`${HZERO_PLATFORM}/v1/${templatesApi()}`, {
    method: 'PUT',
    body: params,
  });
}
