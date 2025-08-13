import { createElement } from 'react';
import dynamic from 'dva/dynamic';

const modelNotExisted = (app = {}, model) =>
  !(app._models || []).some(
    ({ namespace }) => namespace === model.substring(model.lastIndexOf('/') + 1)
  );
// wrapper of dynamic
export const dynamicWrapper = (app, models, component) =>
  dynamic({
    app,
    models: () =>
      models
        .filter(model => modelNotExisted(app, model))
        .map(m => import(`../../models/${m}.js`)) || [],
    // add routerData prop
    component: () =>
      // if (!routerDataCache) {
      //   routerDataCache = getRouterData(app);
      // }
      component().then(raw => {
        const Component = raw.default || raw;
        return props =>
          createElement(Component, {
            ...props,
            // routerData: routerDataCache,
          });
      }),
  });
