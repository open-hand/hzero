/**
 * service - 租户初始化处理日志
 * @date: 2019/6/18
 * @author: hulingfangzi <lingfangzi.hu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import request from 'utils/request';
import { HZERO_IAM } from 'utils/config';
import { parseParameters } from 'utils/utils';

/**
 * 查询租户初始化处理日志列表
 * @async
 * @function queryLog
 * @param {object} params - 查询参数
 */
export async function queryLog(params) {
  const param = parseParameters(params);
  return request(`${HZERO_IAM}/v1/tenant-init-logs`, {
    method: 'GET',
    query: { ...param },
  });
}

/**
 * 查询租户初始化处理日志图形数据
 * @async
 * @function queryLogPic
 * @param {string} instanceKey - 实例键值
 */
export async function queryLogPic(instanceKey) {
  return request(`${HZERO_IAM}/v1/tenant-init-logs`, {
    method: 'GET',
    query: { instanceKey, page: 0, size: 999 },
  });
}
