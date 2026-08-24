/**
 * CommonLayout 的 公用/基础 方法
 * @author WY <yang.wang06@hand-china.com>
 * @creationDate 2019/12/16
 * @copyright 2019 ® HAND
 */

/**
 * 获取样式, 给样式加上公共前缀等操作
 * @param {string} cls - 样式
 */
export function getClassName(cls) {
  return `hzero-common-layout-${cls}`;
}
