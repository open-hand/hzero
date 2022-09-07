import React from 'react';
import { Form, Modal } from 'hzero-ui';

@Form.create({ fieldNameProp: null })
export default class TriggerMsgDrawer extends React.PureComponent {
  render() {
    const { triggerMsgData = {}, title, modalVisible, ...other } = this.props;
    const {
      triggerMsg, // 调度日志
    } = triggerMsgData;
    return (
      <Modal
        destroyOnClose
        title={title}
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        visible={modalVisible}
        {...other}
      >
        <p dangerouslySetInnerHTML={{ __html: triggerMsg }} />
      </Modal>
    );
  }
}
