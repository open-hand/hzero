/**
 * Drawer - 服务器定义-抽屉
 * @date: 2019-7-2
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import React from 'react';
import { Form, Modal, Input, Row, Col } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import { getCurrentOrganizationId, encryptPwd } from 'utils/utils';
import { MODAL_FORM_ITEM_LAYOUT } from 'utils/constants';

@Form.create({ fieldNameProp: null })
export default class Drawer extends React.Component {
  /**
   * validateNewPasswordNotSame - 验证新密码 不能和 确认密码 不同
   * @param {String} value - 新的密码
   * @param {Function} callback - 校验失败 需要回调错误， 否则空的回调
   * @param {Object} form - 表单
   * @memberof UserInfo
   */
  @Bind()
  validateNewPasswordNotSame(value, callback, form) {
    if (value && value !== form.getFieldValue('loginEncPwd')) {
      callback(intl.get('hpfm.ssoConfig.model.ssoConfig.differentPassword').d('密码与之前不一致'));
    } else {
      callback();
    }
  }

  @Bind()
  handleOk() {
    const { form, onOk, initData, publicKey } = this.props;
    form.validateFields((err, fieldsValue) => {
      if (!err) {
        const fieldValues = {
          tenantId: getCurrentOrganizationId(),
          ...initData,
          loginEncPwd: encryptPwd(fieldsValue.loginEncPwd, publicKey),
          confirmPassword: encryptPwd(fieldsValue.confirmPassword, publicKey),
        };
        onOk(fieldValues);
      }
    });
  }

  render() {
    const { modalVisible, title = '', loading = false, onCancel, form } = this.props;
    const { getFieldDecorator } = form;
    return (
      <Modal
        destroyOnClose
        title={title}
        visible={modalVisible}
        confirmLoading={loading}
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        onCancel={onCancel}
        onOk={this.handleOk}
      >
        <Form>
          <Row>
            <Col>
              <Form.Item
                label={intl.get('hpfm.ssoConfig.model.ssoConfig.newPassword').d('新密码')}
                {...MODAL_FORM_ITEM_LAYOUT}
              >
                {getFieldDecorator('loginEncPwd', {
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hpfm.ssoConfig.model.ssoConfig.newPassword').d('新密码'),
                      }),
                    },
                    {
                      max: 60,
                      message: intl.get('hzero.common.validation.max', {
                        max: 60,
                      }),
                    },
                  ],
                })(<Input type="password" trim inputChinese={false} />)}
              </Form.Item>
            </Col>
            <Col>
              <Form.Item
                label={intl.get('hpfm.ssoConfig.model.ssoConfig.confirmPassword').d('确认密码')}
                {...MODAL_FORM_ITEM_LAYOUT}
              >
                {getFieldDecorator('confirmPassword', {
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl
                          .get('hpfm.ssoConfig.model.ssoConfig.confirmPassword')
                          .d('确认密码'),
                      }),
                    },
                    {
                      validator: (_, value, callback) => {
                        this.validateNewPasswordNotSame(value, callback, form);
                      },
                    },
                    {
                      max: 60,
                      message: intl.get('hzero.common.validation.max', {
                        max: 60,
                      }),
                    },
                  ],
                })(<Input type="password" trim inputChinese={false} />)}
              </Form.Item>
            </Col>
          </Row>
        </Form>
      </Modal>
    );
  }
}
