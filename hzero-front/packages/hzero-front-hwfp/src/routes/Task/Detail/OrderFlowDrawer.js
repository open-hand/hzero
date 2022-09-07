import React, { PureComponent } from 'react';
import { Modal, Table } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';

export default class OrderFlowDrawer extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      selectedRowKeys: [],
      selectedRows: [],
    };
  }

  /**
   * 提交选中
   */
  @Bind()
  handleSubmit() {
    const { onOk = e => e } = this.props;
    const { selectedRowKeys = [], selectedRows } = this.state;
    if (selectedRowKeys.length > 0) {
      onOk(selectedRows);
      this.handleCancel();
      this.setState({
        selectedRowKeys: [],
        selectedRows: [],
      });
    }
  }

  /**
   * 选中节点
   * @param {*} selectedRowKeys
   * @param {*} selectedRows
   */
  @Bind()
  onSelectChange(selectedRowKeys, selectedRows) {
    this.setState({ selectedRowKeys, selectedRows });
  }

  @Bind()
  handleCancel() {
    const { onCancel = e => e } = this.props;
    onCancel();
    this.setState({
      selectedRowKeys: [],
      selectedRows: [],
    });
  }

  render() {
    const { dataSource, visible, title } = this.props;
    const { selectedRowKeys } = this.state;
    const rowSelection = {
      selectedRowKeys,
      onChange: this.onSelectChange,
      type: 'radio',
      width: 50,
    };

    const columns = [
      {
        title: intl.get('hwfp.task.view.option.orderFlowName').d('顺序流名称'),
        dataIndex: 'name',
        width: 380,
      },
    ];
    return (
      <Modal
        destroyOnClose
        width={500}
        title={title}
        visible={visible}
        onCancel={this.handleCancel}
        onOk={this.handleSubmit}
        bodyStyle={{ maxHeight: '300px', overflowY: 'auto' }}
      >
        <Table
          bordered
          dataSource={dataSource}
          rowSelection={rowSelection}
          columns={columns}
          pagination={false}
        />
      </Modal>
    );
  }
}
