/**
 * FieldProp.js
 * @author WY
 * @date 2019-01-04
 * @email yang.wang06@hand-china.com
 */

import React from 'react';
import { Form, Input, Divider, Checkbox } from 'hzero-ui';
import { forEach, isFunction } from 'lodash';
// import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';

@Form.create({
  fieldNameProp: null,
  onValuesChange(props, changedValues, allValues) {
    if (isFunction(props.onValuesChange)) {
      props.onValuesChange(props, changedValues, allValues);
    }
  },
})
class FieldProp extends React.Component {
  state = {}; // 防止第一次 state 是空报错

  render() {
    const { field } = this.props;
    const propValues = {};
    forEach(field.config, prop => {
      propValues[prop.attributeName] = prop.value;
    });
    return (
      <Form>
        {this.renderFieldCommonProps(propValues)}
        <Divider />
        {this.renderFieldTypeProps(propValues)}
      </Form>
    );
  }

  renderFieldCommonProps() {
    const { form, field } = this.props;
    return (
      <React.Fragment>
        <Form.Item key="fieldName" label="编码">
          {form.getFieldDecorator('fieldName', {
            initialValue: field.fieldName,
            rules: [
              {
                required: true,
                message: intl.get('hzero.common.validation.notNull', {
                  name: intl.get('hpfm.ui.field.fieldName').d('编码'),
                }),
              },
            ],
          })(<Input disabled />)}
        </Form.Item>
        <Form.Item key="fieldLabel" label="标题">
          {form.getFieldDecorator('fieldLabel', {
            initialValue: field.fieldLabel,
          })(<Input />)}
        </Form.Item>
      </React.Fragment>
    );
  }

  renderFieldTypeProps(propValues) {
    const { field } = this.props;
    const renderFunc = `renderFieldType${field.componentType}Props`;
    if (isFunction(this[renderFunc])) {
      return this[renderFunc](propValues);
    }
    return null;
  }

  // render other fields

  renderFieldTypeTabPaneProps(propValues) {
    const { form } = this.props;
    return (
      <React.Fragment>
        <Form.Item>
          {form.getFieldDecorator('forceRender', {
            initialValue: propValues.forceRender,
          })(
            <Checkbox checkedValue unCheckedValue={false}>
              强制渲染
            </Checkbox>
          )}
        </Form.Item>
      </React.Fragment>
    );
  }

  // render other fields
}

FieldProp.displayName = 'DynamicTabs(FieldProp)';
export default FieldProp;
