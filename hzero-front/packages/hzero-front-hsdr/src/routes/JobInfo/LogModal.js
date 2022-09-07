import React, { Component } from 'react';
import { Modal, Form, Button, Spin } from 'hzero-ui';
import intl from 'utils/intl';

@Form.create({ fieldNameProp: null })
export default class LogModal extends Component {
  render() {
    const { onOk, logDetail, logVisible, logDetailLoading } = this.props;
    return (
      <Modal
        title={intl.get('hsdr.jobLog.model.jobLog.logDetail').d('日志详情')}
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        visible={logVisible}
        width={600}
        destroyOnClose
        closable={false}
        footer={
          <Button type="primary" onClick={onOk}>
            {intl.get('hzero.common.button.ok').d('确定')}
          </Button>
        }
      >
        <Spin spinning={logDetailLoading}>
          <pre dangerouslySetInnerHTML={{ __html: logDetail }} />
        </Spin>
      </Modal>
    );
  }
}
