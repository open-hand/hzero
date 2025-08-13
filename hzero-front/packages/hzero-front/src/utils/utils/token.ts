/**
 * token相关
 * @date: 2019-12-25
 * @author: wjc <jiacheng.wang@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import qs from 'query-string';
import Cookies, { CookieGetOptions } from 'universal-cookie';
import { getConfig } from 'hzero-boot';

export const ACCESS_TOKEN = 'access_token';
export const REFRESH_TOKEN = 'refresh_token';
const cookies = new Cookies();

export function getAccessToken() {
  const privateRouter = window.location.pathname.startsWith('/private');
  const privateToken = qs.parse(window.location.search) || {};
  return privateRouter
    ? privateToken.access_token
    : cookies.get(ACCESS_TOKEN, {
        path: '/',
      } as CookieGetOptions);
}

export function setAccessToken(token) {
  // @ts-ignore
  const patchTokenConfig = getConfig('patchToken');
  let patchToken;
  if (patchTokenConfig) {
    if (typeof patchTokenConfig === 'function') {
      // @ts-ignore
      patchToken = patchTokenConfig();
    } else {
      patchToken = patchTokenConfig;
    }
  }
  cookies.set(ACCESS_TOKEN, token, {
    path: '/',
    ...patchToken,
  });
}

export function removeAccessToken() {
  cookies.remove(ACCESS_TOKEN, {
    path: '/',
  });
}

export function getRefreshToken() {
  const privateRouter = window.location.pathname.startsWith('/private');
  const privateToken = qs.parse(window.location.search) || {};
  return privateRouter
    ? privateToken.refresh_token
    : cookies.get(REFRESH_TOKEN, {
        path: '/',
      } as CookieGetOptions);
}

export function setRefreshToken(token) {
  cookies.set(REFRESH_TOKEN, token, {
    path: '/',
  });
}

export function removeRefreshToken() {
  cookies.remove(REFRESH_TOKEN, {
    path: '/',
  });
}

/**
 * 抽取AccessToken
 * @param {String} hash   hash值
 */
export function extractAccessTokenFromHash(hash) {
  if (hash) {
    const ai = hash.indexOf(ACCESS_TOKEN);
    if (ai !== -1) {
      const accessTokenReg = /#?access_token=[0-9a-zA-Z-]*/g; // todo 确定 确定的 access_token 头, 现在看起来时 /#access_token
      hash.match(accessTokenReg);
      const centerReg = hash.match(accessTokenReg)[0];
      const accessToken = centerReg.split('=')[1];
      return accessToken;
    }
  }
  return null;
}

/**
 * 抽取RefreshToken
 * @param {String} hash   hash值
 */
export function extractRefreshTokenFromHash(hash) {
  if (hash) {
    const ai = hash.indexOf(REFRESH_TOKEN);
    if (ai !== -1) {
      const refreshTokenReg = /#?refresh_token=[0-9a-zA-Z-]*/g; // todo 确定 确定的 access_token 头, 现在看起来时 /#access_token
      hash.match(refreshTokenReg);
      const centerReg = hash.match(refreshTokenReg)[0];
      const refreshToken = centerReg.split('=')[1];
      return refreshToken;
    }
  }
  return null;
}

/**
 * 抽取ErrorMessage
 * @param {String} search   search值
 */
export function extractErrorMessageFromSearch(search) {
  if (search) {
    const { errorMessage } = qs.parse(search.substring(1));
    return errorMessage;
  }
  return null;
}
