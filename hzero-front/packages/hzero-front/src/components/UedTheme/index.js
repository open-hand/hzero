import { createGlobalStyle, StyleSheetManager } from 'styled-components';
import ThemeContext from '@hzero-front-ui/cfg/lib/utils/ThemeContext';
import { getStyleSheetTarget } from '@hzero-front-ui/cfg/lib/utils/utils';
import React, { useContext, useEffect } from 'react';
import { configure } from 'choerodon-ui';

import expandMenuCss from './expandMenuCss';
import getCommonLayoutStyle from './LayoutCommon';
import getSideExpandLayoutCss from './sideExpandLayout';
import getSideCascadeLayoutCss from './sideCascadeLayout';
import getTopLayoutCss from './topLayoutCss';
import globalOverride from './GlobalOverride';
import HzeroGlobalStyle from './HzeroGlobalStyle';
import C7nPolyfillStyle from './DefaultC7NPolyfill';

const GlobalStyle = createGlobalStyle`
  /* hzero-layout-global-style */
  ${getCommonLayoutStyle}
  ${getSideExpandLayoutCss}
  ${getSideCascadeLayoutCss}
  ${getTopLayoutCss}
  ${expandMenuCss}
  ${globalOverride}
`;

export default () => {
  const themeCtx = useContext(ThemeContext);
  let theme = {};
  if (themeCtx && themeCtx.originTheme) {
    theme = themeCtx.originTheme;
  }
  const { config = {} } = themeCtx;
  const shouldUsePolyfill = !config.current || config.current.schema === 'theme2';
  useEffect(() => {
    if (config.current.schema !== 'theme2') {
      configure({
        tableRowHeight: 34,
      });
    } else {
      configure({
        tableRowHeight: 36,
      });
    }
  }, [config.current.schema]);
  return (
    <>
      <StyleSheetManager target={getStyleSheetTarget('hzero-ued-c7n-polyfill-style')}>
        <>{shouldUsePolyfill && <C7nPolyfillStyle theme={theme} />}</>
      </StyleSheetManager>
      <StyleSheetManager target={getStyleSheetTarget('hzero-common-global-style')}>
        <HzeroGlobalStyle />
      </StyleSheetManager>
      <StyleSheetManager target={getStyleSheetTarget('hzero-ued-theme-layout-and-global-style')}>
        <>{config.current && config.current.schema !== 'theme2' && <GlobalStyle theme={theme} />}</>
      </StyleSheetManager>
    </>
  );
};
