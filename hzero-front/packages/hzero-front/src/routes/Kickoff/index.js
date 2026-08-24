import React from 'react';
import { Button, Row } from 'hzero-ui';
// import queryString from 'query-string';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import { getEnvConfig } from 'utils/iocUtils';
import { removeAccessToken, removeAllCookie, getSession, setSession } from 'utils/utils';
import Icons from 'components/Icons';

export default class Kickoff extends React.Component {
  @Bind()
  handleReLogin() {
    const { LOGIN_URL } = getEnvConfig();
    const cacheLocation = getSession('redirectUrl');
    setSession('isErrorFlag', false);
    removeAccessToken();
    removeAllCookie();
    // 由于 LOGIN_URL 可以 配置, 所以 做一次判断
    if (LOGIN_URL.includes('?')) {
      window.location.href = `${LOGIN_URL}&redirect_uri=${cacheLocation}`; // 401 需要在登录后返回401的页面
    } else {
      window.location.href = `${LOGIN_URL}?redirect_uri=${cacheLocation}`; // 401 需要在登录后返回401的页面
    }
  }

  render() {
    return (
      <>
        <Row type="flex" justify="center" style={{ marginTop: 100 }}>
          <Icons
            type="world-map"
            size="250"
            color="#003266"
            style={{ width: 400, margin: 'auto', marginTop: -40 }}
          />
        </Row>
        <Row type="flex" justify="center">
          <h1 style={{ marginTop: -40 }}>
            {intl
              .get('hzero.common.view.message.title.unauthorized.reLogin')
              .d('抱歉，您的账号已在其他地点登录，请点击下方按钮重新登录！')}
          </h1>
        </Row>
        <Row type="flex" justify="center">
          <Button
            onClick={this.handleReLogin}
            size="large"
            style={{ backgroundColor: '#003266', width: 200, color: 'white', marginTop: 10 }}
          >
            {intl.get('hzero.common.button.reLogin').d('重新登录')}
          </Button>
        </Row>
      </>
    );
  }
}
