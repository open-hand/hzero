import React, { PureComponent } from 'react';
import { Table, Tag } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';

const prefix = 'hpfm.login.audit.model';

export default class ListTable extends PureComponent {
  @Bind()
  onCell() {
    return {
      style: {
        overflow: 'hidden',
        maxWidth: 200,
        textOverflow: 'ellipsis',
        whiteSpace: 'nowrap',
      },
      onClick: e => {
        const { target } = e;
        if (target.style.whiteSpace === 'normal') {
          target.style.whiteSpace = 'nowrap';
        } else {
          target.style.whiteSpace = 'normal';
        }
      },
    };
  }

  render() {
    const { loading, dataSource, pagination, onChange } = this.props;
    const columns = [
      {
        title: intl.get(`${prefix}.auditTypeMeaning`).d('审计类型'),
        width: 150,
        dataIndex: 'auditTypeMeaning',
        render: (_, record) => {
          let color = '';
          switch (record.auditType) {
            case 'LOGIN_FAILURE':
              color = 'red';
              break;
            case 'LOGIN':
              color = 'green';
              break;
            case 'LOGOUT':
              color = 'orange';
              break;
            default:
              color = 'blue';
              return;
          }
          return <Tag color={color}>{record.auditTypeMeaning}</Tag>;
        },
      },
      {
        title: intl.get(`${prefix}.account`).d('账号'),
        width: 200,
        dataIndex: 'loginName',
      },
      {
        title: intl.get(`${prefix}.name`).d('名称'),
        width: 200,
        dataIndex: 'userName',
      },
      {
        title: intl.get(`${prefix}.phone`).d('手机号'),
        width: 200,
        dataIndex: 'phone',
      },
      {
        title: intl.get(`${prefix}.login.time`).d('登录时间'),
        width: 200,
        dataIndex: 'loginDate',
      },
      {
        title: intl.get(`${prefix}.login.address`).d('登录地址'),
        width: 200,
        dataIndex: 'loginIp',
      },
      {
        title: intl.get(`${prefix}.login.device`).d('登录设备'),
        width: 200,
        dataIndex: 'loginDevice',
      },
      {
        title: intl.get(`${prefix}.login.loginMessage`).d('登录信息'),
        width: 200,
        dataIndex: 'loginMessage',
        onCell: this.onCell,
      },
    ];

    return (
      <Table
        bordered
        rowKey="order"
        loading={loading}
        columns={columns}
        dataSource={dataSource.map((item, index) => ({ ...item, order: index + 1 }))}
        pagination={pagination}
        onChange={page => onChange(page)}
      />
    );
  }
}
