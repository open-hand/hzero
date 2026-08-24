import React from 'react';
import { Table } from 'hzero-ui';

import { Button as ButtonPermission } from 'components/Permission';

import { tableScrollWidth } from 'utils/utils';
import { enableRender, operatorRender } from 'utils/renderer';
import intl from 'utils/intl';

/**
 * 日历数据展示列表
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Function} onSearch - 分页查询
 * @reactProps {Function} editDetail - 查看详情
 * @reactProps {Function} editRow - 编辑行
 * @reactProps {Boolean} loading - 数据加载完成标记
 * @reactProps {Array} dataSource - Table数据源
 * @reactProps {object} pagination - 分页器
 * @reactProps {Number} pagination.current - 当前页码
 * @reactProps {Number} pagination.pageSize - 分页大小
 * @reactProps {Number} pagination.total - 数据总量
 * @return React.element
 */
export default class ListTable extends React.Component {
  /**
   * render
   * @returns React.element
   */
  render() {
    const { loading, dataSource, pagination, onSearch, onEditRow, onEditDetail, path } = this.props;

    const columns = [
      {
        title: intl.get('hpfm.calendar.model.calendar.calendarName').d('描述'),
        dataIndex: 'calendarName',
      },
      {
        title: intl.get('hpfm.calendar.model.calendar.country').d('国家/地区'),
        dataIndex: 'countryName',
      },
      {
        title: intl.get('hpfm.calendar.model.calendar.maintain').d('日历维护'),
        dataIndex: 'maintain',
        width: 90,
        render: (val, record) => (
          <span className="action-link">
            <a onClick={() => onEditDetail(record)}>
              {intl.get('hpfm.calendar.model.calendar.maintain').d('日历维护')}
            </a>
          </span>
        ),
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
        width: 60,
        fixed: 'right',
        render: (val, record) => {
          const operators = [
            {
              key: 'edit',
              ele: (
                <ButtonPermission
                  onClick={() => onEditRow(record)}
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.edit`,
                      type: 'button',
                      meaning: '日历定义-编辑',
                    },
                  ]}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            },
          ];
          return operatorRender(operators, record);
        },
      },
    ];
    return (
      <Table
        bordered
        rowKey="calendarId"
        loading={loading}
        columns={columns}
        scroll={{ x: tableScrollWidth(columns) }}
        dataSource={dataSource}
        pagination={pagination}
        onChange={page => onSearch(page)}
      />
    );
  }
}
