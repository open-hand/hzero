// self接口报错后的提示页面
import React, { useEffect } from 'react';
import { Row } from 'hzero-ui';

import intl from 'utils/intl';
import { removeAccessToken, removeAllCookie } from 'utils/utils';
import selfError from '../../assets/selfError/selfError.svg';

const ErrorMessage = (props) => {
  const {
    location: { state: { message } = {} },
  } = props;

  useEffect(() => {
    removeAccessToken();
    removeAllCookie();
  }, []);

  return (
    <>
      <Row type="flex" justify="center">
        <img alt="" src={selfError} style={{ width: 400, marginTop: 100, color: '#003266' }} />
      </Row>
      <Row type="flex" justify="center">
        <h1 style={{ marginTop: -40 }}>
          {message}
          {message && '，'}
          {intl
            .get('hzero.common.view.message.title.error.message.contact.admin')
            .d('请联系管理员')}
        </h1>
      </Row>
    </>
  );
};

export default ErrorMessage;
