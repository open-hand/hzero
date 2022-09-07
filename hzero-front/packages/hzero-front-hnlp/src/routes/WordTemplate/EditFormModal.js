/**
 * EditFormModal
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-05-29
 * @copyright 2019-05-29 © HAND
 */
import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Col, Form, Input, Modal, Row } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { isNil } from 'lodash';

import Lov from 'components/Lov';

import { EDIT_FORM_CLASSNAME, EDIT_FORM_ROW_LAYOUT, MODAL_FORM_ITEM_LAYOUT } from 'utils/constants';

@Form.create({ fieldNameProp: null })
export default class EditFormModal extends Component {
  static propTypes = {
    isCreate: PropTypes.bool,
    languageMessage: PropTypes.object.isRequired,
  };

  static defaultProps = {
    isCreate: true,
  };

  @Bind()
  handleModalOk() {
    const { form, onOk } = this.props;
    form.validateFields((err, fieldsValue) => {
      if (!err) {
        onOk(fieldsValue);
      }
    });
  }

  @Bind()
  handleChangeTenantId() {
    this.props.form.resetFields('templateId');
  }

  @Bind()
  handleModalCancel() {
    const { onCancel } = this.props;
    onCancel();
  }

  render() {
    const {
      form,
      organizationId,
      isCreate,
      visible = false,
      record = {},
      languageMessage,
      createLoading = false,
      updateLoading = false,
      queryDetailLoading = false,
      isTenantRoleLevel,
    } = this.props;
    const modalTitle = isCreate
      ? languageMessage.view.title.create
      : languageMessage.view.title.edit;
    return (
      <Modal
        destroyOnClose
        width={520}
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
        <Form className={EDIT_FORM_CLASSNAME}>
          <Row {...EDIT_FORM_ROW_LAYOUT}>
            <Col>
              {!isTenantRoleLevel && (
                <Form.Item
                  {...MODAL_FORM_ITEM_LAYOUT}
                  label={languageMessage.model.templateWord.tenant}
                >
                  {form.getFieldDecorator('tenantId', {
                    initialValue: isCreate ? undefined : record.tenantId,
                    rules: [
                      {
                        required: true,
                        message: languageMessage.common.validation.notNull(
                          languageMessage.model.templateWord.tenant
                        ),
                      },
                    ],
                  })(
                    <Lov
                      disabled={!isCreate}
                      code="HPFM.TENANT"
                      textValue={isCreate ? undefined : record.tenantName}
                      onChange={this.handleChangeTenantId}
                    />
                  )}
                </Form.Item>
              )}
            </Col>
            <Col>
              <Form.Item
                {...MODAL_FORM_ITEM_LAYOUT}
                label={languageMessage.model.templateWord.template}
              >
                {form.getFieldDecorator('templateId', {
                  initialValue: isCreate ? undefined : record.templateId,
                  rules: [
                    {
                      required: true,
                      message: languageMessage.common.validation.notNull(
                        languageMessage.model.templateWord.template
                      ),
                    },
                  ],
                })(
                  <Lov
                    disabled={
                      !isCreate || (!isTenantRoleLevel && isNil(form.getFieldValue('tenantId')))
                    }
                    code="HNLP.TEMPLATE"
                    textValue={isCreate ? undefined : record.templateName}
                    queryParams={{
                      tenantId: !isTenantRoleLevel
                        ? form.getFieldValue('tenantId')
                        : organizationId,
                    }}
                  />
                )}
              </Form.Item>
            </Col>
            <Col>
              <Form.Item
                {...MODAL_FORM_ITEM_LAYOUT}
                label={languageMessage.model.templateWord.actualWord}
              >
                {form.getFieldDecorator('actualWord', {
                  initialValue: isCreate ? undefined : record.actualWord,
                  rules: [
                    {
                      required: true,
                      message: languageMessage.common.validation.notNull(
                        languageMessage.model.templateWord.actualWord
                      ),
                    },
                    {
                      max: 300,
                      message: languageMessage.common.validation.max(300),
                    },
                  ],
                })(<Input />)}
              </Form.Item>
            </Col>
            <Col>
              <Form.Item
                {...MODAL_FORM_ITEM_LAYOUT}
                label={languageMessage.model.templateWord.word}
              >
                {form.getFieldDecorator('word', {
                  initialValue: isCreate ? undefined : record.word,
                  rules: [
                    {
                      max: 300,
                      message: languageMessage.common.validation.max(300),
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
}
