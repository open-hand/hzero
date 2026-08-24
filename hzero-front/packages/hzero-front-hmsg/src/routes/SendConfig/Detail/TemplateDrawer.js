import React from 'react';
import { Modal, Button, Spin } from 'hzero-ui';

import intl from 'utils/intl';

export default class TemplateDrawer extends React.Component {
  render() {
    const { title, anchor, visible, loading = false, onClose = (e) => e, content } = this.props;
    return (
      <Modal
        title={title}
        visible={visible}
        destroyOnClose
        width={700}
        onCancel={onClose}
        wrapClassName={`ant-modal-sidebar-${anchor}`}
        transitionName={`move-${anchor}`}
        footer={
          <Button onClick={onClose}>{intl.get('hzero.common.button.close').d('关闭')}</Button>
        }
      >
        <Spin spinning={loading}>
          <div dangerouslySetInnerHTML={{ __html: content }} />
        </Spin>
      </Modal>
    );
  }
}
