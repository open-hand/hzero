/**
 * DataTable
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019/9/29
 * @copyright 2019/9/29 © HAND
 */

import React, { Component } from 'react';
import { Popconfirm, Table } from 'hzero-ui';

import intl from 'utils/intl';
import { tableScrollWidth } from 'utils/utils';
import { TagRender, dateTimeRender } from 'utils/renderer';

export default class DataTable extends Component {
  getColumns() {
    return [
      {
        title: intl.get('hzero.common.component.excelExport.hd.m.hd.taskCode').d('任务编号'),
        width: 240,
        dataIndex: 'taskCode',
      },
      {
        title: intl.get('hzero.common.component.excelExport.hd.m.hd.taskName').d('任务名称'),
        dataIndex: 'taskName',
      },
      {
        title: intl.get('hzero.common.component.excelExport.hd.m.hd.serviceName').d('所属服务'),
        dataIndex: 'serviceName',
        width: 200,
      },
      {
        title: intl.get('hzero.common.component.excelExport.hd.m.hd.state').d('任务状态'),
        width: 120,
        dataIndex: 'state',
        render: val => {
          const statusLists = [
            {
              status: 'DONE',
              color: 'green',
              text: intl.get('hzero.common.component.excelExport.hd.m.hd.state.done').d('已结束'),
            },
            {
              status: 'DOING',
              color: '',
              text: intl
                .get('hzero.common.component.excelExport.hd.m.hd.state.doing')
                .d('正在进行'),
            },
            {
              status: 'CANCELLED',
              color: 'red',
              text: intl
                .get('hzero.common.component.excelExport.hd.m.hd.state.cancelled')
                .d('已取消'),
            },
          ];
          return TagRender(val, statusLists);
        },
      },
      {
        title: intl.get('hzero.common.component.excelExport.hd.m.hd.endDateTime').d('任务结束时间'),
        dataIndex: 'endDateTime',
        width: 200,
        render: dateTimeRender,
      },
      {
        title: intl.get('hzero.common.component.excelExport.hd.m.hd.errorInfo').d('异常信息'),
        dataIndex: 'errorInfo',
        render: errorInfo =>
          errorInfo ? (
            <a
              onClick={() => {
                const { onShowErrorInfo } = this.props;
                onShowErrorInfo(errorInfo);
              }}
            >
              {errorInfo}
            </a>
          ) : null,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 60,
        fixed: 'right',
        render: (text, record) => {
          const { onRecordDownload, onRecordCancel } = this.props;
          return (
            <span className="action-link">
              {record.state === 'DONE' && (
                <a
                  onClick={() => {
                    onRecordDownload(record);
                  }}
                >
                  {intl.get('hzero.common.button.download').d('下载')}
                </a>
              )}
              {record.state === 'DOING' && (
                <Popconfirm
                  title={intl.get('hzero.common.message.confirm.cancel').d('是否取消导出？')}
                  onConfirm={() => {
                    onRecordCancel(record);
                  }}
                >
                  <a>{intl.get('hzero.common.button.cancel').d('取消')}</a>
                </Popconfirm>
              )}
            </span>
          );
        },
      },
    ];
  }

  render() {
    const { dataSource = [], pagination = {}, onChange, loading = false } = this.props;
    const columns = this.getColumns();
    return (
      <Table
        bordered
        loading={loading}
        columns={columns}
        scroll={{ x: tableScrollWidth(columns) }}
        dataSource={dataSource}
        pagination={pagination}
        onChange={onChange}
      />
    );
  }
}
