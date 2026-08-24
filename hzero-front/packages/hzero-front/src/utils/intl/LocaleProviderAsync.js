import React, { Component } from 'react';
import { ConfigProvider } from 'hzero-ui';
import { LocaleProvider as C7NLocaleProvider } from 'choerodon-ui';
import { localeContext } from 'choerodon-ui/pro';
import { connect } from 'dva';

import { getEnvConfig } from 'utils/iocUtils';
import { resolveRequire } from 'utils/utils';

const { configureParams } = getEnvConfig();

@connect(({ global = {} }) => ({
  language: global.language,
  hzeroUILocale: global.hzeroUILocale,
  c7nLocale: global.c7nLocale,
  supportLanguage: global.supportLanguage,
}))
export default class LocalProviderAsync extends Component {
  componentDidUpdate(prevProps) {
    const { supportLanguage, language } = this.props;
    if (prevProps.supportLanguage !== supportLanguage) {
      const c7nLocaleContext = {};
      if (supportLanguage) {
        supportLanguage.forEach((intlRecord) => {
          c7nLocaleContext[intlRecord.code] = intlRecord.name;
        });
      }
      // 更新 c7n 的 locale supports
      localeContext.setSupports(c7nLocaleContext);
    }

    if (prevProps.language !== language && language) {
      // c7n localeContext already update in models/global; so not need updatet there
      // localeContext.setLocale(language); // this is error, locale should be a deep object with intl;
    }
  }

  render() {
    const { hzeroUILocale, c7nLocale } = this.props;
    const config = {
      modalMovable: configureParams?.modalMovable,
      modalAutoCenter: configureParams?.modalAutoCenter,
    };
    return (
      <C7NLocaleProvider locale={resolveRequire(c7nLocale)}>
        <ConfigProvider {...config} {...this.props} locale={resolveRequire(hzeroUILocale)} />
      </C7NLocaleProvider>
    );
  }
}
