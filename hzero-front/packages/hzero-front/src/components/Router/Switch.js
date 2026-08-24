/*eslint-disable*/

import React from 'react';
import { Switch, withRouter } from 'dva/router';

import { startsWith } from 'lodash';

@withRouter
export default class WrapperSwitch extends Switch {
  shouldComponentUpdate(nextProps, _, nextLegacyContext) {
    const { router: { route: { location: { pathname } = {} } = {} } = {} } = nextLegacyContext;
    const { activeTabKey, tabKey, tabPathname } = nextProps;
    /* todo 只有当 pathname 等于 Tab 里的 path 时 才能更新 */
    return (
      tabKey === activeTabKey &&
      /* sometimes the pathname is different with tabPathname */
      startsWith(decodeURIComponent(pathname), decodeURIComponent(tabPathname))
    );
  }
}
