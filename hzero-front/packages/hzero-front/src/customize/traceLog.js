import { isFunction } from 'lodash';

import { mapCustomize } from 'utils/customize';

/**
 * 如果 存在 则加载
 * 否则 返回 null
 * @param traceLog
 * @return {Promise<null|*>}
 */
export async function loadTraceLogAsync(code) {
  if (mapCustomize.has({ module: 'hzero-front-hadm', feature: 'TraceLog', key: code })) {
    const layout = mapCustomize.get({ module: 'hzero-front-hadm', feature: 'TraceLog', key: code });
    if (isFunction(layout && layout.component)) {
      const traceLog = await layout.component();
      return traceLog;
    }
  }
  return null;
}

export function setTraceLog(config) {
  // TODO: 判断是否需要检查 重复设置的问题
  mapCustomize.set({
    module: 'hzero-front-hadm',
    feature: 'TraceLog',
    key: config.code,
    data: { component: config.component },
  });
}

/**
 * 监听变化
 * @param {*} listener 触发器
 */
export function watchTraceLogSetComponent(listener, initRun) {
  if (initRun) {
    listener();
  }
  const mListener = (setObj = {}) => {
    const { module, feature } = setObj;
    if (module === 'hzero-front-hadm' && feature === 'TraceLog') {
      listener(setObj);
    }
  };
  mapCustomize.on('set', mListener);
  const unListener = () => {
    mapCustomize.off('set', mListener);
  };
  return unListener;
}
