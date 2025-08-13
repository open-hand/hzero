import { forEach, isFunction, isRegExp } from 'lodash';
import pathToRegexp from 'path-to-regexp';

function getRelation(str1, str2) {
  if (str1 === str2) {
    console.warn('Two path are equal!'); // eslint-disable-line
  }
  const arr1 = str1.split('/');
  const arr2 = str2.split('/');
  if (arr2.every((item, index) => item === arr1[index])) {
    return 1;
  } else if (arr1.every((item, index) => item === arr2[index])) {
    return 2;
  }
  return 3;
}

function getRenderArr(routes) {
  let renderArr: any[] = [];
  renderArr.push(routes[0]);
  for (let i = 1; i < routes.length; i += 1) {
    let isAdd = false;
    // 是否包含
    isAdd = renderArr.every((item) => getRelation(item, routes[i]) === 3);
    // 去重
    renderArr = renderArr.filter((item) => getRelation(item, routes[i]) !== 1);
    if (isAdd) {
      renderArr.push(routes[i]);
    }
  }
  return renderArr;
}

/**
 * Get router routing configuration
 * { path:{name,...param}}=>Array<{name,path ...param}>
 * @param {string} path
 * @param {routerData} routerData
 */
export function getRoutes(path, routerData = {}) {
  /* FIXME: START 会选到不是直接子路由 */
  // let routes = Object.keys(routerData).filter(
  //   routePath => routePath.indexOf(path) === 0 && routePath !== path
  // );
  let routes: any[] = [];
  // 匹配到的 路由的 path。相当于 match.path
  let routerPath = '';
  // 匹配到的 路由的 path 的 开始正则。
  // 用来找到 当前 path 的路由 和 所有子路由
  let routerPathRegexpStart;

  const routerKeys = Object.keys(routerData);

  routerKeys.find((routePath) => {
    const route = routerData[routePath];
    if (route.pathRegexp.test(path)) {
      routerPath = routePath;
      routerPathRegexpStart = pathToRegexp(routerPath, [], {
        end: false,
      });
      return true;
    }
    return false;
  });

  if (isRegExp(routerPathRegexpStart)) {
    routerKeys.forEach((routePath) => {
      if (routerPath !== routePath && routerPathRegexpStart.test(routePath)) {
        routes.push(routePath);
      }
    });

    // routes = routes.sort((key1, key2) => {
    //   const route2 = routerData[key2];
    //   const route1 = routerData[key1];
    //   if (route2?.index === undefined && route1?.index === undefined) {
    //     return 0;
    //   }
    //   if (route2?.index) {
    //     return 1;
    //   }
    //   if (route1?.index) {
    //     return -1;
    //   }
    //   return 0;
    // });
  }
  /* FIXME: END */
  // Replace path to '' eg. path='user' /user/name => name
  routes = routes.map((item) => item.replace(path, ''));
  // Get the route to be rendered to remove the deep rendering
  const renderArr = getRenderArr(routes);
  // Conversion and stitching parameters
  const renderRoutes = renderArr.map((item) => {
    const exact = !routes.some((route) => route !== item && getRelation(route, item) === 1);
    return {
      exact,
      ...routerData[`${path}${item}`],
      key: `${path}${item}`,
      path: `${path}${item}`,
    };
  });
  return renderRoutes.sort((a, b) => (a.loadIndex || 999) - (b.loadIndex || 999));
}

/**
 * Get router routing configuration
 * { path:{name,...param}}=>Array<{name,path ...param}>
 * @param {String} path
 * @param {Object} [routerData={}]
 */
export function getRoutesContainsSelf(path, routerData = {}) {
  let routes: any[] = [];
  // 匹配到的 路由的 path。相当于 match.path
  let routerPath = '';
  // 匹配到的 路由的 path 的 开始正则。
  // 用来找到 当前 path 的路由 和 所有子路由
  let routerPathRegexpStart;

  const routerKeys = Object.keys(routerData);

  routerKeys.find((routePath) => {
    const route = routerData[routePath];
    if (route.pathRegexp.test(path)) {
      routerPath = routePath;
      routerPathRegexpStart = pathToRegexp(routerPath, [], {
        end: false,
      });
      return true;
    }
    return false;
  });

  if (isRegExp(routerPathRegexpStart)) {
    routerKeys.forEach((routePath) => {
      if (routerPathRegexpStart.test(routePath)) {
        if (routePath.startsWith(routerPath)) {
          const route = routerData[routePath];
          routes.push(route);
        }
      }
    });
    routes = routes.sort((item1, item2) => {
      if (item1.notTrueRoute && !item2.notTrueRoute) {
        return -1;
      } else if (!item1.notTrueRoute && item2.notTrueRoute) {
        return 1;
      }
      return 0;
    });
  }

  // 所有匹配到的路由的 path
  let routePaths: any[] = [];
  forEach(routes, (route) => {
    routePaths.push(route.path);
  });
  routePaths = routePaths.map((item) => item.replace(routerPath, ''));
  // Get the route to be rendered to remove the deep rendering
  const renderArr = getRenderArr(routePaths);
  // Conversion and stitching parameters
  const renderRoutes = renderArr.map((item) => {
    const exact = !routePaths.some((route) => route !== item && getRelation(route, item) === 1);
    return {
      exact,
      ...routerData[`${routerPath}${item}`],
      key: `${routerPath}${item}`,
      path: `${routerPath}${item}`,
    };
  });
  return renderRoutes.sort((a, b) => (a.loadIndex || 999) - (b.loadIndex || 999));
}

/**
 * getModuleRouters - 获取模块路由的方法
 * @param {!object} app - dva.app对象
 * @param {Array<Object>} [modules=[]] - 模块路由方法集合
 * @return {Object} - 返回模块路由对象集合
 */
export function getModuleRouters(app, modules: any[] = []) {
  let routers = {};
  modules.forEach((n) => {
    routers = Object.assign(routers, isFunction(n.getRouterData) ? n.getRouterData(app) || {} : {});
  });
  return routers;
}

/**
 * 获取 require('moduleName') 或者 import('moduleName') 的真正的导出对象
 * @param {object} module
 */
export function resolveRequire(module) {
  return module && module.__esModule ? module.default : module;
}
