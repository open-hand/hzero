import React, { PureComponent } from 'react';
import { Modal, Spin, Form, Button } from 'hzero-ui';
import intl from 'utils/intl';

@Form.create({ fieldNameProp: null })
export default class ErrorModal extends PureComponent {
  render() {
    const { onOk, errorDetail, errorVisible, errorDetailLoading = false } = this.props;

    return (
      <Modal
        title={intl.get('hsdr.jobLog.model.jobLog.errorDetail').d('错误详情')}
        visible={errorVisible}
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
        <Spin spinning={errorDetailLoading}>
          <pre dangerouslySetInnerHTML={{ __html: errorDetail }} />
        </Spin>
      </Modal>
    );
  }
}
