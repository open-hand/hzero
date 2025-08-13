import React, { PureComponent, Fragment } from 'react';
import FilterForm from './FilterForm';
import DataTable from './DataTable';

/**
 * 岗位分配员工-查询框/数据列表组件
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Object} form - 表单对象
 * @reactProps {string} locate - 组件位置
 * @reactProps {Function} onSearch - 表单查询
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
export default class List extends PureComponent {
  /**
   * render
   * @returns React.element
   */
  render() {
    const { locate, onSearch, onRef, ...tableProps } = this.props;

    const filterProps = {
      locate,
      onRef,
      onSearch,
    };
    return (
      <Fragment>
        <div className="table-list-search">
          <FilterForm {...filterProps} />
        </div>
        <DataTable {...tableProps} />
      </Fragment>
    );
  }
}
