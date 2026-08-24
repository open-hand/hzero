import React, { PureComponent } from 'react';
import { Layout, Menu, Icon } from 'hzero-ui';
import pathToRegexp from 'path-to-regexp';
import { Link } from 'dva/router';
import { isFunction } from 'lodash';
import { Bind } from 'lodash-decorators';

import { openTab } from 'utils/menuTab';
import intl from 'utils/intl';
import { urlToList } from '../_utils/pathTools';

import hzeroDefaultIcon from '../../assets/hzero-default-favicon.png';

import styles from './index.less';

const { Sider } = Layout;
const { SubMenu } = Menu;

const getIcon = icon => {
  if (typeof icon === 'string' && icon.indexOf('http') === 0) {
    return <img src={icon} alt="" className={`${styles.icon} sider-menu-item-img`} />;
  }
  if (typeof icon === 'string') {
    return <Icon type={icon} />;
  }
  return icon;
};

/**
 * Recursively flatten the data
 * [{path:string},{path:string}] => {path,path2}
 * @param  menu
 */
export const getFlatMenuKeys = menu =>
  menu.reduce((keys, item) => {
    keys.push(item.path);
    if (item.children) {
      return keys.concat(getFlatMenuKeys(item.children));
    }
    return keys;
  }, []);

/**
 * Recursively flatten the data
 * [{path:string},{path:string}] => {path,path2}
 * @param  menu
 */
export const getMenuIdToDataMap = menu =>
  menu.reduce(
    (map, item) =>
      // map[item.id] = item;
      // if (item.children) {
      //   return ;
      // }
      // return map;
      ({
        ...map,
        [item.id]: item,
        ...(item.children ? getMenuIdToDataMap(item.children) : {}),
      }),
    {}
  );

/**
 * Find all matched menu keys based on paths
 * @param  flatMenuKeys: [/abc, /abc/:id, /abc/:id/info]
 * @param  paths: [/abc, /abc/11, /abc/11/info]
 */
export const getMenuMatchKeys = (flatMenuKeys, paths) =>
  paths.reduce(
    (matchKeys, path) =>
      matchKeys.concat(flatMenuKeys.filter(item => pathToRegexp(item).test(path))),
    []
  );

/**
 * Find all matched menu keys based on paths
 * @param  menuConfig: menu config
 * @param  pathname: /abc/11/info
 */
export const getMenuMatchIds = (menuIdToDataMap, pathname) => {
  let currentMenuId = '';

  if (!currentMenuId) {
    const paths = urlToList(pathname).reverse();
    const menuConfig = Object.values(menuIdToDataMap);

    paths.some(path => {
      menuConfig.some(item => {
        if (pathToRegexp(item.path).test(path)) {
          currentMenuId = item.id;
        }
        return currentMenuId;
      });
      return currentMenuId;
    });
  }

  let currentMenuIdPath = [];
  while (menuIdToDataMap[currentMenuId]) {
    currentMenuIdPath.push(`${currentMenuId}`);
    currentMenuId = menuIdToDataMap[currentMenuId].parentId;
  }
  currentMenuIdPath = currentMenuIdPath.reverse();
  return currentMenuIdPath;
};

export default class SiderMenu extends PureComponent {
  constructor(props) {
    super(props);
    this.menus = props.menuData;
    this.menuIdToDataMap = getMenuIdToDataMap(props.menuData);
    this.state = {
      openKeys: this.getDefaultCollapsedSubMenus(props),
      logo: hzeroDefaultIcon,
      prevLogo: hzeroDefaultIcon,
    };
  }

  componentDidMount() {
    const { logo } = this.props;
    const { prevLogo } = this.state;
    if (prevLogo !== logo) {
      // 有 logo 开始 懒加载 logo
      if (logo) {
        const img = new Image();
        img.onload = this.updateLogo;
        img.onerror = this.setDefaultLogo;
        img.src = logo;
      }
    }
  }

  @Bind()
  updateLogo() {
    const { logo } = this.props;
    this.setState({
      logo,
    });
  }

  @Bind()
  setDefaultLogo() {
    this.setState({
      logo: hzeroDefaultIcon,
    });
  }

  //  eslint-disable-next-line
  UNSAFE_componentWillReceiveProps(nextProps) {
    if (nextProps.location.pathname !== this.props.location.pathname) {
      this.setState({
        openKeys: this.getDefaultCollapsedSubMenus(nextProps),
      });
    }
    if (nextProps.menuData !== this.props.menuData) {
      this.menus = nextProps.menuData;
      this.menuIdToDataMap = getMenuIdToDataMap(nextProps.menuData);
    }
    // 更新 logo
    if (nextProps.logo !== this.state.prevLogo) {
      if (nextProps.logo) {
        // 有 logo 开始 懒加载 logo
        const img = new Image();
        img.onload = this.updateLogo;
        img.onerror = this.setDefaultLogo;
        img.src = nextProps.logo;
      } else {
        // 没有logo,直接设置logo,为默认logo
        this.setDefaultLogo();
      }
    }
  }

