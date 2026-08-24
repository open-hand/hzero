/**
 * urk相关
 * @date: 2019-12-25
 * @author: wjc <jiacheng.wang@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import qs from 'query-string';

export function isUrl(path) {
  /* eslint no-useless-escape:0 */
  const reg = /(((^https?:(?:\/\/)?)(?:[-;:&=\+\$,\w]+@)?[A-Za-z0-9.-]+|(?:www.|[-;:&=\+\$,\w]+@)[A-Za-z0-9.-]+)(:[\d]+)?((?:\/[\+~%\/.\w-_]*)?\??(?:[-\+=&;%@.\w_]*)#?(?:[\w]*))?)$/g;
  return reg.test(path);
}

/**
 * 生成带Get参数的URL
 * @param {String} url      原来的url
 * @param {Object} params   get 参数
 */
export function generateUrlWithGetParam(url, params) {
  let newUrl = url;
  if (params && Object.keys(params).length >= 1) {
    const newParams = params; // filterNullValueObject
    if (Object.keys(newParams).length >= 1) {
      newUrl += `${url.indexOf('?') >= 0 ? '&' : '?'}${qs.stringify(newParams)}`;
    }
  }
  return newUrl;
}

/**
 * 得到get请求后面的参数部分,并去掉参数值为空的
 * @param param
 * @returns {String}
 */
export function getUrlParam(param) {
  let on = true;
  let result = '';
  for (const item in param) {
    if (on) {
      on = false;
      if (param[item] || param[item] === 0 || param[item] === false) {
        result = `?${item}=${encodeURIComponent(param[item])}`;
      } else {
        result = '?';
      }
    } else if (param[item] || param[item] === 0 || param[item] === false) {
      result = `${result}&${item}=${encodeURIComponent(param[item])}`;
    }
  }
  return result;
}
