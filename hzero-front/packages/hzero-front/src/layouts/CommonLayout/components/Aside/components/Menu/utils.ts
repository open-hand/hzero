/**
 * @email WY <yang.wang06@hand-china.com>
 * @creationDate 2019/12/19
 * @copyright HAND ® 2019
 */

import { MenuItem } from './types';

interface TravelTreeOption<T> {
  childrenName?: string;
  parent?: T;
  childrenNullable: boolean;
}

/**
 * 遍历并转化树
 */
export function mapTree<T = any, P = T>(
  tree: T[],
  iter: (item: T, parent?: P) => P,
  options: TravelTreeOption<P>,
): P[] {
  return (tree || []).map(item => {
    const { childrenName = 'children', parent, childrenNullable = false } = options || {};
    const tran = iter(item, parent);
    if (tran[childrenName] || !childrenNullable) {
      tran[childrenName] = mapTree(tran[childrenName] || [], iter, {
        ...options,
        parent: tran,
      });
    }
    return tran;
  });
}

/**
 * 遍历树
 */
export function travelTree<T = any>(
  tree: T[],
  iter: (item: T, parent?: T) => void,
  options: TravelTreeOption<T>,
) {
  return (tree || []).map(item => {
    const { childrenName = 'children', parent, childrenNullable = false } = options || {};
    iter(item, parent);
    if (item[childrenName] || !childrenNullable) {
      travelTree(item[childrenName] || [], iter, {
        ...options,
        parent: item,
      });
    }
    return item;
  });
}

/**
 * 判断 activeMenu 是否在 menu 的访问路径中
 * A > A1 > A2 > A3
 * A2 在 A,A1,A2 的访问路径
 * A1 在 A,A1 的访问路径
 * @param {id} activeMenu - 路径菜单
 * @param {id, parent} menu - 菜单
 * @returns {boolean}
 */
export function isMenuInAccessPath(activeMenu?: MenuItem, menu?: MenuItem): boolean {
  if (activeMenu) {
    if (menu && activeMenu.key === menu.key) {
      return true;
    } else if (activeMenu.parent) {
      return isMenuInAccessPath(activeMenu.parent, menu);
    }
  }
  return false;
}
