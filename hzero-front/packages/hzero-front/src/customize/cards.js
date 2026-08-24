import { isFunction } from 'lodash';

import { mapCustomize } from 'utils/customize';

/**
 * 如果 卡片存在 则加载卡片
 * 否则 返回 null
 * @param cardCode
 * @return {Promise<null|*>}
 */
export async function loadCardAsync(cardCode) {
  if (mapCustomize.has({ module: 'hzero-front', feature: 'cards', key: cardCode })) {
    const layout = mapCustomize.get({ module: 'hzero-front', feature: 'cards', key: cardCode });
    if (isFunction(layout && layout.component)) {
      const card = await layout.component();
      return card;
    }
  }
  return null;
}

export function setCard(cardConfig) {
  // TODO: 判断是否需要检查 重复设置的问题
  mapCustomize.set({
    module: 'hzero-front',
    feature: 'cards',
    key: cardConfig.code,
    data: { component: cardConfig.component },
  });
}
