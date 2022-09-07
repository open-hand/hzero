import React from 'react';
import { Form, Spin, Modal } from 'hzero-ui';

@Form.create({ fieldNameProp: null })
export default class HandleMsgDrawer extends React.PureComponent {
  render() {
    const {
      pullLoading,
      logContent, // 拉取到的日志内容
      tipsMessage,
      title,
      modalVisible,
      ...other
    } = this.props;
    return (
      <Modal
        destroyOnClose
        title={title}
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        visible={modalVisible}
        {...other}
      >
        <p dangerouslySetInnerHTML={{ __html: logContent }} />
        <div dangerouslySetInnerHTML={{ __html: tipsMessage }} />
        <div style={{ textAlign: 'center' }}>
          <Spin spinning={pullLoading} />
        </div>
      </Modal>
    );
  }
}
