import React, { useCallback, useEffect, useMemo, useState } from 'react';
import { routerRedux, Switch, Route, withRouter } from 'dva/router';
import { getConfig } from 'hzero-boot';

import ModalContainer, { registerContainer } from 'components/Modal/ModalContainer';
import { ModalContainer as C7nModalContainer } from 'choerodon-ui/pro';
import Authorized from 'components/Authorized/WrapAuthorized';
import PermissionProvider from 'components/Permission/PermissionProvider';
import UedThemeConfig from 'components/UedThemeConfig';

import LocalProviderAsync from 'utils/intl/LocaleProviderAsync';

import { dynamicWrapper } from 'utils/router';
import { inject } from 'what-di';
import DefaultContainer from 'utils/iocUtils/DefaultContainer';
import { initIoc, getEnvConfig } from 'utils/iocUtils';
import { UedProvider } from 'utils/iocUtils/UedProvider';
import ThemeContext from '@hzero-front-ui/cfg/lib/utils/ThemeContext';
import { watermark, cacWaterMark } from 'utils/watermark';
import { getCurrentUser } from 'utils/utils';

// 初始化ioc容器
initIoc();

// 水印配置
function initWaterMark() {
  const f = () => {
    const container = document.querySelector(
      ".ant-tabs[class*='index_menu-tabs']>.ant-tabs-content"
    );
    if (!container) {
      setTimeout(f, 300);
      return;
    }
    const oldWater = container.querySelectorAll('.mask_mark');
    const config = cacWaterMark(container);
    const newMaskNums = config.watermark_cols * config.watermark_rows;
    if (newMaskNums === oldWater.length) {
      setTimeout(f, 300);
      return;
    }
    const { realName, loginName, waterMarkFlag } = getCurrentUser();
    if (oldWater.length > 0) {
      oldWater.forEach((node) => {
        container.removeChild(node);
      });
    }
    if (waterMarkFlag) {
      watermark(`${loginName}-${realName}-${new Date().toLocaleDateString()}`, config, container);
    }
    setTimeout(f, 300);
  };
  setTimeout(f, 300);
}
initWaterMark();

const WithRouterC7nModalContainer = withRouter(C7nModalContainer);
const { ConnectedRouter } = routerRedux;
const { DefaultAuthorizedRoute, PubAuthorizedRoute } = Authorized;

function RouterConfig({ history, app }) {
  const [uedConfig, setUedConfig] = useState({
    Container: DefaultContainer,
  });

  const Layout = dynamicWrapper(app, ['user', 'login'], () => import('./layouts/Layout'));
  const PubLayout = dynamicWrapper(app, ['user', 'login'], () => import('./layouts/PubLayout'));

  // 免登陆无权限路由
  const PublicLayout = dynamicWrapper(app, [], () => import('./layouts/PublicLayout'));
  // 免登陆权限路由
  const PrivateLayout = dynamicWrapper(app, [], () => import('./layouts/PrivateLayout'));

  useEffect(() => {
    const ued = inject(UedProvider);
    ued.subscribe(({ Container = null }) => {
      setUedConfig({
        Container: Container || DefaultContainer,
      });
    });
  }, []);

  // 页面布局中的head
  const layoutExtraHeader = useMemo(() => {
    return getConfig('layoutExtraHeader');
  }, []);

  const handleThemeChange = useCallback((e) => {
    localStorage.setItem('themeConfigCurrent', e?.current?.schema);
  }, []);

  const { configureParams } = getEnvConfig();
  const defaultTheme = configureParams?.defaultTheme || 'theme2';

  return (
    <uedConfig.Container defaultTheme={defaultTheme} onChange={handleThemeChange}>
      <LocalProviderAsync>
        <PermissionProvider>
          <ConnectedRouter history={history}>
            <>
              <ThemeContext.Consumer>
                {(themeProps) => {
                  return (
                    <UedThemeConfig setTheme={themeProps.setTheme} schema={themeProps.schema} />
                  );
                }}
              </ThemeContext.Consumer>
              <ModalContainer ref={registerContainer} />
              <WithRouterC7nModalContainer />
              <Switch>
                <Route path="/private" render={(props) => <PrivateLayout {...props} />} />
                <Route path="/public" render={(props) => <PublicLayout {...props} />} />
                <PubAuthorizedRoute path="/pub" render={(props) => <PubLayout {...props} />} />
                <DefaultAuthorizedRoute
                  path="/"
                  render={(props) => (
                    <Layout
                      {...props}
                      extraHeaderRight={layoutExtraHeader}
                      headerProps={{ toolbarProps: { extraHeaderRight: layoutExtraHeader } }}
                    />
                  )}
                />
              </Switch>
            </>
          </ConnectedRouter>
        </PermissionProvider>
      </LocalProviderAsync>
    </uedConfig.Container>
  );
}

export default RouterConfig;
