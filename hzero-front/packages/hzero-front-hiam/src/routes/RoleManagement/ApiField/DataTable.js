/**
 * 接口字段权限维护 /hiam/role/api/:roleId
 * DataTable
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-07-11
 * @copyright 2019-07-10 © HAND
 */

import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Table } from 'hzero-ui';

import { Button as ButtonPermission } from 'components/Permission';

import { operatorRender } from 'utils/renderer';
import { tableScrollWidth } from 'utils/utils';
import intl from 'hzero-front/lib/utils/intl';

export default class DataTable extends Component {
  static propTypes = {
    // dataSource: PropTypes.array.isRequired,
    // pagination: PropTypes.object.isRequired,
    // loading: PropTypes.bool.isRequired,
    onChange: PropTypes.func.isRequired,
    // 字段权限维护按钮点击
    onApiFieldPermissionMaintain: PropTypes.func.isRequired,
  };

  getColumns() {
    return [
      {
        title: intl.get('hiam.roleManagement.model.api.serviceName').d('服务名'),
        dataIndex: 'serviceName',
        width: 160,
      },
      {
        title: intl.get('hiam.roleManagement.model.api.method').d('请求方式'),
        dataIndex: 'method',
        width: 160,
      },
      {
        title: intl.get('hiam.roleManagement.model.api.allocatedCount').d('已配置'),
        dataIndex: 'allocatedCount',
        width: 80,
        render: (value) => {
          return value || 0;
        },
      },
      {
        title: intl.get('hiam.roleManagement.model.api.path').d('请求路径'),
        width: 400,
        dataIndex: 'path',
      },
      {
        title: intl.get('hiam.roleManagement.model.api.description').d('请求描述'),
        dataIndex: 'description',
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        key: 'operator',
        width: 110,
        fixed: 'right',
        render: (_, record) => {
          const { path, onApiFieldPermissionMaintain } = this.props;
          const actions = [];
          actions.push({
            key: 'gotoDetail',
            ele: (
              <ButtonPermission
                type="text"
                permissionList={[
                  {
                    code: `${path}.button.permissionMaintain`,
                    type: 'button',
                    meaning: '接口字段权限配置-字段权限维护',
                  },
                ]}
                onClick={() => {
                  onApiFieldPermissionMaintain(record);
                }}
              >
                {intl.get('hiam.roleManagement.view.button.permissionMaintain').d('字段权限维护')}
              </ButtonPermission>
            ),
            len: 6,
            title: intl.get('hiam.roleManagement.view.button.permissionMaintain').d('字段权限维护'),
          });
          return operatorRender(actions);
        },
      },
    ];
  }

  render() {
    const { dataSource, pagination, loading = false, onChange } = this.props;
    const columns = this.getColumns();
    return (
      <Table
        bordered
        rowKey="id"
        dataSource={dataSource}
        pagination={pagination}
        columns={columns}
        scroll={{ x: tableScrollWidth(columns) }}
        onChange={onChange}
        loading={loading}
      />
    );
  }
}
