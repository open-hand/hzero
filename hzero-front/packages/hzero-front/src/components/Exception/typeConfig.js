import intl from 'utils/intl';

import error403 from './imgs/403.svg';
import error404 from './imgs/404.svg';
import error500 from './imgs/500.svg';

const config = {
  403: {
    img: error403,
    title: '403',
    desc() {
      return intl.get('hzero.common.notification.403').d('抱歉，你无权访问该页面');
    },
  },
  404: {
    img: error404,
    title: '404',
    desc() {
      return intl.get('hzero.common.notification.404').d('抱歉，你访问的页面不存在');
    },
  },
  500: {
    img: error500,
    title: '500',
    desc() {
      return intl.get('hzero.common.notification.500').d('抱歉，服务器出错了');
    },
  },
};

export default config;
