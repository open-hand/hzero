/**
 * @email WY <yang.wang06@hand-china.com>
 * @creationDate 2019/12/19
 * @copyright HAND ® 2019
 */
import { connect } from 'dva';
import React from 'react';
import { Spin } from 'hzero-ui';

import { getClassName as getCommonLayoutClassName } from '../../../../utils';

import MainMenu from './MainMenu';
import SideMask from './SideMask';

import { useMenu as commonLayoutUseMenu } from './hooks';
import { MenuItem } from './types';

interface MenuProps<OriginMenu = any> {
  components: {};
  getClassName?: (cls: string) => string;
  useMenu?: (
    menus: OriginMenu[]
  ) => {
    mainMenus: MenuItem[];
    activeMenu?: MenuItem;
  };
  menus?: OriginMenu[];
  menuLoad: Boolean;
  menuQuickIndex: string;
  activeTabKey?: string;
}

const Menu: React.FC<MenuProps> = ({
  getClassName = getCommonLayoutClassName,
  useMenu = commonLayoutUseMenu,
  menus = [],
  activeTabKey,
  menuLoad,
  menuQuickIndex,
}) => {
  const { mainMenus, activeMenu } = useMenu(menus, activeTabKey, menuQuickIndex);
  const [currentMenu, setCurrentMenu] = React.useState<MenuItem>();

  // 遮罩 取消后 设置
  const handleMaskTrigger = React.useCallback(() => {
    setCurrentMenu(undefined);
  }, [setCurrentMenu]);

  return (
    <>
      {menuLoad ? (
        <div className={getClassName('menu-wrap')}>
          <div className={getClassName('menu')}>
            {mainMenus.map((mainMenu) => {
              return (
                <MainMenu
                  key={mainMenu.key}
                  menu={mainMenu}
                  activeMenu={activeMenu}
                  currentMenu={currentMenu}
                  setCurrentMenu={setCurrentMenu}
                />
              );
            })}

            {currentMenu && <SideMask onTrigger={handleMaskTrigger} />}
          </div>
        </div>
      ) : (
        <Spin spinning size="large" className={getClassName('spin')} />
      )}
    </>
  );
};

export default connect(
  ({
    global = { menu: Array, menuLoad: Boolean, menuQuickIndex: String, activeTabKey: String },
  }) => ({
    menus: global.menu,
    menuLoad: global.menuLoad, // 菜单
    menuQuickIndex: global.menuQuickIndex, // 菜单
    activeTabKey: global.activeTabKey,
  })
)(Menu);
