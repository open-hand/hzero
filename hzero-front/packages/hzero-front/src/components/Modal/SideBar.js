import React from 'react';
import { Modal } from 'hzero-ui';

export default class SideBar extends React.PureComponent {
  render() {
    const { side = 'right', children, ...other } = this.props;
    let otherProps = other;
    if (side) {
      otherProps = {
        wrapClassName: `ant-modal-sidebar-${side}`,
        transitionName: `move-${side}`,
        ...other,
      };
    }
    return <Modal {...otherProps}>{children}</Modal>;
  }
}
