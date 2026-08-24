/**
 * 常用功能 集合
 * 租户切换/角色切换/个人中心/退出登录
 */
import React from 'react';
import { withRouter, routerRedux } from 'dva/router';
import { Icon, Avatar, Dropdown, Menu, Divider } from 'hzero-ui';
import { isFunction } from 'lodash';
import { Bind } from 'lodash-decorators';
import { connect } from 'dva';

import intl from 'utils/intl';
import { openTab } from 'utils/menuTab';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import { getEnvConfig } from 'utils/iocUtils';

import DefaultRoleSelect from '../DefaultRoleSelect';
import DefaultTenantSelect from '../DefaultTenantSelect';
import DefaultDataHierarchies from '../DefaultDataHierarchies';
import DefaultDataHierarchiesSelect from '../DefaultDataHierarchiesSelect';

import styles from './styles.less';

import defaultUserAvatar from '../../../assets/logo-usercenter-default.png';

class DefaultCommonSelect extends React.PureComponent {
  state = {
    dropdownVisible: false,
    userAvatar: defaultUserAvatar,
  };

  componentDidMount() {
    // 由于是再个人中心数据加载完成后再加载的数据, 所以需要先设置 头像
    const {
      currentUser: { imageUrl: userAvatar },
      dispatch,
    } = this.props;
    if (userAvatar) {
      const img = new Image();
      img.onload = this.updateUserAvatar;
      img.onerror = this.setDefaultUserAvatar;
      img.src = userAvatar;
    }
    dispatch({
      type: 'user/fetchRoleList',
      payload: { organizationId: getCurrentOrganizationId() },
    });
  }

  componentDidUpdate(prevProps) {
    const prevUserAvatar = prevProps.currentUser && prevProps.currentUser.imageUrl;
    const nextUserAvatar = this.props.currentUser && this.props.currentUser.imageUrl;
    if (prevUserAvatar !== nextUserAvatar) {
      // 只有当 用户头像存在 才会设置 用户头像
      if (nextUserAvatar) {
        const img = new Image();
        img.onload = this.updateUserAvatar;
        img.onerror = this.setDefaultUserAvatar;
        img.src = nextUserAvatar;
      }
    }
  }

  @Bind()
  updateUserAvatar() {
    const nextUserAvatar = this.props.currentUser && this.props.currentUser.imageUrl;
    this.setState({
      userAvatar: nextUserAvatar,
    });
  }

  @Bind()
  setDefaultUserAvatar() {
    this.setState({
      userAvatar: defaultUserAvatar,
    });
  }

  handleMenuClick = ({ key }) => {
    if (key === 'triggerError') {
      this.props.dispatch(routerRedux.push('/exception/trigger'));
      return;
    }
    if (key === 'logout') {
      this.props.dispatch({
        type: 'login/logout',
      });
    }

    if (key !== 'role') {
      this.setState({ dropdownVisible: false });
    }
  };

  handleVisibleChange = (flag) => {
    this.setState({ dropdownVisible: flag });
  };

  render() {
    const { currentUser, roleList = [], hierarchicalSelectList, isModal, isSelect } = this.props;
    const { userAvatar } = this.state;
    const { VERSION_IS_OP } = getEnvConfig();
    const dropdownMenu = (
      <Menu className={styles.menu} selectedKeys={[]} onClick={this.handleMenuClick}>
        {roleList.length !== 0 && <DefaultRoleSelect />}
        <Divider style={{ width: '128px', margin: '4px auto' }} />
        {!VERSION_IS_OP && (
          <Menu.Item key="tenant">
            <DefaultTenantSelect />
          </Menu.Item>
        )}
        {!VERSION_IS_OP && <Divider style={{ width: '128px', margin: '4px auto' }} />}
        <Menu.Item>
          <a
            style={{ color: '#666' }}
            href="#/hiam/user/info"
            onClick={(e) => {
              if (isFunction(e && e.preventDefault)) {
                e.preventDefault();
              }
              openTab({
                title: 'hzero.common.title.userInfo',
                key: '/hiam/user',
                path: '/hiam/user/info',
                icon: 'user',
                closable: true,
              });
            }}
          >
            {intl.get('hzero.common.basicLayout.userInfo').d('个人中心')}
          </a>
        </Menu.Item>
        {currentUser.dataHierarchyFlag && isTenantRoleLevel() && (
          <Divider style={{ width: '128px', margin: '4px auto' }} />
        )}
        {currentUser.dataHierarchyFlag && isModal && isTenantRoleLevel() && (
          <Menu.Item key="data">
            <DefaultDataHierarchies />
          </Menu.Item>
        )}
        {currentUser.dataHierarchyFlag &&
          isSelect &&
          isTenantRoleLevel() &&
          hierarchicalSelectList.map((item) => {
            return <DefaultDataHierarchiesSelect dataHierarchyCode={item.dataHierarchyCode} />;
          })}
        <Divider style={{ width: '128px', margin: '4px auto' }} />
        <Menu.Item key="logout">
          <div className={styles['user-logout']}>
            {intl.get('hzero.common.message.loginOut').d('退出登录')}
            <Icon className={styles['logout-icon']} />
          </div>
        </Menu.Item>
      </Menu>
    );
    return (
      <>
        <Dropdown
          overlay={dropdownMenu}
          trigger={['click']}
          onVisibleChange={this.handleVisibleChange}
          visible={this.state.dropdownVisible}
        >
          <span
            className={
              this.state.dropdownVisible
                ? `${styles['dropdown-open']} ${styles.account}`
                : `${styles.action} ${styles.account}`
            }
          >
            <Avatar size="small" className={styles.avatar} src={userAvatar} />
            <span className={styles.name}>
              {currentUser.realName || currentUser.currentRoleName || ''} <Icon type="down" />
            </span>
          </span>
        </Dropdown>
      </>
    );
  }
}

export default withRouter(
  connect(({ user = {} }) => ({
    currentUser: user.currentUser,
    roleList: user.roleList,
    hierarchicalSelectList: user.hierarchicalSelectList, // 数据层级下拉框列表
    isModal: user.isModal, // 是否显示模态框形式数据层级配置
    isSelect: user.isSelect, // 是否显示下拉框形式数据层级配置
  }))(DefaultCommonSelect)
);
