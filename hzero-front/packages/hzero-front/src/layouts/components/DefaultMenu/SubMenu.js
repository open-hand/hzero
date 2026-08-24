import React from 'react';

import styles from './styles.less';
import SubMenuItem from './SubMenuItem';
import { getMenuKey } from './utils';

class SubMenu extends React.PureComponent {
  render() {
    const {
      menu,
      activeMenus,
      hoverSubMenu,
      getSubMenuItemStyle,
      getSubMenuStyle,
      minHeight,
      onClick,
    } = this.props;
    return (
      <div
        className={[styles['sub-menu'], 'hzero-sub-menu'].join(' ')}
        style={{
          ...getSubMenuStyle(menu),
          minHeight,
        }}
      >
        {menu.children.map((subMenu, index) => (
          <SubMenuItem
            key={getMenuKey(subMenu)}
            onClick={onClick}
            subMenu={subMenu}
            active={activeMenus[1] === subMenu}
            hover={hoverSubMenu === subMenu}
            style={getSubMenuItemStyle(subMenu, index >= menu.children.length - 1)}
          />
        ))}
      </div>
    );
  }
}

export default SubMenu;
