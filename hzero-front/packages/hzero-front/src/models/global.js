import pathToRegexp from 'path-to-regexp';
import { forEach, isBoolean, isNumber, isUndefined, map } from 'lodash';
import { localeContext } from 'choerodon-ui/pro';
import moment from 'moment';

import {
  getCurrentOrganizationId,
  getCurrentRole,
  getResponse,
  resolveRequire,
  getMomentLocale,
} from 'utils/utils';
import {
  getInitialActiveTabKey,
  getInitialTabData,
  persistMenuTabs,
  tabListen,
  getActiveTabMenuId,
} from 'utils/menuTab';
import intl from 'utils/intl';
import { getRouterData } from 'utils/router';

import { getDvaApp } from 'utils/iocUtils';
import {
  queryCount,
  queryMenu,
  queryPromptLocale,
  queryPublicPromptLocale,
  queryUnifyIdpValue,
  updateDefaultLanguage,
  checkEntryDirectory,
} from '../services/api';
import { getC7nLocale, getC7nProLocale, getHzeroUILocale } from '../services/localeApi';
import { endTrace, startTrace, getTraceStatus } from '../services/traceLogService';

/**
 * 将原始的菜单数据转成 intl 的多语言格式
 * @param {object[]} menuData - 原始的请求菜单接口的数据
 * @param {object} menuLocaleIntl - 存储菜单的 localeIntl
 * @returns
 */
function parseMenuToLocaleIntl(menuData, menuLocaleIntl) {
  if (menuData) {
    menuData.forEach((menuItem) => {
      // eslint-disable-next-line no-param-reassign
      menuLocaleIntl[menuItem.code] = menuItem.name;
      if (menuItem.subMenus) {
        parseMenuToLocaleIntl(menuItem.subMenus, menuLocaleIntl);
      }
    });
  }
}

/**
 * 获取打平的菜单
 * @param {object[]} menus - 已经处理过的有层级的菜单
 */
function getFlatMenuData(menus) {
  let keys = {};
  menus.forEach((item) => {
    if (item.children) {
      keys[item.path] = { ...item };
      keys = { ...keys, ...getFlatMenuData(item.children) };
    } else {
      keys[item.path] = { ...item };
    }
  });
  return keys;
}

function parseMenuData(menus, parent, menuData = []) {
  menuData.forEach((item) => {
    const {
      code,
      icon,
      route,
      id,
      parentId,
      quickIndex,
      type,
      name,
      helpType,
      helpValue,
      ...rest
    } = item;
    const menuItem = {
      // TODO: 菜单需要使用到的数据在这里赋值
      name: code, // 菜单的名字是多语言
      icon,
      path: route || '', // oracle 数据库 没有返回 防止拼接时候 undefined
      id,
      parentId,
      quickIndex,
      type,
      title: name,
      helpType,
      helpValue,
      menuItem: rest,
    };
    if (parent) {
      parent.children = parent.children || []; // eslint-disable-line
      parent.children.push(menuItem);
    } else {
      menus.push(menuItem);
    }
    if (item.subMenus) {
      parseMenuData(menus, menuItem, item.subMenus);
    }
  });
}

async function querySupportLanguage() {
  return queryUnifyIdpValue('HPFM.LANGUAGE');
}

/**
 * 初始化 所有菜单的叶子节点
 * @param {Object} menu
 * @param {Object[]} queryMenus
 */
function getMenuNodeList(menu, queryMenus = []) {
  for (let i = 0; i < menu.length; i++) {
    if (isUndefined(menu[i].children)) {
      queryMenus.push({ ...menu[i], title: menu[i].name && intl.get(menu[i].name) });
    } else {
      getMenuNodeList(menu[i].children, queryMenus);
    }
  }
  return queryMenus;
}

// 存储 加载了哪些多语言, 还会存储 hzero-ui 的多语言
const localLangCache = {};

