/**
 * GroupUsers
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-06-06
 * @copyright 2019-06-06 © HAND
 */

import React, { Component } from 'react';
import { Bind } from 'lodash-decorators';
import { Table } from 'hzero-ui';

import { Button as ButtonPermission } from 'components/Permission';

import { createPagination, tableScrollWidth } from 'utils/utils';
import notification from 'utils/notification';
import intl from 'utils/intl';
import GroupUsersUserModal from './GroupUsersUserModal';

export default class GroupUsers extends Component {
  constructor(props) {
    super(props);
    this.state = {};
  }

  componentDidMount() {
    this.handleSearch();
  }

  // 查询已经分配的用户
  handleSearch(pagination = {}) {
    const { getCurrentGroupUsers, userGroupId } = this.props;
    getCurrentGroupUsers({
      userGroupId,
      query: pagination,
    }).then((res) => {
      if (res) {
        this.setState({
          selectedRowKeys: [],
          selectedRows: [],
          dataSource: res.content,
          pagination: createPagination(res),
          cachePagination: pagination,
        });
      }
    });
  }

  // 删除 已经分配的用户
  @Bind()
  delCurrentGroupUsers() {
    const { delCurrentGroupUsers, userGroupId } = this.props;
    const { selectedRows } = this.state;
    delCurrentGroupUsers({
      userGroupId,
      users: selectedRows,
    }).then((res) => {
      if (res) {
        notification.success();
        const { cachePagination } = this.state;
        this.handleSearch(cachePagination);
      }
    });
  }

  // 查询 未分配给当前用户组的用户
  @Bind()
  handleSearchRest(query) {
    const { tenantId, userGroupId, getCurrentRestGroupUsers } = this.props;
    return getCurrentRestGroupUsers({
      userGroupId,
      query: {
        ...query,
        tenantId,
      },
    });
  }

  // 分配用户给用户组
  @Bind()
  assignUsersToGroup(users) {
    const { tenantId, userGroupId, assignUsersToGroup } = this.props;
    assignUsersToGroup({
      userGroupId,
      tenantId,
      users,
    }).then((res) => {
      if (res) {
        notification.success();
        const { cachePagination } = this.state;
        this.handleSearch(cachePagination);
        this.setState({
          visible: false,
        });
      }
    });
  }

  // Table
  getColumns() {
    return [
      {
        title: intl.get('hiam.userGroupManagement.model.userGroup.loginName').d('用户账号'),
        dataIndex: 'loginName',
        width: 200,
      },
      {
        title: intl.get('hiam.userGroupManagement.model.userGroup.realName').d('用户名称'),
        dataIndex: 'realName',
        width: 200,
      },
    ];
  }

  @Bind()
  handleRowSelectionChange(_, selectedRows) {
    this.setState({
      selectedRows,
      selectedRowKeys: selectedRows.map((r) => r.assignId),
    });
  }

  @Bind()
  handleTableChange(page, filter, sort) {
    this.handleSearch({ page, sort });
  }

  // Button
  @Bind()
  handleAddBtnClick() {
    this.setState({
      visible: true,
    });
  }

  @Bind()
  handleDelBtnClick() {
    this.delCurrentGroupUsers();
  }

  // Modal
  @Bind()
  closeGroupUsersUserModal() {
    this.setState({
      visible: false,
    });
  }

  render() {
    const {
      path,
      getCurrentRestGroupUsersLoading,
      assignUsersToGroupLoading = false,
      delCurrentGroupUsersLoading = false,
      getCurrentGroupUsersLoading = false,
    } = this.props;
    const { dataSource = [], selectedRowKeys = [], pagination = false, visible } = this.state;
    const columns = this.getColumns();
    const rowSelection = {
      selectedRowKeys,
      onChange: this.handleRowSelectionChange,
    };
    return (
      <>
        <div className="table-operator">
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.addUser`,
                type: 'button',
                meaning: '用户组管理-新增用户',
              },
            ]}
            htmlType="button"
            type="primary"
            onClick={this.handleAddBtnClick}
            loading={getCurrentGroupUsersLoading}
            disabbled={assignUsersToGroupLoading || delCurrentGroupUsersLoading}
          >
            {intl.get('hzero.common.button.add').d('新增')}
          </ButtonPermission>
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.deleteUser`,
                type: 'button',
                meaning: '用户组管理-删除用户',
              },
            ]}
            htmlType="button"
            onClick={this.handleDelBtnClick}
            loading={delCurrentGroupUsersLoading}
            disabled={
              selectedRowKeys.length === 0 ||
              getCurrentGroupUsersLoading ||
              assignUsersToGroupLoading
            }
          >
            {intl.get('hzero.common.button.delete').d('删除')}
          </ButtonPermission>
        </div>
        <Table
          bordered
          rowKey="assignId"
          dataSource={dataSource}
          rowSelection={rowSelection}
          pagination={pagination}
          columns={columns}
          scroll={{ x: tableScrollWidth(columns) }}
          onChange={this.handleTableChange}
          loading={getCurrentGroupUsersLoading}
        />
        {visible && (
          <GroupUsersUserModal
            visible={visible}
            onSearch={this.handleSearchRest}
            onOk={this.assignUsersToGroup}
            onCancel={this.closeGroupUsersUserModal}
            getCurrentRestGroupUsersLoading={getCurrentRestGroupUsersLoading}
            assignUsersToGroupLoading={assignUsersToGroupLoading}
          />
        )}
      </>
    );
  }
}
