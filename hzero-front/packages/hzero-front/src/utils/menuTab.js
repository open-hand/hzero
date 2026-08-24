/**
 * @date 2018-07-15
 * @version 1.0.0
 * @author WY
 * @email  yang.wang06@hand-china.com
 * @copyright Copyright (c) 2018, Hand
 */
/* eslint no-underscore-dangle: ["error", { "allow": ["_store"] }] */

import { routerRedux } from 'dva/router';
import pathToRegexp from 'path-to-regexp';
import { findIndex, forEach, isArray, isEmpty, isObject, isString, slice, split } from 'lodash';
import EventEmitter from 'event-emitter';
import qs from 'query-string';
import uuid from 'uuid/v4';
import { getDvaApp } from './iocUtils';

import { getCurrentRole, getSession, setSession } from './utils';

const menuTabEventManager =
  window.menuTabEventManager || (window.menuTabEventManager = new EventEmitter());

const menuTabBeforeRemoveEvents =
  window.menuTabBeforeRemoveEvents || (window.menuTabBeforeRemoveEvents = new Map());

/**
 * 添加对应 某个tab 的关闭前时间
 * @export
 * @param {string} tabKey
 * @param {() => boolean | Promise} onBeforeHandler - 返回 false 或 Promise.reject 不会关闭 tab
 */
function onBeforeMenuTabRemove(tabKey, onBeforeHandler) {
  if (menuTabBeforeRemoveEvents.has(tabKey)) {
    // todo
    // console.warn('同一个tab关闭前事件只能有一个回调');
  } else {
    menuTabBeforeRemoveEvents.set(tabKey, onBeforeHandler);
  }
}

/**
 * 获取 某个tab的 onBefore 事件
 * @export
 * @param {string} tabKey
 * @returns {() => boolean | Promise} onBeforeHandler - 返回 false 或 Promise.reject 不会关闭 tab
 */
function getBeforeMenuTabRemove(tabKey) {
  return menuTabBeforeRemoveEvents.get(tabKey);
}

/**
 * 移除 某个tab的 onBefore 事件
 * @export
 * @param {string} tabKey
 * @returns {boolean}
 */
function deleteBeforeMenuTabRemove(tabKey) {
  return menuTabBeforeRemoveEvents.delete(tabKey);
}

// const menuItemType = {
//   root: 'root',
//   dir: 'dir',
//   menu: 'menu',
// };

/**
 * 用来在菜单还没有加载的时候保存 pathname
 */
const pathnameStack = [];
// 存储 tabs 的 key
const menuTabSessionKey = 'menuTabSessionKey';

// 工作台 tab
const workplaceTab = {
  title: 'hzero.common.title.workspace', // todo 可不可以使用编码, openTab 时 使用 title: intl.get(title).d(title),
  icon: 'home',
  closable: false,
  key: '/workplace',
  path: '/workplace',
};

function getActiveTabMenuId(activeTabKey) {
  const routerData = getMenuLeafData();
  let activeTabMenu = {};
  forEach(routerData, (route) => {
    const reg = pathToRegexp(route.path, [], { end: false });
    if (reg.test(activeTabKey) && route.id && route.type !== 'inner-link') {
      activeTabMenu = route;
      return false;
    }
  });
  return activeTabMenu;
}

/**
 * 获得tab的数据
 */
function getMenuId() {
  const state = getDvaApp()._store.getState();
  const {
    global: { activeTabMenuId },
  } = state;
  return activeTabMenuId;
}

/**
 * 获得tab的数据
 */
function getTabData() {
  const state = getDvaApp()._store.getState();
  const { global: { tabs = [] } = {} } = state;
  return tabs;
}

/**
 * 获得菜单的数据
 */
function getMenuData() {
  const state = getDvaApp()._store.getState();
  const { global: { menu = [] } = {} } = state;
  return menu;
}

/**
 * 获得菜单的数据
 */
function getMenuLeafData() {
  const state = getDvaApp()._store.getState();
  const { global: { menuLeafNode = [] } = {} } = state;
  return menuLeafNode;
}

/**
 * 判断菜单是否加载完毕
 */
