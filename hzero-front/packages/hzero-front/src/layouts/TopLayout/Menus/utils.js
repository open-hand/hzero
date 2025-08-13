import React from 'react';
import { isEmpty } from 'lodash';
import pathToRegexp from 'path-to-regexp';

/**
 * 深度遍历, 计算一次
 * 计算激活的 菜单
 * @param {object[]} leafMenus - 所有的三级菜单
 * @param {string} activeTabKey - 激活的 tab 的key
 * @return {[object, object, object]} 激活的菜单
 */
export function computeActiveMenus(leafMenus = [], activeTabKey) {
  for (let i = 0; i < leafMenus.length; i++) {
    const leafMenu = leafMenus[i];
    if (leafMenu.__pathExp.test(activeTabKey) && leafMenu.type !== 'inner-link') {
      const activeMenus = [];
      let curMenu = leafMenu;
      while (curMenu) {
        activeMenus.push(curMenu);
        curMenu = curMenu.parent;
      }
      return activeMenus;
    } else if (leafMenu.type === 'inner-link' && `/link/${leafMenu.id}` === activeTabKey) {
      const activeMenus = [];
      let curMenu = leafMenu;
      while (curMenu) {
        activeMenus.push(curMenu);
        curMenu = curMenu.parent;
      }
      return activeMenus;
    }
  }
  return [];
}

/**
 * 将菜单转成 合适 的结构
 * @param {object[]} menus - 菜单
 */
export function computeMenus(menus = [], parent) {
  // 存储所有的叶子节点
  const retLeafMenus = [];
  const retMenus = menus.map((mainMenu) => {
    const { children = [], ...childMenu } = mainMenu;
    childMenu.parent = parent;
    const { leafMenus: reLeafMenus, menus: reMenus } = computeMenus(children, childMenu);
    retLeafMenus.push(...reLeafMenus);
    childMenu.children = reMenus;

    if (isLeafMenu(mainMenu)) {
      childMenu.__pathExp = pathToRegexp(childMenu.path, [], { end: false });
      retLeafMenus.push(childMenu);
    }

    return childMenu;
  });

  return {
    leafMenus: retLeafMenus,
    menus: retMenus,
  };
}

/**
 * 判断是否是叶子菜单
 * @param {object} menu - 菜单
 */
export function isLeafMenu(menu = {}) {
  return !!menu.path && isEmpty(menu.children);
}

export const MenuContext = React.createContext({});
