/**
 * 侧边菜单布局
 * 基于 SideLayout 改造
 * 初始显示一级菜单, 之后展开所有菜单到3级
 */

import React from 'react';
import classNames from 'classnames';
import { Icon } from 'hzero-ui';
import { connect } from 'dva';

import { getCurrentOrganizationId } from 'utils/utils';

import DefaultLayoutAction from '../components/DefaultLayoutAction';
import DefaultCheckUserSafe from '../components/DefaultCheckUserSafe';
import DefaultListenAccessToken from '../components/DefaultListenAccessToken';
import DefaultListenWebSocket from '../components/DefaultListenWebSocket';
import DefaultListenFavicon from '../components/DefaultListenFavicon';

import { defaultGetClassName } from './utils';

import NormalHeader from '../DefaultLayout/components/NormalHeader';
import NormalNav from '../DefaultLayout/components/NormalNav';
import NormalContent from '../DefaultLayout/components/NormalContent';

import SideLineMenu from './components/SideLineMenu';

import './styles.less';

const SideLayout = ({
  getClassName = defaultGetClassName,
  currentUser = {},
  roleList,
  extraHeaderRight,
  logout,
  fetchRoleList,
  hierarchicalSelectList,
  fetchDataHierarchiesList,
  isModal,
  isSelect,
  dispatch,
}) => {
  // const [collapsed, setCollapsed] = useDebounceState(false);
  const [collapsed, setCollapsed] = React.useState(false);
  const getHeaderClassName = React.useCallback((...paths) => getClassName('header', ...paths), [
    getClassName,
  ]);
  const getNavClassName = React.useCallback((...paths) => getClassName('nav', ...paths), [
    getClassName,
  ]);
  const getContentClassName = React.useCallback((...paths) => getClassName('content', ...paths), [
    getClassName,
  ]);
  const handleSetCollapsed = React.useCallback(() => {
    setCollapsed(!collapsed);
  }, [collapsed]);
  const sideMenuRef = React.useRef();
  const WrapSideMenu = React.useMemo(
    () => (props) => <SideLineMenu {...props} imperativeRef={sideMenuRef} />,
    [sideMenuRef, SideLineMenu]
  );
  /* 进入到菜单中 */
  const handleSearchMouseEnter = React.useCallback(() => {
    if (sideMenuRef.current) {
      sideMenuRef.current.setActiveMenu();
    }
  }, [sideMenuRef]);
  React.useEffect(() => {
    if (sideMenuRef.current) {
      sideMenuRef.current.setMaskHeight();
    }
  }, [sideMenuRef]);
  const { logo, title } = currentUser;

  return (
    <div
      className={classNames(getClassName('container'), {
        [getClassName('container', 'collapsed')]: collapsed,
      })}
    >
      <div className={getClassName('header')}>
        <NormalHeader
          dispatch={dispatch}
          logo={logo}
          title={title}
          realName={currentUser.realName}
          roleName={currentUser.currentRoleName}
          userAvatar={currentUser.imageUrl}
          dataHierarchyFlag={currentUser.dataHierarchyFlag}
          hierarchicalSelectList={hierarchicalSelectList}
          fetchRoleList={fetchRoleList}
          isModal={isModal}
          isSelect={isSelect}
          fetchDataHierarchiesList={fetchDataHierarchiesList}
          logout={logout}
          roleList={roleList}
          extraRight={extraHeaderRight}
          collapsed={collapsed}
          getClassName={getHeaderClassName}
        />
      </div>
      <div className={getClassName('body')}>
        <div className={getClassName('nav')}>
          <NormalNav
            collapsed={collapsed}
            getClassName={getNavClassName}
            onSearchMouseEnter={handleSearchMouseEnter}
            components={{
              Menu: WrapSideMenu,
            }}
          />
        </div>
        <div className={getClassName('content')}>
          <Icon
            className={getClassName('collapsed', 'trigger')}
            type={collapsed ? 'menu-unfold' : 'menu-fold'}
            onClick={handleSetCollapsed}
          />
          <NormalContent getClassName={getContentClassName} />
        </div>
      </div>
      <DefaultLayoutAction />
      <DefaultCheckUserSafe />
      <DefaultListenAccessToken />
      <DefaultListenWebSocket />
      <DefaultListenFavicon />
    </div>
  );
};

export default connect(
  ({ user = {}, global = {} }) => ({
    currentUser: user.currentUser, // 当前用户
    roleList: user.roleList, // 当前角色
    hierarchicalSelectList: user.hierarchicalSelectList, // 数据层级下拉框列表
    isModal: user.isModal, // 是否显示模态框形式数据层级配置
    isSelect: user.isSelect, // 是否显示下拉框形式数据层级配置
    menu: global.menu, // 菜单
    routerData: global.routerData, // 路由配置
    activeTabKey: global.activeTabKey, // 当前路由
    tabs: global.tabs, // 所有 tab 页
    language: global.language, // 当前语言
    // count: global.count, // 当前消息计数
  }),

  (dispatch) => ({
    dispatch,
    logout() {
      return dispatch({
        type: 'login/logout',
      });
    },
    fetchRoleList() {
      return dispatch({
        type: 'user/fetchRoleList',
        payload: { organizationId: getCurrentOrganizationId() },
      });
    },
    fetchDataHierarchiesList() {
      return dispatch({
        type: 'user/queryDataHierarchies',
        payload: { organizationId: getCurrentOrganizationId() },
      });
    },
  }),
  null,
  { pure: false }
)(SideLayout);

// export default SideLayout;
