import React, { PureComponent } from 'react';
import { Table, Popconfirm } from 'hzero-ui';

import { dateTimeRender, operatorRender } from 'utils/renderer';
import intl from 'utils/intl';
import { tableScrollWidth } from 'utils/utils';

import { processStatusRender } from '@/utils/util';

/**
 * 监控流程数据列表
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
    const {
      loading,
      dataSource = [],
      pagination = {},
      onChange,
      onDetail,
      onSuspendedReason,
      onStop,
      onSuspend,
      onResume,
      onRetry,
      isSiteFlag,
      processStatus = [],
      onException = (e) => e,
    } = this.props;
    const processStatusObj = {};
    processStatus.forEach((item) => {
      processStatusObj[item.value] = item.meaning;
    });
    const columns = [
      isSiteFlag && {
        title: intl.get('hzero.common.model.tenantName').d('租户'),
        dataIndex: 'tenantName',
        width: 200,
      },
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
        width: 200,
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
        title: intl.get('hwfp.monitor.model.monitor.startTime').d('创建时间'),
        dataIndex: 'startTime',
        width: 160,
        render: dateTimeRender,
      },
      {
        title: intl.get('hwfp.monitor.model.monitor.endTime').d('结束时间'),
        dataIndex: 'endTime',
        width: 160,
        render: dateTimeRender,
      },
      {
        title: intl.get('hwfp.monitor.model.monitor.exceptionMsgHead').d('挂起原因'),
        dataIndex: 'exceptionMsgHead',
        width: 250,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        dataIndex: 'operator',
        width: 240,
        fixed: 'right',
        render: (_, record) => {
          const actions = [
            {
              key: 'detail',
              ele: (
                <a onClick={() => onDetail(record)}>
                  {intl.get('hzero.common.button.detail').d('详情')}
                </a>
              ),
              len: 3,
              title: intl.get('hzero.common.button.detail').d('详情'),
            },
          ];
          if (record.hasException) {
            actions.push({
              key: 'jumpNode',
              ele: (
                <a
                  onClick={() => {
                    onException(record);
                  }}
                >
                  {intl.get('hwfp.monitor.view.option.jumpNode').d('异常信息')}
                </a>
              ),
              len: 4,
              title: intl.get('hwfp.monitor.view.option.jumpNode').d('异常信息'),
            });
          }
          if (record.endTime === null) {
            actions.push({
              key: 'stop',
              ele: (
                <Popconfirm
                  placement="topRight"
                  title={intl.get('hzero.common.message.confirm.stopProcess').d('是否终止此条记录')}
                  onConfirm={() => onStop(record.encryptId, 'stopProcess')}
                >
                  <a>{intl.get('hwfp.monitor.view.option.stop').d('终止流程')}</a>
                </Popconfirm>
              ),
              len: 4,
              title: intl.get('hwfp.monitor.view.option.stop').d('终止流程'),
            });
            if (record.suspended) {
              actions.push({
                key: 'resume',
                ele: (
                  <a
                    onClick={() => {
                      onResume(record.encryptId, 'resumeProcess');
                    }}
                  >
                    {intl.get('hwfp.monitor.view.option.resume').d('恢复流程')}
                  </a>
                ),
                len: 4,
                title: intl.get('hwfp.monitor.view.option.resume').d('恢复流程'),
              });
              actions.push({
                key: 'suspendedDetail',
                ele: (
                  <a
                    onClick={() => {
                      onSuspendedReason(record.encryptId);
                    }}
                  >
                    {intl.get('hwfp.monitor.view.option.suspendedDetail').d('挂起详情')}
                  </a>
                ),
                len: 4,
                title: intl.get('hwfp.monitor.view.option.suspendedDetail').d('挂起详情'),
              });
              actions.push({
                key: 'retry',
                ele: (
                  <a
                    onClick={() => {
                      onRetry(record);
                    }}
                  >
                    {intl.get('hwfp.monitor.view.option.retry').d('指定审批人')}
                  </a>
                ),
                len: 5,
                title: intl.get('hwfp.monitor.view.option.retry').d('指定审批人'),
              });
            } else {
              actions.push({
                key: 'suspend',
                ele: (
                  <Popconfirm
                    placement="topRight"
                    title={intl.get('hzero.common.message.confirm.suspend').d('是否挂起此条记录')}
                    onConfirm={() => onSuspend(record.encryptId, 'suspendProcess')}
                  >
                    <a>{intl.get('hwfp.common.view.option.suspend').d('挂起')}</a>
                  </Popconfirm>
                ),
                len: 3,
                title: intl.get('hwfp.common.view.option.suspend').d('挂起'),
              });
            }
          }
          return operatorRender(actions, record);
        },
      },
    ].filter(Boolean);
    return (
      <Table
        bordered
        scroll={{ x: tableScrollWidth(columns, 100) }}
        // rowKey="id"
        loading={loading}
        columns={columns}
        dataSource={dataSource}
        pagination={pagination}
        onChange={onChange}
      />
    );
  }
}
