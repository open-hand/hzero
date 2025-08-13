/**
 * Menu
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-05-24
 * @copyright 2019-05-24 © HAND
 */

import React, { Component } from 'react';
import { Bind } from 'lodash-decorators';
import { uniq } from 'lodash';
import { Menu, Spin } from 'hzero-ui';

import Icons from 'components/Icons';

import intl from 'utils/intl';

import { openMenu } from '../../components/DefaultMenu/utils';

import { isLeafMenu } from './utils';
import { getStyle } from '../utils';

export default class Menus extends Component {
  constructor(props) {
    super(props);
    const { activeMenus = [] } = props;
    const selectedKeys = [];
    const openMenuKeys = [];
    for (let i = 0; i < activeMenus.length - 1; i++) {
      selectedKeys.push(`${activeMenus[i].id}`);
      openMenuKeys.push(`${activeMenus[i].id}`);
    }
    openMenuKeys.pop();
    this.state = {
      selectedKeys,
      openMenuKeys,
    };
  }

  static getDerivedStateFromProps(nextProps, prevState) {
    const { prevActiveMenus, openMenuKeys = [] } = prevState;
    const { mainMenu, activeMenus = [] } = nextProps;
    const curActiveMainMenu = activeMenus[activeMenus.length - 1];
    if (
      mainMenu === curActiveMainMenu || // 切换的Tab 属于当前菜单
      prevActiveMenus !== activeMenus // 需要保证 不会每次都变化
    ) {
      const selectedKeys = [];
      const openKeys = [];
      for (let i = 0; i < activeMenus.length - 1; i++) {
        selectedKeys.push(`${activeMenus[i].id}`);
        openKeys.push(`${activeMenus[i].id}`);
      }
      openKeys.pop();
      return {
        selectedKeys,
        openMenuKeys: uniq([...openMenuKeys, ...openKeys]),
      };
    }
    return null;
  }

  @Bind()
  handleMenuItemClick({ item }) {
    const { 'data-hzero-menu': menu } = item.props;
    // 打开菜单
    openMenu(menu);
  }

  @Bind()
  handleMenuOpenChange(openMenuKeys = []) {
    this.setState({
      openMenuKeys,
    });
  }

  renderTOPMenu() {
    const { mainMenu: { children = [] } = {} } = this.props;
    return children.map((m) => this.renderMenu(m, true));
  }

  renderMenu(menu, renderIcon = false) {
    if (isLeafMenu(menu)) {
      return this.renderMenuItem(menu, renderIcon);
    } else {
      return this.renderSubMenu(menu, renderIcon);
    }
  }

  renderMenuItem(menu, renderIcon = false) {
    return (
      <Menu.Item key={menu.id} data-hzero-menu={menu}>
        {renderIcon && (
          <Icons
            type={menu.icon || 'development-management'}
            style={{ width: '12px', marginRight: 8 }}
          />
        )}
        {menu.name && intl.get(menu.name).d(menu.name)}
      </Menu.Item>
    );
  }

  renderSubMenu(menu, renderIcon = false) {
    const { children = [] } = menu;
    return (
      <Menu.SubMenu
        key={menu.id}
        title={
          <>
            {renderIcon && (
              <Icons
                type={menu.icon || 'development-management'}
                style={{ width: '12px', marginRight: 8 }}
              />
            )}
            {menu.name && intl.get(menu.name).d(menu.name)}
          </>
        }
      >
        {children.map((childMenu) => this.renderMenu(childMenu))}
      </Menu.SubMenu>
    );
  }

  render() {
    const { mainMenu, menuLoad } = this.props;
    const { selectedKeys = [], openMenuKeys = [] } = this.state;
    // theme 变为 dark
    // arrow 样式 要是 白色的
    if (menuLoad) {
      return (
        <Menu
          mode="inline"
          theme="dark"
          openKeys={openMenuKeys}
          selectedKeys={selectedKeys}
          onOpenChange={this.handleMenuOpenChange}
          onClick={this.handleMenuItemClick}
        >
          {this.renderTOPMenu(mainMenu, true)}
        </Menu>
      );
    } else {
      return <Spin spinning size="large" className={getStyle('spin')} />;
    }
  }
}
