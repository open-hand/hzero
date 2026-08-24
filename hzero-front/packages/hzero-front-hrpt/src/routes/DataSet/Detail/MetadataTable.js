import React, { PureComponent } from 'react';
import { Table } from 'hzero-ui';
import intl from 'utils/intl';
import { tableScrollWidth } from 'utils/utils';
import { operatorRender } from 'utils/renderer';

/**
 * 元数据数据列表
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
export default class MetadataTable extends PureComponent {
  /**
   * 编辑
   * @param {object} record - 数据对象
   */
  editOption(record) {
    this.props.onEdit(record);
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const { loading, dataSource = [], metadataRowSelection } = this.props;
    const columns = [
      {
        title: intl.get('hrpt.common.view.serialNumber').d('序号'),
        dataIndex: 'ordinal',
        width: 100,
      },
      {
        title: intl.get('hrpt.reportDataSet.model.reportDataSet.name').d('列名'),
        dataIndex: 'name',
        width: 200,
      },
      {
        title: intl.get('hrpt.reportDataSet.model.reportDataSet.text').d('标题'),
        dataIndex: 'text',
      },
      {
        title: intl.get('hrpt.reportDataSet.model.reportDataSet.dataType').d('数据类型'),
        dataIndex: 'dataType',
        width: 150,
      },
      {
        title: intl.get('hrpt.reportDataSet.model.reportDataSet.decimals').d('精度'),
        dataIndex: 'decimals',
        width: 100,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        dataIndex: 'option',
        fixed: 'right',
        width: 60,
        render: (val, record) => {
          const operators = [];
          operators.push({
            key: 'edit',
            ele: (
              <a onClick={() => this.editOption(record)}>
                {intl.get('hzero.common.button.edit').d('编辑')}
              </a>
            ),
            len: 2,
            title: intl.get('hzero.common.button.edit').d('编辑'),
          });
          return operatorRender(operators);
        },
      },
    ];
    return (
      <Table
        bordered
        rowKey="ordinal"
        rowSelection={metadataRowSelection}
        loading={loading}
        columns={columns}
        scroll={{ x: tableScrollWidth(columns) }}
        dataSource={dataSource}
        pagination={false}
      />
    );
  }
}
