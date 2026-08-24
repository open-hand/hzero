import React from 'react';
import { Layout, Spin, Tabs } from 'hzero-ui';
import { isFunction, map, isEqual, cloneDeep } from 'lodash';
import { connect } from 'dva';
import { Link, Redirect, Route, Switch } from 'dva/router';

import getTabRoutes from 'components/Router';
import Exception from 'components/Exception';
import { cleanCache } from 'components/CacheComponent';

import { isPromise } from 'utils/utils';
import { closeTab, getBeforeMenuTabRemove, getTabFromKey, openTab } from 'utils/menuTab';

import styles from './index.less';
import Popover from './Popover';

const { Content } = Layout;
const { TabPane } = Tabs;
const DefaultNotFound = () => (
  <Exception type="404" style={{ minHeight: 500, height: '80%' }} linkElement={Link} />
);
const EMPTY_ROUTE = () => null;

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

// document.addEventListener('fullscreenchange', () => {

// });

const fc = () => {
  if (
    !(
      document.fullScreenElement ||
      document.mozFullScreenElement ||
      document.webkitFullscreenElement ||
      document.msFullscreenElement ||
      document.webkitIsFullScreen
    )
  ) {
    const node = document.querySelector(`.hzero-fullscreen`);
    if (node) {
      node.classList.remove('hzero-fullscreen');
    }
  }
};

document.onfullscreenchange = fc;
document.onmsfullscreenchange = fc;
document.onmozfullscreenchange = fc;
document.onwebkitfullscreenchange = fc;

const arr = ['undefined', 'string', 'number', 'boolean'];

class DefaultMenuTabs extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      oldProps: {},
    };
  }

  shouldComponentUpdate() {
    return true;
  }

  /**
   * 控制半受控属性 dataSource
   * 当 父组件 dataSource 改变时, 使用父组件的 dataSource, 之后都是本组件自己的dataSource
   * @param {Object} nextProps - 接收的属性
   * @param {Object} prevState - 上一个State
   */
  static getDerivedStateFromProps(nextProps, prevState) {
    const obj = cloneDeep(prevState);
    obj.flag = true;
    const {
      removeOtherMenuTab,
      removeAllMenuTab,
      removeSomeMenuTab,
      updateRefreshMenuTabsKeyMap,
      ...others
    } = nextProps;
    const flag = Object.keys(others).every((o) => {
      const type = typeof nextProps[o];
      if (arr.includes(type) || nextProps[o] === null) {
        obj.oldProps[o] = nextProps[o];

        return nextProps[o] === prevState.oldProps[o];
      } else {
        obj.oldProps[o] = cloneDeep(nextProps[o]);

        return isEqual(nextProps[o], prevState.oldProps[o]);
      }
    });
    obj.flag = flag;
    return obj;
  }

  /**
   * 切换 tab
   * @param {string} activeKey - menuTab 的 key
   */
  onTabChange(activeKey) {
    // const { history } = this.props;
    openTab(getTabFromKey(activeKey));
  }

  onTabEdit = (targetKey, action) => {
    this[action](targetKey);
  };

  /**
   * 关闭 tab
   */
  remove(targetKey) {
    const onBeforeHandler = getBeforeMenuTabRemove(targetKey);
    if (isFunction(onBeforeHandler)) {
      const isShouldDelete = onBeforeHandler();
      if (isPromise(isShouldDelete)) {
        isShouldDelete.then(
          // 关闭tab
          () => {
            cleanCache(targetKey);
            closeTab(targetKey);
          }
        );
      } else if (isShouldDelete !== false) {
        cleanCache(targetKey);
        closeTab(targetKey);
      }
    } else {
      cleanCache(targetKey);
      closeTab(targetKey);
    }
  }

  getBaseRedirect() {
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
  }

  render() {
    const {
      activeTabKey, // 当前激活的 Tab 的 key
      NotFound = DefaultNotFound, // 当找不到路由时返回的路由
      tabs = [], // 所有打开的 tabs
      menu = [], // 所有的菜单
      layoutLoading,
      pathname,
      routerData = {}, // 所有的路由
      extraRight = null, // 右侧额外的组件
      refreshMenuTabsKeyMap = new Map(),
    } = this.props;
    if (layoutLoading) {
      return (
        <div style={{ textAlign: 'center' }}>
          <Spin size="large" />
        </div>
      );
    }
    const redirectData = [{ from: '/', to: '/workplace' }]; // 根目录需要跳转到工作台
    menu.forEach((item) => {
      getRedirect(item, redirectData);
    });
    const bashRedirect = this.getBaseRedirect();

    return (
      <>
        <Switch>
          {map(redirectData, (item) => (
            <Redirect key={item.from} exact from={item.from} to={item.to} />
          ))}
          {bashRedirect ? <Redirect exact from="/" to={bashRedirect} /> : null}
          {menu.length === 0 ? null : <Route render={EMPTY_ROUTE} />}
        </Switch>
        <Tabs
          hideAdd
          onChange={this.onTabChange}
          activeKey={activeTabKey}
          type="editable-card"
          onEdit={this.onTabEdit}
          tabBarExtraContent={extraRight}
          className={styles['menu-tabs']}
        >
          {map(tabs, (pane, index) => (
            <TabPane
              closable={pane.closable}
              tab={<Popover {...this.props} pane={pane} index={index} />}
              key={pane.key}
            >
              <Content className="page-container" key={refreshMenuTabsKeyMap.get(pane.key)}>
                {getTabRoutes({
                  pane,
                  routerData,
                  NotFound,
                  pathname,
                  menu,
                  activeTabKey,
                })}
              </Content>
            </TabPane>
          ))}
        </Tabs>
      </>
    );
  }
}

function mapStateToProps({ global = {}, user = {}, routing }) {
  const { currentUser = {} } = user;
  const { menuLayout = 'default-layout' } = currentUser;
  return {
    activeTabKey: global.activeTabKey,
    tabs: global.tabs,
    menu: global.menu,
    routerData: global.routerData,
    language: global.language,
    layoutLoading: global.layoutLoading,
    menuLayout,
    pathname: routing && routing.location.pathname,
    refreshMenuTabsKeyMap: global.refreshMenuTabsKeyMap,
  };
}

function mapDispatchToProps(dispatch) {
  return {
    removeSomeMenuTab(payload) {
      return dispatch({
        type: 'global/removeSomeMenuTab',
        payload,
      });
    },
    removeOtherMenuTab(payload) {
      return dispatch({
        type: 'global/removeOtherMenuTab',
        payload,
      });
    },
    removeAllMenuTab(payload) {
      return dispatch({
        type: 'global/removeAllMenuTab',
        payload,
      });
    },
    updateRefreshMenuTabsKeyMap(payload) {
      return dispatch({
        type: 'global/updateRefreshMenuTabsKeyMap',
        payload,
      });
    },
  };
}

export default connect(mapStateToProps, mapDispatchToProps, null, { pure: false })(DefaultMenuTabs);
