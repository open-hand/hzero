/**
 * @email WY <yang.wang06@hand-china.com>
 * @creationDate 2019/12/19
 * @copyright HAND ® 2019
 */

import React from 'react';
import pathToRegexp from 'path-to-regexp';

import { isDir, isLink, isMenu } from '../../../../../components/DefaultMenu/utils';

import { MenuItem, OriginMenu } from './types';
import { mapTree, travelTree } from './utils';

function transformMenu(menu: OriginMenu): MenuItem {
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

function transformDir(menu: OriginMenu): MenuItem {
  return {
    type: menu.type || 'dir',
    children: [],
    id: menu.id,
    path: menu.path,
    name: menu.name,
    icon: menu.icon,
    key: `${menu.type}-${menu.id}`,
    quickIndex: menu.quickIndex,
  };
}

function transformIfNeedExpand(menu: OriginMenu): MenuItem {
  if (isMenu(menu)) {
    const leafMenu = transformMenu(menu);
    const dirMenu = { ...leafMenu, children: [leafMenu] };
    leafMenu.parent = dirMenu;
    return dirMenu;
  }
  if (isDir(menu)) {
    return { ...transformDir(menu), children: menu.children };
  }
  if (menu.path) {
    const leafMenu = transformMenu(menu);
    const dirMenu = { ...leafMenu, children: [leafMenu] };
    leafMenu.parent = dirMenu;
    return dirMenu;
  }
  return { ...transformDir(menu), children: menu.children };
}

export function useMenu(
  menus?: OriginMenu[],
  activeTabKey?: string,
  menuQuickIndex?: string
): {
  mainMenus: MenuItem[];
  activeMenu?: MenuItem;
} {
  const mainMenus = React.useMemo(() => {
    if (menus) {
      return mapTree<OriginMenu, MenuItem>(
        menus,
        (item, parent) => {
          if (parent && parent.parent) {
            // level is three, already transform to menu
            return { ...transformMenu(item), parent };
          }
          if (parent) {
            // level is second
            const dir = transformIfNeedExpand(item);
            dir.parent = parent;
            if (item.children) {
              const children: MenuItem[] = [];
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
            return dir;
          }
          // root
          const root = transformIfNeedExpand(item);
          // 将所有二级菜单放到 同根目录名的目录中
          const newChildren: MenuItem[] = [];
          const aggregateGrandson: MenuItem[] = [];
          root.children.forEach((sItem) => {
            if (!sItem.children || sItem.children.length === 0) {
              aggregateGrandson.push(sItem);
            } else {
              newChildren.push(sItem);
            }
          });
          if (aggregateGrandson.length !== 0) {
            newChildren.unshift({
              type: 'dir',
              children: aggregateGrandson,
              id: item.id,
              name: item.name,
              key: `dir-${item.id}`,
            });
            root.children = newChildren;
          }
          return root;
        },
        {
          childrenName: 'children',
          childrenNullable: false,
          parent: undefined,
        }
      );
    }
    return [];
  }, [menus]);
  const activeMenu = React.useMemo<MenuItem | undefined>(() => {
    if (!activeTabKey) {
      return undefined;
    }

    let activeMenuItem: MenuItem | undefined;
    if (menuQuickIndex && activeTabKey === '/workplace') {
      [activeMenuItem] = mainMenus.filter((item) => {
        return item.quickIndex === menuQuickIndex;
      });
    }
    travelTree(
      mainMenus,
      (item) => {
        if (isMenu(item)) {
          if (!item.pathToRegexp) {
            if (isLink(item)) {
              // eslint-disable-next-line no-param-reassign
              item.pathToRegexp = pathToRegexp(`/link/${item.id}`, [], { end: false });

              if (item.pathToRegexp.test(activeTabKey)) {
                activeMenuItem = item;
              }
            } else {
              const path: string = item.path || '';
              // eslint-disable-next-line no-param-reassign
              item.pathToRegexp = pathToRegexp(path, [], { end: false });

              if (item.pathToRegexp.test(activeTabKey)) {
                activeMenuItem = item;
              }
            }
          } else if (item.pathToRegexp.test(activeTabKey)) {
            activeMenuItem = item;
          }
        }
      },
      { childrenName: 'children', childrenNullable: true }
    );
    return activeMenuItem;
  }, [mainMenus, activeTabKey]);
  return {
    mainMenus,
    activeMenu,
  };
}
