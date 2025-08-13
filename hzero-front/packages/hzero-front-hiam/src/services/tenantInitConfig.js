/**
 * TenantInitHandleConfig - 租户初始化处理配置
 * @date: 2019/6/18
 * @author: hulingfangzi <lingfangzi.hu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import request from 'utils/request';
import { HZERO_IAM } from 'utils/config';
import { parseParameters } from 'utils/utils';

/**
 * 查询租户初始化处理器配置列表
 * @async
 * @function queryConfig
 * @param {*} params - 查询参数
 */
export async function queryConfig(params) {
  const param = parseParameters(params);
  return request(`${HZERO_IAM}/v1/tenant-init-configs/list`, {
    method: 'GET',
    query: { ...param },
  });
}

/**
 * 格式化查询租户初始化处理器配置
 * @async
 * @function queryFormatConfig
 */
export async function queryFormatConfig() {
  return request(`${HZERO_IAM}/v1/tenant-init-configs/map`, {
    method: 'GET',
  });
}
