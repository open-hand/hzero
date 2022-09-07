import React, { Component } from 'react';
import { Table } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import { enableRender } from 'utils/renderer';

const rowKey = 'processKey';

export default class List extends Component {
  @Bind()
  getColumns() {
    const { handleEdit = () => {} } = this.props;
    return [
      {
        title: intl.get('hwfp.automaticProcess.model.automaticProcess.processKey').d('流程编码'),
        dataIndex: 'processKey',
        width: 120,
      },
      {
        title: intl.get('hwfp.automaticProcess.model.automaticProcess.processName').d('流程名称'),
        dataIndex: 'processName',
        width: 120,
      },
      {
        title: intl.get('hwfp.automaticProcess.model.automaticProcess.condition').d('处理条件'),
        dataIndex: 'processConditionMeaning',
        width: 60,
      },
      {
        title: intl.get('hwfp.automaticProcess.model.automaticProcess.conditionInfo').d('条件明细'),
        dataIndex: 'conditionDetail',
        width: 180,
      },
      {
        title: intl.get('hwfp.automaticProcess.model.automaticProcess.rule').d('处理规则'),
        dataIndex: 'processRuleMeaning',
        width: 60,
      },
      {
        title: intl.get('hwfp.automaticProcess.model.automaticProcess.action').d('处理动作'),
        dataIndex: 'processAction',
        width: 120,
      },
      {
        title: intl.get('hwfp.automaticProcess.model.automaticProcess.status').d('状态'),
        dataIndex: 'enabledFlag',
        render: enableRender,
        width: 80,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 80,
        render: (text, record) => (
          <a onClick={() => handleEdit(record)}>{intl.get('hzero.common.button.edit').d('编辑')}</a>
        ),
      },
    ];
  }

  @Bind()
  handleChange(pagination) {
    const { handleChangePagination = () => {}, handleSelectRows = () => {} } = this.props;
    // 翻页时清空已选择行数据
    handleSelectRows([], []);
    handleChangePagination({ page: pagination });
  }

  render() {
    const {
      dataSource = [],
      pagination = {},
      selectedRowKeys = [],
      loading = false,
      handleSelectRows = () => {},
    } = this.props;
    const rowSelection = {
      selectedRowKeys,
      onChange: (rowKeys, rows) => {
        handleSelectRows(rowKeys, rows);
      },
    };

    return (
      <Table
        bordered
        rowKey={rowKey}
        rowSelection={rowSelection}
        columns={this.getColumns()}
        dataSource={dataSource}
        loading={loading}
        pagination={pagination}
        onChange={this.handleChange}
      />
    );
  }
}
