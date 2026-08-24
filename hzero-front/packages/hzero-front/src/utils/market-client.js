// 市场客户端配置
import { API_HOST } from 'utils/config';
import { getAccessToken, getRequestId } from 'utils/utils';

/**
 * 业务原因：产品部分 和 服务部分 请求的接口是不一样的
 * 产品列表 API 请求地址
 */
export const API_PRODUCT_HOST = 'https://gateway.open.hand-china.com';
/**
 * 产品-请求服务前缀
 */
export const HMKT_PRODUCT_SERVICE = '/service';
/**
 * 产品详情跳转地址
 */
export const CLIENT_JUMP_URL = 'https://open.hand-china.com';

/**
 * 客户端请求静默失败的接口
 * @param query
 */
export function clientRequest(url, _data) {
  const { apiHost = API_HOST, method = 'GET', body, ...rest } = _data;
  return new Promise((resolve, reject) => {
    const checkStatus = (response) => {
      if (response.status >= 200 && response.status < 300) {
        return response.json();
      } else {
        reject();
      }
    };
    fetch(`${apiHost}${url}`, {
      headers: {
        Authorization: `bearer ${getAccessToken()}`,
        'H-Request-Id': `${getRequestId()}`,
      },
      ...rest,
      method,
      body: JSON.stringify(body),
    })
      .then(checkStatus)
      .then((res) => {
        resolve(res);
      });
  });
}
