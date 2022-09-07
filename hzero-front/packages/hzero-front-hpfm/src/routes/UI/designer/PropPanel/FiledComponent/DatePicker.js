/**
 * FieldDataPicker.js
 * @author WY
 * @date 2018/11/14
 * @email yang.wang06@hand-china.com
 */

import React from 'react';
import { Form, Checkbox } from 'hzero-ui';
import { isBoolean } from 'lodash';

// import intl from 'utils/intl/index';

import { attributeNameProp, attributeTypeProp, attributeValueProp } from '../../config';
import DataType from '../../DataType';

export default class FieldDataPicker extends React.Component {
  render() {
    const { form, propValues = {} } = this.props;
    return (
      <React.Fragment>
        {/* showToday Boolean 显示今天按钮 */}
        <Form.Item>
          {form.getFieldDecorator('showToday', {
            initialValue: propValues.showToday,
          })(
            <Checkbox checkedValue unCheckedValue={false}>
              显示今天按钮
            </Checkbox>
          )}
        </Form.Item>
      </React.Fragment>
    );
  }
}

export function getConfigOfPropValues(propValues, newConfig = []) {
  if (isBoolean(propValues.showToday)) {
    newConfig.push({
      [attributeNameProp]: 'showToday',
      [attributeValueProp]: propValues.showToday,
      [attributeTypeProp]: DataType.Boolean,
    });
  }
  return newConfig;
}

export const lifeUpgrade = [];

FieldDataPicker.getConfigOfPropValues = getConfigOfPropValues;

FieldDataPicker.lifeUpgrade = lifeUpgrade;
