/**
 * 所有的一级菜单, 当前激活的 一级菜单
 *
 * MainMenu
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-05-24
 * @copyright 2019-05-24 © HAND
 */

import React, { Component } from 'react';
// import PropTypes from 'prop-types';
import { Bind } from 'lodash-decorators';
import { Tabs } from 'hzero-ui';

import intl from 'utils/intl';

import { getStyle } from '../utils';

class MainMenu extends Component {
  // static propTypes = {
  //   onMainMenuChange: PropTypes.func.isRequired,
  // };

  state = {
    defaultActiveKey: null,
    loadFlag: false,
  };

  @Bind()
  handleMainMenuChange(menuId) {
    const { onMainMenuChange, menus = [] } = this.props;
    const changeMainMenu = menus.find((menu) => `${menu.id}` === menuId);
    if (changeMainMenu) {
      onMainMenuChange(changeMainMenu);
    }
  }

  static getDerivedStateFromProps(nextProps, prevState) {
    const { loadFlag } = prevState;
    const { menuLoad, menuQuickIndex, onMainMenuChange, menus = [] } = nextProps;
    if (menuLoad && !loadFlag && menuQuickIndex) {
      const { id } = menus.filter((item) => item.quickIndex === menuQuickIndex)[0] || {};
      if (id) {
        const changeMainMenu = menus.find((menu) => `${menu.id}` === id);
        if (changeMainMenu) {
          onMainMenuChange(changeMainMenu);
        }
        return { defaultActiveKey: id, loadFlag: true };
      } else {
        return { loadFlag: true };
      }
    } else if (menuLoad && !loadFlag) {
      return { loadFlag: true };
    }
  }

  render() {
    const { menus, language, extraRight = null, menuLoad } = this.props;
    const { defaultActiveKey, loadFlag } = this.state;
    return (
      <>
        {menuLoad && loadFlag ? (
          <Tabs
            type="card"
            className={getStyle('main-menu')}
            onChange={this.handleMainMenuChange}
            tabBarExtraContent={extraRight}
            defaultActiveKey={defaultActiveKey}
          >
            {menus.map((menu) => (
              <Tabs.TabPane
                key={menu.id}
                tab={language ? menu.name && intl.get(menu.name).d(menu.name) : '...'}
              />
            ))}
          </Tabs>
        ) : null}
      </>
    );
  }
}

export default MainMenu;
