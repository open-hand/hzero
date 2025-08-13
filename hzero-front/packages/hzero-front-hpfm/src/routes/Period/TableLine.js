import React, { Component } from 'react';
import { Table } from 'hzero-ui';
import intl from 'utils/intl';
import { dateRender } from 'utils/renderer';

/**
 * 期间查询数据展示组件
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Function} onChange - 分页查询
 * @reactProps {Array} dataSource - table数据源
 * @reactProps {String} pagination - 分页器
 * @reactProps {Number} [pagination.current] - 当前页码
 * @reactProps {Number} [pagination.pageSize] - 分页大小
 * @reactProps {Number} [pagination.total] - 数据总量
 * @return React.element
 */
export default class ListTable extends Component {
  /**
   * render
   * @returns React.element
   */
  render() {
    const { loading, dataSource, pagination, onChange } = this.props;

    const columns = [
      {
        title: intl.get('hpfm.period.model.period.periodSetCode').d('会计期编码'),
        dataIndex: 'periodSetCode',
      },
      {
        title: intl.get('hpfm.period.model.period.periodSetName').d('会计期名称'),
        dataIndex: 'periodSetName',
        key: 'periodSetName',
        width: 200,
      },
      {
        title: intl.get('hpfm.period.model.period.periodTotalCount').d('期间总数'),
        dataIndex: 'periodTotalCount',
        width: 100,
      },
      {
        title: intl.get('hpfm.period.model.period.periodName').d('期间'),
        dataIndex: 'periodName',
        width: 100,
      },
      {
        title: intl.get('hpfm.period.model.period.periodYear').d('年'),
        dataIndex: 'periodYear',
        width: 100,
      },
      {
        title: intl.get('hpfm.period.model.period.startDate').d('期间从'),
        dataIndex: 'startDate',
        width: 100,
        render: dateRender,
      },
      {
        title: intl.get('hpfm.period.model.period.endDate').d('期间至'),
        dataIndex: 'endDate',
        width: 100,
        render: dateRender,
      },
      {
        title: intl.get('hpfm.period.model.period.periodQuarter').d('季度'),
        dataIndex: 'periodQuarter',
        width: 100,
      },
    ];
    return (
      <Table
        bordered
        rowKey="periodId"
        loading={loading}
        columns={columns}
        dataSource={dataSource}
        pagination={pagination}
        onChange={page => onChange(page)}
      />
    );
  }
}
