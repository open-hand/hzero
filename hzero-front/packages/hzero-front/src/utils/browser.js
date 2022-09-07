/**
 * 用来判断浏览器的版本等
 */

/**
 * 高程3的代码
 * 好像有点过时 ie 的 11 和 Edge
 */
export function client() {
  // 检查浏览器
  // rendering engines
  const engine = {
    ie: 0,
    gecko: 0,
    webkit: 0,
    khtml: 0,
    opera: 0,

    // complete version
    ver: null,
  };

  // browsers
  const browser = {
    // browsers
    ie: 0,
    firefox: 0,
    safari: 0,
    konq: 0,
    opera: 0,
    chrome: 0,

    // specific version
    ver: null,
  };

  // platform/device/OS
  const system = {
    win: false,
    mac: false,
    x11: false,

    // mobile devices
    iphone: false,
    ipod: false,
    ipad: false,
    ios: false,
    android: false,
    nokiaN: false,
    winMobile: false,

    // game systems
    wii: false,
    ps: false,
  };

  // detect rendering engines/browsers
  const ua = navigator.userAgent;
  if (window.opera) {
    browser.ver = window.opera.version();
    engine.ver = browser.ver;
    browser.opera = parseFloat(engine.ver);
    engine.opera = browser.opera;
  } else if (/AppleWebKit\/(\S+)/.test(ua)) {
    engine.ver = RegExp.$1;
    engine.webkit = parseFloat(engine.ver);

    // figure out if it's Chrome or Safari
    if (/Chrome\/(\S+)/.test(ua)) {
      browser.ver = RegExp.$1;
      browser.chrome = parseFloat(browser.ver);
    } else if (/Version\/(\S+)/.test(ua)) {
      browser.ver = RegExp.$1;
      browser.safari = parseFloat(browser.ver);
    } else {
      // approximate version
      let safariVersion = 1;
      if (engine.webkit < 100) {
        safariVersion = 1;
      } else if (engine.webkit < 312) {
        safariVersion = 1.2;
      } else if (engine.webkit < 412) {
        safariVersion = 1.3;
      } else {
        safariVersion = 2;
      }

      browser.ver = safariVersion;
      browser.safari = browser.ver;
    }
  } else if (/KHTML\/(\S+)/.test(ua) || /Konqueror\/([^;]+)/.test(ua)) {
    browser.ver = RegExp.$1;
    engine.ver = browser.ver;
    browser.konq = parseFloat(engine.ver);
    engine.khtml = browser.konq;
  } else if (/rv:([^)]+)\) Gecko\/\d{8}/.test(ua)) {
    engine.ver = RegExp.$1;
    engine.gecko = parseFloat(engine.ver);

    // determine if it's Firefox
    if (/Firefox\/(\S+)/.test(ua)) {
      browser.ver = RegExp.$1;
      browser.firefox = parseFloat(browser.ver);
    }
  } else if (/MSIE ([^;]+)/.test(ua)) {
    browser.ver = RegExp.$1;
    engine.ver = browser.ver;
    browser.ie = parseFloat(engine.ver);
    engine.ie = browser.ie;
  }

  // detect browsers
  browser.ie = engine.ie;
  browser.opera = engine.opera;

  // detect platform
  const p = navigator.platform;
  system.win = p.indexOf('Win') === 0;
  system.mac = p.indexOf('Mac') === 0;
  system.x11 = p === 'X11' || p.indexOf('Linux') === 0;

  // detect windows operating systems
  if (system.win) {
    if (/Win(?:dows )?([^do]{2})\s?(\d+\.\d+)?/.test(ua)) {
      if (RegExp.$1 === 'NT') {
        switch (RegExp.$2) {
          case '5.0':
            system.win = '2000';
            break;
          case '5.1':
            system.win = 'XP';
            break;
          case '6.0':
            system.win = 'Vista';
            break;
          case '6.1':
            system.win = '7';
            break;
          default:
            system.win = 'NT';
            break;
        }
      } else if (RegExp.$1 === '9x') {
        system.win = 'ME';
      } else {
        system.win = RegExp.$1;
      }
    }
  }

  // mobile devices
  system.iphone = ua.indexOf('iPhone') > -1;
  system.ipod = ua.indexOf('iPod') > -1;
  system.ipad = ua.indexOf('iPad') > -1;
  system.nokiaN = ua.indexOf('NokiaN') > -1;

  // windows mobile
  if (system.win === 'CE') {
    system.winMobile = system.win;
  } else if (system.win === 'Ph') {
    if (/Windows Phone OS (\d+.\d+)/.test(ua)) {
      system.win = 'Phone';
      system.winMobile = parseFloat(RegExp.$1);
    }
  }

  // determine iOS version
  if (system.mac && ua.indexOf('Mobile') > -1) {
    if (/CPU (?:iPhone )?OS (\d+_\d+)/.test(ua)) {
      system.ios = parseFloat(RegExp.$1.replace('_', '.'));
    } else {
      system.ios = 2; // can't really detect - so guess
    }
  }

  // determine Android version
  if (/Android (\d+\.\d+)/.test(ua)) {
    system.android = parseFloat(RegExp.$1);
  }

  // gaming systems
  system.wii = ua.indexOf('Wii') > -1;
  system.ps = /playstation/i.test(ua);

  // return it
  return {
    engine,
    browser,
    system,
  };
}

/**
 * 只需要判断是不是 ie 11 以下 所以不处理其他的浏览器
 * 获取ie的版本信息 如果返回 -1 则不是ie浏览器
 */
export function getIeVersion() {
  const { userAgent } = navigator; // 取得浏览器的userAgent字符串
  if (userAgent.match(/rv:([\d.]+)\) like Gecko/)) {
    return 11;
  } else if (userAgent.match(/MSIE ([\d.]+)/)) {
    return userAgent.match(/MSIE ([\d.]+)/)[1];
  } else {
    return -1;
  }
}
