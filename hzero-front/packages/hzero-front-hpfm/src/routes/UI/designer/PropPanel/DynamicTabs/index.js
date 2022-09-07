/**
 * @author WY
 * @date 2019-01-04
 * @email yang.wang06@hand-china.com
 */

import React from 'react';
import { isFunction, isBoolean, forEach, isNumber, find } from 'lodash';

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

class DynamicTabs extends React.Component {
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
          // field.requiredFlag = allValues.requiredFlag;
          // // todo 字段的 visibleFlag 不使用了
          // field.visiableFlag = allValues.visiableFlag;
          // // todo 字段的 enabledFlag 不能编辑了
          // field.enabledFlag = allValues.enabledFlag;
          field.description = allValues.description;

          const getConfigOfPropValuesFunc = `get${field.componentType}ConfigOfPropValues`;
          if (this[getConfigOfPropValuesFunc]) {
            this[getConfigOfPropValuesFunc](allValues, newConfig, field);
          }
          const prevFieldConfigs = field.config;
          field.config = newConfig.map(fieldConfig => {
            const prevFieldConfig = find(
              prevFieldConfigs,
              prevC => prevC[attributeNameProp] === fieldConfig[attributeNameProp]
            );
            return { ...prevFieldConfig, ...fieldConfig };
          });
          const newField = field; // { ...field };
          // 更新 feild 在 component 中的引用
          let fieldRefUpdate = false;
          forEach(component.fields, (f, index) => {
            if (f === field) {
              fieldRefUpdate = true;
              component.fields[index] = newField;
              return false;
            }
            return !fieldRefUpdate;
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
  getTabPaneConfigOfPropValues(allValues, newConfig) {
    if (isBoolean(allValues.forceRender)) {
      newConfig.push({
        attributeName: 'forceRender',
        attributeType: DataType.Boolean,
        value: allValues.forceRender,
      });
    }
  }
  // field props
}

export default DynamicTabs;
