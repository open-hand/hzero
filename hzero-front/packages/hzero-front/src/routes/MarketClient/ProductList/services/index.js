import request from 'utils/request';
import { API_PRODUCT_HOST, HMKT_PRODUCT_SERVICE } from 'utils/market-client';

/**
 * 新产品中心查询接口
 * @function query - 查询列表数据
 */
export function fetchProductList(query) {
  return request(`${API_PRODUCT_HOST}${HMKT_PRODUCT_SERVICE}/public/v1/products/search`, {
    method: 'GET',
    query,
  });
}
/**
 * 获取分类数据
 * @function query - 查询列表数据
 */
export function fetchCategoryList(query) {
  return request(`${API_PRODUCT_HOST}${HMKT_PRODUCT_SERVICE}/public/v1/categories/list`, {
    method: 'GET',
    query,
  });
}
