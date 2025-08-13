/**
 * SideMenu - 无限展开层级菜单
 * 当菜单选中时, 对应的菜单是有高亮的
 */

import React from 'react';
import { isEmpty } from 'lodash';
import { connect } from 'dva';
import { Spin } from 'hzero-ui';

import {
  defaultCollapsedMenuItemWith,
  defaultMenuItemWith,
} from '../../../components/DefaultMenu/utils';

// import { useDebounceState } from '../../hooks';

import { useSideMenu } from './hooks';

import MainMenu from './MainMenu';
import SideCascaderMenu from './SideCascaderMenu';
import SideMask from './SideMask';

import { defaultGetClassName } from './utils';

const SideMenu = ({
  getClassName = defaultGetClassName,
  collapsed = false,
  menus,
  menuLoad,
  activeTabKey,
  menuQuickIndex,
  components = {},
  menuItemWidth = defaultMenuItemWith,
  collapsedMenuItemWidth = defaultCollapsedMenuItemWith,
  imperativeRef,
}) => {
  const { Mask = SideMask, CascaderMenu = SideCascaderMenu } = components;
  const [sideMenus, currentMenu] = useSideMenu([menus, activeTabKey, menuQuickIndex]) || [];
  // const [activeMenu, setActiveMenu] = useDebounceState();
  // const [maskHeight, setMaskHeight] = useDebounceState(36);
  const [activeMenu, setActiveMenu] = React.useState();
  const [maskHeight, setMaskHeight] = React.useState(36);
  const leftTopMaskStyle = React.useMemo(
    () =>
      collapsed
        ? {
            width: collapsedMenuItemWidth,
            height: maskHeight,
          }
        : {
            width: menuItemWidth,
            height: maskHeight,
          },
    [collapsed, maskHeight, collapsedMenuItemWidth, menuItemWidth]
  );
  const rightMaskStyle = React.useMemo(
    () =>
      collapsed
        ? {
            left: collapsedMenuItemWidth,
          }
        : {},
    [collapsed, collapsedMenuItemWidth]
  );
  const handleMaskTrigger = React.useCallback(() => {
    setActiveMenu();
  }, [setActiveMenu]);
  React.useImperativeHandle(
    imperativeRef,
    () => ({
      setActiveMenu,
      setMaskHeight,
    }),
    [imperativeRef, setActiveMenu, setMaskHeight]
  );
  return (
    <>
      {menuLoad ? (
        <div className={getClassName()}>
          {sideMenus.map((menu) => (
            <MainMenu
              menu={menu}
              currentMenu={currentMenu}
              activeMenu={activeMenu}
              setActiveMenu={setActiveMenu}
              components={{ CascaderMenu }}
              collapsed={collapsed}
            />
          ))}
          {!isEmpty(activeMenu) && (
            <Mask
              onTrigger={handleMaskTrigger}
              collapsed={collapsed}
              leftTopStyle={leftTopMaskStyle}
              rightStyle={rightMaskStyle}
            />
          )}
        </div>
      ) : (
        <Spin spinning size="large" className={getClassName('spin')} />
      )}
    </>
  );
};

export default connect(({ global = {} }) => ({
  menus: global.menu,
  menuLoad: global.menuLoad,
  menuQuickIndex: global.menuQuickIndex,
  activeTabKey: global.activeTabKey,
}))(SideMenu);
