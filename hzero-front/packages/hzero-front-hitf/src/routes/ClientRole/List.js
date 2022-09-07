/**
 * Table - 角色管理 - 列表页面表格
 * @date: 2018-7-4
 * @author: lijun <jun.li06@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { Badge, Table } from 'hzero-ui';

import { isTenantRoleLevel, tableScrollWidth } from 'utils/utils';
import intl from 'utils/intl';
import { VERSION_IS_OP } from 'utils/config';
import { operatorRender } from 'utils/renderer';

class List extends PureComponent {
  render() {
    const {
      code,
      dataSource = [],
      loading,
      organizationId,
      onListChange = (e) => e,
      tenantsMulti,
      openAuthEditor = () => {},
      ...others
    } = this.props;
    const isTenant = isTenantRoleLevel();
    const columns = [
      {
        dataIndex: 'name',
        title: intl.get('hitf.clientRole.model.clientRole.name').d('角色名称'),
      },
      {
        dataIndex: 'code',
        title: intl.get('hitf.clientRole.model.clientRole.code').d('角色编码'),
        width: isTenant ? 300 : 150,
      },
      !VERSION_IS_OP &&
        !isTenant && {
          dataIndex: 'levelMeaning',
          title: intl.get('hitf.clientRole.model.clientRole.level').d('角色层级'),
          width: 90,
        },
      {
        dataIndex: 'parentRoleName',
        title: intl.get('hitf.clientRole.model.clientRole.topRole').d('上级角色'),
        width: 150,
      },
      !isTenant && {
        dataIndex: 'roleSource',
        title: intl.get('hitf.clientRole.model.clientRole.roleSource').d('角色来源'),
        width: 120,
        render: (text, record) => record.roleSourceMeaning,
      },
      !VERSION_IS_OP &&
        (!isTenant || tenantsMulti) && {
          dataIndex: 'tenantName',
          title: intl.get('hitf.clientRole.model.clientRole.tenant').d('所属租户'),
          width: 150,
        },
      {
        dataIndex: 'inheritedRoleName',
        title: intl.get('hitf.clientRole.model.clientRole.belong').d('继承自'),
        width: 150,
      },
      {
        dataIndex: 'enabled',
        title: intl.get('hzero.common.status').d('状态'),
        width: 90,
        render: (text, record) => (
          <Badge
            status={record.enabled ? 'success' : 'error'}
            text={
              record.enabled
                ? intl.get('hzero.common.status.enable').d('启用')
                : intl.get('hzero.common.button.disable').d('禁用')
            }
          />
        ),
      },
      {
        dataIndex: 'levelPath',
        title: intl.get('hitf.clientRole.model.clientRole.levelPath').d('角色路径'),
      },
      {
        key: 'operator',
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 80,
        fixed: 'right',
        render: (text, record) => {
          const { assignedFlag, adminFlag, haveAdminFlag, enabled } = record;
          const branch =
            (assignedFlag === 1 ? 4 : 0) +
            (adminFlag === 1 ? 2 : 0) +
            (haveAdminFlag === 1 ? 1 : 0);
          const operators = [];
          const showBranchArr = [7, 5, 1];
          // 分配&管理&父级管理角色/分配&父级管理角色/父级管理角色 同时角色是启用状态
          const isAuth = showBranchArr.includes(branch) && enabled;
          if (isAuth) {
            operators.push({
              key: 'auth',
              ele: (
                <a onClick={() => openAuthEditor(record)}>
                  {intl.get('hitf.clientRole.view.button.auth').d('授权')}
                </a>
              ),
              len: 2,
              title: intl.get('hitf.clientRole.view.button.auth').d('授权'),
            });
          }
          return operatorRender(operators, record);
        },
      },
    ].filter(Boolean);

    const tableProps = {
      dataSource,
      loading,
      columns,
      pagination: false,
      bordered: true,
      childrenColumnName: 'childRoles',
      rowKey: this.defaultTableRowKey,
      scroll: { x: tableScrollWidth(columns) },
      onChange: onListChange,
      ...others,
    };
    return <Table {...tableProps} />;
  }
}

export default List;
