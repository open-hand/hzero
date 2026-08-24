import React from 'react';
import { Modal, Input, Form, Button } from 'hzero-ui';
import { isFunction } from 'lodash';
import { Bind } from 'lodash-decorators';

import CountDown from 'components/CountDown';

import intl from 'utils/intl';
import { getSession } from 'utils/utils';

const { Item: FormItem } = Form;

@Form.create({ fieldNameProp: null })
export default class Password extends React.Component {
  @Bind()
  saveBtnClick() {
    const { onOk, form } = this.props;
    if (!isFunction(onOk)) {
      return;
    }
    form.validateFields((err, values) => {
      if (!err) {
        // todo
        onOk({
          phone: values.phone,
          captcha: values.captcha,
          captchaKey: values.phone ? getSession(`sub-account-org-phone`) : undefined,
          businessScope: values.phone ? 'UPDATE_PASSWORD' : undefined,
        });
      }
    });
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
   * 向对应的手机号发送验证码
   * @param {Object} params 验证手机号
   * @param {String} params.phone 手机号
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
        title={intl.get('hiam.subAccount.view.option.passwordReset').d('重置密码')}
      >
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
              <CountDown target={validCodeLimitTimeEnd} onEnd={this.handleValidCodeLimitTimeEnd} />
            ) : (
              intl.get('hiam.userInfo.view.option.gainCaptcha').d('获取验证码')
            )}
          </Button>
        </FormItem>
      </Modal>
    );
  }
}
