import React, { PureComponent } from 'react';
import { Link } from 'dva/router';
import { Table, Icon } from 'hzero-ui';

import intl from 'utils/intl';
import { operatorRender } from 'utils/renderer';

/**
 * 跳转条件数据列表
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
   * render
   * @returns React.element
   */
  render() {
    const { loading, dataSource, pagination, onChange } = this.props;
    const columns = [
      {
        title: intl.get('hrpt.common.view.serialNumber').d('序号'),
        dataIndex: 'orderSeq',
        width: 70,
      },
      {
        title: intl.get('hrpt.common.report.reportCode').d('报表代码'),
        dataIndex: 'reportCode',
        width: 250,
      },
      {
        title: intl.get('hrpt.common.report.reportName').d('报表名称'),
        dataIndex: 'reportName',
        width: 200,
      },
      {
        title: intl.get('hrpt.reportQuery.model.reportQuery.reportTypeCode').d('报表类型'),
        dataIndex: 'reportTypeMeaning',
        width: 120,
        render: (text, record) => {
          const code = record.reportTypeCode;
          if (code === 'T' || code === 'ST') {
            return (
              <span>
                <Icon type="table" style={{ color: '#2B975C', marginRight: 5, fontSize: 16 }} />
                {text}
              </span>
            );
          } else if (code === 'C') {
            return (
              <span>
                <Icon type="pie-chart" style={{ color: '#AB82FF', marginRight: 5, fontSize: 16 }} />
                {text}
              </span>
            );
          } else if (code === 'D') {
            return (
              <span>
                <Icon type="profile" style={{ color: '#E95D3B', marginRight: 5, fontSize: 16 }} />
                {text}
              </span>
            );
          } else {
            return (
              <span>
                <Icon type="table" style={{ color: '#E95D3B', marginRight: 5, fontSize: 16 }} />
                {text}
              </span>
            );
          }
        },
      },
      {
        title: intl.get('hrpt.reportQuery.model.reportQuery.remark').d('备注'),
        dataIndex: 'remark',
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        dataIndex: 'operator',
        width: 60,
        fixed: 'right',
        render: (val, record) => {
          const operators = [];
          operators.push({
            key: 'view',
            ele: (
              <Link to={`/hrpt/report-query/detail/${record.reportUuid}/${record.reportName}`}>
                {intl.get('hzero.common.button.view').d('查看')}
              </Link>
            ),
            len: 2,
            title: intl.get('hzero.common.button.view').d('查看'),
          });
          return operatorRender(operators);
        },
      },
    ];
    return (
      <Table
        bordered
        rowKey="reportId"
        loading={loading}
        columns={columns}
        dataSource={dataSource}
        pagination={pagination}
        onChange={onChange}
      />
    );
  }
}
