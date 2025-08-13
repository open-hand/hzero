/**
 * DefaultLayout 管理器
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-07-11
 * @copyright 2019-07-11 © HAND
 */

import * as React from 'react';
import { connect } from 'dva';
import queryString from 'querystring';

import { getCurrentLanguage } from 'utils/utils';
import { loadLayout } from '../../customize/layout';

const LayoutLoadStatus = {
  LOADING: 0,
  SUCCESS: 1,
  ERROR: 2,
};

export class DefaultLayout extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      loadStatus: LayoutLoadStatus.LOADING,
      language: getCurrentLanguage(),
    };
  }

  componentDidMount() {
    const { menuLayout } = this.props;
    this.loadCurrentLayout(menuLayout);
  }

  loadCurrentLayout(layoutName = 'inline') {
    this.setState(
      {
        loadStatus: LayoutLoadStatus.LOADING,
      },
      () => {
        loadLayout(layoutName)()
          .then((Layout) => {
            const { language = '' } = this.state;
            // 设置中文时的表单样式处理
            if (language && language !== 'zh_CN') {
              document.body.className = 'global-layout';
            }
            this.setState({
              CurrentLayout: Layout && Layout.__esModule ? Layout.default : Layout,
              loadStatus: LayoutLoadStatus.SUCCESS,
            });
          })
          .catch(() => {
            this.loadCurrentLayout();
          });
      }
    );
  }

  componentDidUpdate(prevProps) {
    const { menuLayout } = this.props;
    const { menuLayout: prevMenuLayout, location, dispatch, menuQuickIndex } = prevProps;
    if (menuLayout !== prevMenuLayout) {
      this.loadCurrentLayout(menuLayout);
    }
    if ((location.pathname === '/' || location.pathname === '/workplace') && !menuQuickIndex) {
      const { menu } = queryString.parse(location.search.substring(1));
      dispatch({
        type: 'global/updateState',
        payload: { menuQuickIndex: menu },
      });
    }
  }

  render() {
    const { loadStatus, CurrentLayout } = this.state;
    const { menuLayout, ...otherProps } = this.props;
    switch (loadStatus) {
      case LayoutLoadStatus.SUCCESS:
      case LayoutLoadStatus.ERROR:
        return React.createElement(CurrentLayout, otherProps);
      case LayoutLoadStatus.LOADING:
      default:
        return null;
    }
  }
}

export default connect(({ user = {}, global = {} }) => {
  const { currentUser = {} } = user;
  const { menuLayout = 'inline' } = currentUser;
  const { menuQuickIndex } = global;
  return {
    menuLayout,
    menuQuickIndex,
  };
})(DefaultLayout);
