/**
 * 接口字段权限维护
 * /hiam/role/field/:roleId/:permissionId
 * EditFormModal
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-07-10
 * @copyright 2019-07-10 © HAND
 */

import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Col, Form, Input, Modal, Row, Select } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import Lov from 'components/Lov';

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

  // field change
  /**
   * 字段改变后 修改字段类型
   * @param {FIELD} field
   */
  @Bind()
  handleFieldChange(_, field) {
    const { form } = this.props;
    form.setFieldsValue({
      fieldType: field.fieldType,
    });
  }

  render() {
    const {
      isCreate = true,
      visible = false,
      loading = false,
      form,
      record = {},
      fieldType = [],
      permissionRule = [],
      dimensionValue,
      permissionDimension,
      permissionId,
      organizationId,
    } = this.props;
    const title = isCreate
      ? intl.get('hiam.roleManagement.view.title.fieldPermissionCreate').d('字段权限新建')
      : intl.get('hiam.roleManagement.view.title.fieldPermissionEdit').d('字段权限编辑');
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
                label={intl
                  .get('hiam.roleManagement.model.fieldPermission.fieldName')
                  .d('字段名称')}
              >
                {form.getFieldDecorator('fieldId', {
                  initialValue: record.fieldId,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl
                          .get('hiam.roleManagement.model.fieldPermission.fieldName')
                          .d('字段名称'),
                      }),
                    },
                  ],
                })(
                  <Lov
                    queryParams={{
                      dimensionValue,
                      permissionDimension,
                      permissionId,
                      organizationId,
                    }}
                    code="HIAM.FIELD.PERMISSION"
                    disabled={!isCreate}
                    textValue={record.fieldDescription}
                    onChange={this.handleFieldChange}
                  />
                )}
              </Form.Item>
            </Col>
            <Col>
              <Form.Item
                {...MODAL_FORM_ITEM_LAYOUT}
                label={intl
                  .get('hiam.roleManagement.model.fieldPermission.fieldType')
                  .d('字段类型')}
              >
                {form.getFieldDecorator('fieldType', {
                  initialValue: record.fieldType,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl
                          .get('hiam.roleManagement.model.fieldPermission.fieldType')
                          .d('字段类型'),
                      }),
                    },
                  ],
                })(
                  <Select disabled>
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
                label={intl.get('hiam.roleManagement.model.fieldPermission.rule').d('权限规则')}
              >
                {form.getFieldDecorator('permissionType', {
                  initialValue: record.permissionType,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl
                          .get('hiam.roleManagement.model.fieldPermission.rule')
                          .d('权限规则'),
                      }),
                    },
                  ],
                })(
                  <Select>
                    {permissionRule.map(item => (
                      <Select.Option key={item.value} value={item.value}>
                        {item.meaning}
                      </Select.Option>
                    ))}
                  </Select>
                )}
              </Form.Item>
            </Col>
            {form.getFieldValue('permissionType') === 'DESENSITIZE' && (
              <Col>
                <Form.Item
                  {...MODAL_FORM_ITEM_LAYOUT}
                  label={intl
                    .get('hiam.roleManagement.model.fieldPermission.desensitize')
                    .d('脱敏规则')}
                >
                  {form.getFieldDecorator('permissionRule', {
                    initialValue: record.permissionRule,
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl
                            .get('hiam.roleManagement.model.fieldPermission.desensitize')
                            .d('脱敏规则'),
                        }),
                      },
                      {
                        max: 60,
                        message: intl.get('hzero.common.validation.max', { max: 60 }),
                      },
                    ],
                  })(<Input />)}
                </Form.Item>
              </Col>
            )}
          </Row>
        </Form>
      </Modal>
    );
  }
}
