import React, { Component } from 'react';
import { Modal, Form, Spin, Button } from 'hzero-ui';
import intl from 'utils/intl';

@Form.create({ fieldNameProp: null })
export default class MessageDrawer extends Component {
  render() {
    const { onOk, messageVisible, dataSource, messageDetailLoading } = this.props;

    return (
      <Modal
        title={dataSource.title}
        visible={messageVisible}
        width={750}
        destroyOnClose
        closable={false}
        footer={
          <Button type="primary" onClick={onOk}>
            {intl.get('hzero.common.button.ok').d('确定')}
          </Button>
        }
      >
        <Spin spinning={messageDetailLoading}>
          <pre dangerouslySetInnerHTML={{ __html: dataSource.text }} />
        </Spin>
      </Modal>
    );
  }
}
