import React, { Component } from 'react';
import { Table } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import { dateTimeRender } from 'utils/renderer';
import { tableScrollWidth } from 'utils/utils';
import intl from 'utils/intl';

import { processStatusRender } from '@/utils/util';

export default class List extends Component {
  @Bind()
  handleChange(pagination = {}) {
    this.props.handleChange({
      page: pagination,
    });
  }

  get columns() {
    const { processStatus = [] } = this.props;
    const processStatusObj = {};
    processStatus.forEach((item) => {
      processStatusObj[item.value] = item.meaning;
    });
    return [
      {
        title: intl.get('hwfp.common.model.process.approvalStatus').d('审批状态'),
        dataIndex: 'suspended',
        width: 180,
        render: (val) => processStatusRender(processStatusObj, val),
      },
      {
        title: intl.get('hwfp.common.model.process.name').d('流程名称'),
        dataIndex: 'processName',
        width: 250,
      },
      {
        title: intl.get('hwfp.common.model.process.description').d('流程描述'),
        dataIndex: 'description',
        width: 250,
      },
      {
        title: intl.get('hwfp.common.view.message.current.stage').d('当前节点'),
        dataIndex: 'name',
        width: 150,
      },
      {
        title: intl.get('hwfp.common.view.message.handler').d('当前处理人'),
        dataIndex: 'assigneeName',
        width: 150,
        render: (val) => val && val.split(',').map((item) => `${item} `),
      },
      {
        title: intl.get('hwfp.common.model.apply.owner').d('申请人'),
        dataIndex: 'startUserName',
        width: 150,
        render: (val, record) => (
          <span>
            {val}({record.startUserId})
          </span>
        ),
      },
      {
        title: intl.get('hwfp.task.model.task.creationTime').d('创建时间'),
        dataIndex: 'startTime',
        width: 160,
        render: dateTimeRender,
      },
    ];
  }

  render() {
    const { dataSource = [], loading, pagination, selectedRows = [], onSelectRows } = this.props;
    const selectedRowKeys = selectedRows.map((item) => item.id);

    return (
      <Table
        bordered
        rowKey="id"
        loading={loading}
        dataSource={dataSource}
        pagination={pagination}
        columns={this.columns}
        onChange={this.handleChange}
        scroll={{ x: tableScrollWidth(this.columns) }}
        rowSelection={{
          selectedRowKeys,
          onChange: (_, rows) => {
            onSelectRows(rows);
          },
        }}
      />
    );
  }
}
