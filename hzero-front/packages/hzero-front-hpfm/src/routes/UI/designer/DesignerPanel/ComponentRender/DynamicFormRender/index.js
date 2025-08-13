/**
 * index
 * @author WY yang.wang06@hand-china.com
 * @date 2018/10/10
 */

import React from 'react';
import { map, isFunction, cloneDeep, isNumber, slice, concat } from 'lodash';
import { Bind } from 'lodash-decorators';

import Row from './Row';

import styles from '../../index.less';
import DrawDragField from '../../Drag/DrawDragField';
import { emptyField, emptyFieldType, fieldLabelProp, fieldNameProp } from '../../../config';
import DrawDragComponent from '../../Drag/DrawDragComponent';

export default class DynamicFormRender extends React.Component {
  constructor(props) {
    super(props);
    this.wrapperFieldComponent = this.wrapperFieldComponent.bind(this);
    this.handleSwapField = this.handleSwapField.bind(this);
    this.handleRemoveField = this.handleRemoveField.bind(this);
    this.handleAddField = this.handleAddField.bind(this);
    this.handleUpdateComponent = this.handleUpdateComponent.bind(this);

    this.state = {
      willRemovePositionX: -1,
    };
    this.handleUpdateWillPositionX = this.handleUpdateWillPositionX.bind(this);
  }

  render() {
    const {
      component = {},
      fieldLabelWidth,
      currentEditField,
      currentEditComponent,
      onActiveComponent,
    } = this.props;
    const { willRemovePositionX } = this.state;
    const rowCount = component.fields.length;
    let lineNumber = 0;

    const rows = map(component.fields, (field, rowIndex) => {
      lineNumber += 1;
      return (
        <Row
          key={lineNumber}
          wrapperFieldComponent={this.wrapperFieldComponent}
          fieldLabelWidth={fieldLabelWidth}
          rowIndex={rowIndex}
          positionY={rowIndex}
          component={component}
          onRemoveField={this.handleRemoveField}
          cols={field}
          rowCount={rowCount}
          currentEditField={currentEditField}
          onUpdateComponent={this.handleUpdateComponent}
          // 删除的位置
          willRemovePositionX={willRemovePositionX}
          onUpdateWillRemovePositionX={this.handleUpdateWillPositionX}
          onRefreshTemplate={this.handleRefresh}
        />
      );
    });
    return (
      <div className={styles['dynamic-form']}>
        <DrawDragComponent
          component={component}
          onActiveComponent={onActiveComponent}
          currentEditComponent={currentEditComponent}
          currentEditField={currentEditField}
        >
          {rows}
        </DrawDragComponent>
      </div>
    );
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

  handleSwapField(component, dragField, dropField, dragFieldOptions, dropFieldOptions) {
    // 保证数据一定正确
    const tempField = component.fields[dragFieldOptions.rowIndex][dragFieldOptions.colIndex];
    const tempFieldColspan = tempField.colspan;
    // 交换 colspan
    // eslint-disable-next-line
    component.fields[dragFieldOptions.rowIndex][dragFieldOptions.colIndex].colspan =
      component.fields[dropFieldOptions.rowIndex][dropFieldOptions.colIndex].colspan;
    // eslint-disable-next-line
    component.fields[dropFieldOptions.rowIndex][
      dropFieldOptions.colIndex
    ].colspan = tempFieldColspan;

    // 交换 field
    // eslint-disable-next-line
    component.fields[dragFieldOptions.rowIndex][dragFieldOptions.colIndex] =
      component.fields[dropFieldOptions.rowIndex][dropFieldOptions.colIndex];
    // eslint-disable-next-line
    component.fields[dropFieldOptions.rowIndex][dropFieldOptions.colIndex] = tempField;
    this.handleRefresh();
  }

  /**
   * todo 要将 site 字段放到 removeSiteFields 中
   * @param {object} component
   * @param {object} field
   * @param {object} fieldOptions
   */
  handleRemoveField(component, field, fieldOptions) {
    // 保证数据一定正确
    // TODO 跨行的处理
    if (field.colspan && isNumber(field.colspan)) {
      const fillFields = [];
      for (let index = 0; index < field.colspan; index += 1) {
        fillFields.push(cloneDeep(emptyField));
      }
      // eslint-disable-next-line
      component.fields[fieldOptions.rowIndex] = concat(
        [],
        slice(component.fields[fieldOptions.rowIndex], 0, fieldOptions.colIndex),
        fillFields,
        slice(component.fields[fieldOptions.rowIndex], fieldOptions.colIndex + 1)
      );
    } else {
      // eslint-disable-next-line
      component.fields[fieldOptions.rowIndex][fieldOptions.colIndex] = cloneDeep(emptyField);
    }
    // // todo 租户级代码
    // if(field.siteFlag === 1) {
    //   // 平台级 需要放在 removeSiteFields 中
    //   // eslint-disable-next-line no-param-reassign
    //   field.visiableFlag = 0;
    //   component.removeSiteFields.push(field);
    // }
    this.handleRefresh();
  }

  handleAddField(component, dragField, dropField, dragFieldOptions, dropFieldOptions) {
    const { onUpdateComponent } = this.props;
    if (dropField.componentType !== emptyFieldType) {
      // TODO 只能在 空 的地方添加新的字段, 非空的也进不来, 因为有 canDrop
      return;
    }
    const newField = cloneDeep(dragField);
    const fieldProps = [];
    // 字段本身的属性

    newField.config = fieldProps;
    newField[fieldLabelProp] = newField.name;
    newField[fieldNameProp] = 'defaultCode';

    // 删除 field 的其他属性
    delete newField.className;
    delete newField.type;
    delete newField.defaultProps;
    delete newField.name;
    delete newField.props;

    // 字段在容器中的属性
    switch (component.templateType) {
      case 'DynamicForm':
        // 处理字段在表单中的属性
        break;
      default:
        break;
    }
    // eslint-disable-next-line
    component.fields[dropFieldOptions.rowIndex][dropFieldOptions.colIndex] = newField;
    // TODO 更新 PageDesigner 中的数据
    if (isFunction(onUpdateComponent)) {
      onUpdateComponent(component);
    }
    // TODO remove next line after debug complete
    this.forceUpdate();
  }

  handleUpdateWillPositionX(willRemovePositionX = -1) {
    this.setState({ willRemovePositionX });
  }

  handleUpdateComponent(/* component */) {
    // TODO 更新 component;
    this.forceUpdate();
  }

  @Bind()
  handleRefresh() {
    this.forceUpdate();
  }
}
