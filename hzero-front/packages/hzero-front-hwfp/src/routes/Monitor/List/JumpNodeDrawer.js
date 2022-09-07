import React, { PureComponent } from 'react';
import { Modal, Table } from 'hzero-ui';
import intl from 'utils/intl';

/**
 * 新建或编辑模态框数据展示
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Function} onAdd - 添加确定的回调函数
 * @reactProps {Function} onEdit - 编辑确定的回调函数
 * @reactProps {Object} tableRecord - 表格中信息的一条记录
 * @reactProps {Boolean} isCreate - 是否为新建账户
 * @reactProps {String} anchor - 模态框弹出方向
 * @return React.element
 */
export default class JumpNodeDrawer extends PureComponent {
  render() {
    const {
      fetchNodeLoading,
      jumpLoading,
      visible,
      title,
      validNodeList,
      onSelectNode,
      onOk,
      onCancel,
    } = this.props;

    const rowSelection = {
      type: 'radio',
      onSelect: record => onSelectNode(record),
    };
    const columns = [
      {
        title: intl.get('hwfp.monitor.model.monitor.model.nodeId').d('节点id'),
        key: 'nodeId',
        dataIndex: 'nodeId',
      },
      {
        title: intl.get('hwfp.monitor.model.monitor.model.nodeName').d('节点名称'),
        key: 'name',
        dataIndex: 'name',
      },
    ];
    return (
      <Modal
        destroyOnClose
        width={800}
        title={title}
        visible={visible}
        onCancel={onCancel}
        onOk={onOk}
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        confirmLoading={jumpLoading}
      >
        <Table
          bordered
          rowKey="nodeId"
          pagination={false}
          filterBar={false}
          loading={fetchNodeLoading}
          dataSource={validNodeList}
          columns={columns}
          rowSelection={rowSelection}
        />
      </Modal>
    );
  }
}
