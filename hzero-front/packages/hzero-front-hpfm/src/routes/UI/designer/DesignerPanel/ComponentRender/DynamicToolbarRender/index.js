/**
 * index.js
 * @author WY
 * @date 2018/10/9
 * @email yang.wang06@hand-china.com
 */

import React from 'react';
import { cloneDeep, concat, isFunction, map, slice } from 'lodash';

import Col from './Col';

import styles from '../../index.less';
import DrawDragField from '../../Drag/DrawDragField';
import { emptyFieldType, fieldNameProp } from '../../../config';
import DrawDragComponent from '../../Drag/DrawDragComponent';

export default class DynamicToolbarRender extends React.Component {
  state = {};

  constructor(props) {
    super(props);
    this.wrapperFieldComponent = this.wrapperFieldComponent.bind(this);
    this.handleAddField = this.handleAddField.bind(this);
    this.handleRemoveField = this.handleRemoveField.bind(this);
    this.handleSwapField = this.handleSwapField.bind(this);
  }

  render() {
    const {
      component,
      currentEditComponent,
      currentEditField,
      onAddField,
      onActiveField,
      onSwapField,
      onRemoveField,
      onActiveComponent,
    } = this.props;
    return (
      <div className={styles['dynamic-toolbar']}>
        <DrawDragComponent
          component={component}
          onAddField={onAddField}
          onRemoveField={onRemoveField}
          onActiveComponent={onActiveComponent}
          currentEditComponent={currentEditComponent}
          currentEditField={currentEditField}
        >
          {map(component.fields, (field, index) => {
            return (
              <Col
                wrapperFieldComponent={this.wrapperFieldComponent}
                key={index}
                pComponent={component}
                currentEditField={currentEditField}
                component={field}
                colOptions={{ index }}
                onRemoveField={this.handleRemoveField}
                onActiveField={onActiveField}
                onSwapField={onSwapField}
              />
            );
          })}
        </DrawDragComponent>
      </div>
    );
  }

  handleAddField(component, dragField, dropField, dragFieldOptions, dropFieldOptions) {
    const { onUpdateComponent, onActiveField } = this.props;
    if (dropField.componentType !== emptyFieldType) {
      // TODO 只能在 空 的地方添加新的字段, 非空的也进不来, 因为有 canDrop
      return;
    }
    const newField = cloneDeep(dragField);
    // 字段本身的属性

    newField[fieldNameProp] = 'defaultCode';

    // 删除 field 的其他属性
    delete newField.className;
    delete newField.name;

    // eslint-disable-next-line
    component.fields[dropFieldOptions.index] = newField;

    // TODO 更新 PageDesigner 中的数据
    if (isFunction(onUpdateComponent)) {
      onUpdateComponent(component);
    }
    if (isFunction(onActiveField)) {
      onActiveField(component, newField);
    }
  }

  handleSwapField(component, dragField, dropField, dragFieldOptions, dropFieldOptions) {
    const { onUpdateComponent, onActiveField } = this.props;

    // eslint-disable-next-line no-param-reassign
    component.fields[dragFieldOptions.index] = dropField;
    // eslint-disable-next-line no-param-reassign
    component.fields[dropFieldOptions.index] = dragField;

    // 保证数据一定正确
    // TODO 更新 PageDesigner 中的数据
    if (isFunction(onUpdateComponent)) {
      onUpdateComponent(component);
    }

    if (isFunction(onActiveField)) {
      onActiveField(component, dragField);
    }
  }

  handleRemoveField(component, field, fieldOptions) {
    const { onUpdateComponent, onActiveComponent } = this.props;
    // 保证数据一定正确

    // eslint-disable-next-line no-param-reassign
    component.fields = concat(
      [],
      slice(component.fields, 0, fieldOptions.index),
      slice(component.fields, fieldOptions.index + 1)
    );

    // TODO 更新 PageDesigner 中的数据
    if (isFunction(onUpdateComponent)) {
      onUpdateComponent(component);
    }
    if (isFunction(onActiveComponent)) {
      onActiveComponent(component);
    }
  }

  wrapperFieldComponent(fieldElement, field, fieldOptions) {
    const { component, onActiveField, currentEditField } = this.props;
    return (
      <DrawDragField
        pComponent={component}
        component={field}
        onSwapField={this.handleSwapField}
        onRemoveField={this.handleRemoveField}
        onAddField={this.handleAddField}
        onActiveField={onActiveField}
        fieldOptions={fieldOptions}
        currentEditField={currentEditField}
      >
        {fieldElement}
      </DrawDragField>
    );
  }
}
