/**
 * ComponentProp.js
 * @date 2018-10-04
 * @author WY yang.wang06@hand-china.com
 */

import React from 'react';
import { Form, Input, Checkbox as HzeroCheckbox, Divider } from 'hzero-ui';
import { forEach, isFunction } from 'lodash';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';

import { attributeNameProp, attributeValueProp } from '../../config';

// const buttonComponentStyle = {
//   width: '100%',
// };

@Form.create({
  fieldNameProp: null,
  onValuesChange(props, changedValues, allValues) {
    if (isFunction(props.onValuesChange)) {
      props.onValuesChange(props, changedValues, allValues);
    }
  },
})
class ComponentProp extends React.Component {
  constructor(props) {
    super(props);
    this.filterDynamicFormComponentScript = this.filterDynamicFormComponentScript.bind(this);
  }

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
        {this.renderComponentDynamicFormProps(propValues)}
      </Form>
    );
  }

  renderComponentCommonProps() {
    const { form, component } = this.props;
    return (
      <React.Fragment>
        <Form.Item label={intl.get('hpfm.ui.model.template.code').d('编码')}>
          {form.getFieldDecorator('templateCode', {
            initialValue: component.templateCode,
            rules: [
              {
                required: true,
                message: intl.get('hzero.common.validation.notNull', {
                  name: intl.get('hpfm.ui.model.template.code').d('编码'),
                }),
              },
            ],
          })(<Input disabled />)}
        </Form.Item>
        <Form.Item label={intl.get('hpfm.ui.model.common.description').d('名称')}>
          {form.getFieldDecorator('description', {
            initialValue: component.description,
            rules: [
              {
                required: true,
                message: intl.get('hzero.common.validation.notNull', {
                  name: intl.get('hpfm.ui.model.common.description').d('名称'),
                }),
              },
            ],
          })(<Input />)}
        </Form.Item>
        {/* 租户是否可编辑,没有用到了 */}
        {/* <Form.Item className="ant-form-item-without-help"> */}
        {/* {form.getFieldDecorator('enabledFlag', { */}
        {/* initialValue: component.enabledFlag === 0 ? 0 : 1, */}
        {/* valuePropName: 'checked', */}
        {/* })(<Checkbox>{intl.get('hpfm.ui.component.enabledFlag').d('允许编辑')}</Checkbox>)} */}
        {/* </Form.Item> */}
      </React.Fragment>
    );
  }

  renderComponentDynamicFormProps(propValues) {
    const { form } = this.props;
    return (
      <React.Fragment>
        <Form.Item>
          {form.getFieldDecorator('editable', {
            initialValue: propValues.editable,
          })(
            <HzeroCheckbox checkedValue={false} unCheckedValue>
              只读模式
            </HzeroCheckbox>
          )}
        </Form.Item>
        <Form.Item label={intl.get('hpfm.ui.component.dynamicForm.rowKey').d('主键字段')}>
          {form.getFieldDecorator('rowKey', {
            initialValue: propValues.rowKey,
          })(<Input />)}
        </Form.Item>
        <Form.Item label={intl.get('hpfm.ui.model.common.queryUrl').d('加载数据URL')}>
          {form.getFieldDecorator('queryUrl', {
            initialValue: propValues.queryUrl,
          })(<Input />)}
        </Form.Item>
        <Form.Item label={intl.get('hpfm.ui.model.common.submitUrl').d('提交数据URL')}>
          {form.getFieldDecorator('submitUrl', {
            initialValue: propValues.submitUrl,
          })(<Input />)}
        </Form.Item>
        {/* <Form.Item className="ant-form-item-without-help"> */}
        {/* {form.getFieldDecorator('none', { */}
        {/* initialValue: propValues.none, */}
        {/* })( */}
        {/* <Button */}
        {/* type="primary" */}
        {/* style={buttonComponentStyle} */}
        {/* > */}
        {/* 定义隐藏域 */}
        {/* </Button>, */}
        {/* )} */}
        {/* </Form.Item> */}
        {/* 事件 */}
      </React.Fragment>
    );
  }

  @Bind()
  filterDynamicFormComponentScript(component, funcName) {
    const { component: c } = this.props;
    if (c === component && funcName === 'submit') {
      return false;
    }
  }
}

if (process.env.NODE_ENV === 'production') {
  ComponentProp.displayName = 'DynamicForm(ComponentProp)';
}

export default ComponentProp;
