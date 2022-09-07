import React from 'react';
import { Table, Icon } from 'hzero-ui';
import moment from 'moment';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import { operatorRender } from 'utils/renderer';
import { getDateFormat, tableScrollWidth } from 'utils/utils';

import ExpandRow from './ExpandRow';

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
        title: intl.get('hagd.sagaInstance.model.sagaInstance.sagaCode').d('事务实例'),
        dataIndex: 'sagaCode',
      },
      {
        title: intl.get('hagd.sagaInstance.model.sagaInstance.startTime').d('开始时间'),
        dataIndex: 'startTime',
        width: 120,
        render: text => <span>{moment(text).format(getDateFormat())}</span>,
      },
      {
        title: intl.get('hagd.sagaInstance.model.sagaInstance.refType').d('关联事务类型'),
        dataIndex: 'refType',
        width: 200,
      },
      {
        title: intl.get('hagd.sagaInstance.model.sagaInstance.refId').d('关联业务ID'),
        dataIndex: 'refId',
        width: 200,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 150,
        render: (_, record) => {
          const operators = [
            {
              key: 'btnDetail',
              ele: (
                <span className="action-link">
                  <a onClick={() => this.handleShowDetail(record)}>
                    {intl.get('hagd.sagaInstance.view.title.btnDetail').d('运行详情')}
                  </a>
                </span>
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
        expandedRowRender={record => <ExpandRow record={record} />}
        onChange={onChange}
        pagination={pagination}
      />
    );
  }
}
