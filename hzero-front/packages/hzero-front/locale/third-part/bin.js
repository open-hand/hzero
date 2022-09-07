const { transformUILocale } = require('./utils');

function resolveRequireLocale(moduleName) {
  // eslint-disable-next-line import/no-dynamic-require
  const m = require(moduleName);
  if (m.__esModule) {
    // delete m.default.locale;
    return m.default;
  } else {
    // delete m.locale;
    return m;
  }
}

transformUILocale(resolveRequireLocale('hzero-ui/lib/locale-provider/zh_CN'), 'hzero-ui', 'hzeroUI');
transformUILocale(resolveRequireLocale('choerodon-ui/lib/locale-provider/zh_CN'), 'c7n-ui', 'c7nUI');
transformUILocale(resolveRequireLocale('choerodon-ui/pro/lib/locale-context/zh_CN'), 'c7n-pro-ui', 'c7nProUI');
