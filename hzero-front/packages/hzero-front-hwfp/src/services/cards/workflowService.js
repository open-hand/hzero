/**
 * workflowService.js - 工作台卡片 service
 * @date: 2019-08-26
 * @author: WangTao
 * @copyright: Copyright (c) 2019, Hand
 */

import request from 'utils/request';
import { getCurrentOrganizationId } from 'utils/utils';
import { HZERO_HWFP } from 'utils/config';

const organizationId = getCurrentOrganizationId();

/**
 * 工作流查询
 * @param {Object} params 查询参数
 * @export
 * @returns
 */
export async function queryWorkflow(params) {
  return request(
    `${HZERO_HWFP}/v1/${organizationId}/activiti/task/query?ignoreEmployeeNotFound=true`,
    {
      method: 'POST',
      body: params,
    }
  );
}
