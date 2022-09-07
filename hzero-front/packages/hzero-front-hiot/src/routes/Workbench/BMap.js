import { getEnvConfig } from 'utils/iocUtils';

const { BAIDU_MAP_AK } = getEnvConfig();
export function Map() {
  return new Promise(function (resolve, reject) {
    // if (typeof BMap !== 'undefined') {
    //   resolve();
    //   return true;
    // }
    window.onBMapCallback = function () {
      resolve();
    };
    const script = document.createElement('script');
    script.type = 'text/javascript';
    script.src = `http://api.map.baidu.com/api?v=2.0&ak=${BAIDU_MAP_AK}&callback=onBMapCallback`;
    script.onerror = reject;
    document.head.appendChild(script);
  });
}
