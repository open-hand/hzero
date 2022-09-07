/**
 * @email WY <yang.wang06@hand-china.com>
 * @creationDate 2019/12/19
 * @copyright HAND ® 2019
 */
import * as React from 'react';
import classNames from 'classnames';
import { Tooltip } from 'hzero-ui';

import { openMenu, renderMenuTitle } from '../../../../../components/DefaultMenu/utils';

import { getClassName as getCommonLayoutClassName } from '../../../../utils';
import { MenuItem } from './types';
import { isMenuInAccessPath } from './utils';

interface CascaderMenuProps {
  getClassName?: (cls: string) => string;
  menu: MenuItem;
  activeMenu?: MenuItem;
  currentMenu?: MenuItem;
  setCurrentMenu: (menu?: MenuItem) => void;
}

interface SecondCascaderMenuProps {
  menu: MenuItem;
  activeMenu?: MenuItem;
  currentMenu?: MenuItem;
  setCurrentMenu: (menu?: MenuItem) => void;
  getClassName: (cls: string) => string;
}

const SecondCascaderMenuItem: React.FC<SecondCascaderMenuProps> = ({
  menu,
  activeMenu,
  currentMenu,
  setCurrentMenu,
  getClassName,
}) => {
  const handleMenuEnter = React.useCallback(
    (event: React.MouseEvent) => {
      event.preventDefault();
      event.stopPropagation();
      setCurrentMenu(menu);
    },
    [menu, setCurrentMenu]
  );
  const handleMenuClick = React.useCallback(
    (event: React.MouseEvent) => {
      event.preventDefault();
      event.stopPropagation();
      openMenu(menu);
      setCurrentMenu(undefined);
    },
    [menu, setCurrentMenu]
  );
  return (
    <Tooltip
      title={renderMenuTitle(menu)}
      overlayClassName={getClassName('menu-cascader-second-item-tooltip')}
    >
      <div
        className={classNames(getClassName('menu-cascader-second-item'), {
          [getClassName('menu-cascader-second-item-current')]: isMenuInAccessPath(
            currentMenu,
            menu
          ),
          [getClassName('menu-cascader-second-item-active')]: isMenuInAccessPath(activeMenu, menu),
        })}
        onMouseEnter={handleMenuEnter}
        onClick={handleMenuClick}
      >
        {renderMenuTitle(menu)}
      </div>
    </Tooltip>
  );
};

const SecondCascaderMenu: React.FC<SecondCascaderMenuProps> = ({
  menu,
  activeMenu,
  currentMenu,
  setCurrentMenu,
  getClassName,
}) => {
  return (
    <div className={getClassName('menu-cascader-second')}>
      <div
        className={classNames(getClassName('menu-cascader-second-title'), {
          [getClassName('menu-cascader-second-title-current')]: isMenuInAccessPath(
            currentMenu,
            menu
          ),
        })}
      >
        {renderMenuTitle(menu)}
      </div>
      <div className={getClassName('menu-cascader-second-item-wrap')}>
        {menu.children.map(leaf => (
          <SecondCascaderMenuItem
            getClassName={getClassName}
            menu={leaf}
            activeMenu={activeMenu}
            currentMenu={currentMenu}
            setCurrentMenu={setCurrentMenu}
          />
        ))}
      </div>
    </div>
  );
};

const CascaderMenu: React.FC<CascaderMenuProps> = ({
  menu,
  activeMenu,
  currentMenu,
  setCurrentMenu,
  getClassName = getCommonLayoutClassName,
}) => {
  return (
    <div
      className={classNames(getClassName('menu-cascader'), {
        [getClassName('menu-cascader-current')]: isMenuInAccessPath(currentMenu, menu),
      })}
    >
      {menu.children.map(second => {
        return (
          <SecondCascaderMenu
            menu={second}
            setCurrentMenu={setCurrentMenu}
            getClassName={getClassName}
            currentMenu={currentMenu}
            activeMenu={activeMenu}
          />
        );
      })}
    </div>
  );
};

export default CascaderMenu;
