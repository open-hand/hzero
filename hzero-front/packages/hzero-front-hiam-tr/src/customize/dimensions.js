import { isFunction } from 'lodash';

import { mapCustomize } from 'utils/customize';

/**
 * 如果 存在 则加载页面
 * 否则 返回 null
 * @param dimensionCode
 * @return {Promise<null|*>}
 */
export async function loadDimensionAsync(dimensionCode) {
  if (
    mapCustomize.has({ module: 'hzero-front-hiam-tr', feature: 'dimensions', key: dimensionCode })
  ) {
    const layout = mapCustomize.get({
      module: 'hzero-front-hiam',
      feature: 'dimensions',
      key: dimensionCode,
    });
    if (isFunction(layout && layout.component)) {
      const dimension = await layout.component();
      return dimension;
    }
  }
  return null;
}

export function setDimension(dimensionConfig) {
  // TODO: 判断是否需要检查 重复设置的问题
  mapCustomize.set({
    module: 'hzero-front-hiam-tr',
    feature: 'dimensions',
    key: dimensionConfig.code,
    data: { component: dimensionConfig.component },
  });
}

export function hasDimension(dimensionConfig) {
  // TODO: 判断是否需要检查 重复设置的问题
  return mapCustomize.has({
    module: 'hzero-front-hiam-tr',
    feature: 'dimensions',
    key: dimensionConfig.valueSource,
  });
}
