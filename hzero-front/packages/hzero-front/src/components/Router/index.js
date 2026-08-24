import React from 'react';
import { getRoutesContainsSelf } from 'utils/utils';
import Route from './Route';
import Switch from './Switch';

export default function getTabRoutes({
  pane,
  routerData,
  NotFound,
  menu,
  activeTabKey,
  pathname,
} = {}) {
  const { key: tabKey, path: tabPath } = pane;
  const matchRoutes = getRoutesContainsSelf(tabKey, routerData).map((item) => (
    <Route key={item.key} path={item.path} exact={item.exact} component={item.component} />
  ));
  if (menu.length !== 0 && !routerData[pathname]) {
    matchRoutes.push(<Route key="empty-router" render={NotFound} />);
  }
  return (
    <Switch tabKey={tabKey} activeTabKey={activeTabKey} tabPathname={tabPath} key={tabKey}>
      {matchRoutes}
    </Switch>
  );
}
