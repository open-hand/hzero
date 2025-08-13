import React, { PureComponent } from 'react';
import { Modal, Table } from 'hzero-ui';
// import { isNumber } from 'lodash';

import { tableScrollWidth } from 'utils/utils';

export default class Organization extends PureComponent {
  state = {
    selectedRow: this.props.defaultSelectedRow,
  };

  ok() {
    const { onOk = e => e, onCancel = e => e } = this.props;
    const { selectedRow } = this.state;
    this.setState({
      selectedRow: {},
    });
    onOk(selectedRow);
    onCancel();
  }

  cancel() {
    const { onCancel = e => e } = this.props;
    onCancel();
  }

  defaultRowkey = 'id';

  onRow(record) {
    const setSelect = () => {
      this.setState({
        selectedRow: record,
      });
    };
    return {
      onClick: () => {
        setSelect();
      },
    };
  }

  render() {
    const {
      title,
      visible,
      onCancel,
      onOk,
      dataSource,
      loading,
      defaultSelectedRow,
      prompt = {},
      ...others
    } = this.props;
    const { selectedRow = {} } = this.state;
    const tableColumns = [
      {
        title: prompt.unitCode,
        dataIndex: 'unitCode',
      },
      {
        title: prompt.unitName,
        dataIndex: 'unitName',
      },
    ];
    const tableProps = {
      loading,
      dataSource,
      pagination: false,
      bordered: true,
      childrenColumnName: 'childHrUnits',
      onRow: this.onRow.bind(this),
      columns: tableColumns,
      scroll: { x: tableScrollWidth(tableColumns) },
      rowKey: 'unitId',
      rowSelection: {
        type: 'radio',
        selectedRowKeys: [selectedRow.unitId],
      },
    };
    return (
      <Modal
        title={prompt.selectUnit}
        visible={visible}
        onOk={this.ok.bind(this)}
        onCancel={this.cancel.bind(this)}
        destroyOnClose
        {...others}
      >
        <Table {...tableProps} />
      </Modal>
    );
  }
}
