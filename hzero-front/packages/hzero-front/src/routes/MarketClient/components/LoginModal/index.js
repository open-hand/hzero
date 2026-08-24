import React, { useState } from 'react';
import { Form, Icon, Modal, Input, notification, Row, Col } from 'hzero-ui';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { CLIENT_JUMP_URL } from 'utils/market-client';
import { getResponse, getAccessToken } from 'utils/utils';
import { API_HOST, HZERO_ADM } from 'utils/config';
import { getCaptchaKey } from '../../ServiceList/services';
import styles from './index.less';

const MARKET_USER_INFO_KEY = '__market_user_info_';

function LoginModal({
  loginModalVisible,
  onCancel,
  marketUserLogin,
  form,
  onOk,
  compelCloseModal = false,
}) {
  const { getFieldDecorator, validateFields } = form;
  const [loading, setLoading] = useState(false);
  const [captchaKey, setCaptchaKey] = useState(''); // 验证码 cache
  const [errorMessage, setErrorMessage] = useState(''); // 错误信息

  const _onOk = () => {
    validateFields((err, v) => {
      if (err) return;
      setLoading(true);
      const values = { ...v, password: btoa(v.password) };
      if (Object.prototype.hasOwnProperty.call(v, 'captcha')) {
        values.captchaKey = captchaKey;
      }
      const saveData = Object.keys(values).reduce((pre, next) => {
        return [...pre, `${next}=${values[next]}`];
      }, []);

      // 密码需要 base64 一下，模拟表单提交
      marketUserLogin(saveData.join('&'), (res) => {
        setLoading(false);
        if (getResponse(res)) {
          sessionStorage.setItem(MARKET_USER_INFO_KEY, JSON.stringify(res));
          if (onCancel && !compelCloseModal) onCancel();
          setTimeout(() => {
            if (typeof onOk === 'function') {
              onOk();
            }
          }, 300);
          notification.success({
            message: intl.get('hadm.marketclient.view.login.success').d('登录成功'),
          });
        } else if (res.message.includes('验证码')) {
          // 如果报错提示信息中包含需要验证码, 则展示验证码
          if (!captchaKey) {
            getLoginCaptcha();
          }
          setErrorMessage(res.message);
        } else {
          setErrorMessage(res.message);
        }
      });
    });
  };

  // 回车键登录
  const quickSearch = (e) => {
    if (e && e.keyCode === 13) {
      _onOk();
    }
  };

  const getLoginCaptcha = () => {
    getCaptchaKey().then((res) => {
      if (res && !res.failed) {
        setCaptchaKey(res.captchaKey);
      }
    });
  };

  // 更换账号
  const resetForm = () => {
    setCaptchaKey('');
  };

  return (
    <Modal
      okText={intl.get('hadm.marketclient.view.login').d('登录')}
      title={intl.get('hadm.marketclient.view.login.title').d('请使用开放平台账号登录')}
      visible={loginModalVisible}
      onOk={_onOk}
      maskClosable={false}
      onCancel={onCancel}
      okButtonProps={{ loading }}
      style={{ marginTop: '10%' }}
    >
      <div className={styles['header-desc']}>
        <p>
          {intl
            .get('hadm.marketclient.view.login.description')
            .d('使用开放平台账号登录后，我们将帮您获取最新版本服务信息并进行版本对比。')}
        </p>
      </div>
      <Form
        style={{ width: '80%', margin: '0 auto' }}
        layout="horizontal"
        onKeyDown={(e) => quickSearch(e)}
      >
        <Form.Item>
          {getFieldDecorator('userName', {
            rules: [
              {
                required: true,
                message: intl
                  .get('hadm.marketclient.view.login.placeholder.account')
                  .d('请输入邮箱/手机号账号'),
              },
            ],
          })(
            <Input
              onChange={resetForm}
              size="large"
              prefix={<Icon type="user" style={{ color: 'rgba(0,0,0,.25)' }} />}
              placeholder={intl
                .get('hadm.marketclient.view.login.placeholder.account')
                .d('请输入邮箱/手机号账号')}
            />
          )}
        </Form.Item>
        <Form.Item>
          {getFieldDecorator('password', {
            rules: [
              {
                required: true,
                message: intl
                  .get('hadm.marketclient.view.login.placeholder.password')
                  .d('请输入登录密码'),
              },
            ],
          })(
            <Input
              size="large"
              prefix={<Icon type="lock" style={{ color: 'rgba(0,0,0,.25)' }} />}
              type="password"
              placeholder={intl
                .get('hadm.marketclient.view.login.placeholder.password')
                .d('请输入登录密码')}
            />
          )}
        </Form.Item>
        {captchaKey && (
          <Form.Item>
            <Row gutter={8}>
              <Col span={14}>
                {getFieldDecorator('captcha')(<Input size="large" autoComplete="off" />)}
              </Col>
              <Col span={10} onClick={() => getLoginCaptcha()}>
                <img
                  src={`${API_HOST}${HZERO_ADM}/v1/market/captcha/${captchaKey}?access_token=${getAccessToken()}`}
                  alt="captchaImg"
                  className={styles['captcha-img']}
                />
              </Col>
            </Row>
          </Form.Item>
        )}
        <div className={styles.error}>{errorMessage}</div>
        <Form.Item>
          <div className={styles['footer-operation']}>
            <div>
              <a href={`${CLIENT_JUMP_URL}/user/forget`} target="_blank" rel="noopener noreferrer">
                {intl.get('hadm.marketclient.view.login.forgetPassword').d('忘记密码')}
              </a>
            </div>
            <div>
              <a
                href={`${CLIENT_JUMP_URL}/user/register`}
                target="_blank"
                rel="noopener noreferrer"
              >
                {intl.get('hadm.marketclient.view.login.register').d('我要注册')}
              </a>
            </div>
          </div>
        </Form.Item>
      </Form>
    </Modal>
  );
}

export default formatterCollections({
  code: ['hadm.marketclient'],
})(Form.create({ fieldNameProp: null })(LoginModal));
