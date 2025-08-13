/**
 * SafeInfo.js
 * @date 2018/11/23
 * @author WY yang.wang06@hand-china.com
 * @copyright Copyright (c) 2018, Hand
 */

import React from 'react';
import { Button, Form, Icon, Input, Modal } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { isFunction, map } from 'lodash';
import { routerRedux } from 'dva/router';

import CountDown from 'components/CountDown';

import intl from 'utils/intl';
import { AUTH_HOST } from 'utils/config';
import {
  getAccessToken,
  getUserOrganizationId,
  getSession,
  setSession,
  encryptPwd,
  getCurrentUser,
} from 'utils/utils';
import { EMAIL, PHONE } from 'utils/regExp';
import notification from 'utils/notification';
import { validatePasswordRule } from '@/utils/validator';

import styles from './index.less';

import Main from './components/Main';
// import LineItem from './components/LineItem';
import Content from './components/Content';
import ModalForm from './components/ModalForm';
import EditFormModal from './components/EditFormModal';
import MaxLenItem from './components/MaxLenItem';
import OpenAppTable from './components/OpenAppsTable';

const { Item: FormItem } = Form;

const formItemLayout = {
  labelCol: { span: 5 },
  wrapperCol: { span: 19 },
};
// const securityLevelStyle = {
//   margin: '10px 0',
// };

export default class SafeInfo extends React.Component {
  renderModalFormItems;

  // 渲染 ModalForm 中的 FormItems
  handleModalFormOk;

  // Modal onOk
  state = {
    modalProps: { visible: false }, // 模态框属性
  };

