/**
 * 创建 可 拖动的 字段
 * @date 2019/1/4
 * @author WY yang.wang06@hand-china.com
 * @copyright (c) 2019 Hand
 */

import React from 'react';
import { DragSource } from 'react-dnd';
import { DragType, emptyFieldType } from '../../config';
import createDropField from '../Drop/createDropField';

import styles from '../index.less';

export default function createDrawDragField({
  dragType = DragType.drawDragField,
  canDrag = defaultCanDrop,
  beginDrag = defaultBeginDrag,
  dropField = createDropField(),
} = {}) {
  const drawDragFieldSpec = {
    beginDrag,
    canDrag,
  };
  const drawDragFieldCollect = (connect, monitor) => {
    const connectDragSource = connect.dragSource();
    const isDragging = monitor.isDragging();
    return {
      connectDragSource,
      isDragging,
    };
  };
  const DropField = dropField;

  @DragSource(dragType, drawDragFieldSpec, drawDragFieldCollect)
  class DrawDragField extends React.Component {
    constructor(props) {
      super(props);
      this.state = {};
    }

    render() {
      const {
        // drag
        connectDragSource,
        isDragging,
        // addon options
        fieldOptions,
        currentEditField,
        component = {},
        pComponent,
        children,
        onSwapField,
        onAddField,
        onActiveField,
      } = this.props;

      const drawDragFieldClassName = [styles['draw-drag-field']];

      return isDragging ? (
        <div className={`${styles['draw-drag-field']} ${styles['draw-drag-field-dragging']}`}>
          <div style={{ visibility: 'hidden' }}>{children}</div>
        </div>
      ) : (
        connectDragSource &&
          connectDragSource(
            <div className={drawDragFieldClassName.join(' ')}>
              <DropField
                pComponent={pComponent}
                component={component}
                onSwapField={onSwapField}
                onAddField={onAddField}
                onActiveField={onActiveField}
                fieldOptions={fieldOptions}
                currentEditField={currentEditField}
              >
                {children}
              </DropField>
            </div>
          )
      );
    }
  }

  return DrawDragField;
}

function defaultCanDrop(props) {
  const { component } = props;
  return component.componentType !== emptyFieldType;
}

function defaultBeginDrag(props) {
  return {
    component: props.component,
    pComponent: props.pComponent,
    fieldOptions: props.fieldOptions,
  };
}
