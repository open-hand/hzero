/**
 * FieldSwitch.js
 * @author WY
 * @date 2018/11/14
 * @email yang.wang06@hand-china.com
 */

import React from 'react';
import { Form, Input } from 'hzero-ui';
import { isString } from 'lodash';

// import intl from 'utils/intl';

import { attributeNameProp, attributeTypeProp, attributeValueProp } from '../../config';
import DataType from '../../DataType';

export default class FieldSwitch extends React.Component {
  render() {
    const { form, propValues = {} } = this.props;
    return (
      <React.Fragment>
        <Form.Item key="checkedChildren" label="开时显示值">
          {form.getFieldDecorator('checkedChildren', {
            initialValue: propValues.checkedChildren,
          })(<Input />)}
        </Form.Item>
        <Form.Item key="unCheckedChildren" label="关时显示值">
          {form.getFieldDecorator('unCheckedChildren', {
            initialValue: propValues.unCheckedChildren,
          })(<Input />)}
        </Form.Item>
      </React.Fragment>
    );
  }
}

export function getConfigOfPropValues(propValues, newConfig = []) {
  if (isString(propValues.checkedChildren)) {
    newConfig.push({
      [attributeNameProp]: 'checkedChildren',
      [attributeValueProp]: propValues.checkedChildren,
      [attributeTypeProp]: DataType.String,
    });
  }
  if (isString(propValues.unCheckedChildren)) {
    newConfig.push({
      [attributeNameProp]: 'unCheckedChildren',
      [attributeValueProp]: propValues.unCheckedChildren,
      [attributeTypeProp]: DataType.String,
    });
  }
  return newConfig;
}

export const lifeUpgrade = [];

FieldSwitch.getConfigOfPropValues = getConfigOfPropValues;

FieldSwitch.lifeUpgrade = lifeUpgrade;
