import React from 'react';
import { withRouter, Route } from 'dva/router';
import { connect } from 'dva';
import Exception from 'components/Exception';
import {
  extractAccessTokenFromHash,
  extractRefreshTokenFromHash,
  extractErrorMessageFromSearch,
  setAccessToken,
  setRefreshToken,
  getCurrentOrganizationId,
  getCurrentRole,
  setSession,
} from 'utils/utils';

@withRouter
@connect()
export default class AuthorizedRoute extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      isAuthorized: false,
      isException: false,
    };
  }

  componentDidMount() {
    const { dispatch, history, location } = this.props;
    const token = extractAccessTokenFromHash(window.location.hash);
    const refreshToken = extractRefreshTokenFromHash(window.location.hash);
    const errorMessage = extractErrorMessageFromSearch(window.location.search);
    if (errorMessage) {
      history.push({
        pathname: '/public/error-message',
        state: {
          message: errorMessage,
        },
      });
      return;
    } else if (token) {
      setAccessToken(token, 60 * 60);
      setRefreshToken(refreshToken, 60 * 60);
      // 保留上次退出时的页面路径和search
      history.push({
        pathname: location.pathname,
        search: location.search,
      });
    }

    dispatch({
      type: 'user/fetchCurrent',
    }).then((res) => {
      if (res) {
        if (!(res instanceof Error)) {
          // 请求 self 接口成功
          if (!res.failed) {
            // 请求 self 接口成功
            // 设置当前语言到session
            setSession('language', res.language);
            dispatch({
              type: 'global/init',
              payload: {
                language: res.language, // 加载菜单国际化
                organizationId: getCurrentOrganizationId(),
                roleId: getCurrentRole().id,
              },
            }).then(() => {
              this.setState({
                isAuthorized: true,
              });
            });
          } else {
            // 清除首屏loading
            const loader = document.querySelector('#loader-wrapper');
            this.setState({
              isException: true,
            });
            if (loader) {
              loader.parentNode.removeChild(loader);
            }
            history.push({
              pathname: '/public/self-error',
              state: {
                message: res.message,
              },
            });
          }
        } else {
          // 其他错误
          this.setState({
            isException: true,
          });
          // 清除首屏loading
          const loader = document.querySelector('#loader-wrapper');
          if (loader) {
            document.body.removeChild(loader);
          }
          history.push('/exception/500');
        }
      }
      // else {
      //   // self 接口 请求 401
      // }
    });

    // request(AUTH_SELF_URL).then(user => {
    //   if (user && !isNil(user.id)) {
    //     this.setState({
    //       isAuthorized: true,
    //     });
    //   }
    // })
    // .catch(() => {
    //   removeAccessToken();
    //   window.location.href = AUTH_URL;
    // });
  }

  render() {
    const { component: Component, render, ...rest } = this.props;
    const { isAuthorized, isException } = this.state;
    // eslint-disable-next-line no-nested-ternary
    return isAuthorized === true ? (
      <Route {...rest} render={(props) => (Component ? <Component {...props} /> : render(props))} />
    ) : isException === true ? (
      <Exception type="500" />
    ) : null;
  }
}
