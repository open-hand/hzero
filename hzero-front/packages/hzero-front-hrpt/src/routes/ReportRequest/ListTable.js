import React, { PureComponent } from 'react';
import { Table, Tag } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import { dateTimeRender, operatorRender } from 'utils/renderer';
import { tableScrollWidth } from 'utils/utils';

/**
 * 数据列表
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
  @Bind()
  handleDetail(record) {
    this.props.onDetail(record);
  }

  @Bind()
  handleExport(record) {
    this.props.onExport(record);
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const { tenantRoleLevel, loading, dataSource, pagination, onChange } = this.props;
    const columns = [
      {
        title: intl.get('entity.tenant.tag').d('租户'),
        dataIndex: 'tenantName',
        width: 150,
      },
      {
        title: intl.get('hrpt.common.report.reportCode').d('报表代码'),
        dataIndex: 'reportCode',
      },
      {
        title: intl.get('hrpt.common.report.reportName').d('报表名称'),
        dataIndex: 'reportName',
        width: 150,
      },
      {
        title: intl.get('hrpt.common.view.requestStatus').d('运行状态'),
        dataIndex: 'requestStatusMeaning',
        width: 100,
        render: (text, record) => {
          let mean = '';
          switch (record.requestStatus) {
            case 'F': // 已完成
              mean = (
                <Tag color="green" style={{ margin: 0 }}>
                  {text}
                </Tag>
              );
              break;
            case 'E': // 错误
              mean = (
                <Tag color="red" style={{ margin: 0 }}>
                  {text}
                </Tag>
              );
              break;
            case 'W': // 警告
              mean = (
                <Tag color="gold" style={{ margin: 0 }}>
                  {text}
                </Tag>
              );
              break;
            case 'R': // 运行中
              mean = (
                <Tag color="blue" style={{ margin: 0 }}>
                  {text}
                </Tag>
              );
              break;
            // case 'P': // 就绪
            //   mean = <Tag style={{ margin: 0 }}>{text}</Tag>;
            //   break;
            default:
              mean = <Tag style={{ margin: 0 }}>{text}</Tag>;
              break;
          }
          return mean;
        },
      },
      {
        title: intl.get('hrpt.reportRequest.model.reportRequest.requester').d('请求人名称'),
        dataIndex: 'requester',
        width: 150,
      },
      {
        title: intl.get('hrpt.common.view.startTime').d('开始时间'),
        width: 150,
        dataIndex: 'startDate',
        render: dateTimeRender,
      },
      {
        title: intl.get('hrpt.common.view.endTime').d('结束时间'),
        width: 150,
        dataIndex: 'endDate',
        render: dateTimeRender,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        dataIndex: 'operator',
        width: 130,
        fixed: 'right',
        render: (text, record) => {
          const operators = [];
          operators.push({
            key: 'detail',
            ele: (
              <a onClick={() => this.handleDetail(record)}>
                {intl.get('hzero.common.button.detail').d('详情')}
              </a>
            ),
            len: 2,
            title: intl.get('hzero.common.button.detail').d('详情'),
          });
          if (record.fileUrl) {
            operators.push({
              key: 'export',
              ele: (
                <a onClick={() => this.handleExport(record)}>
                  {intl.get('hrpt.common.report.export').d('导出结果')}
                </a>
              ),
              len: 4,
              title: intl.get('hrpt.common.report.export').d('导出结果'),
            });
          }
          return operatorRender(operators);
        },
      },
    ].filter((col) => (tenantRoleLevel ? col.dataIndex !== 'tenantName' : true));
    return (
      <Table
        bordered
        rowKey="requestId"
        loading={loading}
        columns={columns}
        scroll={{ x: tableScrollWidth(columns) }}
        dataSource={dataSource}
        pagination={pagination}
        onChange={onChange}
      />
    );
  }
}
