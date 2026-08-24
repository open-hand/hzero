/**
 * Drawer - Excel异步导出-抽屉
 * @date: 2019-8-7
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import React from 'react';
import { Form, Modal } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

@Form.create({ fieldNameProp: null })
export default class Drawer extends React.Component {
  @Bind()
  handleCancel() {
    const { onCancel } = this.props;
    onCancel();
  }

  render() {
    const { title, modalVisible, initData } = this.props;

    return (
      <Modal
        destroyOnClose
        title={title}
        visible={modalVisible}
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        footer={null}
        onCancel={this.handleCancel}
      >
        {initData}
      </Modal>
    );
  }
}
