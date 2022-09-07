import React, { PureComponent } from 'react';
import { Form, Table, Tooltip } from 'hzero-ui';

import intl from 'utils/intl';
import { dateTimeRender, approveNameRender } from 'utils/renderer';
import { tableScrollWidth } from 'utils/utils';

import styles from './index.less';
@Form.create({ fieldNameProp: null })
export default class TaskDetail extends PureComponent {
  /**
   * render
   * @returns React.element
   */
  render() {
    const { detail = {} } = this.props;

    const columns = [
      {
        title: intl.get('hwfp.common.model.approval.action').d('审批动作'),
        dataIndex: 'action',
        width: 100,
        render: approveNameRender,
      },
      {
        title: intl.get('hwfp.common.model.approval.step').d('审批环节'),
        dataIndex: 'name',
      },
      {
        title: intl.get('hwfp.common.model.approval.owner').d('审批人'),
        dataIndex: 'assigneeName',
        width: 150,
      },
      {
        title: intl.get('hwfp.common.model.approval.opinion', { title: '审批意见' }).d('审批意见'),
        dataIndex: 'comment',
        width: 300,
        render: (value) => (
          <Tooltip title={value} placement="topLeft" overlayClassName={styles['opinion']}>
            {value}
          </Tooltip>
        ),
      },
      {
        title: intl.get('hwfp.monitor.model.approval.time').d('审批时间'),
        dataIndex: 'endTime',
        width: 180,
        render: dateTimeRender,
      },
    ];
    return (
      <>
        <Table
          bordered
          rowKey="id"
          // loading={loading}
          pagination={false}
          dataSource={detail.historicTaskList}
          columns={columns}
          scroll={{ x: tableScrollWidth(columns) }}
        />
      </>
    );
  }
}
