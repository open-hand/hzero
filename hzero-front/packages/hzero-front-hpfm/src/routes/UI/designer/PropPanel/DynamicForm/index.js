/**
 * index.js
 * @date 2018-10-02
 * @author WY yang.wang06@hand-china.com
 */

import React from 'react';
import { isFunction, isBoolean, forEach, isString, find } from 'lodash';

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

import fieldComponents from '../FiledComponent/index';

class DynamicForm extends React.Component {
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
          field.requiredFlag = allValues.requiredFlag;
          // enabledFlag 是用来字段禁用的
          field.enabledFlag = allValues.enabledFlag;
          if (isBoolean(allValues.labelDisplayFlag)) {
            newConfig.push({
              [attributeNameProp]: 'labelDisplayFlag',
              [attributeValueProp]: allValues.labelDisplayFlag,
              [attributeTypeProp]: DataType.Boolean,
            });
          }

          if (isString(allValues.description)) {
            newConfig.push({
              [attributeNameProp]: 'description',
              [attributeValueProp]: allValues.description,
              [attributeTypeProp]: DataType.String,
            });
          }
          if (isString(allValues.placeholder)) {
            newConfig.push({
              [attributeNameProp]: 'placeholder',
              [attributeValueProp]: allValues.placeholder,
              [attributeTypeProp]: DataType.String,
            });
          }
          if (isString(allValues.onChange)) {
            newConfig.push({
              [attributeNameProp]: 'onChange',
              [attributeValueProp]: allValues.onChange,
              [attributeTypeProp]: DataType.String,
            });
          }

          const FieldComponent = fieldComponents[field.componentType];
          if (FieldComponent) {
            FieldComponent.getConfigOfPropValues(allValues, newConfig);
          } else {
            const getConfigOfPropValuesFunc = `get${field.componentType}ConfigOfPropValues`;

            if (this[getConfigOfPropValuesFunc]) {
              this[getConfigOfPropValuesFunc](allValues, newConfig);
            }
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
          forEach(component.fields, (fArr, rowIndex) => {
            forEach(fArr, (f, colIndex) => {
              if (f === field) {
                fieldRefUpdate = true;
                component.fields[rowIndex][colIndex] = newField;
                return false;
              }
            });
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
          // todo enabledFlag 没有了
          // component.enabledFlag = allValues.enabledFlag;

          // DynamicForm's attributes;

          // 查询URL
          if (isString(allValues.queryUrl)) {
            newConfig.push({
              [attributeNameProp]: 'queryUrl',
              [attributeValueProp]: allValues.queryUrl,
              [attributeTypeProp]: DataType.String,
            });
          }
          // 提交URL
          if (isString(allValues.submitUrl)) {
            newConfig.push({
              [attributeNameProp]: 'submitUrl',
              [attributeValueProp]: allValues.submitUrl,
              [attributeTypeProp]: DataType.String,
            });
          }
          // // 提交事件
          // if (isString(allValues.submit)) {
          //   newConfig.push({
          //     [attributeNameProp]: 'submit',
          //     [attributeValueProp]: allValues.submit,
          //     [attributeTypeProp]: DataType.String,
          //   });
          // }
          // 数据主键
          if (isString(allValues.rowKey)) {
            newConfig.push({
              [attributeNameProp]: 'rowKey',
              [attributeValueProp]: allValues.rowKey,
              [attributeTypeProp]: DataType.String,
            });
          }
          // 是否可编辑
          if (isBoolean(allValues.editable)) {
            newConfig.push({
              [attributeNameProp]: 'editable',
              [attributeValueProp]: allValues.editable,
              [attributeTypeProp]: DataType.Boolean,
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
}

export default DynamicForm;
