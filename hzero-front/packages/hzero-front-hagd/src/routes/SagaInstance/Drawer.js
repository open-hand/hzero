import React from 'react';
import { Form, Modal, Spin, Button } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';

import SagaImage from '../components/SageImage';

@Form.create({ fieldNameProp: null })
export default class Drawer extends React.Component {
  @Bind()
  handleCancel() {
    const { onCancel } = this.props;
    onCancel();
  }

  @Bind()
  handleUnLock() {
    const { onUnLock, initData } = this.props;
    onUnLock(initData);
  }

  @Bind()
  handleRetry() {
    const { onRetry, initData } = this.props;
    onRetry(initData);
  }

  render() {
    const {
      title,
      modalVisible,
      loading,
      initLoading = false,
      unLockLoading = false,
      retryLoading = false,
      initData = {},
    } = this.props;
    return (
      <Modal
        destroyOnClose
        title={title}
        width="1200px"
        visible={modalVisible}
        confirmLoading={loading}
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        onCancel={this.handleCancel}
        footer={
          <Button type="primary" onClick={this.handleCancel}>
            {intl.get('hzero.common.button.close').d('关闭')}
          </Button>
        }
      >
        <Spin spinning={initLoading}>
          <SagaImage
            unLockLoading={unLockLoading}
            retryLoading={retryLoading}
            onUnLock={this.handleUnLock}
            onRetry={this.handleRetry}
            data={initData}
            instance
          />
        </Spin>
      </Modal>
    );
  }
}
