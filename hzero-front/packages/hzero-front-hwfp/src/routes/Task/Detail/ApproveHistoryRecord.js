import React, { Component } from 'react';
import { Spin, Table, Collapse } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import UploadModal from 'components/Upload';
import intl from 'utils/intl';
import { dateTimeRender, approveNameRender } from 'utils/renderer';
import { tableScrollWidth } from 'utils/utils';
import { BKT_HWFP } from 'utils/config';

import styles from './index.less';

const { Panel } = Collapse;

export default class ApproveHistoryRecord extends Component {
  @Bind()
  getColumns() {
    return [
      {
        title: intl.get('hwfp.common.model.approval.time').d('审批时间'),
        dataIndex: 'endTime',
        width: 180,
        render: dateTimeRender,
      },
      {
        title: intl.get('hwfp.common.model.approval.action').d('审批动作'),
        dataIndex: 'action',
        width: 120,
        render: approveNameRender,
      },
      {
        title: intl.get('hwfp.common.model.approval.step').d('审批环节'),
        dataIndex: 'name',
        width: 150,
      },
      {
        title: intl.get('hwfp.common.model.approval.owner').d('审批人'),
        dataIndex: 'assigneeName',
        width: 150,
      },
      {
        title: intl.get('hwfp.common.model.approval.opinion', { title: '审批意见' }).d('审批意见'),
        dataIndex: 'comment',
      },
      {
        title: intl.get('hwfp.common.model.approval.file').d('附件'),
        dataIndex: 'attachmentUuid',
        fixed: 'right',
        width: 150,
        render: (val, record) => {
          if (record.attachmentUuid) {
            return (
              <UploadModal
                attachmentUUID={val}
                bucketName={BKT_HWFP}
                bucketDirectory="hwfp01"
                viewOnly
              />
            );
          }
          return <div style={{ height: record.action ? 22 : 17 }} />;
        },
      },
    ];
  }

  render() {
    const { records = [], loading = false } = this.props;
    return (
      <Spin spinning={loading}>
        <Collapse bordered={false} defaultActiveKey={['0']} className={styles['collapse-items']}>
          {records.map((item, index) => (
            <Panel
              // eslint-disable-next-line react/no-array-index-key
              key={index}
              header={
                <div className={styles['collapse-item-title']}>
                  <span style={{ marginRight: 20 }}>
                    {intl.get('hwfp.common.model.process.ID').d('流程标识')}: {item.id}
                  </span>
                  <span style={{ marginRight: 20 }}>
                    {intl.get('hwfp.task.view.message.title.startTime').d('开始时间')}:{' '}
                    {item.startTime}
                  </span>
                  <span>
                    {intl.get('hwfp.task.view.message.title.endTime').d('结束时间')}: {item.endTime}
                  </span>
                </div>
              }
            >
              <Table
                bordered
                rowKey="id"
                pagination={false}
                dataSource={item.historicTaskExtList || []}
                columns={this.getColumns()}
                scroll={{ x: tableScrollWidth(this.getColumns()) }}
              />
            </Panel>
          ))}
        </Collapse>
      </Spin>
    );
  }
}
