// import { createElement } from 'react';
// import dynamic from 'dva/dynamic';
// import { isString } from 'lodash';
// import routers from '../config/routers';

// const modelNotExisted = (app = {}, model) =>
//   // eslint-disable-next-line
//   !(app._models || []).some(({ namespace }) => {
//     return namespace === model.substring(model.lastIndexOf('/') + 1);
//   });

// export function convertRouter(nestRoutes, options = {}) {
//   const { app } = options;
//   const convertRoutes = {};
//   unWindRouteToRouter(nestRoutes, convertRoutes, { app });
//   return convertRoutes;
// }

// function unWindRouteToRouter(nestRoutes, convertRoutes, options = {}) {
//   const { app } = options;
//   nestRoutes.forEach(route => {
//     const newRoute = {};
//     assignRouterData(newRoute, route, { app });
//     // eslint-disable-next-line no-param-reassign
//     convertRoutes[route.path] = newRoute;
//     // 子路由
//     if (route.components !== undefined) {
//       unWindRouteToRouter(route.components, convertRoutes, options);
//     }
//   });
// }

// function assignRouterData(router, route, options = {}) {
//   const { app } = options;
//   const models = route.models === undefined ? [] : route.models;
//   if (route.component === undefined) {
//     // eslint-disable-next-line no-param-reassign
//     router.component = dynamicWrapper(app, models, () => import('../routes'));
//   } else if (isString(route.component)) {
//     // eslint-disable-next-line no-param-reassign
//     router.component = dynamicWrapper(app, models, () => import(`../routes/${route.component}`));
//   } else {
//     // eslint-disable-next-line no-param-reassign
//     router.component = route.component;
//   }
//   if (route.key !== undefined) {
//     // eslint-disable-next-line no-param-reassign
//     router.key = route.key;
//   }
//   if (route.authorized !== undefined) {
//     // eslint-disable-next-line no-param-reassign
//     router.authorized = route.authorized;
//   }
//   if (route.title !== undefined) {
//     // eslint-disable-next-line no-param-reassign
//     router.title = route.title;
//   }
//   if (route.icon !== undefined) {
//     // eslint-disable-next-line no-param-reassign
//     router.icon = route.icon;
//   }
// }

// // wrapper of dynamic
// export const dynamicWrapper = (app, models, component) =>
//   dynamic({
//     app,
//     models: () =>
//       models.filter(model => modelNotExisted(app, model)).map(m => import(`../models/${m}.js`)) ||
//       [],
//     // add routerData prop
//     component: () =>
//       // if (!routerDataCache) {
//       //   routerDataCache = getRouterData(app);
//       // }
//       component().then(raw => {
//         const Component = raw.default || raw;
//         return props =>
//           createElement(Component, {
//             ...props,
//             // routerData: routerDataCache,
//           });
//       }),
//   });

// export function getWrapperRouterData(app) {
//   return {
//     // ...getDefaultRouters(app),
//     ...convertRouter(routers || [], { app }),
//   };
// }

// export function getRouterData(app) {
//   return convertRouter(routers || [], { app });
// }
import { getConvertRouter } from 'hzero-front/lib/utils/getConvertRouter';
import routers from '../config/routers';

const convertRouter = app =>
  getConvertRouter({
    hzeroRoutes: routers,
    options: { app },
  });

export function getRouterData(app) {
  return convertRouter(app)();
}
