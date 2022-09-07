/**
 * service 导入历史
 * @since 2019-10-10
 * @author  WT <tao13.wang@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import request from 'utils/request';
import { HZERO_IMP } from 'utils/config';
import { getCurrentOrganizationId } from 'utils/utils';

const organizationId = getCurrentOrganizationId();

/**
 * 模板头数据查询接口
 * @async
 * @function loadTemplate
 * @param {Object} params - 查询参数
 * @param {String} params.code - 模板编码
 */
export async function loadTemplate(params) {
  const { prefixPatch, code } = params;
  const reqUrl = prefixPatch
    ? `${prefixPatch}/v1/${organizationId}/import/template/${code}/info`
    : `${HZERO_IMP}/v1/${organizationId}/template/${code}/info`;
  return request(reqUrl, {
    method: 'GET',
  });
}
