/**
 * @date 2019-04-04
 * @author WY <yang.wang06@hand-china.com>
 */
import React from 'react';
import pathToRegexp from 'path-to-regexp';
import { isEmpty, isUndefined } from 'lodash';
import { Icon } from 'choerodon-ui/pro';

import Icons from 'components/Icons';

import intl from 'utils/intl';
import { openTab } from 'utils/menuTab';

import styles from './styles.less';

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
      return [leafMenu.subMenu.mainMenu, leafMenu.subMenu, leafMenu];
    } else if (leafMenu.type === 'inner-link' && `/link/${leafMenu.id}` === activeTabKey) {
      return [leafMenu.subMenu.mainMenu, leafMenu.subMenu, leafMenu];
    }
  }
  return [];
}

/**
 *
 * 将菜单转成 合适 的结构
 * @param {object[]} menus - 菜单
 * @returns {{leafMenus: *, menus: *}}
 * - leafMenus 从三级菜单到 一级菜单(用来计算激活的菜单)
 * - menus: 不够三级菜单的 由上级菜单补全, 超过三级菜单的 变为三级菜单
 */
export function computeMenus(menus = []) {
  // 存储所有的叶子节点
  const retLeafMenus = [];
  const retMenus = menus.map((mainMenu) => {
    const { children = [], ...mM } = mainMenu;
    const subMenus = [];
    if (isLeafMenu(mainMenu)) {
      // 根目录就是菜单
      subMenus.push({ ...mM, isOnlyMainMenu: true });
    } else {
      subMenus.push(...children);
    }
    mM.children = subMenus.map((subMenu) => {
      const { children: subMenuChildren = [], ...sM } = subMenu;
      const sMChildren = [];
      sM.mainMenu = mM;
      const leafMenus = [];
      if (isLeafMenu(subMenu)) {
        leafMenus.push({ ...subMenu, isOnlySubMenu: true });
      } else {
        leafMenus.push(...subMenuChildren);
      }
      repeatCollectLeafMenus(leafMenus, sMChildren, sM, false);
      retLeafMenus.push(...sMChildren);
      sM.children = sMChildren;
      if (sMChildren.length === 1 && sMChildren[0].id === sM.id) {
        sM.isOnlySubMenu = true;
      }
      return sM;
    });
    mM.allChildrenAreOnlySubMenu = mM.children.every((subMenu) => subMenu.isOnlySubMenu);
    return mM;
  });
  return {
    leafMenus: retLeafMenus,
    menus: retMenus,
  };
}

/**
 * @param {!object[]} children - 所有的子菜单
 * @param {object[]} collect - 已经存放的子菜单
 * @param {object} subMenu - 二级菜单
 // * @param {boolean} isRepeat - 是否是四级及以下菜单
 */
// eslint-disable-next-line no-unused-vars
function repeatCollectLeafMenus(children = [], collect = [], subMenu /* , isRepeat */) {
  for (let i = 0; i < children.length; i++) {
    const { children: lC, ...lM } = children[i];
    if (isLeafMenu(lM)) {
      // 三级菜单必定是 叶子菜单 否则 可能是一个非法的菜单
      lM.subMenu = subMenu;
      lM.__pathExp = pathToRegexp(lM.path, [], { end: false });
      collect.push(lM);
    }
    if (!isEmpty(lC)) {
      repeatCollectLeafMenus(lC, collect, subMenu, true);
    }
  }
  return collect;
}

/**
 * 判断是否是叶子菜单
 * @param {object} menu - 菜单
 */
export function isLeafMenu(menu = {}) {
  return !!menu.path && isEmpty(menu.children);
  // return isEmpty(menu.children);
}

/**
 * 渲染一级菜单的icon
 * @param {string} menu - menu 菜单
 * @param {string} classPrefix - 样式前缀
 // * @param {boolean} isHover - 时候hover
 */
export function renderIcon(menu, classPrefix = 'main-menu-item' /* , isHover */) {
  const classNames = [styles[`${classPrefix}-icon`]];
  const { name, icon, path } = menu;
  classNames.push(styles[`${classPrefix}-icon-img`]);
  if (!isUndefined(icon)) {
    return name.includes('choerodon') && path.includes('hlcd') ? (
      <Icon type={icon} style={{ fontSize: '12px' }} className={classNames.join(' ')} />
    ) : (
      <Icons style={{ width: '12px' }} type={icon} className={classNames.join(' ')} />
    );
  } else {
    return (
      <Icons
        style={{ width: '12px' }}
        type="development-management"
        className={classNames.join(' ')}
      />
    );
  }
}

/**
 * 获取菜单的标题
 * @param {object} menu -菜单标题
 */
export function renderMenuTitle(menu) {
  return (menu.name && intl.get(menu.name)) || '...';
}

/**
 * 获取菜单对应的 key
 * @param {!object} menu - 菜单
 */
export function getMenuKey(menu) {
  return menu.id;
}

/**
 * 根据菜单打开页面
 */
export function openMenu(menu) {
  if (menu.type === 'window' && menu.path) {
    const url = menu.path.startsWith('http') ? menu.path : `http://${menu.path}`;
    const targetWin = window.open(url);
    targetWin.opener = null;
    return;
  }
  // 打开菜单
  openTab({
    icon: menu.icon,
    title: menu.name,
    key: menu.type === 'link' || menu.type === 'inner-link' ? `/link/${menu.id}` : menu.path,
    closable: true,
    search: menu.search,
    type: menu.type, // 菜单的类型，link类型将会打开iframe嵌入外部网址
  });
}

/**
 * 菜单是否是 目录
 * TODO: 如果菜单是 根目录/目录 则是目录
 * @param menu - 菜单
 * @returns {boolean}
 */
export function isDir(menu) {
  return ['dir', 'root'].includes(menu.type);
}

/**
 * 菜单是否是 菜单
 * TODO: 如果菜单是 菜单/链接/内部链接 则是目录
 * @param menu - 菜单
 * @returns {boolean}
 */
export function isMenu(menu) {
  return ['menu', 'link', 'inner-link', 'window'].includes(menu.type);
}

/**
 * 菜单如果是 link 或者 inner-link
 * @param {Object} menu 菜单
 * @returns {boolean}
 */
export function isLink(menu) {
  return ['link', 'inner-link'].includes(menu.type);
}

export const MenuContext = React.createContext(undefined, (prev, next) =>
  // FIXME: 看子状态是否更新到底怎么判断
  prev === next ? 0 : 1
);

export const defaultMenuItemWith = 220;
export const defaultCollapsedMenuItemWith = 64;

export const menuItemHeight = 40;
export const pageHeaderHeight = 48;

// 行高 20 + padding 10 * 2
export const leafMenuItemHeight = 40;
