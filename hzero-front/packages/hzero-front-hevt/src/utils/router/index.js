/*
 * @Descripttion:
 * @version: 0.0.1
 * @Author: heqiheng <qiheng.he@hand-china.com>
 * @Date: 2020-05-14 18:08:30
 * @Copyright: Copyright (c) 2020, Hand
 */
// import { isString } from 'lodash';
// import { getRouterData as getDefaultRouters } from 'utils/router';

// import routers from '../../config/routers';
// import '../../customize';

// import { dynamicWrapper } from './utils';

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
//     router.component = dynamicWrapper(app, models, () => import('../../routes'));
//   } else if (isString(route.component)) {
//     // eslint-disable-next-line no-param-reassign
//     router.component = dynamicWrapper(app, models, () => import(`../../routes/${route.component}`));
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

// export function getWrapperRouterData(app) {
//   return {
//     ...getDefaultRouters(app),
//     ...convertRouter(routers || [], { app }),
//   };
// }

// export function getRouterData(app) {
//   return convertRouter(routers || [], { app });
// }

// export { dynamicWrapper };

import { getConvertRouter } from 'hzero-front/lib/utils/getConvertRouter';
import routers from '../config/routers';

const convertRouter = (app) =>
  getConvertRouter({
    hzeroRoutes: routers,
    options: { app },
  });

export function getRouterData(app) {
  return convertRouter(app)();
}
