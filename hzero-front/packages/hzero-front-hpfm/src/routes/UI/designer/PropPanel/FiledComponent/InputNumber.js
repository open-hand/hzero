/**
 * FieldInputNumber.js
 * @author WY
 * @date 2018/11/14
 * @email yang.wang06@hand-china.com
 */

import React from 'react';
import { Form, Checkbox, InputNumber } from 'hzero-ui';
import { isBoolean, isNumber } from 'lodash';

// import intl from 'utils/intl';

import { attributeNameProp, attributeTypeProp, attributeValueProp } from '../../config';
import DataType from '../../DataType';

const inputComponentStyle = {
  width: '100%',
};

export default class FieldInputNumber extends React.Component {
  render() {
    const { form, propValues = {} } = this.props;
    return (
      <React.Fragment>
        <Form.Item key="allowThousandth">
          {form.getFieldDecorator('allowThousandth', {
            initialValue: propValues.allowThousandth,
          })(<Checkbox>允许千分位</Checkbox>)}
        </Form.Item>
        <Form.Item key="precision" label="数值精度">
          {form.getFieldDecorator('precision', {
            initialValue: propValues.precision,
          })(<InputNumber min={0} precision={0} step={1} style={inputComponentStyle} />)}
        </Form.Item>
        <Form.Item key="min" label="最小值">
          {form.getFieldDecorator('min', {
            initialValue: propValues.min,
            rules: [
              // todo 最小值 必须小于 最大值
              { type: 'number' },
            ],
          })(<InputNumber style={inputComponentStyle} />)}
        </Form.Item>
        <Form.Item key="max" label="最大值">
          {form.getFieldDecorator('max', {
            initialValue: propValues.max,
            rules: [
              // 最大值 必须小于 最小值
              { type: 'number' },
            ],
          })(<InputNumber style={inputComponentStyle} />)}
        </Form.Item>
        <Form.Item key="step" label="每次改变步数，可以为小数">
          {form.getFieldDecorator('step', {
            initialValue: propValues.step,
          })(<InputNumber style={inputComponentStyle} />)}
        </Form.Item>
      </React.Fragment>
    );
  }
}

export function getConfigOfPropValues(propValues, newConfig = []) {
  if (isBoolean(propValues.allowThousandth)) {
    newConfig.push({
      [attributeNameProp]: 'allowThousandth',
      [attributeValueProp]: propValues.allowThousandth,
      [attributeTypeProp]: DataType.Boolean,
    });
  }
  if (isNumber(propValues.precision)) {
    newConfig.push({
      [attributeNameProp]: 'precision',
      [attributeValueProp]: propValues.precision,
      [attributeTypeProp]: DataType.Number,
    });
  }
  if (isNumber(propValues.max)) {
    newConfig.push({
      [attributeNameProp]: 'max',
      [attributeValueProp]: propValues.max,
      [attributeTypeProp]: DataType.Number,
    });
  }
  if (isNumber(propValues.min)) {
    newConfig.push({
      [attributeNameProp]: 'min',
      [attributeValueProp]: propValues.min,
      [attributeTypeProp]: DataType.Number,
    });
  }
  if (isNumber(propValues.step)) {
    newConfig.push({
      [attributeNameProp]: 'step',
      [attributeValueProp]: propValues.step,
      [attributeTypeProp]: DataType.Number,
    });
  }
  return newConfig;
}

export const lifeUpgrade = [];

FieldInputNumber.getConfigOfPropValues = getConfigOfPropValues;

FieldInputNumber.lifeUpgrade = lifeUpgrade;