function isMenuTabReady() {
  const state = getDvaApp()._store.getState();
  const { global: { menuLoad = false, tabsIsInit = false } = {} } = state;
  return menuLoad && tabsIsInit;
}

/**
 * 获取所有的路由信息
 */
function getRouterData() {
  const state = getDvaApp()._store.getState();
  const { global: { routerData = {} } = {} } = state;
  return routerData;
}

/**
 * 获取真正的 pathname 和 search
 * 有些 监听 到的 url 可能被 search 污染了, 需要把 search 剥离出去
 * 注意 有些 search 可能还是在 location 中，而不是在url， 所以 search 可能为空
 * @param {!String} pathname - url 地址
 */
function getLocation(pathname) {
  const [realPathname, search] = split(pathname, '?');
  return { pathname: realPathname, search };
}

/**
 * 判断是不是合法的tabPathname
 * '' / 登录,登出 其他的都认为合法的pathname
 * '' / /user/login /user/logout
 * @param {!String} pathname - 切换的pathname
 */
function isValidTabPathname(pathname) {
  return (
    isString(pathname) &&
    findIndex(
      ['', '/', '/user/login', '/user/logout'],
      (invalidPathname) => invalidPathname === pathname
    ) === -1
  );
}

/**
 * 从pathname中获取 tabKey
 * @param {!String} pathname - 切换的pathname
 */
function getTabKey(pathname) {
  return slice(split(pathname, '/'), 1);
}

/**
 *  判断新的tabKey 与 之前的 tabKey 的区别
 * @param {!String[]} newTabKey - 新的页面的 tabKey
 * @param {String[]} [oldTabKey=[]] - tab的 tabKey
 * @return {Number} 0: 不是同一个tab,1: 是同一个tab
 */
function diffTabKey(newTabKey, oldTabKey = []) {
  const nLen = newTabKey.length;
  const oLen = oldTabKey.length;
  const maxLen = Math.max(oLen, nLen);
  const minLen = Math.min(oLen, nLen);
  let index = 0;
  for (; index < maxLen; index++) {
    if (newTabKey[index] !== oldTabKey[index]) {
      break;
    }
  }
  if (minLen === index) {
    // 从列表到详情
    return 1;
  }
  return 0;
}

/**
 * 替换原先的tab 同时 更新 activeKey
 * @param {!Object} tab - 需要替换的
 * @param {!String} pathname - 新的页面path
 * @param {String|Object} search - search
 */
function replaceTabPath(tab, pathname, search) {
  const newTab = {
    ...tab,
    path: pathname,
    search,
  };
  getDvaApp()._store.dispatch({
    type: 'global/replaceTab',
    payload: {
      tab,
      newTab,
    },
  });
}

// /**
//  * 获取离匹配 tabKey 最深的menu信息
//  * @param {!String[]} tabKey - tabKey
//  * @param {!Object[]} menuData - 菜单的信息
//  * @return {deepestMenuPathTabKey,deepestMenu} - 返回的数据结构
//  */
// function getTabDeepestMenuInfo(tabKey, menuData) {
//   let path = '';
//   let deepestMenuPathKey;
//   let deepestMenu = { children: menuData };
//   forEach(tabKey, key => {
//     path += `/${key}`;
//     forEach(deepestMenu.children, menu => {
//       if (menu.path === path) {
//         deepestMenuPathKey = path;
//         deepestMenu = menu;
//         return false;
//       }
//     });
//   });
//   // 菜单中 没有最近的菜单, tabKey 是非法的
//   return deepestMenu.children === menuData
//     ? null
//     : {
//         deepestMenuPathTabKey: getTabKey(deepestMenuPathKey),
//         deepestMenu,
//       };
// }

function countCharNum(str, char) {
  if (!str || !char) {
    return 0;
  }
  let count = 0;
  for (const c of str) {
    if (c === char) {
      count++;
    }
  }
  return count;
}

/**
 * 获取字符位置
 */
function findCharNPosition(str, subStr, n) {
  if (!str || !subStr) {
    return 0;
  }
  let position = -1;
  let count = n;
  while (count !== 0) {
    position = str.indexOf(subStr, position + 1);
    count--;
  }
  return position;
}

