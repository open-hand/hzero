/* eslint-disable no-underscore-dangle, no-param-reassign */

import React from 'react';
import { forEach, isObject, startsWith, isEmpty } from 'lodash';
import EventEmitter from 'event-emitter';
import { getActiveTabKey } from 'utils/menuTab';

const emitter =
  window.CacheComponentEventManager || (window.CacheComponentEventManager = new EventEmitter());

// const CLEAR_MAP = {};
const CACHE_MAP = {};
const WILL_CACHE_PRE_MAP = {};

function getWillCacheCount(cachePreKey) {
  const { [cachePreKey]: { willCacheCount } = { willCacheCount: 0 } } = WILL_CACHE_PRE_MAP;
  return willCacheCount;
}

// 当 缓存组件 didMount 时 cachePreKey 计数 +1
emitter.on('willCache', (cachePreKey) => {
  if (WILL_CACHE_PRE_MAP[cachePreKey]) {
    WILL_CACHE_PRE_MAP[cachePreKey].willCacheCount = Math.max(
      1,
      WILL_CACHE_PRE_MAP[cachePreKey].willCacheCount
    );
  } else {
    WILL_CACHE_PRE_MAP[cachePreKey] = {
      willCacheCount: 1,
    };
  }
});

// 当缓存组件 willUnmount 时 cachePreKey 计数 -1 并将 cacheKey 对应的缓存写进缓存
emitter.on('save', (cacheKey, data, cachePreKey) => {
  if (WILL_CACHE_PRE_MAP[cachePreKey]) {
    WILL_CACHE_PRE_MAP[cachePreKey].willCacheCount = Math.max(
      0,
      getWillCacheCount(cachePreKey) - 1
    );
    setCache(cacheKey, data);
  }
});

// 如果 Tab 没有关闭, 会添加一个 清除方法 在 组件触发缓存后 立即清空缓存 直至 全部清除完毕 后清除 清除方法
emitter.on('clean', (cachePreKey) => {
  if (getWillCacheCount(cachePreKey) > 0) {
    const cleanFunc = (cacheKey, _, componentCachePreKey) => {
      if (cachePreKey === componentCachePreKey) {
        delete CACHE_MAP[cacheKey];
        if (getWillCacheCount(cachePreKey) === 0) {
          emitter.off('save', cleanFunc);
        }
      }
    };
    emitter.on('save', cleanFunc);
  } else {
    cleanCacheData(cachePreKey);
  }
});

/**
 * 通过 cacheKey 删除缓存
 * @param {String} cacheKey - 缓存的key
 */
export function deleteCache(cacheKey) {
  if (cacheKey) {
    delete CACHE_MAP[cacheKey];
  }
}

function setCache(key, data) {
  CACHE_MAP[key] = data;
}

function getCache(key) {
  if (key) {
    return CACHE_MAP[key];
  }
}

/**
 * @param {!String} cachePreKey - 删除对应 缓存头的 key
 */
function cleanCacheData(cachePreKey) {
  const removeCacheKeys = [];
  // CLEAR_MAP[cachePreKey] = true;
  forEach(CACHE_MAP, (_, cacheKey) => {
    if (startsWith(cacheKey, cachePreKey)) {
      // CLEAR_MAP[cacheKey] = true;
      removeCacheKeys.push(cacheKey);
    }
  });
  forEach(removeCacheKeys, (cacheKey) => {
    delete CACHE_MAP[cacheKey];
  });
}

export function cleanCache(cachePreKey) {
  emitter.emit('clean', cachePreKey);
  // cleanCacheData(cachePreKey);
}

export default function cacheComponent({ cacheKey, cachePreKey = getActiveTabKey() } = {}) {
  return (Component) =>
    class CacheComponent extends React.Component {
      loadCache(component, callback) {
        if (cacheKey) {
          const { form } = component.props;
          const cache = getCache(cacheKey);
          if (form && cache && cache.form) {
            if (isObject(cache.form)) {
              Object.keys(cache.form).forEach((item) => form.registerField(item));
            }
            if (cache.__LOV_TEXT_FIELD__) {
              Object.keys(cache.__LOV_TEXT_FIELD__).forEach((k) => {
                const instance = form.getFieldInstance(k);
                if (instance?.state?.lov && instance?.props?.code && !instance?.props?.textField) {
                  instance.setState({ textField: cache.__LOV_TEXT_FIELD__[k] });
                }
              });
            }
            form.setFieldsValue(cache.form);
          }

          if (cache && cache.state) {
            component.setState(cache.state, () => {
              if (callback) callback();
            });
          } else if (callback) {
            callback();
          }
        }
      }

      componentWillUnmount() {
        // if(CLEAR_MAP[cacheKey]){
        //   delete CLEAR_MAP[cacheKey];
        // }else{
        if (cacheKey && this.pageComponent) {
          const { form } = this.pageComponent.props;
          const cache = {};
          if (form) {
            const { getFieldsValue, getFieldInstance } = form;
            cache.form = getFieldsValue();
            const __LOV_TEXT_FIELD__ = {};
            Object.keys(cache.form).forEach((k) => {
              const instance = getFieldInstance(k);
              if (
                instance?.state?.textField &&
                instance?.props?.code &&
                !instance?.props?.textField
              ) {
                __LOV_TEXT_FIELD__[k] = instance?.state?.textField;
              }
            });
            if (!isEmpty(__LOV_TEXT_FIELD__)) {
              cache.__LOV_TEXT_FIELD__ = __LOV_TEXT_FIELD__;
            }
          }

          if (this.pageComponent.state) {
            cache.state = this.pageComponent.state;
          }
          // setCache(cacheKey,cache);
          emitter.emit('save', cacheKey, cache, cachePreKey);
        }
        // }
      }

      setComponent = (component) => {
        this.pageComponent = component;
      };

      fakeComponentDidMount(Comp) {
        if (Comp._isFakeComponentDidMount !== true) {
          const { componentDidMount } = Comp.prototype;
          const { loadCache } = this;
          Comp.prototype.componentDidMount = function newComponentDidMount() {
            emitter.emit('willCache', cachePreKey);
            loadCache(this, () => {
              if (typeof componentDidMount === 'function') {
                componentDidMount.call(this);
              }
            });
          };
          Comp._isFakeComponentDidMount = true;
        }
      }

      render() {
        this.fakeComponentDidMount(Component);
        return <Component ref={this.setComponent} {...this.props} />;
      }
    };
}
