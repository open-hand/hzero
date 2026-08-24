/**
 * service - 报表平台/报表定义
 * @date: 2018-11-22
 * @version: 1.0.0
 * @author: CJ <juan.chen01@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */

import request from 'utils/request';
import { HZERO_RPT } from 'utils/config';
import { parseParameters, getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';

const organizationId = getCurrentOrganizationId();
const organizationRoleLevel = isTenantRoleLevel();
/**
 * 请求API前缀
 * @type {string}
 */
const prefix = `${HZERO_RPT}/v1`;

/**
 * 数据查询
 * @async
 * @function fetchReportDefList
 * @param {object} params - 查询条件
 * @param {!number} [params.page = 0] - 数据页码
 * @param {!number} [params.size = 10] - 分页大小
 * @returns {object} fetch Promise
 */
export async function fetchReportDefList(params) {
  const param = parseParameters(params);
  return request(
    organizationRoleLevel
      ? `${prefix}/${organizationId}/reports/designer`
      : `${prefix}/reports/designer`,
    {
      method: 'GET',
      query: { ...param },
    }
  );
}
/**
 * 初始化列
 * @async
 * @function getMetaMetaColumns
 * @param {object} params - 查询条件
 * @returns {object} fetch Promise
 */
export async function getMetaMetaColumns(params) {
  return request(
    organizationRoleLevel
      ? `${prefix}/${organizationId}/reports/designer/init-column`
      : `${prefix}/reports/designer/init-column`,
    {
      method: 'GET',
      query: { ...params },
    }
  );
}
/**
 * 新建初始化列信息数据
 * @async
 * @function getInitMetaColumn
 * @returns {object} fetch Promise
 */
export async function getInitMetaColumn() {
  return request(
    organizationRoleLevel
      ? `${prefix}/${organizationId}/reports/designer/metacolumn`
      : `${prefix}/reports/designer/metacolumn`,
    {
      method: 'GET',
    }
  );
}
/**
 * 获取报表定义明细
 * @async
 * @function fetchReportDefinitionDetail
 * @param {object} params - 查询条件
 * @returns {object} fetch Promise
 */
export async function fetchReportDefinitionDetail(params) {
  return request(
    organizationRoleLevel
      ? `${prefix}/${organizationId}/reports/designer/${params.reportId}`
      : `${prefix}/reports/designer/${params.reportId}`,
    {
      method: 'GET',
    }
  );
}
/**
 * 模板数据查询
 * @async
 * @function fetchInitTemplate
 * @param {object} params - 查询条件
 * @param {!number} [params.page = 0] - 数据页码
 * @param {!number} [params.size = 10] - 分页大小
 * @returns {object} fetch Promise
 */
export async function fetchInitTemplate(params) {
  const { tenantId, reportId, templateTypeCode, ...otherParams } = params;
  const param = parseParameters(otherParams);
  return request(
    organizationRoleLevel
      ? `${prefix}/${organizationId}/report-templates`
      : `${prefix}/report-templates`,
    {
      method: 'GET',
      query: { ...param, tenantId, reportId, templateTypeCode },
    }
  );
}
/**
 * 获取模板明细
 * @async
 * @function fetchTemplateDetail
 * @param {object} params - 查询条件
 * @returns {object} fetch Promise
 */
export async function fetchTemplateDetail(params) {
  return request(
    organizationRoleLevel
      ? `${prefix}/${organizationId}/report-templates/${params.reportId}`
      : `${prefix}/report-templates/${params.reportId}`,
    {
      method: 'GET',
    }
  );
}
/**
 * 新增报表定义
 * @async
 * @function createReportDefinition
 * @param {object} params - 请求参数
 * @param {!object} params.dto - 待保存对象
 */
export async function createReportDefinition(params) {
  return request(
    organizationRoleLevel
      ? `${prefix}/${organizationId}/reports/designer`
      : `${prefix}/reports/designer`,
    {
      method: 'POST',
      body: { ...params },
    }
  );
}

/**
 * 复制报表定义
 * @async
 * @function copyReportDefinition
 * @param {object} params - 请求参数
 */
export async function copyReportDefinition(params) {
  return request(`${prefix}/${organizationId}/reports/designer/${params.reportId}/copy`, {
    method: 'POST',
  });
}

/**
 * 更新报表定义
 * @async
 * @function updateReportDefinition
 * @param {object} params - 请求参数
 */
export async function updateReportDefinition(params) {
  return request(
    organizationRoleLevel
      ? `${prefix}/${organizationId}/reports/designer`
      : `${prefix}/reports/designer`,
    {
      method: 'PUT',
      body: { ...params },
    }
  );
}
/**
 * 删除报表定义
 * @async
 * @function deleteReportDefinition
 * @param {object} params - 请求参数
 */
export async function deleteReportDefinition(params) {
  return request(
    organizationRoleLevel
      ? `${prefix}/${organizationId}/reports/designer`
      : `${prefix}/reports/designer`,
    {
      method: 'DELETE',
      body: { ...params },
    }
  );
}

/**
 * 新增模板
 * @async
 * @function createTemplate
 * @param {object} params - 请求参数
 */
export async function createTemplate(params) {
  return request(
    organizationRoleLevel
      ? `${prefix}/${organizationId}/report-templates`
      : `${prefix}/report-templates`,
    {
      method: 'POST',
      body: params.newParams,
    }
  );
}
/**
 * 删除模板
 * @async
 * @function deleteTemplate
 * @param {object} params - 请求参数
 */
export async function deleteTemplate(params) {
  return request(
    organizationRoleLevel
      ? `${prefix}/${organizationId}/report-templates`
      : `${prefix}/report-templates`,
    {
      method: 'DELETE',
      body: params,
    }
  );
}
/**
 * 改变默认模板
 * @async
 * @function changeDefaultTemplate
 * @param {object} params - 请求参数
 */
export async function changeDefaultTemplate(params) {
  return request(
    organizationRoleLevel
      ? `${prefix}/${organizationId}/report-templates`
      : `${prefix}/report-templates`,
    {
      method: 'PUT',
      body: params.newParams,
    }
  );
}

/**
 * 获取已分配的权限
 * @async
 * @function fetchTemplateDetail
 * @param {object} params - 查询条件
 * @returns {object} fetch Promise
 */
export async function fetchAssignedPermission(params) {
  return request(
    organizationRoleLevel
      ? `${prefix}/${organizationId}/report-permissions`
      : `${prefix}/report-permissions`,
    {
      method: 'GET',
      query: params,
    }
  );
}

/**
 * 查询权限详情
 * @async
 * @function fetchPermissionDetail
 * @param {Object} params - 查询参数
 */
export async function fetchPermissionDetail(params) {
  return request(
    organizationRoleLevel
      ? `${HZERO_RPT}/v1/${organizationId}/report-permissions/${params.permissionId}`
      : `${HZERO_RPT}/v1/report-permissions/${params.permissionId}`,
    {
      method: 'GET',
    }
  );
}

/**
 * 分配权限
 * @async
 * @function createPermission
 * @param {String} params - 保存参数
 */
export async function createPermission(params) {
  return request(
    organizationRoleLevel
      ? `${HZERO_RPT}/v1/${organizationId}/report-permissions`
      : `${HZERO_RPT}/v1/report-permissions`,
    {
      method: 'POST',
      body: params,
    }
  );
}

/**
 * 修改权限
 * @async
 * @function updatePermission
 * @param {String} params
 */
export async function updatePermission(params) {
  return request(
    organizationRoleLevel
      ? `${HZERO_RPT}/v1/${organizationId}/report-permissions`
      : `${HZERO_RPT}/v1/report-permissions`,
    {
      method: 'PUT',
      body: params,
    }
  );
}

/**
 * 删除权限
 * @async
 * @function deletePermission
 * @param {object} params - 请求参数
 */
export async function deletePermission(params) {
  return request(
    organizationRoleLevel
      ? `${prefix}/${organizationId}/report-permissions`
      : `${prefix}/report-permissions`,
    {
      method: 'DELETE',
      body: params,
    }
  );
}

/**
 * 获取导出类型
 * @async
 * @function fetchExportType
 * @returns {object} fetch Promise
 */
export async function fetchExportType() {
  return request(`${prefix}/reports/export-type`, {
    method: 'GET',
  });
}
