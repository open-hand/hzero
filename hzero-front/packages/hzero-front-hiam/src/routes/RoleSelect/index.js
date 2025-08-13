import React, { PureComponent } from 'react';
import { Icon, Menu, Spin, Dropdown, Avatar } from 'hzero-ui';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import { isEmpty, find } from 'lodash';
import { Bind } from 'lodash-decorators';

import NProgress from 'components/NProgress';

import { getCurrentRole } from 'utils/utils';

import { cleanMenuTabs } from 'utils/menuTab';
import defaultUserAvatar from 'hzero-front/lib/assets/logo-usercenter-default.png';
import styles from './index.less';

@connect(({ user = {} }) => ({
  user,
}))
export default class RoleSelect extends PureComponent {
  state = {
    userAvatar: defaultUserAvatar,
  };

  onClick({ key }) {
    const { user = {}, dispatch } = this.props;
    const { roleList = [] } = user;
    const newDefaultRole = find(roleList, o => o.id === Number(key)) || {};
    NProgress.start();
    const { id, name } = newDefaultRole;
    dispatch({
      type: 'user/updateCurrentRole',
      payload: { roleId: id },
    }).then(res => {
      NProgress.done();
      if (!(res && res.failed)) {
        dispatch({
          type: 'user/updateCurrentUser',
          payload: {
            currentRoleId: id,
            currentRoleName: name,
          },
        });
        // 切换到 工作台
        // warn 清空 tabs 信息
        cleanMenuTabs();
        dispatch(routerRedux.push({ pathname: '/workplace' }));
        window.location.reload();
      }
    });
  }

  componentDidMount() {
    // 由于是再个人中心数据加载完成后再加载的数据, 所以需要先设置 头像
    const {
      user: { currentUser: { imageUrl: userAvatar } = {} },
    } = this.props;
    if (userAvatar) {
      const img = new Image();
      img.onload = this.updateUserAvatar;
      img.onerror = this.setDefaultUserAvatar;
      img.src = userAvatar;
    }
  }

  componentDidUpdate(prevProps) {
    const {
      user: { currentUser: { imageUrl: prevUserAvatar } = {} },
    } = prevProps;
    const {
      user: { currentUser: { imageUrl: nextUserAvatar } = {} },
    } = this.props;
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
    const {
      user: { currentUser: { imageUrl: nextUserAvatar } = {} },
    } = this.props;
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

  render() {
    const { user = {} } = this.props;
    const { userAvatar } = this.state;
    const { roleList = [] } = user;
    const menu = (
      <Menu selectedKeys={[`${(getCurrentRole() || {}).id}`]} onClick={this.onClick.bind(this)}>
        {isEmpty(roleList) ? (
          <Spin />
        ) : (
          roleList.map(n => (
            <Menu.Item key={n.id}>
              {/* <Icon type="user" style={{ marginRight: 8 }} /> */}
              {n.name}
            </Menu.Item>
          ))
        )}
      </Menu>
    );
    return (
      <Dropdown overlay={menu} placement="bottomLeft" trigger={['click']}>
        <span className={styles['select-role']}>
          <Avatar size="small" className={styles.avatar} src={userAvatar} />
          {getCurrentRole().name || ''}
          <Icon type="down" />
        </span>
      </Dropdown>
    );
  }
}
