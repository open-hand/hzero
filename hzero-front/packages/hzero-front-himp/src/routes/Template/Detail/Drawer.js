/**
 * Editor - 侧边栏抽屉弹出组件
 * @since 2018-08-23
 * @author yuan.tian <yuan.tian@hand-china.com>
 * @version 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import React, { PureComponent } from 'react';
import PropTypes from 'prop-types';
import { Modal } from 'hzero-ui';

export default class Drawer extends PureComponent {
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
    const { anchor, title, visible, children, onCancel, onOk, ...others } = this.props;
    return (
      <Modal
        destroyOnClose
        title={title}
        wrapClassName={`ant-modal-sidebar-${anchor}`}
        transitionName={`move-${anchor}`}
        visible={visible}
        onOk={onOk}
        onCancel={onCancel}
        {...others}
      >
        {children}
      </Modal>
    );
  }
}
