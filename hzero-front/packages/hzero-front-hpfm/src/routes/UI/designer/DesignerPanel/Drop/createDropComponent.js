/**
 * DropComponent
 * @author WY yang.wang06@hand-china.com
 * @date 2018/10/10
 */
import React from 'react';
import { clone, isEmpty, forEach, isFunction } from 'lodash';
import { DropTarget } from 'react-dnd';

import { attributeNameProp, attributeValueProp, DragType } from '../../config';

import styles from '../index.less';

/**
 * 创建一个 DropComponent 包装组件
 * @param {{dropAcceptTypes: string | string[], drop: Function}} - 配置
 * @param {string|string[]} [dropAcceptTypes=[DragType.dragField, DragType.drawDragComponent]] - 接收 drop 的类型
 * @param {Function} drop - 触发drop后的回调方法
 */
export default function createDropComponent({
  dropAcceptTypes = [DragType.dragField, DragType.drawDragComponent],
  drop = defaultDrop,
} = {}) {
  const dropComponentSpec = {
    drop,
  };

  const dropComponentCollect = (connect, monitor) => {
    const connectDropTarget = connect.dropTarget();
    const isOver = monitor.isOver();
    return {
      connectDropTarget,
      isOver,
    };
  };

  // drop component only accepts dragField, dragComponent and drawDragComponent

  @DropTarget(dropAcceptTypes, dropComponentSpec, dropComponentCollect)
  class DropComponent extends React.Component {
    render() {
      const {
        component = {},
        connectDropTarget,
        currentEditComponent,
        currentEditField,
        children,
      } = this.props;
      const config = clone(component);
      const configProps = {};
      forEach(config.props, prop => {
        configProps[prop[attributeNameProp]] = prop[attributeValueProp];
      });
      delete config.props;
      Object.assign(config, configProps);

      const dropComponentClassNames = [styles['drop-component']];

      if (!currentEditField && currentEditComponent === component) {
        dropComponentClassNames.push(styles['drop-component-active']);
      }
      /**
       * notice
       * 字段布局 和 字段 之间包一层 DrawDrag 和 Drop
       */
      return (
        connectDropTarget &&
        connectDropTarget(
          <div className={dropComponentClassNames.join(' ')}>
            {isEmpty(component.fields) ? '拖入组件' : children}
          </div>
        )
      );
    }
  }
  return DropComponent;
}

function defaultDrop(props, monitor) {
  if (!monitor.didDrop()) {
    // 如果在子组件中已经 drop 了, 这边就不需要继续 drop 了
    const dragType = monitor.getItemType();
    if (dragType === DragType.dragField) {
      const { onAddField, component, currentEditComponent } = props;
      if (component === currentEditComponent) {
        if (isFunction(onAddField)) {
          onAddField(component, monitor.getItem().component);
        }
      }
    }
  }
}
