import React from 'react';
import { Modal, Table } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';

export default class Drawer extends React.PureComponent {
  // 查询同步用户数据
  @Bind()
  handleSearch(record) {
    const { onShow, ldapHistoryId } = this.props;
    onShow({ ...record, id: ldapHistoryId });
  }

  /**
   * @function handlePagination - 分页操作
   * @param {Object} pagination - 分页参数
   */
  @Bind()
  handlePagination(pagination = {}) {
    this.handleSearch({
      page: pagination,
    });
  }

  render() {
    const {
      syncUserErrorVisible,
      loading,
      syncErrorInfo,
      syncErrorPagination,
      onCancel,
    } = this.props;
    const columns = [
      {
        title: intl.get('hiam.ldap.view.message.sync.uuid').d('UUID'),
        width: 130,
        dataIndex: 'uuid',
      },
      {
        title: intl.get('hiam.ldap.view.message.sync.realName').d('用户名'),
        width: 120,
        render: (_, record) => (
          <span>
            {record.realName}({record.loginName})
          </span>
        ),
      },
      {
        title: intl.get('hiam.ldap.view.message.sync.email').d('邮箱'),
        width: 220,
        dataIndex: 'email',
      },
      {
        title: intl.get('hiam.ldap.view.message.sync.phone').d('手机号'),
        width: 150,
        dataIndex: 'phone',
      },
      {
        title: intl.get('hiam.ldap.view.message.sync.cause').d('失败原因'),
        dataIndex: 'cause',
      },
    ];

    return (
      <Modal
        destroyOnClose
        keyboard={false}
        title={intl.get('hiam.ldap.option.syncErrorUser').d('同步用户错误详情')}
        width={900}
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        visible={syncUserErrorVisible}
        zIndex={1000}
        onCancel={onCancel}
        footer={null}
      >
        <Table
          dataSource={syncErrorInfo.content}
          pagination={syncErrorPagination}
          columns={columns}
          onChange={this.handlePagination}
          loading={loading}
        />
      </Modal>
    );
  }
}
