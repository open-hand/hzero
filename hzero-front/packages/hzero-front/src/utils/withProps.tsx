/**
 * withProps 高阶组件 注入 props
 * @date 2019/09/01
 * @author wuyunqiang yunqiang.wu@hand-china.com
 * @copyright Copyright (c) 2018, Hand
 */

import React from 'react';
import { DataSet } from 'choerodon-ui/pro';
import { getDvaApp } from './iocUtils';
import { menuTabEventManager } from './menuTab';

/**
 * 默认清除时间
 */
const CLEAN_CACHE_TIMEOUT = 10 * 1000 * 60;

interface ThrottleTimeSingled extends Function {
  // tslint:disable-next-line: ban-types
  clean: Function;
  // tslint:disable-next-line: ban-types
  startTimeoutClean: Function;
}

const _withPropsCache: Map<string, any> =
  (window as any)._withPropsCache || ((window as any)._withPropsCache = new Map());

/**
 * 缓存方法的返回值, 有点类似于单例模式，区别在于，调用 startTimeoutClean 之后会在指定时间内清除缓存，
 * 每次调用该方法是缓存时间重置。
 * @param {*} func 缓存的方法
 * @returns {Function} throttleTimeSingleFun 返回的包装后端方法
 * @returns {Function} throttleTimeSingleFun.startTimeoutClean 开始计时，当到达指定时候后清除缓存数据
 */
// tslint:disable-next-line: ban-types
export function throttleTimeSingle(func: Function, options: WithPropsOption): ThrottleTimeSingled {
  const {
    // cacheState = false,
    // wait = CLEAN_CACHE_TIMEOUT,
    keepOriginDataSet = true,
    cacheKey = '',
    cleanWhenClose = true,
  } = options;

  if (typeof func !== 'function') {
    throw new TypeError('FUNC_ERROR_TEXT');
  }
  let result;
  let lastArgs;
  let lastThis;
  let timerId;

  const clean = () => {
    if (timerId !== undefined) {
      clearTimeout(timerId);
      timerId = undefined;
    }
    lastArgs = undefined;
    lastThis = undefined;
    timerId = undefined;
    result = undefined;
    if (cacheKey) {
      _withPropsCache.delete(cacheKey);
      for (const cCacheKey of _withPropsCache.keys()) {
        if (cCacheKey.startsWith(cacheKey)) {
          const obj = _withPropsCache.get(cCacheKey);
          if (obj && obj.clean) {
            obj.clean();
          }
        }
      }
    }
  };

  const startTimeoutClean = (wait = CLEAN_CACHE_TIMEOUT) => {
    if (wait === 0) {
      clean();
      return;
    }
    if (timerId !== undefined) {
      clearTimeout(timerId);
    }
    timerId = setTimeout(clean, wait);
  };

  function invokeFunc() {
    const args = lastArgs;
    const thisArg = lastThis;

    lastArgs = undefined;
    lastThis = undefined;
    result = func.apply(thisArg, args);

    if (cacheKey) {
      _withPropsCache.set(cacheKey, {
        cacheData: result,
        clean,
      });
    }
    if (cleanWhenClose) {
      const tabsKey = getDvaApp()?._store?.getState()?.global?.activeTabKey;
      if (tabsKey) {
        const listener = ({ tabKey }) => {
          if (tabKey === tabsKey) {
            clean();
            menuTabEventManager.off('close', listener);
          }
        };
        menuTabEventManager.on('close', listener);
      }
    }
    return result;
  }

  function throttleTimeSingled(this: any, ...rest) {
    lastArgs = rest;
    lastThis = this;
    if (timerId === undefined) {
      return invokeFunc();
    } else {
      clearTimeout(timerId);
      const lastResult = result;
      const args = lastArgs;
      const thisArg = lastThis;
      if (keepOriginDataSet) {
        return lastResult;
      }
      const dataSets = func.apply(thisArg, [...args, lastResult]);
      Object.keys(lastResult).forEach((dataSetName) => {
        const cacheDs = lastResult[dataSetName];
        if (cacheDs.queryDataSet) {
          const cacheFormData = cacheDs.queryDataSet.records[0].toData();
          const qds = dataSets[dataSetName].queryDataSet;
          if (!qds.current) {
            qds.create({});
          }
          Object.entries(cacheFormData || {}).forEach(([key, value]) => {
            qds.current.set(key, value);
          });
        }
      });
      result = dataSets;
      return result;
    }
  }

  throttleTimeSingled.startTimeoutClean = startTimeoutClean;
  throttleTimeSingled.clean = clean;

  return throttleTimeSingled;
}

