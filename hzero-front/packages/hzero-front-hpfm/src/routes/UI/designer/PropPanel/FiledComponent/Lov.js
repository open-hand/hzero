/**
 * FieldLov.js
 * @author WY
 * @date 2018/11/14
 * @email yang.wang06@hand-china.com
 */

import React from 'react';
import { Form, Checkbox, Input } from 'hzero-ui';
import { isBoolean, isString } from 'lodash';

// import intl from 'utils/intl';

import { attributeNameProp, attributeTypeProp, attributeValueProp } from '../../config';
import DataType from '../../DataType';

const booleanCheckProps = {
  checkedValue: true,
  unCheckedValue: false,
};

export default class FieldLov extends React.Component {
  render() {
    const { form, propValues = {} } = this.props;
    return (
      <React.Fragment>
        {/* code String 值集视图的编码 */}
        <Form.Item key="code" label="LOV编码">
          {form.getFieldDecorator('code', {
            initialValue: propValues.code,
          })(<Input />)}
        </Form.Item>
        {/* cascadeFlag Boolean 是否开启级联 */}
        <Form.Item className="ant-form-item-checkbox-cascade-parent" key="cascadeFlag">
          {form.getFieldDecorator('cascadeFlag', {
            initialValue: propValues.cascadeFlag,
          })(<Checkbox {...booleanCheckProps}>级联</Checkbox>)}
        </Form.Item>
        {/* cascadeFrom String 级联的父字段(表单中的编码) */}
        <Form.Item
          key="cascadeFrom"
          className="ant-form-item-checkbox-cascade-child"
          label="级联字段"
        >
          {form.getFieldDecorator('cascadeFrom', {
            initialValue: propValues.cascadeFrom,
            rules: [
              // { required: true, message: '级联字段不能为空' },
            ],
          })(<Input />)}
        </Form.Item>
        {/* cascadeField String 传递给接口的参数 */}
        <Form.Item key="cascadeField" label="级联映射参数">
          {form.getFieldDecorator('cascadeField', {
            initialValue: propValues.cascadeField,
            rules: [
              // { required: true, message: '级联映射参数' },
            ],
          })(<Input />)}
        </Form.Item>
        {/* textField String 编辑时显示字段(第一次编辑) */}
        <Form.Item key="textField" label="显示值字段">
          {form.getFieldDecorator('textField', {
            initialValue: propValues.textField,
            rules: [
              // { required: true, message: '级联映射参数' },
            ],
          })(<Input />)}
        </Form.Item>
      </React.Fragment>
    );
  }
}

export function getConfigOfPropValues(propValues, newConfig = []) {
  if (isString(propValues.code)) {
    newConfig.push({
      [attributeNameProp]: 'code',
      [attributeValueProp]: propValues.code,
      [attributeTypeProp]: DataType.String,
    });
  }
  if (isBoolean(propValues.cascadeFlag)) {
    newConfig.push({
      [attributeNameProp]: 'cascadeFlag',
      [attributeValueProp]: propValues.cascadeFlag,
      [attributeTypeProp]: DataType.Boolean,
    });
  }
  if (isString(propValues.cascadeFrom)) {
    newConfig.push({
      [attributeNameProp]: 'cascadeFrom',
      [attributeValueProp]: propValues.cascadeFrom,
      [attributeTypeProp]: DataType.String,
    });
  }
  if (isString(propValues.cascadeField)) {
    newConfig.push({
      [attributeNameProp]: 'cascadeField',
      [attributeValueProp]: propValues.cascadeField,
      [attributeTypeProp]: DataType.String,
    });
  }
  if (isString(propValues.textField)) {
    newConfig.push({
      [attributeNameProp]: 'textField',
      [attributeValueProp]: propValues.textField,
      [attributeTypeProp]: DataType.String,
    });
  }
  return newConfig;
}

export const lifeUpgrade = [];

FieldLov.getConfigOfPropValues = getConfigOfPropValues;

FieldLov.lifeUpgrade = lifeUpgrade;
