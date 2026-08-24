/**
 * 创建 可 拖动的 字段
 * @date 2019/1/4
 * @author WY yang.wang06@hand-china.com
 * @copyright (c) 2019 Hand
 */

import React from 'react';
import { isFunction } from 'lodash';
import { DragSource } from 'react-dnd';
//
import { DragType } from '../../config';
import createDropComponent from '../Drop/createDropComponent';

import styles from '../index.less';

export default function createDrawDragComponent({
  componentCanDrag = false,
  dropComponent = createDropComponent(),
  ...dragProps
} = {}) {
  const DropComponent = dropComponent;
  class DrawDragComponent extends React.Component {
    constructor(props) {
      super(props);
      this.state = {};

      this.handleRemoveComponent = this.handleRemoveComponent.bind(this);
      this.handleDrawComponentClick = this.handleDrawComponentClick.bind(this);
    }

    handleDrawComponentClick(e) {
      // 阻止冒泡
      e.stopPropagation();
      const { onActiveComponent, component } = this.props;
      if (isFunction(onActiveComponent)) {
        onActiveComponent(component);
      }
    }

    render() {
      const {
        component = {},
        // connectDragSource,
        onAddField,
        onActiveField,
        onSwapField,
        onRemoveField,
        children,
        currentEditComponent,
        currentEditField,
      } = this.props;
      return (
        <div className={styles['draw-drag-component']} onClick={this.handleDrawComponentClick}>
          <span
            className={styles['draw-drag-component-remove']}
            onClick={this.handleRemoveComponent}
          />
          <DropComponent
            component={component}
            onAddField={onAddField}
            onActiveField={onActiveField}
            onSwapField={onSwapField}
            onRemoveField={onRemoveField}
            currentEditComponent={currentEditComponent}
            currentEditField={currentEditField}
          >
            {children}
          </DropComponent>
        </div>
      );
    }

    handleRemoveComponent(e) {
      // 阻止冒泡
      e.stopPropagation();
      const { component, onRemoveComponent } = this.props;
      if (isFunction(onRemoveComponent)) {
        onRemoveComponent(component);
      }
    }
  }
  if (componentCanDrag) {
    const {
      drawType = DragType.drawDragComponent,
      beginDrag = defaultBeginDrag,
      canDrag = defaultCanDrop,
    } = dragProps;
    const drawDragComponentSpec = {
      beginDrag,
      canDrag,
    };

    const drawDragComponentCollect = (connect, monitor) => {
      const connectDragSource = connect.dragSource();
      const connectDragPreview = connect.dragPreview();
      const isDragging = monitor.isDragging();
      return {
        connectDragSource,
        connectDragPreview,
        isDragging,
      };
    };
    return DragSource(drawType, drawDragComponentSpec, drawDragComponentCollect)(DrawDragComponent);
  }
  return DrawDragComponent;
}

function defaultBeginDrag(props) {
  return {
    component: props.component,
  };
}
function defaultCanDrop() {
  return false;
}
