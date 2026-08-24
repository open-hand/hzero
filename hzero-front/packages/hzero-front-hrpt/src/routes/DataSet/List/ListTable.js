import React, { PureComponent } from 'react';
import { Table, Popconfirm } from 'hzero-ui';
import intl from 'utils/intl';
import { enableRender, operatorRender } from 'utils/renderer';
import { tableScrollWidth } from 'utils/utils';

/**
 * 数据集数据列表
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Function} onChange - 分页查询
 * @reactProps {Boolean} loading - 数据加载完成标记
 * @reactProps {Array} dataSource - Table数据源
 * @reactProps {Object} pagination - 分页器
 * @reactProps {Number} pagination.current - 当前页码
 * @reactProps {Number} pagination.pageSize - 分页大小
 * @reactProps {Number} pagination.total - 数据总量
 * @return React.element
 */
export default class ListTable extends PureComponent {
  /**
   * 编辑
   * @param {object} record - 数据对象
   */
  editOption(record) {
    this.props.onEdit(record);
  }

  /**
   * 删除
   * @param {object} record - 数据对象
   */
  deleteOption(record) {
    this.props.onDelete(record);
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const {
      loading,
      dataSource,
      pagination,
      onChange,
      tenantRoleLevel,
      onShowTable = e => e,
    } = this.props;
    const columns = [
      {
        title: intl.get('entity.tenant.tag').d('租户'),
        dataIndex: 'tenantName',
        width: 200,
      },
      {
        title: intl.get('hrpt.reportDataSet.model.reportDataSet.datasetCode').d('数据集代码'),
        dataIndex: 'datasetCode',
        width: 150,
      },
      {
        title: intl.get('hrpt.reportDataSet.model.reportDataSet.datasetName').d('数据集名称'),
        dataIndex: 'datasetName',
      },
      {
        title: intl.get('hrpt.reportDataSet.modal.reportDataSet.type').d('类型'),
        dataIndex: 'sqlTypeMeaning',
        width: 100,
      },
      {
        title: intl.get('hzero.common.remark').d('备注'),
        dataIndex: 'remark',
        width: 100,
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        dataIndex: 'enabledFlag',
        width: 100,
        render: enableRender,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        dataIndex: 'operator',
        width: 180,
        render: (val, record) => {
          const operators = [];
          operators.push(
            {
              key: 'edit',
              ele: (
                <a onClick={() => this.editOption(record)}>
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </a>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            },
            {
              key: 'table',
              ele: (
                <a onClick={() => onShowTable(record)}>
                  {intl.get('hrpt.reportDataSet.button.table').d('查看报表')}
                </a>
              ),
              len: 4,
              title: intl.get('hrpt.reportDataSet.button.table').d('查看报表'),
            },
            {
              key: 'delete',
              ele: (
                <Popconfirm
                  placement="topRight"
                  title={intl.get('hzero.common.message.confirm.delete').d('是否删除此条记录？')}
                  onConfirm={() => this.deleteOption(record)}
                >
                  <a>{intl.get('hzero.common.button.delete').d('删除')}</a>
                </Popconfirm>
              ),
              len: 2,
              title: intl.get('hzero.common.button.delete').d('删除'),
            }
          );
          return operatorRender(operators);
        },
      },
    ].filter(col => (tenantRoleLevel ? col.dataIndex !== 'tenantName' : true));
    return (
      <Table
        bordered
        rowKey="datasetId"
        loading={loading}
        columns={columns}
        scroll={{ x: tableScrollWidth(columns) }}
        dataSource={dataSource}
        pagination={pagination}
        onChange={page => onChange(page)}
      />
    );
  }
}
