/**
 * index.js
 * @author WY
 * @date 2018-10-02
 * @email yang.wang06@hand-china.com
 */

import React from 'react';
import { isFunction, forEach, isString, isNumber, find } from 'lodash';

import { modalBtnPrefix, subEventPrefix } from 'components/DynamicComponent/config';

import FieldProp from './FieldProp';
import ComponentProp from './ComponentProp';
import {
  attributeNameProp,
  attributeTypeProp,
  attributeValueProp,
  fieldLabelProp,
  fieldNameProp,
} from '../../config';
import DataType from '../../DataType';

const TOOLBAR_BTN_PREFIX = '[btn]';

class DynamicToolbar extends React.Component {
  constructor(props) {
    super(props);
    this.handleFieldValuesChange = this.handleFieldValuesChange.bind(this);
    this.handleComponentValuesChange = this.handleComponentValuesChange.bind(this);

    // 拿到 form 的ref
    this.editFormRef = React.createRef();
  }

  render() {
    const { field, component } = this.props;
    if (field) {
      // edit field
      return (
        <FieldProp
          {...this.props}
          onValuesChange={this.handleFieldValuesChange}
          ref={this.editFormRef}
        />
      );
    } else if (component) {
      // edit component
      return (
        <ComponentProp
          {...this.props}
          onValuesChange={this.handleComponentValuesChange}
          ref={this.editFormRef}
        />
      );
    } else {
      // no edit component or field
      return null;
    }
  }

  /**
   * deal field's attribute change
   */
  handleFieldValuesChange(props, changeValues, allValues) {
    // 使用 allValues 重写 field 的属性
    const { field, component, onRefresh } = this.props;
    const { validateFields } = this.editFormRef.current || {};
    if (validateFields) {
      validateFields(err => {
        if (!err) {
          const newConfig = [];
          field[fieldLabelProp] = allValues[fieldLabelProp];
          field[fieldNameProp] = allValues[fieldNameProp];
          // fields's common prop;
          // // 不会编辑的 属性
          // field.requiredFlag = allValues.requiredFlag;
          // // todo 字段的 visibleFlag 不使用了
          // field.visiableFlag = allValues.visiableFlag;
          // // todo 字段的 enabledFlag 不能编辑了
          // field.enabledFlag = allValues.enabledFlag;
          const prevFieldConfigs = field.config;

          const getConfigOfPropValuesFunc = `get${field.componentType}ConfigOfPropValues`;

          if (this[getConfigOfPropValuesFunc]) {
            this[getConfigOfPropValuesFunc](allValues, newConfig, field);
          }

          field.config = newConfig.map(fieldConfig => {
            const prevFieldConfig = find(
              prevFieldConfigs,
              prevC => prevC.attributeName === fieldConfig.attributeName
            );
            return { ...prevFieldConfig, ...fieldConfig };
          });
          const newField = field; // { ...field };
          // 更新 field 在 component 中的引用
          forEach(component.fields, (f, index) => {
            if (f === field) {
              component.fields[index] = newField;
              return false;
            }
          });

          if (isFunction(onRefresh)) {
            onRefresh();
          }
        }
      });
    }
  }

  /**
   * deal component attribute change
   */
  handleComponentValuesChange(props, changeValues, allValues) {
    // 使用 allValues 重写 component 的属性
    // use allValues override component's props
    const { component, onRefresh } = this.props;
    const { validateFields } = this.editFormRef.current || {};
    if (validateFields) {
      validateFields(err => {
        if (!err) {
          const newConfig = [];
          // common component's prop;
          component.templateCode = allValues.templateCode;
          component.description = allValues.description;
          component.enabledFlag = allValues.enabledFlag;
          // DynamicToolbar's attributes;
          if (isNumber(allValues.style.marginBottom)) {
            newConfig.push({
              [attributeNameProp]: 'style.marginBottom',
              [attributeValueProp]: allValues.style.marginBottom,
              [attributeTypeProp]: DataType.Number,
            });
          }
          const prevComponentConfigs = component.config;
          component.config = newConfig.map(componentConfig => {
            const prevComponentConfig = find(
              prevComponentConfigs,
              prevC => prevC[attributeNameProp] === componentConfig[attributeNameProp]
            );
            return { ...prevComponentConfig, ...componentConfig };
          });
          if (isFunction(onRefresh)) {
            onRefresh();
          }
        }
      });
    }
  }

  // field props

  /**
   * deal Button
   */
  getButtonConfigOfPropValues(propValues, newConfig = [], field) {
    if (isString(propValues.type)) {
      newConfig.push({
        [attributeNameProp]: 'type',
        [attributeValueProp]: propValues.type,
        [attributeTypeProp]: DataType.String,
      });
      // warn will remove when save
      // eslint-disable-next-line no-param-reassign
      field.type = propValues.type;
    }
    if (isString(propValues.onClick)) {
      newConfig.push({
        [attributeNameProp]: 'onClick',
        [attributeValueProp]: propValues.onClick,
        [attributeTypeProp]: DataType.String,
      });
    }
    if (isNumber(propValues.style.marginRight)) {
      newConfig.push({
        [attributeNameProp]: 'style.marginRight',
        [attributeValueProp]: propValues.style.marginRight,
        [attributeTypeProp]: DataType.Number,
      });
      // warn will remove when save
      // eslint-disable-next-line no-param-reassign
      field.style = {
        marginRight: propValues.style.marginRight,
      };
    }
    const { btnProps, btnConfigs } = propValues;
    forEach(btnProps, (btnPropValue, btnPropKey) => {
      if (btnPropKey === 'modalBtns') {
        for (let i = 0; i < btnPropValue.length; i += 1) {
          newConfig.push({
            [attributeNameProp]: `${TOOLBAR_BTN_PREFIX}${modalBtnPrefix}[${i}]`,
            [attributeValueProp]: btnPropValue[i][attributeValueProp],
            [attributeTypeProp]: DataType.String,
          });
        }
      } else if (btnPropKey === 'subEvents') {
        for (let i = 0; i < btnPropValue.length; i += 1) {
          newConfig.push({
            [attributeNameProp]: `${TOOLBAR_BTN_PREFIX}${subEventPrefix}[${i}]`,
            [attributeValueProp]: btnPropValue[i][attributeValueProp],
            [attributeTypeProp]: DataType.String,
          });
        }
      } else {
        newConfig.push({
          [attributeNameProp]: `${TOOLBAR_BTN_PREFIX}[${btnPropKey}]`,
          [attributeValueProp]: btnPropValue,
          [attributeTypeProp]: btnConfigs[btnPropKey],
        });
      }
    });
  }

  // field props
}

export default DynamicToolbar;
