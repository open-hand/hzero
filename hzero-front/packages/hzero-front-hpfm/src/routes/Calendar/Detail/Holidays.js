import React from 'react';
import { Table } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import moment from 'moment';

import intl from 'utils/intl';

/**
 * 公共假期数据展示列表
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Function} searchPaging - 分页查询
 * @reactProps {Object} form - 表单对象
 * @reactProps {Boolean} loading - 数据加载完成标记
 * @reactProps {Array} dataSource - Table数据源
 * @reactProps {object} pagination - 分页器
 * @reactProps {Number} pagination.current - 当前页码
 * @reactProps {Number} pagination.pageSize - 分页大小
 * @reactProps {Number} pagination.total - 数据总量
 * @return React.element
 */
export default class Holidays extends React.Component {
  @Bind()
  search(selectedRowKeys) {
    this.props.onSelect(selectedRowKeys);
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const { loading, dataSource, pagination, dateFormat, selectedRowKeys, onSearch } = this.props;

    const columns = [
      {
        title: intl.get('hpfm.calendar.model.calendar.holidayType').d('公休假期类型'),
        dataIndex: 'holidayTypeMeaning',
        width: 150,
      },
      {
        title: intl.get('hpfm.calendar.model.calendar.holidayName').d('公休假期'),
        dataIndex: 'holidayName',
        width: 150,
      },
      {
        title: intl.get('hpfm.calendar.model.calendar.dateRange').d('假期范围'),
        dataIndex: 'dateRange',
        width: 250,
        render: (val, record) => (
          <span>
            {moment(record.startDate).format(dateFormat)}~
            {moment(record.endDate).format(dateFormat)}
          </span>
        ),
      },
      {
        title: intl.get('hpfm.calendar.model.calendar.keyDate').d('假期当天'),
        dataIndex: 'keyDate',
        width: 100,
        render: val => <span>{moment(val).format(dateFormat)}</span>,
      },
      {
        title: intl.get('hpfm.calendar.model.calendar.remark').d('说明'),
        dataIndex: 'remark',
      },
    ];
    return (
      <Table
        bordered
        rowKey="holidayId"
        loading={loading}
        columns={columns}
        dataSource={dataSource}
        pagination={pagination}
        onChange={page => onSearch(page)}
        rowSelection={{
          selectedRowKeys,
          onChange: this.search,
        }}
      />
    );
  }
}
