/**
 * OCRInspectionService OCR识别发票查验
 * @date: 2019-8-25
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */

import request from 'utils/request';
import { HZERO_INVOICE } from 'utils/config';
import { getCurrentOrganizationId } from 'utils/utils';

const organizationId = getCurrentOrganizationId();
/**
 * 创建配置
 * @async
 * @function createConfig
 * @param {Object} params - 查询参数
 */
export async function create(params) {
  return request(`${HZERO_INVOICE}/v1/${organizationId}/invoice/ocr/check`, {
    method: 'POST',
    body: params,
  });
}
