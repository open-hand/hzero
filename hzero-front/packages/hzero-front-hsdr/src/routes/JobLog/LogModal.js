import React, { PureComponent } from 'react';
import { Modal, Spin, Form, Button } from 'hzero-ui';
import intl from 'utils/intl';

@Form.create({ fieldNameProp: null })
export default class LogModal extends PureComponent {
  render() {
    const { onOk, logDetail, logVisible, logDetailLoading = false } = this.props;

    return (
      <Modal
        title={intl.get('hsdr.jobLog.model.jobLog.logDetail').d('日志详情')}
        visible={logVisible}
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
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
