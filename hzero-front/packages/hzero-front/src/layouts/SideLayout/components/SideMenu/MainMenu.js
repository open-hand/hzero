import React from 'react';
import classNames from 'classnames';

import { renderMenuTitle } from '../../../components/DefaultMenu/utils';

import SideCascaderMenu from './SideCascaderMenu';
import SideMenuIcon from './SideMenuIcon';

import { defaultGetMainMenuClassName, isMenuInAccessPath } from './utils';

/**
 *
 * @param currentMenu - 当前 menuTab 激活的Tab 对应的菜单
 * @param menu - 一级菜单
 * @param components - 是否覆盖组件
 * @param collapsed - 是否收起
 * @param getClassName - 获取样式
 * @param activeMenu - 当前 hover 的菜单
 * @param setActiveMenu - 设置当前激活的菜单
 */
const MainMenu = ({
  menu,
  currentMenu,
  activeMenu,
  setActiveMenu,
  components = {},
  collapsed,
  getClassName = defaultGetMainMenuClassName,
}) => {
  const CascaderMenu = components.CascaderMenu || SideCascaderMenu;
  const MenuIcon = components.MenuIcon || SideMenuIcon;
  const [interacted, setInteracted] = React.useState(false);
  // 当前 hover 路径上的 菜单
  const handleMouseEnter = React.useCallback(
    event => {
      event.preventDefault();
      event.stopPropagation();
      setActiveMenu(menu);
      setInteracted(true);
    },
    [setActiveMenu, setInteracted, menu]
  );
  return (
    <div
      className={classNames(getClassName(), {
        [getClassName('current')]: isMenuInAccessPath(currentMenu, menu),
        [getClassName('active')]: isMenuInAccessPath(activeMenu, menu),
      })}
    >
      <div className={getClassName('content')} onMouseEnter={handleMouseEnter}>
        <MenuIcon menu={menu} classPrefix={getClassName('content')} />
        <div className={getClassName('content', 'title')}>{renderMenuTitle(menu)}</div>
      </div>
      {interacted && (
        <CascaderMenu
          key={menu.id}
          menu={menu}
          activeMenu={activeMenu}
          setActiveMenu={setActiveMenu}
          currentMenu={currentMenu}
          collapsed={collapsed}
        />
      )}
    </div>
  );
};

export default MainMenu;