function getGlobalModalConfig({ app, getWrapperRouterData = (e) => e }) {
  return {
    namespace: 'global',

    state: {
      collapsed: false,
      menu: [],
      language: '', // 当前语言
      // language: 'zh_CN',
      hzeroUILocale: {}, // 组件的国际化
      supportLanguage: [],
      routerData: {},
      notices: [],
      announces: [],
      layoutLoading: false,
      count: 0,
      menuLeafNode: [],
      tabs: [],
      activeTabKey: '/workplace',
      traceLogInfo: {}, // trace 数据
      traceStatus: false, // trace 状态
      traceGroupId: undefined, // trace id
      tabsIsInit: false, // menuTabs 初始化完成
      menuLoad: false, // 菜单加载完毕
      activeTabMenuId: undefined,
      refreshMenuTabsKeyMap: new Map(), //  DefaultMenuTabs组件用于渲染tab的key
      marketConfig: {}, // 应用市场配置
    },

    effects: {
      /**
       * 首屏 预加载内容
       * 初始化语言和菜单数据
       */ *init({ payload: { organizationId, language } }, { call, put, all }) {
        // 第一次加载肯定是没有切换语言的
        localLangCache[language] = {};
        const promiseAllLocale = [
          call(queryPromptLocale, organizationId, language, 'hzero.common'),
        ];
        const [promptLocale] = yield all(promiseAllLocale);
        const safePromptLocale = getResponse(promptLocale);
        const loadLocales = (intl && intl.options && intl.options.locales) || {}; // 设置或切换 当前intl的语言
        intl.init({
          currentLocale: language,
          locales: loadLocales,
          warningHandler: (/* e */) => {
            // todo
            // console.warn(e);
          },
        });
        intl.load({
          [language]: safePromptLocale,
        });
        yield put({
          type: 'loadUILocale',
          payload: {
            language,
          },
        });

        // 初始化 menuTabs
        yield put({ type: 'initMenuTabs' });
        // tab 初始化完成后才进入 menuTab 处理流程
        tabListen();
      },
      *lazyInit({ payload: { organizationId, language } }, { call, put, all }) {
        const [supportLanguage] = yield all([
          call(querySupportLanguage),
          // 获取角色列表, 角色切换用到
          put({ type: 'user/fetchRoleList', payload: { organizationId } }),
          // 获取动态表格数据
        ]);
        const safeSupportLanguage = getResponse(supportLanguage);
        const list = [];
        const menuListRes = yield call(queryMenu, { lang: language });
        const menuList = getResponse(menuListRes);
        const menuLocaleIntl = {};
        parseMenuToLocaleIntl(menuList, menuLocaleIntl);
        parseMenuData(list, null, menuList || []);
        const menus = list;
        parseMenuToLocaleIntl(menuList, menuLocaleIntl);
        intl.load({
          [language]: menuLocaleIntl,
        });
        const menuData = getFlatMenuData(menus);
        const routerConfig = getWrapperRouterData(app || getDvaApp());
        const routerData = {};
        // The route matches the menu
        Object.keys(routerConfig).forEach((path) => {
          // Regular match item name
          // eg.  router /user/:id === /user/chen
          const pathRegexp = pathToRegexp(path);
          const menuKey = Object.keys(menuData).find((key) => pathRegexp.test(`${key}`));
          let menuItem = {};
          // If menuKey is not empty
          if (menuKey) {
            menuItem = menuData[menuKey];
          }
          let router = routerConfig[path];
          // If you need to configure complex parameter routing,
          // https://github.com/ant-design/ant-design-pro-site/blob/master/docs/router-and-nav.md#%E5%B8%A6%E5%8F%82%E6%95%B0%E7%9A%84%E8%B7%AF%E7%94%B1%E8%8F%9C%E5%8D%95
          // eg . /list/:type/user/info/:id
          router = {
            ...router,
            name: router.name || menuItem.name,
            // tab 用到的数据
            pathRegexp,
            title: router.title || menuItem.name,
            icon: router.icon || menuItem.icon,
            closable: isBoolean(router.closable) ? router.closable : true,
            path,
            // tab 用到的数据
            authority: router.authority || menuItem.authority,
            hideInBreadcrumb: router.hideInBreadcrumb || menuItem.hideInBreadcrumb,
          };
          routerData[path] = router;
        });
        const queryMenus = getMenuNodeList(menus, []);

        yield put({
          type: 'updateState',
          payload: {
            routerData,
            language,
            menu: menus,
            menuLeafNode: queryMenus,
            supportLanguage: safeSupportLanguage,
            // 菜单加载完毕
            menuLoad: true,
          },
        });
      },
      *fetchCount(_, { call, put }) {
        const data = yield call(queryCount);
        if (data && isNumber(data.unreadMessageCount)) {
          const { unreadMessageCount } = data;
          yield put({
            type: 'saveNotices',
            payload: { count: unreadMessageCount },
          });
        }
      },
      *changeLanguage({ payload }, { put }) {
        const language = payload;
        const organizationId = getCurrentOrganizationId();
        const roleId = getCurrentRole().id;
        yield put({
          type: 'updateLocale',
          payload: {
            language,
            roleId,
            organizationId,
          },
        });
      },
      /**
       * 更新国际化
       * antdLocale, C7nUILocale, commonLocale, menuLocale
       */ *updateLocale({ payload }, { call, put, all }) {
        // 角色id是必须的
        const { language, organizationId } = payload;
        if (!localLangCache[language]) {
          localLangCache[language] = {};
          const [promptLocale, menuData] = yield all([
            call(queryPromptLocale, organizationId, language, 'hzero.common'),
            call(queryMenu, { lang: language }),
          ]);
          const safePromptLocale = getResponse(promptLocale);
          const safeMenuData = getResponse(menuData);
          const safeMenuLocale = {};
          parseMenuToLocaleIntl(safeMenuData, safeMenuLocale);

          // 设置或切换 当前intl的语言
          const loadLocales = (intl && intl.options && intl.options.locales) || {};
          intl.init({
            currentLocale: language,
            locales: loadLocales,
            warningHandler: (/* e */) => {
              // todo
              // console.warn(e);
            },
          });

          intl.load({
            [language]: { ...safePromptLocale, ...safeMenuLocale },
          });
          yield put({
            type: 'updateMenuLeafNode',
          });
          yield put({
            type: 'updateState',
            payload: {
              language,
            },
          });
        } else {
          // 设置或切换 当前intl的语言
          const loadLocales = (intl && intl.options && intl.options.locales) || {};
          intl.init({
            currentLocale: language,
            locales: loadLocales,
            warningHandler: (/* e */) => {
              // todo
              // console.warn(e);
            },
          });

          const updateState = { language };
          yield put({
            type: 'updateMenuLeafNode',
          });
          yield put({
            type: 'updateState',
            payload: updateState,
          });
        }
        yield put({
          type: 'loadUILocale',
          payload: {
            language,
          },
        });
      },

      /**
       * 加载 UI 的多语言
       */ *loadUILocale({ payload: { language, organizationId } }, { call, put, all }) {
        let c7nLocale;
        let c7nProLocale;
        let hzeroUILocale;
        if (localLangCache[language] && localLangCache[language].c7nLocale) {
          [c7nLocale, c7nProLocale, hzeroUILocale] = [
            localLangCache[language].c7nLocale,
            localLangCache[language].c7nProLocale,
            localLangCache[language].hzeroUILocale,
          ];
        } else {
          const promiseAllLocale = [
            call(getC7nLocale, language, organizationId),
            call(getC7nProLocale, language, organizationId),
            call(getHzeroUILocale, language, organizationId),
          ];
          [c7nLocale, c7nProLocale, hzeroUILocale] = yield all(promiseAllLocale);
          localLangCache[language].c7nLocale = c7nLocale;
          localLangCache[language].c7nProLocale = c7nProLocale;
          localLangCache[language].hzeroUILocale = hzeroUILocale;
        }
        localeContext.setLocale(resolveRequire(c7nProLocale));
        if (hzeroUILocale) {
          try {
            const momentLocale = getMomentLocale(hzeroUILocale.locale.replace('_', '-'));
            if (momentLocale === 'en_US') {
              moment.locale();
            } else {
              import(`moment/locale/${momentLocale}.js`);
              // 获取moment语言
              moment.locale(momentLocale);
            }
          } catch (e) {
            console.error(e);
          }
          // 保证一定有antd的语言，没有可能是调用了多次 updateLocale
        }
        yield put({
          type: 'updateState',
          payload: {
            c7nLocale,
            hzeroUILocale,
          },
        });
      },

      *updateDefaultLanguage({ payload }, { call }) {
        const res = yield call(updateDefaultLanguage, payload);
        return getResponse(res);
      },

      *clearNotices({ payload }, { put, select }) {
        yield put({
          type: 'saveClearedNotices',
          payload,
        });
        const count = yield select((state) => state.global.notices.length);
        yield put({
          type: 'user/changeNotifyCount',
          payload: count,
        });
      },
      *removeTab({ payload }, { put, select }) {
        const state = yield select((st) => st.global);
        const { tabs, activeTabKey } = state;
        let activeKey = activeTabKey;
        let lastIndex;
        tabs.forEach((pane, i) => {
          if (pane.key === payload) {
            lastIndex = i - 1;
          }
        });
        const panes = tabs.filter((pane) => pane.key !== payload);
        if (lastIndex >= 0 && activeTabKey === payload) {
          activeKey = panes[lastIndex].key;
        }
        const activeTabMenuId = getActiveTabMenuId(activeKey);
        yield put({
          type: 'updateState',
          payload: {
            // openTab 会更新 activeTabKey, 并且需要用到之前的 activeTabKey 来判断需不需要 push。
            // activeTabKey: activeKey,
            tabs: [...panes],
            activeTabMenuId: activeTabMenuId.id,
          },
        });
        return activeKey;
      },
      // 关闭其他tab 返回下一个激活的 activeKey
      *removeOtherMenuTab({ payload }, { put, select }) {
        const state = yield select((st) => st.global);
        const { tabs } = state;
        const { tab } = payload;
        const activeTabMenuId = getActiveTabMenuId(tab.key);
        yield put({
          type: 'updateState',
          payload: {
            // openTab 会更新 activeTabKey, 并且需要用到之前的 activeTabKey 来判断需不需要 push。
            // activeTabKey: tab.key,
            tabs: tabs.filter((t) => t.key === tab.key || !t.closable),
            activeTabMenuId: activeTabMenuId.id,
          },
        });
        return tab.key;
      },
      // 关闭其他tab 返回下一个激活的 activeKey
      *removeSomeMenuTab({ payload }, { put, select }) {
        const state = yield select((st) => st.global);
        const { tabs } = state;
        const { removeTabs } = payload;
        yield put({
          type: 'updateState',
          payload: {
            tabs: tabs.filter((t) => !removeTabs.includes(t.key) || !t.closable),
          },
        });
      },
      // 关闭所有tab 返回下一个激活的 activeKey
      *removeAllMenuTab({ payload }, { put, select }) {
        const state = yield select((st) => st.global);
        const { tabs } = state;
        const { tab } = payload;
        let closestTab1; // 记录遍历过程中的 不可关闭的Tab
        let closestTab2; // 记录左距离最近的 不可关闭的Tab
        const nextTabs = tabs.filter((t) => {
          if (t.key === tab.key) {
            if (!t.closable) {
              // 如果该Tab不可关闭, 那么该Tab为下一个打开的Tab
              closestTab2 = t;
            } else {
              // 如果该Tab可关闭, 那么新的Tab为之前最近的不可关闭的Tab
              closestTab2 = closestTab1;
            }
          }
          if (t.closable) {
            return false;
          }
          closestTab1 = t;
          return true;
        });
        const activeTabMenuId = getActiveTabMenuId(closestTab2 || closestTab1 || { key: '/' }.key);
        yield put({
          type: 'updateState',
          payload: {
            // 至少会有工作台tab 所有不对 nextTabs 做空判断
            // openTab 会更新 activeTabKey, 并且需要用到之前的 activeTabKey 来判断需不需要 push。
            // activeTabKey: closestTab2.key,
            activeTabMenuId: activeTabMenuId.id,
            tabs: nextTabs,
          },
        });
        // 如果没有固定tab, 则将
        return (closestTab2 || closestTab1 || { key: '/' }).key;
      },
      *getRemoveTabInfo({ payload }, { select }) {
        const state = yield select((st) => st.global);
        const { tabs, activeTabKey } = state;
        const closeTabKey = payload;
        const removeTabInfo = {
          nextTabs: [],
          nextActiveTabKey: '',
        };
        let isNextTabKeyNeedSet = activeTabKey === closeTabKey;
        forEach(tabs, (tab) => {
          if (tab.key !== closeTabKey) {
            removeTabInfo.nextTabs.push(tab);
            if (isNextTabKeyNeedSet) {
              removeTabInfo.nextActiveTabKey = tab.key;
              removeTabInfo.nextTab = tab;
            }
          } else {
            isNextTabKeyNeedSet = false;
          }
        });
        return removeTabInfo;
      },
      // 查询语言值集
      *querySupportLanguage(_, { call, put }) {
        const supportLanguage = getResponse(yield call(querySupportLanguage));
        if (supportLanguage) {
          yield put({
            type: 'updateState',
            payload: { supportLanguage },
          });
        }
      },
      /**
       * pubLayout 预加载
       * 首屏 预加载内容
       * 初始化语言和菜单数据
       */ *pubInit({ payload: { language, organizationId } }, { call, put, all }) {
        const supportLanguage = getResponse(yield call(querySupportLanguage));
        if (supportLanguage) {
          yield put({
            type: 'updateState',
            payload: { supportLanguage },
          });
        }
        // 第一次加载肯定是没有切换语言的
        localLangCache[language] = {};
        const promiseAllLocale = [
          call(queryPromptLocale, organizationId, language, 'hzero.common'),
        ];
        const [promptLocale] = yield all(promiseAllLocale);
        const safePromptLocale = getResponse(promptLocale);
        // 设置或切换 当前intl的语言
        const loadLocales = (intl && intl.options && intl.options.locales) || {};
        intl.init({
          currentLocale: language,
          locales: loadLocales,
          warningHandler: (/* e */) => {
            // todo
            // console.warn(e);
          },
        });
        intl.load({
          [language]: safePromptLocale,
        });
        yield put({
          type: 'loadUILocale',
          payload: {
            language,
          },
        });
      },
      /**
       * 用于 pubLayout 的懒加载
       */ *pubLazyInit({ payload: { language } }, { put }) {
        // 获取动态表格数据
        const routerConfig = getWrapperRouterData(app || getDvaApp());
        const routerData = {};
        // The route matches the menu
        Object.keys(routerConfig).forEach((path) => {
          // Regular match item name
          // eg.  router /user/:id === /user/chen
          const pathRegexp = pathToRegexp(path);
          let router = routerConfig[path];
          // If you need to configure complex parameter routing,
          // https://github.com/ant-design/ant-design-pro-site/blob/master/docs/router-and-nav.md#%E5%B8%A6%E5%8F%82%E6%95%B0%E7%9A%84%E8%B7%AF%E7%94%B1%E8%8F%9C%E5%8D%95
          // eg . /list/:type/user/info/:id
          router = {
            ...router,
            name: router.name,
            // tab 用到的数据
            pathRegexp,
            title: router.title,
            icon: router.icon,
            closable: isBoolean(router.closable) ? router.closable : true,
            path,
            // tab 用到的数据
            authority: router.authority,
          };
          routerData[path] = router;
        });
        // 初始化 menuTabs
        yield put({ type: 'initMenuTabs' });

        // tab 初始化完成后才进入 menuTab 处理流程
        tabListen();

        yield put({
          type: 'updateState',
          payload: {
            routerData,
            language,
            // TabListen 监听
            menuLoad: true,
          },
        });
      },

      /**
       * 用于 publicLayout 的懒加载
       */ *publicLazyInit(_, { put }) {
        // 获取动态表格数据
        const routerConfig = getWrapperRouterData(app || getDvaApp());
        const routerData = {};
        // The route matches the menu
        Object.keys(routerConfig).forEach((path) => {
          // Regular match item name
          // eg.  router /user/:id === /user/chen
          const pathRegexp = pathToRegexp(path);
          let router = routerConfig[path];
          // If you need to configure complex parameter routing,
          // https://github.com/ant-design/ant-design-pro-site/blob/master/docs/router-and-nav.md#%E5%B8%A6%E5%8F%82%E6%95%B0%E7%9A%84%E8%B7%AF%E7%94%B1%E8%8F%9C%E5%8D%95
          // eg . /list/:type/user/info/:id
          router = {
            ...router,
            name: router.name,
            // tab 用到的数据
            pathRegexp,
            title: router.title,
            icon: router.icon,
            closable: isBoolean(router.closable) ? router.closable : true,
            path,
            // tab 用到的数据
            authority: router.authority,
          };
          routerData[path] = router;
        });
        // 初始化 menuTabs
        yield put({ type: 'initMenuTabs' });
        yield put({
          type: 'updateState',
          payload: {
            routerData,
            // TabListen 监听
            menuLoad: true,
            tabsIsInit: true,
          },
        });
      },

      *publicLayoutLanguage({ payload: { language, organizationId } }, { call, put }) {
        localLangCache[language] = {};
        const commonPrompt = getResponse(
          yield call(queryPublicPromptLocale, language, 'hzero.common')
        );
        const loadLocales = (intl && intl.options && intl.options.locales) || {}; // 设置或切换 当前intl的语言
        intl.init({
          currentLocale: language,
          locales: loadLocales,
        });
        intl.load({
          [language]: commonPrompt,
        });
        yield put({
          type: 'loadUILocale',
          payload: {
            language,
            organizationId,
          },
        });
        yield put({
          type: 'updateState',
          payload: {
            language,
          },
        });
      },

      /**
       * 用于 privateLayout 的懒加载
       */ *privateLazyInit({ payload: { language, organizationId } }, { call, put, all }) {
        // 获取动态表格数据
        const routerConfig = getWrapperRouterData(app || getDvaApp());
        const routerData = {};
        Object.keys(routerConfig).forEach((path) => {
          const pathRegexp = pathToRegexp(path);
          let router = routerConfig[path];
          router = {
            ...router,
            name: router.name,
            // tab 用到的数据
            pathRegexp,
            title: router.title,
            icon: router.icon,
            closable: isBoolean(router.closable) ? router.closable : true,
            path,
            // tab 用到的数据
            authority: router.authority,
          };
          routerData[path] = router;
        });
        // 加载国际化
        // 第一次加载肯定是没有切换语言的
        localLangCache[language] = {};
        const promiseAllLocale = [
          call(queryPromptLocale, organizationId, language, 'hzero.common'),
        ];
        const [promptLocale] = yield all(promiseAllLocale);
        const safePromptLocale = getResponse(promptLocale);
        // 设置或切换 当前intl的语言
        const loadLocales = (intl && intl.options && intl.options.locales) || {};
        intl.init({
          currentLocale: language,
          locales: loadLocales,
          warningHandler: (/* e */) => {
            // todo
            // console.warn(e);
          },
        });
        intl.load({
          [language]: safePromptLocale,
        });
        // 初始化 menuTabs
        yield put({
          type: 'loadUILocale',
          payload: {
            language,
          },
        });

        const supportLanguage = getResponse(yield call(querySupportLanguage));
        yield put({ type: 'initMenuTabs' });

        yield put({
          type: 'updateState',
          payload: {
            routerData,
            language,
            supportLanguage,
            // TabListen 监听
            menuLoad: true,
            tabsIsInit: true,
          },
        });
      },

      /**
       * 首屏 预加载内容
       * 初始化语言和菜单数据
       */ *baseInit({ payload: { language, organizationId } }, { call, put, all }) {
        // 第一次加载肯定是没有切换语言的
        localLangCache[language] = {};
        const promiseAllLocale = [
          call(queryPromptLocale, organizationId, language, 'hzero.common'),
        ];
        const [promptLocale] = yield all(promiseAllLocale);
        const safePromptLocale = getResponse(promptLocale);
        // 设置或切换 当前intl的语言
        const loadLocales = (intl && intl.options && intl.options.locales) || {};
        intl.init({
          currentLocale: language,
          locales: loadLocales,
          warningHandler: (/* e */) => {
            // todo
            // console.warn(e);
          },
        });
        intl.load({
          [language]: safePromptLocale,
        });
        // 初始化 menuTabs
        yield put({ type: 'initMenuTabs' });

        // tab 初始化完成后才进入 menuTab 处理流程
        tabListen();
        yield put({
          type: 'loadUILocale',
          payload: {
            language,
          },
        });
      },
      *baseLazyInit({ payload: { language } }, { call, put, all }) {
        const list = [];
        const menuListReq = call(queryMenu, { lang: language });
        // 获取动态表格数据
        const [menuListRes] = yield all([menuListReq]);
        // let lowCodeMenuDataRes = [];

        // const { hzeroFrontHlcdModelPlugin } = plugins;
        // if (hzeroFrontHlcdModelPlugin && VERSION_IS_OP) {
        //   lowCodeMenuDataRes = yield call(hzeroFrontHlcdModelPlugin.queryLowCodeMenuAll);
        // }
        // const menuList = hzeroFrontHlcdModelPlugin
        //   ? hzeroFrontHlcdModelPlugin.withLowCodeMenuData(
        //       lowCodeMenuDataRes,
        //       getResponse(menuListRes)
        //     )
        //   : getResponse(menuListRes);
        const menuList = getResponse(menuListRes);
        // TODO: 接口完成后 通过菜单来获取 菜单的国际化
        // const menuLocale = {};
        parseMenuData(list, null, menuList || []);
        const menus = list;
        const menuLocaleIntl = {};
        parseMenuToLocaleIntl(menuList, menuLocaleIntl);

        const menuData = getFlatMenuData(menus);
        intl.load({
          [language]: menuLocaleIntl,
        });
        const routerConfig = getWrapperRouterData(app || getDvaApp());
        const routerData = {};
        // The route matches the menu
        Object.keys(routerConfig).forEach((path) => {
          // Regular match item name
          // eg.  router /user/:id === /user/chen
          const pathRegexp = pathToRegexp(path);
          const menuKey = Object.keys(menuData).find((key) => pathRegexp.test(`${key}`));
          let menuItem = {};
          // If menuKey is not empty
          if (menuKey) {
            menuItem = menuData[menuKey];
          }
          let router = routerConfig[path];
          // If you need to configure complex parameter routing,
          // https://github.com/ant-design/ant-design-pro-site/blob/master/docs/router-and-nav.md#%E5%B8%A6%E5%8F%82%E6%95%B0%E7%9A%84%E8%B7%AF%E7%94%B1%E8%8F%9C%E5%8D%95
          // eg . /list/:type/user/info/:id
          router = {
            ...router,
            name: router.name || menuItem.name,
            // tab 用到的数据
            pathRegexp,
            title: router.title || menuItem.name,
            icon: router.icon || menuItem.icon,
            closable: isBoolean(router.closable) ? router.closable : true,
            path,
            // tab 用到的数据
            authority: router.authority || menuItem.authority,
            hideInBreadcrumb: router.hideInBreadcrumb || menuItem.hideInBreadcrumb,
          };
          routerData[path] = router;
        });
        const queryMenus = getMenuNodeList(menus, []);
        yield put({
          type: 'updateState',
          payload: {
            routerData,
            language,
            menu: menus,
            menuLeafNode: queryMenus,
            // 菜单加载完毕
            menuLoad: true,
            // lowCodeMenuDataRes,
          },
        });
      },

      // 获取 trace 状态
      *getTraceStatus(_, { call, put }) {
        const res = getResponse(yield call(getTraceStatus));
        if (res) {
          yield put({
            type: 'updateState',
            payload: { traceStatus: res },
          });
        }
        return res;
      },

      // trace 相关
      *startTrace(_, { call }) {
        return getResponse(yield call(startTrace));
      },

      *endTrace(_, { call, put }) {
        const res = getResponse(yield call(endTrace));
        if (res) {
          yield put({
            type: 'updateState',
            payload: {
              traceLogInfo: res,
            },
          });
        }
        return res;
      },
      *checkEntryDirectory({ payload }, { call, put }) {
        const entryList = yield call(checkEntryDirectory, payload);
        const response = getResponse(entryList);
        yield put({
          type: 'updateState',
          payload: { entryList: response },
        });
        return response;
      },
    },
    reducers: {
      updateState(state, { payload }) {
        return {
          ...state,
          ...payload,
        };
      },
      changeLayoutCollapsed(state, { payload }) {
        return {
          ...state,
          collapsed: payload,
        };
      },
      saveNotices(state, { payload }) {
        return {
          ...state,
          ...payload,
        };
      },
      saveAnnounces(state, { payload }) {
        return {
          ...state,
          ...payload,
        };
      },
      saveClearedNotices(state, { payload }) {
        return {
          ...state,
          notices: state.notices.filter((item) => item.type !== payload),
        };
      },
      addTab(state, { payload }) {
        const { newTab } = payload;
        const { tabs } = state;
        const activeTabMenuId = getActiveTabMenuId(newTab.key);
        return {
          ...state,
          activeTabKey: newTab.key,
          activeTabMenuId: activeTabMenuId.id,
          tabs: [...tabs, newTab],
        };
      },
      /**
       * 使用新的 tab 替换掉原先的tab
       * @param {Object} state - 之前的state
       * @param {Object} payload
       * @param {Object} payload.tab 需要被替换的tab
       * @param {String!} payload.tab.key 需要被替换的tab的key
       * @param {Object!} payload.newTab 新的tab
       */
      replaceTab(state, { payload }) {
        const { tab, newTab } = payload;
        const { tabs } = state;
        const newTabs = map(tabs, (lTab) => {
          if (lTab.key === tab.key) {
            return newTab;
          }
          return lTab;
        });
        const activeTabMenuId = getActiveTabMenuId(newTab.key);
        return {
          ...state,
          activeTabKey: newTab.key,
          activeTabMenuId: activeTabMenuId.id,
          tabs: newTabs,
        };
      },
      /**
       *
       * 更新 tabs 中对应key的tab, 不激活更新的tab
       * @param {Object} state - 之前的state
       * @param {Object} updateTab - 更新的 tab patch
       * @param {Object} updateTab.key - 更新的 tab 的key
       * @returns {{activeTabKey: *, tabs: *}}
       */
      updateTab(state, { payload: updateTab }) {
        const { tabs } = state;
        const newTabs = map(tabs, (lTab) => {
          if (lTab.key === updateTab.key) {
            return {
              ...lTab,
              ...updateTab,
            };
          }
          return lTab;
        });
        return {
          ...state,
          tabs: newTabs,
        };
      },
      cleanTabs(state) {
        const activeTabMenuId = getActiveTabMenuId(getInitialActiveTabKey());
        return {
          ...state,
          tabs: getInitialTabData(),
          activeTabKey: getInitialActiveTabKey(),
          activeTabMenuId: activeTabMenuId.id,
        };
      },
      updateMenuLeafNode(state) {
        return {
          ...state,
          menuLeafNode: state.menuLeafNode.map((menu) => ({
            ...menu,
            title: menu.name && intl.get(menu.name).d(menu.name),
          })),
        };
      },
      // 初始化 menuTabs
      initMenuTabs(state) {
        const tabs = getInitialTabData();
        const activeTabKey = getInitialActiveTabKey();
        return {
          ...state,
          activeTabKey: state.tabsIsInit ? state.activeTabKey : activeTabKey,
          tabs: state.tabsIsInit ? state.tabs : tabs,
          tabsIsInit: true,
        };
      },
      // 初始化 menuTabs
      initActiveTabMenuId(state) {
        const activeTabMenuId = getActiveTabMenuId(state.activeTabKey || getInitialActiveTabKey());
        return {
          ...state,
          activeTabMenuId: activeTabMenuId.id,
        };
      },
      hideLayoutPageLoading(state) {
        return {
          ...state,
          layoutLoading: false,
        };
      },
      showLayoutPageLoading(state) {
        return {
          ...state,
          layoutLoading: true,
        };
      },
      // 刷新DefaultMenuTabs组件渲染的tabKey
      updateRefreshMenuTabsKeyMap(state, { payload }) {
        return {
          ...state,
          ...payload,
        };
      },
      updateMarketConfig(state, { payload }) {
        const { feedbackFlag } = payload;
        return {
          ...state,
          feedbackFlag,
        };
      },
    },
    subscriptions: {
      setup({ history }) {
        // Subscribe history(url) change, trigger `load` action if pathname is `/`
        const unListen = history.listen(({ pathname }) => {
          tabListen(pathname);
        });
        return () => {
          unListen();
          persistMenuTabs();
        };
      },
    },
  };
}

export { getGlobalModalConfig };

export default getGlobalModalConfig({ getWrapperRouterData: getRouterData });
