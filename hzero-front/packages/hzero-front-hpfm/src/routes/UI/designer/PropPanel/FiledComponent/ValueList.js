/**
 * FieldValueList.js
 * @date 2018/11/14
 * @author WY yang.wang06@hand-china.com
 * @copyright (c) 2018 Hand
 */

import React from 'react';
import { Form, Input, Checkbox } from 'hzero-ui';
import { isBoolean, isString } from 'lodash';

// import intl from 'utils/intl';

import { attributeNameProp, attributeTypeProp, attributeValueProp } from '../../config';
import DataType from '../../DataType';

const booleanCheckProps = {
  checkedValue: true,
  unCheckedValue: false,
};

export default class FieldValueList extends React.Component {
  render() {
    const { form, propValues = {} } = this.props;
    return (
      <React.Fragment>
        {/* lazyLoad Boolean 是否开启懒加载 */}
        <Form.Item key="lazyLoad">
          {form.getFieldDecorator('lazyLoad', {
            initialValue: propValues.lazyLoad !== false,
          })(<Checkbox {...booleanCheckProps}>懒加载</Checkbox>)}
        </Form.Item>
        {/* lovCode String 值集的编码 */}
        <Form.Item key="lovCode" label="值集编码">
          {form.getFieldDecorator('lovCode', {
            initialValue: propValues.lovCode,
          })(React.createElement(Input, {}, undefined))}
        </Form.Item>
        {/* queryUrl String 直接调用查询的接口 */}
        <Form.Item key="queryUrl" label="加载URL">
          {form.getFieldDecorator('queryUrl', {
            initialValue: propValues.queryUrl,
          })(React.createElement(Input))}
        </Form.Item>
        {/* valueField String 值字段 */}
        <Form.Item key="valueField" label="值字段">
          {form.getFieldDecorator('valueField', {
            initialValue: propValues.valueField,
          })(<Input />)}
        </Form.Item>
        {/* displayField String 显示字段 */}
        <Form.Item key="displayField" label="显示字段">
          {form.getFieldDecorator('displayField', {
            initialValue: propValues.displayField,
          })(<Input />)}
        </Form.Item>
        {/* textField String 用于后台返回数据时的显示值(还没有加载数据) */}
        <Form.Item key="textField" label="显示值字段">
          {form.getFieldDecorator('textField', {
            initialValue: propValues.textField,
          })(React.createElement(Input))}
        </Form.Item>
        {/* cascadeFlag Boolean 是否级联 */}
        <Form.Item key="cascadeFlag" className="ant-form-item-checkbox-cascade-parent">
          {form.getFieldDecorator('cascadeFlag', {
            initialValue: propValues.cascadeFlag,
          })(<Checkbox {...booleanCheckProps}>级联</Checkbox>)}
        </Form.Item>
        {/* CascadeFrom 级联父字段 */}
        <Form.Item
          key="cascadeFrom"
          label="级联字段"
          className="ant-form-item-checkbox-cascade-child"
        >
          {form.getFieldDecorator('cascadeFrom', {
            initialValue: propValues.cascadeFrom,
            rules: [
              // { required: true, message: '级联字段不能为空' },
            ],
          })(<Input />)}
        </Form.Item>
        {/* cascadeField String 级联传给接口的参数 */}
        <Form.Item key="cascadeField" label="级联映射参数">
          {form.getFieldDecorator('cascadeField', {
            initialValue: propValues.cascadeField,
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
  if (isString(propValues.lovCode)) {
    newConfig.push({
      [attributeNameProp]: 'lovCode',
      [attributeValueProp]: propValues.lovCode,
      [attributeTypeProp]: DataType.String,
    });
  }
  if (isString(propValues.queryUrl)) {
    newConfig.push({
      [attributeNameProp]: 'queryUrl',
      [attributeValueProp]: propValues.queryUrl,
      [attributeTypeProp]: DataType.String,
    });
  }
  if (isBoolean(propValues.lazyLoad)) {
    newConfig.push({
      [attributeNameProp]: 'lazyLoad',
      [attributeValueProp]: propValues.lazyLoad,
      [attributeTypeProp]: DataType.Boolean,
    });
  }
  if (isString(propValues.valueField)) {
    newConfig.push({
      [attributeNameProp]: 'valueField',
      [attributeValueProp]: propValues.valueField,
      [attributeTypeProp]: DataType.String,
    });
  }
  if (isString(propValues.displayField)) {
    newConfig.push({
      [attributeNameProp]: 'displayField',
      [attributeValueProp]: propValues.displayField,
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
  return newConfig;
}

export const lifeUpgrade = [];

FieldValueList.getConfigOfPropValues = getConfigOfPropValues;

FieldValueList.lifeUpgrade = lifeUpgrade;
