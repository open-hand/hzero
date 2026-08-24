/**
 * FieldProp.js
 * @author WY
 * @date 2018-01-07
 * @email yang.wang06@hand-china.com
 */

import React from 'react';
import { Form, Input, Divider, InputNumber, Button } from 'hzero-ui';
import { forEach, isFunction, startsWith, set } from 'lodash';
import { Bind } from 'lodash-decorators';

import { modalBtnPrefix, subEventPrefix } from 'components/DynamicComponent/config';
import ValueList from 'components/ValueList';

import intl from 'utils/intl';

import { attributeNameProp, attributeTypeProp, attributeValueProp } from '../../config';
import ButtonEditModal from '../ScriptEdit/ButtonEditModal';

const inputComponentStyle = {
  width: '100%',
};

const TOOLBAR_BTN_PREFIX = '[btn]';
const actionStyle = {
  width: '100%',
  marginTop: 8,
  marginBottom: 8,
};

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
    const { field, form } = this.props;
    const propValues = {};
    forEach(field.config, prop => {
      propValues[prop.attributeName] = prop.value;
    });
    const btnConfigs = {};
    const btnProps = {
      btn: {},
    };
    forEach(field.config, prop => {
      if (startsWith(prop[attributeNameProp], TOOLBAR_BTN_PREFIX)) {
        // 设置 btn 的属性
        if (startsWith(prop[attributeNameProp], `${TOOLBAR_BTN_PREFIX}${modalBtnPrefix}`)) {
          btnConfigs[(prop[attributeNameProp] || '').substr(TOOLBAR_BTN_PREFIX.length)] =
            prop[attributeTypeProp];
          if (btnProps.btn.modalBtns) {
            btnProps.btn.modalBtns.push(prop);
          } else {
            btnProps.btn.modalBtns = [prop];
          }
        } else if (startsWith(prop[attributeNameProp], `${TOOLBAR_BTN_PREFIX}${subEventPrefix}`)) {
          btnConfigs[(prop[attributeNameProp] || '').substr(TOOLBAR_BTN_PREFIX.length)] =
            prop[attributeTypeProp];
          if (btnProps.btn.subEvents) {
            btnProps.btn.subEvents.push(prop);
          } else {
            btnProps.btn.subEvents = [prop];
          }
        } else {
          // 设置除了 modalBtn, subEvent 之外的属性类型
          set(btnProps, prop[attributeNameProp], prop[attributeValueProp]);
        }
      } else {
        propValues[prop[attributeNameProp]] = prop[attributeValueProp];
      }
    });

    return (
      <Form>
        {form.getFieldDecorator('btnConfigs', {
          initialValue: btnConfigs,
        })(<div />)}
        {form.getFieldDecorator('btnProps', {
          initialValue: btnProps.btn,
        })(<div />)}
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
                message: intl.get('hzero.common.validate.notNull', {
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

  renderFieldTypeButtonProps(propValues = {}) {
    const { form } = this.props;
    const { buttonEditVisible = false, buttonEditProps = {} } = this.state;
    return (
      <React.Fragment>
        <Form.Item key="type" label="类型">
          {form.getFieldDecorator('type', {
            initialValue: propValues.type,
          })(
            React.createElement(ValueList, {
              options: [
                { value: 'primary', meaning: '主要' },
                { value: 'dashed', meaning: '不重要' },
                { value: 'danger', meaning: '危险' },
              ],
              style: { width: '100%' },
            })
          )}
        </Form.Item>
        <Form.Item key="styleMarginRight" label="右间距">
          {form.getFieldDecorator('style.marginRight', {
            initialValue: propValues['style.marginRight'],
          })(<InputNumber style={inputComponentStyle} />)}
        </Form.Item>
        <Button
          key="action"
          onClick={() => {
            this.handleOpenButtonEditModal(propValues);
          }}
          style={actionStyle}
          type="primary"
        >
          执行动作
        </Button>
        {buttonEditVisible && (
          <ButtonEditModal
            key="actionModal"
            buttonEditProps={buttonEditProps}
            visible={buttonEditVisible}
            onOk={this.handleButtonEditModalOk}
            onCancel={this.handleButtonEditModalCancel}
          />
        )}
      </React.Fragment>
    );
  }

  @Bind()
  handleOpenButtonEditModal() {
    const { config = {}, form } = this.props;
    const btnProps = form.getFieldValue('btnProps');
    this.setState({
      buttonEditVisible: true,
      buttonEditProps: {
        propsValue: btnProps,
        scripts: config.scripts,
        components: config.fields,
      },
    });
  }

  @Bind()
  handleButtonEditModalOk({ validateData, attrConfig }) {
    const { form } = this.props;
    const btnConfigs = {};
    forEach(attrConfig, attr => {
      btnConfigs[attr[attributeNameProp]] = attr[attributeTypeProp];
    });
    form.setFieldsValue({
      btnProps: validateData,
      btnConfigs,
    });
    // 关闭模态框
    this.handleButtonEditModalCancel();
  }

  @Bind()
  handleButtonEditModalCancel() {
    this.setState({
      buttonEditVisible: false,
      buttonEditProps: {},
    });
  }
}

FieldProp.displayName = 'DynamicTabs(FieldProp)';

export default FieldProp;
