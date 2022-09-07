/**
 * EditFormModal
 * 编辑表单 默认为新建
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-05-27
 * @copyright 2019-05-27 © HAND
 */

import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Col, Form, Input, InputNumber, Modal, Row, Select, Spin, Tooltip, Icon } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { isNil } from 'lodash';

import Lov from 'components/Lov';

import { EDIT_FORM_CLASSNAME, MODAL_FORM_ITEM_LAYOUT } from 'utils/constants';

@Form.create({ fieldNameProp: null })
export default class EditFormModal extends Component {
  static propTypes = {
    isCreate: PropTypes.bool,
    organizationId: PropTypes.number.isRequired,
    languageMessage: PropTypes.object.isRequired,
    onOk: PropTypes.func.isRequired,
    onCancel: PropTypes.func.isRequired,
  };

  static defaultProps = {
    isCreate: true,
  };

  @Bind()
  handleModalOk() {
    const { form, onOk } = this.props;
    form.validateFields((err, fieldsValue) => {
      if (!err) {
        if (isNil(form.getFieldValue('typeNum'))) {
          const fieldValues = {
            ...fieldsValue,
            typeNum: -1,
            isCustom: 1,
          };
          onOk(fieldValues);
        } else {
          onOk(fieldsValue);
        }
      }
    });
  }

  @Bind()
  handleModalCancel() {
    const { onCancel } = this.props;
    onCancel();
  }

  render() {
    const {
      organizationId,
      isSiteFlag,
      form,
      isCreate,
      init,
      visible = false,
      record = {},
      languageMessage,
      createLoading = false,
      updateLoading = false,
      queryDetailLoading = false,
      basicDataType = [],
    } = this.props;
    const modalTitle = isCreate
      ? languageMessage.view.title.create
      : languageMessage.view.title.edit;
    return (
      <Modal
        destroyOnClose
        width={570}
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        visible={visible}
        title={modalTitle}
        onOk={this.handleModalOk}
        onCancel={this.handleModalCancel}
        confirmLoading={createLoading || updateLoading}
        cancelButtonProps={{ disabled: createLoading || updateLoading }}
        okButtonProps={{ disabled: queryDetailLoading }}
      >
        <Spin spinning={createLoading || updateLoading || queryDetailLoading}>
          <Form className={EDIT_FORM_CLASSNAME}>
            <Row>
              {isSiteFlag && (
                <Col>
                  <Form.Item
                    {...MODAL_FORM_ITEM_LAYOUT}
                    label={languageMessage.model.configTemplate.tenant}
                  >
                    {form.getFieldDecorator('tenantId', {
                      initialValue: record.tenantId,
                      rules: [
                        {
                          required: true,
                          message: languageMessage.common.validation.notNull(
                            languageMessage.model.configTemplate.tenant
                          ),
                        },
                      ],
                    })(
                      <Lov
                        allowClear
                        textValue={record.tenantName}
                        code="HPFM.TENANT"
                        disabled={!isCreate}
                        onChange={val => {
                          form.resetFields('templateId');
                          init(val);
                        }}
                      />
                    )}
                  </Form.Item>
                </Col>
              )}
              <Col>
                <Form.Item
                  {...MODAL_FORM_ITEM_LAYOUT}
                  label={languageMessage.model.configTemplate.template}
                >
                  {form.getFieldDecorator('templateId', {
                    initialValue: isCreate ? undefined : record.templateId,
                    rules: [
                      {
                        required: true,
                        message: languageMessage.common.validation.notNull(
                          languageMessage.model.configTemplate.template
                        ),
                      },
                    ],
                  })(
                    <Lov
                      allowClear
                      disabled={
                        isSiteFlag ? !isCreate || isNil(form.getFieldValue('tenantId')) : !isCreate
                      }
                      code="HNLP.TEMPLATE"
                      textValue={isCreate ? undefined : record.templateName}
                      queryParams={{
                        tenantId: isSiteFlag ? form.getFieldValue('tenantId') : organizationId,
                      }}
                    />
                  )}
                </Form.Item>
              </Col>
              <Col>
                <Form.Item
                  {...MODAL_FORM_ITEM_LAYOUT}
                  label={
                    <span>
                      {languageMessage.model.configTemplate.actualType}
                      <Tooltip title={languageMessage.model.configTemplate.actualTypeMsg}>
                        <Icon type="question-circle-o" />
                      </Tooltip>
                    </span>
                  }
                >
                  {form.getFieldDecorator('actualType', {
                    initialValue: isCreate ? undefined : record.actualType,
                    rules: [
                      {
                        required: true,
                        message: languageMessage.common.validation.notNull(
                          languageMessage.model.configTemplate.actualType
                        ),
                      },
                    ],
                  })(
                    <Select allowClear disabled={!isCreate}>
                      <Select.OptGroup label={languageMessage.common.predefined}>
                        {['$person', '$time', '$location', '$number', '$money'].map(value => (
                          <Select.Option key={value} value={value}>
                            {value}
                          </Select.Option>
                        ))}
                      </Select.OptGroup>
                      <Select.OptGroup label={languageMessage.common.custom}>
                        {basicDataType.map(item => (
                          <Select.Option key={item.value} value={item.value}>
                            {item.meaning}
                          </Select.Option>
                        ))}
                      </Select.OptGroup>
                    </Select>
                  )}
                </Form.Item>
              </Col>
              <Col>
                <Form.Item
                  {...MODAL_FORM_ITEM_LAYOUT}
                  label={
                    <span>
                      {languageMessage.model.configTemplate.type}
                      <Tooltip title={languageMessage.model.configTemplate.typeMsg}>
                        <Icon type="question-circle-o" />
                      </Tooltip>
                    </span>
                  }
                >
                  {form.getFieldDecorator('type', {
                    initialValue: isCreate ? undefined : record.type,
                    rules: [
                      {
                        required: true,
                        message: languageMessage.common.validation.notNull(
                          languageMessage.model.configTemplate.type
                        ),
                      },
                      {
                        max: 100,
                        message: languageMessage.common.validation.max(100),
                      },
                    ],
                  })(<Input />)}
                </Form.Item>
              </Col>
              <Col>
                <Form.Item
                  {...MODAL_FORM_ITEM_LAYOUT}
                  label={
                    <span>
                      {languageMessage.model.configTemplate.typeNum}
                      <Tooltip title={languageMessage.model.configTemplate.typeNumMsg}>
                        <Icon type="question-circle-o" />
                      </Tooltip>
                    </span>
                  }
                >
                  {form.getFieldDecorator('typeNum', {
                    initialValue: isCreate ? undefined : record.typeNum,
                  })(<InputNumber precision={0} min={-1} />)}
                </Form.Item>
              </Col>
              {/* <Col>
                <Form.Item
                  {...MODAL_FORM_ITEM_LAYOUT}
                  label={languageMessage.model.configTemplate.isCustom}
                >
                  {form.getFieldDecorator('isCustom', {
                    initialValue: isCreate ? 1 : record.isCustom,
                  })(<Switch />)}
                </Form.Item>
              </Col> */}
            </Row>
          </Form>
        </Spin>
      </Modal>
    );
  }
}
