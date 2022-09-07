/**
 * FieldPermission - 安全组字段权限tab
 * @date: 2019-11-27
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import React, { Component } from 'react';
import { Table } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import { tableScrollWidth } from 'utils/utils';
import intl from 'utils/intl';

import FieldConfigDrawer from './Drawer';

export default class FieldPermissionTab extends Component {
  constructor(props) {
    super(props);
    this.state = {
      visible: false,
      currentInterface: {},
    };
  }

  @Bind()
  handleOpenDrawer(record) {
    this.setState({
      currentInterface: record,
      visible: true,
    });
  }

  @Bind()
  handleCloseDrawer() {
    this.setState({
      visible: false,
      currentInterface: {},
    });
  }

  get columns() {
    return [
      {
        title: intl.get('hiam.roleManagement.model.api.serviceName').d('服务名'),
        dataIndex: 'serviceName',
      },
      {
        title: intl.get('hiam.roleManagement.model.api.method').d('请求方式'),
        dataIndex: 'method',
      },
      {
        title: intl.get('hiam.roleManagement.model.api.path').d('请求路径'),
        dataIndex: 'path',
      },
      {
        title: intl.get('hiam.roleManagement.model.api.description').d('请求描述'),
        dataIndex: 'description',
        width: 300,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 120,
        fixed: 'right',
        render: (_, record) => (
          <span className="action-link">
            <a onClick={() => this.handleOpenDrawer(record)}>
              {intl.get('hiam.roleManagement.model.api.fdPermissionMaintenance').d('字段权限维护')}
            </a>
          </span>
        ),
      },
    ];
  }

  render() {
    const {
      dataSource,
      loading,
      pagination,
      onFetchFieldConfigList = () => {},
      queryFieldConfigLoading,
    } = this.props;
    const { visible, currentInterface } = this.state;
    const tableProps = {
      rowKey: 'id',
      columns: this.columns,
      bordered: true,
      dataSource,
      loading,
      pagination,
      scroll: { x: tableScrollWidth(this.columns) },
    };
    const drawerProps = {
      visible,
      loading: queryFieldConfigLoading,
      currentInterface,
      onFetchDetail: onFetchFieldConfigList,
      onCancel: this.handleCloseDrawer,
    };
    return (
      <>
        <Table {...tableProps} />
        <FieldConfigDrawer {...drawerProps} />
      </>
    );
  }
}
