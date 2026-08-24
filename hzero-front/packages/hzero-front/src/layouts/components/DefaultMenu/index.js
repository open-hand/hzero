/**
 * @date 2019-03-05
 * @author WY yang.wang06@hand-china.com
 * @copyright ® HAND 2019
 * @react
 * <DefaultMenu>
 *  <MainMenuItem>
 *    <SubMenuWrap>
 *      <SubMenu>
 *        <SubMenuItem />
 *      </SubMenu>
 *      <LeafMenu>
 *        <LeafMenuItemWrap>
 *          <LeafMenuItem />
 *        </LeafMenuItemWrap>
 *      </LeafMenu>
 *    </SubMenuWrap>
 *   </MainMenuItem>
 * </DefaultMenu>
 */

/**
 * update at 2019-09-03
 * 如果 只有二级菜单, 则不显示三级菜单, 这里保留三级菜单的结构(三级菜单高度计算需要)
 * 判断条件 subMenu.children.length === 1 && subMenu.children[0] === subMenu;
 */

import React from 'react';
import { isEmpty, omit } from 'lodash';
import { connect } from 'dva';
import { Spin } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import MainMenuItem from './MainMenuItem';

import { computeActiveMenus, computeMenus, getMenuKey, MenuContext } from './utils';

import styles from './styles.less';

const omitProps = [
  'menus',
  'activeTabKey',
  'tabs',
  'collapsed',
  'className',
  'language',
  'dispatch',
  'offsetTop', // 菜单距离顶部的高度
];

class DefaultMenu extends React.PureComponent {
  constructor(props) {
    super(props);
    const { activeTabKey, menus } = props;
    const { leafMenus: computeLeafMenusRet, menus: computeMenusRet } = computeMenus(menus);
    this.state = {
      activeMenus: [], // 激活的 Tab 对应的菜单
      menus: computeMenusRet, // 菜单
      leafMenus: computeLeafMenusRet, // 三级菜单
      prevActiveTabKey: activeTabKey, // 存储上一次的 activeTabKey
      prevMenuQuickIndex: undefined, // 存储上一次的 menuQuickIndex
      prevMenus: menus, // 存储上一次的 menus
      activeMenu: null, // 当前激活的 一级菜单
      hoverMenu: null, // 当前hover的 一级菜单
    };
  }

  static getDerivedStateFromProps(nextProps, prevState) {
    const { activeTabKey, menus, menuQuickIndex } = nextProps;
    const { prevActiveTabKey, prevMenus, prevMenuQuickIndex } = prevState;
    const nextState = {};
    if (prevMenus !== menus) {
      if (isEmpty(menus)) {
        nextState.leafMenus = [];
        nextState.menus = [];
      } else {
        // 还没有菜单
        const { leafMenus: computeLeafMenusRet, menus: computeMenusRet } = computeMenus(menus);
        // 叶子节点 和 新的菜单
        nextState.leafMenus = computeLeafMenusRet;
        nextState.menus = computeMenusRet;
        if (menuQuickIndex && menuQuickIndex !== prevMenuQuickIndex) {
          nextState.activeMenus = computeMenusRet.filter((item) => {
            return item.quickIndex === menuQuickIndex;
          });
        } else {
          nextState.activeMenus = computeActiveMenus(nextState.leafMenus, activeTabKey);
        }
      }
      nextState.prevMenus = menus;
    }
    if (activeTabKey !== prevActiveTabKey && !nextState.activeMenus) {
      // 重新计算 激活的 menu
      nextState.activeTabKey = activeTabKey;
      nextState.activeMenus = computeActiveMenus(
        nextState.leafMenus || prevState.leafMenus,
        activeTabKey
      );
      nextState.prevActiveTabKey = activeTabKey;
    }
    return !isEmpty(nextState) ? nextState : null;
  }

  @Bind()
  handleMainMenuItemClick(e, activeMenu) {
    e.stopPropagation();
    this.setState({
      activeMenu,
    });
  }

  @Bind()
  handleCancelActiveMenu() {
    this.setState({
      activeMenu: null,
    });
  }

  // FIXME: 换成标准 ICON(icon-font) 后 就不需要在 父组件 保存 hover 的menu 了
  @Bind()
  handleMainMenuItemHover(e, hoverMenu) {
    e.stopPropagation();
    this.setState({
      hoverMenu,
    });
  }

  @Bind()
  handleCancelHoverMenu() {
    this.setState({
      hoverMenu: null,
    });
  }

  render() {
    const { collapsed, className, language, offsetTop = 48, menuLoad } = this.props;
    const { menus, activeMenus, activeMenu, hoverMenu } = this.state;
    const passProps = omit(this.props, omitProps);
    const menuClassNames = [styles['main-menu'], 'hzero-main-menu']; // 加上固定类名
    if (collapsed) {
      menuClassNames.push(styles['main-menu-collapsed'], 'hzero-main-menu-collapsed');
    }
    // 加入固定的类名，方便后续切换主题
    const mainMenuWrapClassNames = [styles['main-menu-wrap'], className, 'hzero-main-menu-wrap'];
    return (
      <>
        {menuLoad ? (
          <div {...passProps} className={mainMenuWrapClassNames.join(' ')}>
            <ul className={menuClassNames.join(' ')}>
              <MenuContext.Provider value={language}>
                {menus.map((mainMenu) => (
                  <MainMenuItem
                    onClick={this.handleMainMenuItemClick}
                    onHover={this.handleMainMenuItemHover}
                    onCancelHover={this.handleCancelHoverMenu}
                    onSubMenuMaskClick={this.handleCancelActiveMenu}
                    onLeafMenuClick={this.handleCancelActiveMenu}
                    key={getMenuKey(mainMenu)}
                    menu={mainMenu}
                    activeMenus={activeMenus}
                    active={activeMenu === mainMenu}
                    hover={hoverMenu === mainMenu}
                    offsetTop={offsetTop}
                  />
                ))}
              </MenuContext.Provider>
            </ul>
          </div>
        ) : (
          <Spin size="large" className={styles['main-menu-spin']} spinning />
        )}
      </>
    );
  }
}

export default connect(({ global = {} }) => ({
  language: global.language,
  menus: global.menu,
  menuLoad: global.menuLoad,
  menuQuickIndex: global.menuQuickIndex,
  activeTabKey: global.activeTabKey,
  tabs: global.tabs,
}))(DefaultMenu);
