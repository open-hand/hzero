/**
 * DataTable
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-08-21
 * @copyright 2019 © HAND
 */

import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Table } from 'hzero-ui';

import intl from 'utils/intl';
import { tableScrollWidth } from 'utils/utils';
import { dateTimeRender, operatorRender, TagRender } from 'utils/renderer';

class DataTable extends Component {
  getColumns() {
    return [
      {
        title: intl.get('himp.commentImport.model.commentImport.batch').d('批次号'),
        dataIndex: 'batch',
      },
      {
        title: intl.get('himp.commentImport.model.commentImport.dataCount').d('数据数量'),
        dataIndex: 'dataCount',
        width: 100,
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        dataIndex: 'status',
        width: 130,
        render: status => {
          const { dataImportStatus = [] } = this.props;
          const statusList = [
            { status: 'UPLOADING', color: 'blue' /* , text: 'Excel导入' */ },
            { status: 'UPLOADED', color: 'green' /* , text: '验证成功' */ },
            { status: 'CHECKING', color: 'blue' /* , text: '验证失败' */ },
            { status: 'CHECKED', color: 'green' /* , text: '导入成功' */ },
            { status: 'IMPORTING', color: 'blue' /* , text: '导入失败' */ },
            { status: 'IMPORTED', color: 'green' /* , text: '数据异常' */ },
          ];
          return TagRender(
            status,
            dataImportStatus.map(item => {
              const tagItem = statusList.find(t => t.status === item.value) || {};
              return {
                status: item.value,
                text: item.meaning,
                color: tagItem.color,
              };
            })
          );
        },
      },
      {
        title: intl.get('himp.commentImport.model.commentImport.creationDate').d('创建日期'),
        dataIndex: 'creationDate',
        width: 160,
        render: dateTimeRender,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        key: 'operator',
        width: 110,
        render: (_, record) => {
          // TODO: 操作逻辑
          const { onRecordRestore, onRecordDelete } = this.props;
          const operators = [
            {
              key: 'restore',
              ele: (
                <a
                  onClick={() => {
                    onRecordRestore(record);
                  }}
                >
                  {intl.get('himp.commentImport.view.button.restore').d('恢复')}
                </a>
              ),
              len: 2,
              title: intl.get('himp.commentImport.view.button.restore').d('恢复'),
            },
          ];
          if (['IMPORTED', 'CHECKED', 'UPLOADED'].includes(record.status)) {
            operators.push({
              key: 'delete',
              ele: (
                <a
                  onClick={() => {
                    onRecordDelete(record);
                  }}
                >
                  {intl.get('hzero.common.button.delete').d('删除')}
                </a>
              ),
              len: 2,
              title: intl.get('hzero.common.button.delete').d('删除'),
            });
          }
          return operatorRender(operators, record);
        },
      },
    ];
  }

  render() {
    const {
      dataSource = [],
      pagination = false,
      onChange,
      queryLoading = false,
      deleteLoading = false,
    } = this.props;
    const columns = this.getColumns();
    return (
      <Table
        loading={queryLoading || deleteLoading}
        bordered
        rowKey="batch"
        columns={columns}
        scroll={{ x: tableScrollWidth(columns) }}
        dataSource={dataSource}
        pagination={pagination}
        onChange={onChange}
      />
    );
  }
}

DataTable.propTypes = {
  onChange: PropTypes.func.isRequired,
  onRecordRestore: PropTypes.func.isRequired,
  onRecordDelete: PropTypes.func.isRequired,
};
export default DataTable;
