import React, { Component } from 'react';
import { Table } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import { enableRender } from 'utils/renderer';

import styles from '../style/index.less';

const rowKey = 'approveDetailId';
export default class List extends Component {
  @Bind()
  getColumns() {
    const { handleEdit = () => {} } = this.props;
    return [
      {
        title: intl.get('hwfp.processDefine.model.processDefine.approveRule').d('审批规则'),
        dataIndex: 'approveRuleName',
      },
      {
        title: intl.get('hwfp.processDefine.model.processDefine.approver').d('审批者'),
        dataIndex: 'approver',
      },
      {
        title: intl.get('hwfp.processDefine.model.processDefine.enabledCondition').d('启用条件'),
        dataIndex: 'enabledConditionName',
        width: 200,
      },
      {
        title: intl.get('hwfp.processDefine.model.processDefine.status').d('状态'),
        dataIndex: 'enabledFlag',
        render: enableRender,
        width: 120,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 100,
        render: (text, record) => (
          <a onClick={() => handleEdit(record)}>{intl.get('hzero.common.button.edit').d('编辑')}</a>
        ),
      },
    ];
  }

  render() {
    const { dataSource = [], loading, handleSelectRows = () => {} } = this.props;
    const rowSelection = {
      onChange: (selectedRowKeys, selectedRows) => {
        handleSelectRows(selectedRows);
      },
    };

    return (
      <Table
        className={styles['right-content-detail-table']}
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
