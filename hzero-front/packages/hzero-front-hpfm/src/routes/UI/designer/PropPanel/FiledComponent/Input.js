/**
 * Input.js
 * @author WY
 * @date 2018/11/14
 * @email yang.wang06@hand-china.com
 */

import React from 'react';
import { Form, Checkbox } from 'hzero-ui';
import { isBoolean, isString } from 'lodash';

import ValueList from 'components/ValueList/index';

// import intl from 'utils/intl';

import pageStyles from '../../index.less';
import { attributeNameProp, attributeTypeProp, attributeValueProp } from '../../config';
import DataType from '../../DataType';

const typeCaseOptions = [
  { value: '', meaning: '不转换' },
  { value: 'upper', meaning: '大写' },
  { value: 'lower', meaning: '小写' },
];

export default class FieldInput extends React.Component {
  render() {
    const { form, propValues = {} } = this.props;
    return (
      <React.Fragment>
        <Form.Item>
          {form.getFieldDecorator('inputChinese', {
            initialValue: isBoolean(propValues.inputChinese) ? propValues.inputChinese : true,
          })(
            <Checkbox checkedValue={false} unCheckedValue>
              不允许输入中文
            </Checkbox>
          )}
        </Form.Item>
        <Form.Item>
          {form.getFieldDecorator('trimAll', {
            initialValue: isBoolean(propValues.trimAll) ? propValues.trimAll : false,
          })(
            <Checkbox checkedValue unCheckedValue={false}>
              删除所有空格
            </Checkbox>
          )}
        </Form.Item>
        <Form.Item label="大小写转换">
          {form.getFieldDecorator('typeCase', {
            initialValue: propValues.typeCase,
          })(<ValueList options={typeCaseOptions} className={pageStyles['full-width']} />)}
        </Form.Item>
      </React.Fragment>
    );
  }
}

export function getConfigOfPropValues(propValues, newConfig = []) {
  if (isBoolean(propValues.inputChinese)) {
    newConfig.push({
      [attributeNameProp]: 'inputChinese',
      [attributeValueProp]: propValues.inputChinese,
      [attributeTypeProp]: DataType.String,
    });
  }
  if (isString(propValues.typeCase)) {
    newConfig.push({
      [attributeNameProp]: 'typeCase',
      [attributeValueProp]: propValues.typeCase,
      [attributeTypeProp]: DataType.String,
    });
  }
  if (isBoolean(propValues.trimAll)) {
    newConfig.push({
      [attributeNameProp]: 'trimAll',
      [attributeValueProp]: propValues.trimAll,
      [attributeTypeProp]: DataType.Boolean,
    });
  }
  return newConfig;
}

export const lifeUpgrade = [];

FieldInput.getConfigOfPropValues = getConfigOfPropValues;

FieldInput.lifeUpgrade = lifeUpgrade;
