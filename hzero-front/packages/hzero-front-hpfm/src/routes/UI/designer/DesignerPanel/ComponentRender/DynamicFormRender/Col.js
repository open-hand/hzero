/**
 * Col
 * @author WY yang.wang06@hand-china.com
 * @date 2018/10/10
 */
import React from 'react';

import { isNumber } from 'lodash';

import { Input, InputNumber, DatePicker, Col as HzeroCol } from 'hzero-ui';

import Lov from 'components/Lov';
import ValueList from 'components/ValueList';
import Switch from 'components/Switch';
import Checkbox from 'components/Checkbox';

import { emptyFieldType, fieldLabelProp } from '../../../config';
import CellControl from './CellControl';

import styles from '../../index.less';

const componentType = {
  Lov,
  ValueList,
  Switch,
  Checkbox,
  Input,
  InputNumber,
  TextArea: Input.TextArea,
  DatePicker,
};

function getFieldProps(component) {
  const fieldProps = {};
  if (component.enabledFlag !== 1) {
    fieldProps.disabled = true;
  }
  switch (component.componentType) {
    case 'DatePicker':
      fieldProps.placeholder = '';
      break;
    default:
      break;
  }
  return fieldProps;
}

export default class Col extends React.Component {
  render() {
    const { fieldLabelWidth = 120 } = this.props;
    const {
      component,
      wrapperFieldComponent,
      pComponent,
      onRemoveField,
      rowIndex,
      colIndex,
      rowCount,
      colCount,
      currentEditField,
      onUpdateComponent,
      willRemovePositionY,
      willRemovePositionX,
      positionY,
      positionX,
      positionRightX,
      onUpdateWillRemovePositionY,
      onUpdateWillRemovePositionX,
      onRefreshTemplate,
    } = this.props;

    const FieldType = componentType[component.componentType] || Input;

    const { fields = [] } = pComponent;
    const menuItemProps = {
      canInsertRight: true,
      canInsertBottom: true,
    };
    // 可以删除列,行
    if (colCount > 1) {
      menuItemProps.canRemoveCol = true;
    }
    if (rowCount > 1) {
      menuItemProps.canRemoveRow = true;
    }
    // 合并
    if (
      fields[rowIndex] &&
      fields[rowIndex][colIndex + 1] &&
      fields[rowIndex][colIndex + 1].componentType === emptyFieldType
    ) {
      menuItemProps.canMergeRight = true;
    }
    // 取消合并
    if (component.colspan && isNumber(component.colspan)) {
      menuItemProps.canCancelMergeRight = true;
    }
    const fieldOptions = { rowIndex, colIndex };

    // 字段是不是必输
    const isRequired = component.requiredFlag === 1;

    return (
      <HzeroCol {...getColLayout(colCount, component.colspan)}>
        {wrapperFieldComponent(
          <CellControl
            component={component}
            pComponent={pComponent}
            menuItemProps={menuItemProps}
            onRemoveField={onRemoveField}
            currentEditField={currentEditField}
            fieldOptions={fieldOptions}
            colCount={colCount}
            rowIndex={rowIndex}
            colIndex={colIndex}
            onUpdateComponent={onUpdateComponent}
            willRemovePositionY={willRemovePositionY}
            willRemovePositionX={willRemovePositionX}
            positionY={positionY}
            positionX={positionX}
            positionRightX={positionRightX}
            onUpdateWillRemovePositionX={onUpdateWillRemovePositionX}
            onUpdateWillRemovePositionY={onUpdateWillRemovePositionY}
            onRefresh={onRefreshTemplate}
          >
            {component.componentType === emptyFieldType ? (
              <div
                className={`${styles['dynamic-form-item']} ${
                  styles['dynamic-form-item-placeholder']
                }`}
              />
            ) : (
              <div className={styles['dynamic-form-item']}>
                <div
                  className={`${styles['dynamic-form-item-label']} ${
                    isRequired ? styles.required : ''
                  }`}
                  style={{
                    width: fieldLabelWidth,
                    minWidth: fieldLabelWidth,
                    maxWidth: fieldLabelWidth,
                  }}
                >
                  {component[fieldLabelProp]}:&nbsp;
                </div>
                <div className={styles['dynamic-form-item-control']}>
                  {React.createElement(FieldType, getFieldProps(component))}
                </div>
              </div>
            )}
          </CellControl>,
          component,
          fieldOptions
        )}
      </HzeroCol>
    );
  }
}

function getColLayout(colCount, colspan = 1) {
  return {
    span: Math.floor((24 * (colspan || 1)) / colCount),
  };
}

if (process.env.NODE_ENV !== 'production') {
  Col.displayName = 'DynamicForm(Col)';
}
