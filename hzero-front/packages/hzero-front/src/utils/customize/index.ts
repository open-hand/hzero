/**
 * customize - 客制化 配置
 * TODO: config.lock - 一些数据 是不能修改的
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019/9/18
 * @copyright 2019/9/18 © HAND
 */

import EventEmitter, { Emitter, EventListener } from 'event-emitter';
import { Bind } from 'lodash-decorators';

class GlobalMapStore {
  private moduleMapStoreMap: Map<string, Map<string, Map<string, any>>>;

  private eventManager: Emitter;

  constructor() {
    this.moduleMapStoreMap = new Map();
    this.eventManager = EventEmitter();
  }

  emit(type: string, ...args: any[]) {
    return this.eventManager.emit(type, ...args);
  }

  on(type: string, listener: EventListener) {
    return this.eventManager.on(type, listener);
  }

  once(type: string, listener: EventListener) {
    return this.eventManager.once(type, listener);
  }

  off(type: string, listener: EventListener) {
    return this.eventManager.off(type, listener);
  }

  /**
   * 清空对应的 数据
   * @param {String} module - 模块名
   * @param {String} feature - 功能名
   * @param {any} [...config] - 预留配置
   */
  @Bind()
  public clear({ module, feature }): void {
    const featureStore = this.moduleMapStoreMap.get(module);
    if (featureStore) {
      const featureMap = featureStore.get(feature);
      if (featureMap) {
        featureMap.clear();
      }
    }
  }

  /**
   * 删除对应 storeMap key 的 entry
   * 删除成功 返回 true
   * 未设置 返回 false
   * @param {String} module - 模块名
   * @param {String} feature - 功能名
   * @param {String} key - map 的 key
   * @param {any} [...config] - 预留配置
   */
  @Bind()
  public delete({ module, feature, key }): boolean {
    const featureStore = this.moduleMapStoreMap.get(module);
    if (featureStore) {
      const storeMap = featureStore.get(feature);
      if (storeMap) {
        return storeMap.delete(key);
      }
    }
    return false;
  }

  /**
   * 返回 对应 数据的 entries
   * @param {String} module - 模块名
   * @param {String} feature - 功能名
   * @param {any} [...config] - 预留配置
   */
  @Bind()
  public entries({ module, feature }) {
    let mapStore;
    let featureStore = this.moduleMapStoreMap.get(module);
    if (featureStore) {
      mapStore = featureStore.get(feature);
      if (!mapStore) {
        mapStore = new Map();
        featureStore.set(feature, mapStore);
      }
    } else {
      featureStore = new Map();
      this.moduleMapStoreMap.set(module, featureStore);
      mapStore = new Map();
      featureStore.set(feature, mapStore);
    }
    return mapStore.entries();
  }

  /**
   * 遍历对应的 数据
   * @param {String} module - 模块名
   * @param {String} feature - 功能名
   * @param {Function} fn - 遍历方法 (value, key, map) => void
   * @param {?any} thisArg - 遍历方法对应的 this
   * @param {any} [...config] - 预留配置
   */
  @Bind()
  public forEach({ module, feature, fn, thisArg }) {
    const featureStore = this.moduleMapStoreMap.get(module);
    if (featureStore) {
      const featureMap = featureStore.get(feature);
      if (featureMap) {
        featureMap.forEach(fn, thisArg);
      }
    }
  }

  /**
   * 获取数据
   * 获取到 返回 数据
   * 否则 返回 undefined
   * @param {String} module - 模块名
   * @param {String} feature - 功能名
   * @param {String} key - map 的 key
   * @param {any} [...config] - 预留配置
   * @return {any|undefined}
   */
  @Bind()
  public get({ module, feature, key }) {
    const featureStore = this.moduleMapStoreMap.get(module);
    if (featureStore) {
      const featureMap = featureStore.get(feature);
      if (featureMap) {
        if (featureMap.has(key)) {
          return featureMap.get(key);
        }
      }
    }
    return undefined;
  }

  /**
   * 是否存在 数据
   * 存在 返回 true
   * 不存在/未设置 返回 false
   * @param {String} module - 模块名
   * @param {String} feature - 功能名
   * @param {String} key - map 的 key
   * @param {any} [...config] - 预留配置
   */
  @Bind()
  public has({ module, feature, key }) {
    const featureStore = this.moduleMapStoreMap.get(module);
    if (featureStore) {
      const featureMap = featureStore.get(feature);
      if (featureMap) {
        return featureMap.has(key);
      }
    }
    return false;
  }

