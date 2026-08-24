import React, { PureComponent, Fragment } from 'react';
import { Table } from 'hzero-ui';
import intl from 'utils/intl';
/**
 * 员工信息-数据展示列表
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Object} form - 表单对象
 * @reactProps {Boolean} loading - 数据加载完成标记
 * @reactProps {Array} dataSource - Table数据源
 * @reactProps {Array} selectedRowKeys - 表格选中数据行的rowkey数组
 * @reactProps {Function} onChange - 分页查询
 * @reactProps {Function} rowSelectChange - 选中项发生变化时的回调
 * @reactProps {Object} pagination
 * @reactProps {Number} pagination.current - 当前页码
 * @reactProps {Number} pagination.pageSize - 分页大小
 * @reactProps {Number} pagination.total - 数据总量
 * @return React.element
 */
export default class DataTable extends PureComponent {
  /**
   * render
   * @returns React.element
   */
  render() {
    const {
      dataSource,
      selectedRowKeys,
      rowSelectChange,
      pagination,
      onChange,
      loading,
    } = this.props;
    const columns = [
      {
        title: intl.get('entity.employee.code').d('员工编码'),
        dataIndex: 'employeeNum',
      },
      {
        title: intl.get('entity.employee.name').d('员工姓名'),
        dataIndex: 'name',
      },
    ];
    const rowSelection = {
      selectedRowKeys,
      onChange: rowSelectChange,
      getCheckboxProps: record => ({
        disabled: record.disabled,
      }),
    };
    return (
      <Fragment>
        <Table
          bordered
          rowKey="employeeId"
          columns={columns}
          loading={loading}
          dataSource={dataSource}
          rowSelection={rowSelection}
          pagination={pagination}
          onChange={page => onChange({ page })}
        />
      </Fragment>
    );
  }
}
