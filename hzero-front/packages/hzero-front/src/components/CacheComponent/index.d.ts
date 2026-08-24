/**
 * 获取当前激活的 tab 的 key
 */
function getActiveTabKey(): string;

class CacheComponentArgs {
  /**
   * 缓存的 key 一般为组件对应的 路由
   */
  cacheKey: string;

  /**
   * 一般同一个 tab 具有相同的 cachePreKey
   */
  cachePreKey: string = getActiveTabKey();
}

export default function cacheComponent(cacheComponentArgs: CacheComponentArgs) {}

/**
 * 清除一个tab下的所有页面的缓存
 * @param {string} cachePreKey - 一个tab下所有页面的 cachePreKey
 */
export function cleanCache(cachePreKey: string): void;

/**
 * 删除某个特定的 页面的缓存
 * @param {string} cacheKey - 一个缓存的key
 */
export function deleteCache(cacheKey: string): void;
