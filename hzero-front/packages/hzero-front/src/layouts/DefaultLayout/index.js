/**
 * NormalLayout
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019/8/27
 * @copyright 2019 © HAND
 */

import React, { Component } from 'react';
import DocumentTitle from 'react-document-title';
import classNames from 'classnames';
import { connect } from 'dva';
import { Bind, Throttle } from 'lodash-decorators';
import { Icon } from 'hzero-ui';

import { getCurrentOrganizationId } from 'utils/utils';
import { DEBOUNCE_TIME } from 'utils/constants';
// layouts/SideLayout/components
// import { Container } from '@hzero-front-ui/hzero-front-themecfg/lib';
import SideHeaderSearch from '../SideLayout/components/SideHeaderSearch';
import SideHistory from '../SideLayout/components/SideHeaderSearch/History';
// layouts/components
import DefaultCheckUserSafe from '../components/DefaultCheckUserSafe';
import DefaultListenAccessToken from '../components/DefaultListenAccessToken';
import DefaultListenWebSocket from '../components/DefaultListenWebSocket';
import DefaultListenFavicon from '../components/DefaultListenFavicon';
// ./
import NormalHeader from './components/NormalHeader';
import NormalNav from './components/NormalNav';
import NormalContent from './components/NormalContent';
import DefaultLayoutAction from '../components/DefaultLayoutAction';

import { getClassName } from './utils';

import './styles.less';

const getHeaderSearchClassName = (...paths) => getClassName('side-search', ...paths);
const getHeaderSearchHistoryClassName = (...paths) => getHeaderSearchClassName('history', ...paths);

const WrapHistory = (props) => (
  <SideHistory {...props} getClassName={getHeaderSearchHistoryClassName} />
);
const WrapHeaderSearch = (props) => (
  <SideHeaderSearch
    {...props}
    getClassName={getHeaderSearchClassName}
    components={{
      History: WrapHistory,
    }}
  />
);

class NormalLayout extends Component {
  static propTypes = {};

  static defaultProps = {};

  state = {
    collapsed: false,
  };

  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({
      type: 'global/changeLayoutCollapsed',
      payload: { collapsed: false },
    });
  }

  componentWillUnmount() {
    this.handleToggleCollapse.cancel();
  }

  @Bind()
  fetchRoleList() {
    const { dispatch } = this.props;
    dispatch({
      type: 'user/fetchRoleList',
      payload: { organizationId: getCurrentOrganizationId() },
    });
  }

  @Bind()
  fetchDataHierarchiesList() {
    const { dispatch } = this.props;
    dispatch({
      type: 'user/queryDataHierarchies',
      payload: { organizationId: getCurrentOrganizationId() },
    });
  }

  @Bind()
  logout() {
    const { dispatch } = this.props;
    dispatch({
      type: 'login/logout',
    });
  }

  @Throttle(DEBOUNCE_TIME)
  @Bind()
  handleToggleCollapse() {
    const { collapsed } = this.state;
    this.setState({
      collapsed: !collapsed,
    });
    const { dispatch } = this.props;
    dispatch({
      type: 'global/changeLayoutCollapsed',
      payload: { collapsed: !collapsed },
    });
  }

  renderLayout() {
    const {
      currentUser = {},
      roleList,
      extraHeaderRight,
      dispatch,
      hierarchicalSelectList,
      isModal,
      isSelect,
    } = this.props;
    const { logo, title } = currentUser;
    const { collapsed } = this.state;
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
            isModal={isModal}
            isSelect={isSelect}
            fetchRoleList={this.fetchRoleList}
            fetchDataHierarchiesList={this.fetchDataHierarchiesList}
            logout={this.logout}
            roleList={roleList}
            extraRight={extraHeaderRight}
            collapsed={collapsed}
          />
        </div>
        <div className={getClassName('body')}>
          <div className={getClassName('nav')}>
            <NormalNav
              collapsed={collapsed}
              components={{
                HeaderSearch: WrapHeaderSearch,
              }}
            />
          </div>
          <div className={getClassName('content')}>
            <Icon
              className={getClassName('collapsed', 'trigger')}
              type={collapsed ? 'menu-unfold' : 'menu-fold'}
              onClick={this.handleToggleCollapse}
            />
            <NormalContent />
          </div>
        </div>
        <DefaultLayoutAction />
        <DefaultCheckUserSafe />
        <DefaultListenAccessToken />
        <DefaultListenWebSocket />
        <DefaultListenFavicon />
      </div>
    );
  }

  render() {
    const { currentUser = {} } = this.props;
    return (
      <DocumentTitle title={currentUser.title || ''}>
        {/* <Container> */}
        {/* <LayoutStyle /> */}
        {this.renderLayout()}
        {/* </Container> */}
      </DocumentTitle>
    );
  }
}

export default connect(
  ({ user = {}, global = {} }) => ({
    currentUser: user.currentUser, // 当前用户
    hierarchicalSelectList: user.hierarchicalSelectList, // 数据层级下拉框列表
    isModal: user.isModal, // 是否显示模态框形式数据层级配置
    isSelect: user.isSelect, // 是否显示下拉框形式数据层级配置
    roleList: user.roleList, // 当前角色
    menu: global.menu, // 菜单
    routerData: global.routerData, // 路由配置
    activeTabKey: global.activeTabKey, // 当前路由
    tabs: global.tabs, // 所有 tab 页
    language: global.language, // 当前语言
    // count: global.count, // 当前消息计数
  }),
  null,
  null,
  { pure: false }
)(NormalLayout);