  constructor(props) {
    super(props);
    this.handleModalFormOk = this.handleModalFormCancelDefault; // 默认是取消
  }

  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({
      type: 'userInfo/fetchOpenAccountList',
    });
    // dispatch({
    //   type: 'personalLoginRecord/fetchRecords',
    //   payload: {
    //     tenantId: getCurrentOrganizationId(),
    //     page: 0,
    //     size: 3,
    //   },
    // });
    // 获取公钥
    dispatch({
      type: 'userInfo/getPublicKey',
    });
  }

  render() {
    const { modalProps } = this.state;
    const {
      editModalLoading = false,
      modalProps: editFormModalProps,
      openAccountList = [],
    } = this.props;
    const { additionInfo = {} } = getCurrentUser();
    const { paasFlag } = additionInfo;
    // const {step = 'form-key'} = editFormModalProps;
    // 只有在发送验证码后才能点下一步
    const editFormModalOkBtnProps = {};
    switch (editFormModalProps.step) {
      case 'validateOldEmail':
        editFormModalOkBtnProps.disabled = !getSession('user-info-oldEmail');
        break;
      case 'validateOldPhone':
        editFormModalOkBtnProps.disabled = !getSession('user-info-oldPhone');
        break;
      case 'validatePhone':
        editFormModalOkBtnProps.disabled = !getSession('user-info-verifyPhone');
        break;
      case 'validateNewEmail':
      case 'validateNewPhone':
      case 'validateUnCheckedEmail':
      case 'validateUnCheckedPhone':
        editFormModalOkBtnProps.disabled = !editFormModalProps.captchaKey;
        break;
      default:
        break;
    }
    modalProps.confirmLoading = editModalLoading; // editModalLoading 包含密码的loading

    return (
      <div className={styles.safe}>
        <Main title={intl.get('hiam.userInfo.view.title.main.safeSetting').d('安全设置')}>
          <Content>
            {this.renderPassword()}
            {this.renderPhone()}
            {this.renderEmail()}
          </Content>
        </Main>
        {openAccountList.length > 0 && (
          <Main
            title={
              // 如果为true则显示PaaS的title，否则显示hzero平台原有title
              !paasFlag ? (
                intl.get('hiam.userInfo.view.title.subMain.openApp').d('第三方账号绑定')
              ) : (
                <>
                  <div>
                    {intl.get('hiam.userInfo.view.title.subMain.openApp').d('第三方账号绑定')}
                  </div>
                  <div>
                    <span className={styles['open-app-desc']}>
                      {intl
                        .get('hiam.userInfo.view.title.subMain.openAppDesc')
                        .d(
                          '以下是系统管理平台的域名信息，可选择任一域名进行扫码绑定，绑定后访问相应域名时可直接扫码登录'
                        )}
                    </span>
                  </div>
                </>
              )
            }
            className={styles['open-app']}
          >
            {/* 如果为true则显示PaaS的三方绑定表格，否则显示hzero平台原有绑定页面 */}
            {paasFlag ? (
              <OpenAppTable data={openAccountList} onUnBindApp={this.handleUnBindApp} />
            ) : (
              <Content>{this.renderOpenApp()}</Content>
            )}
          </Main>
        )}
        {/* 为true则证明开启PaaS，不显示登录日志 */}
        {!paasFlag && (
          <Main
            title={
              <div className={styles['login-log']}>
                <div>{intl.get('hiam.userInfo.view.login.log').d('登录日志')}</div>
                <Button
                  key="bind"
                  type="primary"
                  style={{ marginRight: 5, marginBottom: 6 }}
                  onClick={this.handleCheckLoginLog}
                >
                  {intl.get('hzero.common.button.view').d('查看')}
                </Button>
              </div>
            }
            className={styles['open-app']}
          />
        )}
        {/* <Table
          style={{ marginBottom: 2 }}
          bordered
          rowKey="order"
          columns={columns}
          dataSource={loginData.map((item, index) => ({ ...item, order: index + 1 }))}
          pagination={false}
        /> */}
        <ModalForm
          modalProps={modalProps}
          renderFormItems={this.renderModalFormItems}
          onOk={this.handleModalFormOk}
        />
        <EditFormModal
          width={500}
          confirmLoading={editModalLoading}
          okButtonProps={editFormModalOkBtnProps}
          {...editFormModalProps}
        />
      </div>
    );
  }

  // // security-level
  // renderSecurityLevel() {
  //   const { userInfo: { securityLevelMeaning } } = this.props;
  //   return (
  //     <LineItem
  //       key="securityLevel"
  //       style={securityLevelStyle}
  //       itemLabel={null}
  //       content={
  //         <React.Fragment>
  //           <span className={styles['security-level']}>您当前账号的安全程度</span>
  //           <span>{securityLevelMeaning}</span>
  //         </React.Fragment>
  //       }
  //     />
  //   );
  // }

  @Bind()
  handleModalFormCancelDefault() {
    setSession(`user-info-verifyPhone`, 0);
    this.renderModalFormItems = undefined;
    this.handleModalFormOk = this.handleModalFormOkDefault;
    this.setState({
      modalProps: { visible: false },
    });
  }

  // password

  renderPassword() {
    const { userInfo = {} } = this.props;
    const btns = [
      <Button key="update" onClick={this.handlePasswordEdit}>
        {intl.get('hzero.common.button.update').d('修改')}
      </Button>,
    ];
    const comment = intl
      .get('hiam.userInfo.view.message.password')
      .d(
        '安全性高的密码可以使账号更安全。建议您定期更换密码，设置一个包含字母，符号或数字中至少两项长度超过6位的密码。'
      );
    let content = '';
    if (userInfo.passwordResetFlag === 1) {
      content = (
        <span key="desc" className={styles['color-bind']}>
          <Icon type="check-circle" />
          &nbsp;
          {intl.get('hiam.userInfo.view.status.setted').d('已设置')}
        </span>
      );
    } else {
      content = (
        <span key="desc" className={styles['color-unbind']}>
          <Icon type="info-circle" />
          &nbsp;
          {intl.get('hiam.userInfo.view.status.noSetPassword').d('管理员设置的初始密码,请修改')}
        </span>
      );
    }

    return (
      <MaxLenItem
        className={styles['max-len-item-content-wrapper-safe']}
        itemIcon={null}
        description={intl.get('hiam.userInfo.model.user.loginPassword').d('登录密码')}
        content={content}
        comment={comment}
        btns={btns}
      />
    );
  }

  @Bind()
  handlePasswordEdit() {
    const {
      dispatch,
      userInfo: { phoneCheckFlag },
      modalProps,
    } = this.props;
    this.renderModalFormItems = this.renderPasswordFormItems;
    this.handleModalFormOk = this.handlePasswordUpdate;
    dispatch({
      type: 'userInfo/getPasswordRule',
      payload: { organizationId: getUserOrganizationId() },
    }).then((res) => {
      if (res && res.forceCodeVerify) {
        if (res && phoneCheckFlag) {
          this.setState({
            modalProps: {
              title: intl.get('hiam.userInfo.view.message.title.form.password').d('更改密码'),
              visible: true,
              onCancel: this.handleModalFormCancelDefault,
            },
          });
        } else {
          notification.warning({
            message: intl
              .get('hiam.userInfo.view.confirmBindPhone')
              .d('当前用户未绑定手机号，请先绑定手机号。'),
          });
        }
      } else {
        this.setState({
          modalProps: {
            title: intl.get('hiam.userInfo.view.message.title.form.password').d('更改密码'),
            visible: true,
            onCancel: this.handleModalFormCancelDefault,
          },
        });
      }
    });
    dispatch({
      type: 'userInfo/updateState',
      payload: {
        modalProps: {
          ...modalProps.validCache?.validatePhone,
          ...modalProps,
        },
      },
    });
  }

  @Bind()
  handlePasswordUpdate(fieldsValue) {
    const { publicKey, dispatch, passwordTipMsg = {} } = this.props;
    const { onPasswordUpdate } = this.props;
    const captchaKey = getSession('user-info-verifyPhone');
    if (passwordTipMsg.loginAgain) {
      Modal.confirm({
        title: `${intl
          .get('hiam.userInfo.view.confirmLoginAgain')
          .d('修改密码后需要重新登录，是否确认？')}`,
        onOk() {
          onPasswordUpdate({
            password: encryptPwd(fieldsValue.password, publicKey),
            originalPassword: encryptPwd(fieldsValue.originalPassword, publicKey),
            phone: passwordTipMsg.forceCodeVerify ? fieldsValue.phone : undefined,
            captcha: passwordTipMsg.forceCodeVerify ? fieldsValue.captcha : undefined,
            captchaKey: passwordTipMsg.forceCodeVerify ? captchaKey : undefined,
            businessScope: passwordTipMsg.forceCodeVerify ? 'UPDATE_PASSWORD' : undefined,
          }).then((res) => {
            if (res && res.failed) {
              notification.warning({
                message: res.message,
              });
              if (res.message === '您的密码错误，还可以尝试0次') {
                dispatch({
                  type: 'login/logout',
                });
              }
            } else {
              dispatch({
                type: 'login/logout',
              });
            }
          });
        },
      });
    } else {
      onPasswordUpdate({
        password: encryptPwd(fieldsValue.password, publicKey),
        originalPassword: encryptPwd(fieldsValue.originalPassword, publicKey),
        phone: passwordTipMsg.forceCodeVerify ? fieldsValue.phone : undefined,
        captcha: passwordTipMsg.forceCodeVerify ? fieldsValue.captcha : undefined,
        captchaKey: passwordTipMsg.forceCodeVerify ? captchaKey : undefined,
        businessScope: passwordTipMsg.forceCodeVerify ? 'UPDATE_PASSWORD' : undefined,
      }).then((res) => {
        if (res && res.failed) {
          notification.warning({
            message: res.message,
          });
          if (res.message === '您的密码错误，还可以尝试0次') {
            dispatch({
              type: 'login/logout',
            });
          }
        } else {
          this.handleModalFormCancelDefault();
        }
      });
    }
  }

  @Bind()
  renderPasswordFormItems(form) {
    const {
      userInfo: { loginName, phone },
      passwordTipMsg = {},
      modalProps,
      modalProps: { validCodeSendLimitFlag, validCodeLimitTimeEnd },
      postCaptchaLoading = false,
    } = this.props;
    const { getFieldDecorator } = form;
    const { forceCodeVerify } = passwordTipMsg;
    const { validateNewPasswordNotSame, validatePasswordAnther } = this; // 验证改变了 this
    return [
      <FormItem
        required
        key="originalPassword"
        label={intl.get('hiam.userInfo.model.user.originalPassword').d('原密码')}
        {...formItemLayout}
      >
        {getFieldDecorator('originalPassword', {
          validateTrigger: 'onBlur',
          rules: [
            {
              required: true,
              message: intl.get('hzero.common.validation.notNull', {
                name: intl.get('hiam.userInfo.model.user.originalPassword').d('原密码'),
              }),
            },
            {
              max: 110,
              message: intl.get('hzero.common.validation.max', {
                max: 110,
              }),
            },
          ],
          // 密码字段自动填充 https://developer.mozilla.org/zh-CN/docs/Web/Security/Securing_your_site/Turning_off_form_autocompletion
        })(
          <Input
            type="password"
            autoComplete="new-password"
            onChange={() => {
              setTimeout(() => {
                form.validateFields(['password'], { force: true });
              });
            }}
          />
        )}
      </FormItem>,
      <FormItem
        required
        key="password"
        label={intl.get('hiam.userInfo.model.user.password').d('密码')}
        {...formItemLayout}
      >
        {getFieldDecorator('password', {
          rules: [
            {
              required: true,
              message: intl.get('hzero.common.validation.notNull', {
                name: intl.get('hiam.userInfo.model.userInfo.password').d('密码'),
              }),
            },
            {
              validator: (_, value, callback) => {
                validatePasswordRule(value, callback, { ...passwordTipMsg, loginName });
              },
            },
            {
              validator: (_, value, callback) => {
                validateNewPasswordNotSame(value, callback, form);
              },
            },
            {
              max: 110,
              message: intl.get('hzero.common.validation.max', {
                max: 110,
              }),
            },
          ],
        })(
          <Input
            type="password"
            onChange={(e) => {
              this.validatePasswordRepeatForPassword(e, form);
            }}
          />
        )}
      </FormItem>,
      // // 密码输入框改了, 判断是否需要确认密码
      <FormItem
        required
        key="anotherPassword"
        label={intl.get('hiam.userInfo.model.user.anotherPassword').d('确认密码')}
        {...formItemLayout}
      >
        {getFieldDecorator('anotherPassword', {
          rules: [
            {
              required: true,
              message: intl.get('hzero.common.validation.notNull', {
                name: intl.get('hiam.userInfo.model.userInfo.anotherPassword').d('确认密码'),
              }),
            },
            {
              validator: (_, value, callback) => {
                validatePasswordAnther(value, callback, form);
              },
            },
          ],
        })(<Input type="password" />)}
      </FormItem>,
      forceCodeVerify && (
        <FormItem
          required
          label={intl.get('hiam.userInfo.model.user.phone').d('手机号码')}
          {...formItemLayout}
        >
          {getFieldDecorator('phone', {
            initialValue: phone,
          })(<Input disabled />)}
        </FormItem>
      ),
      forceCodeVerify && (
        <FormItem
          required
          label={intl.get('hiam.userInfo.model.user.phoneCaptcha').d('短信验证码')}
          {...formItemLayout}
        >
          {getFieldDecorator('captcha', {
            validateTrigger: 'onBlur',
            rules: [
              {
                required: true,
                message: intl.get('hzero.common.validation.notNull', {
                  name: intl.get('hiam.userInfo.model.userInfo.phoneCaptcha').d('短信验证码'),
                }),
              },
            ],
          })(<Input style={{ width: 257, marginRight: 10 }} />)}
          <Button
            style={{ width: 90 }}
            disabled={validCodeSendLimitFlag}
            loading={postCaptchaLoading}
            onClick={() => {
              this.handleGainValidCodeBtnClick({
                type: 'verifyPhone',
                value: phone,
                modalProps: { ...modalProps, step: 'validatePhone' },
              });
            }}
          >
            {validCodeSendLimitFlag ? (
              <CountDown target={validCodeLimitTimeEnd} onEnd={this.handleValidCodeLimitEnd} />
            ) : (
              intl.get('hiam.userInfo.view.option.gainCaptcha').d('获取验证码')
            )}
          </Button>
        </FormItem>
      ),
    ].filter(Boolean);
  }

  // phone

  renderPhone() {
    const { userInfo = {} } = this.props;
    const btns = [];
    let content = '';
    if (userInfo.phoneCheckFlag === 1) {
      content = (
        <span className={styles['color-bind']}>
          <Icon type="check-circle" />
          &nbsp;
          {intl.get('hiam.userInfo.view.message.bind').d('已绑定')}
          &nbsp;
          {userInfo.phone}
          {userInfo.phone ? <>&nbsp;</> : ''}
        </span>
      );
      btns.push(
        <Button key="update" onClick={this.handlePhoneEdit}>
          {intl.get('hzero.common.button.update').d('修改')}
        </Button>
      );
    } else {
      content = (
        <span className={styles['color-unbind']}>
          <Icon type="info-circle" />
          &nbsp;
          {intl.get('hiam.userInfo.view.message.unbind').d('未绑定')}
          &nbsp;
          {userInfo.phone}
          {userInfo.phone ? <>&nbsp;</> : ''}
        </span>
      );
      btns.push(
        <Button key="bind" type="primary" onClick={this.handleUnCheckedPhoneBind}>
          {intl.get('hiam.userInfo.view.option.bind').d('绑定')}
        </Button>
      );
    }
    return (
      <MaxLenItem
        itemIcon={null}
        content={content}
        className={styles['max-len-item-content-wrapper-safe']}
        description={intl.get('hiam.userInfo.model.user.phoneBind').d('绑定手机')}
        comment={intl
          .get('hiam.userInfo.view.message.phoneBind')
          .d('可用手机号加密码登录HZERO，可通过手机号找回密码')}
        btns={btns}
      />
    );
  }

  // email

  renderEmail() {
    const { userInfo = {} } = this.props;
    const btns = [];
    let content = '';
    if (userInfo.emailCheckFlag === 1) {
      content = (
        <span className={styles['color-bind']}>
          <Icon type="check-circle" />
          &nbsp;
          {intl.get('hiam.userInfo.view.message.bind').d('已绑定')}
          &nbsp;
          {userInfo.email}
          {userInfo.email ? <>&nbsp;</> : ''}
        </span>
      );
      btns.push(
        <Button key="update" onClick={this.handleEmailEdit}>
          {intl.get('hzero.common.button.update').d('修改')}
        </Button>
      );
    } else {
      content = (
        <span className={styles['color-unbind']}>
          <Icon type="info-circle" />
          &nbsp;
          {intl.get('hiam.userInfo.view.message.unbind').d('未绑定')}
          &nbsp;
          {userInfo.email}
          {userInfo.email ? <>&nbsp;</> : ''}
        </span>
      );
      btns.push(
        <Button key="bind" type="primary" onClick={this.handleUnCheckedEmailBind}>
          {intl.get('hiam.userInfo.view.option.bind').d('绑定')}
        </Button>
      );
    }
    return (
      <MaxLenItem
        itemIcon={null}
        className={styles['max-len-item-content-wrapper-safe']}
        description={intl.get('hiam.userInfo.model.user.emailBind').d('邮箱绑定')}
        content={content}
        comment={intl
          .get('hiam.userInfo.view.message.emailBind')
          .d('可用邮箱加密码登录HZERO，可用邮箱找回密码')}
        btns={btns}
      />
    );
  }

  // open-app
  renderOpenApp() {
    const { openAccountList = [] } = this.props;
    return map(openAccountList, (openApp) => {
      const btns = [];
      if (openApp.openName) {
        btns.push(
          <Button
            key="unbind"
            className={styles['btn-bind']}
            onClick={() => this.handleUnBindApp(openApp)}
          >
            {intl.get('hiam.userInfo.view.option.unBind').d('解绑')}
          </Button>
        );
      } else {
        btns.push(
          <Button
            key="bind"
            type="primary"
            className={styles['btn-bind']}
            onClick={this.handleBindApp.bind(this, openApp)}
          >
            {intl.get('hiam.userInfo.view.option.bind').d('绑定')}
          </Button>
        );
      }
      return (
        <MaxLenItem
          key={openApp.openAppId}
          icon={<img src={openApp.appImage} alt="" style={{ height: 36, width: 36 }} />}
          description={openApp.appName}
          content={openApp.openName || ''}
          btns={btns}
        />
      );
    });
  }

  /**
   * @function handleBindApp - 获取绑定认证地址
   * @param {object} item - 第三方数据
   */
  handleBindApp(item) {
    window.open(
      `${AUTH_HOST}/open/${item.appCode}?channel=${
        item.channel
      }&access_token=${getAccessToken()}&bind_redirect_uri=${encodeURIComponent(window.location)}`,
      '_self',
      'noopener,noreferrer'
    );
  }

  /**
   * @function handleUnBindApp - 解除第三方绑定
   * @param {object} item - 第三方应用数据
   */
  @Bind()
  handleUnBindApp(item, setLoading) {
    const { dispatch, userInfo: { id } = {} } = this.props;
    Modal.confirm({
      title: `${intl.get('hiam.userInfo.view.confirmUnBind').d('确定解除绑定')}？`,
      onOk() {
        setLoading && setLoading(true);
        dispatch({
          type: 'userInfo/unBindOpenAccount',
          payload: { ...item },
        }).then((res) => {
          if (res) {
            notification.success();
            dispatch({
              type: 'userInfo/fetchOpenAccountList',
              payload: { userId: id },
            });
            setLoading && setLoading(false);
          }
        });
      },
    });
  }

  // todo

  /**
   * 发送验证码
   * 旧手机的验证码, 新手机的验证码, 新邮箱的验证码
   * 获取验证码, 使用同一个变量, 要注意使用 和 清除
   * @param {'oldPhone' | 'newPhone' | 'newEmail'} [type='oldPhone'] - 获取哪个验证码
   * @param {!String} value - 对应的手机号 或者 邮箱
   * @param {Object} ...params - 其他参数
   */
  @Bind()
  handleGainValidCodeBtnClick({ type = 'oldPhone', value, businessScope = 'self', ...params }) {
    const { dispatch, modalProps = {} } = this.props;
    dispatch({
      type: 'userInfo/postCaptcha',
      payload: { type, value, modalProps, businessScope, ...params },
    });
  }

  /**
   * 解除 - 计时限制
   * 倒计时组件 计时完成后 触发, 取消计时状态
   */
  @Bind()
  handleValidCodeLimitEnd() {
    const { dispatch, modalProps = {} } = this.props;
    dispatch({
      type: 'userInfo/captchaLimitEnd',
      payload: { modalProps },
    });
  }

  /**
   * 修改邮箱： 校验身份
   * @memberof UserInfo
   */
  @Bind()
  handleEmailEdit() {
    const {
      userInfo: { email },
    } = this.props;
    // 修改邮箱 前置校验成功后的回调
    const onNext = this.openUpdateNewEmailForm;
    // 校验身份, 成功后 打开 修改邮箱的模态框
    this.handleOpenForm({
      title: intl.get('hiam.userInfo.view.message.title.form.email').d('更改邮箱'),
      // 如果之前没有邮箱, 校验身份使用密码， 否值 默认使用 邮箱认证身份
      ...(email
        ? this.getValidateOldEmailFormProps(onNext)
        : this.getValidatePasswordFormProps(onNext)),
    });
  }

  /**
   * 打开修改邮箱的模态框
   */
  @Bind()
  openUpdateNewEmailForm() {
    this.handleOpenForm({
      ...this.getValidateNewEmailFormProps(),
    });
  }

  @Bind()
  handleUnCheckedEmailBind() {
    this.handleOpenForm(this.getValidateUnCheckedEmailFormProps());
  }

  /**
   *查看个人登录记录
   * @memberof SafeInfo
   */
  @Bind()
  handleCheckLoginLog() {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/hiam/user/login-log`,
      })
    );
  }

  /**
   * 修改手机： 校验身份
   * @memberof UserInfo
   */
  @Bind()
  handlePhoneEdit() {
    const {
      userInfo: { phone },
    } = this.props;
    // 修改手机 前置校验成功后的回调,
    const onNext = this.openUpdateNewPhoneForm;
    // 校验身份, 成功后 打开 修改手机的模态框
    this.handleOpenForm({
      title: intl.get('hiam.userInfo.view.message.title.form.phone').d('更改手机号码'),
      ...(phone
        ? this.getValidateOldPhoneFormProps(onNext)
        : this.getValidatePasswordFormProps(onNext)),
    });
  }

  @Bind()
  handleUnCheckedPhoneBind() {
    this.handleOpenForm(this.getValidateUnCheckedPhoneFormProps());
  }

  /**
   * 打开修改手机的模态框
   */
  @Bind()
  openUpdateNewPhoneForm() {
    this.handleOpenForm({
      ...this.getValidateNewPhoneFormProps(),
    });
  }

  /**
   * 获取 通过邮箱认证身份时的 EditFormModal 的属性
   * @param {Function} onNext - 通过邮箱 认证身份后的 下一步操作
   */
  @Bind()
  getValidateOldEmailFormProps(onNext) {
    const formProps = {
      step: 'validateOldEmail',
      okText: intl.get('hzero.common.button.next').d('下一步'),
      onCancel: this.handleCloseForm,
      onOk: (fieldsValue, form) => {
        const { modalProps = {}, dispatch } = this.props;
        const { captcha } = fieldsValue;
        form.resetFields();
        dispatch({
          type: 'userInfo/validatePreValidate',
          payload: { captcha, type: 'oldEmail', modalProps, businessScope: 'self' },
        }).then((res) => {
          if (res) {
            onNext();
          }
        });
      },
    };
    formProps.formItems = (form) => {
      const { getFieldDecorator } = form;
      const {
        userInfo,
        modalProps: { validCodeSendLimitFlag, validCodeLimitTimeEnd },
        postCaptchaLoading = false,
      } = this.props;
      return (
        <>
          <FormItem
            required
            label={intl.get('hiam.userInfo.model.user.email').d('邮箱')}
            {...formItemLayout}
          >
            {getFieldDecorator('oldEmail', {
              initialValue: userInfo.email,
            })(<Input disabled />)}
          </FormItem>
          <FormItem
            required
            label={intl.get('hiam.userInfo.model.user.emailCaptcha').d('邮箱验证码')}
            {...formItemLayout}
          >
            {getFieldDecorator('captcha', {
              validateTrigger: 'onBlur',
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hiam.userInfo.model.userInfo.emailCaptcha').d('邮箱验证码'),
                  }),
                },
              ],
            })(<Input style={{ width: '257px', marginRight: '10px' }} />)}
            <Button
              style={{ width: 90 }}
              loading={postCaptchaLoading}
              disabled={validCodeSendLimitFlag}
              onClick={() => {
                this.handleGainValidCodeBtnClick({ type: 'oldEmail', value: userInfo.email });
              }}
            >
              {validCodeSendLimitFlag ? (
                <CountDown target={validCodeLimitTimeEnd} onEnd={this.handleValidCodeLimitEnd} />
              ) : (
                intl.get('hiam.userInfo.view.option.gainCaptcha').d('获取验证码')
              )}
            </Button>
          </FormItem>
          <div style={{ textAlign: 'right' }}>
            <a
              onClick={() => {
                this.handleOpenForm({
                  ...this.getValidatePasswordFormProps(onNext),
                });
              }}
            >
              {intl
                .get('hiam.userInfo.view.message.cantReceiveEmailCaptcha')
                .d('邮箱无法收到验证码')}
              ？
            </a>
          </div>
        </>
      );
    };
    return formProps;
  }

  /**
   * 获取 通过手机认证身份时的 EditFormModal 的属性
   * @param {Function} onNext - 通过手机 认证身份后的 下一步操作
   */
  @Bind()
  getValidateOldPhoneFormProps(onNext) {
    const {
      userInfo: { phoneCheckFlag },
    } = this.props;
    if (phoneCheckFlag !== 1) {
      // 手机号 没有经过 校验, 则只能通过密码校验
      return this.getValidatePasswordFormProps(onNext);
    }
    const formProps = {
      step: 'validateOldPhone',
      okText: intl.get('hzero.common.button.next').d('下一步'),
      onCancel: this.handleCloseForm,
      onOk: (fieldsValue, form) => {
        const { modalProps = {}, dispatch } = this.props;
        const { captcha } = fieldsValue;
        form.resetFields();
        dispatch({
          type: 'userInfo/validatePreValidate',
          payload: { captcha, type: 'oldPhone', modalProps, businessScope: 'self' },
        }).then((res) => {
          if (res) {
            onNext();
          }
        });
      },
    };
    formProps.formItems = (form) => {
      const { getFieldDecorator } = form;
      const {
        userInfo,
        modalProps: { validCodeSendLimitFlag, validCodeLimitTimeEnd },
        postCaptchaLoading = false,
      } = this.props;
      return (
        <>
          <FormItem
            required
            label={intl.get('hiam.userInfo.model.user.phone').d('手机号码')}
            {...formItemLayout}
          >
            {getFieldDecorator('oldPhone', {
              initialValue: userInfo.phone,
            })(<Input disabled />)}
          </FormItem>
          <FormItem
            required
            label={intl.get('hiam.userInfo.model.user.phoneCaptcha').d('短信验证码')}
            {...formItemLayout}
          >
            {getFieldDecorator('captcha', {
              validateTrigger: 'onBlur',
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hiam.userInfo.model.userInfo.phoneCaptcha').d('短信验证码'),
                  }),
                },
              ],
            })(<Input style={{ width: 257, marginRight: 10 }} />)}
            <Button
              style={{ width: 90 }}
              disabled={validCodeSendLimitFlag}
              loading={postCaptchaLoading}
              onClick={() => {
                this.handleGainValidCodeBtnClick({ type: 'oldPhone', value: userInfo.phone });
              }}
            >
              {validCodeSendLimitFlag ? (
                <CountDown target={validCodeLimitTimeEnd} onEnd={this.handleValidCodeLimitEnd} />
              ) : (
                intl.get('hiam.userInfo.view.option.gainCaptcha').d('获取验证码')
              )}
            </Button>
          </FormItem>
          <div style={{ textAlign: 'right' }}>
            <a
              onClick={() => {
                this.handleOpenForm({
                  ...this.getValidatePasswordFormProps(onNext),
                });
              }}
            >
              {intl
                .get('hiam.userInfo.view.message.cantReceivePhoneCaptcha')
                .d('手机无法接收验证码')}
              ？
            </a>
          </div>
        </>
      );
    };
    return formProps;
  }

  /**
   * 获取 通过密码认证身份时的 EditFormModal 的属性
   * @param {Function} onNext - 通过密码 认证身份后的 下一步操作
   */
  @Bind()
  getValidatePasswordFormProps(onNext) {
    const { publicKey } = this.props;
    const formProps = {
      step: 'validatePassword',
      okText: intl.get('hzero.common.button.next').d('下一步'),
      onCancel: this.handleCloseForm,
      onOk: (fieldsValue, form) => {
        const { modalProps = {}, dispatch } = this.props;
        const { captcha } = fieldsValue;
        form.resetFields();
        dispatch({
          type: 'userInfo/validatePreValidate',
          payload: {
            captcha,
            captchaKey: modalProps.captchaKey,
            modalProps,
            businessScope: 'self',
          },
        }).then((res) => {
          if (res) {
            onNext();
          }
        });
      },
    };
    formProps.formItems = (form) => {
      const { getFieldDecorator } = form;
      return (
        <>
          <FormItem
            required
            label={intl.get('hiam.userInfo.model.user.originalPassword').d('原密码')}
            {...formItemLayout}
          >
            {getFieldDecorator('password', {
              validateTrigger: 'onBlur',
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hiam.userInfo.model.user.originalPassword').d('原密码'),
                  }),
                },
              ],
            })(<Input type="password" />)}
          </FormItem>
        </>
      );
    };
    formProps.onOk = (fieldsValue) => {
      const { password } = fieldsValue;
      const { modalProps = {}, dispatch } = this.props;
      dispatch({
        type: 'userInfo/validatePrePassword',
        payload: { password: encryptPwd(password, publicKey), modalProps, businessScope: 'self' },
      }).then((res) => {
        if (res) {
          if (isFunction(onNext)) {
            onNext();
          }
        }
      });
    };
    return formProps;
  }

  /**
   * 获取 修改邮箱时 EditFormModal 的属性
   */
  @Bind()
  getValidateNewEmailFormProps() {
    const formProps = {
      title: intl.get('hiam.userInfo.view.message.title.form.email').d('更改邮箱'),
      step: 'validateNewEmail',
      onCancel: this.handleCloseForm,
      onOk: (fieldsValue, form) => {
        const { captcha, newEmail } = fieldsValue;
        // 新的邮箱的 验证 key
        const {
          userInfo = {},
          modalProps: { lastCheckKey, captchaKey: emailCaptchaKey },
          dispatch,
        } = this.props;
        dispatch({
          type: 'userInfo/validateNewEmail',
          payload: {
            email: newEmail,
            captcha,
            captchaKey: emailCaptchaKey,
            lastCheckKey,
            userInfo,
            businessScope: 'self',
          },
        }).then((res) => {
          if (res) {
            form.resetFields();
            this.handleCloseForm();
          }
        });
      },
      formItems: (form) => {
        const { getFieldDecorator } = form;
        const {
          modalProps: { validCodeSendLimitFlag, validCodeLimitTimeEnd },
          postCaptchaLoading = false,
        } = this.props;
        return (
          <>
            <FormItem
              required
              label={intl.get('hiam.userInfo.model.user.email').d('邮箱')}
              {...formItemLayout}
            >
              {getFieldDecorator('newEmail', {
                validateTrigger: 'onBlur',
                validateFirst: true,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hiam.userInfo.model.user.email').d('邮箱'),
                    }),
                  },
                  {
                    pattern: EMAIL,
                    message: intl.get('hzero.common.validation.email').d('邮箱格式不正确'),
                  },
                  {
                    validator: this.validateEmailIsNoLastEmail,
                  },
                  {
                    validator: this.validateEmailRegister,
                  },
                ],
              })(<Input />)}
            </FormItem>
            <FormItem
              required
              label={intl.get('hiam.userInfo.model.user.emailCaptcha').d('邮箱验证码')}
              {...formItemLayout}
            >
              {getFieldDecorator('captcha', {
                validateTrigger: 'onBlur',
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hiam.userInfo.model.user.emailCaptcha').d('邮箱验证码'),
                    }),
                  },
                ],
              })(<Input style={{ width: 257, marginRight: 10 }} />)}
              <Button
                style={{ width: 90 }}
                disabled={validCodeSendLimitFlag}
                loading={postCaptchaLoading}
                onClick={() => {
                  const {
                    modalProps: { lastCheckKey },
                  } = this.props;
                  form.validateFields(['newEmail'], (err, fieldsValue) => {
                    if (!err) {
                      this.handleGainValidCodeBtnClick({
                        type: 'newEmail',
                        lastCheckKey,
                        value: fieldsValue.newEmail,
                      });
                    }
                  });
                }}
              >
                {validCodeSendLimitFlag ? (
                  <CountDown
                    target={validCodeLimitTimeEnd}
                    onEnd={this.handleValidCodeLimitEnd}
                    disabled={!!form.getFieldError('newEmail')}
                  />
                ) : (
                  intl.get('hiam.userInfo.view.option.gainCaptcha').d('获取验证码')
                )}
              </Button>
            </FormItem>
          </>
        );
      },
    };
    return formProps;
  }

  /**
   * 绑定邮箱 | 验证未验证的邮箱
   * 验证未验证的邮箱: 给邮箱发送验证码 后 修改邮箱
   * 绑定邮箱: 验证身份 后 修改邮箱
   */
  @Bind()
  getValidateUnCheckedEmailFormProps() {
    const {
      userInfo: { email, emailCheckFlag },
    } = this.props;
    let formProps;
    if (emailCheckFlag !== 1 && !email) {
      // 绑定邮箱
      formProps = this.getValidateOldPhoneFormProps(this.openUpdateNewEmailForm);
    } else {
      // 验证邮箱
      formProps = {
        step: 'validateUnCheckedEmail',
        onCancel: this.handleCloseForm,
        onOk: (fieldsValue, form) => {
          const { captcha } = fieldsValue;
          // 新的邮箱的 验证 key
          const {
            modalProps: { captchaKey: emailCaptchaKey },
            userInfo = {},
            dispatch,
          } = this.props;
          dispatch({
            type: 'userInfo/validateUnCheckedEmail',
            payload: {
              captcha,
              captchaKey: emailCaptchaKey,
              userInfo,
              businessScope: 'self',
            },
          }).then((res) => {
            if (res) {
              form.resetFields();
              this.handleCloseForm();
            }
          });
        },
        formItems: (form) => {
          const { getFieldDecorator } = form;
          const {
            modalProps: { validCodeSendLimitFlag, validCodeLimitTimeEnd },
            userInfo = {},
            postCaptchaLoading = false,
          } = this.props;
          return (
            <>
              <FormItem
                required
                label={intl.get('hiam.userInfo.model.user.email').d('邮箱')}
                {...formItemLayout}
              >
                {getFieldDecorator('oldEmail', {
                  initialValue: userInfo.email,
                  validateTrigger: 'onBlur',
                  validateFirst: true,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hiam.userInfo.model.user.email').d('邮箱'),
                      }),
                    },
                    {
                      pattern: EMAIL,
                      message: intl.get('hzero.common.validation.email').d('邮箱格式不正确'),
                    },
                  ],
                })(<Input disabled />)}
              </FormItem>
              <FormItem
                required
                label={intl.get('hiam.userInfo.model.user.emailCaptcha').d('邮箱验证码')}
                {...formItemLayout}
              >
                {getFieldDecorator('captcha', {
                  validateTrigger: 'onBlur',
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hiam.userInfo.model.user.emailCaptcha').d('邮箱验证码'),
                      }),
                    },
                  ],
                })(<Input style={{ width: 257, marginRight: 10 }} />)}
                <Button
                  style={{ width: 90 }}
                  disabled={validCodeSendLimitFlag}
                  loading={postCaptchaLoading}
                  onClick={() => {
                    form.validateFields(['oldEmail'], (err, fieldsValue) => {
                      if (!err) {
                        this.handleGainValidCodeBtnClick({
                          type: 'oldEmail',
                          value: fieldsValue.oldEmail,
                        });
                      }
                    });
                  }}
                >
                  {validCodeSendLimitFlag ? (
                    <CountDown
                      target={validCodeLimitTimeEnd}
                      onEnd={this.handleValidCodeLimitEnd}
                      disabled={!!form.getFieldError('oldEmail')}
                    />
                  ) : (
                    intl.get('hiam.userInfo.view.option.gainCaptcha').d('获取验证码')
                  )}
                </Button>
              </FormItem>
            </>
          );
        },
      };
    }
    formProps.title = intl
      .get('hiam.userInfo.view.message.title.form.unCheckedEmail')
      .d('绑定邮箱');
    return formProps;
  }

  /**
   * 获取 修改手机时 EditFormModal 的属性
   */
  @Bind()
  getValidateNewPhoneFormProps() {
    const formProps = {
      title: intl.get('hiam.userInfo.view.message.title.form.phone').d('更改手机号码'),
      step: 'validateNewPhone',
      onCancel: this.handleCloseForm,
      onOk: (fieldsValue, form) => {
        const { captcha, newPhone } = fieldsValue;
        // 新的邮箱的 验证 key
        const {
          modalProps: { lastCheckKey, captchaKey: phoneCaptchaKey },
          userInfo = {},
          dispatch,
        } = this.props;
        dispatch({
          type: 'userInfo/validateNewPhone',
          payload: {
            phone: newPhone,
            captcha,
            captchaKey: phoneCaptchaKey,
            lastCheckKey,
            userInfo,
            businessScope: 'self',
          },
        }).then((res) => {
          if (res) {
            form.resetFields();
            this.handleCloseForm();
          }
        });
      },
      formItems: (form) => {
        const { getFieldDecorator } = form;
        const {
          modalProps: { validCodeSendLimitFlag, validCodeLimitTimeEnd },
          postCaptchaLoading = false,
        } = this.props;
        return (
          <>
            <FormItem
              required
              label={intl.get('hiam.userInfo.model.user.phone').d('手机号码')}
              {...formItemLayout}
            >
              {getFieldDecorator('newPhone', {
                validateTrigger: 'onBlur',
                validateFirst: true,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hiam.userInfo.model.user.phone').d('手机号码'),
                    }),
                  },
                  {
                    pattern: PHONE,
                    message: intl.get('hzero.common.validation.phone').d('手机格式不正确'),
                  },
                  {
                    validator: this.validatePhoneIsNoLastPhone,
                  },
                  {
                    validator: this.validatePhoneRegister,
                  },
                ],
              })(<Input />)}
            </FormItem>
            <FormItem
              required
              label={intl.get('hiam.userInfo.model.user.phoneCaptcha').d('短信验证码')}
              {...formItemLayout}
            >
              {getFieldDecorator('captcha', {
                validateTrigger: 'onBlur',
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hiam.userInfo.model.user.phoneCaptcha').d('短信验证码'),
                    }),
                  },
                ],
              })(<Input style={{ width: 257, marginRight: 10 }} />)}
              <Button
                style={{ width: 90 }}
                disabled={validCodeSendLimitFlag}
                loading={postCaptchaLoading}
                onClick={() => {
                  const {
                    modalProps: { lastCheckKey },
                  } = this.props;
                  form.validateFields(['newPhone'], (err, fieldsValue) => {
                    if (!err) {
                      this.handleGainValidCodeBtnClick({
                        type: 'newPhone',
                        lastCheckKey,
                        value: fieldsValue.newPhone,
                      });
                    }
                  });
                }}
              >
                {validCodeSendLimitFlag ? (
                  <CountDown
                    target={validCodeLimitTimeEnd}
                    onEnd={this.handleValidCodeLimitEnd}
                    disabled={!!form.getFieldError('newPhone')}
                  />
                ) : (
                  intl.get('hiam.userInfo.view.option.gainCaptcha').d('获取验证码')
                )}
              </Button>
            </FormItem>
          </>
        );
      },
    };
    return formProps;
  }

  /**
   * 绑定手机号的表单
   * @returns {{title: *, step: string, onCancel: UserInfo.handleCloseForm, onOk: onOk, formItems: (function(*): *)}}
   */
  @Bind()
  getValidateUnCheckedPhoneFormProps() {
    const formProps = {
      title: intl.get('hiam.userInfo.view.message.title.form.unCheckedPhone').d('绑定手机号码'),
      step: 'validateUnCheckedPhone',
      onCancel: this.handleCloseForm,
      onOk: (fieldsValue, form) => {
        const { captcha } = fieldsValue;
        // 新的邮箱的 验证 key
        const {
          modalProps: { captchaKey: phoneCaptchaKey },
          userInfo = {},
          dispatch,
        } = this.props;
        dispatch({
          type: 'userInfo/validateUnCheckedPhone',
          payload: {
            captcha,
            captchaKey: phoneCaptchaKey,
            userInfo,
            businessScope: 'self',
          },
        }).then((res) => {
          if (res) {
            form.resetFields();
            this.handleCloseForm();
          }
        });
      },
      formItems: (form) => {
        const { getFieldDecorator } = form;
        const {
          modalProps: { validCodeSendLimitFlag, validCodeLimitTimeEnd },
          userInfo = {},
          postCaptchaLoading = false,
        } = this.props;
        return (
          <>
            <FormItem
              required
              label={intl.get('hiam.userInfo.model.user.phone').d('手机号码')}
              {...formItemLayout}
            >
              {getFieldDecorator('oldPhone', {
                initialValue: userInfo.phone,
                validateTrigger: 'onBlur',
                validateFirst: true,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hiam.userInfo.model.user.phone').d('手机号码'),
                    }),
                  },
                  {
                    pattern: PHONE,
                    message: intl.get('hzero.common.validation.phone').d('手机格式不正确'),
                  },
                ],
              })(<Input disabled />)}
            </FormItem>
            <FormItem
              required
              label={intl.get('hiam.userInfo.model.user.phoneCaptcha').d('短信验证码')}
              {...formItemLayout}
            >
              {getFieldDecorator('captcha', {
                validateTrigger: 'onBlur',
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hiam.userInfo.model.user.phoneCaptcha').d('短信验证码'),
                    }),
                  },
                ],
              })(<Input style={{ width: 257, marginRight: 10 }} />)}
              <Button
                style={{ width: 90 }}
                disabled={validCodeSendLimitFlag}
                loading={postCaptchaLoading}
                onClick={() => {
                  form.validateFields(['oldPhone'], (err, fieldsValue) => {
                    if (!err) {
                      this.handleGainValidCodeBtnClick({
                        type: 'oldPhone',
                        value: fieldsValue.oldPhone,
                      });
                    }
                  });
                }}
              >
                {validCodeSendLimitFlag ? (
                  <CountDown
                    target={validCodeLimitTimeEnd}
                    onEnd={this.handleValidCodeLimitEnd}
                    disabled={!!form.getFieldError('oldPhone')}
                  />
                ) : (
                  intl.get('hiam.userInfo.view.option.gainCaptcha').d('获取验证码')
                )}
              </Button>
            </FormItem>
          </>
        );
      },
    };
    return formProps;
  }

  /**
   * handleOpenForm - 打开模态框
   * @param {Object} payload - modalProps 的数据
   */
  @Bind()
  handleOpenForm(payload) {
    const { dispatch, modalProps = {} } = this.props;
    dispatch({
      type: 'userInfo/openForm',
      payload: { ...payload, modalProps },
    });
  }

  /**
   * 关闭模态框
   */
  @Bind()
  handleCloseForm() {
    const { dispatch, modalProps = {} } = this.props;
    dispatch({
      type: 'userInfo/closeForm',
      payload: { modalProps },
    });
  }

  // validations

  /**
   * validatePasswordAnther - 验证 新密码 和 确认密码 相同
   * @param {String} value - 新的密码
   * @param {Function} callback - 校验失败 需要回调错误， 否则空的回调
   * @param {Object} form - 表单
   * @memberof UserInfo
   */
  @Bind()
  validatePasswordAnther(value, callback, form) {
    if (value && value !== form.getFieldValue('password')) {
      callback(intl.get('hiam.userInfo.view.validation.passwordSame').d('确认密码必须与密码相同'));
    } else {
      callback();
    }
  }

  /**
   * 检查 确认密码是否与密码一致
   */
  @Bind()
  validatePasswordRepeatForPassword(e, form) {
    const anotherPassword = form.getFieldValue('anotherPassword');
    const anotherPasswordField = {
      value: anotherPassword,
    };
    if (e.target.value) {
      if (e.target.value === anotherPassword) {
        anotherPasswordField.errors = null;
      } else {
        anotherPasswordField.errors = [
          new Error(
            intl.get('hiam.userInfo.view.validation.passwordSame').d('确认密码必须与密码相同')
          ),
        ];
      }
    } else {
      anotherPasswordField.errors = null;
    }
    form.setFields({
      anotherPassword: anotherPasswordField,
    });
  }

  /**
   * validateNewPasswordNotSame - 验证新密码 不能和 旧密码 相同
   * @param {String} value - 新的密码
   * @param {Function} callback - 校验失败 需要回调错误， 否则空的回调
   * @param {Object} form - 表单
   * @memberof UserInfo
   */
  @Bind()
  validateNewPasswordNotSame(value, callback, form) {
    if (value && value === form.getFieldValue('originalPassword')) {
      callback(
        intl.get('hiam.userInfo.view.validation.passwordNoSame').d('新密码不能与原密码相同')
      );
    } else {
      callback();
    }
  }
}
