import { isFunction } from 'lodash';
import { EventListener } from 'event-emitter';
import { mapCustomize } from 'utils/customize';

/**
 * 如果 存在 则加载
 * 否则 返回 null
 * @param IMCode
 * @return {Promise<null|*>}
 */
export async function loadIMAsync(IMCode) {
  if (mapCustomize.has({ module: 'hzero-front-hims', feature: 'IM', key: IMCode })) {
    const layout = mapCustomize.get({ module: 'hzero-front-hims', feature: 'IM', key: IMCode });
    if (isFunction(layout && layout.component)) {
      const IM = await layout.component();
      return IM;
    }
  }
  return null;
}

export function setIM(IMConfig) {
  // TODO: 判断是否需要检查 重复设置的问题
  mapCustomize.set({
    module: 'hzero-front-hims',
    feature: 'IM',
    key: IMConfig.code,
    data: { component: IMConfig.component },
  });
}

/**
 * 监听 IM 变化
 * @param listener 监听器
 * @param initRun 第一次是否允许监听器
 */
export function watchIMSetComponent(listener: EventListener, initRun: boolean) {
  if (initRun) {
    listener();
  }
  const mListener = (setObj: any = {}) => {
    const { module, feature } = setObj;
    if (module === 'hzero-front-hims' && feature === 'IM') {
      listener(setObj);
      mapCustomize.off('set', mListener);
    }
  };
  mapCustomize.on('set', mListener);
  const unListener = () => {
    mapCustomize.off('set', mListener);
  };
  return unListener;
}
