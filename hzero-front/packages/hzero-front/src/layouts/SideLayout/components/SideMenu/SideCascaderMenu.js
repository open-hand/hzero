import React from 'react';
import classNames from 'classnames';

import {
  defaultCollapsedMenuItemWith,
  defaultMenuItemWith,
  isDir,
  isMenu,
  openMenu,
  renderMenuTitle,
} from '../../../components/DefaultMenu/utils';

import { defaultGetSideCascaderMenuClassName, isMenuInAccessPath } from './utils';

/**
 * 菜单专用的 div 包装组件, 防止 方法的 bind
 * @param menu
 * @param onMouseEnter
 * @param onClick
 * @param divProps
 * @returns {*}
 * @constructor
 */
const Div = ({ menu, onMouseEnter, onClick, ...divProps }) => {
  const handleMouseEnter = React.useCallback(
    event => {
      event.preventDefault();
      event.stopPropagation();
      onMouseEnter(menu);
    },
    [menu, onMouseEnter]
  );
  const handleClick = React.useCallback(
    event => {
      event.preventDefault();
      event.stopPropagation();
      onClick(menu);
    },
    [menu, onClick]
  );
  return <div {...divProps} onMouseEnter={handleMouseEnter} onClick={handleClick} />;
};

const SideCascaderMenu = ({
  menu, // parentMenu
  components = {}, // 覆盖组件
  currentMenu,
  activeMenu,
  setActiveMenu,
  collapsed,
  getClassName = defaultGetSideCascaderMenuClassName, // 样式方法
  menuItemWidth = defaultMenuItemWith,
  collapsedMenuItemWidth = defaultCollapsedMenuItemWith,
}) => {
  const CascaderMenu = components.CascaderMenu || SideCascaderMenu;
  const [interacted, setInteracted] = React.useState(false);
  const style = React.useMemo(
    () => ({
      // 收起后, 展开菜单需要整体 左移 220 - 64
      left: menu.level * menuItemWidth + (collapsed ? collapsedMenuItemWidth - menuItemWidth : 0),
    }),
    [collapsed, menu, menuItemWidth, collapsedMenuItemWidth]
  );
  const handleMouseEnter = React.useCallback(
    interactMenu => {
      setActiveMenu(interactMenu);
      setInteracted(true);
    },
    [setActiveMenu, setInteracted]
  );
  const handleClick = React.useCallback(
    clickMenu => {
      if (isMenu(clickMenu)) {
        openMenu(clickMenu);
        setActiveMenu();
      }
    },
    [setActiveMenu]
  );
  const cascaderMenuEles = [];
  const subCascaderMenuEles = [];
  (menu && menu.children).forEach(m => {
    cascaderMenuEles.push(
      <div className={getClassName('content', 'wrap')} key={m.id}>
        <Div
          menu={m}
          onMouseEnter={handleMouseEnter}
          onClick={handleClick}
          className={classNames(getClassName('content'), {
            [getClassName('content', 'dir')]: isDir(m),
            [getClassName('content', 'menu')]: isMenu(m),
            [getClassName('content', 'current')]: isMenuInAccessPath(currentMenu, m),
            [getClassName('content', 'active')]: isMenuInAccessPath(activeMenu, m),
          })}
        >
          {renderMenuTitle(m)}
        </Div>
      </div>
    );
    if (interacted && (m && m.children).length !== 0) {
      subCascaderMenuEles.push(
        <CascaderMenu
          key={m.id}
          components={components}
          getClassName={getClassName}
          collapsed={collapsed}
          menu={m}
          currentMenu={currentMenu}
          activeMenu={activeMenu}
          setActiveMenu={setActiveMenu}
        />
      );
    }
  });
  return (
    <>
      <div
        className={classNames(getClassName(), {
          [getClassName('current')]: isMenuInAccessPath(currentMenu, menu),
          [getClassName('active')]: isMenuInAccessPath(activeMenu, menu),
          [getClassName('leaf')]: menu.level === 2,
        })}
        style={style}
      >
        {cascaderMenuEles}
      </div>
      {subCascaderMenuEles}
    </>
  );
};

export default SideCascaderMenu;
