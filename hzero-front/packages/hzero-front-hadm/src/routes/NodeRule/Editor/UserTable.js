/**
 * nodeRule - 节点组规则 - 新增/编辑 - 用户表格
 * @date: 2018-9-7
 * @author: wangjiacheng <jiacheng.wang@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import { Table, Form, Input } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import { createPagination, tableScrollWidth } from 'utils/utils';
import intl from 'utils/intl';

const { Search } = Input;
@Form.create({ fieldNameProp: null })
export default class UserTable extends React.PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      value: '',
    };
  }

  /**
   * @function handleSearch - 搜索表单
   * @param {string} value - 搜索条件
   */
  @Bind()
  handleSearch(value) {
    const { dispatch, actionTenant } = this.props;
    dispatch({
      type: 'nodeRule/fetchUserList',
      payload: {
        // ...actionTenant,
        loginName: value,
        organizationId: actionTenant.tenantId,
        page: 0,
        size: 10,
      },
    });
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const {
      loading,
      userList,
      standardTable,
      onUserSelectChange,
      selectedUserRowKeys,
    } = this.props;
    const { value } = this.state;
    const columns = [
      // {
      //   title: intl.get('hadm.nodeRule.model.nodeRule.userId').d('用户ID'),
      //   width: 100,
      //   dataIndex: 'id',
      // },
      {
        title: intl.get('hadm.nodeRule.model.nodeRule.loginName').d('账户'),
        dataIndex: 'loginName',
      },
      {
        title: intl.get('hadm.nodeRule.model.nodeRule.realName').d('用户名'),
        dataIndex: 'realName',
      },
    ];
    const rowSelection = {
      selectedRowKeys: selectedUserRowKeys,
      onChange: onUserSelectChange,
    };
    return (
      <>
        <Search
          style={{ width: 200, marginBottom: 12 }}
          placeholder={intl.get('hadm.nodeRule.model.nodeRule.loginName').d('账户')}
          onSearch={this.handleSearch}
          value={value}
          onChange={(e) => {
            this.setState({ value: e.target.value });
          }}
          disabled={userList.length === 0}
        />
        <Table
          bordered
          rowKey="id"
          rowSelection={rowSelection}
          loading={loading}
          dataSource={userList.content || []}
          columns={columns}
          scroll={{ x: tableScrollWidth(columns) }}
          pagination={createPagination(userList)}
          onChange={(pagination) => {
            standardTable(pagination, value);
          }}
        />
      </>
    );
  }
}
