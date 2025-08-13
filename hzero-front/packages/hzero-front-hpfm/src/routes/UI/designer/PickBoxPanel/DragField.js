/**
 * 可拖动的 新增的字段
 */
import React from 'react';
import { DragSource } from 'react-dnd';

import { DragType } from '../config';

import { getIconClassName } from '../icon';

const classNames = {
  formClassName: getIconClassName('form'),
  tableClassName: getIconClassName('table'),
  toolbarClassName: getIconClassName('buttonq'),
  modalClassName: getIconClassName('square'),
  selectClassName: getIconClassName('select'),
  dateClassName: getIconClassName('date'),
  numberClassName: getIconClassName('plus-numberfill'),
  lovClassName: getIconClassName('search'),
  inputClassName: getIconClassName('wenben'),
  checkboxClassName: getIconClassName('checkbox'),
  switchClassName: getIconClassName('kaiguanclose'),
  buttonClassName: getIconClassName('button-component'),
};

const dragFieldSpec = {
  beginDrag(props) {
    return {
      component: props.component,
    };
  },
};

const dragFieldCollect = (connect, monitor) => {
  const connectDragSource = connect.dragSource();
  const connectDragPreview = connect.dragPreview();
  const isDragging = monitor.isDragging();
  return {
    connectDragSource,
    connectDragPreview,
    isDragging,
  };
};

@DragSource(DragType.dragField, dragFieldSpec, dragFieldCollect)
class DragField extends React.Component {
  constructor(props) {
    super(props);
    this.state = {};
  }

  render() {
    const { component = {}, connectDragSource } = this.props;
    return (
      connectDragSource &&
      connectDragSource(
        <div className="drag-field pick-box-panel-item">
          <i className={classNames[component.className]} />
          <h3>{component.name}</h3>
        </div>
      )
    );
  }
}

export default DragField;
