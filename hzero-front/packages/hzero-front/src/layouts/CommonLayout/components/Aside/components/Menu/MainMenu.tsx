/**
 * @email WY <yang.wang06@hand-china.com>
 * @creationDate 2019/12/19
 * @copyright HAND ® 2019
 */

import React from 'react';
import classNames from 'classnames';

import { renderMenuTitle } from '../../../../../components/DefaultMenu/utils';

import { getClassName as getCommonLayoutClassName } from '../../../../utils';

import CascaderMenu from './CascaderMenu';
import MenuIcon from './MenuIcon';

import { MenuItem } from './types';
import { isMenuInAccessPath } from './utils';

interface MainMenuProps {
  menu: MenuItem;
  activeMenu?: MenuItem;
  currentMenu?: MenuItem;
  setCurrentMenu: (menu?: MenuItem) => void;
  getClassName?: (cls: string) => string;
}

const MainMenu: React.FC<MainMenuProps> = ({
  menu,
  getClassName = getCommonLayoutClassName,
  setCurrentMenu,
  activeMenu,
  currentMenu,
}) => {
  const [interacted, setInteracted] = React.useState(false);
  // 当前 hover 路径上的 菜单
  const handleMouseEnter = React.useCallback(
    event => {
      event.preventDefault();
      event.stopPropagation();
      setCurrentMenu(menu);
      setInteracted(true);
    },
    [setCurrentMenu, setInteracted, menu]
  );
  return (
    <div className={getClassName('menu-main')} onMouseEnter={handleMouseEnter}>
      <div
        className={classNames(getClassName('menu-main-content'), {
          [getClassName('menu-main-content-active')]: isMenuInAccessPath(activeMenu, menu),
          [getClassName('menu-main-content-current')]: isMenuInAccessPath(currentMenu, menu),
        })}
      >
        <MenuIcon menu={menu} classPrefix={getClassName('menu-main')} />
        <div className={getClassName('menu-main-content-title')}>{renderMenuTitle(menu)}</div>
      </div>
      {interacted && (
        <CascaderMenu
          key={menu.key}
          menu={menu}
          currentMenu={currentMenu}
          setCurrentMenu={setCurrentMenu}
          activeMenu={activeMenu}
        />
      )}
    </div>
  );
};

export default MainMenu;
