import { isFunction } from 'lodash';

import { mapCustomize } from 'utils/customize';

const defaultLayout = () => import('../layouts/DefaultLayout');

export function loadLayout(layoutName) {
  if (mapCustomize.has({ module: 'hzero-front', feature: 'layout', key: layoutName })) {
    const layout = mapCustomize.get({ module: 'hzero-front', feature: 'layout', key: layoutName });
    return isFunction(layout && layout.component) ? layout.component : defaultLayout;
  }
  return defaultLayout;
}

export function setLayout(layoutName, layout, cover = false) {
  if (
    !mapCustomize.has({ module: 'hzero-front', feature: 'layout', key: layoutName }) ||
    (cover && mapCustomize.has({ module: 'hzero-front', feature: 'layout', key: layoutName }))
  ) {
    mapCustomize.set({
      module: 'hzero-front',
      feature: 'layout',
      key: layoutName,
      data: { component: layout },
    });
  }
}
