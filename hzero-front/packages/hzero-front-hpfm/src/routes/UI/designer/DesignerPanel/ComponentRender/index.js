/**
 * index.js - render component
 * @author WY
 * @date 2018/10/9
 * @email yang.wang06@hand-china.com
 */

import React from 'react';

import DrawDragField from '../Drag/DrawDragField';

import EmptyRender from './EmptyRender';
import DynamicFormRender from './DynamicFormRender';
import DynamicToolbarRender from './DynamicToolbarRender';
import DynamicTableRender from './DynamicTableRender';
import DynamicTabsRender from './DynamicTabsRender';

export default class ComponentRender extends React.Component {
  constructor(props) {
    super(props);
    this.wrapperFieldComponent = this.wrapperFieldComponent.bind(this);
  }

  render() {
    const {
      component,
      config,
      onActiveField,
      onAddField,
      onSwapField,
      onRemoveField,
      onActiveComponent,
      currentEditComponent,
      currentEditField,
      // 拿到 Render 的this
      refRender,
    } = this.props;

    let Render = EmptyRender;

    switch (component.templateType) {
      case 'DynamicForm':
        Render = DynamicFormRender;
        break;
      case 'DynamicToolbar':
        Render = DynamicToolbarRender;
        break;
      case 'DynamicTable':
        Render = DynamicTableRender;
        break;
      case 'DynamicTabs':
        Render = DynamicTabsRender;
        break;
      default:
        break;
    }
    return (
      <Render
        {...config}
        component={component}
        onActiveField={onActiveField}
        onSwapField={onSwapField}
        onAddField={onAddField}
        onRemoveField={onRemoveField}
        currentEditField={currentEditField}
        currentEditComponent={currentEditComponent}
        wrapperFieldComponent={this.wrapperFieldComponent}
        onActiveComponent={onActiveComponent}
        refRender={refRender}
      />
    );
  }

  wrapperFieldComponent(fieldElement, field) {
    const { component, onSwapField, onAddField, onActiveField, currentEditField } = this.props;
    return (
      <DrawDragField
        pComponent={component}
        component={field}
        onSwapField={onSwapField}
        onAddField={onAddField}
        onActiveField={onActiveField}
        currentEditField={currentEditField}
      >
        {fieldElement}
      </DrawDragField>
    );
  }
}
