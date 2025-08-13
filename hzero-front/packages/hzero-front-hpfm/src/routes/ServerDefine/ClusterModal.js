/**
 * Modal - 服务器定义-模态框
 * @date: 2019-7-2
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import React from 'react';
import { Form, Modal, Table } from 'hzero-ui';

import intl from 'utils/intl';

@Form.create({ fieldNameProp: null })
export default class ClusterModal extends React.Component {
  render() {
    const { modalVisible, title = '', loading = false, initData = [], onCancel } = this.props;
    const columns = [
      {
        title: intl.get('hpfm.serverDefine.model.serverDefine.clusterCode').d('集群编码'),
        width: 236,
        dataIndex: 'clusterCode',
      },
      {
        title: intl.get('hpfm.serverDefine.model.serverDefine.clusterName').d('集群名称'),
        dataIndex: 'clusterName',
        Width: 236,
      },
    ];
    return (
      <Modal destroyOnClose title={title} visible={modalVisible} onCancel={onCancel} footer={null}>
        <Table
          bordered
          columns={columns}
          loading={loading}
          pagination={false}
          dataSource={initData}
        />
      </Modal>
    );
  }
}
