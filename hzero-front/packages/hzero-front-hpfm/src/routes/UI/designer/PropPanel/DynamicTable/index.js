/**
 * index.js
 * @author WY
 * @date 2018-10-03
 * @email yang.wang06@hand-china.com
 */

import React from 'react';
import { isFunction, forEach, isString, has, find } from 'lodash';

import FieldProp from './FieldProp';
import ComponentProp from './ComponentProp';
import {
  attributeNameProp,
  attributeTypeProp,
  attributeValueProp,
  autoSizeWidth,
  fieldLabelProp,
  fieldNameProp,
} from '../../config';
import DataType from '../../DataType';

class DynamicTable extends React.Component {
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
          // 不要 visiableFlag 字段了
          // field.visiableFlag = allValues.visiableFlag;
          // todo 字段的 enabledFlag 不能编辑了
          // field.enabledFlag = allValues.enabledFlag;
          field.description = allValues.description;
          field.align = allValues.align;

          if (has(changeValues, 'autoSize')) {
            // autoSize 有更改才改变 width
            if (allValues.autoSize) {
              field.width = 0; // can set field width in right only when autoSize is 0
            } else {
              field.width = autoSizeWidth;
            }
          }

          const getConfigOfPropValuesFunc = `get${field.componentType}ConfigOfPropValues`;

          if (this[getConfigOfPropValuesFunc]) {
            this[getConfigOfPropValuesFunc](allValues, newConfig);
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

          // DynamicTable's attributes;
          if (isString(allValues.queryUrl)) {
            newConfig.push({
              [attributeNameProp]: 'queryUrl',
              [attributeValueProp]: allValues.queryUrl,
              [attributeTypeProp]: DataType.String,
            });
          }
          if (isString(allValues.removeUrl)) {
            newConfig.push({
              [attributeTypeProp]: DataType.String,
              [attributeValueProp]: allValues.removeUrl,
              [attributeNameProp]: 'removeUrl',
            });
          }
          if (isString(allValues.batchRemoveUrl)) {
            newConfig.push({
              [attributeNameProp]: 'batchRemoveUrl',
              [attributeValueProp]: allValues.batchRemoveUrl,
              [attributeTypeProp]: DataType.String,
            });
          }
          if (isString(allValues.queryForm)) {
            newConfig.push({
              [attributeNameProp]: 'queryForm',
              [attributeValueProp]: allValues.queryForm,
              [attributeTypeProp]: DataType.String,
            });
          }
          // rowKey
          newConfig.push({
            [attributeNameProp]: 'rowKey',
            [attributeTypeProp]: DataType.String,
            [attributeValueProp]: allValues.rowKey,
          });
          // warn pagination
          if (allValues.pagination) {
            newConfig.push({
              [attributeTypeProp]: DataType.Boolean,
              [attributeNameProp]: 'pagination',
              [attributeValueProp]: true,
            });
            const defaultPageSize = allValues.defaultPageSize || 10;
            newConfig.push({
              [attributeTypeProp]: DataType.Number,
              [attributeNameProp]: 'defaultPageSize',
              [attributeValueProp]: defaultPageSize,
            });
            component.pagination = true;
            component.defaultPageSize = defaultPageSize;
          } else {
            newConfig.push({
              [attributeTypeProp]: DataType.Boolean,
              [attributeNameProp]: 'pagination',
              [attributeValueProp]: false,
            });
            component.pagination = false;
            delete component.defaultPageSize;
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
   * deal LinkButton
   */
  getLinkButtonConfigOfPropValues(propValues, newConfig = []) {
    if (isString(propValues.modalRef)) {
      newConfig.push({
        [attributeNameProp]: 'modalRef',
        [attributeValueProp]: propValues.modalRef,
        [attributeTypeProp]: DataType.String,
      });
    }
    if (isString(propValues.paramMap)) {
      newConfig.push({
        [attributeNameProp]: 'paramMap',
        [attributeValueProp]: propValues.paramMap,
        [attributeTypeProp]: DataType.String,
      });
    }
  }

  /**
   * deal ColumnField
   */
  // getColumnFieldConfigOfPropValues(propValues, newConfig = []) {
  // }

  // field props
}

export default DynamicTable;
