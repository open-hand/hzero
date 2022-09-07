import React from 'react';
import { Form, Input, message, Modal, Table } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';

@Form.create({ fieldNameProps: null })
export default class JumpModal extends React.Component {
  state = {
    // currentActivity: {},
    selectedRowKeys: [],
    selectedRows: [],
  };

  @Bind()
  handleOk() {
    const { onOk = e => e, form } = this.props;
    const { selectedRows = [] } = this.state;
    form.validateFields((err, values) => {
      if (!err) {
        if (selectedRows.length === 0) {
          message.error(intl.get('hwfp.task.view.message.selectAct').d('请选择节点'));
          return;
        }
        onOk(values, selectedRows[0]);
      }
    });
  }

  @Bind()
  handleCancel() {
    const { form, onCancel = e => e } = this.props;
    form.resetFields();
    this.setState({ selectedRowKeys: [], selectedRows: [] });
    onCancel();
  }

  @Bind()
  onSelectChange(selectedRowKeys, selectedRows) {
    this.setState({ selectedRowKeys, selectedRows });
  }

  render() {
    const { form, visible = false, jumpList = [], approveLoading = false } = this.props;
    const { selectedRowKeys } = this.state;

    const rowSelection = {
      selectedRowKeys,
      type: 'radio',
      onChange: this.onSelectChange,
    };
    const columns = [
      {
        title: intl.get('hwfp.task.model.task.actName').d('节点名称'),
        dataIndex: 'actName',
        width: 150,
      },
      {
        title: intl.get('hwfp.task.model.task.approver').d('审批人'),
        dataIndex: 'approverName',
      },
    ];

    return (
      <Modal
        title={intl.get('hwfp.task.view.message.reject').d('指定驳回节点')}
        visible={visible}
        width="620px"
        confirmLoading={approveLoading}
        onOk={this.handleOk}
        onCancel={this.handleCancel}
      >
        <Form>
          <Form.Item label={intl.get('hwfp.task.model.task.comment').d('驳回意见')}>
            {form.getFieldDecorator('comment', {
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hwfp.task.model.task.comment').d('驳回意见'),
                  }),
                },
              ],
            })(<Input.TextArea autosize={{ minRows: 3, maxRows: 6 }} maxLength={200} />)}
          </Form.Item>
        </Form>
        <Table bordered rowSelection={rowSelection} columns={columns} dataSource={jumpList} />
      </Modal>
    );
  }
}
