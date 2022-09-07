/**
 * DropPage
 * @author WY yang.wang06@hand-china.com
 * @date 2018/10/10
 */
import React from 'react';
import { isFunction } from 'lodash';
import { DropTarget } from 'react-dnd';

import intl from 'utils/intl';

import { DropType, DragType } from '../../config';

import styles from '../index.less';

const dropPageSpec = {
  drop(props, monitor) {
    const { component: dragOrDrawDragComponent } = monitor.getItem();
    const dragType = monitor.getItemType();
    if (dragType === DragType.dragComponent) {
      const { onAddComponent } = props;
      if (isFunction(onAddComponent)) {
        onAddComponent(dragOrDrawDragComponent);
      }
    } else if (dragType === DragType.DrawDragComponent) {
      const { onMoveComponent } = props;
      if (isFunction(onMoveComponent)) {
        onMoveComponent(dragOrDrawDragComponent);
      }
    }
  },
};

const dropPageCollect = (connect, monitor) => {
  const connectDropTarget = connect.dropTarget();
  const isOver = monitor.isOver();
  const canDrop = monitor.canDrop();
  const draggingColor = monitor.getItemType();
  return {
    connectDropTarget,
    isOver,
    canDrop,
    draggingColor,
  };
};

@DropTarget(DropType.draw, dropPageSpec, dropPageCollect)
class DropPage extends React.Component {
  constructor(props) {
    super(props);
    this.handleActivePage = this.handleActivePage.bind(this);
  }

  handleActivePage() {
    const { onActiveComponent } = this.props;
    if (isFunction(onActiveComponent)) {
      // 激活当前编辑窗口为 Page
      onActiveComponent();
    }
  }

  render() {
    const { connectDropTarget } = this.props;
    return (
      connectDropTarget &&
      connectDropTarget(
        <div className={styles['drop-page-more']} onClick={this.handleActivePage}>
          {intl.get('hpfm.ui.message.field.operateDes').d('拖入更多组件|点击切换左侧组件为容器')}
        </div>
      )
    );
  }
}

export default DropPage;
