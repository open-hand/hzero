import React, { Component } from 'react';
import { Modal, Progress, Form, Button } from 'hzero-ui';
import intl from 'utils/intl';

@Form.create({ fieldNameProp: null })
export default class ProgressModal extends Component {
  render() {
    const { onOk, progressVisible, progressValue = {} } = this.props;
    const { progress = 0, message = '' } = progressValue;
    return (
      <Modal
        title={intl.get('hsdr.jobLog.model.jobLog.taskProgress').d('任务进度')}
        visible={progressVisible}
        width={520}
        destroyOnClose
        closable={false}
        footer={
          <Button type="primary" onClick={onOk}>
            {intl.get('hzero.common.button.ok').d('确定')}
          </Button>
        }
      >
        <div>
          <div style={{ textAlign: 'center', margin: '4px 0' }}>{message}</div>
          <Progress percent={progress} />
        </div>
      </Modal>
    );
  }
}