interface DataSetMap {
  [key: string]: DataSet;
}

interface WithPropsOption {
  /**
   * 关闭页面之后多久会自动清空缓存 毫秒值
   */
  wait?: number;
  /** 是否缓存数据状态 */
  cacheState?: boolean;
  /** 缓存清除的 key ，用户关联子父页面，当父页面缓存清空时 子页面的缓存也自动清空。子key 和 父key的管理是前缀 比如 父： abc.xxx-list 子 abc.xxx-list.detail */
  cacheKey?: string;
  /** 关闭 tabs 时 是否自动清空缓存, 默认值 true */
  cleanWhenClose?: boolean;
  /**
   * 保持原来的 DataSet 对象. 默认值 true
   */
  keepOriginDataSet?: boolean;
}

type InitPropsFun = (
  /** 上一次缓存的数据 */
  lastResult: DataSetMap | undefined
) => DataSetMap;

/**
 * 高级组件 单例模式缓存props,并且在组件生命周期之后的指定时间后清除缓存
 * @param initPropsFun {()=>({ [key: string]: DataSet })} initPropsFun 返回一个 dataset map 的方法
 * @param options.cacheState {boolean} 是否缓存数据状态
 * @param options.wait {number} 关闭页面之后多久会自动清空缓存 毫秒值
 * @param options.cacheKey 缓存清除的 key ，用户关联子父页面，当父页面缓存清空时 子页面的缓存也自动清空。子key 和 父key的管理是前缀 比如 父： abc.xxx-list 子 abc.xxx-list.detail
 * @param options.cleanWhenClose {boolean} 关闭 tabs 时 是否自动清空缓存
 *
 * @example
 *  // initProps 是延迟运行的，initProps 返回的数据可以注入到组件的 props 里面
 *  const initProps = () => {
 *    const tableDS = new DataSet({
 *      ...dataSetProps(),
 *        autoQuery: true,
 *        exportUrl: '...',
 *    });
 *    return {
 *      tableDS,
 *    };
 *  };
 *  @withProps(initProps, { cacheState: true })
 *  export default class ListPage extends PureComponent {
 *    initWithProps() {
 *       // 这里可以完成 ds 的事件绑定, 或者对 initProps 返回的 Props 做一些持久化的修改
 *       this.props.tableDS.addEventListener('query', this.handleQueryEvent);
 *    }
 *    componentDidMount() {
 *      // 这里可以拿到通过时间缓存控制过的 initProps 的返回值
 *      console.log(this.props.tableDS);
 *   }
 * }
 */
export default function withProps(
  /**
   * initPropsFun 返回一个 dataset map 的方法
   */
  initPropsFun: InitPropsFun,
  /**
   * 其他选项
   */
  options: WithPropsOption
): Function {
  const {
    cacheState = false,
    wait = CLEAN_CACHE_TIMEOUT,
    // cacheKey = '',
    // cleanWhenClose = true,
  } = options;
  const throttleTimeSingleFun = throttleTimeSingle(initPropsFun, options);
  return (Components) => {
    class WithPropsComponent extends React.Component {
      constructor(props) {
        super(props);
        const dataSets = throttleTimeSingleFun();
        this.state = dataSets;
      }

      // componentWillReceiveProps(nextProps) {
      //   if ((this.props as any).language !== nextProps!.language) {
      //     throttleTimeSingleFun.clean();
      //     setTimeout(() => {
      //       this.setState(throttleTimeSingleFun());
      //     }, 400);
      //   }
      // }

      static getDerivedStateFromProps(nextProps, prevState) {
        if (prevState!.language && nextProps!.language !== prevState!.language) {
          throttleTimeSingleFun.clean();
          return throttleTimeSingleFun();
        }
        return null;
      }

      componentWillUnmount() {
        if (cacheState) {
          throttleTimeSingleFun.startTimeoutClean(wait);
        } else {
          throttleTimeSingleFun.clean();
        }
      }

      render() {
        return React.createElement(
          Components,
          { ...this.state, ...this.props },
          this.props.children
        );
      }
    }
    return WithPropsComponent;
  };
}
