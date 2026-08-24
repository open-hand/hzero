/**
 * 监听 theme 的 修改
 *
 */
import React from 'react';
import { connect } from 'dva';
import useThemeHelper from '@hzero-front-ui/cfg/lib/components/Container/useThemeHelper';
import { defaultConfig } from '@hzero-front-ui/cfg/lib/utils/config';

import { getEnvConfig } from 'utils/iocUtils';

const { MULTIPLE_SKIN_ENABLE } = getEnvConfig();

let ued = false;
try {
  ued = MULTIPLE_SKIN_ENABLE ? JSON.parse(MULTIPLE_SKIN_ENABLE) : false;
} catch (e) {
  ued = false;
}
class DefaultListenTheme extends React.PureComponent {
  constructor(props) {
    super(props);
    this.state = {};
  }

  static getDerivedStateFromProps(nextProps, prevState) {
    if (ued) {
      const nextState = {};
      const { setTheme = (e) => e, menuLayoutTheme } = nextProps;
      nextState.menuLayoutTheme = menuLayoutTheme;
      if (prevState.menuLayoutTheme !== menuLayoutTheme) {
        const themeConfigCurrent = localStorage.getItem('themeConfigCurrent');
        if (!themeConfigCurrent && setTheme) {
          const { readOriginLocalTheme, setLocalTheme } = useThemeHelper();
          const localTheme = readOriginLocalTheme();
          const schemaMap = ['theme1', 'theme2'];
          if (schemaMap.includes(menuLayoutTheme)) {
            const schemaCurrent = localTheme[menuLayoutTheme]?.current || defaultConfig;
            localStorage.setItem('themeConfigCurrent', menuLayoutTheme);
            setLocalTheme({
              current: {
                ...schemaCurrent,
                schema: menuLayoutTheme,
              },
              prev: {},
            });
            setTheme({
              current: {
                ...schemaCurrent,
                schema: menuLayoutTheme,
              },
              prev: {},
            });
          }
        }
      }
      return nextState;
    }
  }

  render() {
    return null;
  }
}

export default connect(({ user = {} }) => ({
  menuLayoutTheme: (user.currentUser || {}).menuLayoutTheme,
}))(DefaultListenTheme);
