/**
 * EditFormModal
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-07-10
 * @copyright 2019-07-10 © HAND
 */

import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Col, Form, Input, InputNumber, Modal, Row, Select } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';

import { MODAL_FORM_ITEM_LAYOUT } from 'utils/constants';

@Form.create({ fieldNameProp: null })
export default class EditFormModal extends Component {
  static propTypes = {
    onOk: PropTypes.func.isRequired,
    onCancel: PropTypes.func.isRequired,
    isCreate: PropTypes.bool.isRequired,
    // fieldType: PropTypes.array.isRequired,
    // queryLoading: PropTypes.bool.isRequired,
    // saveLoading: PropTypes.bool.isRequired,
  };

  // Modal
  @Bind()
  handleOk() {
    const { form } = this.props;
    form.validateFields((err, data) => {
      if (!err) {
        const { onOk } = this.props;
        onOk(data);
      }
    });
  }

  @Bind()
  handleCancel() {
    const { onCancel } = this.props;
    onCancel();
  }

  render() {
    const {
      isCreate = true,
      visible = false,
      loading = false,
      form,
      record = {},
      fieldType = [],
    } = this.props;
    const title = isCreate
      ? intl.get('hiam.apiField.view.title.fieldCreate').d('字段新建')
      : intl.get('hiam.apiField.view.title.fieldEdit').d('字段编辑');
    return (
      <Modal
        destroyOnClose
        title={title}
        visible={visible}
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        onCancel={this.handleCancel}
        onOk={this.handleOk}
        confirmLoading={loading}
      >
        <Form>
          <Row>
            <Col>
              <Form.Item
                {...MODAL_FORM_ITEM_LAYOUT}
                label={intl.get('hiam.apiField.model.field.fieldName').d('字段名称')}
              >
                {form.getFieldDecorator('fieldName', {
                  initialValue: record.fieldName,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hiam.apiField.model.field.fieldName').d('字段名称'),
                      }),
                    },
                    {
                      max: 120,
                      message: intl.get('hzero.common.validation.max', { max: 120 }),
                    },
                  ],
                })(<Input disabled={!isCreate} />)}
              </Form.Item>
            </Col>
            <Col>
              <Form.Item
                {...MODAL_FORM_ITEM_LAYOUT}
                label={intl.get('hiam.apiField.model.field.fieldType').d('字段类型')}
              >
                {form.getFieldDecorator('fieldType', {
                  initialValue: record.fieldType,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hiam.apiField.model.field.fieldType').d('字段类型'),
                      }),
                    },
                  ],
                })(
                  <Select>
                    {fieldType.map(item => (
                      <Select.Option key={item.value} value={item.value}>
                        {item.meaning}
                      </Select.Option>
                    ))}
                  </Select>
                )}
              </Form.Item>
            </Col>
            <Col>
              <Form.Item
                {...MODAL_FORM_ITEM_LAYOUT}
                label={intl.get('hiam.apiField.model.field.fieldDescription').d('字段描述')}
              >
                {form.getFieldDecorator('fieldDescription', {
                  initialValue: record.fieldDescription,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hiam.apiField.model.field.fieldDescription').d('字段描述'),
                      }),
                    },
                    {
                      max: 480,
                      message: intl.get('hzero.common.validation.max', { max: 480 }),
                    },
                  ],
                })(<Input />)}
              </Form.Item>
            </Col>
            <Col>
              <Form.Item
                {...MODAL_FORM_ITEM_LAYOUT}
                label={intl.get('hiam.apiField.model.field.orderSeq').d('排序号')}
              >
                {form.getFieldDecorator('orderSeq', {
                  initialValue: record.orderSeq || 0,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hiam.apiField.model.field.orderSeq').d('排序号'),
                      }),
                    },
                  ],
                })(<InputNumber style={{ width: '100%' }} precision={0} min={0} />)}
              </Form.Item>
            </Col>
          </Row>
        </Form>
      </Modal>
    );
  }
}
