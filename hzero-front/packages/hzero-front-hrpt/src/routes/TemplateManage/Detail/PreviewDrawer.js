import React from 'react';
import { Modal } from 'hzero-ui';

export default class PreviewDrawer extends React.Component {
  render() {
    const { title, value, visible, footer, onCancel = e => e } = this.props;
    return (
      <Modal title={title} visible={visible} onCancel={onCancel} footer={footer}>
        <div dangerouslySetInnerHTML={{ __html: value }} />
      </Modal>
    );
  }
}
