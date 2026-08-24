/**
 * ModalForm
 * 仅用于密码修改框
 * @date 2018/11/26
 * @author WY yang.wang06@hand-china.com
 * @copyright Copyright (c) 2018, Hand
 */

import React from 'react';
import { Form, Modal } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

function noop() {}

@Form.create({ fieldNameProp: null })
export default class ModalForm extends React.Component {
  render() {
    const { modalProps, renderFormItems = noop, form } = this.props;
    return (
      <Modal {...modalProps} destroyOnClose afterClose={this.cleanForm} onOk={this.handleOk}>
        {renderFormItems(form)}
      </Modal>
    );
  }

  @Bind()
  cleanForm() {
    const { form } = this.props;
    form.resetFields();
  }

  @Bind()
  handleOk() {
    const { form } = this.props;
    form.validateFields((err, fieldsValue) => {
      if (!err) {
        const { onOk } = this.props;
        onOk(fieldsValue, form);
      }
    });
  }
}
