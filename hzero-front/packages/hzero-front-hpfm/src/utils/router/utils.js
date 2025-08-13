import { createElement } from 'react';
import dynamic from 'dva/dynamic';

const modelNotExisted = (app = {}, model) =>
  !(app._models || []).some(({ namespace }) => {
    return namespace === model.substring(model.lastIndexOf('/') + 1);
  });
// wrapper of dynamic
export const dynamicWrapper = (app, models, component) => {
  return dynamic({
    app,
    models: () =>
      models
        .filter(model => modelNotExisted(app, model))
        .map(m => import(`../../models/${m}.js`)) || [],
    // add routerData prop
    component: () => {
      // if (!routerDataCache) {
      //   routerDataCache = getRouterData(app);
      // }
      return component().then(raw => {
        const Component = raw.default || raw;
        return props =>
          createElement(Component, {
            ...props,
            // routerData: routerDataCache,
          });
      });
    },
  });
};
