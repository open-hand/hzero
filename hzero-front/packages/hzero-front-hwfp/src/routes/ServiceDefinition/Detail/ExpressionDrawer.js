import React from 'react';
import { Form, Input, Modal, Select, Icon } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';

const FormItem = Form.Item;
const { Option } = Select;

const formLayout = {
  labelCol: { span: 5 },
  wrapperCol: { span: 17 },
};

@Form.create({ fieldNameProp: null })
export default class ExpressionDrawer extends React.Component {
  /**
   * 保存
   */
  @Bind()
  handleOK() {
    const { form, onOk } = this.props;
    form.validateFields((err, fieldsValue) => {
      if (!err) {
        onOk({
          ...fieldsValue,
          parameterSource: fieldsValue.parameterSource.key,
          parameterSourceMeaning: fieldsValue.parameterSource.label,
          rightParameterSource: fieldsValue.rightParameterSource.key,
          rightParameterSourceMeaning: fieldsValue.rightParameterSource.label,
        });
      }
    });
  }

  @Bind()
  handleSource(value = {}) {
    const { onChangeSource = (e) => e } = this.props;
    if (value.key === 'VARIABLE') {
      onChangeSource(value.key);
    }
  }

  @Bind()
  handleCancel() {
    const { onCancel = (e) => e } = this.props;
    onCancel();
  }

  @Bind()
  emitEmpty(event, field) {
    event.stopPropagation();
    event.preventDefault();
    this.props.form.setFieldsValue({ [field]: null });
  }

  render() {
    const {
      form,
      initData,
      modalVisible,
      paramSaving,
      serviceOperatorList = [],
      paramterSourceList = [],
      variableList = [],
    } = this.props;
    const { getFieldDecorator, getFieldValue } = form;
    const {
      parameterName,
      parameterSource,
      parameterSourceMeaning,
      defaultValue,
      parameterValue,
      operator,
      rightParameterSource,
      rightParameterSourceMeaning,
      rightParameterValue,
    } = initData;
    return (
      <Modal
        destroyOnClose
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        title={intl.get('hwfp.serviceDefinition.view.title.editParams').d('编辑参数')}
        visible={modalVisible}
        confirmLoading={paramSaving}
        onCancel={this.handleCancel}
        onOk={this.handleOK}
      >
        <Form>
          <FormItem
            {...formLayout}
            label={intl.get('hwfp.serviceDefinition.model.param.orderNumber').d('序号')}
          >
            {getFieldDecorator('parameterName', {
              initialValue: parameterName,
              rules: [
                {
                  required: !defaultValue,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hwfp.serviceDefinition.model.param.orderNumber').d('序号'),
                  }),
                },
              ],
            })(<Input />)}
          </FormItem>
          <FormItem
            {...formLayout}
            label={intl
              .get('hwfp.serviceDefinition.model.param.leftParameterSource')
              .d('左参数来源')}
          >
            {getFieldDecorator('parameterSource', {
              initialValue: { key: parameterSource, label: parameterSourceMeaning },
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl
                      .get('hwfp.serviceDefinition.model.param.leftParameterSource')
                      .d('左参数来源'),
                  }),
                },
              ],
            })(
              <Select labelInValue onChange={this.handleSource}>
                {paramterSourceList.map((item) => (
                  <Option key={item.value} value={item.value}>
                    {item.meaning}
                  </Option>
                ))}
              </Select>
            )}
          </FormItem>
          {form.getFieldValue('parameterSource').key === 'VARIABLE' ? (
            <FormItem
              {...formLayout}
              label={intl
                .get('hwfp.serviceDefinition.model.param.leftParameterValue')
                .d('左参数值')}
            >
              {getFieldDecorator('parameterValue', {
                initialValue: parameterValue,
                rules: [
                  {
                    required: !defaultValue,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hwfp.serviceDefinition.model.param.leftParameterValue')
                        .d('左参数值'),
                    }),
                  },
                ],
              })(
                <Select
                  showSearch
                  filterOption={(input, option) =>
                    option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
                  }
                >
                  {variableList.map((item) => (
                    <Option key={item.variableName} value={item.variableName}>
                      {item.description}
                    </Option>
                  ))}
                </Select>
              )}
            </FormItem>
          ) : (
            <FormItem
              {...formLayout}
              label={intl
                .get('hwfp.serviceDefinition.model.param.leftParameterValue')
                .d('左参数值')}
            >
              {getFieldDecorator('parameterValue', {
                initialValue: parameterValue,
                rules: [
                  {
                    required: !defaultValue,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hwfp.serviceDefinition.model.param.leftParameterValue')
                        .d('左参数值'),
                    }),
                  },
                ],
              })(<Input />)}
            </FormItem>
          )}
          <FormItem
            {...formLayout}
            label={intl.get('hwfp.serviceDefinition.model.param.operator').d('操作符')}
          >
            {getFieldDecorator('operator', {
              initialValue: operator,
            })(
              <Select allowClear>
                {serviceOperatorList.map((item) => (
                  <Option key={item.value} value={item.value}>
                    {item.meaning}
                  </Option>
                ))}
              </Select>
            )}
          </FormItem>
          <FormItem
            {...formLayout}
            label={intl
              .get('hwfp.serviceDefinition.model.param.rightParameterSource')
              .d('右参数来源')}
          >
            {getFieldDecorator('rightParameterSource', {
              initialValue: { key: rightParameterSource, label: rightParameterSourceMeaning },
            })(
              <Select allowClear labelInValue onChange={this.handleSource}>
                {paramterSourceList.map((item) => (
                  <Option key={item.value} value={item.value}>
                    {item.meaning}
                  </Option>
                ))}
              </Select>
            )}
          </FormItem>
          {form.getFieldValue('rightParameterSource').key === 'VARIABLE' ? (
            <FormItem
              {...formLayout}
              label={intl
                .get('hwfp.serviceDefinition.model.param.rightParameterValue')
                .d('右参数值')}
            >
              {getFieldDecorator('rightParameterValue', {
                initialValue: rightParameterValue,
              })(
                <Select
                  allowClear
                  showSearch
                  filterOption={(input, option) =>
                    option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
                  }
                >
                  {variableList.map((item) => (
                    <Option key={item.variableName} value={item.variableName}>
                      {item.description}
                    </Option>
                  ))}
                </Select>
              )}
            </FormItem>
          ) : (
            <FormItem
              {...formLayout}
              label={intl
                .get('hwfp.serviceDefinition.model.param.rightParameterValue')
                .d('右参数值')}
            >
              {getFieldDecorator('rightParameterValue', {
                initialValue: rightParameterValue,
              })(
                <Input
                  suffix={
                    getFieldValue('rightParameterValue') ? (
                      <Icon
                        key="clear-description"
                        style={{ cursor: 'pointer', color: 'rgba(0,0,0,.25)' }}
                        type="close-circle"
                        onClick={(e) => this.emitEmpty(e, 'rightParameterValue')}
                      />
                    ) : null
                  }
                />
              )}
            </FormItem>
          )}
        </Form>
      </Modal>
    );
  }
}
