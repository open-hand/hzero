import React from 'react';
import { Table } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import intl from 'utils/intl';
import Checkbox from 'components/Checkbox';

/**
 * 工作日数据展示列表
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
export default class Weekdays extends React.Component {
  /**
   * 更改工作日状态
   * @param {object} event - 事件对象
   * @param {object} record - 工作日对象
   */
  @Bind()
  changeStatus(event, record) {
    this.props.onChange(event.target.checked, record.weekdayId);
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const { loading, dataSource } = this.props;

    const columns = [
      {
        title: intl.get('hpfm.calendar.model.calendar.week').d('星期'),
        dataIndex: 'weekdayMeaning',
        width: 150,
      },
      {
        title: intl.get('hpfm.calendar.model.calendar.workday').d('工作日'),
        dataIndex: 'weekdayFlag',
        render: (val, record) => (
          <Checkbox checked={val} onChange={e => this.changeStatus(e, record)} />
        ),
      },
    ];
    return (
      <Table
        bordered
        rowKey="weekdayId"
        style={{ width: 350 }}
        loading={loading}
        columns={columns}
        dataSource={dataSource}
        pagination={false}
      />
    );
  }
}
