import React from 'react';
import { Table, Modal } from 'hzero-ui';

import formatterCollections from 'utils/intl/formatterCollections';
import { tableScrollWidth } from 'utils/utils';
import intl from 'utils/intl';
import { dateTimeRender } from 'utils/renderer';

@formatterCollections({ code: ['hadm.common'] })
export default class VersionsDrawer extends React.Component {
  render() {
    const {
      title,
      visible,
      serviceVersionList = [],
      onOk,
      onChange,
      onCancel,
      pagination,
      initLoading,
    } = this.props;
    const columns = [
      {
        title: intl.get('hadm.common.model.common.versionNumber').d('服务版本'),
        width: 150,
        dataIndex: 'versionNumber',
      },
      {
        title: intl.get('hadm.common.model.common.releaseDate').d('发布时间'),
        width: 200,
        dataIndex: 'releaseDate',
        render: dateTimeRender,
      },
      {
        title: intl.get('hadm.common.model.common.updateDate').d('更新时间'),
        width: 200,
        dataIndex: 'lastUpdateDate',
        render: dateTimeRender,
      },
    ];
    return (
      <Modal
        destroyOnClose
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        title={title}
        visible={visible}
        onCancel={() => onCancel(false)}
        onOk={() => onOk(false)}
      >
        <Table
          bordered
          rowKey={record => record.serviceVersionId}
          loading={initLoading}
          dataSource={serviceVersionList}
          columns={columns}
          scroll={{ x: tableScrollWidth(columns) }}
          pagination={pagination}
          onChange={onChange}
        />
      </Modal>
    );
  }
}
