/**
 * createDropField
 * @author WY yang.wang06@hand-china.com
 * @date 2019/1/4
 */
import React from 'react';
import { isFunction } from 'lodash';
import { DropTarget } from 'react-dnd';

import { DragType, emptyFieldType } from '../../config';

import styles from '../index.less';

/**
 * 创建 DropField
 *
 * @param {{
 *  dropAcceptTypes: string|string[],
 *  canDrop: (props: object, monitor: object) => boolean,
 *  drop: Function,
 * }} [{
 *   dropAcceptTypes = [DragType.dragField, DragType.drawDragField],
 *   canDrop = defaultCanDrop,
 *   drop = defaultDrop,
 * }={}]
 * @param {string|string[]} [dropAcceptTypes=[DragType.dragField, DragType.drawDragField]] - 接收的 drop 类型
 * @param {(props: object, monitor: object) => boolean} [canDrop=defaultCanDrop] - 是否可以 drop
 * @param {Function} [drop=defaultDrop] - drop 触发的方法
 */
export default function createDropField({
  dropAcceptTypes = [DragType.dragField, DragType.drawDragField],
  canDrop = defaultCanDrop,
  drop = defaultDrop,
} = {}) {
  const dropFieldSpec = {
    drop,
    canDrop,
  };

  const dropFieldCollect = (connect, monitor) => {
    const connectDropTarget = connect.dropTarget();
    const isOverAndCanDrop = monitor.isOver() && monitor.canDrop();
    return {
      connectDropTarget,
      isOverAndCanDrop,
    };
  };

  @DropTarget(dropAcceptTypes, dropFieldSpec, dropFieldCollect)
  class DropField extends React.Component {
    constructor(props) {
      super(props);

      this.handleFieldClick = this.handleFieldClick.bind(this);
    }

    handleFieldClick(e) {
      // 阻止冒泡
      e.stopPropagation();
      const { onActiveField, component, pComponent } = this.props;
      if (onActiveField) {
        onActiveField(pComponent, component);
      }
    }

    render() {
      const {
        connectDropTarget,
        children,
        isOverAndCanDrop,
        currentEditField,
        component,
      } = this.props;

      const isActiveField = currentEditField === component;

      const isEmptyField = component.componentType === emptyFieldType;
      const dropFieldClassName = [styles['drop-field']];

      if (isOverAndCanDrop) {
        dropFieldClassName.push(styles['drop-field-over']);
      }

      if (isEmptyField) {
        dropFieldClassName.push(styles['drop-field-placeholder']);
      }

      if (isActiveField) {
        dropFieldClassName.push(styles['drop-field-active']);
      }

      return (
        connectDropTarget &&
        connectDropTarget(
          <div className={dropFieldClassName.join(' ')} onClick={this.handleFieldClick}>
            {React.cloneElement(children, { isOverAndCanDrop })}
          </div>
        )
      );
    }
  }
  return DropField;
}

function defaultCanDrop(props, monitor) {
  const { pComponent: drawPComponent } = monitor.getItem();
  const { pComponent: dropPComponent, component } = props;
  const dragType = monitor.getItemType();

  if (dragType === DragType.dragField) {
    // 新增只能替换空的 field
    if (component.componentType === emptyFieldType) {
      return true;
    }
  } else if (dragType === DragType.drawDragField) {
    // 只能交换同一个 component 里面的 field
    if (drawPComponent === dropPComponent) {
      return true;
    }
  }
  return false;
}

function defaultDrop(props, monitor) {
  const {
    component: dragField,
    pComponent: drawPComponent,
    fieldOptions: dragFieldOptions,
  } = monitor.getItem();
  const { component: dropField, pComponent, fieldOptions: dropFieldOptions } = props;

  const dragType = monitor.getItemType();

  if (dragType === DragType.drawDragField) {
    // 交换位置, 已经由 canDrop 保证 只能是同一个 pComponent
    const { onSwapField } = props;
    if (drawPComponent === pComponent && isFunction(onSwapField)) {
      onSwapField(pComponent, dragField, dropField, dragFieldOptions, dropFieldOptions);
    }
  } else if (dragType === DragType.dragField) {
    const { onAddField } = props;
    if (isFunction(onAddField)) {
      onAddField(pComponent, dragField, dropField, dragFieldOptions, dropFieldOptions);
    }
  }
}
