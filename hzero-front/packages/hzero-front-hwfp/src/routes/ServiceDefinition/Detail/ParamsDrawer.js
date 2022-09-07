import React from 'react';
import { Form, Input, Modal, Select } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import { MODAL_FORM_ITEM_LAYOUT } from 'utils/constants';

const FormItem = Form.Item;
const { Option } = Select;

@Form.create({ fieldNameProp: null })
export default class ParamsDrawer extends React.Component {
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
        });
      }
    });
  }

  @Bind()
  handleSource(value = {}) {
    const { onChangeSource = e => e } = this.props;
    if (value.key === 'VARIABLE') {
      onChangeSource(value.key);
    }
  }

  @Bind()
  handleCancel() {
    const { onCancel = e => e } = this.props;
    onCancel();
  }

  render() {
    const {
      form,
      initData,
      modalVisible,
      paramSaving,
      paramterSourceList = [],
      variableList = [],
    } = this.props;
    const { getFieldDecorator } = form;
    const {
      parameterName,
      parameterSource,
      parameterSourceMeaning,
      defaultValue,
      interfaceParameterType,
      parameterValue,
      description,
      interfaceParameterTypeMeaning,
    } = initData;
    // 隐藏域
    getFieldDecorator('interfaceParameterType', {
      initialValue: interfaceParameterType,
    });
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
            {...MODAL_FORM_ITEM_LAYOUT}
            label={intl.get('hwfp.serviceDefinition.model.param.parameterName').d('参数名称')}
          >
            {getFieldDecorator('parameterName', {
              initialValue: parameterName,
            })(<Input disabled />)}
          </FormItem>
          <FormItem
            {...MODAL_FORM_ITEM_LAYOUT}
            label={intl.get('hwfp.serviceDefinition.model.param.parameterType').d('参数类型')}
          >
            {getFieldDecorator('interfaceParameterTypeMeaning', {
              initialValue: interfaceParameterTypeMeaning,
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl
                      .get('hwfp.serviceDefinition.model.param.parameterType')
                      .d('参数类型'),
                  }),
                },
              ],
            })(<Input disabled />)}
          </FormItem>
          <FormItem
            {...MODAL_FORM_ITEM_LAYOUT}
            label={intl.get('hwfp.serviceDefinition.model.param.defaultValue').d('默认值')}
          >
            {getFieldDecorator('defaultValue', {
              initialValue: defaultValue,
            })(<Input disabled />)}
          </FormItem>
          <FormItem
            {...MODAL_FORM_ITEM_LAYOUT}
            label={intl.get('hwfp.serviceDefinition.model.service.parameterSource').d('参数来源')}
          >
            {getFieldDecorator('parameterSource', {
              initialValue: { key: parameterSource, label: parameterSourceMeaning },
              rules: [
                {
                  required: !defaultValue,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl
                      .get('hwfp.serviceDefinition.model.service.parameterSource')
                      .d('参数来源'),
                  }),
                },
              ],
            })(
              <Select labelInValue onChange={this.handleSource}>
                {paramterSourceList.map(item => (
                  <Option key={item.value} value={item.value}>
                    {item.meaning}
                  </Option>
                ))}
              </Select>
            )}
          </FormItem>
          {form.getFieldValue('parameterSource').key === 'VARIABLE' ? (
            <FormItem
              {...MODAL_FORM_ITEM_LAYOUT}
              label={intl.get('hwfp.serviceDefinition.model.param.parameterValue').d('参数值')}
            >
              {getFieldDecorator('parameterValue', {
                initialValue: parameterValue,
                rules: [
                  {
                    required: !defaultValue,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hwfp.serviceDefinition.model.param.parameterValue')
                        .d('参数值'),
                    }),
                  },
                ],
              })(
                <Select
                  showSearch
                  filterOption={(input, option) =>
                    option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0}
                >
                  {variableList.map(item => (
                    <Option key={item.variableName} value={item.variableName}>
                      {item.description}
                    </Option>
                  ))}
                </Select>
              )}
            </FormItem>
          ) : (
            <FormItem
              {...MODAL_FORM_ITEM_LAYOUT}
              label={intl.get('hwfp.serviceDefinition.model.param.parameterValue').d('参数值')}
            >
              {getFieldDecorator('parameterValue', {
                initialValue: parameterValue,
                rules: [
                  {
                    required: !defaultValue,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hwfp.serviceDefinition.model.param.parameterValue')
                        .d('参数值'),
                    }),
                  },
                ],
              })(<Input />)}
            </FormItem>
          )}
          <FormItem
            {...MODAL_FORM_ITEM_LAYOUT}
            label={intl.get('hwfp.serviceDefinition.model.param.description').d('参数描述')}
          >
            {getFieldDecorator('description', {
              initialValue: description,
            })(<Input disabled />)}
          </FormItem>
        </Form>
      </Modal>
    );
  }
}
