/**
 * exception - 报错详情Modal
 * @date: 2018-8-21
 * @author: LZY <zhuyan.luo@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import { Form, Modal, Button } from 'hzero-ui';

import intl from 'utils/intl';

@Form.create({ fieldNameProp: null })
export default class ExceptionMsgDrawer extends React.PureComponent {
  render() {
    const { exceptionDetail = {}, visible, title, onCancel } = this.props;
    return (
      <Modal
        destroyOnClose
        title={title}
        width={520}
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        visible={visible}
        onCancel={onCancel}
        footer={
          <Button type="primary" style={{ float: 'left' }} onClick={onCancel}>
            {intl.get('hzero.common.button.back').d('返回')}
          </Button>
        }
      >
        <div>{exceptionDetail.messStr}</div>
      </Modal>
    );
  }
}
