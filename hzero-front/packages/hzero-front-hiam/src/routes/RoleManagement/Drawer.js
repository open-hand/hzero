import React, { PureComponent } from 'react';
import PropTypes from 'prop-types';
import { Modal } from 'hzero-ui';

export default class Drawers extends PureComponent {
  static propTypes = {
    anchor: PropTypes.oneOf(['left', 'right', 'top', 'bottom']),
    title: PropTypes.string,
    visible: PropTypes.bool,
    onOk: PropTypes.func,
    onCancel: PropTypes.func,
  };

  static defaultProps = {
    anchor: 'left',
    title: '',
    visible: false,
    onOk: e => e,
    onCancel: e => e,
  };

  render() {
    const {
      anchor,
      title,
      visible,
      children,
      onCancel,
      onOk,
      wrapClassName,
      ...others
    } = this.props;
    return (
      <Modal
        title={title}
        wrapClassName={`ant-modal-sidebar-${anchor} ${wrapClassName}`}
        transitionName={`move-${anchor}`}
        visible={visible}
        onOk={onOk}
        onCancel={onCancel}
        destroyOnClose
        {...others}
      >
        {children}
      </Modal>
    );
  }
}
