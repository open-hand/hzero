import React from 'react';
import { Bind } from 'lodash-decorators';

import SubMenuWrap from './SubMenuWrap';

import {
  MenuContext,
  menuItemHeight as defaultMenuItemHeight,
  pageHeaderHeight as defaultPageHeaderHeight,
  renderIcon,
  renderMenuTitle,
} from './utils';
import styles from './styles.less';

class MainMenuItem extends React.PureComponent {
  constructor(props) {
    super(props);
    const { forceRender = false } = props;
    this.state = {
      loaded: forceRender,
      subMenuWrapStyle: {}, // 默认为控对象
    };

    this.cacheSubMenuWrapStyle = new Map();
    this.mainMenuItemRef = React.createRef();
  }

  @Bind()
  renderMainMenuItemTitle() {
    const { menu } = this.props;
    return renderMenuTitle(menu);
  }

  // TODO: 只能cache本一级菜单的样式, 需要提升到 Menu 中
  getSubMenuWrapStyle(mouseTop = defaultPageHeaderHeight) {
    const { menuItemHeight = defaultMenuItemHeight } = this.props;
    let totalHeight;
    if (document.compatMode === 'BackCompat') {
      totalHeight = document.body.clientHeight;
    } else {
      totalHeight = document.documentElement.clientHeight;
    }
    const cacheKey = `${totalHeight}---${mouseTop}`;
    let subMenuWrapStyle;
    if (!this.cacheSubMenuWrapStyle.has(cacheKey)) {
      const { pageHeaderHeight = defaultPageHeaderHeight } = this.props;
      const max = totalHeight - pageHeaderHeight;
      const min = Math.min(totalHeight - mouseTop + menuItemHeight / 2, max);
      subMenuWrapStyle = {
        minHeight: min,
        maxHeight: max,
      };
      this.cacheSubMenuWrapStyle.set(cacheKey, subMenuWrapStyle);
    }
    return subMenuWrapStyle || this.cacheSubMenuWrapStyle.get(cacheKey);
  }

  @Bind()
  handleMainMenuItemClick(e) {
    const { onClick, menu, offsetTop = 48 } = this.props;
    const { loaded } = this.state;
    const nextPartialState = {
      subMenuWrapStyle: this.getSubMenuWrapStyle(
        // FIXME: 如何获取元素相对于屏幕的高度距离
        this.mainMenuItemRef.current.offsetTop -
          this.mainMenuItemRef.current.parentElement.parentElement.scrollTop +
          offsetTop
        // e.clientY
      ),
    };
    if (!loaded) {
      nextPartialState.loaded = true;
    }
    this.setState(nextPartialState);
    onClick(e, menu);
  }

  @Bind()
  handleMouseEnter(e) {
    const { onHover, menu } = this.props;
    onHover(e, menu);
  }

  @Bind()
  handleMouseLeave(e) {
    const { onCancelHover, menu } = this.props;
    onCancelHover(e, menu);
  }

  render() {
    const { menu, activeMenus, active, hover } = this.props;
    const { loaded, subMenuWrapStyle } = this.state;
    const classNames = [styles['main-menu-item'], 'hzero-main-menu-item'];
    const tabMenuActive = menu === activeMenus[0];
    if (tabMenuActive) {
      classNames.push(styles['main-menu-item-hover'], 'hzero-main-menu-item-tab-active');
    }
    if (active) {
      classNames.push(styles['main-menu-item-active'], 'hzero-main-menu-item-active');
    }
    if (hover) {
      classNames.push(styles['main-menu-item-hover'], 'hzero-main-menu-item-hover');
    }
    const menuContentClass = [styles['main-menu-item-content'], 'hzero-main-menu-item-content'];
    const menuItemTitle = [styles['main-menu-item-title'], 'hzero-main-menu-item-title'];
    return (
      <li
        className={classNames.join(' ')}
        onClick={this.handleMainMenuItemClick}
        ref={this.mainMenuItemRef}
      >
        <div
          className={menuContentClass.join(' ')}
          key="main-menu-content"
          onMouseEnter={this.handleMouseEnter}
          onMouseLeave={this.handleMouseLeave}
        >
          {renderIcon(menu)}
          <span className={menuItemTitle.join(' ')}>
            <MenuContext.Consumer>{this.renderMainMenuItemTitle}</MenuContext.Consumer>
          </span>
        </div>
        {loaded && (
          <SubMenuWrap
            key="sub-menu-wrap"
            style={subMenuWrapStyle}
            menu={menu}
            activeMenus={activeMenus}
            onLeafMenuClick={this.handleLeafMenuClick}
          />
        )}
        {loaded && (
          <div
            className={[styles['sub-menu-mask'], 'hzero-sub-menu-mask'].join(' ')}
            onClick={this.handleSubMenuMaskClick}
          />
        )}
      </li>
    );
  }

  /**
   * 菜单右侧mask点击事件
   */
  @Bind()
  handleSubMenuMaskClick(e) {
    e.stopPropagation();
    const { onSubMenuMaskClick } = this.props;
    onSubMenuMaskClick();
  }

  @Bind()
  handleLeafMenuClick() {
    const { onLeafMenuClick } = this.props;
    onLeafMenuClick();
  }
}

export default MainMenuItem;