  /**
   * Convert pathname to openKeys
   * /list/search/articles = > ['list','/list/search']
   * @param  props
   */
  getDefaultCollapsedSubMenus(props) {
    const {
      location: { pathname },
    } = props || this.props;
    return getMenuMatchIds(this.menuIdToDataMap, pathname, this.menuId);
  }

  /**
   * 判断是否是http链接.返回 Link 或 a
   * Judge whether it is http link.return a or Link
   * @memberof SiderMenu
   */
  @Bind()
  getMenuItemPath(item) {
    const {
      history: { createHref },
    } = this.props;
    const itemPath = this.conversionPath(item.path);
    const icon = getIcon(item.icon);
    const { target, name } = item;
    // Is it a http link
    if (/^https?:\/\//.test(itemPath)) {
      return (
        <a href={itemPath} target={target}>
          {icon}
          <span>{name && intl.get(name).d(name)}</span>
        </a>
      );
    }
    return (
      <a
        // href={`#${itemPath}`}
        href={createHref({ pathname: itemPath })}
        target={target}
        onClick={e => {
          if (isFunction(e && e.preventDefault)) {
            e.preventDefault();
          }
          this.menuId = item.id;
          // 之前的逻辑
          if (this.props.isMobile) {
            if (isFunction(this.props.onCollapse)) {
              this.props.onCollapse(true);
            }
          }
          // 打开菜单
          openTab({
            icon: item.icon,
            title: item.name,
            key: item.path,
            closable: true,
            search: item.search,
          });
        }}
      >
        {icon}
        <span>{name && intl.get(name).d(name)}</span>
      </a>
    );
  }

  /**
   * get SubMenu or Item
   */
  getSubMenuOrItem = item => {
    if (item.children && item.children.some(child => child.name)) {
      const childrenItems = this.getNavMenuItems(item.children);
      // 当无子菜单时就不展示菜单
      if (childrenItems && childrenItems.length > 0) {
        return (
          <SubMenu
            title={
              item.icon ? (
                <span>
                  {getIcon(item.icon)}
                  <span>{item.name && intl.get(item.name).d(item.name)}</span>
                </span>
              ) : (
                item.name
              )
            }
            key={item.id}
          >
            {childrenItems}
          </SubMenu>
        );
      }
      return null;
    }
    return <Menu.Item key={item.id}>{this.getMenuItemPath(item)}</Menu.Item>;
  };

  /**
   * 获得菜单子节点
   * @memberof SiderMenu
   */
  getNavMenuItems = menusData => {
    if (!menusData) {
      return [];
    }
    return menusData
      .filter(item => item.name && !item.hideInMenu)
      .map(item => {
        // make dom
        const ItemDom = this.getSubMenuOrItem(item);
        return this.checkPermissionItem(item.authority, ItemDom);
      })
      .filter(item => item);
  };

  // Get the currently selected menu
  getSelectedMenuKeys = () => {
    const {
      location: { pathname },
    } = this.props;
    return getMenuMatchIds(this.menuIdToDataMap, pathname, this.menuId);
  };

  // conversion Path
  // 转化路径
  conversionPath = path => {
    if (path && path.indexOf('http') === 0) {
      return path;
    }
    return `/${path || ''}`.replace(/\/+/g, '/');
  };

  // permission to check
  checkPermissionItem = (authority, ItemDom) => {
    if (this.props.Authorized && this.props.Authorized.check) {
      const { check } = this.props.Authorized;
      return check(authority, ItemDom);
    }
    return ItemDom;
  };

  isMainMenu = key => key && this.menus.some(item => item.id === key);

  handleOpenChange = openKeys => {
    const lastOpenKey = openKeys[openKeys.length - 1];
    const moreThanOne = openKeys.filter(openKey => this.isMainMenu(openKey)).length > 1;
    this.setState({
      openKeys: moreThanOne ? [lastOpenKey] : [...openKeys],
    });
    // this.setState({
    //   openKeys,
    // });
  };

  render() {
    const { title, collapsed, onCollapse } = this.props;
    const { logo, openKeys } = this.state;
    // Don't show popup menu when it is been collapsed
    const menuProps = collapsed
      ? {}
      : {
          openKeys,
          onOpenChange: this.handleOpenChange,
        };
    // if pathname can't match, use the nearest parent's key
    let selectedKeys = this.getSelectedMenuKeys();
    if (!selectedKeys.length) {
      selectedKeys = [openKeys[openKeys.length - 1]];
    }
    return (
      <Sider
        trigger={null}
        collapsible
        collapsed={collapsed}
        breakpoint="lg"
        onCollapse={onCollapse}
        width={256}
        className={styles.sider}
      >
        <div className={styles.logo} key="logo">
          <Link to="/">
            <img src={logo} alt="" />
            {!collapsed && <h1 title={title}>{title}</h1>}
          </Link>
        </div>
        <Menu
          key="menu"
          theme="dark"
          mode="inline"
          {...menuProps}
          selectedKeys={selectedKeys}
          style={{ padding: '16px 0', width: '100%', flex: 'auto', overflowY: 'auto' }}
        >
          {this.getNavMenuItems(this.menus)}
        </Menu>
      </Sider>
    );
  }
}
