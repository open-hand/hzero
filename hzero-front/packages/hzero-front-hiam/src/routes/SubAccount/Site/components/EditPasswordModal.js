import React from 'react';
import { Modal, Input, Form, Button } from 'hzero-ui';

import { isFunction } from 'lodash';
import { Bind } from 'lodash-decorators';

import CountDown from 'components/CountDown';

import intl from 'utils/intl';
import { encryptPwd, getSession } from 'utils/utils';
import { validatePasswordRule } from '@/utils/validator';

const { Item: FormItem } = Form;

@Form.create({ fieldNameProp: null })
export default class EditPasswordModal extends React.Component {
  @Bind()
  saveBtnClick() {
    const { onOk, form, publicKey } = this.props;
    if (!isFunction(onOk)) {
      return;
    }
    form.validateFields((err, values) => {
      if (!err) {
        // todo
        onOk({
          password: encryptPwd(values.password, publicKey),
          anotherPassword: encryptPwd(values.anotherPassword, publicKey),
          phone: values.phone,
          captcha: values.captcha,
          captchaKey: values.phone ? getSession(`sub-account-phone`) : undefined,
          businessScope: values.phone ? 'UPDATE_PASSWORD' : undefined,
        });
      }
    });
  }

  /**
   * 检查 确认密码是否与密码一致
   * @param {String} rule
   * @param {String} value
   * @param {Function} callback
   */
  @Bind()
  validatePasswordRepeat(rule, value, callback) {
    const { form } = this.props;
    if (value && value !== form.getFieldValue('password')) {
      callback(
        intl.get('hiam.subAccount.view.validation.passwordSame').d('确认密码必须与密码一致')
      );
    } else {
      callback();
    }
  }

  /**
   * 检查 确认密码是否与密码一致
   */
  @Bind()
  validatePasswordRepeatForPassword(e) {
    const { form } = this.props;
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
            intl.get('hiam.subAccount.view.validation.passwordSame').d('确认密码必须与密码一致')
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
   * 检查 新密码与原密码不能相同
   * @param {String} rule
   * @param {String} value
   * @param {Function} callback
   */
  @Bind()
  validateNewPasswordNotSame(rule, value, callback) {
    const { form } = this.props;
    if (value && value === form.getFieldValue('originalPassword')) {
      callback(
        intl.get('hiam.subAccount.view.validation.passwordNoSame').d('新密码不能与原密码相同')
      );
    } else {
      callback();
    }
  }

  /**
   * 向对应的手机号发送验证码
   * @param {Object} params 验证手机号
   * @param {String} params.phone 手机号
   */
  @Bind()
  sendCaptcha() {
    const { onSend = (e) => e } = this.props;
    onSend();
  }

  /**
   * 停止计时
   */
  @Bind()
  handleValidCodeLimitTimeEnd() {
    const { onEnd = (e) => e } = this.props;
    onEnd();
  }

  render() {
    const {
      form,
      visible,
      onCancel,
      confirmLoading,
      phone,
      editRecord: { loginName },
      passwordTipMsg = {},
      validCodeSendLimitFlag = false,
      validCodeLimitTimeEnd = 0,
      postCaptchaLoading,
    } = this.props;
    const { getFieldDecorator } = form;
    const labelCol = { md: 6 };
    const wrapperCol = { md: 12 };
    return (
      <Modal
        destroyOnClose
        confirmLoading={confirmLoading}
        width={500}
        visible={visible}
        onOk={this.saveBtnClick}
        onCancel={onCancel}
        title={intl.get('hiam.subAccount.view.option.passwordUpdate').d('修改密码')}
      >
        <FormItem
          required
          key="password"
          labelCol={labelCol}
          wrapperCol={wrapperCol}
          label={intl.get('hiam.subAccount.model.user.password').d('密码')}
        >
          {getFieldDecorator('password', {
            rules: [
              {
                required: true,
                message: intl.get('hzero.common.validation.notNull', {
                  name: intl.get('hiam.subAccount.model.subAccount.password').d('密码'),
                }),
              },
              {
                validator: (_, value, callback) => {
                  validatePasswordRule(value, callback, { ...passwordTipMsg, loginName });
                },
              },
              {
                validator: this.validateNewPasswordNotSame,
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
              autoComplete="new-password"
              onChange={this.validatePasswordRepeatForPassword}
            />
          )}
        </FormItem>
        <FormItem
          required
          labelCol={labelCol}
          wrapperCol={wrapperCol}
          key="anotherPassword"
          label={intl.get('hiam.subAccount.model.user.anotherPassword').d('确认密码')}
        >
          {getFieldDecorator('anotherPassword', {
            rules: [
              {
                required: true,
                message: intl.get('hzero.common.validation.notNull', {
                  name: intl.get('hiam.subAccount.model.subAccount.anotherPassword').d('确认密码'),
                }),
              },
              {
                validator: this.validatePasswordRepeat,
              },
              {
                max: 110,
                message: intl.get('hzero.common.validation.max', {
                  max: 110,
                }),
              },
            ],
          })(<Input type="password" autoComplete="new-password" />)}
        </FormItem>
        {passwordTipMsg.forceCodeVerify && (
          <FormItem
            required
            labelCol={labelCol}
            wrapperCol={wrapperCol}
            label={intl.get('hiam.subAccount.model.user.phone').d('手机号码')}
          >
            {getFieldDecorator('phone', {
              initialValue: phone,
            })(<Input disabled />)}
          </FormItem>
        )}
        {passwordTipMsg.forceCodeVerify && (
          <FormItem
            required
            labelCol={labelCol}
            wrapperCol={wrapperCol}
            label={intl.get('hiam.subAccount.model.user.phoneCaptcha').d('短信验证码')}
          >
            {getFieldDecorator('captcha', {
              validateTrigger: 'onBlur',
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hiam.subAccount.model.userInfo.phoneCaptcha').d('短信验证码'),
                  }),
                },
              ],
            })(<Input style={{ width: 125, marginRight: 10 }} />)}
            <Button
              style={{ width: 90 }}
              disabled={validCodeSendLimitFlag}
              loading={postCaptchaLoading}
              onClick={this.sendCaptcha}
            >
              {validCodeSendLimitFlag ? (
                <CountDown
                  target={validCodeLimitTimeEnd}
                  onEnd={this.handleValidCodeLimitTimeEnd}
                />
              ) : (
                intl.get('hiam.userInfo.view.option.gainCaptcha').d('获取验证码')
              )}
            </Button>
          </FormItem>
        )}
      </Modal>
    );
  }
}
