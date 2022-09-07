import React from 'react';
import { Table, Icon } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import { tableScrollWidth } from 'utils/utils';
import intl from 'utils/intl';
import { dateRender, operatorRender } from 'utils/renderer';

export default class SagaTable extends React.Component {
  @Bind()
  handleShowDetail(record) {
    const { showDetail = e => e } = this.props;
    showDetail(record);
  }

  render() {
    const { loading, dataSource, onChange, pagination } = this.props;
    const columns = [
      {
        title: intl.get('hagd.sagaInstance.model.sagaInstance.status').d('状态'),
        dataIndex: 'status',
        width: 150,
        render: val => {
          let statusText = '';
          let iconType = {};
          switch (val) {
            case 'FAILED':
              statusText = intl.get('hagd.sagaInstance.model.sagaInstance.status.failed').d('失败');
              iconType = {
                type: 'close-circle-o',
                style: { color: '#f44336', marginRight: 4 },
              };
              break;
            case 'COMPLETED':
              statusText = intl
                .get('hagd.sagaInstance.model.sagaInstance.status.completed')
                .d('完成');
              iconType = {
                type: 'check-circle-o',
                style: { color: '#00bfa5', marginRight: 4 },
              };
              break;
            case 'RUNNING':
              statusText = intl
                .get('hagd.sagaInstance.model.sagaInstance.status.running')
                .d('运行中');
              iconType = {
                type: 'dashboard',
                style: { color: '#4d90fe', marginRight: 4 },
              };
              break;
            case 'NON_CONSUMER':
              statusText = intl
                .get('hagd.sagaInstance.model.sagaInstance.status.nonConsumer')
                .d('未消费');
              iconType = {
                type: 'close-circle-o',
                style: { color: '#f44336', marginRight: 4 },
              };
              break;
            // case 'QUEUE':
            //   statusText = '队列中';
            //   iconType = {
            //     type: 'clock-circle-o',
            //     style: { background: 'yellow' },
            //   };
            //   break;
            default:
              break;
          }
          return (
            <div>
              <Icon {...iconType} />
              {statusText}
            </div>
          );
        },
      },
      {
        title: intl.get('hagd.sagaInstance.model.sagaInstance.task.taskCode').d('任务编码'),
        dataIndex: 'taskInstanceCode',
        width: 200,
      },
      {
        title: intl.get('hagd.sagaInstance.model.sagaInstance.task.sagaInstance').d('所属事务实例'),
        dataIndex: 'sagaInstanceCode',
        width: 200,
      },
      {
        title: intl.get('hagd.saga.model.saga.description').d('描述'),
        dataIndex: 'description',
      },
      {
        title: intl.get('hagd.sagaInstance.model.sagaInstance.task.startTime').d('实际开始时间'),
        dataIndex: 'plannedStartTime',
        width: 110,
        render: dateRender,
      },
      {
        title: intl.get('hagd.sagaInstance.model.sagaInstance.task.endTime').d('实际完成时间'),
        dataIndex: 'actualEndTime',
        width: 110,
        render: dateRender,
      },
      {
        title: intl.get('hagd.sagaInstance.model.sagaInstance.task.retriedCount').d('重试次数'),
        dataIndex: 'retriedCount',
        width: 100,
        render: (val, record) => `${record.retriedCount}/${record.maxRetryCount}`,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 150,
        render: (_, record) => {
          const operators = [
            {
              key: 'btnDetail',
              ele: (
                <a onClick={() => this.handleShowDetail(record)}>
                  {intl.get('hagd.sagaInstance.view.title.btnDetail').d('运行详情')}
                </a>
              ),
              len: 4,
              title: intl.get('hagd.sagaInstance.view.title.btnDetail').d('运行详情'),
            },
          ];
          return operatorRender(operators, record, { limit: 2 });
        },
      },
    ];
    return (
      <Table
        bordered
        rowKey="id"
        loading={loading}
        dataSource={dataSource}
        columns={columns}
        scroll={{ x: tableScrollWidth(columns) }}
        onChange={onChange}
        pagination={pagination}
      />
    );
  }
}
