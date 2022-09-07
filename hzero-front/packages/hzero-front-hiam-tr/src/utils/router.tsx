import { getConvertRouter } from 'hzero-boot/lib/utils/getConvertRouter';
import { RoutersConfig } from 'hzero-boot/lib/typings/IRouterConfig';
// import { setCard } from 'hzero-front/lib/customize/cards';
import routers from '../config/routers';

const convertRouter = (app) =>
  getConvertRouter({
    hzeroRoutes: routers as RoutersConfig,
    // getModels: (m) => import(
    //   /* webpackExclude: /global\.(js|ts|tsx)$/ */
    //   /* webpackChunkName: "model-" */
    //   `../models/${m}.js`
    // ),
    // getPages: (c) => import(
    //   /* webpackChunkName: "page-" */
    //   /* webpackInclude: /\.(js|jsx|ts|tsx|vue)$/ */
    //   /* webpackExclude: /\.d\.ts$/ */
    //   `../pages/${c}`
    // ),
    options: { app },
  });

export function getRouterData(app) {
  return convertRouter(app)();
}
