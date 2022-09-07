/**
 * ComponentProp.js
 * @author WY
 * @date 2018-10-02
 * @email yang.wang06@hand-china.com
 */

import React from 'react';
import { Divider, Form, Input /* , Checkbox as HzeroCheckbox */, InputNumber } from 'hzero-ui';
import { forEach, isFunction } from 'lodash';

import intl from 'utils/intl';

import { attributeNameProp, attributeValueProp } from '../../config';

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
      propValues[prop[attributeNameProp]] = prop[attributeValueProp];
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
        <Form.Item key="templateCode" label={intl.get('hpfm.ui.model.template.code').d('编码')}>
          {form.getFieldDecorator('templateCode', {
            initialValue: component.templateCode,
            rules: [
              {
                required: true,
                message: intl.get('hzero.common.validate.notNull', {
                  name: intl.get('hpfm.ui.model.template.code').d('编码'),
                }),
              },
            ],
          })(<Input disabled />)}
        </Form.Item>
        <Form.Item
          key="description"
          label={intl.get('hpfm.ui.model.template.description').d('名称')}
        >
          {form.getFieldDecorator('description', {
            initialValue: component.description,
            rules: [
              {
                required: true,
                message: intl.get('hzero.common.validate.notNull', {
                  name: intl.get('hpfm.ui.model.template.description').d('名称'),
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
        <Form.Item
          key="styleMarginBottom"
          label={intl.get('hpfm.ui.model.style.marginBottom').d('下间距')}
        >
          {form.getFieldDecorator('style.marginBottom', {
            initialValue: propValues['style.marginBottom'],
          })(<InputNumber style={inputComponentStyle} />)}
        </Form.Item>
      </React.Fragment>
    );
  }
}

if (process.env.NODE_ENV === 'production') {
  ComponentProp.displayName = 'DynamicToolbar(ComponentProp)';
}

export default ComponentProp;
