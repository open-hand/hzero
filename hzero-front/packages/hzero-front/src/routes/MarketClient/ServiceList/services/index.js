import request from 'utils/request';
import fetch from 'dva/fetch';
import { getAccessToken, getRequestId } from 'utils/utils';
import { API_HOST, HZERO_ADM } from 'utils/config';

/**
 * 新产品中心查询接口
 * @function query - 查询列表数据
 */
export function fetchServiceList(query) {
  return request(`${HZERO_ADM}/v1/market/server/services`, {
    method: 'GET',
    query,
  });
}

/**
 * 查询 Hzero 版本
 * @function query - 查询列表数据
 */
export function queryHzeroVersion(query) {
  return request(`${HZERO_ADM}/v1/market/server/services/hzero-version`, {
    method: 'GET',
    query,
  });
}

/**
 * 查询 可对比版本
 * @function query - 查询列表数据
 */
export function queryServiceVersionList(query) {
  return request(`${HZERO_ADM}/v1/market/server/libs/dependency-versions`, {
    method: 'GET',
    query,
  });
}

/**
 * 查询 Hzero 服务详情
 * @function query - 查询列表数据
 */
export function queryServiceDetail(serviceId, query) {
  return request(`${HZERO_ADM}/v1/market/server/services/${serviceId}`, {
    method: 'GET',
    query,
  });
}

/**
 * 服务 对比结果
 * @function query - 查询列表数据
 */
export function queryServiceCompareResult(serviceId, query) {
  return request(`${HZERO_ADM}/v1/market/server/services/${serviceId}/libs/diff`, {
    method: 'GET',
    query,
  });
}

/**
 * 接口api 对比结果
 * @function query - 查询列表数据
 */
export function queryApiCompareResult(serviceId, query) {
  return request(`${HZERO_ADM}/v1/market/swagger/diff/${serviceId}`, {
    method: 'GET',
    query,
  });
}

/**
 * 配置对比结果
 * @function query - 查询列表数据
 */
export function queryConfigCompareResult(serviceId, query) {
  return request(`${HZERO_ADM}/v1/market/server/services/${serviceId}/config/diff`, {
    method: 'GET',
    query,
  });
}

/**
 * 保存市场配置
 * @function query - 查询列表数据
 */
export function testConnect(query) {
  return request(`${HZERO_ADM}/v1/market/config/reachable`, {
    method: 'GET',
    query,
  });
}

/**
 * 市场用户客户端登录
 */
export function marketUserLogin(data, cb) {
  const checkStatus = (response) => {
    if (response.status === 204) {
      return {};
    }
    if (response.status >= 200 && response.status < 300) {
      if (response.responseType === 'blob') {
        return response.blob();
      }
      try {
        return response.responseType === 'text' ? response.text() : response.json();
      } catch (e) {
        // console.log(e);
      }
    }
    return {};
  };
  fetch(`${API_HOST}${HZERO_ADM}/v1/market/login`, {
    body: data,
    method: 'POST',
    headers: {
      credentials: 'include',
      Authorization: `bearer ${getAccessToken()}`,
      'H-Request-Id': `${getRequestId()}`,
      'Content-type': 'application/x-www-form-urlencoded',
    },
  })
    .then(checkStatus)
    .then((res) => {
      if (typeof cb === 'function') cb(res);
    });
}

export function getCaptchaKey() {
  return request(`${HZERO_ADM}/v1/market/captcha-code`, {
    method: 'GET',
  });
}

/**
 * 刷新Redis缓存
 */
export function refreshCache() {
  return request(`${HZERO_ADM}/v1/market/server/extra-data/refresh`, {
    method: 'GET',
  });
}
