import request from 'utils/request';
import { HZERO_RPT } from 'utils/config';
import { parseParameters, getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';

const organizationId = getCurrentOrganizationId();
const organizationRoleLevel = isTenantRoleLevel();

/**
 * 模板管理数据
 * @async
 * @function fetchTemplateManageList
 * @param {object} params - 查询条件
 * @param {!number} [params.page = 0] - 数据页码
 * @param {!number} [params.size = 10] - 分页大小
 * @returns {object} fetch Promise
 */
export async function fetchTemplateManageList(params) {
  const param = parseParameters(params);
  return request(
    organizationRoleLevel
      ? `${HZERO_RPT}/v1/${organizationId}/templates`
      : `${HZERO_RPT}/v1/templates`,
    {
      method: 'GET',
      query: { ...param },
    }
  );
}
/**
 * 获取模板管理头详情
 * @async
 * @function fetchTemplateHeaderDetail
 * @param {object} params - 查询条件
 * @returns {object} fetch Promise
 */
export async function fetchTemplateHeaderDetail(params) {
  return request(
    organizationRoleLevel
      ? `${HZERO_RPT}/v1/${organizationId}/templates/${params.templateId}`
      : `${HZERO_RPT}/v1/templates/${params.templateId}`,
    {
      method: 'GET',
    }
  );
}
/**
 * 获取模板管理行
 * @async
 * @function fetchTemplateLine
 * @param {object} params - 查询条件
 * @returns {object} fetch Promise
 */
export async function fetchTemplateLine(params) {
  const { templateId, ...otherParams } = params;
  const param = parseParameters(otherParams);
  return request(
    organizationRoleLevel
      ? `${HZERO_RPT}/v1/${organizationId}/template-dtls/${templateId}`
      : `${HZERO_RPT}/v1/template-dtls/${templateId}`,
    {
      method: 'GET',
      query: { ...param },
    }
  );
}
/**
 * 获取模板管理行详情
 * @async
 * @function fetchTemplateLineDetail
 * @param {object} params - 查询条件
 * @returns {object} fetch Promise
 */
export async function fetchTemplateLineDetail(params) {
  return request(
    organizationRoleLevel
      ? `${HZERO_RPT}/v1/${organizationId}/template-dtls/detail/${params.templateDtlId}`
      : `${HZERO_RPT}/v1/template-dtls/detail/${params.templateDtlId}`,
    {
      method: 'GET',
    }
  );
}
/**
 * 添加模板管理
 * @async
 * @function createTemplateManage
 * @param {object} params - 请求参数
 * @returns {object} fetch Promise
 */
export async function createTemplateManage(params) {
  return request(
    organizationRoleLevel
      ? `${HZERO_RPT}/v1/${organizationId}/templates`
      : `${HZERO_RPT}/v1/templates`,
    {
      method: 'POST',
      body: params,
    }
  );
}

/**
 * 编辑模板管理
 * @async
 * @function editTemplateManage
 * @param {object} params - 请求参数
 * @returns {object} fetch Promise
 */
export async function editTemplateManage(params) {
  return request(
    organizationRoleLevel
      ? `${HZERO_RPT}/v1/${organizationId}/templates`
      : `${HZERO_RPT}/v1/templates`,
    {
      method: 'PUT',
      body: params,
    }
  );
}

/**
 * 删除模板管理
 * @async
 * @function deleteTemplateManage
 * @param {object} params - 请求参数
 * @returns {object} fetch Promise
 */
export async function deleteTemplateManage(params) {
  return request(
    organizationRoleLevel
      ? `${HZERO_RPT}/v1/${organizationId}/templates`
      : `${HZERO_RPT}/v1/templates`,
    {
      method: 'DELETE',
      body: params,
    }
  );
}
/**
 * 添加模板管理行
 * @async
 * @function createTemplateLine
 * @param {object} params - 请求参数
 * @returns {object} fetch Promise
 */
export async function createTemplateLine(params) {
  return request(
    organizationRoleLevel
      ? `${HZERO_RPT}/v1/${organizationId}/template-dtls`
      : `${HZERO_RPT}/v1/template-dtls`,
    {
      method: 'POST',
      body: params,
    }
  );
}

/**
 * 编辑模板管理
 * @async
 * @function editTemplateLine
 * @param {object} params - 请求参数
 * @returns {object} fetch Promise
 */
export async function editTemplateLine(params) {
  return request(
    organizationRoleLevel
      ? `${HZERO_RPT}/v1/${organizationId}/template-dtls`
      : `${HZERO_RPT}/v1/template-dtls`,
    {
      method: 'PUT',
      body: params,
    }
  );
}
/**
 * 删除模板管理
 * @async
 * @function deleteTemplateLine
 * @param {object} params - 请求参数
 * @returns {object} fetch Promise
 */
export async function deleteTemplateLine(params) {
  return request(
    organizationRoleLevel
      ? `${HZERO_RPT}/v1/${organizationId}/template-dtls`
      : `${HZERO_RPT}/v1/template-dtls`,
    {
      method: 'DELETE',
      body: params.newParameters,
    }
  );
}
