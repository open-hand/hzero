import React from 'react';

import { Layout } from 'hzero-ui';
import qs from 'querystring';
import { map } from 'lodash';
import DocumentTitle from 'react-document-title';
import { connect } from 'dva';
import { Redirect, Route, Switch } from 'dva/router';
import dynamic from 'dva/dynamic';
import LoadingBar from 'components/NProgress/LoadingBar';

import { getRoutesContainsSelf, getCurrentLanguage } from 'utils/utils';
import { tabListen } from 'utils/menuTab';

import './index.less';

const { Content } = Layout;

const EMPTY_ROUTE = () => null;

const showStyle = {};

const hiddenStyle = {
  display: 'none',
};

/**
 * 菜单数据结构改变 只有菜单有path,目录没有path
 * 所有的菜单必须有 服务前缀 `/服务前缀/...功能集合/功能/...子功能`
 * 根据菜单取得重定向地址.
 */
const getRedirect = (item, redirectData = []) => {
  if (item && item.children) {
    // 目录
    for (let i = 0; i < item.children.length; i++) {
      getRedirect(item.children[i], redirectData);
    }
    return redirectData;
  }
  if (item && item.path) {
    // 菜单
    let menuPaths = item.path.split('/');
    if (!menuPaths[0]) {
      menuPaths = menuPaths.slice(1, menuPaths.length);
    }
    let menuPath = '';
    for (let i = 0; i < menuPaths.length - 1; i++) {
      menuPath += `/${menuPaths[i]}`;
      const from = menuPath;
      const to = `${menuPath}/${menuPaths[i + 1]}`;
      const exist = redirectData.some((route) => route.from === from);
      if (!exist) {
        redirectData.push({ from, to });
      }
    }
  }
};

class PublicLayout extends React.Component {
  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({
      type: 'global/publicLazyInit',
    }).then(() => {
      // 不需要初始化菜单成功
      tabListen();
    });

    const routerParam = qs.parse(this.props.history.location.search.substr(1));
    const { language: paramLanguage = 'zh_CN', organizationId } = routerParam;
    dispatch({
      type: 'global/publicLayoutLanguage',
      payload: {
        language: paramLanguage,
        organizationId,
      },
    });

    // 设置中文时的表单样式处理
    const language = getCurrentLanguage();
    if (language && language !== 'zh_CN') {
      document.body.className = 'global-layout';
    }

    // 清除首屏loading
    const loader = document.querySelector('#loader-wrapper');
    if (loader) {
      loader.parentNode.removeChild(loader);
      // 设置默认页面加载动画
      dynamic.setDefaultLoadingComponent(() => <LoadingBar />);
    }
  }

  getBashRedirect = () => {
    // According to the url parameter to redirect
    // 这里是重定向的,重定向到 url 的 redirect 参数所示地址
    const urlParams = new URL(window.location.href);

    const redirect = urlParams.searchParams.get('redirect');
    // Remove the parameters in the url
    if (redirect) {
      urlParams.searchParams.delete('redirect');
      window.history.replaceState(null, 'redirect', urlParams.href);
    } else {
      const { routerData = {} } = this.props;
      // get the first authorized route path in routerData
      // const authorizedPath = Object.keys(routerData).find(
      //   item => check(routerData[item].authority, item) && item !== '/'
      // );
      const authorizedPath = Object.keys(routerData).find((item) => item !== '/');
      return authorizedPath;
    }
    return redirect;
  };

  render() {
    const { routerData, menu = [], activeTabKey, tabs, currentUser = {} } = this.props;
    const redirectData = [{ from: '/', to: '/workplace' }]; // 根目录需要跳转到工作台
    menu.forEach((item) => {
      getRedirect(item, redirectData);
    });
    const bashRedirect = this.getBashRedirect();

    const layout = (
      <Layout style={{ height: '100vh', overflow: 'hidden' }}>
        <Switch>
          {map(redirectData, (item) => (
            <Redirect key={item.from} exact from={item.from} to={item.to} />
          ))}
          {bashRedirect ? <Redirect exact from="/" to={bashRedirect} /> : null}
          {menu.length === 0 ? null : <Route render={EMPTY_ROUTE} />}
        </Switch>
        {map(tabs, (pane) => (
          <Content
            key={pane.key}
            className="page-container"
            style={activeTabKey === pane.key ? showStyle : hiddenStyle}
          >
            {getRoutesContainsSelf(pane.key, routerData).map((item) => (
              <Route
                key={item.key}
                path={item.path}
                exact={item.exact}
                component={item.component}
              />
            ))}
            {/* {menu.length === 0 ? null : <Route render={NotFound} />} */}
          </Content>
        ))}
      </Layout>
    );

    return <DocumentTitle title={currentUser.title || ''}>{layout}</DocumentTitle>;
  }
}

export default connect(({ user = {}, global = {} }) => ({
  currentUser: user.currentUser,
  menu: global.menu,
  routerData: global.routerData,
  activeTabKey: global.activeTabKey,
  tabs: global.tabs,
  language: global.language,
}))(PublicLayout);
