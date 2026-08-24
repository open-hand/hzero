/**
 * hooks - 菜单的 hooks
 * @author WY <yang.wang06@hand-china.com>
 * @copyright HAND © 2019
 */

import React from 'react';
import pathToRegexp from 'path-to-regexp';
import { cloneDeep } from 'lodash';

import { isMenu, isLink } from '../../../components/DefaultMenu/utils';

import { travelMapTree, travelTree } from '../../utils';
// import { useDebounceState } from '../../hooks';

function transformMenu(menu) {
  return {
    type: menu.type || 'menu',
    children: [],
    id: menu.id,
    path: menu.path,
    name: menu.name,
    // icon: menu.icon, // menu shouldn't have icon
    key: `${menu.type}-${menu.id}`,
    quickIndex: menu.quickIndex,
  };
}

/**
 * 将菜单转化为 SideMenu 需要的格式, 以及计算currentMenu
 * @param menus
 * @param activeTabKey
 */
function calculateComputeMenusAndCurrent(menus = [], activeTabKey) {
  let currentMenu = null;
  const computeMenus = travelMapTree(menus, (item, parent) => {
    const tran = {
      ...item,
      parent,
    };
    // TODO: 一般菜单 必定有 type, low-code 可能没有 type
    if (tran.type === undefined) {
      if (tran.path) {
        if (!tran.parent) {
          // 如果当前 low-code 菜单是一级菜单 那么需要拼接
          const c = { ...tran, parent: tran, type: 'menu' };
          tran.type = 'dir';
          tran.children = [c];
        } else {
          tran.type = 'menu';
        }
      }
    }
    if (isMenu(item) || item.pathToRegexp) {
      if (isLink(item)) {
        tran.pathToRegexp = pathToRegexp(`/link/${item.id}`, [], { end: false });
      } else {
        tran.pathToRegexp = pathToRegexp(item.path, [], { end: false });
      }
      if (tran.pathToRegexp.test(activeTabKey)) {
        currentMenu = tran;
      }
      let cur = tran;
      const treePath = [cur];
      while (cur.parent) {
        cur = cur.parent;
        treePath.push(cur);
      }
      treePath.forEach((m, index) => {
        // eslint-disable-next-line no-param-reassign
        m.level = treePath.length - index;
        // eslint-disable-next-line no-param-reassign
        m.height = index;
      });
    }

    if (parent && !parent.parent && !isMenu(item)) {
      const dir = cloneDeep(tran);
      if (dir.children) {
        const children = [];
        travelTree(
          item.children,
          (c) => {
            if (isMenu(c) || c.path) {
              children.push({ ...transformMenu(c), parent: dir });
            }
          },
          { childrenNullable: false, childrenName: 'children', parent: dir }
        );
        dir.children = children;
        return dir;
      }
    }

    return tran;
  });
  return [computeMenus, currentMenu];
}

// /**
//  * 将菜单转化为 SideMenu 需要的格式
//  * @param menus
//  */
// function calculateComputeMenus(menus = []) {
//   return travelMapTree(menus, (item, parent) => {
//     const tran = {
//       ...item,
//       parent,
//     };
//     if (item.type === 'menu' || item.pathToRegexp) {
//       tran.pathToRegexp = pathToRegexp(item.path, [], { end: false });
//       let cur = tran;
//       const treePath = [cur];
//       while (cur.parent) {
//         cur = cur.parent;
//         treePath.push(cur);
//       }
//       treePath.forEach((m, index) => {
//         // eslint-disable-next-line no-param-reassign
//         m.level = treePath.length - index;
//         // eslint-disable-next-line no-param-reassign
//         m.height = index;
//       });
//     }
//   });
// }
//
// /**
//  * 计算 当前 menuTab 打开的菜单
//  * @param computeMenus
//  * @param activeTabKey
//  */
// function calculateCurrentMenu(computeMenus, activeTabKey) {
//   return travelTreeFind(computeMenus, item => {
//     return item.pathToRegexp.test(activeTabKey);
//   });
// }

/**
 * Now we calculate computeMenus and currentMenu after the menus or activeMenuKey changes.
 * If we find performance issues, we will update the compressorMenus and currentMenu separately.
 * @param inputs
 * @returns {{computeMenus: *, currentMenu: *}}
 */
export const useSideMenu = (inputs = []) => {
  const [computeMenus, setComputeMenus] = React.useState([]);
  const [currentMenu, setCurrentMenu] = React.useState({});
  React.useMemo(() => {
    const [menus, activeTabKey, menuQuickIndex] = inputs;
    if (menuQuickIndex && activeTabKey === '/workplace') {
      const cm = menus.filter((item) => {
        return item.quickIndex === menuQuickIndex;
      });
      const [cms] = calculateComputeMenusAndCurrent(menus);
      setComputeMenus(cms);
      setCurrentMenu(cm[0]);
    } else {
      const [cms, cm] = calculateComputeMenusAndCurrent(menus, activeTabKey);
      setComputeMenus(cms);
      setCurrentMenu(cm);
    }
  }, [...inputs, setComputeMenus, setCurrentMenu]);
  return [computeMenus, currentMenu];
};
