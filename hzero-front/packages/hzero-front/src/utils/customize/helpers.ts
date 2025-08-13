/**
 * 针对 customize 提供的一些帮助方法
 */
import { Bind } from 'lodash-decorators';
import React from 'react';

import intl from 'utils/intl';
import { resolveRequire } from 'utils/utils';

import { mapCustomize } from './index';

export class FeatureMapStore {
  // eslint can't parse this syntax
  private module: string;

  private feature: string;

  constructor(module: string, feature: string) {
    this.feature = feature;
    this.module = module;
  }

  /**
   * 获取数据
   *  获取到 返回 数据
   *  否则 返回 undefined
   * @param {String} key - map 的 key
   * @param {any} [config] - 预留配置
   * @return {any|undefined}
   */
  @Bind()
  public get(key) {
    return mapCustomize.get({
      module: this.module,
      feature: this.feature,
      key,
    });
  }

  /**
   * 设置 entry 数据
   * @param {String} key - map 的 key
   * @param {any} data - map key 对应的 value
   * @param {any} [config] - 预留配置
   */
  @Bind()
  public set(key, data): FeatureMapStore {
    mapCustomize.set({
      module: this.module,
      feature: this.feature,
      key,
      data,
    });
    return this;
  }

  /**
   * 清空对应的 数据
   * @param {any} [config] - 预留配置
   */
  @Bind()
  public clear() {
    return mapCustomize.clear({
      module: this.module,
      feature: this.feature,
    });
  }

  /**
   * 删除对应 storeMap key 的 entry
   * 删除成功 返回 true
   * 未设置 返回 false
   * @param {String} key - map 的 key
   * @param {any} [config] - 预留配置
   */
  @Bind()
  public delete(key) {
    return mapCustomize.delete({
      module: this.module,
      feature: this.feature,
      key,
    });
  }

  /**
   * 返回 对应 数据的 entries
   * @param {any} [config] - 预留配置
   */
  @Bind()
  public entries() {
    return mapCustomize.entries({
      module: this.module,
      feature: this.feature,
    });
  }

  /**
   * 遍历对应的 数据
   * @param {Function} fn - 遍历方法 (value, key, map) => void
   * @param {?any} thisArg - 遍历方法对应的 this
   * @param {any} [config] - 预留配置
   */
  @Bind()
  public forEach(fn, thisArg) {
    return mapCustomize.forEach({
      module: this.module,
      feature: this.feature,
      fn,
      thisArg,
    });
  }

  /**
   * 是否存在 数据
   * 存在 返回 true
   * 不存在/未设置 返回 false
   * @param {String} key - map 的 key
   * @param {any} [config] - 预留配置
   */
  @Bind()
  public has(key) {
    return mapCustomize.has({
      module: this.module,
      feature: this.feature,
      key,
    });
  }

  /**
   * 返回 对应 数据的 keys
   * @param {any} [config] - 预留配置
   */
  @Bind()
  public keys() {
    return mapCustomize.keys({
      module: this.module,
      feature: this.feature,
    });
  }

  /**
   * 返回 对应 数据的 values
   * @param {any} [config] - 预留配置
   */
  @Bind()
  public values() {
    return mapCustomize.values({
      module: this.module,
      feature: this.feature,
    });
  }

  /**
   * 获取对应数据的 @@iterator
   * @param {any} [config] - 预留配置
   */
  public [Symbol.iterator] = () => {
    return mapCustomize[Symbol.iterator]({
      module: this.module,
      feature: this.feature,
    });
  };

  /**
   * 获取对应数据的 数量
   * 未设置 返回 0
   * 已设置 返回 数据 数量
   * @param {any} [config] - 预留配置
   */
  @Bind()
  public size() {
    return mapCustomize.size({
      module: this.module,
      feature: this.feature,
    });
  }
}

/**
 * @param {string} hzeroModule
 * @param {string} feature
 */
export function mapCustomizeBuilder(hzeroModule, feature) {
  return new FeatureMapStore(hzeroModule, feature);
}

/**
 * LowCode 组件没有加载成功
 * @param componentCode
 * @returns {*}
 * @constructor
 */
export function DefaultNotFound({ componentCode }) {
  return React.createElement(
    'div',
    undefined,
    intl
      .get('hzero.common.error.message.sharedComponentError', { componentCode })
      .d(`shared <${componentCode} /> 组件加载失败`)
  );
}

/**
 * LowCode 组件加载中
 * @returns {null}
 * @constructor
 */
export function DefaultLoading({ componentCode }) {
  return React.createElement(
    'div',
    undefined,
    intl
      .get('hzero.common.error.message.sharedComponentLoading', { componentCode })
      .d(`shared <${componentCode} /> 组件加载中`)
  );
}

export interface ComponentMapCustomizeBuilderOptions {
  NotFound: any;
  Loading: any;
}

/**
 *
 * @param {string} module - 模块名
 * @param {string} [feature='component'] - 功能名
 * @param {object} [options] - 配置
 * @param {React.Component} [options.NotFount] - 没有加载到对应 componentCode 的组件时 渲染的组件
 * @param {React.Component} [options.Loading] - React.lazy/Suspense 加载组件时的fallback
 */
export function componentMapCustomizeBuilder(
  module,
  feature = 'component',
  options: ComponentMapCustomizeBuilderOptions
) {
  const { NotFound = DefaultNotFound, Loading = DefaultLoading } = options || {};
  const { set, get } = mapCustomizeBuilder(module, feature);
  return {
    /**
     * @param {string} componentCode - 组件编码
     * @param factory - React.lazy 使用
     */
    setComponent(componentCode, factory) {
      return set(componentCode, factory);
    },
    /**
     *
     * @param {string} componentCode - 组件代码
     * @param {any} componentProps - 组件属性
     * @returns {React.SFCElement<React.SuspenseProps>}
     * @constructor
     */
    SharedComponent({ componentCode, componentProps }) {
      const CustomizeComponent = React.useMemo(() => {
        // TODO: 如果做微服务; get 方法应该本身就是异步的
        // 如果没有获取到组件编码 对应的数据/ 则应该认为失败
        return React.lazy(() => {
          return Promise.resolve(
            (get(componentCode) ||
              (() =>
                Promise.resolve({
                  __esModule: true,
                  default: () => {
                    return React.createElement(NotFound, { componentCode });
                  },
                })))()
          ).then(cmp => ({
            __esModule: true,
            default: resolveRequire(cmp),
          }));
        });
      }, []);
      return React.createElement(
        React.Suspense,
        {
          fallback: React.createElement(Loading, { componentCode }),
        },
        React.createElement(CustomizeComponent, componentProps)
      );
    },
  };
}
