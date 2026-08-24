/**
 * TextArea.js
 * @author WY
 * @date 2018/11/14
 * @email yang.wang06@hand-china.com
 */

import React from 'react';
import { Form, InputNumber } from 'hzero-ui';
import { isNumber } from 'lodash';

// import intl from 'utils/intl';

import { attributeNameProp, attributeTypeProp, attributeValueProp } from '../../config';
import DataType from '../../DataType';

const inputComponentStyle = {
  width: '100%',
};

export default class FieldTextArea extends React.Component {
  render() {
    const { form, propValues = {} } = this.props;
    return (
      <React.Fragment>
        <Form.Item key="autoSizeMinRows" label="最小行数">
          {form.getFieldDecorator('autosize.minRows', {
            initialValue: propValues['autosize.minRows'],
            rules: [
              { type: 'number' },
              // TODO 最小行数 必须小于 最大行数
            ],
          })(<InputNumber style={inputComponentStyle} min={0} precision={0} step={1} />)}
        </Form.Item>
        <Form.Item key="autoSizeMaxRows" label="最大行数">
          {form.getFieldDecorator('autosize.maxRows', {
            initialValue: propValues['autosize.maxRows'],
            rules: [
              { type: 'number' },
              // TODO 最大行数 必须大于 最小行数
            ],
          })(<InputNumber style={inputComponentStyle} min={0} precision={0} step={1} />)}
        </Form.Item>
      </React.Fragment>
    );
  }
}

export function getConfigOfPropValues(propValues, newConfig = []) {
  if (isNumber(propValues.autosize.minRows)) {
    newConfig.push({
      [attributeNameProp]: 'autosize.minRows',
      [attributeValueProp]: propValues.autosize.minRows,
      [attributeTypeProp]: DataType.Number,
    });
  }
  if (isNumber(propValues.autosize.maxRows)) {
    newConfig.push({
      [attributeNameProp]: 'autosize.maxRows',
      [attributeValueProp]: propValues.autosize.maxRows,
      [attributeTypeProp]: DataType.Number,
    });
  }
  return newConfig;
}

export const lifeUpgrade = [];

FieldTextArea.getConfigOfPropValues = getConfigOfPropValues;

FieldTextArea.lifeUpgrade = lifeUpgrade;
