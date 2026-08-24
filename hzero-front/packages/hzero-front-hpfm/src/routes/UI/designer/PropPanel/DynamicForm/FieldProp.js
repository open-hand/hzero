/**
 * FieldProp.js
 * @date 2018-10-04
 * @author WY yang.wang06@hand-china.com
 */

import React from 'react';
import { Form, Input, Checkbox as HzeroCheckbox, Divider } from 'hzero-ui';
import { forEach, isFunction } from 'lodash';

import Checkbox from 'components/Checkbox';

import intl from 'utils/intl';

import { attributeNameProp, attributeValueProp, fieldLabelProp, fieldNameProp } from '../../config';

import fieldComponents from '../FiledComponent/index';

// 当 组件是 Checkbox 或者 Switch 时, requiredFlag 为 0, 且不能编辑
const requiredFlagNoNeedStyle = {
  display: 'none',
};

@Form.create({
  fieldNameProp: null,
  onValuesChange(props, changedValues, allValues) {
    if (isFunction(props.onValuesChange)) {
      props.onValuesChange(props, changedValues, allValues);
    }
  },
})
class FieldProp extends React.Component {
  // state = {
  // };
  //
  // constructor(props) {
  //   super(props);
  // }

  render() {
    const { field } = this.props;
    const propValues = {};
    forEach(field.config, prop => {
      propValues[prop[attributeNameProp]] = prop[attributeValueProp];
    });
    return (
      <Form>
        {this.renderFieldCommonProps(propValues)}
        <Divider />
        {this.renderFieldTypeProps(propValues)}
      </Form>
    );
  }

  renderFieldCommonProps(propValues) {
    const { form, field } = this.props;
    const isBooleanLikeValue =
      field.componentType === 'Checkbox' || field.componentType === 'Switch';
    const requiredFlagStyle = isBooleanLikeValue ? requiredFlagNoNeedStyle : undefined;
    const requiredFlag = isBooleanLikeValue ? 0 : field.requiredFlag === 1 ? 1 : 0;
    return (
      <React.Fragment>
        {/* enabledFlag 字段禁用 */}
        <Form.Item key="enabledFlag">
          {form.getFieldDecorator('enabledFlag', {
            initialValue: field.enabledFlag === 0 ? 0 : 1,
          })(
            <HzeroCheckbox checkedValue={0} unCheckedValue={1}>
              只读
            </HzeroCheckbox>
          )}
        </Form.Item>
        {/*
            requiredFlag TinyInt {1, 0} 必输
            当 组件是 Checkbox 或者 Switch 时 没有必输
          */}
        <Form.Item key="requiredFlag" style={requiredFlagStyle}>
          {form.getFieldDecorator('requiredFlag', {
            initialValue: requiredFlag,
          })(<Checkbox>必输</Checkbox>)}
        </Form.Item>
        {/* labelDisplayFlag String 显示标题 */}
        <Form.Item className="ant-form-item-checkbox-cascade-parent" key="labelDisplayFlag">
          {form.getFieldDecorator('labelDisplayFlag', {
            initialValue: propValues.labelDisplayFlag !== false,
            valuePropName: 'checked',
          })(<HzeroCheckbox>显示标题</HzeroCheckbox>)}
        </Form.Item>
        {/* fieldLabel 标题 */}
        <Form.Item className="ant-form-item-checkbox-cascade-child" key={fieldLabelProp}>
          {form.getFieldDecorator(fieldLabelProp, {
            initialValue: field[fieldLabelProp],
          })(<Input />)}
        </Form.Item>
        <Form.Item label="字段名" key={fieldNameProp}>
          {form.getFieldDecorator(fieldNameProp, {
            initialValue: field[fieldNameProp],
            rules: [
              {
                required: true,
                message: intl.get('hzero.common.validation.notNull', {
                  name: '字段名',
                }),
              },
            ],
          })(<Input />)}
        </Form.Item>
        {/* description 描述 */}
        <Form.Item label="描述" key="description">
          {form.getFieldDecorator('description', {
            initialValue: propValues.description,
          })(<Input />)}
        </Form.Item>
        <Form.Item label="为空提示" key="placeholder">
          {form.getFieldDecorator('placeholder', {
            initialValue: propValues.placeholder,
          })(<Input />)}
        </Form.Item>

        {/* 事件 */}
        {/* <Form.Item */}
        {/* label={intl.get('hpfm.ui.field.common.event.change').d('change事件')} */}
        {/* className="ant-form-item-without-help" */}
        {/* > */}
        {/* {form.getFieldDecorator('onChange', { */}
        {/* initialValue: propValues.onChange, */}
        {/* })(<FunctionSelect */}
        {/* components={config.fields} */}
        {/* scripts={config.scripts} */}
        {/* />)} */}
        {/* </Form.Item> */}
      </React.Fragment>
    );
  }

  renderFieldTypeProps(propValues = {}) {
    const { field, form } = this.props;
    const FieldComponent = fieldComponents[field.componentType];
    if (FieldComponent) {
      return <FieldComponent form={form} propValues={propValues} />;
    }
    return null;
  }
}

if (process.env.NODE_ENV === 'production') {
  FieldProp.displayName = 'DynamicForm(FieldProp)';
}

export default FieldProp;
