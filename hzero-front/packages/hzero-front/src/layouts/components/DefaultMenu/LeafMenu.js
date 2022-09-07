import React from 'react';
import { Bind } from 'lodash-decorators';
import { isFunction } from 'lodash';

import LeafMenuItemWrap from './LeafMenuItemWrap';

import { getMenuKey } from './utils';

import styles from './styles.less';

class LeafMenu extends React.PureComponent {
  @Bind()
  handleLeafMenuClick(leafMenu, subMenu) {
    const { onClick } = this.props;
    if (isFunction(onClick)) {
      onClick(leafMenu, subMenu);
    }
  }

  @Bind()
  handleLeafMenuMouseEnter(subMenu) {
    const { onMouseEnter } = this.props;
    if (isFunction(onMouseEnter)) {
      onMouseEnter(subMenu);
    }
  }

  @Bind()
  handleLeafMenuMouseLeave(subMenu) {
    const { onMouseLeave } = this.props;
    if (isFunction(onMouseLeave)) {
      onMouseLeave(subMenu);
    }
  }

  render() {
    const { menu, activeMenus, getSubMenuStyle, minHeight } = this.props;
    const lineLen = menu.children.length - 1;
    if (menu.allChildrenAreOnlySubMenu) {
      return null;
    }
    return (
      <div
        className={[styles['leaf-menu'], 'hzero-leaf-menu'].join(' ')}
        style={{
          ...getSubMenuStyle(menu),
          minHeight,
        }}
      >
        {menu.children.map((subMenu, index) => {
          const { isOnlySubMenu } = subMenu;
          const prevSubMenu = menu.children[index - 1] || { isOnlySubMenu: false };
          const nextSubMenu = menu.children[index + 1] || { isOnlySubMenu: false };
          const prevSubMenuIsOnlySubMenu = prevSubMenu.isOnlySubMenu;
          const nextSubMenuIsOnlySubMenu = nextSubMenu.isOnlySubMenu;
          return (
            <React.Fragment key={getMenuKey(subMenu)}>
              <LeafMenuItemWrap
                subMenu={subMenu}
                isLast={index >= lineLen}
                prevSubMenuIsOnlySubMenu={prevSubMenuIsOnlySubMenu}
                nextSubMenuIsOnlySubMenu={nextSubMenuIsOnlySubMenu}
                activeMenus={activeMenus}
                onClick={this.handleLeafMenuClick}
                onMouseEnter={this.handleLeafMenuMouseEnter}
                onMouseLeave={this.handleLeafMenuMouseLeave}
              />
              {index < lineLen && !isOnlySubMenu && (
                <div
                  className={[
                    styles['leaf-menu-item-wrap-line'],
                    'hzero-leaf-menu-item-wrap-line',
                  ].join(' ')}
                />
              )}
            </React.Fragment>
          );
        })}
      </div>
    );
  }
}

export default LeafMenu;
