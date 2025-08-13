/**
 * 接口字段维护 /hiam/api-field
 * DataTable
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-07-10
 * @copyright 2019-07-10 © HAND
 */

import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Table } from 'hzero-ui';

import { Button as ButtonPermission } from 'components/Permission';

import { operatorRender } from 'utils/renderer';
import { tableScrollWidth } from 'utils/utils';
import intl from 'utils/intl';

export default class DataTable extends Component {
  static propTypes = {
    // dataSource: PropTypes.array.isRequired,
    // pagination: PropTypes.object.isRequired,
    // loading: PropTypes.bool.isRequired,
    onChange: PropTypes.func.isRequired,
    // 字段维护按钮点击
    onApiFieldMaintain: PropTypes.func.isRequired,
    // 路由的 path
    path: PropTypes.string.isRequired,
  };

  getColumns() {
    const { path } = this.props;
    return [
      {
        title: intl.get('hiam.apiField.model.api.serviceName').d('服务名'),
        dataIndex: 'serviceName',
        width: 160,
      },
      {
        title: intl.get('hiam.apiField.model.api.method').d('请求方式'),
        dataIndex: 'method',
        width: 160,
      },
      {
        title: intl.get('hiam.apiField.model.api.path').d('请求路径'),
        width: 400,
        dataIndex: 'path',
      },
      {
        title: intl.get('hiam.apiField.model.api.description').d('请求描述'),
        dataIndex: 'description',
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        key: 'operator',
        width: 90,
        fixed: 'right',
        render: (_, record) => {
          const { onApiFieldMaintain } = this.props;
          const actions = [];
          actions.push({
            key: 'gotoDetail',
            ele: (
              <ButtonPermission
                permissionList={[
                  {
                    code: `${path}.button.edit`,
                    type: 'button',
                    meaning: '接口字段维护-字段维护',
                  },
                ]}
                type="text"
                onClick={() => {
                  onApiFieldMaintain(record);
                }}
              >
                {intl.get('hiam.apiField.view.button.fieldMaintain').d('字段维护')}
              </ButtonPermission>
            ),
            len: 4,
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
