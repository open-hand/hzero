// self接口报错后的提示页面
import React from 'react';
import { Button, Row } from 'hzero-ui';

import intl from 'utils/intl';
import { getEnvConfig } from 'utils/iocUtils';
import { removeAccessToken, removeAllCookie, getAccessToken, ACCESS_TOKEN } from 'utils/utils';
import { cleanMenuTabs } from 'utils/menuTab';
import selfError from '../../assets/selfError/selfError.svg';
import styles from './index.less';

const SelfError = (props) => {
  const {
    location: { state: { message } = {} },
  } = props;

  const handleReLogin = () => {
    const accessToken = getAccessToken();
    removeAccessToken();
    // 退出登录后清空cookie
    removeAllCookie();
    cleanMenuTabs(); // warn 在退出登录后需要清空 menuTabs 信息
    sessionStorage.clear();
    const { LOGOUT_URL } = getEnvConfig();
    if (LOGOUT_URL.includes('?')) {
      window.location = `${LOGOUT_URL}&${ACCESS_TOKEN}=${accessToken}`;
    } else {
      window.location = `${LOGOUT_URL}?${ACCESS_TOKEN}=${accessToken}`;
    }
  };

  return (
    <>
      <Row type="flex" justify="center">
        <img alt="" src={selfError} style={{ width: 400, marginTop: 100, color: '#003266' }} />
      </Row>
      <Row type="flex" justify="center">
        <h1 style={{ marginTop: -40 }}>
          {message}{' '}
          {intl
            .get('hzero.common.view.message.title.selfError.reLogin')
            .d('请点击下方按钮重新登录！')}
        </h1>
      </Row>
      <Row type="flex" justify="center">
        <div className={styles['button-center']}>
          <Button
            onClick={handleReLogin}
            size="large"
            style={{
              backgroundColor: '#003266',
              textAlign: 'center',
              marginTop: 20,
              width: 200,
              color: 'white',
              height: 38,
              fontSize: 15,
            }}
          >
            {intl.get('hzero.common.button.reLogin').d('重新登录')}
          </Button>
        </div>
      </Row>
    </>
  );
};

export default SelfError;
