/**
 * EditFormModal
 * 编辑表单 默认为新建
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-05-27
 * @copyright 2019-05-27 © HAND
 */

import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Col, Form, Input, Modal, Row, Select, Spin, Tooltip, Icon } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import Lov from 'components/Lov';
import intl from 'utils/intl';
import Switch from 'components/Switch';
import TLEditor from 'components/TLEditor';

import { EDIT_FORM_CLASSNAME, EDIT_FORM_ROW_LAYOUT, MODAL_FORM_ITEM_LAYOUT } from 'utils/constants';
import { CODE_UPPER } from 'utils/regExp';

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
  handleModalCancel() {
    const { onCancel } = this.props;
    onCancel();
  }

  render() {
    const {
      form,
      isCreate,
      visible = false,
      record = {},
      languageMessage,
      modelAccuracy = [],
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
        <Spin spinning={updateLoading || createLoading || queryDetailLoading}>
          <Form className={EDIT_FORM_CLASSNAME}>
            <Row {...EDIT_FORM_ROW_LAYOUT}>
              <Col>
                {!isTenantRoleLevel && (
                  <Form.Item
                    {...MODAL_FORM_ITEM_LAYOUT}
                    label={languageMessage.model.template.tenantName}
                  >
                    {form.getFieldDecorator('tenantId', {
                      initialValue: isCreate ? undefined : record.tenantId,
                      rules: [
                        {
                          required: true,
                          message: languageMessage.common.validation.notNull(
                            languageMessage.model.template.tenantName
                          ),
                        },
                      ],
                    })(
                      <Lov
                        code="HPFM.TENANT"
                        disabled={!isCreate}
                        textValue={isCreate ? undefined : record.tenantName}
                      />
                    )}
                  </Form.Item>
                )}
              </Col>
              <Col>
                <Form.Item
                  {...MODAL_FORM_ITEM_LAYOUT}
                  label={
                    <span>
                      {languageMessage.model.template.templateCode}&nbsp;
                      <Tooltip
                        title={intl
                          .get('hnlp.template.model.template.templateCode.type')
                          .d('在调用识别接口时使用的编码参数')}
                      >
                        <Icon type="question-circle-o" />
                      </Tooltip>
                    </span>
                  }
                >
                  {form.getFieldDecorator('templateCode', {
                    initialValue: isCreate ? undefined : record.templateCode,
                    rules: [
                      {
                        required: true,
                        message: languageMessage.common.validation.notNull(
                          languageMessage.model.template.templateCode
                        ),
                      },
                      {
                        pattern: CODE_UPPER,
                        message: intl
                          .get('hzero.common.validation.codeUpper')
                          .d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                      },
                      {
                        max: 100,
                        message: languageMessage.common.validation.max(100),
                      },
                    ],
                  })(<Input trim inputChinese={false} disabled={!isCreate} typeCase="upper" />)}
                </Form.Item>
              </Col>
              <Col>
                <Form.Item
                  {...MODAL_FORM_ITEM_LAYOUT}
                  label={languageMessage.model.template.templateName}
                >
                  {form.getFieldDecorator('templateName', {
                    initialValue: isCreate ? undefined : record.templateName,
                    rules: [
                      {
                        required: true,
                        message: languageMessage.common.validation.notNull(
                          languageMessage.model.template.templateName
                        ),
                      },
                      {
                        max: 100,
                        message: languageMessage.common.validation.max(100),
                      },
                    ],
                  })(
                    <TLEditor
                      label={languageMessage.model.template.templateName}
                      field="templateName"
                      token={record ? record._token : null}
                    />
                  )}
                </Form.Item>
              </Col>
              <Col>
                <Form.Item
                  {...MODAL_FORM_ITEM_LAYOUT}
                  label={
                    <span>
                      {languageMessage.model.template.replaceChar}&nbsp;
                      <Tooltip
                        title={intl
                          .get('hnlp.template.model.template.replaceChar.type')
                          .d('识别文本中可能存在的特殊字符，识别时会按照字符替换为空')}
                      >
                        <Icon type="question-circle-o" />
                      </Tooltip>
                    </span>
                  }
                >
                  {form.getFieldDecorator('replaceChar', {
                    initialValue: isCreate ? undefined : record.replaceChar,
                    rules: [
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
                  label={
                    <span>
                      {languageMessage.model.template.maxGram}&nbsp;
                      <Tooltip
                        title={intl
                          .get('hnlp.template.model.template.maxGram.type')
                          .d('识别算法的准确度，准确度越高，误识别率越低，识别性能消耗越高')}
                      >
                        <Icon type="question-circle-o" />
                      </Tooltip>
                    </span>
                  }
                >
                  {form.getFieldDecorator('maxGram', {
                    // Select 需要转为字符串
                    initialValue: isCreate ? '4' : `${record.maxGram || ''}`,
                    rules: [
                      {
                        required: true,
                        message: languageMessage.common.validation.notNull(
                          languageMessage.model.template.maxGram
                        ),
                      },
                    ],
                  })(
                    <Select allowClear>
                      {modelAccuracy.map((item) => (
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
                  label={languageMessage.model.template.description}
                >
                  {form.getFieldDecorator('description', {
                    initialValue: isCreate ? undefined : record.description,
                    rules: [
                      {
                        max: 600,
                        message: languageMessage.common.validation.max(600),
                      },
                    ],
                  })(<Input />)}
                </Form.Item>
              </Col>
              <Col>
                <Form.Item {...MODAL_FORM_ITEM_LAYOUT} label={languageMessage.common.status.enable}>
                  {form.getFieldDecorator('enabledFlag', {
                    initialValue: isCreate ? 1 : record.enabledFlag,
                  })(<Switch />)}
                </Form.Item>
              </Col>
            </Row>
          </Form>
        </Spin>
      </Modal>
    );
  }
}
