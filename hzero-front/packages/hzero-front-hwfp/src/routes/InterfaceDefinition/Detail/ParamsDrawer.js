import React from 'react';
import { Form, Input, Modal, Select, Spin, Icon } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import { CODE } from 'utils/regExp';

const FormItem = Form.Item;
const { Option } = Select;

const formLayout = {
  labelCol: { span: 5 },
  wrapperCol: { span: 17 },
};

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
        onOk(fieldsValue);
      }
    });
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
      title,
      modalVisible,
      paramSaving,
      initLoading = false,
      paramTypeList = [],
    } = this.props;
    const { getFieldDecorator, getFieldValue } = form;
    const { parameterName, parameterType, defaultValue, description } = initData;
    return (
      <Modal
        destroyOnClose
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        title={title}
        visible={modalVisible}
        confirmLoading={paramSaving}
        onCancel={this.handleCancel}
        onOk={this.handleOK}
      >
        <Spin spinning={initLoading}>
          <Form>
            <FormItem
              {...formLayout}
              label={intl.get('hwfp.interfaceDefinition.model.param.parameterName').d('参数名称')}
            >
              {getFieldDecorator('parameterName', {
                initialValue: parameterName,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hwfp.interfaceDefinition.model.param.parameterName')
                        .d('参数名称'),
                    }),
                  },
                  {
                    pattern: CODE,
                    message: intl
                      .get('hzero.common.validation.code')
                      .d('大小写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                  },
                  {
                    max: 60,
                    message: intl.get('hzero.common.validation.max', {
                      max: 60,
                    }),
                  },
                ],
              })(<Input trim inputChinese={false} />)}
            </FormItem>
            <FormItem
              {...formLayout}
              label={intl.get('hwfp.interfaceDefinition.model.param.parameterType').d('参数类型')}
            >
              {getFieldDecorator('parameterType', {
                initialValue: parameterType,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hwfp.interfaceDefinition.model.param.parameterType')
                        .d('参数类型'),
                    }),
                  },
                ],
              })(
                <Select>
                  {paramTypeList.map((item) => (
                    <Option key={item.value} value={item.value}>
                      {item.meaning}
                    </Option>
                  ))}
                </Select>
              )}
            </FormItem>
            <FormItem
              {...formLayout}
              label={intl.get('hwfp.interfaceDefinition.model.param.defaultValue').d('默认值')}
            >
              {getFieldDecorator('defaultValue', {
                initialValue: defaultValue,
                rules: [
                  {
                    max: 60,
                    message: intl.get('hzero.common.validation.max', {
                      max: 60,
                    }),
                  },
                ],
              })(
                <Input
                  onClick={() => {}}
                  suffix={
                    getFieldValue('defaultValue') ? (
                      <Icon
                        key="clear-defaultValue"
                        style={{ cursor: 'pointer', color: 'rgba(0,0,0,.25)' }}
                        type="close-circle"
                        onClick={(e) => this.emitEmpty(e, 'defaultValue')}
                      />
                    ) : null
                  }
                />
              )}
            </FormItem>
            <FormItem
              {...formLayout}
              label={intl.get('hwfp.interfaceDefinition.model.param.description').d('参数描述')}
            >
              {getFieldDecorator('description', {
                initialValue: description,
                rules: [
                  {
                    max: 240,
                    message: intl.get('hzero.common.validation.max', {
                      max: 240,
                    }),
                  },
                ],
              })(
                <Input
                  suffix={
                    getFieldValue('description') ? (
                      <Icon
                        key="clear-description"
                        style={{ cursor: 'pointer', color: 'rgba(0,0,0,.25)' }}
                        type="close-circle"
                        onClick={(e) => this.emitEmpty(e, 'description')}
                      />
                    ) : null
                  }
                />
              )}
            </FormItem>
          </Form>
        </Spin>
      </Modal>
    );
  }
}
