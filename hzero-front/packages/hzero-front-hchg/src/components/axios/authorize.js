import { removeAllCookie } from 'utils/utils';
import { LOGIN_URL } from 'utils/config';

export default function authorize() {
  removeAllCookie();
  const cacheLocation = encodeURIComponent(window.location.toString()); // 由于 LOGIN_URL 可以 配置, 所以 做一次判断

  if (LOGIN_URL.includes('?')) {
    window.location.href = ''.concat(LOGIN_URL, '&redirect_uri=').concat(cacheLocation); // 401 需要在登录后返回401的页面
  } else {
    window.location.href = ''.concat(LOGIN_URL, '?redirect_uri=').concat(cacheLocation); // 401 需要在登录后返回401的页面
  }
}
