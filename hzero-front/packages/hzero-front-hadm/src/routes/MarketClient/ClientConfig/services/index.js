import request from 'utils/request';
import { HZERO_ADM } from 'utils/config';

/**
 * 获取市场配置
 * @function query - 查询列表数据
 */
export function queryMarketConfig(query) {
  return request(`${HZERO_ADM}/v1/market/config`, {
    method: 'GET',
    query,
  });
}
/**
 * 保存市场配置
 */
export function saveMarketConfig(data) {
  return request(`${HZERO_ADM}/v1/market/config`, {
    method: 'POST',
    body: data,
  });
}
