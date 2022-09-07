import React from 'react';
import { Bind } from 'lodash-decorators';
import { isFunction } from 'lodash';
import { Tooltip } from 'hzero-ui';

import { MenuContext, openMenu, renderMenuTitle } from './utils';

import styles from './styles.less';

class LeafMenuItem extends React.PureComponent {
  @Bind()
  renderLeafMenuItemTitle() {
    const { leafMenu } = this.props;
    return renderMenuTitle(leafMenu);
  }

  @Bind()
  handleLeafMenuClick(e) {
    e.preventDefault();
    e.stopPropagation();
    const { leafMenu, onClick } = this.props;
    // TODO: 直接在 LeafMenu 处理点击事件
    // 打开菜单
    openMenu(leafMenu);
    if (isFunction(onClick)) {
      onClick(leafMenu);
    }
  }

  render() {
    const { active } = this.props;
    const classNames = [styles['leaf-menu-item'], 'hzero-leaf-menu-item'];
    if (active) {
      classNames.push(styles['leaf-menu-item-active'], 'hzero-leaf-menu-item-active');
    }
    return (
      <Tooltip
        overlayClassName={styles['menu-tooltip']}
        title={<MenuContext.Consumer>{this.renderLeafMenuItemTitle}</MenuContext.Consumer>}
      >
        <a
          // title={renderMenuTitle(leafMenu)}
          className={classNames.join(' ')}
          onClick={this.handleLeafMenuClick}
        >
          <MenuContext.Consumer>{this.renderLeafMenuItemTitle}</MenuContext.Consumer>
        </a>
      </Tooltip>
    );
  }
}

export default LeafMenuItem;
