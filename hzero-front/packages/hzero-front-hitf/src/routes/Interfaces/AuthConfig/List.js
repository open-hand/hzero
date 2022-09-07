/**
 * List  - 应用管理 - 首页列表
 * @date: 2018-7-4
 * @author: lijun <jun.li06@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { Table } from 'hzero-ui';

import intl from 'utils/intl';
import { tableScrollWidth, isTenantRoleLevel } from 'utils/utils';
import { operatorRender } from 'utils/renderer';

export default class List extends PureComponent {
  render() {
    const {
      dataSource = [],
      pagination,
      loading,
      openEditor = (e) => e,
      onChange = (e) => e,
      listRowKey,
      selectedRowKeys = [],
      onRowSelectionChange = () => {},
      currentTenantId,
    } = this.props;

    const tableColumns = [
      {
        title: intl.get('hitf.interfaces.model.interfaces.authLevel').d('认证层级'),
        dataIndex: 'authLevelMeaning',
        width: 100,
      },
      {
        title: intl.get('hitf.interfaces.model.interfaces.authLevelValue').d('认证层级值'),
        dataIndex: 'authLevelValueMeaning',
        width: 180,
      },
      {
        title: intl.get('hitf.interfaces.model.interfaces.authType').d('认证模式'),
        width: 150,
        render: (_text, record) => {
          const { httpAuthorization = {} } = record;
          const { authTypeMeaning } = httpAuthorization;
          return authTypeMeaning;
        },
      },
      {
        title: intl.get('hitf.interfaces.model.interfaces.remark').d('备注'),
        dataIndex: 'remark',
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 80,
        fixed: 'right',
        render: (text, record) => {
          const operators = [
            {
              key: 'edit',
              ele: (
                <a
                  onClick={() => openEditor(record)}
                  disabled={isTenantRoleLevel() && currentTenantId !== record.tenantId}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </a>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            },
          ];
          return operatorRender(operators, record);
        },
      },
    ];
    const tableProps = {
      dataSource,
      loading,
      onChange,
      pagination,
      bordered: true,
      columns: tableColumns,
      scroll: { x: tableScrollWidth(tableColumns) },
      rowSelection: {
        selectedRowKeys,
        onChange: onRowSelectionChange,
      },
      rowKey: listRowKey,
    };
    return <Table {...tableProps} />;
  }
}
