import React, { Component } from 'react';
import { Table } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import isNil from 'lodash/isNil';
import isNumber from 'lodash/isNumber';

import intl from 'utils/intl';
import { enableRender } from 'utils/renderer';

const rowKey = 'approveChainLineId';

export default class List extends Component {
  @Bind()
  getColumns() {
    const { handleEdit = () => {}, handleEditDetail = () => {} } = this.props;
    return [
      {
        title: intl.get('hwfp.processDefine.model.processDefine.approveOrder').d('审批顺序'),
        dataIndex: 'approveOrder',
        width: 100,
      },
      {
        title: intl.get('hwfp.processDefine.model.processDefine.name').d('审批链名称'),
        dataIndex: 'name',
        width: 150,
      },
      {
        title: intl.get('hwfp.processDefine.model.processDefine.approveMethod').d('审批方式'),
        dataIndex: 'serviceName',
        width: 120,
      },
      {
        title: intl.get('hwfp.processDefine.model.processDefine.proportion').d('比例'),
        dataIndex: 'approveMethodValue',
        width: 80,
        render: (text) => (!isNil(text) && isNumber(text) ? `${text * 100}%` : text),
      },
      {
        title: intl.get('hwfp.processDefine.model.processDefine.approver').d('审批者'),
        dataIndex: 'approver',
      },
      {
        title: intl.get('hwfp.processDefine.model.processDefine.status').d('状态'),
        dataIndex: 'enabledFlag',
        render: enableRender,
        width: 80,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 120,
        render: (text, record) => (
          <>
            <a onClick={() => handleEdit(record)}>
              {intl.get('hzero.common.button.edit').d('编辑')}
            </a>
            <a onClick={() => handleEditDetail(record)} style={{ marginLeft: 8 }}>
              {intl.get('hzero.processDefine.view.button.editApproveRule').d('审批规则')}
            </a>
          </>
        ),
      },
    ];
  }

  render() {
    const {
      dataSource = [],
      loading,
      handleSelectRows = () => {},
      selectedRowKeys = [],
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
        pagination={false}
      />
    );
  }
}
