/**
 * BtnTitleEditModal.js
 * @date 2018-12-19
 * @author WY yang.wang06@hand-china.com
 * @copyright Copyright (c) 2018, Hand
 */

import React from 'react';
import PropTypes from 'prop-types';
import { Col, Form, Input, Modal, Row } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';

const formItemLayout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 16 },
};

@Form.create({ fieldNameProp: null })
export default class BtnTitleEditModal extends React.Component {
  static propTypes = {
    onCancel: PropTypes.func.isRequired,
    onSave: PropTypes.func.isRequired,
    title: PropTypes.string.isRequired,
  };

  render() {
    const { form, btnKey, btnProps, btnConfig, index, title, ...modalProps } = this.props;
    return (
      <Modal
        {...modalProps}
        width={520}
        title="按钮名称编辑"
        onOk={this.handleSave}
        onCancel={this.handleCancel}
      >
        <Form>
          <Row>
            <Col>
              <Form.Item {...formItemLayout} label="按钮名称">
                {form.getFieldDecorator('title', {
                  initialValue: title,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', { name: '按钮名称' }),
                    },
                  ],
                })(<Input />)}
              </Form.Item>
            </Col>
          </Row>
        </Form>
      </Modal>
    );
  }

  @Bind()
  handleSave() {
    const { form } = this.props;
    form.validateFields(['title'], (err, { title } = {}) => {
      if (!err) {
        const { onSave } = this.props;
        onSave(title);
      }
    });
  }

  @Bind()
  handleCancel() {
    const { onCancel } = this.props;
    onCancel();
  }
}
