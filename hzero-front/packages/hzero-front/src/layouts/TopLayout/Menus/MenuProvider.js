/**
 * MenuProvider
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-05-24
 * @copyright 2019-05-24 © HAND
 */

import React, { Component } from 'react';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';

import { computeActiveMenus, computeMenus, MenuContext } from './utils';

const { isEmpty } = require('lodash');

class MenuProvider extends Component {
  constructor(props) {
    super(props);
    const { activeTabKey, menus } = props;
    const { leafMenus: computeLeafMenusRet, menus: computeMenusRet } = computeMenus(menus);
    this.state = {
      activeMenus: [], // 激活的 Tab 对应的菜单
      menus: computeMenusRet, // 菜单
      leafMenus: computeLeafMenusRet, // 三级菜单
      prevActiveTabKey: activeTabKey, // 存储上一次的 activeTabKey
      prevMenus: menus, // 存储上一次的 menus
      // activeMenu: null, // 当前激活的 一级菜单
      // hoverMenu: null, // 当前hover的 一级菜单
    };
  }

  static getDerivedStateFromProps(nextProps, prevState) {
    const { activeTabKey, menus } = nextProps;
    const { prevActiveTabKey, prevMenus } = prevState;
    const nextState = {};
    if (prevMenus !== menus) {
      if (isEmpty(menus)) {
        // 还没有菜单
        nextState.leafMenus = [];
        nextState.menus = [];
      } else {
        const { leafMenus: computeLeafMenusRet, menus: computeMenusRet } = computeMenus(menus);
        // 叶子节点 和 新的菜单
        nextState.leafMenus = computeLeafMenusRet;
        nextState.menus = computeMenusRet;
        // INFO: 由 menus 变化引起的 菜单重新计算 需要 更新 一级菜单
        if (computeMenusRet && computeMenusRet[0]) {
          // eslint-disable-next-line prefer-destructuring
          nextState.mainMenu = computeMenusRet[0];
        }
      }
      nextState.prevMenus = menus;
    }
    if (activeTabKey !== prevActiveTabKey) {
      // 重新计算 激活的 menu
      nextState.activeTabKey = activeTabKey;
      nextState.activeMenus = computeActiveMenus(
        nextState.leafMenus || prevState.leafMenus,
        activeTabKey
      );
      nextState.prevActiveTabKey = activeTabKey;
    }
    if (!isEmpty(nextState)) {
      return nextState;
    }
    return null;
  }

  @Bind()
  handleMainMenuChange(mainMenu) {
    if (mainMenu) {
      this.setState({
        mainMenu,
      });
    }
  }

  render() {
    const { language, children, menuLoad, menuQuickIndex } = this.props;
    const { menus, leafMenus, activeMenus, mainMenu } = this.state;
    return (
      <MenuContext.Provider
        value={{
          menus,
          activeMenus,
          leafMenus,
          language,
          mainMenu,
          menuLoad,
          menuQuickIndex,
          onMainMenuChange: this.handleMainMenuChange,
        }}
      >
        {children}
      </MenuContext.Provider>
    );
  }
}

export default connect(({ global = {} }) => ({
  language: global.language,
  menus: global.menu,
  menuLoad: global.menuLoad,
  activeTabKey: global.activeTabKey,
  menuQuickIndex: global.menuQuickIndex,
}))(MenuProvider);
