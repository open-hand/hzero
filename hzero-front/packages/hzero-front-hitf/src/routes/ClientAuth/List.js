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
import { tableScrollWidth } from 'utils/utils';
import { yesOrNoRender, operatorRender } from 'utils/renderer';

export default class List extends PureComponent {
  defaultTableRowKey = 'id';

  render() {
    const {
      dataSource = [],
      pagination,
      loading,
      tenantRoleLevel,
      openEditor = (e) => e,
      onChange = (e) => e,
    } = this.props;
    const tableColumns = [
      {
        title: intl.get('hitf.application.model.application.client').d('客户端'),
        dataIndex: 'name',
        width: 180,
      },
      {
        title: intl.get('hitf.application.model.application.statisticsLevel').d('统计维度'),
        dataIndex: 'statisticsLevelMeaning',
        width: 150,
      },
      !tenantRoleLevel && {
        title: intl.get('hzero.common.model.common.tenantId').d('租户'),
        dataIndex: 'clientTenantName',
        width: 150,
      },
      {
        title: intl.get('hitf.application.model.application.remark').d('说明'),
        dataIndex: 'remark',
      },
      {
        title: intl.get('hitf.application.model.application.authFlag').d('是否授权'),
        dataIndex: 'authFlag',
        width: 90,
        render: yesOrNoRender,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 70,
        fixed: 'right',
        render: (text, record) => {
          const operators = [
            {
              key: 'edit',
              ele: (
                <a onClick={() => openEditor(record)}>
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
    ].filter(Boolean);
    const tableProps = {
      dataSource,
      loading,
      onChange,
      pagination,
      bordered: true,
      columns: tableColumns,
      scroll: { x: tableScrollWidth(tableColumns) },
      rowKey: this.defaultTableRowKey,
    };
    return <Table {...tableProps} />;
  }
}
