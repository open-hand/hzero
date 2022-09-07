import React, { PureComponent } from 'react';
import { Table, Tooltip } from 'hzero-ui';

import UploadModal from 'components/Upload';

import intl from 'utils/intl';
import { dateTimeRender, approveNameRender } from 'utils/renderer';
import { tableScrollWidth } from 'utils/utils';
import { BKT_HWFP } from 'utils/config';

import styles from './index.less';

export default class ApproveHistory extends PureComponent {
  /**
   * render
   * @returns React.element
   */
  render() {
    const { detail, historyApprovalRecords = [], mergeHistoryFlag, loading = false } = this.props;
    // eslint-disable-next-line
    const dataSource = !mergeHistoryFlag
      ? detail.historicTaskList
      : loading
      ? []
      : []
          .concat(...historyApprovalRecords.map((item) => item.historicTaskExtList || []))
          .concat(detail.historicTaskList || []);
    const columns = [
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
        // width: 150,
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
        },
      },
    ];
    return (
      <Table
        bordered
        rowKey="id"
        loading={loading}
        pagination={false}
        dataSource={dataSource}
        columns={columns}
        scroll={{ x: tableScrollWidth(columns) }}
      />
    );
  }
}
