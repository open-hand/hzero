/**
 * 公式组件
 * @Author: wangke <ke.wang05@hand-china.com>
 * @Date: 2019-10-15
 * @LastEditTime: 2019-10-15 16:45
 * @Copyright: Copyright (c) 2018, Hand
 */
import request from 'utils/request';
import { HZERO_HRES } from 'utils/config';
import { getCurrentOrganizationId } from 'utils/utils';

/**
 * 变量选择菜单查询
 * @param ruleCode
 * @returns {Promise<void>}
 */
export async function queryTreeData(ruleCode) {
  return request(
    `${HZERO_HRES}/v1/${getCurrentOrganizationId()}/formula-component/query-variable?ruleCode=${ruleCode}`,
    {
      method: 'GET',
    }
  );
}

/**
 * 校验公式是否合法
 * @param param
 * @returns {Promise<void>}
 */
export async function validateFormula(param) {
  return request(`${HZERO_HRES}/v1/${getCurrentOrganizationId()}/rule/validate-formula`, {
    method: 'POST',
    query: { ...param },
  });
}
