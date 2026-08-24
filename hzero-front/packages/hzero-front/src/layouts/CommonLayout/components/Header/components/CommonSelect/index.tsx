/**
 * 常用功能 集合
 * 租户切换/角色切换/个人中心/退出登录
 */
import { connect } from 'dva';
import { RouteProps, routerRedux, withRouter } from 'dva/router';
import { Avatar, Divider, Dropdown, Icon, Menu } from 'hzero-ui';
import { isFunction } from 'lodash';
import { Bind } from 'lodash-decorators';
import React from 'react';

import intl from 'utils/intl';
import { openTab } from 'utils/menuTab';
import { getCurrentOrganizationId } from 'utils/utils';

// @ts-ignore
import defaultUserAvatar from '@/assets/logo-usercenter-default.png';

import DefaultRoleSelect from '../../../../../components/DefaultRoleSelect';
// @ts-ignore
import styles from './styles.less';

interface CommonSelectProps extends RouteProps {
  currentUser: {
    currentRoleName: string;
    realName: string;
    imageUrl: string;
    dataHierarchyFlag: boolean;
  };
  dispatch: (value: any) => Promise<any>;
  roleList: any[];
}

interface CommonSelectState {
  dropdownVisible: boolean;
  userAvatar: string;
}

class CommonSelect extends React.PureComponent<CommonSelectProps, CommonSelectState> {
  constructor(props) {
    super(props);

    this.state = {
      dropdownVisible: false,
      userAvatar: defaultUserAvatar,
    };
  }

  public componentDidMount() {
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

  public componentDidUpdate(prevProps) {
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
  public updateUserAvatar() {
    const nextUserAvatar = this.props.currentUser && this.props.currentUser.imageUrl;
    this.setState({
      userAvatar: nextUserAvatar,
    });
  }

  @Bind()
  public setDefaultUserAvatar() {
    this.setState({
      userAvatar: defaultUserAvatar,
    });
  }

  public handleMenuClick = ({ key }) => {
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

  public handleVisibleChange = (flag) => {
    this.setState({ dropdownVisible: flag });
  };

  public render() {
    const { currentUser, roleList = [] } = this.props;
    const { userAvatar } = this.state;
    const dropdownMenu = (
      <Menu className={styles.menu} selectedKeys={[]} onClick={this.handleMenuClick}>
        {roleList.length !== 0 && <DefaultRoleSelect />}
        <Divider style={{ width: '128px', margin: '4px auto' }} />
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
        <Divider style={{ width: '128px', margin: '4px auto' }} />
        <Menu.Item key="logout">
          <div className={styles['user-logout']}>
            {intl.get('hzero.common.message.loginOut').d('退出登录')}
            <i className={styles['logout-icon']} />
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
  connect(({ user = { roleList: Array, currentUser: Object } }) => ({
    currentUser: user.currentUser,
    roleList: user.roleList,
  }))(CommonSelect)
);
