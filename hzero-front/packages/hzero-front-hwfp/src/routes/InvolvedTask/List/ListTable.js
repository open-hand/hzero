import React, { PureComponent } from 'react';
import { Table } from 'hzero-ui';

import intl from 'utils/intl';
import { dateTimeRender, operatorRender } from 'utils/renderer';
import { tableScrollWidth } from 'utils/utils';
import { openTab } from 'utils/menuTab';

import { processStatusRender } from '@/utils/util';

/**
 * 参与流程数据列表
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
   * 详情
   * @param {object} record - 头数据
   */
  changeDetail(record) {
    openTab({
      title: `${record.processName}`,
      key: `/hwfp/involved-task/detail/${record.encryptId}`,
      path: `/hwfp/involved-task/detail/${record.encryptId}`,
      icon: 'edit',
      closable: true,
    });
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const { loading, dataSource = [], pagination = {}, onChange, processStatus = [] } = this.props;
    const processStatusObj = {};
    processStatus.forEach((item) => {
      processStatusObj[item.value] = item.meaning;
    });
    const columns = [
      {
        title: intl.get('hwfp.common.model.process.ID').d('流程标识'),
        dataIndex: 'id',
        width: 100,
      },
      {
        title: intl.get('hwfp.common.model.process.approvalStatus').d('审批状态'),
        dataIndex: 'processStatus',
        width: 180,
        render: (val) => processStatusRender(processStatusObj, val),
      },
      {
        title: intl.get('hwfp.common.model.process.name').d('流程名称'),
        dataIndex: 'processName',
      },
      {
        title: intl.get('hwfp.common.model.process.description').d('流程描述'),
        dataIndex: 'description',
      },
      {
        title: intl.get('hwfp.common.view.message.current.stage').d('当前节点'),
        dataIndex: 'taskName',
        width: 150,
      },
      {
        title: intl.get('hwfp.common.model.apply.owner').d('申请人'),
        dataIndex: 'startUserName',
        width: 150,
        render: (val, record) => (
          <span>
            {val}({record.startUserId})
          </span>
        ),
      },
      {
        title: intl.get('hwfp.common.view.message.handler').d('当前处理人'),
        dataIndex: 'currentApprover',
        width: 150,
        render: (val) => val && val.split(',').map((item) => `${item} `),
      },
      {
        title: intl.get('hwfp.involvedTask.model.involvedTask.startTime').d('创建时间'),
        dataIndex: 'startTime',
        width: 160,
        render: dateTimeRender,
      },
      {
        title: intl.get('hwfp.involvedTask.model.involvedTask.endTime').d('结束时间'),
        dataIndex: 'endTime',
        width: 160,
        render: dateTimeRender,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        dataIndex: 'operator',
        width: 100,
        align: 'center',
        fixed: 'right',
        render: (_, record) => {
          const operators = [
            {
              key: 'detail',
              ele: (
                <a onClick={() => this.changeDetail(record)}>
                  {intl.get('hzero.common.button.detail').d('详情')}
                </a>
              ),
              len: 4,
              title: intl.get('hzero.common.button.detail').d('详情'),
            },
          ];
          return operatorRender(operators, record);
        },
      },
    ];
    return (
      <Table
        bordered
        //  rowKey="id"
        loading={loading}
        dataSource={dataSource}
        pagination={pagination}
        onChange={onChange}
        columns={columns}
        scroll={{ x: tableScrollWidth(columns) }}
      />
    );
  }
}
