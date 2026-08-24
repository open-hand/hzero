import React from 'react';
import { connect } from 'dva';
import { isArray, isString } from 'lodash';

import { getCurrentOrganizationId } from '../utils';
import intl from '.';

import { queryPromptLocale } from '../../services/api';

const cache = window.intlCache || (window.intlCache = new Map());

function getDisplayName(WrappedComponent) {
  return WrappedComponent.displayName || WrappedComponent.name || 'Component';
}

export default function formatterCollections({ code = '' } = {}) {
  return (Component) => {
    const intlLoadPromise = (orgId, lang, promptKey) => queryPromptLocale(orgId, lang, promptKey);

    class IntlComponent extends React.Component {
      static displayName = `IntlComponent(${getDisplayName(Component)})`;

      state = {
        localeLoaded: false,
      };

      loading = false;

      currentLanguage = null;

      async loadLocale(language) {
        if (language && this.currentLanguage !== language) {
          // 必须要 language 有值
          this.currentLanguage = language;
          let currentCacheLang;
          if (cache.has(language)) {
            currentCacheLang = cache.get(language);
          } else {
            currentCacheLang = new Map();
            cache.set(language, currentCacheLang);
          }
          let promptKey = '';
          const multipleCode = [];
          if (isString(code)) {
            if (!currentCacheLang.get(code)) {
              promptKey = code;
              currentCacheLang.set(code, true);
            }
          } else if (isArray(code)) {
            code.forEach((c) => {
              if (!currentCacheLang.get(c)) {
                currentCacheLang.set(c, true);
                multipleCode.push(c);
              }
            });
            promptKey = multipleCode.join(',');
          }
          if (promptKey) {
            this.loading = true;
            try {
              const organizationId = getCurrentOrganizationId() || 0;
              const data = await intlLoadPromise(organizationId, language, promptKey);
              if (data) {
                // if (isArray(code)) {
                //   code.forEach(c => {
                //     currentCacheLang.set(c, data);
                //   });
                // } else {
                //   currentCacheLang.set(code, data);
                // }
                intl.load({
                  [language]: data,
                });
                return;
              }
              // 语言没有加载成功
              if (isArray(code)) {
                code.forEach((c) => {
                  currentCacheLang.delete(c);
                });
              } else {
                currentCacheLang.delete(code);
              }
            } finally {
              this.loading = false;
              this.setState({
                localeLoaded: true,
              });
            }
          } else {
            this.setState({
              localeLoaded: true,
            });
          }
        }
      }

      componentDidMount() {
        const { language } = this.props;
        this.loadLocale(language);
      }

      componentDidUpdate() {
        this.loadLocale(this.props.language);
      }

      shouldComponentUpdate() {
        return !this.loading;
      }

      render() {
        const { localeLoaded } = this.state;
        return localeLoaded ? <Component intl={intl} {...this.props} /> : null;
      }
    }

    return connect(({ global = {} }) => ({
      language: global.language,
    }))(IntlComponent);
  };
}
