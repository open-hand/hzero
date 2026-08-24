/**
 * 角色切换
 */
import React, { PureComponent } from 'react';
import { Menu, Spin } from 'hzero-ui';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import { find, isEmpty } from 'lodash';
import { Bind } from 'lodash-decorators';

import NProgress from 'components/NProgress';

import { getCurrentRole } from 'utils/utils';
import { cleanMenuTabs } from 'utils/menuTab';

import styles from './index.less';

class DefaultRoleSelect extends PureComponent {
  @Bind()
  onClick({ key }) {
    const { user = {}, updateCurrentRole, updateCurrentUser, gotoTab } = this.props;
    const { roleList = [] } = user;
    const newDefaultRole = find(roleList, (o) => String(o.id) === key) || {};
    NProgress.start();
    const { id, name } = newDefaultRole;
    updateCurrentRole({ roleId: id }).then((res) => {
      NProgress.done();
      if (!(res && res.failed)) {
        updateCurrentUser({
          currentRoleId: id,
          currentRoleName: name,
        });
        // 切换到 工作台
        // warn 清空 tabs 信息
        cleanMenuTabs();
        gotoTab({ pathname: '/workplace' });
        window.location.reload();
      }
    });
  }

  render() {
    const { user = {} } = this.props;
    const { roleList = [] } = user;
    return (
      <Menu
        className="default-role-select-dropdown"
        selectedKeys={[`${(getCurrentRole() || {}).id}`]}
        onClick={this.onClick}
      >
        {isEmpty(roleList) ? (
          <Menu.Item key="loading-spin">
            <Spin />
          </Menu.Item>
        ) : (
          <Menu.SubMenu
            className={styles['select-role-wrap']}
            title={
              <div className={styles['select-role']}>
                <span>{getCurrentRole().name || ''}</span>
              </div>
            }
          >
            {roleList.map((n) => (
              <Menu.Item key={n.id} className={styles['select-role-item']}>
                <div className={styles['select-role-item-divider']}>{n.name}</div>
              </Menu.Item>
            ))}
          </Menu.SubMenu>
        )}
      </Menu>
    );
  }
}

export default connect(
  ({ user = {} }) => ({
    user,
  }),
  (dispatch) => ({
    // 更新当前角色(调接口)
    updateCurrentRole: (payload) =>
      dispatch({
        type: 'user/updateCurrentRole',
        payload,
      }),
    // 更新用户信息
    updateCurrentUser: (payload) =>
      dispatch({
        type: 'user/updateCurrentUser',
        payload,
      }),
    // 跳转到页面
    gotoTab: (location, state) => dispatch(routerRedux.push(location, state)),
  })
)(DefaultRoleSelect);