/**
 * 获取菜单
 * warn 会修改 menuData 菜单对象, 加上 pathRegexp
 */
function getMenuInfoByRoute(route, menuData) {
  if (menuData.type === 'inner-link') {
    return null;
  }
  // menuData 是数组
  if (isArray(menuData)) {
    for (let i = 0; i < menuData.length; i++) {
      const menu = getMenuInfoByRoute(route, menuData[i]);
      if (menu) {
        return menu;
      }
    }
  } else if (isArray(menuData.children)) {
    // menuData 是 目录
    const menu = getMenuInfoByRoute(route, menuData.children);
    if (menu) {
      return menu;
    }
  } else if (menuData.path) {
    const p = findCharNPosition(route.path, '/', countCharNum(menuData.path, '/') + 1);
    if (p === -1) {
      if (route.pathRegexp.test(menuData.path)) {
        return menuData; // 找到 菜单
      }
    } else if (route.pathRegexp.test(menuData.path + route.path.substr(p))) {
      return menuData; // 找到 菜单
    }
  }
  return null;
}

/**
 * 通过 key 获取 tab
 * 如果没有找到 返回 默认 tab
 * @param {!String} key - tab 的 key
 */
function getTabFromKey(key) {
  const tabData = getTabData();
  let findTab = workplaceTab;
  forEach(tabData, (tab) => {
    if (tab.key === key) {
      findTab = tab;
      return false;
    }
  });
  return findTab;
}

/**
 * 获取初始化的tab
 */
function getInitialTabData() {
  const roleId = getCurrentRole().id;
  const prevTabs = getSession(`${menuTabSessionKey}-${roleId}`);
  if (isEmpty(prevTabs.tabs)) {
    return [workplaceTab];
  }
  return prevTabs.tabs;
}

/**
 * 获取初始化的 activeKey
 * @returns {*}
 */
function getInitialActiveTabKey() {
  const roleId = getCurrentRole().id;
  const prevTabs = getSession(`${menuTabSessionKey}-${roleId}`);
  if (isEmpty(prevTabs.activeTabKey)) {
    return '/workplace';
  }
  return prevTabs.activeTabKey;
}

/**
 * 通过 tabKey 查找 tab
 * 如果没有找到 返回 null
 * @param {!String} key
 * @returns
 */
function findTabFromKey(key) {
  const tabData = getTabData();
  let findTab = null;
  forEach(tabData, (tab) => {
    if (tab.key === key) {
      findTab = tab;
      return false;
    }
  });
  return findTab;
}

/**
 * 通过 路径 获取 路由
 * @param {*} pathname
 * @returns
 */
function findRouteFromPathname(pathname) {
  const routerData = getRouterData();
  let findRoute;
  forEach(routerData, (route) => {
    if (route && route.pathRegexp && route.pathRegexp.test(pathname)) {
      findRoute = route;
      return false;
    }
  });
  return findRoute;
}

/**
 * 获取当前激活的 tab Key
 */
function getActiveTabKey() {
  const state = getDvaApp()._store.getState();
  const { global: { activeTabKey } = {} } = state;
  return activeTabKey;
}

/**
 * 添加 tab 并且 切换路由
 */
function addTabAndPush(tab) {
  createTab(tab);
  push(tab.path, tab.search, tab.state);
}

/**
 * 激活tab 并且 切换路由
 * @param {*} tab
 */
function activeTabAndPush(tab) {
  activeTab(tab);
  push(tab.path, tab.search, tab.state);
}

/**
 * @param {!String} path - 将要跳转的path
 * @param {!String} search - history 的search信息
 */
function push(path, search, state) {
  if (isString(path)) {
    getDvaApp()._store.dispatch(
      routerRedux.push({
        pathname: path,
        search,
        state,
      })
    );
  }
}

/**
 * 创建 tab
 * @param {!Object} tab - tab数据
 * @param {!String} tab.key - 打开 tab 的 key
 * @param {String} [tab.path=key] - 打开页面的path
 * @param {String} [tab.title='NewTab'] - tab的标题
 * @param {String} [tab.icon=null] - icon的值,antd 的 Icon
 * @param {Boolean} [tab.closable=true] - tab 是否可以关闭
 */
