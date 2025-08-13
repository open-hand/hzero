/**
 * DefaultLayoutAction
 *
 * 通用的 一般情况下的 Layout 的 初始化流程
 *
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019/8/27
 * @copyright 2019 © HAND
 */

import React, { Component } from 'react';
import { connect } from 'dva';
import dynamic from 'dva/dynamic';

import LoadingBar from 'components/NProgress/LoadingBar';

import { tabListen } from 'utils/menuTab';

class DefaultLayoutAction extends Component {
  componentDidMount() {
    // 清除首屏loading
    const loader = document.querySelector('#loader-wrapper');
    if (loader) {
      try {
        loader.parentNode.removeChild(loader);
      } catch (e) {
        loader.remove();
      }
      // 设置默认页面加载动画
      dynamic.setDefaultLoadingComponent(() => <LoadingBar />);
    }
    this.init();
  }

  init() {
    const { dispatch, currentUser = {} } = this.props;
    const { language } = currentUser;
    dispatch({
      type: 'global/baseLazyInit',
      payload: {
        language,
      },
    }).then(() => {
      // 初始化菜单成功后 调用 tabListen 来触发手动输入的网址
      tabListen();
      // FIXME:可以删了吧
      dispatch({
        type: 'global/initActiveTabMenuId',
      });
    });
  }

  render() {
    return null;
  }
}

export default connect(
  ({ user = {} }) => ({
    currentUser: user.currentUser, // 当前用户
  }),
  null,
  null,
  { pure: false }
)(DefaultLayoutAction);
