import { kebabCase } from 'lodash';

/**
 * build the className by paths
 * 1. prefix hzero-side-layout-menu in paths,
 * 2. let paths join with -
 * 3. call lodash.kebabCase
 * 4. return className
 * @param {...string} [paths] - the strings of className
 */
function defaultGetClassName(...paths) {
  return kebabCase(['hzero-side-layout-menu', ...paths].join('-'));
}

function defaultGetMainMenuClassName(...paths) {
  return defaultGetClassName('main', ...paths);
}

function defaultGetSideCascaderMenuClassName(...paths) {
  return defaultGetClassName('side-casacader', ...paths);
}

function defaultGetSideCascaderMenuMaskClassName(...paths) {
  return defaultGetSideCascaderMenuClassName('mask', ...paths);
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
function isMenuInAccessPath(activeMenu, menu) {
  return (
    (activeMenu && menu && activeMenu.id === menu.id) ||
    (activeMenu && activeMenu.parent && isMenuInAccessPath(activeMenu.parent, menu))
  );
}

export {
  defaultGetClassName,
  defaultGetMainMenuClassName,
  defaultGetSideCascaderMenuClassName,
  defaultGetSideCascaderMenuMaskClassName,
  isMenuInAccessPath,
};
