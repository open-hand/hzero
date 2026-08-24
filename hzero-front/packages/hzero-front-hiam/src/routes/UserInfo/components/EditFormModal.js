/**
 * EditFormModal.js - 个人中心的编辑模态框
 * @date 2018/11/15
 * @author WY yang.wang06@hand-china.com
 * @copyright Copyright (c) 2018, Hand
 */

import React from 'react';
import { Modal, Form } from 'hzero-ui';
import { isFunction } from 'lodash';

import { Bind } from 'lodash-decorators';

@Form.create({ fieldNameProp: 'key' })
export default class EditFormModal extends React.PureComponent {
  @Bind()
  handleOk() {
    const { onOk, form } = this.props;
    if (isFunction(onOk)) {
      form.validateFields((err, fieldsValue) => {
        if (!err) {
          onOk(fieldsValue, form);
        }
      });
    }
  }

  @Bind()
  handleCancel() {
    const { onCancel, form } = this.props;
    if (isFunction(onCancel)) {
      form.resetFields();
      onCancel(form);
    }
  }

  @Bind()
  handleAfterClose() {
    const { form } = this.props;
    form.resetFields();
  }

  render() {
    const { formItems = [], onOk, onCancel, form, ...modalProps } = this.props;
    return (
      <Modal
        {...modalProps}
        onOk={this.handleOk}
        onCancel={this.handleCancel}
        afterClose={this.handleAfterClose}
      >
        <Form>{isFunction(formItems) ? formItems(form) : formItems}</Form>
      </Modal>
    );
  }
}
