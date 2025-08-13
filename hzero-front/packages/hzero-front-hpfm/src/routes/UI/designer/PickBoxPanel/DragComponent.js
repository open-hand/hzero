/**
 * 可拖动的 新增的容器
 */
import React from 'react';
import PropTypes from 'prop-types';
import { DragSource } from 'react-dnd';

import intl from 'utils/intl';

import { DragType } from '../config';
import styles from './index.less';

import {getIconClassName} from '../icon';


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

const dragComponentSpec = {
  beginDrag(props) {
    return {
      component: props.component,
    };
  },
};
const dragComponentCollect = (connect, monitor) => {
  const connectDragSource = connect.dragSource();
  const connectDragPreview = connect.dragPreview();
  const isDragging = monitor.isDragging();
  return {
    connectDragSource,
    connectDragPreview,
    isDragging,
  };
};

@DragSource(DragType.dragComponent, dragComponentSpec, dragComponentCollect)
class DragComponent extends React.Component {
  static propTypes = {
    component: PropTypes.object.isRequired,
  };

  constructor(props) {
    super(props);
    this.state = {};
  }

  render() {
    const { component = {}, connectDragSource } = this.props;
    const dragComponentClassName = `${styles['drag-component']} pick-box-panel-item`;
    return (
      connectDragSource &&
      connectDragSource(
        <div className={dragComponentClassName}>
          <i className={classNames[component.className]} />
          <h3>{intl.get(component.description).d(component.defaultIntlDescription)}</h3>
        </div>
      )
    );
  }
}

export default DragComponent;
