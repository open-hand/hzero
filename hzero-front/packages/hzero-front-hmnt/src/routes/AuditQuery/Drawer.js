/**
 * Drawer -抽屉
 * @date: 2019-7-18
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import React from 'react';
import { Form, Modal, Spin, Table, Button } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';

@Form.create({ fieldNameProp: null })
export default class Drawer extends React.Component {
  @Bind()
  handleCancel() {
    const { onCancel } = this.props;
    onCancel();
  }

  @Bind()
  onCell() {
    return {
      style: {
        overflow: 'hidden',
        maxWidth: 180,
        textOverflow: 'ellipsis',
        whiteSpace: 'nowrap',
      },
      onDoubleClick: e => {
        const { target } = e;
        if (target.style.whiteSpace === 'normal') {
          target.style.whiteSpace = 'nowrap';
        } else {
          target.style.whiteSpace = 'normal';
        }
      },
    };
  }

  render() {
    const { title, modalVisible, editflag, initLoading, initData } = this.props;
    const columns = [
      {
        title: intl.get('hmnt.auditQuery.model.auditQuery.field').d('参数名称'),
        width: 180,
        dataIndex: 'field',
      },
      {
        title: intl.get('hmnt.auditQuery.model.auditQuery.value').d('参数值'),
        width: 180,
        onCell: this.onCell,
        dataIndex: 'value',
      },
    ];
    return (
      <Modal
        destroyOnClose
        title={title}
        visible={modalVisible}
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
          <Form>
            {editflag ? (
              <Table
                bordered
                rowKey="Id"
                dataSource={initData}
                columns={columns}
                pagination={false}
              />
            ) : (
              <pre>{JSON.stringify(initData, 0, 2)}</pre>
            )}
          </Form>
        </Spin>
      </Modal>
    );
  }
}
