/**
 * ComponentProp.js
 * @author WY
 * @date 2019-01-04
 * @email yang.wang06@hand-china.com
 */

import React from 'react';
import { Divider, Form, Input, InputNumber } from 'hzero-ui';
import { forEach, isFunction } from 'lodash';

import intl from 'utils/intl';

const inputComponentStyle = {
  width: '100%',
};

@Form.create({
  fieldNameProp: null,
  onValuesChange(props, changedValues, allValues) {
    if (isFunction(props.onValuesChange)) {
      props.onValuesChange(props, changedValues, allValues);
    }
  },
})
class ComponentProp extends React.Component {
  render() {
    const { component } = this.props;
    const propValues = {};
    forEach(component.config, prop => {
      propValues[prop.attributeName] = prop.value;
    });
    return (
      <Form>
        {this.renderComponentCommonProps(propValues)}
        <Divider />
        {this.renderComponentDynamicToolbarProps(propValues)}
      </Form>
    );
  }

  renderComponentCommonProps() {
    const { form, component } = this.props;
    return (
      <React.Fragment>
        <Form.Item key="templateCode" label="编码">
          {form.getFieldDecorator('templateCode', {
            initialValue: component.templateCode,
            rules: [
              {
                required: true,
                message: intl.get('hzero.common.validation.notNull', {
                  name: '编码',
                }),
              },
            ],
          })(<Input />)}
        </Form.Item>
        <Form.Item key="description" label="名称">
          {form.getFieldDecorator('description', {
            initialValue: component.description,
            rules: [
              {
                required: true,
                message: intl.get('hzero.common.validation.notNull', {
                  name: '名称',
                }),
              },
            ],
          })(<Input />)}
        </Form.Item>
      </React.Fragment>
    );
  }

  renderComponentDynamicToolbarProps(propValues) {
    const { form } = this.props;
    return (
      <React.Fragment>
        <Form.Item key="styleMarginBottom" label="下间距">
          {form.getFieldDecorator('style.marginBottom', {
            initialValue: propValues['style.marginBottom'],
          })(<InputNumber style={inputComponentStyle} />)}
        </Form.Item>
      </React.Fragment>
    );
  }
}

ComponentProp.displayName = 'DynamicTabs(ComponentProp)';
export default ComponentProp;
