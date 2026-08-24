// @ts-ignore
import { DvaInstance } from 'dva';
// @ts-ignore
import dynamic from 'dva/dynamic';
// @ts-ignore
import { isString } from 'lodash';
import { createElement } from 'react';
import { RoutersConfig } from '../typings/IRouterConfig';

interface GetConvertRouterOptions {
  hzeroRoutes: RoutersConfig;
  getModels?: (m: string) => Promise<any> | any;
  getPages?: (c: string) => Promise<any> | any;
  options: { app: DvaInstance };
}

export function getConvertRouter({
  hzeroRoutes,
  getModels = (e) => e,
  getPages = (e) => e,
  options,
}: GetConvertRouterOptions) {
  const modelNotExisted = (app = {}, model) =>
    // eslint-disable-next-line
    !((app as any)._models || []).some(({ namespace }) => {
      return namespace === model.substring(model.lastIndexOf('/') + 1);
    });

  function unWindRouteToRouter(_hzeroRoutes, convertRoutes, ndeep = 0) {
    _hzeroRoutes.forEach((route, index) => {
      const newRoute = { ...route };
      // delete newRoute.components;

      if (ndeep === 1) {
        newRoute.index = index === 0;
      }

      assignRouterData(newRoute, route);
      // eslint-disable-next-line no-param-reassign
      convertRoutes[route.path] = newRoute;
      // 子路由
      if (route.components !== undefined) {
        unWindRouteToRouter(route.components, convertRoutes, ndeep + 1);
        newRoute.notTrueRoute = true;
      }
    });
  }

  function assignRouterData(router, route) {
    const { app } = options;
    const models = route.models === undefined ? [] : route.models;
    if (route.component === undefined) {
      // @ts-ignore
      // eslint-disable-next-line no-param-reassign
      router.component = require('../routes').default;
    } else {
      const getRouterData = isString(route.component)
        ? () => getPages(route.component)
        : route.component;
      switch (route.type) {
        case 'vue':
        case 'react':
        default:
          // eslint-disable-next-line no-param-reassign
          router.component = dynamicWrapper(app, models, getRouterData);
          break;
      }
    }
    if (route.key !== undefined) {
      // eslint-disable-next-line no-param-reassign
      router.key = route.key;
    }
    if (route.authorized !== undefined) {
      // eslint-disable-next-line no-param-reassign
      router.authorized = route.authorized;
    }
    if (route.title !== undefined) {
      // eslint-disable-next-line no-param-reassign
      router.title = route.title;
    }
    if (route.icon !== undefined) {
      // eslint-disable-next-line no-param-reassign
      router.icon = route.icon;
    }
    // eslint-disable-next-line no-param-reassign
    router.exact = route.exact;
  }

  // wrapper of dynamic
  const dynamicWrapper = (app, _models = [], component) => {
    return (dynamic as any)({
      app,
      models: () =>
        _models
          .filter((item) => {
            if (isString(item) && !modelNotExisted(app, item)) {
              return false;
            } else {
              return true;
            }
          })
          .map((item: any) => {
            // eslint-disable-next-line
            return isString(item) ? getModels(item) : typeof item === 'function' ? item() : item;
          }),
      // add routerData prop
      component: () => {
        // if (!routerDataCache) {
        //   routerDataCache = getRouterData(app);
        // }
        return component().then((raw) => {
          const Component = raw.default || raw;
          return (props) =>
            createElement(Component, {
              ...props,
              // routerData: routerDataCache,
            });
        });
      },
    });
  };

  function convertRouter() {
    const convertRoutes = {};
    unWindRouteToRouter(hzeroRoutes, convertRoutes);
    return convertRoutes;
  }

  return convertRouter;
}