function createTab(tab) {
  getDvaApp()._store.dispatch({
    type: 'global/addTab',
    payload: {
      newTab: tab,
    },
  });
}

/**
 *
 * @param {!Object} tab - tab数据
 * @param {!String} tab.key - 打开 tab 的 key
 * @param {String} [tab.path=key] - 打开页面的path
 * @param {String} [tab.title='NewTab'] - tab的标题
 * @param {String} [tab.icon=null] - icon的值,antd 的 Icon
 * @param {Boolean} [tab.closable=true] - tab 是否可以关闭
 */
function activeTab(tab) {
  getDvaApp()._store.dispatch({
    type: 'global/replaceTab',
    payload: {
      newTab: tab,
      tab,
    },
  });
}

/**
 * 更新对应key的tab 做 {...oldTab, ...updateTab} 操作
 * @param {Object} tab - 更新的tab
 */
function updateTab(tab) {
  getDvaApp()._store.dispatch({
    type: 'global/updateTab',
    payload: tab,
  });
}

/**
 * 格式化url
 * @param {url} path 待格式化的url
 * @return {}
 */
export function parsePath(path) {
  let pathname = path || '/';
  let search = '';
  let hash = '';

  const hashIndex = pathname.indexOf('#');
  if (hashIndex !== -1) {
    hash = pathname.substr(hashIndex);
    pathname = pathname.substr(0, hashIndex);
  }

  const searchIndex = pathname.indexOf('?');
  if (searchIndex !== -1) {
    search = pathname.substr(searchIndex);
    pathname = pathname.substr(0, searchIndex);
  }

  return {
    pathname,
    search: search === '?' ? '' : search,
    hash: hash === '#' ? '' : hash,
  };
}

function searchFormat(search) {
  if (isObject(search)) {
    return search;
  }
  return qs.parse(search);
}

/**
 * 入口, 切换或者新建 tab
 * @todo 不能在同tab 打开同tab
 * @param {!String} key - 打开 tab 的 key
 * @param {String} [path=key] - 打开页面的path
 * @param {String} [title='NewTab'] - tab的标题
 * @param {String} [icon=null] - icon的值,antd 的 Icon
 * @param {Boolean} [closable=true] - tab 是否可以关闭
 * @param {String} [search] - history 的 search 值
 */
function openTab(newTab) {
  const {
    key,
    path = key,
    title,
    icon = null,
    closable = true,
    search,
    type,
    state: tabState,
  } = newTab;
  const activeTabKey = getActiveTabKey();
  const keyParseData = parsePath(key);
  const oldTab = findTabFromKey(keyParseData.pathname);
  let pathParseData;
  let searchData;
  if (newTab.key === '/workplace' || newTab.key === '/') {
    activeTabAndPush(workplaceTab);
  } else if (oldTab === null) {
    pathParseData = parsePath(path);
    searchData = qs.stringify(Object.assign(searchFormat(search), qs.parse(pathParseData.search)));
    addTabAndPush({
      key: keyParseData.pathname,
      path: pathParseData.pathname,
      title,
      icon,
      closable,
      search: searchData,
      state:
        isEmpty(tabState) || !tabState?.hasOwnProperty('_back')
          ? tabState
          : { ...tabState, _back: undefined },
      type,
    });
  } else if (activeTabKey === oldTab.key) {
    activeTab(oldTab);
  } else {
    // 1. search 为undefined, 新的search为 oldTab.search 合并 转化的search
    // 2. search 不为undefined, 新的search 为 search 合并 转化的search
    // 3. key 一定是存在的, 所以path也一定存在(path 是必须的)
    // 4. oldTab存在，以oldTab的path为准
    pathParseData = parsePath(oldTab.path);
    searchData = qs.stringify(Object.assign(searchFormat(search), qs.parse(pathParseData.search)));
    activeTabAndPush({
      ...oldTab,
      ...newTab,
      key: keyParseData.pathname,
      path: pathParseData.pathname,
      search:
        search === undefined
          ? qs.stringify({ ...searchFormat(oldTab.search), ...qs.parse(pathParseData.search) })
          : searchData,
      state:
        isEmpty(tabState) || !tabState?.hasOwnProperty('_back')
          ? tabState
          : { ...tabState, _back: undefined },
    });
  }
}

