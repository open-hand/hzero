import React from 'react';
import classNames from 'classnames';
import { isFunction } from 'lodash';
import { Bind } from 'lodash-decorators';
import { Tooltip } from 'hzero-ui';

import { MenuContext, openMenu, renderMenuTitle } from './utils';

import styles from './styles.less';

class SubMenuItem extends React.PureComponent {
  /**
   * 当前节点 只有二级菜单
   */
  @Bind()
  handleSubMenuClick(e) {
    e.preventDefault();
    e.stopPropagation();
    const { onClick, subMenu } = this.props;
    const [leafMenu] = subMenu.children;
    // TODO: 直接在 LeafMenu 处理点击事件
    // 打开菜单
    openMenu(leafMenu);
    if (isFunction(onClick)) {
      onClick(leafMenu, subMenu);
    }
  }

  @Bind()
  renderSubMenuItemTitle() {
    const { subMenu } = this.props;
    return renderMenuTitle(subMenu);
  }

  render() {
    const { active, hover, style, subMenu } = this.props;
    const { isOnlySubMenu } = subMenu;
    const item = (
      <div
        className={classNames(
          styles['sub-menu-item'],
          {
            [styles['sub-menu-item-active']]: active,
            [styles['sub-menu-item-hover']]: hover,
            'hzero-sub-menu-item-active': active,
            'hzero-sub-menu-item-hover': hover,
          },
          'hzero-sub-menu-item'
        )}
        style={isOnlySubMenu ? { ...style, cursor: 'pointer' } : style}
        onClick={isOnlySubMenu ? this.handleSubMenuClick : undefined}
      >
        <div
          className={classNames(
            styles['sub-menu-item-content'],
            {
              [styles['sub-menu-item-content-only']]: isOnlySubMenu,
            },
            'hzero-sub-menu-item-content'
          )}
        >
          <div className={classNames(styles['sub-menu-item-title'], 'hzero-sub-menu-item-title')}>
            <MenuContext.Consumer>{this.renderSubMenuItemTitle}</MenuContext.Consumer>
          </div>
        </div>
      </div>
    );
    return isOnlySubMenu ? (
      <Tooltip
        overlayClassName={styles['menu-tooltip']}
        title={<MenuContext.Consumer>{this.renderSubMenuItemTitle}</MenuContext.Consumer>}
      >
        {item}
      </Tooltip>
    ) : (
      item
    );
  }
}

export default SubMenuItem;