  /**
   * 返回 对应 数据的 keys
   * @param {String} module - 模块名
   * @param {String} feature - 功能名
   * @param {any} [...config] - 预留配置
   */
  @Bind()
  public keys({ module, feature }) {
    let featureMap;
    let featureStore = this.moduleMapStoreMap.get(module);
    if (featureStore) {
      featureMap = featureStore.get(feature);
      if (!featureMap) {
        featureMap = new Map();
        featureStore.set(feature, featureMap);
      }
    } else {
      featureStore = new Map();
      this.moduleMapStoreMap.set(module, featureStore);
      featureMap = new Map();
      featureStore.set(feature, featureMap);
    }
    return featureMap.keys();
  }

  /**
   * 设置 entry 数据
   * @param {String} module - 模块名
   * @param {String} feature - 功能名
   * @param {String} key - map 的 key
   * @param {any} data - map key 对应的 value
   * @param {any} [...config] - 预留配置
   */
  @Bind()
  public set({ module, feature, key, data }) {
    let featureMap;
    let featureStore = this.moduleMapStoreMap.get(module);
    if (featureStore) {
      featureMap = featureStore.get(feature);
      if (!featureMap) {
        featureMap = new Map();
        featureStore.set(feature, featureMap);
      }
    } else {
      featureStore = new Map();
      this.moduleMapStoreMap.set(module, featureStore);
      featureMap = new Map();
      featureStore.set(feature, featureMap);
    }
    featureMap.set(key, data);
    this.emit('set', { module, feature, key, data });
    return this;
  }

  /**
   * 返回 对应 数据的 values
   * @param {String} module - 模块名
   * @param {String} feature - 功能名
   * @param {any} [...config] - 预留配置
   */
  @Bind()
  public values({ module, feature }) {
    let featureMap;
    let featureStore = this.moduleMapStoreMap.get(module);
    if (featureStore) {
      if (!featureMap) {
        featureMap = new Map();
        featureStore.set(feature, featureMap);
      }
    } else {
      featureStore = new Map();
      this.moduleMapStoreMap.set(module, featureStore);
      featureMap = new Map();
      featureStore.set(feature, featureMap);
    }
    return featureMap.values();
  }

  /**
   * 获取对应数据的 @@iterator
   * @param {String} module - 模块名
   * @param {String} feature - 功能名
   * @param {any} [...config] - 预留配置
   */
  public [Symbol.iterator] = ({ module, feature }) => {
    let featureMap;
    let featureStore = this.moduleMapStoreMap.get(module);
    if (featureStore) {
      featureMap = featureStore.get(feature);
      if (!featureMap) {
        featureMap = new Map();
        featureStore.set(feature, featureMap);
      }
    } else {
      featureStore = new Map();
      this.moduleMapStoreMap.set(module, featureStore);
      featureMap = new Map();
      featureStore.set(feature, featureMap);
    }
    return featureMap[Symbol.iterator]();
  };

  /**
   * 获取对应数据的 数量
   * 未设置 返回 0
   * 已设置 返回 数据 数量
   * @param {String} module - 模块名
   * @param {String} feature - 功能名
   * @param {any} [...config] - 预留配置
   */
  @Bind()
  public size({ module, feature }) {
    const featureStore = this.moduleMapStoreMap.get(module);
    if (featureStore) {
      const featureMap = featureStore.get(feature);
      if (featureMap) {
        return featureMap.size;
      }
    }
    return 0;
  }
}

const GLOGAL_CUSTOMIZE_STORE_KEY = 'GLOGAL_CUSTOMIZE_STORE_KEY';

const getSingleInstanceMapCustomize = (): GlobalMapStore => {
  let singleInstance = window[GLOGAL_CUSTOMIZE_STORE_KEY];
  if (!singleInstance) {
    singleInstance = new GlobalMapStore();
    window[GLOGAL_CUSTOMIZE_STORE_KEY] = singleInstance;
  }
  return singleInstance;
};

// <globalMapStore>
/**
 * @param {String} module - 模块名
 * @param {String} feature - 功能名
 * @param {String} key - map 的 key
 * @param {any} [...config] - 预留配置
 */
// </globalMapStore>

const mapCustomize = getSingleInstanceMapCustomize();

export { mapCustomize };

export default mapCustomize;