/**
 * 关闭 tab
 * @param {!String} key - tab 的key
 */
function closeTab(key) {
  if (isString(key)) {
    // getDvaApp()._store
    //   .dispatch({
    //     type: 'global/getRemoveTabInfo',
    //     payload: key,
    //   })
    //   .then(removeTabInfo => {
    //     // 如果是关闭当前tab,需要打开新的Tab
    //     if (removeTabInfo.nextActiveTabKey) {
    //       openTab(removeTabInfo.nextTab);
    //     }
    //     // 设置新的tabs
    //     getDvaApp()._store.dispatch({
    //       type: 'global/updateState',
    //       payload: {
    //         tabs: removeTabInfo.nextTabs,
    //       },
    //     });
    //   });
    getDvaApp()
      ._store.dispatch({
        type: 'global/removeTab',
        payload: key,
      })
      .then((nextKey) => {
        menuTabEventManager.emit('close', { tabKey: key });
        openTab({ key: nextKey });
      });
  }
}

/**
 * tabListen - 对tab进行切换,或者替换操作
 * 切换tab的入口方法
 * @param {!String} [pathname=''] - 切换的pathname
 */
function tabListen(pathname = '') {
  const menuData = getMenuData();
  if (!isMenuTabReady()) {
    if (isValidTabPathname(pathname)) {
      pathnameStack.push(pathname);
    }
    return;
  }
  // 在菜单和路由加载完成后 tabListen
  while (pathnameStack.length) {
    const lPathname = pathnameStack.pop();
    tabListen(lPathname);
  }

  const { _history: { location: { search, state: tabState } = {} } = {} } = getDvaApp();
  const { pathname: realPathname, search: realSearch = search } = getLocation(pathname);

  if (isValidTabPathname(realPathname)) {
    // 从 tabs 中拿到 对应path 的 tab 和 activeKey
    const state = getDvaApp()._store.getState();
    const { global: { tabs = [], activeTabKey } = {} } = state;
    let listenTab;
    forEach(tabs, (tab) => {
      if (tab.path === realPathname) {
        listenTab = tab;
        return false;
      }
    });
    if (isEmpty(listenTab)) {
      // 打开新的 tab 或者 切换tab
      const route = findRouteFromPathname(realPathname);
      if (isEmpty(route)) {
        // 非法的路径,在router中不存在
        // todo 打开 404 页面 而不是在工作台 404
        // 打开工作台(监听到不存在的tab)
        replaceTabPath(workplaceTab, workplaceTab.path, '');
      } else if (route.authorized) {
        // 打开新的 tab
        // openTab 也可以打开新的tab
        const routeTabKey = route.key || realPathname;
        let routeTab;
        forEach(tabs, (tab) => {
          if (tab.key === routeTabKey) {
            routeTab = tab;
            return false;
          }
        });
        const newRouteTab = {
          key: route.key || realPathname,
          path: realPathname,
          title: route.title,
          icon: route.icon,
          closable: true,
          search: realSearch,
          state:
            isEmpty(tabState) || !tabState?.hasOwnProperty('_back')
              ? tabState
              : { ...tabState, _back: undefined },
        };
        if (routeTab) {
          replaceTabPath(newRouteTab, realPathname, realSearch);
        } else {
          createTab(newRouteTab);
        }
      } else {
        // 查询 tabKey 是否在 tab 中存在
        // 查找菜单中最深的 菜单
        const tabData = getTabData();
        const newTabKey = getTabKey(realPathname);
        let hasUpdate = false;
        const menu = getMenuInfoByRoute(route, menuData);
        // const deepestMenuInfo = getMenuInfoByTabKey(newTabKey, menuData);
        if (menu !== null) {
          forEach(tabData, (tab) => {
            switch (diffTabKey(newTabKey, getTabKey(tab.key))) {
              case 0:
                break;
              case 1:
                // 存在, 切换 activeTab 并更新 path
                hasUpdate = true;
                replaceTabPath(tab, realPathname, realSearch);
                return false;
              default:
                break;
            }
          });
          if (!hasUpdate) {
            // 不存在, 打开新的 tab
            createTab({
              path: realPathname,
              title: menu.name,
              icon: menu.icon,
              closable: true,
              key: menu.path,
              search: realSearch,
              state:
                isEmpty(tabState) || !tabState?.hasOwnProperty('_back')
                  ? tabState
                  : { ...tabState, _back: undefined },
            });
          }
        } else {
          // warn 菜单中没有对应的 tab, 404
          // todo 打开 404 页面 而不是在工作台 404
        }
      }
    } else if (listenTab.key !== activeTabKey) {
      // 切换 activeTab, pathname === listenTab.path
      replaceTabPath(listenTab, realPathname, realSearch);
    }
  }
}

function persistMenuTabs() {
  const { tabs = [], activeTabKey = '/workplace' } = getDvaApp()._store.getState().global || {};
  const pubLayoutPrefixs = ['/pub', '/public/'];
  if (self !== top && pubLayoutPrefixs.some((publicPath) => activeTabKey.startsWith(publicPath))) {
    // TODO: 如果本系统嵌套在别的系统中， 则不记录 数据到 缓存
    return;
  }
  const roleId = getCurrentRole().id;
  // TODO: 过滤掉特殊的路由, 如何做成可配置的
  const persistTabs = tabs.filter(
    (tab) => !pubLayoutPrefixs.some((publicPath) => tab.path.startsWith(publicPath))
  );
  // 过滤掉特殊的路由 和 激活的 tab
  const persistenceTabs = {
    tabs: persistTabs,
    activeTabKey: pubLayoutPrefixs.some((publicPath) => activeTabKey.startsWith(publicPath))
      ? '/workplace'
      : activeTabKey,
  };
  setSession(`${menuTabSessionKey}-${roleId}`, persistenceTabs);
}

/**
 * 清空 menuTabs 的缓存
 * warn 要注意调用顺序, 先清除 sessionStorage 里面的数据, 再清空 redux 里面的数据
 */
function cleanMenuTabs() {
  const roleId = getCurrentRole().id;
  setSession(`${menuTabSessionKey}-${roleId}`, {});
  openTab(workplaceTab); // 先打开工作台 再清空数据 注意顺序
  getDvaApp()._store.dispatch({
    type: 'global/cleanTabs',
  });
}

/**
 * 刷新当前tab页面
 * @param {string} key - tab的key
 * @param {object} e - event
 */
function refreshTab(key, e) {
  const state = getDvaApp()._store.getState();
  const { global: { refreshMenuTabsKeyMap, activeTabKey } = {} } = state;
  const tabKey = key || activeTabKey;
  const refreshKeyMap = refreshMenuTabsKeyMap;
  refreshKeyMap.set(tabKey, uuid());
  if (e) {
    e.stopPropagation();
  }
  menuTabEventManager.emit('refresh', { tabKey });
  getDvaApp()._store.dispatch({
    type: 'global/updateRefreshMenuTabsKeyMap',
    payload: {
      refreshMenuTabsKeyMap: refreshKeyMap,
    },
  });
}

export {
  cleanMenuTabs,
  persistMenuTabs,
  tabListen,
  closeTab,
  openTab,
  updateTab,
  getActiveTabKey,
  getInitialActiveTabKey,
  getInitialTabData,
  getTabFromKey,
  deleteBeforeMenuTabRemove,
  getBeforeMenuTabRemove,
  onBeforeMenuTabRemove,
  menuTabEventManager,
  getActiveTabMenuId,
  getMenuId,
  refreshTab,
  findRouteFromPathname,
  getLocation,
};

window.openTab = openTab; // 需要暴露到 window 下，消息那里有调用 openTab 的地方

// TODO 在完成 测试后 要删除
window.closeTab = closeTab;
